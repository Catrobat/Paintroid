package org.catrobat.paintroid.test.junit.tools;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;

import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.implementation.GeometricFillTool;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import android.graphics.Bitmap;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
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

    static private int tools_brush = R.id.tools_brush;
    static private int tools_shape = R.id.tools_rectangle;
    static private int drawing_surface = R.id.drawingSurfaceView;

    static private int tooloptions = R.id.layout_tool_options;

    static private int undo = R.id.btn_top_undo;

    private int[] Shapes = {    R.id.shapes_square_btn,
                                R.id.shapes_circle_btn,
                                R.id.shapes_heart_btn,
                                R.id.shapes_star_btn };

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testRememberShapeAfterToolSwitch() {

        for(int i = 0; i < Shapes.length; i++) {
            onView(withId(tools_shape)).perform(click());
            onView(withId(tooloptions)).check(matches(isDisplayed()));
            onView(withId(Shapes[i])).perform(click());
            onView(withId(drawing_surface)).perform(doubleClick());
            Bitmap expected_bitmap = PaintroidApplication.drawingSurface.getBitmapCopy();
            onView(withId(tools_brush)).perform(click());
            onView(withId(undo)).perform(click());
            onView(withId(tools_shape)).perform(click());
            onView(withId(tooloptions)).check(matches(isDisplayed()));
            onView(withId(drawing_surface)).perform(doubleClick());
            Bitmap actual_bitmap = PaintroidApplication.drawingSurface.getBitmapCopy();
            assertTrue(expected_bitmap.sameAs(actual_bitmap));
            onView(withId(tools_brush)).perform(click());
            onView(withId(undo)).perform(click());

        }





    }

}
