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

package org.catrobat.paintroid.test.espresso.Integration.intro;

import android.support.v4.view.ViewPager;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.Session;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.intro.IntroPageViewAdapter;
import org.catrobat.paintroid.test.espresso.util.base.IntroTestBase;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.changeIntroPage;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.shouldStartSequence;

@RunWith(JUnit4.class)
public class ViewPagerIntegrationTest extends IntroTestBase {

    private ViewPager viewPager;
    private IntroPageViewAdapter viewPagerAdapter;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        startSequence = false;
        super.setUpAndLaunchActivity();
        viewPager = (ViewPager) PrivateAccess.getMemberValue(WelcomeActivity.class, activity, "viewPager");
        viewPagerAdapter = (IntroPageViewAdapter) viewPager.getAdapter();
    }

    @After
    public void tearDown() throws NoSuchFieldException, IllegalAccessException {
        shouldStartSequence(false);
    }

    @Test
    public void checkStartingIndex() {
        int currentItem = viewPager.getCurrentItem();
        assertEquals(0, currentItem);
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
    public void pressNextAndCheckIndex() throws NoSuchFieldException, IllegalAccessException {
        shouldStartSequence(false);
        for (int i = 0; i < layouts.length; i++) {
            assertEquals(i, viewPager.getCurrentItem());
            onView(withId(R.id.btn_next)).perform(click());
        }
    }

    @Test
    public void SwipeAndCheckIndex() {
        for (int i = 0; i < layouts.length; i++) {
            assertEquals(i, viewPager.getCurrentItem());
            onView(isRoot()).perform(swipeLeft());
        }
    }


}
