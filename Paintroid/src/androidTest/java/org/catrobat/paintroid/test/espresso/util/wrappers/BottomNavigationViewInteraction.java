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

import org.catrobat.paintroid.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;

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

	public ViewInteraction onColorClicked() {
		return onView(allOf(withId(R.id.icon), isDescendantOfA(withId(R.id.action_color_picker))))
				.perform(click());
	}

	public ViewInteraction onLayersClicked() {
		return onView(allOf(withId(R.id.icon), isDescendantOfA(withId(R.id.action_layers))))
				.perform(click());
	}
}
