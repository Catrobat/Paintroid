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

import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.view.View;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerView;
import at.tugraz.ist.paintroid.dialog.colorpicker.HsvAlphaSelectorView;
import at.tugraz.ist.paintroid.dialog.colorpicker.HsvHueSelectorView;
import at.tugraz.ist.paintroid.dialog.colorpicker.HsvSaturationSelectorView;
import at.tugraz.ist.paintroid.dialog.colorpicker.HsvSelectorView;
import at.tugraz.ist.paintroid.dialog.colorpicker.PresetSelectorView;
import at.tugraz.ist.paintroid.dialog.colorpicker.RgbSelectorView;

import com.jayway.android.robotium.solo.Solo;

public class ColorPickerTests extends ActivityInstrumentationTestCase2<MainActivity> {
	private Solo solo;
	private MainActivity mainActivity;
	
	final int COLORPICKER = 0;

	public ColorPickerTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity)solo.getCurrentActivity();
	}
	

	/**
	 * @throws Exception
	 */
	@Smoke
	public void testColorPickerExists() throws Exception {
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(ColorPickerView.class, 1, 200);
		ArrayList<View> views = solo.getViews();
		View colorPickerView = null;
		for (View view : views) {
			if (view instanceof ColorPickerView)
				colorPickerView = view;
		}
		assertNotNull(colorPickerView);
	}
	
	/**
	 * @throws Exception
	 */
	@Smoke
	public void testColorPickerHasAllViews() throws Exception {
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(ColorPickerView.class, 1, 200);
		ArrayList<View> views = solo.getViews();
		View theView = null;
		for (View view : views) {
			if (view instanceof ColorPickerView)
				theView = view;
		}
		assertNotNull(theView);
		solo.clickOnText("RGB");
		views = solo.getViews();
		theView = null;
		for (View view : views) {
			if (view instanceof RgbSelectorView)
				theView = view;
		}
		assertNotNull(theView);
		solo.clickOnText("PRE");
		views = solo.getViews();
		theView = null;
		for (View view : views) {
			if (view instanceof PresetSelectorView)
				theView = view;
		}
		assertNotNull(theView);
		solo.clickOnText("HSV");
		views = solo.getViews();
		theView = null;
		for (View view : views) {
			if (view instanceof HsvSelectorView)
				theView = view;
		}
		assertNotNull(theView);
	}
	
	/**
	 * @throws Exception
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
		int[] selectorCoords = new int[2];
		hsvAlphaSelectorView.getLocationOnScreen(selectorCoords);
		int width = hsvAlphaSelectorView.getWidth();
		int height = hsvAlphaSelectorView.getHeight();
		solo.clickOnScreen(selectorCoords[0]+(width/2), selectorCoords[1]+height-1);
		solo.clickOnButton("New Color");
		assertEquals(Color.TRANSPARENT, mainActivity.getSelectedColor());
	}
	
	/**
	 * @throws Exception
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
		int[] selectorCoords = new int[2];
		hsvSaturationSelectorView.getLocationOnScreen(selectorCoords);
		int width = hsvSaturationSelectorView.getWidth();
		int height = hsvSaturationSelectorView.getHeight();
		solo.clickOnScreen(selectorCoords[0]+width-1, selectorCoords[1]+height-1);
		solo.clickOnButton("New Color");
		assertEquals(Color.BLACK, mainActivity.getSelectedColor());
	}
	
	/**
	 * @throws Exception
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
		int[] selectorCoords = new int[2];
		hsvSaturationSelectorView.getLocationOnScreen(selectorCoords);
		int width = hsvSaturationSelectorView.getWidth();
		solo.clickOnScreen(selectorCoords[0]+width-1, selectorCoords[1]+1);
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
		int height = hsvHueSelectorView.getHeight();
		solo.clickOnScreen(selectorCoords[0]+(width/2), selectorCoords[1]+height-1);
		solo.clickOnButton("New Color");
		int firstColor = mainActivity.getSelectedColor();
		hsvSelectorView = getHsvSelectorView();
		assertNotNull(hsvSelectorView);
		// HSV Hue Selector (2)
		hsvHueSelectorView = null;
		for (View view : views) {
			if (view instanceof HsvHueSelectorView)
				hsvHueSelectorView = view;
		}
		assertNotNull(hsvHueSelectorView);
		solo.clickOnScreen(selectorCoords[0]+(width/2), selectorCoords[1]+(height/2));
		solo.clickOnButton("New Color");
		assertFalse(firstColor == mainActivity.getSelectedColor());
	}
	
	private View getHsvSelectorView() {
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(ColorPickerView.class, 1, 200);
		ArrayList<View> views = solo.getViews();
		View colorPickerView = null;
		for (View view : views) {
			if (view instanceof ColorPickerView)
				colorPickerView = view;
		}
		assertNotNull(colorPickerView);
		solo.clickOnText("HSV");
		views = solo.getViews(colorPickerView);
		View hsvSelectorView = null;
		for (View view : views) {
			if (view instanceof HsvSelectorView)
				hsvSelectorView = view;
		}
		return hsvSelectorView;
	}
	
	@Override
	public void tearDown() throws Exception {
		solo = null;
		mainActivity = null;
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}
}
