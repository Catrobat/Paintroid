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

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Locale;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.dialog.DialogColorPicker;

import com.jayway.android.robotium.solo.Solo;

public class UndoRedoTests extends ActivityInstrumentationTestCase2<MainActivity> {
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

	public UndoRedoTests() {
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

	public void testUndoPath() throws Exception {
		mainActivity = (MainActivity) solo.getCurrentActivity();

		Bitmap initialBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
		solo.drag(screenWidth / 2 - 100, screenWidth / 2 + 100, screenHeight / 2 - 100, screenHeight / 2 + 100, 20);
		Bitmap testBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap2 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		//Check if undo worked
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap2));

		//Check if something has been drawn on the picture
		assertFalse(bitmapIsEqual(initialBitmap, testBitmap));

	}

	public void testUndoPoint() throws Exception {
		mainActivity = (MainActivity) solo.getCurrentActivity();

		//Choosing color red
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		ArrayList<View> actual_views = solo.getViews();
		View colorPickerView = null;
		for (View view : actual_views) {
			if (view instanceof DialogColorPicker.ColorPickerView) {
				colorPickerView = view;
			}
		}
		assertNotNull(colorPickerView);
		int[] colorPickerViewCoordinates = new int[2];
		colorPickerView.getLocationOnScreen(colorPickerViewCoordinates);
		solo.clickOnScreen(colorPickerViewCoordinates[0] + 10, colorPickerViewCoordinates[1] + 18);
		Thread.sleep(500);
		solo.clickOnScreen(colorPickerViewCoordinates[0] + 265, colorPickerViewCoordinates[1] + 50);
		solo.clickOnScreen(colorPickerViewCoordinates[0] + 20, colorPickerViewCoordinates[1] + 340);
		assertEquals(String.valueOf(Color.RED), mainActivity.getCurrentSelectedColor());

		Bitmap initialBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		solo.clickOnScreen(screenWidth / 2, screenWidth / 2);
		Thread.sleep(500);
		Bitmap testBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap2 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		//Check if undo worked
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap2));

		//Check if something has been drawn on the picture
		assertFalse(bitmapIsEqual(initialBitmap, testBitmap));

	}

	public void testUndoMagicWand() throws Exception {
		mainActivity = (MainActivity) solo.getCurrentActivity();

		Bitmap initialBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		solo.clickOnImageButton(WAND);
		solo.clickOnScreen(screenWidth / 2, screenWidth / 2);
		Bitmap testBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap2 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		//Check if undo worked
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap2));

		//Check if something has been drawn on the picture
		assertFalse(bitmapIsEqual(initialBitmap, testBitmap));

	}

	public void testRedo() throws Exception {
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.setAntiAliasing(false);
		Bitmap initialBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
		solo.drag(screenWidth / 2 - 100, screenWidth / 2 + 100, screenHeight / 2 - 100, screenHeight / 2 + 100, 20);
		Thread.sleep(500);
		Bitmap testBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap2 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap3 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		//Check if undo worked
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap2));

		//Check if redo worked
		assertTrue(bitmapIsEqual(testBitmap, testBitmap3));

		//Check if something has been drawn on the picture
		assertFalse(bitmapIsEqual(initialBitmap, testBitmap));

	}

	public void testUndoRedoPathPointAndWand() throws Exception {
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.setAntiAliasing(false);
		Bitmap initialBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
		solo.drag(screenWidth / 2 - 100, screenWidth / 2 + 100, screenHeight / 2 - 100, screenHeight / 2 + 100, 20);
		Thread.sleep(500);
		Bitmap testBitmap1 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnScreen(screenWidth / 2, screenWidth / 2 + 100);
		Thread.sleep(500);
		Bitmap testBitmap2 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(WAND);
		solo.clickOnScreen(screenWidth / 2 + 100, screenWidth / 2);
		Thread.sleep(500);
		Bitmap testBitmap3 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(BRUSH);

		//Choosing color red
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		ArrayList<View> actual_views = solo.getViews();
		View colorPickerView = null;
		for (View view : actual_views) {
			if (view instanceof DialogColorPicker.ColorPickerView) {
				colorPickerView = view;
			}
		}
		assertNotNull(colorPickerView);
		int[] colorPickerViewCoordinates = new int[2];
		colorPickerView.getLocationOnScreen(colorPickerViewCoordinates);
		solo.clickOnScreen(colorPickerViewCoordinates[0] + 10, colorPickerViewCoordinates[1] + 18);
		Thread.sleep(500);
		solo.clickOnScreen(colorPickerViewCoordinates[0] + 265, colorPickerViewCoordinates[1] + 50);
		solo.clickOnScreen(colorPickerViewCoordinates[0] + 20, colorPickerViewCoordinates[1] + 340);
		assertEquals(String.valueOf(Color.RED), mainActivity.getCurrentSelectedColor());
		Thread.sleep(500);
		solo.drag(screenWidth / 2 - 100, screenWidth / 2 + 100, screenHeight / 2 - 100, screenHeight / 2 + 100, 20);
		Thread.sleep(500);
		Bitmap testBitmap4 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap5 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap6 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap7 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap8 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap9 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap10 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap11 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap12 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap13 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap14 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		assertTrue(bitmapIsEqual(testBitmap3, testBitmap5));
		assertTrue(bitmapIsEqual(testBitmap2, testBitmap6));
		assertTrue(bitmapIsEqual(testBitmap1, testBitmap7));
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap8));
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap9));

		assertTrue(bitmapIsEqual(testBitmap1, testBitmap10));
		assertTrue(bitmapIsEqual(testBitmap2, testBitmap11));
		assertTrue(bitmapIsEqual(testBitmap3, testBitmap12));
		assertTrue(bitmapIsEqual(testBitmap4, testBitmap13));
		assertTrue(bitmapIsEqual(testBitmap4, testBitmap14));

		assertFalse(bitmapIsEqual(initialBitmap, testBitmap1));
		assertFalse(bitmapIsEqual(testBitmap1, testBitmap2));
		assertFalse(bitmapIsEqual(testBitmap2, testBitmap3));
		assertFalse(bitmapIsEqual(testBitmap3, testBitmap4));

	}

	public void testNoRedoAfterDraw() throws Exception {
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.setAntiAliasing(false);
		Bitmap initialBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
		solo.drag(screenWidth / 2 - 100, screenWidth / 2 + 100, screenHeight / 2 - 100, screenHeight / 2 + 100, 20);
		Thread.sleep(500);
		Bitmap testBitmap1 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		solo.drag(screenWidth / 2 + 100, screenWidth / 2 - 100, screenHeight / 2 - 100, screenHeight / 2 + 100, 20);
		Bitmap testBitmap2 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap3 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap4 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap5 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnScreen(screenWidth / 2 + 200, screenWidth / 2 + 100);
		Thread.sleep(500);
		Bitmap testBitmap6 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap7 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap8 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		assertTrue(bitmapIsEqual(testBitmap1, testBitmap3));
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap4));
		assertTrue(bitmapIsEqual(testBitmap1, testBitmap5));
		assertTrue(bitmapIsEqual(testBitmap6, testBitmap7));
		assertTrue(bitmapIsEqual(testBitmap5, testBitmap8));

		assertFalse(bitmapIsEqual(initialBitmap, testBitmap1));
		assertFalse(bitmapIsEqual(testBitmap1, testBitmap2));
		assertFalse(bitmapIsEqual(testBitmap5, testBitmap6));

	}

	public void testIfCacheFilesAreDeleted() throws Exception {
		mainActivity = (MainActivity) solo.getCurrentActivity();
		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
		solo.clickOnImageButton(WAND);
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		//Choosing color red
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		ArrayList<View> actual_views = solo.getViews();
		View colorPickerView = null;
		for (View view : actual_views) {
			if (view instanceof DialogColorPicker.ColorPickerView) {
				colorPickerView = view;
			}
		}
		assertNotNull(colorPickerView);
		int[] colorPickerViewCoordinates = new int[2];
		colorPickerView.getLocationOnScreen(colorPickerViewCoordinates);
		solo.clickOnScreen(colorPickerViewCoordinates[0] + 10, colorPickerViewCoordinates[1] + 18);
		Thread.sleep(500);
		solo.clickOnScreen(colorPickerViewCoordinates[0] + 265, colorPickerViewCoordinates[1] + 50);
		solo.clickOnScreen(colorPickerViewCoordinates[0] + 20, colorPickerViewCoordinates[1] + 340);
		assertEquals(String.valueOf(Color.RED), mainActivity.getCurrentSelectedColor());

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.clickOnImageButton(UNDO);
		solo.clickOnImageButton(UNDO);
		solo.clickOnImageButton(REDO);
		solo.clickOnImageButton(REDO);
		solo.clickOnImageButton(UNDO);
		mainActivity.deleteCacheFiles();
		assertFalse(mainActivity.cacheFilesExist());
	}

	public void testIfUndoRedoWorksIfCacheFilesAreMissing() throws Exception {
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.setAntiAliasing(false);
		Bitmap initialBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
		solo.clickOnImageButton(WAND);
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		Thread.sleep(500);
		Bitmap testBitmap1 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		//Choosing color red
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		ArrayList<View> actual_views = solo.getViews();
		View colorPickerView = null;
		for (View view : actual_views) {
			if (view instanceof DialogColorPicker.ColorPickerView) {
				colorPickerView = view;
			}
		}
		assertNotNull(colorPickerView);
		int[] colorPickerViewCoordinates = new int[2];
		colorPickerView.getLocationOnScreen(colorPickerViewCoordinates);
		solo.clickOnScreen(colorPickerViewCoordinates[0] + 10, colorPickerViewCoordinates[1] + 18);
		Thread.sleep(500);
		solo.clickOnScreen(colorPickerViewCoordinates[0] + 265, colorPickerViewCoordinates[1] + 50);
		solo.clickOnScreen(colorPickerViewCoordinates[0] + 20, colorPickerViewCoordinates[1] + 340);
		assertEquals(String.valueOf(Color.RED), mainActivity.getCurrentSelectedColor());

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		Thread.sleep(500);
		Bitmap testBitmap2 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap3 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap4 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap5 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap6 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap7 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		mainActivity.deleteCacheFiles();
		assertFalse(mainActivity.cacheFilesExist());

		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap8 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap9 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		assertTrue(bitmapIsEqual(testBitmap1, testBitmap3));
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap4));
		assertTrue(bitmapIsEqual(testBitmap1, testBitmap5));
		assertTrue(bitmapIsEqual(testBitmap2, testBitmap6));
		assertTrue(bitmapIsEqual(testBitmap1, testBitmap7));

		assertTrue(bitmapIsEqual(testBitmap7, testBitmap8));
		assertTrue(bitmapIsEqual(testBitmap7, testBitmap9));

		assertFalse(bitmapIsEqual(initialBitmap, testBitmap1));
		assertFalse(bitmapIsEqual(testBitmap1, testBitmap2));

	}

	public void testIfDrawingOutsideBitmapAffectsUndo() throws Exception {
		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
		Bitmap testBitmap0 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(BRUSH);
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		Thread.sleep(500);
		Bitmap testBitmap1 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		solo.clickOnImageButton(HAND);
		solo.drag(0, 0, (float) (screenHeight - 200), 100, 10);
		solo.drag(0, 0, (float) (screenHeight - 200), 100, 10);
		solo.clickOnImageButton(BRUSH);
		solo.clickOnScreen(200, screenHeight / 2);
		Thread.sleep(500);
		Bitmap testBitmap2 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.drag(100, screenWidth - 100, screenHeight / 2, screenHeight - 200, 10);
		Bitmap testBitmap3 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(WAND);
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		Bitmap testBitmap4 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);

		solo.clickOnImageButton(HAND);
		solo.drag(0, 0, 100, (float) (screenHeight - 200), 10);
		solo.drag(0, 0, 100, (float) (screenHeight - 200), 10);
		solo.clickOnImageButton(BRUSH);
		solo.clickOnScreen(screenWidth / 2 + 200, screenHeight / 2);
		Thread.sleep(500);
		Bitmap testBitmap5 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap6 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap7 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap8 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap9 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap10 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		assertTrue(bitmapIsEqual(testBitmap1, testBitmap2));
		assertTrue(bitmapIsEqual(testBitmap2, testBitmap3));
		assertTrue(bitmapIsEqual(testBitmap3, testBitmap4));
		assertFalse(bitmapIsEqual(testBitmap4, testBitmap5));
		assertTrue(bitmapIsEqual(testBitmap1, testBitmap6));
		assertTrue(bitmapIsEqual(testBitmap0, testBitmap7));
		assertTrue(bitmapIsEqual(testBitmap0, testBitmap8));
		assertTrue(bitmapIsEqual(testBitmap0, testBitmap9));
		assertTrue(bitmapIsEqual(testBitmap0, testBitmap10));

	}

	private boolean bitmapIsEqual(Bitmap bitmap1, Bitmap bitmap2) {
		if (bitmap1.getWidth() != bitmap2.getWidth() || bitmap1.getHeight() != bitmap2.getHeight()) {
			return false;
		}
		IntBuffer pixelBuffer1 = IntBuffer.allocate(bitmap1.getWidth() * bitmap1.getHeight());
		bitmap1.copyPixelsToBuffer(pixelBuffer1);
		IntBuffer pixelBuffer2 = IntBuffer.allocate(bitmap1.getWidth() * bitmap1.getHeight());
		bitmap2.copyPixelsToBuffer(pixelBuffer2);
		int[] pixelArray1 = pixelBuffer1.array();
		int[] pixelArray2 = pixelBuffer2.array();
		for (int x = 0; x < bitmap1.getWidth(); x++) {
			for (int y = 0; y < bitmap1.getHeight(); y++) {
				if (pixelArray1[x + y * bitmap1.getWidth()] != pixelArray2[x + y * bitmap1.getWidth()]) {
					return false;
				}
			}
		}
		return true;
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
