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

import java.util.Arrays;
import java.util.Locale;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ImageButton;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;

import com.jayway.android.robotium.solo.Solo;

public class ButtonFunctionTests extends ActivityInstrumentationTestCase2<MainActivity> {
	static final String TAG = "PAINTROIDTEST";

	private Solo solo;
	private MainActivity mainActivity;
	private int[] toolbarButtonId;
	private int[] toolbarButtonNormalId;
	private int[] toolbarButtonActiveId;

	public ButtonFunctionTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();
		Locale defaultLocale = new Locale("en");
		Locale.setDefault(defaultLocale);
		Configuration config_before = new Configuration();
		config_before.locale = defaultLocale;
		mainActivity.getBaseContext().getResources()
				.updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());

		toolbarButtonId = new int[] { R.id.ibtn_handTool, R.id.ibtn_zoomTool, R.id.ibtn_brushTool,
				R.id.ibtn_eyeDropperTool, R.id.ibtn_magicWandTool };
		toolbarButtonNormalId = new int[] { R.drawable.ic_hand, R.drawable.ic_zoom, R.drawable.ic_brush,
				R.drawable.ic_eyedropper, R.drawable.ic_magicwand };
		toolbarButtonActiveId = new int[] { R.drawable.ic_hand_active, R.drawable.ic_zoom_active,
				R.drawable.ic_brush_active, R.drawable.ic_eyedropper_active, R.drawable.ic_magicwand_active };
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

	private static int[] drawableToPixelArray(Drawable drawable) {
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		int pixelArray[] = new int[bitmapWidth * bitmapHeight];
		bitmap.getPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
		return pixelArray;
	}

	/**
	 * Check if Buttons change their background when they have been clicked
	 * 
	 * @throws Exception
	 */
	@Smoke
	public void testInitialButtonBackgrounds() throws Exception {
		int initialSelectedButtonId = R.id.ibtn_brushTool;
		for (int i = 0; i < toolbarButtonId.length; i++) {
			ImageButton toolButton = (ImageButton) mainActivity.findViewById(toolbarButtonId[i]);
			assertNotNull(toolButton);
			Drawable toolIcon = mainActivity.getResources().getDrawable(toolbarButtonNormalId[i]);
			if (toolbarButtonId[i] == initialSelectedButtonId) {
				toolIcon = mainActivity.getResources().getDrawable(toolbarButtonActiveId[i]);
			}
			Drawable buttonBg = toolButton.getBackground();
			if (!(buttonBg instanceof BitmapDrawable)) {
				assertTrue(false);
			}
			assertTrue(Arrays.equals(drawableToPixelArray(toolIcon), drawableToPixelArray(buttonBg)));
		}
	}

	/**
	 * Check if Buttons change their background when they have been clicked
	 * 
	 * @throws Exception
	 */
	@Smoke
	public void testChangeButtonBackgrounds() throws Exception {
		for (int i = 0; i < toolbarButtonId.length; i++) {
			ImageButton toolButton = (ImageButton) mainActivity.findViewById(toolbarButtonId[i]);
			assertNotNull(toolButton);
			solo.clickOnView(toolButton);
			Drawable toolIcon = mainActivity.getResources().getDrawable(toolbarButtonActiveId[i]);
			Drawable buttonBg = toolButton.getBackground();
			if (!(buttonBg instanceof BitmapDrawable)) {
				assertTrue(false);
			}
			assertTrue(Arrays.equals(drawableToPixelArray(toolIcon), drawableToPixelArray(buttonBg)));

			int activeButtonId = toolbarButtonId[i];
			for (int j = 0; j < toolbarButtonId.length; j++) {
				ImageButton otherButton = (ImageButton) mainActivity.findViewById(toolbarButtonId[j]);
				assertNotNull(otherButton);
				toolIcon = mainActivity.getResources().getDrawable(toolbarButtonNormalId[j]);
				if (toolbarButtonId[i] == activeButtonId) {
					continue;
				}
				buttonBg = otherButton.getBackground();
				if (!(buttonBg instanceof BitmapDrawable)) {
					assertTrue(false);
				}
				assertTrue(Arrays.equals(drawableToPixelArray(toolIcon), drawableToPixelArray(buttonBg)));
			}
		}
	}

	/**
	 * Test stroke and shape picker
	 * 
	 * @throws Exception
	 */
	//	@Smoke
	//	public void testBrushShape() throws Exception {
	//		mainActivity = (MainActivity) solo.getCurrentActivity();
	//
	//		solo.clickOnImageButton(STROKE);
	//		solo.clickOnImageButton(STROKECIRLCE);
	//		solo.clickOnImageButton(STROKE);
	//		solo.clickOnImageButton(STROKE1);
	//		solo.waitForDialogToClose(100);
	//		Brush brush = mainActivity.getActiveBrush();
	//		assertEquals(1, brush.stroke);
	//		assertEquals(Cap.ROUND, brush.cap);
	//
	//		solo.clickOnImageButton(STROKE);
	//		solo.clickOnImageButton(STROKE3);
	//		solo.waitForDialogToClose(100);
	//		assertEquals(15, brush.stroke);
	//		assertEquals(Cap.ROUND, brush.cap);
	//
	//		solo.clickOnImageButton(STROKE);
	//		solo.clickOnImageButton(STROKERECT);
	//		solo.waitForDialogToClose(100);
	//		assertEquals(15, brush.stroke);
	//		assertEquals(Cap.SQUARE, brush.cap);
	//
	//		solo.clickOnImageButton(STROKE);
	//		solo.clickOnImageButton(STROKE3);
	//		solo.waitForDialogToClose(100);
	//		assertEquals(15, brush.stroke);
	//		assertEquals(Cap.SQUARE, brush.cap);
	//
	//		solo.clickOnImageButton(STROKE);
	//		solo.clickOnImageButton(STROKECIRLCE);
	//		solo.clickOnImageButton(STROKE);
	//		solo.clickOnImageButton(STROKE4);
	//		solo.waitForDialogToClose(100);
	//		assertEquals(25, brush.stroke);
	//		assertEquals(Cap.ROUND, brush.cap);
	//
	//	}

	/**
	 * Tests if there is a new Bitmap created
	 * 
	 * @throws Exception
	 */
	//	@Smoke
	//	public void testNewDrawing() throws Exception {
	//		solo.clickOnImageButton(FILE);
	//		solo.clickOnButton("New Drawing");
	//		mainActivity = (MainActivity) solo.getCurrentActivity();
	//		assertNotNull(mainActivity.getCurrentImage());
	//	}

	/**
	 * Tests if the Bitmap(DrawingSurface) is now cleared
	 * 
	 * @throws Exception
	 */
	//	@Smoke
	//	public void testClearDrawing() throws Exception {
	//		solo.clickOnImageButton(FILE);
	//		solo.clickOnButton("New Drawing");
	//		mainActivity = (MainActivity) solo.getCurrentActivity();
	//		solo.clickOnMenuItem("Clear Drawing");
	//		if (mainActivity.getCurrentImage() != null) {
	//			assertNull(mainActivity.getCurrentImage().getNinePatchChunk());
	//		}
	//	}

	/**
	 * Tests if reset of ZoomValue works
	 * 
	 * @throws Exception
	 */
	//	@Smoke
	//	public void testResetZoom() throws Exception {
	//		solo.clickOnImageButton(FILE);
	//		solo.clickOnButton("New Drawing");
	//		solo.clickOnImageButton(ZOOM);
	//		mainActivity = (MainActivity) solo.getCurrentActivity();
	//		solo.drag(66, 500, 700, 55, 100);
	//		assertFalse(mainActivity.getZoomLevel().equals(String.valueOf(1.0)));
	//		solo.clickOnMenuItem("Reset Zoom");
	//		mainActivity = (MainActivity) solo.getCurrentActivity();
	//		assertEquals(mainActivity.getZoomLevel(), String.valueOf(1.0));
	//	}

	/**
	 * Tests if the drag function works
	 * 
	 * @throws Exception
	 */
	//	@Smoke
	//	public void testScroll() throws Exception {
	//		solo.clickOnImageButton(FILE);
	//		solo.clickOnButton("New Drawing");
	//		solo.clickOnImageButton(HAND);
	//		mainActivity = (MainActivity) solo.getCurrentActivity();
	//		float scrollX = mainActivity.getScrollX();
	//		float scrollY = mainActivity.getScrollY();
	//		solo.drag(66, 500, 700, 55, 100);
	//		assertTrue(scrollX != mainActivity.getScrollX());
	//		assertTrue(scrollY != mainActivity.getScrollY());
	//	}

	/**
	 * Tests if Zooming works
	 * 
	 * @throws Exception
	 */
	//	@Smoke
	//	public void testZoom() throws Exception {
	//		solo.clickOnImageButton(FILE);
	//		solo.clickOnButton("New Drawing");
	//		solo.clickOnImageButton(ZOOM);
	//		mainActivity = (MainActivity) solo.getCurrentActivity();
	//		solo.drag(66, 500, 700, 55, 100);
	//		assertFalse(mainActivity.getZoomLevel().equals(String.valueOf(1.0)));
	//	}

	/**
	 * Tests if the about dialog is present
	 * 
	 * @throws Exception
	 */
	//	@Smoke
	//	public void testAbout() throws Exception {
	//		solo.clickOnMenuItem("More");
	//		solo.clickInList(2);
	//		//		solo.clickOnMenuItem("About");
	//		//		assertTrue(solo.waitForText(aboutTitleText, 1, 300));
	//		solo.clickOnButton("Cancel");
	//		//		assertFalse(solo.waitForText(aboutTitleText, 1, 300));
	//
	//	}

	/**
	 * Tests if the license dialog is present
	 * 
	 * @throws Exception
	 */
	//	@Smoke
	//	public void testGpl() throws Exception {
	//		solo.clickOnMenuItem("More");
	//		solo.clickInList(2);
	//		//		solo.clickOnMenuItem("About");
	//		//		assertTrue(solo.waitForText(aboutTitleText, 1, 300));
	//		solo.clickOnButton("License");
	//		//		assertEquals(licenseText, solo.getText(LICENSETEXT).getText());
	//		solo.clickOnButton("Ok");
	//		solo.clickOnButton("Cancel");
	//		//		assertFalse(solo.waitForText(aboutTitleText, 1, 300));
	//	}
}
