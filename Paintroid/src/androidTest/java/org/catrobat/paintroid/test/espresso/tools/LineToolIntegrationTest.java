/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.tools;

import android.graphics.Color;
import android.graphics.Paint.Cap;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.DEFAULT_STROKE_WIDTH;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.swipe;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withDrawable;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.hamcrest.Matchers.allOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LineToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public ScreenshotOnFailRule screenshotOnFailRule = new ScreenshotOnFailRule();

	@Before
	public void setUp() {
		onToolBarView()
				.performSelectTool(ToolType.LINE);
		onTopBarView().performClickCheckmark();
	}

	@Test
	public void testVerticalLineColor() {
		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testHorizontalLineColor() {
		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testDiagonalLineColor() {
		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testChangeLineToolForm() {

		onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
				.perform(click());

		onToolProperties()
				.checkStrokeWidth(DEFAULT_STROKE_WIDTH)
				.checkCap(Cap.SQUARE);
	}

	@Test
	public void testCheckmarkLineFeature() {
		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView().onCheckmarkButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_checkmark), isEnabled())));

		onTopBarView().performClickCheckmark();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testCheckmarkLineFeatureChangeColor() {
		onToolProperties().setColor(Color.BLACK);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE);

		onToolProperties().setColor(Color.GREEN);

		onDrawingSurfaceView()
				.checkPixelColor(Color.GREEN, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.GREEN, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView().onCheckmarkButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_checkmark), isEnabled())));

		onToolProperties().setColor(Color.RED);

		onTopBarView().performClickCheckmark();

		onDrawingSurfaceView()
				.checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testCheckmarkLineFeatureChangeShape() {
		onToolProperties().setColor(Color.BLACK);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE);

		onToolProperties().setCap(Cap.SQUARE);

		onToolProperties().checkCap(Cap.SQUARE);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView().onCheckmarkButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_checkmark), isEnabled())));

		onToolProperties().setColor(Color.BLACK);

		onTopBarView().performClickCheckmark();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testCheckmarkLineFeatureChangeStrokeWidth() {
		onToolProperties().setColor(Color.BLACK);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE);

		onToolProperties().setStrokeWidth(80);

		onToolProperties().checkStrokeWidth(80);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView().onCheckmarkButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_checkmark), isEnabled())));

		onTopBarView().performClickCheckmark();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}
}
