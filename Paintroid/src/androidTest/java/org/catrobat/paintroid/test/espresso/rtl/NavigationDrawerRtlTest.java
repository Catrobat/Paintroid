/**
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

package org.catrobat.paintroid.test.espresso.rtl;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.rtl.util.RtlActivityTestRule;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.rtl.util.RtlUiTestUtils.openMultilingualActivity;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringStartsWith.startsWith;

@RunWith(AndroidJUnit4.class)
public class NavigationDrawerRtlTest {
	private static final Locale ARABICLOCALE = new Locale("ar");

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new RtlActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Test
	public void testNavigationDrawerCloseOnBack() {
		openMultilingualActivity();
		onData(hasToString(startsWith(ARABICLOCALE.getDisplayName(ARABICLOCALE))))
				.perform(click());

		onView(withId(R.id.drawer_layout))
				.perform(open())
				.check(matches(isOpen()));
		pressBack();
		onView(withId(R.id.drawer_layout))
				.check(matches(not(isOpen())));
	}
}
