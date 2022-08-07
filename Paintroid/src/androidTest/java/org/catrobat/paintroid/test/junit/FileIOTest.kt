package org.catrobat.paintroid.test.junit

import org.catrobat.paintroid.FileIO.getOrientedBitmap
import org.catrobat.paintroid.FileIO.getBitmapOrientation
import org.mockito.Mock
import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class FileIOTest {
    @Mock
    var bitmap: Bitmap? = null

    @Mock
    lateinit var exifInterface: ExifInterface

    @Test
    fun testGetOrientedBitmap() { Assert.assertNull(getOrientedBitmap(bitmap, 0f)) }

    @Test
    fun testGetBitmapOrientation() {
        Assert.assertEquals(0f, getBitmapOrientation(exifInterface), 0f)
        exifInterface = Mockito.mock(ExifInterface::class.java)
        Mockito.`when`(exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        ).thenReturn(ExifInterface.ORIENTATION_ROTATE_90)
        Assert.assertEquals(90f, getBitmapOrientation(exifInterface), 0f)
        Mockito.`when`(exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        ).thenReturn(ExifInterface.ORIENTATION_ROTATE_180)
        Assert.assertEquals(180f, getBitmapOrientation(exifInterface), 0f)
        Mockito.`when`(exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        ).thenReturn(ExifInterface.ORIENTATION_ROTATE_270)
        Assert.assertEquals(270f, getBitmapOrientation(exifInterface), 0f)
        Mockito.`when`(exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        ).thenReturn(ExifInterface.ORIENTATION_NORMAL)
        Assert.assertEquals(0f, getBitmapOrientation(exifInterface), 0f)
    }
}
