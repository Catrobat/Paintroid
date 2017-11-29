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

package org.catrobat.paintroid.test.espresso.intro;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.Session;
import org.catrobat.paintroid.test.espresso.intro.util.WelcomeActivityIntentsTestRule;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.changeIntroPage;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class SharedPreferencesIntegrationTest {

	@Rule
	public WelcomeActivityIntentsTestRule activityRule = new WelcomeActivityIntentsTestRule();

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Test
	public void sharedPreferencesSetOnSkip() {
		onView(withId(R.id.btn_skip))
				.perform(click());
		Session session = new Session(activityRule.getActivity());
		assertFalse(session.isFirstTimeLaunch());
	}

	@Test
	public void sharedPreferencesSetOnFinishIntro() {
		changeIntroPage(activityRule.getLayouts().length - 1);
		onView(withId(R.id.btn_next))
				.perform(click());
		Session session = new Session(activityRule.getActivity());
		assertFalse(session.isFirstTimeLaunch());
	}
}
