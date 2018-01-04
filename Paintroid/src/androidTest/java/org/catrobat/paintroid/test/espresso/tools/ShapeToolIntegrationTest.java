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
import android.graphics.PointF;
import android.support.test.rule.ActivityTestRule;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ShapeToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();
	@Parameter
	public int shape;
	@Parameter(1)
	public String shapeName;

	@Parameters(name = "{1}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{R.id.shapes_square_btn, "Square"},
				{R.id.shapes_circle_btn, "Circle"},
				{R.id.shapes_heart_btn, "Heart"},
				{R.id.shapes_star_btn, "Star"}
		});
	}

	@Before
	public void setUp() throws Exception {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);
	}

	@Test
	public void testRememberPositionAfterOrientationChange() {
		onView(withId(shape))
				.perform(click());

		PointF p = new PointF(0, 0);
		((BaseToolWithShape) PaintroidApplication.currentTool).toolPosition.set(p);

		activityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		assertEquals(p, ((BaseToolWithShape) PaintroidApplication.currentTool).toolPosition);
	}

	@Test
	public void testRememberShapeAfterOrientationChange() {
		onView(withId(shape))
				.perform(click())
				.check(matches(isSelected()));
		onToolBarView()
				.performCloseToolOptions();

		onView(isRoot())
				.perform(click());

		Bitmap expectedBitmap = PaintroidApplication.drawingSurface.getBitmapCopy();

		onTopBarView()
				.performUndo();

		activityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		onView(withId(shape))
				.check(matches(isSelected()));

		onToolBarView()
				.performCloseToolOptions();

		onView(isRoot())
				.perform(click());

		assertTrue(expectedBitmap.sameAs(PaintroidApplication.drawingSurface.getBitmapCopy()));
	}
}
