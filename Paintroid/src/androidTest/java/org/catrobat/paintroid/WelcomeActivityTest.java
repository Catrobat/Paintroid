package org.catrobat.paintroid;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class WelcomeActivityTest {

    @Rule
    public ActivityTestRule<WelcomeActivity> mActivityTestRule = new ActivityTestRule<>(WelcomeActivity.class);

    @Test
    public void welcomeActivityTest() {
        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Hilfe"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ViewInteraction viewPager = onView(
                allOf(withId(R.id.view_pager),
                        withParent(allOf(withId(R.id.welcome),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        viewPager.perform(swipeLeft());

        ViewInteraction viewPager2 = onView(
                allOf(withId(R.id.view_pager),
                        withParent(allOf(withId(R.id.welcome),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        viewPager2.perform(swipeLeft());

        ViewInteraction tapTargetView = onView(
                allOf(withClassName(is("com.getkeepsafe.taptargetview.TapTargetView")), isDisplayed()));
        tapTargetView.perform(click());

        ViewInteraction tapTargetView2 = onView(
                allOf(withClassName(is("com.getkeepsafe.taptargetview.TapTargetView")), isDisplayed()));
        tapTargetView2.perform(click());

        ViewInteraction tapTargetView3 = onView(
                allOf(withClassName(is("com.getkeepsafe.taptargetview.TapTargetView")), isDisplayed()));
        tapTargetView3.perform(click());

        ViewInteraction tapTargetView4 = onView(
                allOf(withClassName(is("com.getkeepsafe.taptargetview.TapTargetView")), isDisplayed()));
        tapTargetView4.perform(click());

        ViewInteraction viewPager3 = onView(
                allOf(withId(R.id.view_pager),
                        withParent(allOf(withId(R.id.welcome),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        viewPager3.perform(swipeLeft());

        ViewInteraction viewPager4 = onView(
                allOf(withId(R.id.view_pager),
                        withParent(allOf(withId(R.id.welcome),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        viewPager4.perform(swipeRight());

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.btn_top_layers),
                        withParent(allOf(withId(R.id.layout_top_bar),
                                withParent(withId(R.id.intro_possibilites_topbar)))),
                        isDisplayed()));
        imageButton.perform(click());

        ViewInteraction tapTargetView5 = onView(
                allOf(withClassName(is("com.getkeepsafe.taptargetview.TapTargetView")), isDisplayed()));
        tapTargetView5.perform(click());

    }

}
