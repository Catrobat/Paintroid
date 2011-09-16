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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;

import com.jayway.android.robotium.solo.Solo;

public class ImportPngTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;
	private DrawingSurface drawingSurface;
	private int screenWidth;
	private int screenHeight;
	private TextView toolButton;
	private TextView parameterButton1;

	public ImportPngTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		Utils.deleteFiles();
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
		toolButton = (TextView) mainActivity.findViewById(R.id.btn_Tool);
		parameterButton1 = (TextView) mainActivity.findViewById(R.id.btn_Parameter1);

		screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
	}

	/**
	 * Check if the import works even if other modes like cursor, centerpoint, etc. are activated.
	 * 
	 */
	public void testImportOnDifferentModes() throws Exception {
		String filename = "import_png_test_1_save";
		File file = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/" + filename + ".png");

		Utils.saveCurrentPicture(solo, filename);

		assertTrue(file.exists());

		drawingSurface.addPng(file.getAbsolutePath());
		solo.sleep(400);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());

		Utils.selectTool(solo, toolButton, R.string.button_brush);
		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		Utils.selectTool(solo, toolButton, R.string.button_cursor);
		assertEquals(ToolType.CURSOR, drawingSurface.getToolType());

		drawingSurface.addPng(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/import_png_test_1_save.png");
		solo.sleep(400);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());

		Utils.selectTool(solo, toolButton, R.string.button_brush);
		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());

		Utils.selectTool(solo, toolButton, R.string.button_zoom);
		assertEquals(ToolType.ZOOM, drawingSurface.getToolType());

		drawingSurface.addPng(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/import_png_test_1_save.png");
		solo.sleep(400);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());

		Utils.selectTool(solo, toolButton, R.string.button_brush);
		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());

		Utils.selectTool(solo, toolButton, R.string.button_floating_box);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());
		drawingSurface.addPng(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/import_png_test_1_save.png");
		solo.sleep(400);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());

		Utils.selectTool(solo, toolButton, R.string.button_brush);
		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());

		file.delete();
	}

	/**
	 * Check if the floating box stamps the correct picture
	 * 
	 */
	public void testImport() throws Exception {
		solo.waitForActivity("MainActivity", 500);

		Utils.selectColorFromPicker(solo, new int[] { 255, 0, 0, 0 }, parameterButton1);

		Utils.selectTool(solo, toolButton, R.string.button_magic);
		assertEquals(ToolType.MAGIC, drawingSurface.getToolType());
		solo.clickOnScreen(screenWidth / 2, screenWidth / 2);

		String filename = "import_png_test_1_save";
		File file = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/" + filename + ".png");

		Utils.saveCurrentPicture(solo, filename);

		assertTrue(file.exists());

		Canvas bitmapCanvas = new Canvas(drawingSurface.getBitmap());
		Paint paint = new Paint();
		paint.setAlpha(0);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		bitmapCanvas.drawPaint(paint);

		Utils.selectTool(solo, toolButton, R.string.button_brush);
		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		drawingSurface.addPng(file.getAbsolutePath());
		solo.sleep(400);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());

		Point boxSize = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize);
		Point boxCoordinates = new Point(drawingSurface.getToolCoordinates());
		assertNotNull(boxCoordinates);

		solo.sleep(500);

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		solo.sleep(500);

		Utils.selectTool(solo, toolButton, R.string.button_brush);
		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());

		assertEquals(Color.BLACK, drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x, boxCoordinates.y));
		assertEquals(Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 - 5, boxCoordinates.y));
		assertEquals(Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 + 5, boxCoordinates.y));
		assertEquals(Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x, boxCoordinates.y + boxSize.y / 2 - 5));
		assertEquals(Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x, boxCoordinates.y - boxSize.y / 2 + 5));
		assertEquals(
				Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 - 5, boxCoordinates.y
						+ boxSize.y / 2 - 5));
		assertEquals(
				Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 + 5, boxCoordinates.y
						- boxSize.y / 2 + 5));
		assertEquals(
				Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 + 5, boxCoordinates.y
						+ boxSize.y / 2 - 5));
		assertEquals(
				Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 - 5, boxCoordinates.y
						- boxSize.y / 2 + 5));

		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 + 5,
				boxCoordinates.y));
		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 - 5,
				boxCoordinates.y));
		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x, boxCoordinates.y
				+ boxSize.y / 2 + 10));
		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x, boxCoordinates.y
				- boxSize.y / 2 - 10));
		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 + 5,
				boxCoordinates.y + boxSize.y / 2 + 10));
		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 - 5,
				boxCoordinates.y - boxSize.y / 2 - 10));
		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 - 5,
				boxCoordinates.y + boxSize.y / 2 + 10));
		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 + 5,
				boxCoordinates.y - boxSize.y / 2 - 10));

		file.delete();
	}

	/**
	 * Check if the floating box stamps the correct picture
	 * after loading two different pictures in a row
	 * 
	 */
	public void testDoubleImport() throws Exception {
		String filename1 = "import_png_test_1_save";
		File file1 = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/" + filename1 + ".png");
		assertFalse(file1.exists());

		Utils.saveCurrentPicture(solo, filename1);
		assertTrue(file1.exists());

		Utils.selectColorFromPicker(solo, new int[] { 255, 0, 0, 0 }, parameterButton1);

		Utils.selectTool(solo, toolButton, R.string.button_magic);
		assertEquals(ToolType.MAGIC, drawingSurface.getToolType());
		solo.clickOnScreen(screenWidth / 2, screenWidth / 2);

		String filename2 = "import_png_test_2_save";
		File file2 = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/" + filename2 + ".png");

		Utils.saveCurrentPicture(solo, filename2);
		assertTrue(file2.exists());

		Canvas bitmapCanvas = new Canvas(drawingSurface.getBitmap());
		Paint paint = new Paint();
		paint.setAlpha(0);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		bitmapCanvas.drawPaint(paint);

		Utils.selectTool(solo, toolButton, R.string.button_brush);
		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		drawingSurface.addPng(file1.getAbsolutePath());
		solo.sleep(400);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());

		drawingSurface.addPng(file2.getAbsolutePath());
		solo.sleep(400);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());

		Point boxSize = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize);
		Point boxCoordinates = new Point(drawingSurface.getToolCoordinates());
		assertNotNull(boxCoordinates);

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		solo.sleep(500);

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		solo.sleep(500);

		Utils.selectTool(solo, toolButton, R.string.button_brush);
		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());

		assertEquals(Color.BLACK, drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x, boxCoordinates.y));
		assertEquals(Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 - 5, boxCoordinates.y));
		assertEquals(Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 + 5, boxCoordinates.y));
		assertEquals(Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x, boxCoordinates.y + boxSize.y / 2 - 5));
		assertEquals(Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x, boxCoordinates.y - boxSize.y / 2 + 5));
		assertEquals(
				Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 - 5, boxCoordinates.y
						+ boxSize.y / 2 - 5));
		assertEquals(
				Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 + 5, boxCoordinates.y
						- boxSize.y / 2 + 5));
		assertEquals(
				Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 + 5, boxCoordinates.y
						+ boxSize.y / 2 - 5));
		assertEquals(
				Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 - 5, boxCoordinates.y
						- boxSize.y / 2 + 5));

		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 + 5,
				boxCoordinates.y));
		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 - 5,
				boxCoordinates.y));
		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x, boxCoordinates.y
				+ boxSize.y / 2 + 10));
		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x, boxCoordinates.y
				- boxSize.y / 2 - 10));
		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 + 5,
				boxCoordinates.y + boxSize.y / 2 + 10));
		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 - 5,
				boxCoordinates.y - boxSize.y / 2 - 10));
		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 - 5,
				boxCoordinates.y + boxSize.y / 2 + 10));
		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 + 5,
				boxCoordinates.y - boxSize.y / 2 - 10));

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
