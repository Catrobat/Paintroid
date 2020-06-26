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
import org.catrobat.paintroid.tools.ToolType;

import androidx.test.espresso.ViewInteraction;

import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withDrawable;
import static org.hamcrest.Matchers.allOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public final class BottomNavigationViewInteraction extends CustomViewInteraction {
	private BottomNavigationViewInteraction() {
		super(onView(withId(R.id.pocketpaint_bottom_navigation)));
	}

	public static BottomNavigationViewInteraction onBottomNavigationView() {
		return new BottomNavigationViewInteraction();
	}

	public ViewInteraction onToolsClicked() {
		return onView(allOf(withId(R.id.icon), isDescendantOfA(withId(R.id.action_tools))))
				.perform(click());
	}

	public ViewInteraction onCurrentClicked() {
		return onView(allOf(withId(R.id.icon), isDescendantOfA(withId(R.id.action_current_tool))))
				.perform(click());
	}

	public ViewInteraction checkShowsCurrentTool(ToolType toolType) {
		onView(allOf(withId(R.id.icon), isDescendantOfA(withId(R.id.action_current_tool))))
				.check(matches(withDrawable(toolType.getDrawableResource())));

		return onView(withId(R.id.action_current_tool))
				.check(matches(hasDescendant(withText(toolType.getNameResource()))));
	}

	public ViewInteraction onColorClicked() {
		return onView(allOf(withId(R.id.icon), isDescendantOfA(withId(R.id.action_color_picker))))
				.perform(click());
	}

	public ViewInteraction onLayersClicked() {
		return onView(allOf(withId(R.id.icon), isDescendantOfA(withId(R.id.action_layers))))
				.perform(click());
	}
}
