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
import android.support.test.espresso.contrib.DrawerActions;

import org.catrobat.paintroid.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public final class NavigationDrawerInteraction extends CustomViewInteraction {
	private NavigationDrawerInteraction() {
		super(onView(withId(R.id.drawer_layout)));
	}

	public static NavigationDrawerInteraction onNavigationDrawer() {
		return new NavigationDrawerInteraction();
	}

	public ViewInteraction onNavigationView() {
		return onView(withId(R.id.nav_view));
	}

	public NavigationDrawerInteraction performOpen() {
		perform(DrawerActions.open());
		onNavigationView()
				.perform(swipeUp());
		return this;
	}

	public NavigationDrawerInteraction performClose() {
		perform(DrawerActions.close());
		return this;
	}
}
