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

import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.widget.LinearLayout;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.intro.TapTargetTopBar;
import org.catrobat.paintroid.test.junit.EspressoHelpers;
import org.catrobat.paintroid.test.junit.intro.TapTargetBaseTest;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class IntroTapTargetTest extends TapTargetBaseTest {

    private static String TT_CLASS_NAME = "com.getkeepsafe.taptargetview.TapTargetView";

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        super.setUp();
    }

    @Test
    public void testBottomBar() throws NoSuchFieldException, IllegalAccessException {
        changePageFromLayoutResource(R.layout.islide_tools);
        LinearLayout bottomBarTools = getBottomBarToolsView();
        List<ToolType> toolTypeList = getToolTypesFromView(bottomBarTools);

        onView(isRoot()).perform(click());

        for (ToolType toolType : toolTypeList) {

            ViewInteraction buttonView =
                    onView(allOf(withId(toolType.getToolButtonID()),
                            isDescendantOfA(withId(R.id.intro_tools_bottom_bar))));

            ViewInteraction fadeView = onView(withId(R.id.intro_tools_textview))
                    .check(matches(isDisplayed()));

            buttonView.perform(scrollTo())
                    .check(matches(isFocusable()))
                    .check(matches(isClickable()))
                    .perform(click());

            fadeView.check(matches(not(isDisplayed())));

            ViewInteraction tapTargetView = onView(
                    allOf(withClassName(is(TT_CLASS_NAME)), isDisplayed()));

            tapTargetView.perform(click())
                    .check(EspressoHelpers.isNotVisible());

        }
    }

    @Test
    public void testTopBarSequence() throws NoSuchFieldException, IllegalAccessException {
        changePageFromLayoutResource(R.layout.islide_possibilities);
        LinearLayout topBarTools = getTopBarToolsView();
        List<ToolType> toolTypeList = getToolTypesFromView(topBarTools);

        EspressoHelpers.espressoWait(admirationDelay);

        ViewInteraction tapTargetView = null;
        for (int i = 0; i < toolTypeList.size(); i++) {
            tapTargetView = onView(allOf(withClassName(is(TT_CLASS_NAME)), isDisplayed()));
            tapTargetView.perform(click());
        }

        tapTargetView.check(EspressoHelpers.isNotVisible());
        onView(withId(R.id.intro_possibilities_textview)).check(matches(isDisplayed()));
    }

    @Test
    public void testTapTargets() throws NoSuchFieldException, IllegalAccessException {
        changePageFromLayoutResource(R.layout.islide_possibilities);
        LinearLayout topBarToolsView = getTopBarToolsView();
        List<ToolType> toolTypeList = getToolTypesFromView(topBarToolsView);
        PrivateAccess.setMemberValue(TapTargetTopBar.class, null, "firsTimeSequence", false);

        EspressoHelpers.espressoWait(admirationDelay);

        for (ToolType toolType : toolTypeList) {

            ViewInteraction fadeView = onView(withId(R.id.intro_possibilities_textview))
                    .check(matches(isDisplayed()));

            onView(allOf(withId(toolType.getToolButtonID()),
                    isDescendantOfA(withId(R.id.intro_possibilites_topbar))))
                    .perform(click());

            fadeView.check(matches(not(isDisplayed())));

            onView(allOf(withClassName(is(TT_CLASS_NAME)), isDisplayed()))
                    .perform(click())
                    .check(EspressoHelpers.isNotVisible());
        }
    }

    @Test
    public void testTapTargetSequenceAppearOnce(){
        int pageIndex = getPageIndexFormLayout(R.layout.islide_possibilities);
        changePage(pageIndex);

        LinearLayout topBarToolsView = getTopBarToolsView();
        List<ToolType> toolTypeList = getToolTypesFromView(topBarToolsView);

        EspressoHelpers.espressoWait(admirationDelay);

        for (int i = 0; i < toolTypeList.size(); i++) {
            onView(allOf(withClassName(is(TT_CLASS_NAME)), isDisplayed()))
                    .perform(click());
        }

        if(pageIndex < layouts.length) {
            onView(isRoot()).perform(swipeLeft());
            onView(isRoot()).perform(swipeRight());
        } else {
            onView(isRoot()).perform(swipeRight());
            onView(isRoot()).perform(swipeLeft());
        }

        onView(allOf(withClassName(is(TT_CLASS_NAME)))).check(EspressoHelpers.isNotVisible());
        onView(withId(R.id.intro_possibilities_textview)).check(matches(isDisplayed()));
    }

}
