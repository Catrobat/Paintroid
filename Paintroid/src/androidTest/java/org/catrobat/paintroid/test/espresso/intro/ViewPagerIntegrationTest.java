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

import android.support.test.espresso.Espresso;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.ViewPager;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.intro.IntroPageViewAdapter;
import org.catrobat.paintroid.test.espresso.intro.util.WelcomeActivityIntentsTestRule;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.shouldStartSequence;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ViewPagerIntegrationTest {

	@Rule
	public WelcomeActivityIntentsTestRule activityRule = new WelcomeActivityIntentsTestRule(false);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private ViewPager viewPager;
	private IntroPageViewAdapter viewPagerAdapter;

	@Before
	public void setUp() {
		viewPager = activityRule.getActivity().viewPager;
		viewPagerAdapter = (IntroPageViewAdapter) viewPager.getAdapter();
	}

	@After
	public void tearDown() {
		shouldStartSequence(false);
	}

	@Test
	public void checkStartingIndex() {
		int currentItem = viewPager.getCurrentItem();
		assertEquals(0, currentItem);
	}

	@Test
	public void checkSlideCount() {
		assertEquals(activityRule.getLayouts().length, viewPagerAdapter.getCount());
	}

	@Test
	public void checkSlides() {
		int[] adapterLayouts = viewPagerAdapter.layouts;
		assertArrayEquals(activityRule.getLayouts(), adapterLayouts);
	}

	@Test
	public void pressNextAndCheckIndex() {
		shouldStartSequence(false);
		for (int i = 0; i < activityRule.getLayouts().length; i++) {
			assertEquals(i, viewPager.getCurrentItem());
			onView(withId(R.id.btn_next)).perform(click());
		}
	}

	@Test
	public void swipeAndCheckIndex() {
		for (int i = 0; i < activityRule.getLayouts().length; i++) {
			assertEquals(i, viewPager.getCurrentItem());
			onView(isRoot()).perform(swipeLeft());
		}
	}

	@Test
	public void pressBack() {
		Espresso.pressBack();
	}
}
