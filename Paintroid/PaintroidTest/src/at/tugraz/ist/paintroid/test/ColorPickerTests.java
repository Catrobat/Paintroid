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
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.view.View;
import android.widget.ImageButton;
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

	private ImageButton colorPickerButton;
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
		colorPickerButton = (ImageButton) mainActivity.findViewById(R.id.ibtn_Color);
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
		solo.clickOnView(colorPickerButton);
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

	/**
	 * Test the left slider for the alpha value on the HSV selector view.
	 */
	@Smoke
	public void testColorPickerHsvAlphaSelector() throws Exception {
		View hsvSelectorView = getHsvSelectorView();
		assertNotNull(hsvSelectorView);

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
		assertNotNull(hsvSelectorView);

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
		assertNotNull(hsvSelectorView);

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

		solo.clickOnView(colorPickerButton);

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

	/**
	 * Helper method to retrieve the colorpicker's HSV selector view.
	 */
	private View getHsvSelectorView() {
		solo.clickOnView(colorPickerButton);
		solo.waitForView(ColorPickerView.class, 1, 200);
		ArrayList<View> views = solo.getViews();
		View colorPickerView = null;
		for (View view : views) {
			if (view instanceof ColorPickerView)
				colorPickerView = view;
		}
		assertNotNull(colorPickerView);

		solo.clickOnText(hsvTab);
		views = solo.getViews(colorPickerView);
		View hsvSelectorView = null;
		for (View view : views) {
			if (view instanceof HsvSelectorView)
				hsvSelectorView = view;
		}
		return hsvSelectorView;
	}
}
