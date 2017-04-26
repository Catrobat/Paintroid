package org.catrobat.paintroid.test.junit.ui;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.ui.BottomBar;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
/**
 * Created by joschi on 22.04.2017.
 */

@RunWith(AndroidJUnit4.class)
public class CloseDialogTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testCloseNavigationDrawerOnBackPressed() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        pressBack();
        onView (withId(R.id.drawer_layout)).check(matches(isClosed()));
    }

    @Test
    public void testCloseLayerDialogOnBackPressed() {
        onView(withId(R.id.drawer_layout)).perform(open(Gravity.RIGHT));
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        pressBack();
        onView (withId(R.id.drawer_layout)).check(matches(isClosed()));

    }

    @Test
    public void testCloseColorPickerDialogOnBackPressed() {
        onView(withId(R.id.btn_top_colorframe)).perform(click());
        onView(withId(R.id.colorchooser_base_layout)).check(matches(isDisplayed()));
        pressBack();
        onView (withId(R.id.colorchooser_base_layout)).check(doesNotExist());
    }

    /*
    @Test
    public void testCloseToolOptionOnBackPressed() {

    }*/

}
