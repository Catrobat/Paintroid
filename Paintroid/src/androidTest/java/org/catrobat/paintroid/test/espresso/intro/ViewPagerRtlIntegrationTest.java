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

import android.support.v4.view.ViewPager;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.intro.IntroPageViewAdapter;
import org.catrobat.paintroid.test.espresso.intro.base.IntroTestBase;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ViewPagerRtlIntegrationTest extends IntroTestBase {

    private ViewPager viewPager;
    private IntroPageViewAdapter viewPagerAdapter;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        rtl = true;
        startSequence = false;
        super.setUpAndLaunchActivity();
        viewPager = (ViewPager) PrivateAccess.getMemberValue(WelcomeActivity.class, activity, "viewPager");
        viewPagerAdapter = (IntroPageViewAdapter) viewPager.getAdapter();
    }

    @Test
    public void checkStartingIndex() {
        int currentItem = viewPager.getCurrentItem();
        assertEquals(layouts.length - 1, currentItem);
    }

    @Test
    public void checkSlideCount() {
        assertEquals(layouts.length, viewPagerAdapter.getCount());
    }

    @Test
    public void checkSlides() throws NoSuchFieldException, IllegalAccessException {
        int[] adapterLayouts = (int[]) PrivateAccess.getMemberValue(IntroPageViewAdapter.class, viewPagerAdapter, "layouts");
        assertEquals(layouts, adapterLayouts);
    }

    @Test
    public void pressNextAndCheckIndex() {
        for (int i = layouts.length - 1; i == 0; i--) {
            assertEquals(i, viewPager.getCurrentItem());
            onView(withId(R.id.btn_next)).perform(click());
        }
    }

    @Test
    public void swipeAndCheckIndex() {
        for (int i = layouts.length - 1; i == 0; i--) {
            assertEquals(i, viewPager.getCurrentItem());
            onView(withId(R.id.btn_next)).perform(swipeRight());
        }
    }


}
