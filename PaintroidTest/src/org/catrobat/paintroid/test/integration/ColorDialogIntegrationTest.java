/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.test.integration;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.ui.Toolbar;
import org.catrobat.paintroid.ui.implementation.DrawingSurfaceImplementation;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TableRow;

public class ColorDialogIntegrationTest extends BaseIntegrationTestClass {

	protected Toolbar mToolbar;
	private final int COLOR_PICKER_DIALOGUE_APPERANCE_DELAY = 10000;

	public ColorDialogIntegrationTest() throws Exception {
		super();
	}

	@Override
	protected void setUp() {
		super.setUp();
		try {
			mToolbar = (Toolbar) PrivateAccess.getMemberValue(MainActivity.class, getActivity(), "mToolbar");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testStandardTabSelected() throws Throwable {
		int expectedIndexTab = 0;

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mButtonParameterTop);
		mSolo.sleep(COLOR_PICKER_DIALOGUE_APPERANCE_DELAY);
		TabHost tabhost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
		assertEquals("After opening Color Picker Dialog, First tab should be the preselected-tab",
				tabhost.getCurrentTab(), expectedIndexTab);
		mSolo.goBack();
	}

	public void testTabsAreSelectable() throws Throwable {
		int indexTabHsv = 1;
		int indexTabRgb = 2;

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mButtonParameterTop);
		mSolo.sleep(COLOR_PICKER_DIALOGUE_APPERANCE_DELAY);

		TabHost tabhost = (TabHost) mSolo.getView(R.id.colorview_tabColors);

		// Substring to click on the text. Only 5 pattern because this length is
		// visible in the tab. Might need refactoring with other languages!
		String tabHsvName = mSolo.getString(R.string.color_hsv).substring(0, 5);
		String tabRgbName = mSolo.getString(R.string.color_rgb).substring(0, 5);

		mSolo.clickOnText(tabHsvName);
		assertEquals(tabhost.getCurrentTab(), indexTabHsv);
		mSolo.sleep(500);

		mSolo.clickOnText(tabRgbName);
		assertEquals(tabhost.getCurrentTab(), indexTabRgb);
		mSolo.sleep(500);
		mSolo.goBack();
	}

	public void testColorNewColorButtonChangesStandard() {
		int numberOfColorsToTest = 20;

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mButtonParameterTop);
		mSolo.sleep(COLOR_PICKER_DIALOGUE_APPERANCE_DELAY);

		TypedArray presetColors = getActivity().getResources().obtainTypedArray(R.array.preset_colors);

		if (numberOfColorsToTest > presetColors.length()) {
			numberOfColorsToTest = presetColors.length();
		}

		for (int counterColors = 0; counterColors < numberOfColorsToTest; counterColors++) {
			Log.d(PaintroidApplication.TAG, "test color # " + counterColors);
			Button colorButton = mSolo.getButton(counterColors);

			if (!(colorButton.getParent() instanceof TableRow)) {
				Log.d(PaintroidApplication.TAG, "button parent is no table row: " + colorButton.getParent());
				continue;
			}

			mSolo.clickOnButton(counterColors);
			mSolo.sleep(500);
			int colorColor = presetColors.getColor(counterColors, 0);

			String buttonNewColorName = getActivity().getResources().getString(R.string.ok);
			Button button = mSolo.getButton(buttonNewColorName);
			Drawable drawable = button.getBackground();
			int buttonTextColor = button.getCurrentTextColor();

			Bitmap bitmap = drawableToBitmap(drawable, button.getWidth(), button.getHeight());
			int buttonColor = bitmap.getPixel(1, 1);
			assertEquals("New Color button has unexpected color", colorColor, buttonColor);
			assertTrue("Button textcolor and backgroundcolor ar the same", buttonColor != buttonTextColor);
			assertTrue("Unexpected text color in butten text",
					(buttonTextColor == Color.BLACK || buttonTextColor == Color.WHITE));

		}

	}

	public void testColorPickerDialogOnBackPressed() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForText(mSolo.getString(R.string.ok), 1, TIMEOUT * 2));
		mSolo.goBack();
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		int oldColor = mToolbar.getCurrentTool().getDrawPaint().getColor();
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForText(mSolo.getString(R.string.ok), 1, TIMEOUT * 2));

		TypedArray presetColors = getActivity().getResources().obtainTypedArray(R.array.preset_colors);

		mSolo.clickOnButton(presetColors.length() / 2);
		mSolo.goBack();

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		int newColor = mToolbar.getCurrentTool().getDrawPaint().getColor();
		assertFalse("After choosing new color, color should not be the same as before", oldColor == newColor);
	}

	public void testColorPickerDialogOnBackgroundPressed() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForText(mSolo.getString(R.string.ok), 1, TIMEOUT * 2));
		mSolo.clickOnScreen(1, 100);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		int oldColor = mToolbar.getCurrentTool().getDrawPaint().getColor();
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForText(mSolo.getString(R.string.ok), 1, TIMEOUT * 2));

		TypedArray presetColors = getActivity().getResources().obtainTypedArray(R.array.preset_colors);

		mSolo.clickOnButton(presetColors.length() / 2);
		mSolo.clickOnScreen(1, 100);

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		int newColor = mToolbar.getCurrentTool().getDrawPaint().getColor();
		assertFalse("After choosing new color, color should not be the same as before", oldColor == newColor);
	}

	public static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		int intrinsicWidth = width;
		int intrinsicHeight = height;
		Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}
}
