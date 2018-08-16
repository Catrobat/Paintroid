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

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.support.test.rule.ActivityTestRule;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.junit.Assert.assertTrue;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ShapeToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Parameter
	public int shape;
	@Parameter(1)
	public String shapeName;

	@Parameters(name = "{1}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{R.id.pocketpaint_shapes_square_btn, "Square"},
				{R.id.pocketpaint_shapes_circle_btn, "Circle"},
				{R.id.pocketpaint_shapes_heart_btn, "Heart"},
				{R.id.pocketpaint_shapes_star_btn, "Star"}
		});
	}

	@Before
	public void setUp() throws Exception {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);
	}

	@Test
	public void testRememberShapeAfterOrientationChange() {
		onView(withId(shape))
				.perform(click())
				.check(matches(isSelected()));
		onToolBarView()
				.performCloseToolOptions();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		Bitmap expectedBitmap = PaintroidApplication.drawingSurface.getBitmapCopy();

		onTopBarView()
				.performUndo();

		activityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		onView(withId(shape))
				.check(matches(isSelected()));

		onToolBarView()
				.performCloseToolOptions();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		assertTrue(expectedBitmap.sameAs(PaintroidApplication.drawingSurface.getBitmapCopy()));
	}

	@Test
	public void testRememberOutlineShapeAfterOrientationChange() {
		onView(withId(R.id.pocketpaint_shape_ibtn_outline))
				.perform(click());
		onView(withId(shape))
				.perform(click())
				.check(matches(isSelected()));
		onToolBarView()
				.performCloseToolOptions();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		Bitmap expectedBitmap = PaintroidApplication.drawingSurface.getBitmapCopy();
		onTopBarView()
				.performUndo();

		activityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		onView(withId(shape))
				.check(matches(isSelected()));

		onToolBarView()
				.performCloseToolOptions();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		assertTrue(expectedBitmap.sameAs(PaintroidApplication.drawingSurface.getBitmapCopy()));
	}
}
