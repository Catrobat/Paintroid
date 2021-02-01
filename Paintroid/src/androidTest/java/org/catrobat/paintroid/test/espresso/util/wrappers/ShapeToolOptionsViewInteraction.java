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
import org.catrobat.paintroid.tools.drawable.DrawableShape;
import org.catrobat.paintroid.tools.drawable.DrawableStyle;

import androidx.test.espresso.ViewAction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public final class ShapeToolOptionsViewInteraction extends CustomViewInteraction {
	private ShapeToolOptionsViewInteraction() {
		super(onView(withId(R.id.pocketpaint_main_tool_options)));
	}

	public static ShapeToolOptionsViewInteraction onShapeToolOptionsView() {
		return new ShapeToolOptionsViewInteraction();
	}

	private int getButtonIdFromBaseShape(DrawableShape baseShape) {
		switch (baseShape) {
			case RECTANGLE:
				return R.id.pocketpaint_shapes_square_btn;
			case OVAL:
				return R.id.pocketpaint_shapes_circle_btn;
			case HEART:
				return R.id.pocketpaint_shapes_heart_btn;
			case STAR:
				return R.id.pocketpaint_shapes_star_btn;
		}
		throw new IllegalArgumentException();
	}

	private int getButtonIdFromShapeDrawType(DrawableStyle shapeDrawType) {
		switch (shapeDrawType) {
			case STROKE:
				return R.id.pocketpaint_shape_ibtn_outline;
			case FILL:
				return R.id.pocketpaint_shape_ibtn_fill;
		}
		throw new IllegalArgumentException();
	}

	public ShapeToolOptionsViewInteraction performSelectShape(DrawableShape shape) {
		onView(withId(getButtonIdFromBaseShape(shape)))
				.perform(click());
		return this;
	}

	public ShapeToolOptionsViewInteraction performSelectShapeDrawType(DrawableStyle shapeDrawType) {
		onView(withId(getButtonIdFromShapeDrawType(shapeDrawType)))
				.perform(click());
		return this;
	}

	public void performSetOutlineWidth(ViewAction setWidth) {
		onView(withId(R.id.pocketpaint_shape_stroke_width_seek_bar))
				.perform(setWidth);
	}
}
