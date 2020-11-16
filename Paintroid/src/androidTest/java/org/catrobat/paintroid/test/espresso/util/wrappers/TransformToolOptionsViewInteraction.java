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

import org.catrobat.paintroid.R;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.setProgress;
import static org.hamcrest.Matchers.not;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public final class TransformToolOptionsViewInteraction extends CustomViewInteraction {
	private TransformToolOptionsViewInteraction() {
		super(onView(withId(R.id.pocketpaint_main_tool_options)));
	}

	public static TransformToolOptionsViewInteraction onTransformToolOptionsView() {
		return new TransformToolOptionsViewInteraction();
	}

	public TransformToolOptionsViewInteraction performAutoCrop() {
		onView(withId(R.id.pocketpaint_transform_auto_crop_btn))
				.perform(click());
		return this;
	}

	public TransformToolOptionsViewInteraction checkAutoDisplayed() {
		onView(withText(R.string.transform_auto_crop_text))
				.check(matches(isDisplayed()));
		return this;
	}

	public TransformToolOptionsViewInteraction performRotateClockwise() {
		onView(withId(R.id.pocketpaint_transform_rotate_right_btn))
				.perform(click());
		return this;
	}

	public TransformToolOptionsViewInteraction performRotateCounterClockwise() {
		onView(withId(R.id.pocketpaint_transform_rotate_left_btn))
				.perform(click());
		return this;
	}

	public TransformToolOptionsViewInteraction performFlipVertical() {
		onView(withId(R.id.pocketpaint_transform_flip_vertical_btn))
				.perform(click());
		return this;
	}

	public TransformToolOptionsViewInteraction performFlipHorizontal() {
		onView(withId(R.id.pocketpaint_transform_flip_horizontal_btn))
				.perform(click());
		return this;
	}

	public TransformToolOptionsViewInteraction moveSliderTo(int moveTo) {
		onView(withId(R.id.pocketpaint_transform_resize_seekbar))
				.perform(setProgress(moveTo));
		return this;
	}

	public TransformToolOptionsViewInteraction performApplyResize() {
		onView(withId(R.id.pocketpaint_transform_apply_resize_btn))
				.perform(click());
		return this;
	}

	public TransformToolOptionsViewInteraction checkPercentageTextMatches(int expected) {
		onView(withId(R.id.pocketpaint_transform_resize_percentage_text))
				.check(matches(withText(Integer.toString(expected))));
		return this;
	}

	public TransformToolOptionsViewInteraction checkIsDisplayed() {
		onView(withId(R.id.pocketpaint_main_tool_options))
				.check(matches(isDisplayed()));
		return this;
	}

	public TransformToolOptionsViewInteraction checkIsNotDisplayed() {
		onView(withId(R.id.pocketpaint_main_tool_options))
				.check(matches(not(isDisplayed())));
		return this;
	}
}
