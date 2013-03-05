/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerView;
import org.catrobat.paintroid.dialog.colorpicker.RgbSelectorView;
import org.catrobat.paintroid.ui.Perspective;
import org.catrobat.paintroid.ui.Perspective;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.GridView;

import com.jayway.android.robotium.solo.Solo;

public class Utils {
	public static final String TAG = "PAINTROID";

	public static void doWorkaroundSleepForDrawingSurfaceThreadProblem() {
		// This has to be done before every test that leads to a new DrawingSurfaceThread
		// Otherwise two threads (one from before) are running in parallel which sometimes
		// leads to this nasty SEGMENTATION FAULT!
		try {
			Thread.sleep(1250);
		} catch (Exception e) {
		}
	}

	public static void assertArrayEquals(int[] a, int[] b) {
		if (a.length != b.length)
			junit.framework.Assert.assertFalse(true);
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) {
				Log.e("assertArrayEquals", "Arrays don't equal on position " + i + ". Expected " + a[i] + " but was "
						+ b[i]);
				junit.framework.Assert.assertFalse(true);
			}
		}
		junit.framework.Assert.assertTrue(true);
	}

	public static void assertArrayNotEquals(int[] a, int[] b) {
		if (a.length != b.length)
			junit.framework.Assert.assertFalse(true);
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) {
				junit.framework.Assert.assertTrue(true);
				return;
			}

		}
		junit.framework.Assert.assertFalse(true);
	}

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

	public static void selectTool(Solo solo, View toolbarMainButton, int stringId) {
		solo.clickOnView(toolbarMainButton);
		solo.waitForView(GridView.class, 1, 2000);
		solo.clickOnText(solo.getCurrentActivity().getString(stringId));
		solo.waitForActivity("MainActivity", 2000);
	}

	public static void selectColorFromPicker(Solo solo, int[] argb, View colorpickerButton) {
		junit.framework.Assert.assertEquals(argb.length, 4);
		Activity mainActivity = solo.getCurrentActivity();

		if (!viewIsVisible(solo, ColorPickerView.class)) {
			solo.clickOnView(colorpickerButton);
		}

		solo.waitForView(ColorPickerView.class, 1, 2000);
		ArrayList<View> views = solo.getViews();
		View colorPickerView = null;
		View rgbSelectorView = null;
		for (View view : views) {
			if (view instanceof ColorPickerView)
				colorPickerView = view;
			if (view instanceof RgbSelectorView)
				rgbSelectorView = view;
		}
		junit.framework.Assert.assertNotNull(colorPickerView);

		String rgbTab = mainActivity.getResources().getString(R.string.color_rgb);
		if (rgbSelectorView == null) {
			solo.clickOnText(rgbTab);
			solo.waitForView(RgbSelectorView.class, 1, 1000);
			views = solo.getViews(colorPickerView);
			for (View view : views) {
				if (view instanceof RgbSelectorView)
					rgbSelectorView = view;
			}
		}
		junit.framework.Assert.assertNotNull(rgbSelectorView);

		solo.setProgressBar(0, argb[1]);
		solo.setProgressBar(1, argb[2]);
		solo.setProgressBar(2, argb[3]);
		solo.setProgressBar(3, argb[0]);
		String newColorButton = mainActivity.getResources().getString(R.string.ok);
		solo.clickOnButton(newColorButton);
		solo.waitForDialogToClose(500);
	}

	/**
	 * @param solo Robotium Solo
	 * @param viewclass Concrete Class of the View
	 * @return true if such a View is visible, false otherwise
	 */
	public static boolean viewIsVisible(Solo solo, Class<? extends View> viewclass) {
		ArrayList<View> visibleViews = solo.getViews();
		for (int i = 0; i < visibleViews.size(); i++) {
			if (visibleViews.get(i).getClass().getName().equals(viewclass.getName()))
				return true;
		}
		return false;
	}

	public static void saveCurrentPicture(Solo solo, String fileName) {
		// TextView toolbarMainButton = (TextView) solo.getCurrentActivity().findViewById(R.id.btn_status_tool);
		// solo.clickOnView(toolbarMainButton);
		// solo.waitForActivity("MenuTabActivity", 1000);
		// solo.clickOnText("File"); // TODO: should be in resources
		// solo.waitForActivity("FileActivity", 1000);
		// Resources resources = solo.getCurrentActivity().getResources();
		// solo.clickOnButton(res.getText(R.string.save).toString());
		solo.clickOnMenuItem(solo.getString(R.string.menu_save_image), true);
		solo.enterText(0, fileName);
		solo.clickOnButton(solo.getString(R.string.done));
		solo.waitForActivity("MainActivity", 1000);
	}

	public static void deleteFiles(File dir) {
		String[] children = dir.list();
		for (int i = 0; i < children.length; i++) {
			new File(dir, children[i]).delete();
		}
	}

	public static void setLocale(Solo solo, Locale locale) {
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		Activity activity = solo.getCurrentActivity();
		Resources res = activity.getBaseContext().getResources();
		res.updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());
	}

	/**
	 * Performs a clickOnScreen at the coordinates defined in percentage of screen width and height.
	 * 
	 * @param solo Solo object of the test
	 * @param percentageX [0..100] x-coordinate in percentage of screen width
	 * @param percentageY [0..100] y-coordinate in percentage of screen height
	 */
	public static void clickOnScreen(Solo solo, int percentageX, int percentageY) {
		Display disp = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();
		float x = disp.getWidth() * (percentageX / 100f);
		float y = disp.getHeight() * (percentageY / 100f);
		solo.clickOnScreen(x, y);
	}

	/**
	 * Performs a drag with coordinates defined in percentage of screen width and height.
	 * 
	 * @param solo Solo object of the test
	 * @param percentageFromX [0..100] starting x-coordinate in percentage of screen width
	 * @param percentageToX [0..100] destination x-coordinate in percentage of screen height
	 * @param percentageFromY [0..100] starting y-coordinate in percentage of screen width
	 * @param percentageToY [0..100] destination y-coordinate in percentage of screen height
	 */
	public static void drag(Solo solo, int percentageFromX, int percentageToX, int percentageFromY, int percentageToY) {
		Display disp = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();
		float fromX = disp.getWidth() * (percentageFromX / 100f);
		float toX = disp.getHeight() * (percentageToX / 100f);
		float fromY = disp.getWidth() * (percentageFromY / 100f);
		float toY = disp.getHeight() * (percentageToY / 100f);
		solo.drag(fromX, toX, fromY, toY, 1);
	}

	public static int[] getPixels(Bitmap bitmap, int x, int y, int brushSize) {
		int[] pixels = new int[brushSize * brushSize];
		bitmap.getPixels(pixels, 0, brushSize, x - (brushSize / 2), y - (brushSize / 2), brushSize, brushSize);
		return pixels;
	}

	public static int[] getPixels(Bitmap bitmap, int x, int y, int width, int height) {
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, x, y, width, height);
		return pixels;
	}

	public static boolean containsValue(int[] array, int value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value) {
				return true;
			}
		}
		return false;
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

	public static synchronized Point convertFromCanvasToScreen(Point canvasPoint, Perspective currentPerspective)
			throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		Float surfaceCenterX = (Float) PrivateAccess.getMemberValue(Perspective.class,
				currentPerspective, "mSurfaceCenterX");
		Float surfaceScale = (Float) PrivateAccess.getMemberValue(Perspective.class, currentPerspective,
				"mSurfaceScale");
		Float surfaceTranslationX = (Float) PrivateAccess.getMemberValue(Perspective.class,
				currentPerspective, "mSurfaceTranslationX");
		Float surfaceCenterY = (Float) PrivateAccess.getMemberValue(Perspective.class,
				currentPerspective, "mSurfaceCenterY");
		Float surfaceTranslationY = (Float) PrivateAccess.getMemberValue(Perspective.class,
				currentPerspective, "mSurfaceTranslationY");

		Point screenPoint = new Point();
		// screenPoint.x = (int) ((p.x - surfaceCenterX) / surfaceScale + surfaceCenterX - surfaceTranslationX);
		// screenPoint.y = (int) ((p.y - surfaceCenterY) / surfaceScale + surfaceCenterY - surfaceTranslationY);

		screenPoint.x = (int) ((canvasPoint.x + surfaceTranslationX - surfaceCenterX) * surfaceScale + surfaceCenterX);
		screenPoint.y = (int) ((canvasPoint.y + surfaceTranslationY - surfaceCenterY) * surfaceScale + surfaceCenterY);

		return screenPoint;
	}
}
