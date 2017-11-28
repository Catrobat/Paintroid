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
import android.support.annotation.IdRes;
import android.support.test.rule.ActivityTestRule;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.junit.Assert.assertTrue;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class ShapeToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Parameters(name = "{1}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
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

	@Parameter
	public @IdRes int shape;

	@Parameter(value = 1)
	public String shape_name;

	@Ignore("Enable with PAINT-234")
	@Test
	public void testRememberShapeAfterToolSwitch() {
		onView(withId(shape))
				.perform(click());
		onToolBarView()
				.performCloseToolOptions();
		onView(isRoot())
				.perform(click());

		Bitmap expected_bitmap = PaintroidApplication.drawingSurface.getBitmapCopy();

		onTopBarView()
				.performUndo();
		onToolBarView()
				.performSelectTool(ToolType.BRUSH)
				.performSelectTool(ToolType.SHAPE)
				.performCloseToolOptions();

		onView(isRoot())
				.perform(click());

		Bitmap actual_bitmap = PaintroidApplication.drawingSurface.getBitmapCopy();
		assertTrue(expected_bitmap.sameAs(actual_bitmap));
	}

}
