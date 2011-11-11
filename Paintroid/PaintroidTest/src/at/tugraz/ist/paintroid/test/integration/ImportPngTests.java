/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test.integration;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import at.tugraz.ist.paintroid.FileIO;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.deprecated.graphic.DrawingSurface;

import com.jayway.android.robotium.solo.Solo;

public class ImportPngTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;
	private DrawingSurface drawingSurface;
	private int screenWidth;
	private int screenHeight;
	private TextView toolbarMainButton;
	private TextView toolbarButton1;

	public ImportPngTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();
		Utils.setLocale(solo, Locale.ENGLISH);

		//TODO drawingSurface = (DrawingSurface) mainActivity.findViewById(R.id.surfaceview);
		drawingSurface = null;
		toolbarMainButton = (TextView) mainActivity.findViewById(R.id.btn_Tool);
		toolbarButton1 = (TextView) mainActivity.findViewById(R.id.btn_Parameter1);

		screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
		Utils.deleteFiles(mainActivity.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES));
	}

	/**
	 * Check if the import works even if other modes like cursor, centerpoint, etc. are activated.
	 * 
	 */
	//	public void testImportOnDifferentModes() throws Exception {
	//		String filename = "import_png_test_1_save";
	//		File file = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/" + filename + ".png");
	//
	//		Utils.saveCurrentPicture(solo, filename);
	//
	//		assertTrue(file.exists());
	//
	//		drawingSurface.addPng(file.getAbsolutePath());
	//		solo.sleep(400);
	//		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());
	//
	//		Utils.selectTool(solo, toolbarMainButton, R.string.button_brush);
	//		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());
	//
	//		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
	//
	//		Utils.selectTool(solo, toolbarMainButton, R.string.button_cursor);
	//		assertEquals(ToolType.CURSOR, drawingSurface.getToolType());
	//
	//		drawingSurface.addPng(Environment.getExternalStorageDirectory().toString()
	//				+ "/Paintroid/import_png_test_1_save.png");
	//		solo.sleep(400);
	//		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());
	//
	//		Utils.selectTool(solo, toolbarMainButton, R.string.button_brush);
	//		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());
	//
	//		Utils.selectTool(solo, toolbarMainButton, R.string.button_zoom);
	//		assertEquals(ToolType.ZOOM, drawingSurface.getToolType());
	//
	//		drawingSurface.addPng(Environment.getExternalStorageDirectory().toString()
	//				+ "/Paintroid/import_png_test_1_save.png");
	//		solo.sleep(400);
	//		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());
	//
	//		Utils.selectTool(solo, toolbarMainButton, R.string.button_brush);
	//		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());
	//
	//		Utils.selectTool(solo, toolbarMainButton, R.string.button_floating_box);
	//		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());
	//		drawingSurface.addPng(Environment.getExternalStorageDirectory().toString()
	//				+ "/Paintroid/import_png_test_1_save.png");
	//		solo.sleep(400);
	//		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());
	//
	//		Utils.selectTool(solo, toolbarMainButton, R.string.button_brush);
	//		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());
	//
	//		file.delete();
	//	}

	/**
	 * Check if the floating box stamps the correct picture
	 * 
	 */
	public void testImport() throws Exception {
		assertTrue(solo.waitForView(DrawingSurface.class, 1, 1000));

		Utils.selectTool(solo, toolbarMainButton, R.string.button_magic);
		assertTrue(solo.waitForView(DrawingSurface.class, 1, 1000));
		Utils.clickOnScreen(solo, 50, 50);

		String fileName = "test";
		File file = FileIO.saveBitmap(mainActivity, null, fileName);
		assertFalse(file.exists());
		Utils.saveCurrentPicture(solo, fileName);
		assertTrue(file.exists());

		assertTrue(solo.waitForView(DrawingSurface.class, 1, 1000));
		drawingSurface.fillWithTransparency();

		Utils.selectTool(solo, toolbarMainButton, R.string.button_brush);
		assertTrue(solo.waitForView(DrawingSurface.class, 1, 1000));

		Intent intent = new Intent();
		intent.setDataAndType(Uri.fromFile(file), "image/png");
		mainActivity.onActivityResult(MainActivity.REQ_IMPORTPNG, Activity.RESULT_OK, intent);

		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());
		Point boxSize = drawingSurface.getFloatingBoxSize();
		Point boxCoordinates = drawingSurface.getToolCoordinates();

		Utils.clickOnScreen(solo, 50, 50); // TODO: click does not work
		solo.sleep(500);

		Utils.selectTool(solo, toolbarMainButton, R.string.button_brush);
		assertTrue(solo.waitForView(DrawingSurface.class, 1, 1000));

		Point bmpCoords = drawingSurface.translate2Image(boxCoordinates.x - (boxSize.x / 2), boxCoordinates.y
				- (boxSize.y / 2));

		Point size = new Point(boxSize.x / 2, boxSize.y / 2);
		int[] pixels = Utils.getPixels(drawingSurface.getBitmap(), bmpCoords.x, bmpCoords.y, size.x, size.y);
		int[] black = new int[size.x * size.y];
		Arrays.fill(black, Color.BLACK);
		//		Utils.assertArrayEquals(pixels, black); 
	}

	/**
	 * Check if the floating box stamps the correct picture
	 * after loading two different pictures in a row
	 * 
	 */
	//	public void testDoubleImport() throws Exception {
	//		String filename1 = "import_png_test_1_save";
	//		File file1 = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/" + filename1 + ".png");
	//		assertFalse(file1.exists());
	//
	//		Utils.saveCurrentPicture(solo, filename1);
	//		assertTrue(file1.exists());
	//
	//		Utils.selectTool(solo, toolbarMainButton, R.string.button_magic);
	//		assertEquals(ToolType.MAGIC, drawingSurface.getToolType());
	//		solo.clickOnScreen(screenWidth / 2, screenWidth / 2);
	//
	//		String filename2 = "import_png_test_2_save";
	//		File file2 = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/" + filename2 + ".png");
	//
	//		Utils.saveCurrentPicture(solo, filename2);
	//		assertTrue(file2.exists());
	//
	//		Canvas bitmapCanvas = new Canvas(drawingSurface.getBitmap());
	//		Paint paint = new Paint();
	//		paint.setAlpha(0);
	//		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
	//		bitmapCanvas.drawPaint(paint);
	//
	//		Utils.selectTool(solo, toolbarMainButton, R.string.button_brush);
	//		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());
	//		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
	//
	//		drawingSurface.addPng(file1.getAbsolutePath());
	//		solo.sleep(400);
	//		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());
	//
	//		drawingSurface.addPng(file2.getAbsolutePath());
	//		solo.sleep(400);
	//		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());
	//
	//		Point boxSize = drawingSurface.getFloatingBoxSize();
	//		assertNotNull(boxSize);
	//		Point boxCoordinates = new Point(drawingSurface.getToolCoordinates());
	//		assertNotNull(boxCoordinates);
	//
	//		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
	//
	//		solo.sleep(500);
	//
	//		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
	//
	//		solo.sleep(500);
	//
	//		Utils.selectTool(solo, toolbarMainButton, R.string.button_brush);
	//		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());
	//
	//		assertEquals(Color.BLACK, drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x, boxCoordinates.y));
	//		assertEquals(Color.BLACK,
	//				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 - 5, boxCoordinates.y));
	//		assertEquals(Color.BLACK,
	//				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 + 5, boxCoordinates.y));
	//		assertEquals(Color.BLACK,
	//				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x, boxCoordinates.y + boxSize.y / 2 - 5));
	//		assertEquals(Color.BLACK,
	//				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x, boxCoordinates.y - boxSize.y / 2 + 5));
	//		assertEquals(
	//				Color.BLACK,
	//				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 - 5, boxCoordinates.y
	//						+ boxSize.y / 2 - 5));
	//		assertEquals(
	//				Color.BLACK,
	//				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 + 5, boxCoordinates.y
	//						- boxSize.y / 2 + 5));
	//		assertEquals(
	//				Color.BLACK,
	//				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 + 5, boxCoordinates.y
	//						+ boxSize.y / 2 - 5));
	//		assertEquals(
	//				Color.BLACK,
	//				drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 - 5, boxCoordinates.y
	//						- boxSize.y / 2 + 5));
	//
	//		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 + 5,
	//				boxCoordinates.y));
	//		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 - 5,
	//				boxCoordinates.y));
	//		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x, boxCoordinates.y
	//				+ boxSize.y / 2 + 10));
	//		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x, boxCoordinates.y
	//				- boxSize.y / 2 - 10));
	//		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 + 5,
	//				boxCoordinates.y + boxSize.y / 2 + 10));
	//		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 - 5,
	//				boxCoordinates.y - boxSize.y / 2 - 10));
	//		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x - boxSize.x / 2 - 5,
	//				boxCoordinates.y + boxSize.y / 2 + 10));
	//		assertTrue(Color.BLACK != drawingSurface.getPixelFromScreenCoordinates(boxCoordinates.x + boxSize.x / 2 + 5,
	//				boxCoordinates.y - boxSize.y / 2 - 10));
	//
	//		file1.delete();
	//		file2.delete();
	//	}
}
