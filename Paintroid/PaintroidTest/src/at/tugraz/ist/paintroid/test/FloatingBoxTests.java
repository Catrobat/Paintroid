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
import android.graphics.Point;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerView;
import at.tugraz.ist.paintroid.dialog.colorpicker.HsvAlphaSelectorView;
import at.tugraz.ist.paintroid.dialog.colorpicker.HsvSaturationSelectorView;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.Mode;

import com.jayway.android.robotium.solo.Solo;

public class FloatingBoxTests extends ActivityInstrumentationTestCase2<MainActivity> {

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

	public FloatingBoxTests() {
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

	/**
	 * Check if the floating box mode is activated after pressing the menu button
	 * and deactivated after pressing it again.
	 * 
	 */
	public void testFloatingBoxModes() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");

		solo.clickOnMenuItem("Stamp");
		Thread.sleep(200);
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		solo.clickOnMenuItem("Stamp");
		Thread.sleep(200);
		assertEquals(Mode.DRAW, drawingSurface.getMode());
	}

	/**
	 * Check if the floating box is working correctly
	 * 
	 */
	public void testFloatingBox() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");

		solo.clickOnMenuItem("Stamp");
		Thread.sleep(200);
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		solo.drag(screenWidth / 2, screenWidth / 2 + 200, screenHeight / 2, screenHeight / 2 + 50, 10);

		Point coordinates = new Point(0, 0);
		coordinates = drawingSurface.getFloatingBoxCoordinates();

		solo.clickOnMenuItem("Stamp");
		Thread.sleep(200);
		assertEquals(Mode.DRAW, drawingSurface.getMode());

		assertTrue(coordinates.equals(screenWidth / 2 + 200, screenHeight / 2 + 50));
	}

	/**
	 * Check if the floating box stamp function is working correctly
	 * 
	 */
	public void testFloatingBoxStamp() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));

		selectBlackColorFromPicker();

		solo.clickOnImageButton(BRUSH);
		solo.clickOnScreen(screenWidth / 2 - 100, screenHeight / 2);
		Thread.sleep(500);
		float[] coordinatesOfLastClick = new float[2];
		drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		Point pixelCoordinates = drawingSurface.getPixelCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1]);
		assertEquals(Color.BLACK, drawingSurface.getBitmap().getPixel(pixelCoordinates.x, pixelCoordinates.y));

		solo.clickOnMenuItem("Stamp");
		Thread.sleep(200);
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		solo.drag(screenWidth / 2, screenWidth / 2 - 100, screenHeight / 2, screenHeight / 2, 10);

		Thread.sleep(500);

		solo.clickOnScreen(screenWidth / 2 - 100, screenHeight / 2);

		Thread.sleep(500);

		solo.drag(screenWidth / 2 - 100, screenWidth / 2 + 100, screenHeight / 2, screenHeight / 2 + 50, 10);

		Point coordinates = new Point(0, 0);
		coordinates = drawingSurface.getFloatingBoxCoordinates();

		solo.clickOnScreen(screenWidth / 2 + 100, screenHeight / 2 + 50);

		solo.clickOnMenuItem("Stamp");
		Thread.sleep(200);
		assertEquals(Mode.DRAW, drawingSurface.getMode());

		assertTrue(coordinates.equals(screenWidth / 2 + 100, screenHeight / 2 + 50));

		pixelCoordinates = drawingSurface.getPixelCoordinates(coordinatesOfLastClick[0] + 200,
				coordinatesOfLastClick[1] + 50);
		assertEquals(Color.BLACK, drawingSurface.getBitmap().getPixel(pixelCoordinates.x, pixelCoordinates.y));
	}

	/**
	 * Check if the floating box triggers a drag if moved to the edge of the screen
	 * 
	 */
	public void testFloatingBoxDrag() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));

		float scrollX = drawingSurface.getScrollX();
		float scrollY = drawingSurface.getScrollY();

		solo.clickOnMenuItem("Stamp");
		Thread.sleep(200);
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		solo.drag(screenWidth / 2, screenWidth / 2 + 500, screenHeight / 2, screenHeight / 2, 10);

		assertTrue(scrollX != drawingSurface.getScrollX());
		assertEquals(scrollY, drawingSurface.getScrollY());

		solo.drag(screenWidth - 10, screenWidth - 200, screenHeight / 2, screenHeight / 2, 10);

		scrollX = drawingSurface.getScrollX();

		solo.drag(screenWidth - 200, screenWidth - 200, screenHeight / 2, screenHeight / 2 + 500, 10);

		assertEquals(scrollX, drawingSurface.getScrollX());
		assertTrue(scrollY != drawingSurface.getScrollY());
	}

	/**
	 * Check if the floating box works if activated outside of image
	 * 
	 */
	public void testFloatingBoxOutsideImage() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));

		float scrollX = drawingSurface.getScrollX();
		float scrollY = drawingSurface.getScrollY();

		solo.clickOnMenuItem("Stamp");
		Thread.sleep(200);
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		solo.drag(screenWidth / 2, screenWidth / 2 + 500, screenHeight / 2, screenHeight / 2, 10);

		assertTrue(scrollX != drawingSurface.getScrollX());
		assertEquals(scrollY, drawingSurface.getScrollY());

		solo.clickOnScreen(screenWidth - 10, screenHeight / 2);
	}

	public void testFloatingBoxResize() throws Exception {
		int robotiumMistake = 25;

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));

		solo.clickOnMenuItem("Stamp");
		Thread.sleep(200);
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		Point boxSize1 = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize1);
		Point coordinates = new Point(drawingSurface.getFloatingBoxCoordinates());
		assertNotNull(coordinates);

		//right
		solo.drag(coordinates.x + boxSize1.x / 2 + 10, coordinates.x + boxSize1.x / 2 + 110, coordinates.y,
				coordinates.y, 10);
		Point boxSize2 = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize2);
		assertEquals(boxSize1.y, boxSize2.y);
		assertTrue(boxSize1.x < boxSize2.x);

		//left
		solo.drag(coordinates.x - boxSize1.x / 2 - 10, coordinates.x - boxSize1.x / 2 - 110, coordinates.y,
				coordinates.y, 10);
		Point boxSize3 = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize3);
		assertEquals(boxSize1.y, boxSize3.y);
		assertTrue(boxSize2.x < boxSize3.x);
		assertTrue(boxSize1.x < boxSize3.x);

		//top
		solo.drag(coordinates.x, coordinates.x, coordinates.y - boxSize1.y / 2 - 10 + robotiumMistake, coordinates.y
				- boxSize1.y / 2 - 100, 10);
		Point boxSize4 = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize4);
		assertEquals(boxSize3.x, boxSize4.x);
		assertTrue(boxSize3.y < boxSize4.y);
		assertTrue(boxSize1.y < boxSize4.y);

		//bottom
		solo.drag(coordinates.x, coordinates.x, coordinates.y + boxSize1.y / 2 + 10 + robotiumMistake, coordinates.y
				+ boxSize1.y / 2 + 100, 10);
		Point boxSize5 = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize5);
		assertEquals(boxSize3.x, boxSize5.x);
		assertTrue(boxSize4.y < boxSize5.y);
		assertTrue(boxSize1.y < boxSize5.y);
	}

	public void testFloatingBoxRotate() throws Exception {
		int roationSymbolDistance = 30;
		int robotiumMistake = 25;

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));

		solo.clickOnMenuItem("Stamp");
		Thread.sleep(200);
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		Point boxSize1 = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize1);
		Point coordinates = new Point(drawingSurface.getFloatingBoxCoordinates());
		assertNotNull(coordinates);

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		Thread.sleep(500);

		float rotation = drawingSurface.getFloatingBoxRotation();

		//left
		solo.drag(coordinates.x - boxSize1.x / 2 - roationSymbolDistance - 10, coordinates.x - roationSymbolDistance
				- boxSize1.x / 2 - 110, coordinates.y - boxSize1.y / 2 - roationSymbolDistance - 10 + robotiumMistake,
				coordinates.y - boxSize1.y / 2 - roationSymbolDistance - 10 + robotiumMistake, 10);
		float rotation_after_1 = drawingSurface.getFloatingBoxRotation();
		assertTrue(rotation > rotation_after_1);

		//    solo.clickOnImageButton(BRUSH);    

		solo.clickOnMenuItem("Stamp");
		Thread.sleep(200);
		assertEquals(Mode.DRAW, drawingSurface.getMode());

		solo.clickOnMenuItem("Stamp");
		Thread.sleep(200);
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		Thread.sleep(500);

		//right
		solo.drag(coordinates.x - boxSize1.x / 2 - roationSymbolDistance - 10, coordinates.x - roationSymbolDistance
				- boxSize1.x / 2 + 110, coordinates.y - boxSize1.y / 2 - roationSymbolDistance - 10 + robotiumMistake,
				coordinates.y - boxSize1.y / 2 - roationSymbolDistance - 10 + robotiumMistake, 10);
		float rotation_after_2 = drawingSurface.getFloatingBoxRotation();
		assertTrue(rotation < rotation_after_2);
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
		solo.clickOnScreen(selectorCoords[0] + (width / 2), selectorCoords[1] + 1);
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
		solo.clickOnScreen(selectorCoords[0] + width - 1, selectorCoords[1] + height - 1);
		solo.clickOnButton("New Color");
		assertEquals(Color.BLACK, drawingSurface.getActiveColor());
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
