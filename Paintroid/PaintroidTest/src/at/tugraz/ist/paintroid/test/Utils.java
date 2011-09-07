/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

public class Utils {
	public static int[] bitmapToPixelArray(Bitmap bitmap) {
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		int pixelArray[] = new int[bitmapWidth * bitmapHeight];
		bitmap.getPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
		return pixelArray;
	}

	public static int[] drawableToPixelArray(Drawable drawable) {
		if (!(drawable instanceof BitmapDrawable)) {
			junit.framework.Assert.assertTrue(false);
		}
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		int pixelArray[] = bitmapToPixelArray(bitmap);
		return pixelArray;
	}

	public static int colorFromDrawable(Drawable drawable) {
		if (!(drawable instanceof ColorDrawable)) {
			junit.framework.Assert.assertTrue(false);
		}
		Canvas canvas = new Canvas();
		Bitmap bitmap = Bitmap.createBitmap(3, 3, Config.ARGB_8888);
		canvas.setBitmap(bitmap);
		drawable.draw(canvas);
		int color = bitmap.getPixel(2, 2);
		bitmap.recycle();
		return color;
	}

	//	public static void selectColorFromPicker(Solo solo, int[] argb) {
	//		junit.framework.Assert.assertEquals(argb.length, 4);
	//		Activity mainActivity = solo.getCurrentActivity();
	//
	//		ImageButton colorButton = (ImageButton) mainActivity.findViewById(R.id.ibtn_Color);
	//		solo.clickOnView(colorButton);
	//		solo.waitForView(ColorPickerView.class, 1, 200);
	//		ArrayList<View> views = solo.getViews();
	//		View colorPickerView = null;
	//		View rgbSelectorView = null;
	//		for (View view : views) {
	//			if (view instanceof ColorPickerView)
	//				colorPickerView = view;
	//			if (view instanceof RgbSelectorView)
	//				rgbSelectorView = view;
	//		}
	//		junit.framework.Assert.assertNotNull(colorPickerView);
	//
	//		String rgbTab = mainActivity.getResources().getString(R.string.color_rgb);
	//		if (rgbSelectorView == null) {
	//			solo.clickOnText(rgbTab);
	//			views = solo.getViews(colorPickerView);
	//			for (View view : views) {
	//				if (view instanceof RgbSelectorView)
	//					rgbSelectorView = view;
	//			}
	//		}
	//		junit.framework.Assert.assertNotNull(rgbSelectorView);
	//
	//		solo.setProgressBar(0, argb[1]);
	//		solo.setProgressBar(1, argb[2]);
	//		solo.setProgressBar(2, argb[3]);
	//		solo.setProgressBar(3, argb[0]);
	//		String newColorButton = mainActivity.getResources().getString(R.string.color_new_color);
	//		solo.clickOnButton(newColorButton);
	//	}
}
