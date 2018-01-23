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
import org.catrobat.paintroid.tools.implementation.GeometricFillTool;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public final class ShapeToolOptionsViewInteraction extends CustomViewInteraction {
	private ShapeToolOptionsViewInteraction() {
		super(onView(withId(R.id.main_tool_options)));
	}

	public static ShapeToolOptionsViewInteraction onShapeToolOptionsView() {
		return new ShapeToolOptionsViewInteraction();
	}

	public ShapeToolOptionsViewInteraction performSelectShape(GeometricFillTool.BaseShape shape) {
		switch (shape) {
			case RECTANGLE:
				onView(withId(R.id.shapes_square_btn))
						.perform(click());
				break;
			case OVAL:
				onView(withId(R.id.shapes_circle_btn))
						.perform(click());
				break;
			case HEART:
				onView(withId(R.id.shapes_heart_btn))
						.perform(click());
				break;
			case STAR:
				onView(withId(R.id.shapes_star_btn))
						.perform(click());
				break;
		}
		return this;
	}
}
