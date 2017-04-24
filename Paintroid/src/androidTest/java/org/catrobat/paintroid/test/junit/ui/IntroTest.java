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
import android.content.Context;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.LinearLayout;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.Session;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.catrobat.paintroid.intro.TapTargetBase.getToolTypeFromView;
import static org.catrobat.paintroid.test.junit.stubs.EspressoHelpers.checkDotsColors;
import static org.catrobat.paintroid.test.junit.stubs.EspressoHelpers.equalsNumberDots;
import static org.catrobat.paintroid.test.junit.stubs.EspressoHelpers.espressoWait;
import static org.catrobat.paintroid.test.junit.stubs.EspressoHelpers.isNotVisible;
import static org.catrobat.paintroid.test.junit.stubs.EspressoHelpers.selectViewPagerPage;
import static org.catrobat.paintroid.test.junit.stubs.EspressoHelpers.withDrawable;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class IntroTest {


    @Rule
    public IntentsTestRule<WelcomeActivity> mActivityRule =
            new IntentsTestRule<>(WelcomeActivity.class, true, false);

    private Session session;
    private Intent intent;
    private WelcomeActivity activity;
    private int[] layouts;
    int colorActive;
    int colorInactive;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        intent = new Intent();
        Context context = getInstrumentation().getTargetContext();
        session = new Session(context);
        session.setFirstTimeLaunch(true);
        mActivityRule.launchActivity(intent);
        activity = mActivityRule.getActivity();
        layouts = (int[]) PrivateAccess.getMemberValue(WelcomeActivity.class, activity, "layouts");
        colorActive = (int) PrivateAccess.getMemberValue(WelcomeActivity.class, activity, "colorActive");
        colorInactive = (int) PrivateAccess.getMemberValue(WelcomeActivity.class, activity, "colorInactive");
    }


    @Test
    public void testButtonsCompleteVisible() {

        for (int i = 0; i < layouts.length - 1; i++) {
            changePage(i);
            onView(withId(R.id.btn_next)).check(matches(isCompletelyDisplayed()));
            onView(withId(R.id.btn_next)).check(matches(withText(R.string.next)));
            onView(withId(R.id.btn_skip)).check(matches(isCompletelyDisplayed()));
            onView(withId(R.id.btn_skip)).check(matches(withText(R.string.skip)));
        }
    }

    @Test
    public void testNumberDots() {
        onView(withId(R.id.layoutDots)).check(matches(equalsNumberDots(layouts.length)));
    }

    @Test
    public void testCheckLastPage() {

        changePage(layouts.length);
        onView(withId(R.id.btn_skip)).check(isNotVisible());
        onView(withId(R.id.btn_next)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.btn_next)).check(matches(withText(R.string.got_it)));
        onView(withId(R.id.btn_next)).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), MainActivity.class)));
    }

    @Test
    public void testCheckDotsColor() {
        for (int i = 0; i < layouts.length; i++) {
            changePage(i);
            onView(withId(R.id.layoutDots)).check(matches(checkDotsColors(i, colorActive, colorInactive)));
        }
    }

    @Test
    public void testPageWelcome() {
        changePage(getPageIndexFormLayout(R.layout.islide_welcome));
        checkSlideText(R.id.intro_welcome_head, R.string.welcome_to_pocket_paint);
        checkSlideText(R.id.intro_welcome_text, R.string.intro_welcome_text);
    }

    @Test
    public void testPageTools(){
        changePage(getPageIndexFormLayout(R.layout.islide_tools));
        checkSlideText(R.id.intro_tools_head, R.string.dialog_tools_title);
        checkSlideText(R.id.intro_tools_text, R.string.intro_tool_more_information);
        List<ToolType> toolTypeList = new ArrayList<>();


        View view = activity.findViewById(R.id.intro_tools_bottom_bar);
        LinearLayout bottomBarTools = (LinearLayout) view.findViewById(R.id.tools_layout);

        for (int i = 0; i < bottomBarTools.getChildCount(); i++) {
            view = bottomBarTools.getChildAt(i);
            ToolType toolType = getToolTypeFromView(view);
            if(toolType == null) {
                continue;
            }

            toolTypeList.add(toolType);
        }
        //        for (ToolType toolType : toolTypeList) {
//        }

        ToolType toolType = toolTypeList.get(0);

        onView(allOf(withId(toolType.getToolButtonID()), isDescendantOfA(withId(R.id.intro_tools_bottom_bar)))).perform(click());

        onData(containsString(activity.getString(toolType.getNameResource()))).check(matches(isDisplayed()));

    }

    @Test
    public void testPageLandscape() {
        changePage(R.layout.islide_landscape);
        checkSlideText(R.id.intro_landscape_head, R.string.landscape);
        checkSlideText(R.id.intro_landscape_text, R.string.intro_landscape_text);
        onView(withId(R.id.image_getstarded)).check(matches(withDrawable(R.drawable.intro_portrait)));

    }

    @Test
    public void testPageGetStared() {
        changePage(R.layout.islide_getstarted);
        checkSlideText(R.id.intro_started_head, R.string.enjoy_pocket_code);
        checkSlideText(R.id.intro_started_text, R.string.intro_get_started);
        onView(withId(R.id.image_landscape)).check(matches(withDrawable(R.drawable.intro_landscape)));
    }





    private void changePage(int page) {
        onView(withId(R.id.view_pager)).perform(selectViewPagerPage(page));
    }

    private void checkSlideText(final int viewResource, final int stringResource) {
        onView(withId(viewResource)).check(matches(withText(stringResource)));
    }

    private int getPageIndexFormLayout(final int layoutResource) {
        for (int i = 0; i < layouts.length; i++) {
            if(layouts[i] == layoutResource)
                return i;
        }

        return -1;
    }
}

