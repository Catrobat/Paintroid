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

import java.util.ArrayList;
import java.util.Locale;

import android.content.res.Configuration;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ImageButton;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerView;
import at.tugraz.ist.paintroid.dialog.colorpicker.HsvAlphaSelectorView;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.Mode;
import at.tugraz.ist.paintroid.graphic.utilities.Tool.ToolState;

import com.jayway.android.robotium.solo.Solo;

public class CursorTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;
	private DrawingSurface drawingSurface;
	private String hsvTab;
	private int screenWidth;
	private int screenHeight;

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

	final int CursorStateINACTIVE = 0;
	final int CursorStateACTIVE = 1;
	final int CursorStateDRAW = 2;

	public CursorTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);

	}

	@Override
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

		drawingSurface = (DrawingSurface) mainActivity.findViewById(R.id.surfaceview);
		hsvTab = mainActivity.getResources().getString(R.string.color_hsv);

		screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
	}

	public void testCursorStates() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		mainActivity = (MainActivity) solo.getCurrentActivity();
		drawingSurface.setAntiAliasing(false);
		solo.clickOnImageButton(BRUSH);
		// double tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.drag(screenWidth / 2, screenWidth / 2 + 1, screenHeight / 2, screenHeight / 2, 50);
		Thread.sleep(400);
		assertEquals(Mode.CURSOR, drawingSurface.getMode());
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		Thread.sleep(400);
		assertEquals(ToolState.DRAW, drawingSurface.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		Thread.sleep(400);
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		// double tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.drag(screenWidth / 2, screenWidth / 2 + 1, screenHeight / 2, screenHeight / 2, 50);
		Thread.sleep(400);
		assertEquals(ToolState.INACTIVE, drawingSurface.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		Thread.sleep(400);
		assertEquals(ToolState.INACTIVE, drawingSurface.getToolState());
		// double tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.drag(screenWidth / 2, screenWidth / 2 + 1, screenHeight / 2, screenHeight / 2, 50);
		Thread.sleep(400);
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		Thread.sleep(400);
		assertEquals(ToolState.DRAW, drawingSurface.getToolState());
		// double tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.drag(screenWidth / 2, screenWidth / 2 + 1, screenHeight / 2, screenHeight / 2, 50);
		Thread.sleep(400);
		assertEquals(ToolState.INACTIVE, drawingSurface.getToolState());

	}

	public void testCursorDraw() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		mainActivity = (MainActivity) solo.getCurrentActivity();
		drawingSurface.setAntiAliasing(false);
		solo.clickOnImageButton(BRUSH);
		// double tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.drag(screenWidth / 2, screenWidth / 2 + 1, screenHeight / 2, screenHeight / 2, 50);
		Thread.sleep(400);
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		float[] coordinatesOfLastClick = new float[2];
		drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		Thread.sleep(400);
		assertEquals(ToolState.DRAW, drawingSurface.getToolState());

		int testPixel1 = drawingSurface.getPixelFromScreenCoordinates(coordinatesOfLastClick[0],
				coordinatesOfLastClick[1]);
		int testPixel2 = drawingSurface.getPixelFromScreenCoordinates(coordinatesOfLastClick[0] + 30,
				coordinatesOfLastClick[1]);

		assertEquals(drawingSurface.getActiveColor(), testPixel1);
		assertEquals(Color.WHITE, testPixel2);

	}

	public void testCursorDrawPath() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		mainActivity = (MainActivity) solo.getCurrentActivity();
		drawingSurface.setAntiAliasing(false);
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
		// double tap
		solo.clickOnScreen(min_x, min_y);
		float[] coordinatesOfFirstClick = new float[2];
		drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfFirstClick);
		solo.drag(min_x, min_x + 1, min_y, min_y, 50);
		Thread.sleep(400);
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		Thread.sleep(400);
		assertEquals(ToolState.DRAW, drawingSurface.getToolState());
		Thread.sleep(500);
		solo.drag(min_x, max_x, min_y, max_y, 50);
		float[] coordinatesOfLastClick = new float[2];
		drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);

		//Change coordinates to real clicked ones
		min_x = coordinatesOfFirstClick[0];
		max_x = coordinatesOfLastClick[0];
		min_y = coordinatesOfFirstClick[1];
		max_y = coordinatesOfLastClick[1];

		float ratioYX = (max_y - min_y) / (max_x - min_x);

		mainActivity = (MainActivity) solo.getCurrentActivity();

		int testPixel1 = drawingSurface.getPixelFromScreenCoordinates(min_x + 20, min_y + Math.round(20 * ratioYX));
		int testPixel2 = drawingSurface.getPixelFromScreenCoordinates(max_x - 20, max_y - Math.round(20 * ratioYX));
		int testPixel3 = drawingSurface.getPixelFromScreenCoordinates(min_x + (max_x - min_x) / 2,
				min_y + Math.round((max_x - min_x) / 2 * ratioYX));
		int testPixel4 = drawingSurface.getPixelFromScreenCoordinates(min_x + 20, min_y + max_y / 2);
		int testPixel5 = drawingSurface.getPixelFromScreenCoordinates(min_x + max_x / 2,
				min_y + Math.round(20 * ratioYX));

		assertEquals(testPixel1, Color.TRANSPARENT);
		assertEquals(testPixel2, Color.TRANSPARENT);
		assertEquals(testPixel3, Color.TRANSPARENT);
		assertTrue(testPixel4 != Color.TRANSPARENT);
		assertTrue(testPixel5 != Color.TRANSPARENT);

	}

	public void testCursorDrawAfterPaintChange() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		mainActivity = (MainActivity) solo.getCurrentActivity();
		drawingSurface.setAntiAliasing(false);
		solo.clickOnImageButton(BRUSH);
		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKECIRLCE);
		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKE3);
		// double tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.drag(screenWidth / 2, screenWidth / 2 + 1, screenHeight / 2, screenHeight / 2, 50);
		Thread.sleep(400);
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		float[] coordinatesOfLastClick = new float[2];
		drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		Thread.sleep(400);
		assertEquals(ToolState.DRAW, drawingSurface.getToolState());

		int testPixel1 = drawingSurface.getPixelFromScreenCoordinates(coordinatesOfLastClick[0],
				coordinatesOfLastClick[1]);
		int testPixel2 = drawingSurface.getPixelFromScreenCoordinates(coordinatesOfLastClick[0] + 30,
				coordinatesOfLastClick[1]);
		assertEquals(Color.BLACK, drawingSurface.getActiveColor());
		assertEquals(drawingSurface.getActiveColor(), testPixel1);
		assertEquals(Color.WHITE, testPixel2);

		selectTransparentColorFromPicker();

		int testPixel3 = drawingSurface.getPixelFromScreenCoordinates(coordinatesOfLastClick[0],
				coordinatesOfLastClick[1]);
		assertEquals(drawingSurface.getActiveColor(), testPixel3);

		int strokeWidth = drawingSurface.getActiveBrush().stroke;

		int testPixel4 = drawingSurface.getPixelFromScreenCoordinates(coordinatesOfLastClick[0] + strokeWidth * 3 / 4,
				coordinatesOfLastClick[1] + strokeWidth * 3 / 4);
		assertEquals(Color.WHITE, testPixel4);

		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKERECT);
		Thread.sleep(500);
		int testPixel5 = drawingSurface.getPixelFromScreenCoordinates(coordinatesOfLastClick[0] + strokeWidth * 3 / 4,
				coordinatesOfLastClick[1] + strokeWidth * 3 / 4);
		assertEquals(drawingSurface.getActiveColor(), testPixel5);

		int testPixel6 = drawingSurface.getPixelFromScreenCoordinates(coordinatesOfLastClick[0] + strokeWidth * 3 / 4
				+ 1, coordinatesOfLastClick[1] + strokeWidth * 3 / 4 + 1);
		assertEquals(Color.WHITE, testPixel6);

		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKE4);
		Thread.sleep(500);
		int testPixel7 = drawingSurface.getPixelFromScreenCoordinates(coordinatesOfLastClick[0] + strokeWidth * 3 / 4
				+ 1, coordinatesOfLastClick[1] + strokeWidth * 3 / 4 + 1);
		assertEquals(drawingSurface.getActiveColor(), testPixel7);
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
		solo.clickOnText(hsvTab);
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
		solo.clickOnScreen(selectorCoords[0] + (width / 2), selectorCoords[1] + height - 1);
		solo.clickOnButton("New Color");
		assertEquals(Color.TRANSPARENT, drawingSurface.getActiveColor());
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