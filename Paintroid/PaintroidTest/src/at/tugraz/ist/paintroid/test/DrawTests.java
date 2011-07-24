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

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerView;
import at.tugraz.ist.paintroid.dialog.colorpicker.HsvAlphaSelectorView;
import at.tugraz.ist.paintroid.dialog.colorpicker.HsvSaturationSelectorView;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.utilities.Brush;

import com.jayway.android.robotium.solo.Solo;

public class DrawTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;

	// Buttonindexes
	final int COLORPICKER = 0;
	final int STROKE = 0;
	final int HAND = 1;
	final int MAGNIFIY = 2;
	final int BRUSH = 3;
	final int EYEDROPPER = 4;
	final int WAND = 5;
	final int UNDO = 6;
	final int REDO = 7;
	final int FILE = 8;

	final int STROKERECT = 0;
	final int STROKECIRLCE = 1;
	final int STROKE1 = 2;
	final int STROKE2 = 3;
	final int STROKE3 = 4;
	final int STROKE4 = 5;

	public DrawTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);

	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		String languageToLoad_before = "en";
		Locale locale_before = new Locale(languageToLoad_before);
		Locale.setDefault(locale_before);

		Configuration config_before = new Configuration();
		config_before.locale = locale_before;

		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources()
				.updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());
	}

	/**
	 * Test if background is transparent
	 * 
	 */
	@Smoke
	public void testDrawTRANSPARENTOnCanvas() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));

		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.setAntiAliasing(false);

		selectTransparentColorFromPicker();

		solo.clickOnImageButton(BRUSH);

		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();

		int halfScreenWidth = (int) (screenWidth * 0.5);
		int halfScreenHeight = (int) (screenHeight * 0.5);

		solo.clickOnScreen(halfScreenWidth, halfScreenHeight);

		Bitmap currentImage = mainActivity.getCurrentImage();

		assertNotNull(currentImage);

		float[] coordinatesOfLastClick = new float[2];
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		Thread.sleep(500);
		int pixel = mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1]);

		// 2.1 returns not completely transparent pixels here
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ECLAIR_MR1) {
			assertEquals(1, Color.alpha(pixel));
		} else {
			assertEquals(Color.TRANSPARENT, pixel);
		}

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Save");
		solo.enterText(0, "test_drawTransparent");
		solo.clickOnButton("Done");

		File file = new File(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/test_drawTransparent.png");

		if (file.exists()) {
			solo.clickOnButton("Yes");
			Log.d("PaintroidTest", "File has been overwriten");
		}

		assertTrue(solo.waitForActivity("MainActivity", 500));

		file.delete();
	}

	/**
	 * Test if Brush function works
	 * 
	 */
	public void testBrush() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.setAntiAliasing(false);
		solo.clickOnImageButton(BRUSH);

		selectTransparentColorFromPicker();

		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();

		ImageButton strokePickerButton = solo.getImageButton(STROKE);
		int[] locationstrokePickerButton = new int[2];
		strokePickerButton.getLocationOnScreen(locationstrokePickerButton);
		locationstrokePickerButton[0] += strokePickerButton.getMeasuredWidth();
		ImageButton handButton = solo.getImageButton(HAND);
		int[] locationHandButton = new int[2];
		handButton.getLocationOnScreen(locationHandButton);
		locationHandButton[1] -= handButton.getMeasuredHeight();

		ArrayList<View> actual_views = solo.getViews();
		View surfaceView = null;
		for (View view : actual_views) {
			if (view instanceof DrawingSurface) {
				surfaceView = view;
			}
		}
		assertNotNull(surfaceView);
		int[] coords = new int[2];
		surfaceView.getLocationOnScreen(coords);

		float min_x = locationstrokePickerButton[0];
		float min_y = coords[1];
		float max_x = screenWidth;
		float max_y = locationHandButton[1];
		// Get coordinates of begin of drag
		solo.clickOnScreen(min_x, min_y);
		float[] coordinatesOfFirstClick = new float[2];
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfFirstClick);
		Thread.sleep(500);
		solo.drag(min_x, max_x, min_y, max_y, 50);
		float[] coordinatesOfLastClick = new float[2];
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);

		//Change coordinates to real clicked ones
		min_x = coordinatesOfFirstClick[0];
		max_x = coordinatesOfLastClick[0];
		min_y = coordinatesOfFirstClick[1];
		max_y = coordinatesOfLastClick[1];

		float ratioYX = ((float) max_y - (float) min_y) / ((float) max_x - (float) min_x);

		mainActivity = (MainActivity) solo.getCurrentActivity();

		int testPixel1 = mainActivity.getPixelFromScreenCoordinates(min_x + 20, min_y + Math.round(20 * ratioYX));
		int testPixel2 = mainActivity.getPixelFromScreenCoordinates(max_x - 20, max_y - Math.round(20 * ratioYX));
		int testPixel3 = mainActivity.getPixelFromScreenCoordinates(min_x + (max_x - min_x) / 2,
				min_y + Math.round((max_x - min_x) / 2 * ratioYX));
		int testPixel4 = mainActivity.getPixelFromScreenCoordinates(min_x + 20, min_y + max_y / 2);
		int testPixel5 = mainActivity
				.getPixelFromScreenCoordinates(min_x + max_x / 2, min_y + Math.round(20 * ratioYX));

		assertEquals(testPixel1, Color.TRANSPARENT);
		assertEquals(testPixel2, Color.TRANSPARENT);
		assertEquals(testPixel3, Color.TRANSPARENT);
		assertTrue(testPixel4 != Color.TRANSPARENT);
		assertTrue(testPixel5 != Color.TRANSPARENT);

		selectBlackColorFromPicker();
		solo.clickOnScreen(35, 400);
		Thread.sleep(500);
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		int pixel = mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1]);
		assertEquals(Color.BLACK, pixel);

	}

	/**
	 * Test different brush sizes
	 * 
	 */
	public void testBrushSizes() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		solo.clickOnImageButton(BRUSH);

		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.setAntiAliasing(false);

		selectBlackColorFromPicker();

		Vector<int[]> strokesToTest = new Vector<int[]>();
		int[] stroke = new int[2];
		stroke[0] = STROKE1;
		stroke[1] = 1;
		strokesToTest.add(stroke);
		stroke = new int[2];
		stroke[0] = STROKE2;
		stroke[1] = 5;
		strokesToTest.add(stroke);
		stroke = new int[2];
		stroke[0] = STROKE3;
		stroke[1] = 15;
		strokesToTest.add(stroke);
		stroke = new int[2];
		stroke[0] = STROKE4;
		stroke[1] = 25;
		strokesToTest.add(stroke);

		int coordinatesIncrement = 100;
		int yDrawCoordinates = 100;

		for (int[] strokeWidthType : strokesToTest) {
			solo.clickOnImageButton(STROKE);
			solo.clickOnImageButton(STROKERECT);
			solo.clickOnImageButton(STROKE);
			solo.clickOnImageButton(strokeWidthType[0]);
			solo.waitForDialogToClose(200);
			solo.clickOnScreen(100, yDrawCoordinates);
			float[] coordinatesOfLastClick = new float[2];
			mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
			Brush brush = mainActivity.getActiveBrush();
			assertEquals(strokeWidthType[1], brush.stroke);
			assertEquals(Cap.SQUARE, brush.cap);
			Thread.sleep(500);
			int halfBrushWidth = (brush.stroke - 1) / 2;
			Point pixelCoordinates = mainActivity.getPixelCoordinates(coordinatesOfLastClick[0],
					coordinatesOfLastClick[1]);
			for (int count_x = -halfBrushWidth - 5; count_x <= halfBrushWidth + 5; count_x++) {
				for (int count_y = -halfBrushWidth - 5; count_y <= halfBrushWidth + 5; count_y++) {
					if (count_x >= -halfBrushWidth && count_x <= halfBrushWidth && count_y >= -halfBrushWidth
							&& count_y <= halfBrushWidth) {
						assertEquals(
								Color.BLACK,
								mainActivity.getCurrentImage().getPixel(pixelCoordinates.x + count_x,
										pixelCoordinates.y + count_y));
					} else {
						assertTrue(Color.BLACK != mainActivity.getCurrentImage().getPixel(pixelCoordinates.x + count_x,
								pixelCoordinates.y + count_y));
					}
				}
			}

			// Not drawn as circle if stroke width too small
			if (strokeWidthType[1] > 5) {
				solo.clickOnImageButton(STROKE);
				solo.clickOnImageButton(STROKECIRLCE);
				solo.waitForDialogToClose(200);
				solo.clickOnScreen(200, yDrawCoordinates);
				mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
				brush = mainActivity.getActiveBrush();
				assertEquals(strokeWidthType[1], brush.stroke);
				assertEquals(Cap.ROUND, brush.cap);
				Thread.sleep(500);
				halfBrushWidth = (brush.stroke - 1) / 2;
				pixelCoordinates = mainActivity.getPixelCoordinates(coordinatesOfLastClick[0],
						coordinatesOfLastClick[1]);

				// midllepoint
				assertEquals(Color.BLACK, mainActivity.getCurrentImage().getPixel(pixelCoordinates.x, pixelCoordinates.y));
				// top, bottom, left and right
				assertEquals(Color.BLACK,
						mainActivity.getCurrentImage()
								.getPixel(pixelCoordinates.x, pixelCoordinates.y - halfBrushWidth));
				assertEquals(
						Color.BLACK,
						mainActivity.getCurrentImage().getPixel(pixelCoordinates.x,
								pixelCoordinates.y + halfBrushWidth - 1));
				assertEquals(Color.BLACK,
						mainActivity.getCurrentImage()
								.getPixel(pixelCoordinates.x - halfBrushWidth, pixelCoordinates.y));
				assertEquals(
						Color.BLACK,
						mainActivity.getCurrentImage().getPixel(pixelCoordinates.x + halfBrushWidth - 1,
								pixelCoordinates.y));
				// Edges
				assertTrue(Color.BLACK != mainActivity.getCurrentImage().getPixel(pixelCoordinates.x - halfBrushWidth,
						pixelCoordinates.y - halfBrushWidth));
				assertTrue(Color.BLACK != mainActivity.getCurrentImage().getPixel(
						pixelCoordinates.x + halfBrushWidth - 1, pixelCoordinates.y - halfBrushWidth));
				assertTrue(Color.BLACK != mainActivity.getCurrentImage().getPixel(pixelCoordinates.x - halfBrushWidth,
						pixelCoordinates.y + halfBrushWidth - 1));
				assertTrue(Color.BLACK != mainActivity.getCurrentImage().getPixel(
						pixelCoordinates.x + halfBrushWidth - 1, pixelCoordinates.y + halfBrushWidth - 1));
			}
			yDrawCoordinates += coordinatesIncrement;
		}

	}

	/**
	 * Test if MagicWand function works
	 * 
	 */
	public void testMagicWand() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));

		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.setAntiAliasing(false);

		selectTransparentColorFromPicker();

		solo.clickOnImageButton(WAND);

		solo.clickOnScreen(35, 400);

		int testPixel1 = mainActivity.getPixelFromScreenCoordinates(35, 350);
		int testPixel2 = mainActivity.getPixelFromScreenCoordinates(25, 255);
		int testPixel3 = mainActivity.getPixelFromScreenCoordinates(40, 360);

		assertEquals(testPixel1, Color.TRANSPARENT);
		assertEquals(testPixel2, Color.TRANSPARENT);
		assertEquals(testPixel3, Color.TRANSPARENT);

		selectBlackColorFromPicker();

		solo.clickOnImageButton(BRUSH);
		solo.clickOnScreen(35, 400);
		float[] coordinatesOfLastClick = new float[2];
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		Thread.sleep(500);
		int testPixelRed = mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0],
				coordinatesOfLastClick[1]);
		assertEquals(Color.BLACK, testPixelRed);

		selectBlackColorFromPicker();
		solo.clickOnImageButton(WAND);

		solo.clickOnScreen(200, 200);

		testPixel1 = mainActivity.getPixelFromScreenCoordinates(200, 200);
		testPixel2 = mainActivity.getPixelFromScreenCoordinates(150, 200);
		testPixel3 = mainActivity.getPixelFromScreenCoordinates(10, 10);

		assertEquals(testPixel1, Color.BLACK);
		assertEquals(testPixel2, Color.BLACK);
		assertEquals(testPixel3, Color.BLACK);
		assertEquals(testPixelRed, Color.BLACK);

	}

	/**
	 * Test if EyeDropper function works
	 * 
	 */
	public void testEyeDropper() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		solo.clickOnImageButton(EYEDROPPER);

		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.setAntiAliasing(false);

		solo.clickOnScreen(35, 400);

		float[] coordinatesOfClick = new float[2];
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfClick);

		int testPixel = mainActivity.getPixelFromScreenCoordinates(coordinatesOfClick[0], coordinatesOfClick[1]);

		assertEquals(mainActivity.getSelectedColor(), testPixel);
	}

	/**
	 * Test if program crashes when any drawing tool is used outside of the bitmap
	 */
	public void testDrawingOutsideBitmap() {
		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
		solo.clickOnImageButton(HAND);
		solo.drag(0, 0, (float) (screenHeight - 200), 100, 10);
		solo.drag(0, 0, (float) (screenHeight - 200), 100, 10);
		solo.clickOnImageButton(BRUSH);
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.clickOnImageButton(WAND);
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		assertEquals(mainActivity, solo.getCurrentActivity());

	}
	
	private void selectTransparentColorFromPicker() {
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(ColorPickerView.class, 1, 200);
		ArrayList<View> views = solo.getViews();
		View colorPickerView = null;
		for (View view : views) {
			if (view instanceof ColorPickerView)
				colorPickerView = view;
		}
		assertNotNull(colorPickerView);
		solo.clickOnText("HSV");
		views = solo.getViews();
		View hsvAlphaSelectorView = null;
		for (View view : views) {
			if (view instanceof HsvAlphaSelectorView)
				hsvAlphaSelectorView = view;
		}
		assertNotNull(hsvAlphaSelectorView);
		int[] selectorCoords = new int[2];
		hsvAlphaSelectorView.getLocationOnScreen(selectorCoords);
		int width = hsvAlphaSelectorView.getWidth();
		int height = hsvAlphaSelectorView.getHeight();
		solo.clickOnScreen(selectorCoords[0]+(width/2), selectorCoords[1]+height-1);
		solo.clickOnButton("New Color");
		assertEquals(Color.TRANSPARENT, mainActivity.getSelectedColor());
	}
	
	private void selectBlackColorFromPicker() {
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(ColorPickerView.class, 1, 200);
		ArrayList<View> views = solo.getViews();
		View colorPickerView = null;
		for (View view : views) {
			if (view instanceof ColorPickerView)
				colorPickerView = view;
		}
		assertNotNull(colorPickerView);
		solo.clickOnText("HSV");
		views = solo.getViews();
		View hsvAlphaSelectorView = null;
		for (View view : views) {
			if (view instanceof HsvAlphaSelectorView)
				hsvAlphaSelectorView = view;
		}
		assertNotNull(hsvAlphaSelectorView);
		int[] selectorCoords = new int[2];
		hsvAlphaSelectorView.getLocationOnScreen(selectorCoords);
		int width = hsvAlphaSelectorView.getWidth();
		solo.clickOnScreen(selectorCoords[0]+(width/2), selectorCoords[1]+1);
		// HSV Saturation Selector
		View hsvSaturationSelectorView = null;
		for (View view : views) {
			if (view instanceof HsvSaturationSelectorView)
				hsvSaturationSelectorView = view;
		}
		assertNotNull(hsvSaturationSelectorView);
		selectorCoords = new int[2];
		hsvSaturationSelectorView.getLocationOnScreen(selectorCoords);
		width = hsvSaturationSelectorView.getWidth();
		int height = hsvSaturationSelectorView.getHeight();
		solo.clickOnScreen(selectorCoords[0]+width-1, selectorCoords[1]+height-1);
		solo.clickOnButton("New Color");
		assertEquals(Color.BLACK, mainActivity.getSelectedColor());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {

			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}
}
