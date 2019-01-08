/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.wrappers.NavigationDrawerInteraction.onNavigationDrawer;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Test
	public void testNavigationDrawerMenuAboutTextIsCorrect() {

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.pocketpaint_menu_about))
				.perform(click());

		Context context = InstrumentationRegistry.getTargetContext();
		String aboutTextExpected = context.getString(R.string.pocketpaint_about_content,
				context.getString(R.string.pocketpaint_about_license));

		onView(withText(aboutTextExpected))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testNavigationDrawerMenuAboutClosesDrawer() {

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.pocketpaint_menu_about))
				.perform(click());

		pressBack();

		onView(withId(R.id.pocketpaint_nav_view))
				.check(matches(not(isDisplayed())));
	}
}
