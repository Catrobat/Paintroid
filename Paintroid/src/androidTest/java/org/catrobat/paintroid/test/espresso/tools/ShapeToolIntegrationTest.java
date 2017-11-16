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

package org.catrobat.paintroid.test.espresso.tools;

import android.graphics.Bitmap;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.*;



@RunWith(AndroidJUnit4.class)
public class ShapeToolIntegrationTest {

	static private int tools_brush = R.id.tools_brush;
	static private int tools_shape = R.id.tools_rectangle;
	static private int drawing_surface = R.id.drawingSurfaceView;

	static private int tooloptions = R.id.layout_tool_options;

	static private int undo = R.id.btn_top_undo;

	private int[] Shapes = {R.id.shapes_square_btn,
			R.id.shapes_circle_btn,
			R.id.shapes_heart_btn,
			R.id.shapes_star_btn};

	@Rule
	public ActivityTestRule<MainActivity> mActivityRule =
			new ActivityTestRule<>(MainActivity.class);

	@Test
	public void testRememberShapeAfterToolSwitch() {

		for (int i = 0; i < Shapes.length; i++) {
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
