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
import java.util.Locale;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.Mode;

import com.jayway.android.robotium.solo.Solo;

public class ImportPngTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;
	private DrawingSurface drawingSurface;
	private String hsvTab;
	private int screenWidth;
	private int screenHeight;

	// Buttonindexes
	final int COLORPICKER = 0;
	final int STROKE = 1;
	final int HAND = 2;
	final int MAGNIFIY = 3;
	final int BRUSH = 4;
	final int EYEDROPPER = 5;
	final int WAND = 6;
	final int UNDO = 7;
	final int REDO = 8;
	final int FILE = 9;

	final int STROKERECT = 0;
	final int STROKECIRLCE = 1;
	final int STROKE1 = 2;
	final int STROKE2 = 3;
	final int STROKE3 = 4;
	final int STROKE4 = 5;

	public ImportPngTests() {
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
	 * Check if the import works even if other modes like cursor, centerpoint, etc. are activated.
	 * 
	 */
	public void testImportOnDifferentModes() throws Exception {
		solo.waitForActivity("MainActivity", 500);

		File file = new File(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/import_png_test_1_save.png");

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Save");
		solo.enterText(0, "import_png_test_1_save");
		solo.clickOnButton("Done");

		// Override
		if (file.exists()) {
			solo.clickOnButton("Yes");
		}
		solo.sleep(500);

		assertTrue(file.exists());

		drawingSurface.addPng(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/import_png_test_1_save.png");
		solo.sleep(400);
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		solo.clickOnMenuItem("Stamp");
		solo.sleep(500);
		assertEquals(Mode.DRAW, drawingSurface.getMode());

		solo.clickOnImageButton(BRUSH);
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.drag(screenWidth / 2, screenWidth / 2 + 1, screenHeight / 2, screenHeight / 2, 50);
		solo.sleep(400);
		assertEquals(Mode.CURSOR, drawingSurface.getMode());

		drawingSurface.addPng(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/import_png_test_1_save.png");
		solo.sleep(400);
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		solo.clickOnMenuItem("Stamp");
		solo.sleep(200);
		assertEquals(Mode.DRAW, drawingSurface.getMode());

		solo.clickOnMenuItem("Define Center Point");
		solo.sleep(200);
		assertEquals(Mode.CENTERPOINT, drawingSurface.getMode());

		drawingSurface.addPng(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/import_png_test_1_save.png");
		solo.sleep(400);
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		solo.clickOnMenuItem("Stamp");
		solo.sleep(200);
		assertEquals(Mode.DRAW, drawingSurface.getMode());

		solo.clickOnMenuItem("Stamp");
		solo.sleep(200);
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		drawingSurface.addPng(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/import_png_test_1_save.png");
		solo.sleep(400);
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		solo.clickOnMenuItem("Stamp");
		solo.sleep(200);
		assertEquals(Mode.DRAW, drawingSurface.getMode());

		file.delete();
	}

	/**
	 * Check if the floating box stamps the correct picture
	 * 
	 */
	public void testImport() throws Exception {
		solo.waitForActivity("MainActivity", 500);

		//		Utils.selectColorFromPicker(solo, new int[] { 255, 0, 0, 0 });

		solo.clickOnImageButton(WAND);
		solo.clickOnScreen(screenWidth / 2, screenWidth / 2);

		File file = new File(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/import_png_test_1_save.png");

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Save");
		solo.enterText(0, "import_png_test_1_save");
		solo.clickOnButton("Done");

		// Override
		if (file.exists()) {
			solo.clickOnButton("Yes");
		}
		solo.sleep(500);

		assertTrue(file.exists());

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		solo.clickOnButton(0);
		assertTrue(solo.waitForActivity("MainActivity", 500));

		solo.clickOnImageButton(BRUSH);

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		drawingSurface.addPng(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/import_png_test_1_save.png");
		solo.sleep(400);
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		Point boxSize = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize);
		Point boxCoordinates = new Point(drawingSurface.getFloatingBoxCoordinates());
		assertNotNull(boxCoordinates);

		solo.sleep(500);

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		solo.sleep(500);

		solo.clickOnMenuItem("Stamp");
		solo.sleep(200);
		assertEquals(Mode.DRAW, drawingSurface.getMode());

		Point boxPixelCoordinates = drawingSurface.getPixelCoordinates(boxCoordinates.x, boxCoordinates.y);
		Point boxPixelSize = drawingSurface.getPixelCoordinates(boxSize.x, boxSize.y);
		assertEquals(Color.BLACK, drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x, boxPixelCoordinates.y));
		assertEquals(
				Color.BLACK,
				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x + boxPixelSize.x / 2 - 5,
						boxPixelCoordinates.y));
		assertEquals(
				Color.BLACK,
				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x - boxPixelSize.x / 2 + 5,
						boxPixelCoordinates.y));
		//		assertEquals(
		//				Color.BLACK,
		//				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x,
		//						boxPixelCoordinates.y + boxPixelSize.y / 2 - 5));
		//		assertEquals(
		//				Color.BLACK,
		//				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x,
		//						boxPixelCoordinates.y - boxPixelSize.y / 2 + 5));
		//		assertEquals(
		//				Color.BLACK,
		//				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x + boxPixelSize.x / 2 - 5,
		//						boxPixelCoordinates.y + boxPixelSize.y / 2 - 5));
		//		assertEquals(
		//				Color.BLACK,
		//				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x - boxPixelSize.x / 2 + 5,
		//						boxPixelCoordinates.y - boxPixelSize.y / 2 + 5));
		//		assertEquals(
		//				Color.BLACK,
		//				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x - boxPixelSize.x / 2 + 5,
		//						boxPixelCoordinates.y + boxPixelSize.y / 2 - 5));
		//		assertEquals(
		//				Color.BLACK,
		//				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x + boxPixelSize.x / 2 - 5,
		//						boxPixelCoordinates.y - boxPixelSize.y / 2 + 5));
		//
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x + boxPixelSize.x / 2 + 5,
		//				boxPixelCoordinates.y));
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x - boxPixelSize.x / 2 - 5,
		//				boxPixelCoordinates.y));
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x,
		//				boxPixelCoordinates.y + boxPixelSize.y / 2 + 10));
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x,
		//				boxPixelCoordinates.y - boxPixelSize.y / 2 - 10));
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x + boxPixelSize.x / 2 + 5,
		//				boxPixelCoordinates.y + boxPixelSize.y / 2 + 10));
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x - boxPixelSize.x / 2 - 5,
		//				boxPixelCoordinates.y - boxPixelSize.y / 2 - 10));
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x - boxPixelSize.x / 2 - 5,
		//				boxPixelCoordinates.y + boxPixelSize.y / 2 + 10));
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x + boxPixelSize.x / 2 + 5,
		//				boxPixelCoordinates.y - boxPixelSize.y / 2 - 10));

		file.delete();
	}

	/**
	 * Check if the floating box stamps the correct picture
	 * after loading two different pictures in a row
	 * 
	 */
	public void testDoubleImport() throws Exception {
		solo.waitForActivity("MainActivity", 500);

		File file1 = new File(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/import_png_test_1_save.png");

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Save");
		solo.enterText(0, "import_png_test_1_save");
		solo.clickOnButton("Done");

		// Override
		if (file1.exists()) {
			solo.clickOnButton("Yes");
		}
		solo.sleep(500);

		assertTrue(file1.exists());

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		solo.clickOnButton(0);
		assertTrue(solo.waitForActivity("MainActivity", 500));

		//		Utils.selectColorFromPicker(solo, new int[] { 255, 0, 0, 0 });

		solo.clickOnImageButton(WAND);
		solo.clickOnScreen(screenWidth / 2, screenWidth / 2);

		File file2 = new File(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/import_png_test_2_save.png");

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Save");
		solo.enterText(0, "import_png_test_2_save");
		solo.clickOnButton("Done");

		// Override
		if (file2.exists()) {
			solo.clickOnButton("Yes");
		}
		solo.sleep(500);

		assertTrue(file2.exists());

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		solo.clickOnButton(0);
		assertTrue(solo.waitForActivity("MainActivity", 500));

		solo.clickOnImageButton(BRUSH);

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		drawingSurface.addPng(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/import_png_test_1_save.png");
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		drawingSurface.addPng(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/import_png_test_2_save.png");
		assertEquals(Mode.FLOATINGBOX, drawingSurface.getMode());

		Point boxSize = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize);
		Point boxCoordinates = new Point(drawingSurface.getFloatingBoxCoordinates());
		assertNotNull(boxCoordinates);

		solo.sleep(500);

		solo.clickLongOnScreen(screenWidth / 2, screenHeight / 2);

		solo.sleep(500);

		solo.clickOnMenuItem("Stamp");
		solo.sleep(200);
		assertEquals(Mode.DRAW, drawingSurface.getMode());

		Point boxPixelCoordinates = drawingSurface.getPixelCoordinates(boxCoordinates.x, boxCoordinates.y);
		Point boxPixelSize = drawingSurface.getPixelCoordinates(boxSize.x, boxSize.y);
		assertEquals(Color.BLACK, drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x, boxPixelCoordinates.y));
		assertEquals(
				Color.BLACK,
				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x + boxPixelSize.x / 2 - 5,
						boxPixelCoordinates.y));
		assertEquals(
				Color.BLACK,
				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x - boxPixelSize.x / 2 + 5,
						boxPixelCoordinates.y));
		//		assertEquals(
		//				Color.BLACK,
		//				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x,
		//						boxPixelCoordinates.y + boxPixelSize.y / 2 - 5));
		//		assertEquals(
		//				Color.BLACK,
		//				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x,
		//						boxPixelCoordinates.y - boxPixelSize.y / 2 + 5));
		//		assertEquals(
		//				Color.BLACK,
		//				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x + boxPixelSize.x / 2 - 5,
		//						boxPixelCoordinates.y + boxPixelSize.y / 2 - 5));
		//		assertEquals(
		//				Color.BLACK,
		//				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x - boxPixelSize.x / 2 + 5,
		//						boxPixelCoordinates.y - boxPixelSize.y / 2 + 5));
		//		assertEquals(
		//				Color.BLACK,
		//				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x - boxPixelSize.x / 2 + 5,
		//						boxPixelCoordinates.y + boxPixelSize.y / 2 - 5));
		//		assertEquals(
		//				Color.BLACK,
		//				drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x + boxPixelSize.x / 2 - 5,
		//						boxPixelCoordinates.y - boxPixelSize.y / 2 + 5));
		//
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x + boxPixelSize.x / 2 + 5,
		//				boxPixelCoordinates.y));
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x - boxPixelSize.x / 2 - 5,
		//				boxPixelCoordinates.y));
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x,
		//				boxPixelCoordinates.y + boxPixelSize.y / 2 + 10));
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x,
		//				boxPixelCoordinates.y - boxPixelSize.y / 2 - 10));
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x + boxPixelSize.x / 2 + 5,
		//				boxPixelCoordinates.y + boxPixelSize.y / 2 + 10));
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x - boxPixelSize.x / 2 - 5,
		//				boxPixelCoordinates.y - boxPixelSize.y / 2 - 10));
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x - boxPixelSize.x / 2 - 5,
		//				boxPixelCoordinates.y + boxPixelSize.y / 2 + 10));
		//		assertTrue(Color.BLACK != drawingSurface.getBitmap().getPixel(boxPixelCoordinates.x + boxPixelSize.x / 2 + 5,
		//				boxPixelCoordinates.y - boxPixelSize.y / 2 - 10));

		file1.delete();
		file2.delete();
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
