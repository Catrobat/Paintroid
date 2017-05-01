package org.catrobat.paintroid.test.junit.tools;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;

import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.implementation.GeometricFillTool;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerActions.close;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;



/**
 * Created by joschi on 27.04.2017.
 */

@RunWith(AndroidJUnit4.class)
public class ShapeToolTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testRememberShapeAfterToolSwitch() {
        onView(withId(R.id.tools_rectangle)).perform(click());
        onView(withId(R.id.layout_tool_options)).check(matches(isDisplayed()));
        onView(withId(R.id.shapes_heart_btn)).perform(click());

    }

}
