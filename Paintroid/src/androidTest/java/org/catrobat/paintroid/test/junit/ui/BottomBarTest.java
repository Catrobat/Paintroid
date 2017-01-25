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

package org.catrobat.paintroid.test.junit.ui;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;



@RunWith(AndroidJUnit4.class)
public class BottomBarTest {


    static private int start = R.id.tools_brush;
    static private int middle = R.id.tools_fill;
    static private int end = R.id.tools_text;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void nextDisplayOnStartTest() {
        onView(withId(R.id.bottom_next)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void previousNotDisplayOnStartTest() {
        onView(withId(R.id.bottom_previous)).check(matches(not(isDisplayed())));
    }


    @Test
    public void previousDisplayedOnEnd() {
        onView(withId(end)).perform(scrollTo());
        onView(withId(R.id.bottom_previous)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void nextNotDisplayedOnEnd() {
        onView(withId(R.id.tools_text)).perform(scrollTo());
        onView(withId(R.id.bottom_next)).check(matches(not(isDisplayed())));
    }

    @Test
    public void previousAndNextDisplayed() {
        onView(withId(middle)).perform(scrollTo());
        onView(withId(start)).check(matches(not(isDisplayed())));
        onView(withId(end)).check(matches(not(isDisplayed())));
        onView(withId(R.id.bottom_previous)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.bottom_next)).check(matches(isCompletelyDisplayed()));
    }

}

