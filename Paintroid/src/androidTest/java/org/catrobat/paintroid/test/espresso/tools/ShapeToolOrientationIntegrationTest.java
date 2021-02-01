/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.tools;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.drawable.DrawableShape;
import org.catrobat.paintroid.tools.drawable.DrawableStyle;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ShapeToolOptionsViewInteraction.onShapeToolOptionsView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.junit.Assert.assertTrue;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(Parameterized.class)
public class ShapeToolOrientationIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Parameter
	public DrawableShape shape;
	@Parameter(1)
	public int shapeId;

	private Workspace workspace;

	@Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{DrawableShape.RECTANGLE, R.id.pocketpaint_shapes_square_btn},
				{DrawableShape.OVAL, R.id.pocketpaint_shapes_circle_btn},
				{DrawableShape.HEART, R.id.pocketpaint_shapes_heart_btn},
				{DrawableShape.STAR, R.id.pocketpaint_shapes_star_btn}
		});
	}

	@Before
	public void setUp() {
		workspace = activityTestRule.getActivity().workspace;
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);
	}

	@Test
	public void testRememberShapeAfterOrientationChange() {
		onShapeToolOptionsView()
				.performSelectShape(shape);
		onView(withId(shapeId))
				.check(matches(isSelected()));
		onToolBarView()
				.performCloseToolOptionsView();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		Bitmap expectedBitmap = workspace.getBitmapOfCurrentLayer();

		onTopBarView()
				.performUndo();

		activityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		onView(withId(shapeId))
				.check(matches(isSelected()));

		onToolBarView()
				.performCloseToolOptionsView();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		assertTrue(expectedBitmap.sameAs(workspace.getBitmapOfCurrentLayer()));
	}

	@Test
	public void testRememberOutlineShapeAfterOrientationChange() {
		onShapeToolOptionsView()
				.performSelectShape(shape)
				.performSelectShapeDrawType(DrawableStyle.STROKE);
		onView(withId(shapeId))
				.check(matches(isSelected()));
		onView(withId(R.id.pocketpaint_shape_ibtn_outline))
				.check(matches(isSelected()));
		onToolBarView()
				.performCloseToolOptionsView();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		Bitmap expectedBitmap = workspace.getBitmapOfCurrentLayer();
		onTopBarView()
				.performUndo();

		activityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		onView(withId(shapeId))
				.check(matches(isSelected()));
		onView(withId(R.id.pocketpaint_shape_ibtn_outline))
				.check(matches(isSelected()));

		onToolBarView()
				.performCloseToolOptionsView();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		assertTrue(expectedBitmap.sameAs(workspace.getBitmapOfCurrentLayer()));
	}
}
