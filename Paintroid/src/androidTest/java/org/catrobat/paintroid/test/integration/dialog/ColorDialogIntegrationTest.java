/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.integration.dialog;

import java.util.ArrayList;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.TopBar;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TableRow;
import android.widget.TextView;

public class ColorDialogIntegrationTest extends BaseIntegrationTestClass {

	protected TopBar mTopBar;
	private final int COLOR_PICKER_DIALOGUE_APPERANCE_DELAY = 1000;

	public ColorDialogIntegrationTest() throws Exception {
		super();
	}

	@Override
	protected void setUp() {
		super.setUp();
		try {
			mTopBar = (TopBar) PrivateAccess.getMemberValue(MainActivity.class, getActivity(), "mTopBar");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void testStandardTabSelected() throws Throwable {
		int expectedIndexTab = 0;

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		mSolo.clickOnView(mButtonTopColor);
		mSolo.sleep(COLOR_PICKER_DIALOGUE_APPERANCE_DELAY);
		TabHost tabhost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
		assertEquals("After opening Color Picker Dialog, First tab should be the preselected-tab",
				tabhost.getCurrentTab(), expectedIndexTab);
		mSolo.goBack();
	}

	@SuppressLint("NewApi")
	public void testTabsAreSelectable() throws Throwable {
		String[] colorChooserTags = { mSolo.getString(R.string.color_pre), mSolo.getString(R.string.color_rgb) };

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		mSolo.clickOnView(mButtonTopColor);
		mSolo.sleep(COLOR_PICKER_DIALOGUE_APPERANCE_DELAY);

		TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
		TabWidget colorTabWidget = tabHost.getTabWidget();

		assertEquals("Wrong tab count ", colorTabWidget.getTabCount(), colorChooserTags.length);
		for (int tabChildIndex = 0; tabChildIndex < colorTabWidget.getChildCount(); tabChildIndex++) {
			mSolo.clickOnView(colorTabWidget.getChildAt(tabChildIndex), true);
			mSolo.sleep(500);
			if (colorChooserTags[tabChildIndex].equalsIgnoreCase(mSolo.getString(R.string.color_pre)))
				assertFalse("In preselection tab and rgb (red) string found",
						mSolo.searchText(mSolo.getString(R.string.color_red)));
			else if (colorChooserTags[tabChildIndex].equalsIgnoreCase(mSolo.getString(R.string.color_rgb))) {
				assertTrue("In rgb tab and red string not found", mSolo.searchText(mSolo.getString(R.string.color_red)));
				assertTrue("In rgb tab and green string not found",
						mSolo.searchText(mSolo.getString(R.string.color_green)));
				assertTrue("In rgb tab and blue string not found",
						mSolo.searchText(mSolo.getString(R.string.color_blue)));
			}
		}
		mSolo.goBack();
	}

	public void testColorNewColorButtonChangesStandard() {
		int numberOfColorsToTest = 20;

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		mSolo.clickOnView(mButtonTopColor);
		mSolo.sleep(COLOR_PICKER_DIALOGUE_APPERANCE_DELAY);

		TypedArray presetColors = getActivity().getResources().obtainTypedArray(R.array.preset_colors);

		numberOfColorsToTest = Math.min(numberOfColorsToTest, presetColors.length());

		for (int counterColors = 0; counterColors < numberOfColorsToTest; counterColors++) {
			Log.d(PaintroidApplication.TAG, "test color # " + counterColors);
			Button colorButton = mSolo.getButton(counterColors);

			if (!(colorButton.getParent() instanceof TableRow)) {
				Log.d(PaintroidApplication.TAG, "button parent is no table row: " + colorButton.getParent());
				continue;
			}

			mSolo.clickOnButton(counterColors);
			mSolo.sleep(200);
			int colorColor = presetColors.getColor(counterColors, 0);

			String buttonNewColorName = getActivity().getResources().getString(R.string.done);
			Button button = mSolo.getButton(buttonNewColorName);
			Drawable drawable = button.getBackground();
			int buttonTextColor = button.getCurrentTextColor();

			Bitmap bitmap = drawableToBitmap(drawable, button.getWidth(), button.getHeight());
			int buttonColor = bitmap.getPixel(1, 1);
			assertEquals("New Color button has unexpected color", colorColor, buttonColor);
			assertTrue("Button textcolor and backgroundcolor ar the same", buttonColor != buttonTextColor);
			assertTrue("Unexpected text color in butten text",
					(buttonTextColor == Color.BLACK || buttonTextColor == Color.WHITE));
			assertTrue("Color not set yet", colorColor == mTopBar.getCurrentTool().getDrawPaint().getColor());
			bitmap.recycle();
			bitmap = null;
		}

	}

	public void testColorPickerDialogOnBackPressed() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForText(mSolo.getString(R.string.done), 1, TIMEOUT * 2));
		mSolo.goBack();
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		int oldColor = mTopBar.getCurrentTool().getDrawPaint().getColor();
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForText(mSolo.getString(R.string.done), 1, TIMEOUT * 2));

		TypedArray presetColors = getActivity().getResources().obtainTypedArray(R.array.preset_colors);

		mSolo.clickOnButton(presetColors.length() / 2);
		mSolo.goBack();

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		int newColor = mTopBar.getCurrentTool().getDrawPaint().getColor();
		assertFalse("After choosing new color, color should not be the same as before", oldColor == newColor);
	}

	public void testIfRGBSeekBarsDoChangeColor() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		final int RGB_TAB_INDEX = 1;
		testOpenColorPickerOnClickOnColorButton();
		TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
		TabWidget colorTabWidget = tabHost.getTabWidget();
		mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
		mSolo.waitForText(mSolo.getString(R.string.color_red));
		final Paint originalStrokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class,
				PaintroidApplication.currentTool, "mCanvasPaint");
		final int originalPaintColor = originalStrokePaint.getColor();
		final ArrayList<ProgressBar> currentProgressBars = mSolo.getCurrentViews(ProgressBar.class);
		assertEquals("No progress bars for ARGB :-(", currentProgressBars.size(), 4);
		final ArrayList<TextView> currentTextViews = mSolo.getCurrentViews(TextView.class,
				mSolo.getView(R.id.rgb_base_layout));
		assertEquals("Missing some text views RGBA and ARGV-values", 9, currentTextViews.size());
		int textValueCounter = 1;
		for (; textValueCounter < currentTextViews.size(); textValueCounter += 2) {
			int textValueAsInteger = Integer.parseInt((String) currentTextViews.get(textValueCounter).getText());
			assertTrue("Not in range 0<=textValue<=255", textValueAsInteger >= 0 && textValueAsInteger <= 255);
		}

		textValueCounter = 1;
		for (ProgressBar barToChange : currentProgressBars) {
			int changeSeekBarTo = (barToChange.getProgress() + 33) % barToChange.getMax();
			mSolo.setProgressBar(barToChange, changeSeekBarTo);
			mSolo.sleep(50);
			if (textValueCounter == 4) { // alpha 0-100%
				int expectetAlphaTextValue = (int) (changeSeekBarTo / 2.55f);
				assertEquals("Text value did not change index:" + textValueCounter, expectetAlphaTextValue,
						Integer.parseInt((String) currentTextViews.get(textValueCounter * 2 - 1).getText()));
			} else
				assertEquals("Text value did not change index:" + textValueCounter, changeSeekBarTo,
						Integer.parseInt((String) currentTextViews.get(textValueCounter * 2 - 1).getText()));
			textValueCounter++;
		}
		mSolo.goBack();
		final Paint rgbChangedStrokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class,
				PaintroidApplication.currentTool, "mCanvasPaint");
		final int rgbChangedPaintColor = rgbChangedStrokePaint.getColor();
		assertFalse("Alpha value did not change", Color.alpha(rgbChangedPaintColor) == Color.alpha(originalPaintColor));
		assertFalse("Red value did not change", Color.red(rgbChangedPaintColor) == Color.red(originalPaintColor));
		assertFalse("Green value did not change", Color.green(rgbChangedPaintColor) == Color.green(originalPaintColor));
		assertFalse("Blue value did not change", Color.blue(rgbChangedPaintColor) == Color.blue(originalPaintColor));

	}

	public void testOpenColorPickerOnClickOnColorButton() {
		mSolo.clickOnView(mButtonTopColor);
		View tabhost = mSolo.getView(R.id.colorview_tabColors);
		assertTrue("ColorChooser TabHost not opening",
				mSolo.waitForView(tabhost, COLOR_PICKER_DIALOGUE_APPERANCE_DELAY, false));
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
