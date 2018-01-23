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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public final class TransformToolOptionsViewInteraction extends CustomViewInteraction {
	private TransformToolOptionsViewInteraction() {
		super(onView(withId(R.id.main_tool_options)));
	}

	public static TransformToolOptionsViewInteraction onTransformToolOptionsView() {
		return new TransformToolOptionsViewInteraction();
	}

	public TransformToolOptionsViewInteraction performAutoCrop() {
		onView(withId(R.id.transform_auto_crop_btn))
				.perform(click());
		return this;
	}

	public TransformToolOptionsViewInteraction performRotateClockwise() {
		onView(withId(R.id.transform_rotate_right_btn))
				.perform(click());
		return this;
	}

	public TransformToolOptionsViewInteraction performRotateCounterClockwise() {
		onView(withId(R.id.transform_rotate_left_btn))
				.perform(click());
		return this;
	}

	public TransformToolOptionsViewInteraction performFlipVertical() {
		onView(withId(R.id.transform_flip_vertical_btn))
				.perform(click());
		return this;
	}

	public TransformToolOptionsViewInteraction performFlipHorizontal() {
		onView(withId(R.id.transform_flip_horizontal_btn))
				.perform(click());
		return this;
	}
}
