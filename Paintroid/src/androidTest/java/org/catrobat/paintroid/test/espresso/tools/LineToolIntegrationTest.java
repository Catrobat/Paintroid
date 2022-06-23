/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
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
import static org.catrobat.paintroid.test.utils.TestUtils.selectColorInDialog;
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

	private CountingIdlingResource idlingResource;

	@Before
	public void setUp() {
		idlingResource = launchActivityRule.getActivity().getIdlingResource();
		IdlingRegistry.getInstance().register(idlingResource);
		onToolBarView()
				.performSelectTool(ToolType.LINE);
		onTopBarView().performClickCheckmark();
	}

	@After
	public void tearDown() {
		IdlingRegistry.getInstance().unregister(idlingResource);
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

	@Test
	public void testConnectedLinesFeature() {
		onToolProperties().setColor(Color.BLACK);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView().performClickPlus();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE);

		onTopBarView().performClickPlus();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT);

		onTopBarView().performClickCheckmark();
	}

	@Test
	public void testConnectedLinesFeatureDrawingLine() {
		onToolProperties().setColor(Color.BLACK);

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT, DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView().performClickPlus();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE);

		onTopBarView().performClickPlus();

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT);

		onTopBarView().performClickCheckmark();
	}

	@Test
	public void testConnectedLinesFeatureRedrawingLine() {
		onToolProperties().setColor(Color.BLACK);

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT, DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT));

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView().performClickPlus();

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT);

		onTopBarView().performClickCheckmark();
	}

	@Test
	public void testClickingUndoOnceOnConnectedLines() {
		onToolProperties().setColor(Color.BLACK);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE);

		onTopBarView().performClickPlus();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView().performUndo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView().performClickCheckmark();
	}

	@Test
	public void testClickingUndoMoreThanOnceOnConnectedLines() {
		onToolProperties().setColor(Color.BLACK);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onTopBarView().performClickPlus();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		onTopBarView().performUndo();

		onTopBarView().performUndo();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

		onTopBarView().performClickCheckmark();
	}

	@Test
	public void testUndoWithDrawingConnectedLines() {
		onToolProperties().setColor(Color.BLACK);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onTopBarView().performClickPlus();

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		onTopBarView().performUndo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT));

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		onTopBarView().performClickPlus();

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT, DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE));

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));

		onTopBarView().performUndo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_LEFT);

		onTopBarView().performClickCheckmark();
	}

	@Test
	public void testColorChangesInConnectedLineMode() {
		onToolProperties().setColor(Color.BLACK);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onTopBarView().performClickPlus();

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));

		selectColorInDialog(0);

		onDrawingSurfaceView()
				.checkPixelColor(Color.parseColor("#FF0074CD"), BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		selectColorInDialog(1);

		onDrawingSurfaceView()
				.checkPixelColor(Color.parseColor("#FF00B4F1"), BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		onTopBarView().performUndo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.parseColor("#FF0074CD"), BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		onTopBarView().performRedo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.parseColor("#FF00B4F1"), BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		onTopBarView().performUndo();
		onTopBarView().performUndo();
		onTopBarView().performUndo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		onTopBarView().performUndo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

		onTopBarView().performRedo();
		onTopBarView().performRedo();
		onTopBarView().performRedo();
		onTopBarView().performRedo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.parseColor("#FF0074CD"), BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		onTopBarView().performClickCheckmark();
	}

	@Test
	public void testColorChangesAndQuittingConnectedLineMode() {
		onToolProperties().setColor(Color.BLACK);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onTopBarView().performClickPlus();

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));

		selectColorInDialog(0);

		onDrawingSurfaceView()
				.checkPixelColor(Color.parseColor("#FF0074CD"), BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		selectColorInDialog(1);

		selectColorInDialog(2);

		onDrawingSurfaceView()
				.checkPixelColor(Color.parseColor("#FF078707"), BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		onTopBarView().performUndo();
		onTopBarView().performUndo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.parseColor("#FF00B4F1"), BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT));

		onDrawingSurfaceView()
				.checkPixelColor(Color.parseColor("#FF0074CD"), BitmapLocationProvider.HALFWAY_BOTTOM_LEFT);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));

		onDrawingSurfaceView()
				.checkPixelColor(Color.parseColor("#FF0074CD"), BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE);

		onTopBarView().performClickCheckmark();
	}
}
