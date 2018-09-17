/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.util.wrappers;

import android.support.design.widget.TabLayout;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.colorpicker.HSVColorPickerView;
import org.catrobat.paintroid.dialog.colorpicker.PresetSelectorView;
import org.catrobat.paintroid.dialog.colorpicker.RgbSelectorView;
import org.hamcrest.Matchers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withDrawable;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

public final class ColorPickerViewInteraction extends CustomViewInteraction {
	protected ColorPickerViewInteraction() {
		super(onView(withId(R.id.color_chooser_base_layout)));
	}

	public static ColorPickerViewInteraction onColorPickerView() {
		return new ColorPickerViewInteraction();
	}

	public ViewInteraction onOkButton() {
		return onView(withId(R.id.color_chooser_button_ok)).inRoot(isDialog());
	}

	public ColorPickerViewInteraction performOpenColorPicker() {
		onView(withId(R.id.pocketpaint_btn_top_color))
				.perform(click());
		return this;
	}

	public ColorPickerViewInteraction performCloseColorPickerWithDialogButton() {
		check(matches(isDisplayed()));
		onOkButton()
				.perform(click());
		return this;
	}

	public ViewInteraction onPresetSelectorView() {
		return onView(Matchers.<View>instanceOf(PresetSelectorView.class));
	}

	public ViewInteraction onRgbSelectorView() {
		return onView(Matchers.<View>instanceOf(RgbSelectorView.class));
	}

	public ViewInteraction onHSVColorPickerView() {
		return onView(Matchers.<View>instanceOf(HSVColorPickerView.class));
	}

	public ViewInteraction onColorChooserTabPreset() {
		return onView(allOf(isDescendantOfA(Matchers.<View>instanceOf(TabLayout.class)),
				withDrawable(R.drawable.ic_color_chooser_tab_preset)));
	}

	public ViewInteraction onColorChooserTabRgba() {
		return onView(allOf(isDescendantOfA(Matchers.<View>instanceOf(TabLayout.class)),
				withDrawable(R.drawable.ic_color_chooser_tab_rgba)));
	}

	public ViewInteraction onColorChooserTabHSV() {
		return onView(allOf(isDescendantOfA(Matchers.<View>instanceOf(TabLayout.class)),
				withDrawable(R.drawable.ic_color_chooser_tab_hsv)));
	}

	public ColorPickerViewInteraction performClickColorPickerPresetSelectorButton(int buttonPosition) {
		onView(
				allOf(
						isDescendantOfA(Matchers.<View>instanceOf(PresetSelectorView.class)),
						instanceOf(RecyclerView.class)
				))
				.perform(RecyclerViewActions.actionOnItemAtPosition(buttonPosition, scrollTo()))
				.perform(RecyclerViewActions.actionOnItemAtPosition(buttonPosition, click()));
		return this;
	}
}
