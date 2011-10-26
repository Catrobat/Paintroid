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

import java.util.Locale;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.deprecated.graphic.DrawingSurface;

import com.jayway.android.robotium.solo.Solo;

public class UndoRedoTests extends ActivityInstrumentationTestCase2<MainActivity> {
	private Solo solo;
	private MainActivity mainActivity;
	private DrawingSurface drawingSurface;

	private String preTab;

	private TextView parameterButton1;
	private TextView parameterButton2;
	private TextView toolButton;
	private Button undoButton;

	final int STROKERECT = 0;
	final int STROKECIRLCE = 1;
	final int STROKE1 = 2;
	final int STROKE2 = 3;
	final int STROKE3 = 4;
	final int STROKE4 = 5;

	public UndoRedoTests() {
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
		mainActivity.getBaseContext().getResources().updateConfiguration(config_before,
				mainActivity.getBaseContext().getResources().getDisplayMetrics());
		drawingSurface = (DrawingSurface) mainActivity.findViewById(R.id.surfaceview);
		preTab = mainActivity.getResources().getString(R.string.color_pre);

		parameterButton1 = (TextView) mainActivity.findViewById(R.id.btn_Parameter1);
		parameterButton2 = (TextView) mainActivity.findViewById(R.id.btn_Parameter2);
		toolButton = (TextView) mainActivity.findViewById(R.id.btn_Tool);
		undoButton = (Button) mainActivity.findViewById(R.id.btn_Undo);
	}

	public void testUndoPath() throws Exception {
		mainActivity = (MainActivity) solo.getCurrentActivity();

		Bitmap initialBitmap = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
		solo.drag(screenWidth / 2 - 100, screenWidth / 2 + 100, screenHeight / 2 - 100, screenHeight / 2 + 100, 20);
		Bitmap testBitmap = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap2 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		//Check if undo worked
		assertBitmapIsEqual(initialBitmap, testBitmap2);

		//Check if something has been drawn on the picture
		bitmapIsNotEqual(initialBitmap, testBitmap);

	}

	public void testUndoPoint() throws Exception {
		Utils.selectColorFromPicker(solo, new int[] { 255, 0, 255, 0 }, parameterButton1);

		Bitmap initialBitmap = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		int screenWidth = mainActivity.getWindowManager().getDefaultDisplay().getWidth();
		solo.clickOnScreen(screenWidth / 2, screenWidth / 2);
		solo.sleep(500);
		Bitmap testBitmap = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap2 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		//Check if undo worked
		assertBitmapIsEqual(initialBitmap, testBitmap2);

		//Check if something has been drawn on the picture
		bitmapIsNotEqual(initialBitmap, testBitmap);

	}

	public void testUndoMagicWand() throws Exception {
		Bitmap initialBitmap = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		int screenWidth = mainActivity.getWindowManager().getDefaultDisplay().getWidth();

		Utils.selectTool(solo, toolButton, R.string.button_magic);
		solo.clickOnScreen(screenWidth / 2, screenWidth / 2);
		Bitmap testBitmap = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap2 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		//Check if undo worked
		assertBitmapIsEqual(initialBitmap, testBitmap2);

		//Check if something has been drawn on the picture
		bitmapIsNotEqual(initialBitmap, testBitmap);

	}

	public void testRedo() throws Exception {
		Bitmap initialBitmap = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		int screenWidth = mainActivity.getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = mainActivity.getWindowManager().getDefaultDisplay().getHeight();

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.sleep(500);
		Bitmap testBitmap1 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap2 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		Utils.selectTool(solo, toolButton, R.string.button_redo);
		solo.sleep(500);
		Bitmap testBitmap3 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		//Check if undo worked
		assertBitmapIsEqual(initialBitmap, testBitmap2);

		//Check if redo worked
		assertBitmapIsEqual(testBitmap1, testBitmap3);

		//Check if something has been drawn on the picture
		bitmapIsNotEqual(initialBitmap, testBitmap1);

	}

	public void testUndoRedoPathPointAndWand() throws Exception {
		Bitmap initialBitmap = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		int screenWidth = mainActivity.getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = mainActivity.getWindowManager().getDefaultDisplay().getHeight();

		solo.drag(screenWidth / 2 - 100, screenWidth / 2 + 100, screenHeight / 2 - 100, screenHeight / 2 + 100, 20);
		solo.sleep(500);
		Bitmap testBitmap1 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnScreen(screenWidth / 2, screenWidth / 2 + 100);
		solo.sleep(500);
		Bitmap testBitmap2 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_magic);
		solo.clickOnScreen(screenWidth / 2 + 100, screenWidth / 2);
		solo.sleep(500);
		Bitmap testBitmap3 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_brush);

		solo.drag(screenWidth / 2 - 100, screenWidth / 2 + 100, screenHeight / 2 - 100, screenHeight / 2 + 100, 20);
		solo.sleep(500);
		Bitmap testBitmap4 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap5 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap6 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap7 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap8 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap9 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		Utils.selectTool(solo, toolButton, R.string.button_redo);
		solo.sleep(500);
		Bitmap testBitmap10 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_redo);
		solo.sleep(500);
		Bitmap testBitmap11 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_redo);
		solo.sleep(500);
		Bitmap testBitmap12 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_redo);
		solo.sleep(500);
		Bitmap testBitmap13 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_redo);
		solo.sleep(500);
		Bitmap testBitmap14 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		// TODO
		//		assertBitmapIsEqual(testBitmap3, testBitmap5);
		//		assertBitmapIsEqual(testBitmap2, testBitmap6);
		//		assertBitmapIsEqual(testBitmap1, testBitmap7);
		//		assertBitmapIsEqual(initialBitmap, testBitmap8);
		//		assertBitmapIsEqual(initialBitmap, testBitmap9);
		//
		//		assertBitmapIsEqual(testBitmap1, testBitmap10);
		//		assertBitmapIsEqual(testBitmap2, testBitmap11);
		//		assertBitmapIsEqual(testBitmap3, testBitmap12);
		//		assertBitmapIsEqual(testBitmap4, testBitmap13);
		//		assertBitmapIsEqual(testBitmap4, testBitmap14);
		//
		//		assertBitmapIsEqual(initialBitmap, testBitmap1);
		//		assertBitmapIsEqual(testBitmap1, testBitmap2);
		//		assertBitmapIsEqual(testBitmap2, testBitmap3);
		//		assertBitmapIsEqual(testBitmap3, testBitmap4);
	}

	public void testNoRedoAfterDraw() throws Exception {
		Bitmap initialBitmap = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		int screenWidth = mainActivity.getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = mainActivity.getWindowManager().getDefaultDisplay().getHeight();

		solo.drag(screenWidth / 2 - 100, screenWidth / 2 + 100, screenHeight / 2 - 100, screenHeight / 2 + 100, 20);
		solo.sleep(500);
		Bitmap testBitmap1 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		solo.drag(screenWidth / 2 + 100, screenWidth / 2 - 100, screenHeight / 2 - 100, screenHeight / 2 + 100, 20);
		Bitmap testBitmap2 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap3 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap4 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_redo);
		solo.sleep(500);
		Bitmap testBitmap5 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnScreen(screenWidth / 2 + 200, screenWidth / 2 + 100);
		solo.sleep(500);
		Bitmap testBitmap6 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_redo);
		solo.sleep(500);
		Bitmap testBitmap7 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap8 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		// TODO
		//		assertBitmapIsEqual(testBitmap1, testBitmap3);
		//		assertBitmapIsEqual(initialBitmap, testBitmap4);
		//		assertBitmapIsEqual(testBitmap1, testBitmap5);
		//		assertBitmapIsEqual(testBitmap6, testBitmap7);
		//		assertBitmapIsEqual(testBitmap5, testBitmap8);
		//
		//		assertBitmapIsEqual(initialBitmap, testBitmap1);
		//		assertBitmapIsEqual(testBitmap1, testBitmap2);
		//		assertBitmapIsEqual(testBitmap5, testBitmap6);

	}

	public void testIfCacheFilesAreDeleted() throws Exception {
		int screenWidth = mainActivity.getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = mainActivity.getWindowManager().getDefaultDisplay().getHeight();

		Utils.selectTool(solo, toolButton, R.string.button_magic);
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		Utils.selectColorFromPicker(solo, new int[] { 255, 0, 255, 0 }, parameterButton1);

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		Utils.selectTool(solo, toolButton, R.string.button_redo);
		Utils.selectTool(solo, toolButton, R.string.button_redo);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		mainActivity.deleteUndoRedoCacheFiles();
		//			assertFalse(mainActivity.cacheFilesExist();
	}

	public void testIfUndoRedoWorksIfCacheFilesAreMissing() throws Exception {
		Bitmap initialBitmap = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		int screenWidth = mainActivity.getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = mainActivity.getWindowManager().getDefaultDisplay().getHeight();

		Utils.selectTool(solo, toolButton, R.string.button_magic);
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.sleep(500);
		Bitmap testBitmap1 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		Utils.selectColorFromPicker(solo, new int[] { 255, 0, 0, 255 }, parameterButton1);

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.sleep(500);
		Bitmap testBitmap2 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap3 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap4 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_redo);
		solo.sleep(500);
		Bitmap testBitmap5 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_redo);
		solo.sleep(500);
		Bitmap testBitmap6 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap7 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		mainActivity.deleteUndoRedoCacheFiles();
		//		assertFalse(mainActivity.cacheFilesExist();

		Utils.selectTool(solo, toolButton, R.string.button_redo);
		solo.sleep(500);
		Bitmap testBitmap8 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap9 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		assertBitmapIsEqual(testBitmap1, testBitmap3);
		assertBitmapIsEqual(initialBitmap, testBitmap4);
		assertBitmapIsEqual(testBitmap1, testBitmap5);
		assertBitmapIsEqual(testBitmap2, testBitmap6);
		assertBitmapIsEqual(testBitmap1, testBitmap7);

		assertBitmapIsEqual(testBitmap7, testBitmap8);
		assertBitmapIsEqual(testBitmap7, testBitmap9);

		//		bitmapIsEqual(initialBitmap, testBitmap1);
		//		bitmapIsEqual(testBitmap1, testBitmap2);
	}

	public void testIfDrawingOutsideBitmapAffectsUndo() throws Exception {
		int screenWidth = mainActivity.getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = mainActivity.getWindowManager().getDefaultDisplay().getHeight();

		Bitmap testBitmap0 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_brush);
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.sleep(500);
		Bitmap testBitmap1 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		Utils.selectTool(solo, toolButton, R.string.button_choose);
		solo.drag(0, 0, (screenHeight - 200), 100, 10);
		solo.drag(0, 0, (screenHeight - 200), 100, 10);
		Utils.selectTool(solo, toolButton, R.string.button_brush);
		solo.clickOnScreen(200, screenHeight / 2);
		solo.sleep(500);
		Bitmap testBitmap2 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		solo.drag(100, screenWidth - 100, screenHeight / 2, screenHeight - 200, 10);
		Bitmap testBitmap3 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_magic);
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		Bitmap testBitmap4 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);

		Utils.selectTool(solo, toolButton, R.string.button_choose);
		solo.drag(0, 0, 100, (screenHeight - 200), 10);
		solo.drag(0, 0, 100, (screenHeight - 200), 10);
		Utils.selectTool(solo, toolButton, R.string.button_brush);
		solo.clickOnScreen(screenWidth / 2 + 200, screenHeight / 2);
		solo.sleep(500);
		Bitmap testBitmap5 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap6 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap7 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap8 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap9 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		Utils.selectTool(solo, toolButton, R.string.button_undo);
		solo.sleep(500);
		Bitmap testBitmap10 = drawingSurface.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
		// TODO
		//		assertBitmapIsEqual(testBitmap1, testBitmap2);
		//		assertBitmapIsEqual(testBitmap2, testBitmap3);
		//		assertBitmapIsEqual(testBitmap3, testBitmap4);
		//		assertBitmapIsEqual(testBitmap4, testBitmap5);
		//		assertBitmapIsEqual(testBitmap1, testBitmap6);
		//		assertBitmapIsEqual(testBitmap0, testBitmap7);
		//		assertBitmapIsEqual(testBitmap0, testBitmap8);
		//		assertBitmapIsEqual(testBitmap0, testBitmap9);
		//		assertBitmapIsEqual(testBitmap0, testBitmap10);
	}

	private void assertBitmapIsEqual(Bitmap bitmap1, Bitmap bitmap2) {
		int[] a = Utils.bitmapToPixelArray(bitmap1);
		int[] b = Utils.bitmapToPixelArray(bitmap2);
		Utils.assertArrayEquals(a, b);
	}

	private void bitmapIsNotEqual(Bitmap bitmap1, Bitmap bitmap2) {
		int[] a = Utils.bitmapToPixelArray(bitmap1);
		int[] b = Utils.bitmapToPixelArray(bitmap2);
		Utils.assertArrayNotEquals(a, b);
	}
}
