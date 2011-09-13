package at.tugraz.ist.paintroid.test;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerView;
import at.tugraz.ist.paintroid.dialog.colorpicker.RgbSelectorView;

import com.jayway.android.robotium.solo.Solo;

public class Utils {
	public static final String TAG = "PAINTROID";

	public static void assertArrayEquals(int[] a, int[] b) {
		if (a.length != b.length)
			junit.framework.Assert.assertFalse(true);
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i])
				junit.framework.Assert.assertFalse(true);
		}
		junit.framework.Assert.assertTrue(true);
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

	public static void selectColorFromPicker(Solo solo, int[] argb) {
		junit.framework.Assert.assertEquals(argb.length, 4);
		Activity mainActivity = solo.getCurrentActivity();

		ImageButton colorButton = (ImageButton) mainActivity.findViewById(R.id.ibtn_Color);
		solo.clickOnView(colorButton);
		solo.waitForView(ColorPickerView.class, 1, 200);
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
		String newColorButton = mainActivity.getResources().getString(R.string.color_new_color);
		solo.clickOnButton(newColorButton);
		solo.waitForDialogToClose(500);
	}

	/**
	 * @param solo
	 *            Robotium Solo
	 * @param viewclass
	 *            Concrete Class of the View
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
}
