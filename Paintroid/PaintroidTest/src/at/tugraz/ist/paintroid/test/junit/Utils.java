package at.tugraz.ist.paintroid.test.junit;

import android.graphics.Bitmap;

public class Utils {
	private Utils() {
	}

	public static boolean arrayEquals(int[] a, int[] b) {
		if (a.length != b.length) {
			return false;
		}
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}

	public static boolean bitmapEquals(Bitmap bmp1, Bitmap bmp2) {
		int[] a = bitmapToPixelArray(bmp1);
		int[] b = bitmapToPixelArray(bmp2);
		return arrayEquals(a, b);
	}

	public static int[] bitmapToPixelArray(Bitmap bitmap) {
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		int pixelArray[] = new int[bitmapWidth * bitmapHeight];
		bitmap.getPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
		return pixelArray;
	}
}
