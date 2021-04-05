package org.catrobat.paintroid.test.junit;

import android.graphics.Bitmap;

import org.catrobat.paintroid.FileIO;
import org.junit.Test;
import org.mockito.Mock;

import androidx.exifinterface.media.ExifInterface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileIOTest {

	@Mock
	Bitmap bitmap;
	@Mock
	ExifInterface exifInterface;

	@Test
	public void testGetOrientedBitmap() {
		assertNull(FileIO.getOrientedBitmap(bitmap, 0));
	}

	@Test
	public void testGetBitmapOrientation() {
		assertEquals(0, FileIO.getBitmapOrientation(exifInterface), 0);
		exifInterface = mock(ExifInterface.class);
		when(exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)).thenReturn(ExifInterface.ORIENTATION_ROTATE_90);
		assertEquals(90f, FileIO.getBitmapOrientation(exifInterface), 0);
		when(exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)).thenReturn(ExifInterface.ORIENTATION_ROTATE_180);
		assertEquals(180f, FileIO.getBitmapOrientation(exifInterface), 0);
		when(exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)).thenReturn(ExifInterface.ORIENTATION_ROTATE_270);
		assertEquals(270f, FileIO.getBitmapOrientation(exifInterface), 0);
		when(exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)).thenReturn(ExifInterface.ORIENTATION_NORMAL);
		assertEquals(0f, FileIO.getBitmapOrientation(exifInterface), 0);
	}
}
