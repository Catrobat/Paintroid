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

import android.support.test.espresso.ViewInteraction;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.colorpicker.PresetSelectorView;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.UiMatcher.hasTablePosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

public final class ColorPickerViewInteraction extends CustomViewInteraction {
	private static final int COLOR_PICKER_BUTTONS_PER_ROW = 4;

	protected ColorPickerViewInteraction() {
		super(onView(withId(R.id.view_colorpicker)));
	}

	public static ColorPickerViewInteraction onColorPickerView() {
		return new ColorPickerViewInteraction();
	}

	public ViewInteraction onOkButton() {
		return onView(withId(R.id.btn_colorchooser_ok));
	}

	public ColorPickerViewInteraction performOpenColorPicker() {
		check(matches(not(isDisplayed())));
		onView(withId(R.id.btn_top_color))
				.perform(click());
		return this;
	}

	public ColorPickerViewInteraction performCloseColorPickerWithDialogButton() {
		check(matches(isDisplayed()));
		onOkButton()
				.perform(click());
		return this;
	}

	public ColorPickerViewInteraction performClickColorPickerPresetSelectorButton(int buttonPosition) {
		final int colorButtonRowPosition = (buttonPosition / COLOR_PICKER_BUTTONS_PER_ROW);
		final int colorButtonColPosition = buttonPosition % COLOR_PICKER_BUTTONS_PER_ROW;

		onView(allOf(isDescendantOfA(withClassName(containsString(PresetSelectorView.class.getSimpleName()))),
				isDescendantOfA(isAssignableFrom(TableLayout.class)),
				isDescendantOfA(isAssignableFrom(TableRow.class)),
				hasTablePosition(colorButtonRowPosition, colorButtonColPosition)))
				.perform(scrollTo())
				.perform(click());
		return this;
	}
}
