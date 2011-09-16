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
import java.util.Iterator;
import java.util.Locale;

import android.content.res.Configuration;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.view.View;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerView;
import at.tugraz.ist.paintroid.dialog.colorpicker.HsvAlphaSelectorView;
import at.tugraz.ist.paintroid.dialog.colorpicker.HsvHueSelectorView;
import at.tugraz.ist.paintroid.dialog.colorpicker.HsvSaturationSelectorView;
import at.tugraz.ist.paintroid.dialog.colorpicker.HsvSelectorView;
import at.tugraz.ist.paintroid.dialog.colorpicker.PresetSelectorView;
import at.tugraz.ist.paintroid.dialog.colorpicker.RgbSelectorView;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;

import com.jayway.android.robotium.solo.Solo;

public class ColorPickerTests extends ActivityInstrumentationTestCase2<MainActivity> {
	private Solo solo;
	private MainActivity mainActivity;
	private DrawingSurface drawingSurface;

	private TextView toolbarMainButton;
	private TextView toolbarButton1;
	private TextView toolbarButton2;
	private String oldColorButton;
	private String newColorButton;
	private String hsvTab;
	private String rgbTab;
	private String preTab;

	public ColorPickerTests() {
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

		drawingSurface = (DrawingSurface) mainActivity.findViewById(R.id.surfaceview);
		toolbarMainButton = (TextView) mainActivity.findViewById(R.id.btn_Tool);
		toolbarButton1 = (TextView) mainActivity.findViewById(R.id.btn_Parameter1);
		toolbarButton2 = (TextView) mainActivity.findViewById(R.id.btn_Parameter2);
		oldColorButton = mainActivity.getResources().getString(R.string.color_old_color);
		newColorButton = mainActivity.getResources().getString(R.string.color_new_color);
		hsvTab = mainActivity.getResources().getString(R.string.color_hsv);
		rgbTab = mainActivity.getResources().getString(R.string.color_rgb);
		preTab = mainActivity.getResources().getString(R.string.color_pre);
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

	@Smoke
	public void testColorPickerHasAllViews() throws Exception {
		solo.clickOnView(toolbarButton1);
		solo.waitForView(ColorPickerView.class, 1, 200);
		ArrayList<View> views = solo.getViews();
		View theView = null;
		for (View view : views) {
			if (view instanceof ColorPickerView)
				theView = view;
		}
		assertNotNull(theView);

		solo.clickOnText(preTab);
		views = solo.getViews();
		theView = null;
		for (View view : views) {
			if (view instanceof PresetSelectorView)
				theView = view;
		}
		assertNotNull(theView);

		solo.clickOnText(rgbTab);
		views = solo.getViews();
		theView = null;
		for (View view : views) {
			if (view instanceof RgbSelectorView)
				theView = view;
		}
		assertNotNull(theView);

		solo.clickOnText(hsvTab);
		views = solo.getViews();
		theView = null;
		for (View view : views) {
			if (view instanceof HsvSelectorView)
				theView = view;
		}
		assertNotNull(theView);
	}

	@Smoke
	public void testOldAndNewColorButtons() throws Exception {
		getRgbSelectorView();
		int oldBgColor = Utils.colorFromDrawable(solo.getButton(oldColorButton).getBackground());
		int newBgColor = Utils.colorFromDrawable(solo.getButton(newColorButton).getBackground());
		int activeColor = drawingSurface.getActiveColor();
		assertEquals(activeColor, oldBgColor);
		assertEquals(oldBgColor, newBgColor);

		int[] argb = new int[] { 255, 255, 0, 0 };
		Utils.selectColorFromPicker(solo, argb, toolbarButton1);
		argb = null;

		getRgbSelectorView();
		oldBgColor = Utils.colorFromDrawable(solo.getButton(oldColorButton).getBackground());
		newBgColor = Utils.colorFromDrawable(solo.getButton(newColorButton).getBackground());
		activeColor = drawingSurface.getActiveColor();
		assertEquals(Color.RED, activeColor);
		assertEquals(activeColor, oldBgColor);
		assertEquals(activeColor, newBgColor);

		solo.clickOnButton(oldColorButton);
		activeColor = drawingSurface.getActiveColor();
		assertEquals(Color.RED, activeColor);
	}

	/**
	 * Test the left slider for the alpha value on the HSV selector view.
	 */
	@Smoke
	public void testColorPickerHsvAlphaSelector() throws Exception {
		View hsvSelectorView = getHsvSelectorView();

		// HSV Alpha Selector
		ArrayList<View> views = solo.getViews(hsvSelectorView);
		View hsvAlphaSelectorView = null;
		for (View view : views) {
			if (view instanceof HsvAlphaSelectorView)
				hsvAlphaSelectorView = view;
		}
		assertNotNull(hsvAlphaSelectorView);

		// select alpha value 0
		int[] selectorCoords = new int[2];
		hsvAlphaSelectorView.getLocationOnScreen(selectorCoords);
		int width = hsvAlphaSelectorView.getWidth();
		int height = hsvAlphaSelectorView.getHeight();
		solo.clickOnScreen(selectorCoords[0] + (width / 2), selectorCoords[1] + height - 1);
		solo.clickOnButton(newColorButton);
		assertEquals(Color.TRANSPARENT, drawingSurface.getActiveColor());
	}

	/**
	 * Test the middle square field for the saturation value on the HSV selector view.
	 */
	@Smoke
	public void testColorPickerHsvSaturationSelector() throws Exception {
		View hsvSelectorView = getHsvSelectorView();

		// HSV Saturation Selector
		ArrayList<View> views = solo.getViews(hsvSelectorView);
		View hsvSaturationSelectorView = null;
		for (View view : views) {
			if (view instanceof HsvSaturationSelectorView)
				hsvSaturationSelectorView = view;
		}
		assertNotNull(hsvSaturationSelectorView);

		// select 0 saturation
		int[] selectorCoords = new int[2];
		hsvSaturationSelectorView.getLocationOnScreen(selectorCoords);
		solo.clickOnScreen(selectorCoords[0] + 1, selectorCoords[1] + 1);
		solo.clickOnButton(newColorButton);
		assertEquals(Color.WHITE, drawingSurface.getActiveColor());
	}

	/**
	 * Test the right slider for the hue value on the HSV selector view.
	 */
	@Smoke
	public void testColorPickerHsvHueSelector() throws Exception {
		View hsvSelectorView = getHsvSelectorView();

		// HSV Saturation Selector
		ArrayList<View> views = solo.getViews(hsvSelectorView);
		View hsvSaturationSelectorView = null;
		for (View view : views) {
			if (view instanceof HsvSaturationSelectorView)
				hsvSaturationSelectorView = view;
		}
		assertNotNull(hsvSaturationSelectorView);

		// select full saturation
		int[] selectorCoords = new int[2];
		hsvSaturationSelectorView.getLocationOnScreen(selectorCoords);
		int width = hsvSaturationSelectorView.getWidth();
		solo.clickOnScreen(selectorCoords[0] + width - 1, selectorCoords[1] + 1);

		// HSV Hue Selector (1)
		View hsvHueSelectorView = null;
		for (View view : views) {
			if (view instanceof HsvHueSelectorView)
				hsvHueSelectorView = view;
		}
		assertNotNull(hsvHueSelectorView);
		selectorCoords = new int[2];
		hsvHueSelectorView.getLocationOnScreen(selectorCoords);
		width = hsvHueSelectorView.getWidth();
		final int height = hsvHueSelectorView.getHeight();
		solo.clickOnScreen(selectorCoords[0] + (width / 2), selectorCoords[1] + height - 1);
		solo.clickOnButton(newColorButton);
		final int firstColor = drawingSurface.getActiveColor();

		solo.clickOnView(toolbarButton1);

		// HSV Hue Selector (2)
		hsvHueSelectorView = null;
		for (View view : views) {
			if (view instanceof HsvHueSelectorView)
				hsvHueSelectorView = view;
		}
		assertNotNull(hsvHueSelectorView);
		solo.clickOnScreen(selectorCoords[0] + (width / 2), selectorCoords[1] + (height / 2));
		solo.clickOnButton(newColorButton);
		assertFalse(firstColor == drawingSurface.getActiveColor());
	}

	@Smoke
	public void testColorPickerRgbSelector() throws Exception {
		getRgbSelectorView();
		solo.setProgressBar(0, 255);
		solo.setProgressBar(1, 255);
		solo.setProgressBar(2, 255);
		solo.setProgressBar(3, 255);
		solo.clickOnButton(newColorButton);
		assertEquals(Color.WHITE, drawingSurface.getActiveColor());
		getRgbSelectorView();
		solo.setProgressBar(0, 0);
		solo.setProgressBar(1, 0);
		solo.setProgressBar(2, 0);
		solo.setProgressBar(3, 255);
		solo.clickOnButton(newColorButton);
		assertEquals(Color.BLACK, drawingSurface.getActiveColor());
		getRgbSelectorView();
		solo.setProgressBar(0, 0);
		solo.setProgressBar(1, 0);
		solo.setProgressBar(2, 0);
		solo.setProgressBar(3, 0);
		solo.clickOnButton(newColorButton);
		assertEquals(Color.TRANSPARENT, drawingSurface.getActiveColor());
		getRgbSelectorView();
		solo.setProgressBar(0, 255);
		solo.setProgressBar(1, 0);
		solo.setProgressBar(2, 0);
		solo.setProgressBar(3, 255);
		solo.clickOnButton(newColorButton);
		assertEquals(Color.RED, drawingSurface.getActiveColor());
		getRgbSelectorView();
		solo.setProgressBar(0, 0);
		solo.setProgressBar(1, 255);
		solo.setProgressBar(2, 0);
		solo.setProgressBar(3, 255);
		solo.clickOnButton(newColorButton);
		assertEquals(Color.GREEN, drawingSurface.getActiveColor());
		getRgbSelectorView();
		solo.setProgressBar(0, 0);
		solo.setProgressBar(1, 0);
		solo.setProgressBar(2, 255);
		solo.setProgressBar(3, 255);
		solo.clickOnButton(newColorButton);
		assertEquals(Color.BLUE, drawingSurface.getActiveColor());
	}

	@Smoke
	public void testColorPickerPresetSelector() throws Exception {
		View presetView = getPreSelectorView();
		ArrayList<View> views = solo.getViews(presetView);
		int previousColor = drawingSurface.getActiveColor();
		int i = 0;
		Iterator<View> iterator = views.iterator();
		while (i++ < 6 && iterator.hasNext()) {
			View view = iterator.next();
			if (view instanceof android.widget.Button) {
				solo.clickOnView(view);
				solo.clickOnButton(newColorButton);
				assertFalse(previousColor == drawingSurface.getActiveColor());
				previousColor = drawingSurface.getActiveColor();
				getPreSelectorView();
			}
		}
	}

	/**
	 * Helper method to open and retrieve the colorpicker's HSV selector view.
	 */
	private View getHsvSelectorView() {
		solo.clickOnView(toolbarButton1);
		solo.waitForView(ColorPickerView.class, 1, 200);
		ArrayList<View> views = solo.getViews();
		View colorPickerView = null;
		View hsvSelectorView = null;
		for (View view : views) {
			if (view instanceof ColorPickerView)
				colorPickerView = view;
			if (view instanceof HsvSelectorView)
				hsvSelectorView = view;
		}
		assertNotNull(colorPickerView);

		if (hsvSelectorView == null) {
			solo.clickOnText(hsvTab);
			views = solo.getViews(colorPickerView);
			for (View view : views) {
				if (view instanceof HsvSelectorView)
					hsvSelectorView = view;
			}
		}
		assertNotNull(hsvSelectorView);
		return hsvSelectorView;
	}

	/**
	 * Helper method to open and retrieve the colorpicker's RGB selector view.
	 */
	private View getRgbSelectorView() {
		solo.clickOnView(toolbarButton1);
		solo.waitForView(ColorPickerView.class, 1, 200);
		ArrayList<View> views = solo.getViews();
		View colorPickerView = null;
		View rgbSelectorView = null;
		for (View view : views) {
			if (view instanceof ColorPickerView)
				colorPickerView = view;
			if (view instanceof RgbSelectorView)
				rgbSelectorView = view;
		}
		assertNotNull(colorPickerView);

		if (rgbSelectorView == null) {
			solo.clickOnText(rgbTab);
			views = solo.getViews(colorPickerView);
			for (View view : views) {
				if (view instanceof RgbSelectorView)
					rgbSelectorView = view;
			}
		}
		assertNotNull(rgbSelectorView);
		return rgbSelectorView;
	}

	/**
	 * Helper method to open and retrieve the colorpicker's Preset selector view.
	 */
	private View getPreSelectorView() {
		solo.clickOnView(toolbarButton1);
		solo.waitForView(ColorPickerView.class, 1, 200);
		ArrayList<View> views = solo.getViews();
		View colorPickerView = null;
		View preSelectorView = null;
		for (View view : views) {
			if (view instanceof ColorPickerView)
				colorPickerView = view;
			if (view instanceof PresetSelectorView)
				preSelectorView = view;
		}
		assertNotNull(colorPickerView);

		if (preSelectorView == null) {
			solo.clickOnText(preTab);
			views = solo.getViews(colorPickerView);
			for (View view : views) {
				if (view instanceof PresetSelectorView)
					preSelectorView = view;
			}
		}
		assertNotNull(preSelectorView);
		return preSelectorView;
	}
}
