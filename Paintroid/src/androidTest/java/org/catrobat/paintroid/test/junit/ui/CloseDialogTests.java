/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.junit.ui;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
public class CloseDialogTests {

	@Rule
	public ActivityTestRule<MainActivity> mActivityRule =
			new ActivityTestRule<>(MainActivity.class);


	@Test
	public void testCloseNavigationDrawerOnBackPressed() {
		onView(withId(R.id.drawer_layout)).perform(open());
		onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
		pressBack();
		onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
	}

	@Test
	public void testCloseLayerDialogOnBackPressed() {
		onView(withId(R.id.drawer_layout)).perform(open(Gravity.RIGHT));
		onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
		pressBack();
		onView(withId(R.id.drawer_layout)).check(matches(isClosed()));

	}

	@Test
	public void testCloseColorPickerDialogOnBackPressed() {
		onView(withId(R.id.btn_top_colorframe)).perform(click());
		onView(withId(R.id.colorchooser_base_layout)).check(matches(isDisplayed()));
		pressBack();
		onView(withId(R.id.colorchooser_base_layout)).check(doesNotExist());
	}


	@Test
	public void testCloseToolOptionOnBackPressed() {
		onView(withId(R.id.tools_rectangle)).perform(click());
		onView(withId(R.id.layout_tool_options)).check((matches(isDisplayed())));
		pressBack();
		onView(withId(R.id.layout_tool_options)).check(matches(not(isDisplayed())));
	}

	@Test
	public void testCloseToolOptionsOnUndoPressed() {
		onView(withId(R.id.tools_text)).perform(scrollTo(), click());
		onView(withId(R.id.layout_tool_options)).check((matches(isDisplayed())));
		onView(withId(R.id.btn_top_undo)).perform(click());
		onView(withId(R.id.layout_tool_options)).check(matches(not(isDisplayed())));
	}

}
