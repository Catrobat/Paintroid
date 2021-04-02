package org.catrobat.paintroid.test;

import android.graphics.Bitmap;
import android.media.ExifInterface;

import org.catrobat.paintroid.FileIO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
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
	}
}
