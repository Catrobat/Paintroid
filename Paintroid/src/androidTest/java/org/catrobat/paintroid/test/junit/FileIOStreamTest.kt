/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2026 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.test.junit

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.annotation.RequiresApi
import androidx.exifinterface.media.ExifInterface
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.paintroid.FileIO
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.Q)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.Q)
class FileIOStreamTest {
    private lateinit var imageFile: File
    private lateinit var resolver: ContentResolver
    private val uri = Uri.parse("content://org.catrobat.paintroid.stream-test/image")
    private var decodedBitmap: Bitmap? = null
    private var activeStreams = 0
    private var openedStreams = 0

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        imageFile = File.createTempFile("single-stream-image", ".jpg", context.cacheDir)
        val source = Bitmap.createBitmap(2, 3, Bitmap.Config.ARGB_8888)
        try {
            imageFile.outputStream().use { source.compress(Bitmap.CompressFormat.JPEG, 100, it) }
            ExifInterface(imageFile.absolutePath).apply {
                setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90.toString())
                saveAttributes()
            }
        } finally {
            source.recycle()
        }
        val provider = Mockito.mock(ContentProvider::class.java)
        Mockito.`when`(provider.openAssetFile(uri, "r", null))
            .thenAnswer { openImage() }
        resolver = ContentResolver.wrap(provider)
    }

    @After
    fun tearDown() {
        decodedBitmap?.recycle()
        imageFile.delete()
    }

    @Test
    fun testDecodeClosesImageStreamBeforeReadingExif() {
        decodedBitmap = FileIO.decodeBitmapFromUri(resolver, uri, BitmapFactory.Options(), null)

        Assert.assertNotNull(decodedBitmap)
        Assert.assertEquals(3, decodedBitmap!!.width)
        Assert.assertEquals(2, decodedBitmap!!.height)
        Assert.assertEquals(2, openedStreams)
        Assert.assertEquals(0, activeStreams)
    }

    @Test
    fun testBoundsDecodeReturnsNullAndClosesItsOnlyStream() {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }

        Assert.assertNull(FileIO.decodeBitmapFromUri(resolver, uri, options, null))

        Assert.assertEquals(2, options.outWidth)
        Assert.assertEquals(3, options.outHeight)
        Assert.assertEquals(1, openedStreams)
        Assert.assertEquals(0, activeStreams)
    }

    @Test(expected = IOException::class)
    fun testMissingInputStreamStillThrowsIOException() {
        val unavailableResolver = ContentResolver.wrap(Mockito.mock(ContentProvider::class.java))

        FileIO.decodeBitmapFromUri(unavailableResolver, uri, BitmapFactory.Options(), null)
    }

    private fun openImage(): AssetFileDescriptor {
        if (activeStreams != 0) {
            throw FileNotFoundException("Provider allows only one open stream")
        }
        val descriptor = ParcelFileDescriptor.open(imageFile, ParcelFileDescriptor.MODE_READ_ONLY)
        openedStreams++
        activeStreams++
        return object : AssetFileDescriptor(descriptor, 0, AssetFileDescriptor.UNKNOWN_LENGTH) {
            override fun createInputStream(): FileInputStream =
                TrackedInputStream(descriptor) { activeStreams-- }
        }
    }

    private class TrackedInputStream(descriptor: ParcelFileDescriptor, val onClose: () -> Unit) :
        ParcelFileDescriptor.AutoCloseInputStream(descriptor) {
        private var closed = false

        override fun close() {
            try {
                super.close()
            } finally {
                if (!closed) {
                    closed = true
                    onClose()
                }
            }
        }
    }
}
