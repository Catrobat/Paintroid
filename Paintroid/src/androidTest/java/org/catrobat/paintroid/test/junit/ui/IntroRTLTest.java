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

import android.content.ComponentName;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.catrobat.paintroid.test.junit.EspressoHelpers.checkDotsColors;
import static org.catrobat.paintroid.test.junit.EspressoHelpers.equalsNumberDots;
import static org.catrobat.paintroid.test.junit.EspressoHelpers.isNotVisible;


@RunWith(AndroidJUnit4.class)
public class IntroRTLTest extends IntroTestBase {

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        rtl = true;
        super.setUp();
    }

    @Test
    public void testNumberDots() {
        onView(withId(R.id.layoutDots)).check(matches(equalsNumberDots(layouts.length)));
    }

    @Test
    public void testCheckLastPage() {
        changePageFromLayoutResource(R.layout.islide_getstarted);
        onView(withId(R.id.btn_next)).check(isNotVisible());
        onView(withId(R.id.btn_skip)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.btn_skip)).check(matches(withText(R.string.got_it)));
        onView(withId(R.id.btn_skip)).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), MainActivity.class)));
    }

    @Test
    public void testButtonsCompleteVisible() {
        for (int i = layouts.length-1; i < 0 ; i--) {
            changePage(i);
            onView(withId(R.id.btn_skip)).check(matches(isCompletelyDisplayed()));
            onView(withId(R.id.btn_skip)).check(matches(withText(R.string.next)));
            onView(withId(R.id.btn_next)).check(matches(isCompletelyDisplayed()));
            onView(withId(R.id.btn_next)).check(matches(withText(R.string.skip)));
        }
    }

    @Test
    public void testCheckDotsColor() {
        for (int i = layouts.length-1; i < 0 ; i--) {
            changePage(i);
            onView(withId(R.id.layoutDots)).check(matches(checkDotsColors(i, colorActive, colorInactive)));
        }
    }

}
