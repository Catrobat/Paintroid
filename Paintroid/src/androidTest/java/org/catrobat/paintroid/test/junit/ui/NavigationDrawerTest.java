package org.catrobat.paintroid.test.junit.ui;

import org.catrobat.paintroid.MainActivity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerActions.close;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class NavigationDrawerTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testNavigationDrawerOpen() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        onView(withId(R.id.drawer_layout)).perform(close());
    }

    @Test
    public void testNavigationDrawerItemMenuSaveClick(){

        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.menu_save_image)).perform(click());
    }

    @Test
    public void testNavigationDrawerItemMenuCopyClick(){
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.menu_save_copy)).perform(click());
    }

    @Test
    public void testNavigationDrawerItemTermsOfUserClick(){
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.menu_terms_of_use_and_service)).perform(click());
    }

    @Test
    public void testNavigationDrawerItemHelpClick(){
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.help_title)).perform(click());
    }

    @Test
    public void testNavigationDrawerItemAboutClick(){
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.menu_about)).perform(click());
    }

    /*@Test
    public void testNavigationDrawerItemLoadClick(){
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.menu_load_image)).perform(click());
        mActivityRule.launchActivity(null);
    }*/
}
