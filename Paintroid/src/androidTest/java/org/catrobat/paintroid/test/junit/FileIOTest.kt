package org.catrobat.paintroid.test.junit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.FileIO.getBitmapOrientation
import org.catrobat.paintroid.FileIO.getOrientedBitmap
import org.junit.Assert
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import java.io.File

class FileIOTest {
    @Mock
    var bitmap: Bitmap? = null

    @Mock
    var exifInterface: ExifInterface? = null

    @Test
    fun testDecodePickerUriWithoutFileSystemPathPreservesOrientation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val imageDirectory = File(context.cacheDir, "images")
        imageDirectory.mkdirs()
        val imageFile = File.createTempFile("picker-image", ".jpg", imageDirectory)
        val source = Bitmap.createBitmap(2, 3, Bitmap.Config.ARGB_8888)
        var decodedBitmap: Bitmap? = null
        try {
            imageFile.outputStream().use { source.compress(Bitmap.CompressFormat.JPEG, 100, it) }
            ExifInterface(imageFile.absolutePath).apply {
                setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90.toString())
                saveAttributes()
            }
            val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", imageFile)

            decodedBitmap = FileIO.decodeBitmapFromUri(
                context.contentResolver, uri,
                BitmapFactory.Options(), context
            )

            Assert.assertNotNull(decodedBitmap)
            Assert.assertEquals(3, decodedBitmap!!.width)
            Assert.assertEquals(2, decodedBitmap.height)
        } finally {
            decodedBitmap?.recycle()
            source.recycle()
            imageFile.delete()
        }
    }

    @Test
    fun testGetOrientedBitmap() {
        Assert.assertNull(getOrientedBitmap(bitmap, 0f))
    }

    @Test
    fun testGetBitmapOrientation() {
        Assert.assertEquals(0f, getBitmapOrientation(exifInterface), 0f)
        exifInterface = Mockito.mock(
            ExifInterface::class.java
        )
        Mockito.`when`(
            (exifInterface as ExifInterface).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        ).thenReturn(
            ExifInterface.ORIENTATION_ROTATE_90
        )
        Assert.assertEquals(90f, getBitmapOrientation(exifInterface), 0f)
        Mockito.`when`(
            (exifInterface as ExifInterface).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        ).thenReturn(
            ExifInterface.ORIENTATION_ROTATE_180
        )
        Assert.assertEquals(180f, getBitmapOrientation(exifInterface), 0f)
        Mockito.`when`(
            (exifInterface as ExifInterface).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        ).thenReturn(
            ExifInterface.ORIENTATION_ROTATE_270
        )
        Assert.assertEquals(270f, getBitmapOrientation(exifInterface), 0f)
        Mockito.`when`(
            (exifInterface as ExifInterface).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        ).thenReturn(
            ExifInterface.ORIENTATION_NORMAL
        )
        Assert.assertEquals(0f, getBitmapOrientation(exifInterface), 0f)
    }
}
