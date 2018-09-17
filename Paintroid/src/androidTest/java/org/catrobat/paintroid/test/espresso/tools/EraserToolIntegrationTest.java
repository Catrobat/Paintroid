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
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.DEFAULT_STROKE_WIDTH;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.setProgress;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withProgress;
import static org.catrobat.paintroid.test.espresso.util.wrappers.BrushPickerViewInteraction.onBrushPickerView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class EraserToolIntegrationTest {

	private static final String TEXT_DEFAULT_STROKE_WIDTH = Integer.toString(DEFAULT_STROKE_WIDTH);

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Test
	public void testEraseNothing() {
		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

		onToolBarView()
				.performSelectTool(ToolType.ERASER);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testErase() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onToolBarView()
				.performSelectTool(ToolType.ERASER);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testSwitchingBetweenBrushAndEraser() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onToolBarView()
				.performSelectTool(ToolType.ERASER);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onToolBarView()
				.performSelectTool(ToolType.ERASER);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testChangeEraserBrushSize() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onToolBarView()
				.performSelectTool(ToolType.ERASER)
				.performOpenToolOptions();

		onBrushPickerView().onStrokeWidthSeekBar()
				.check(matches(allOf(isDisplayed(), withProgress(DEFAULT_STROKE_WIDTH))));
		onBrushPickerView().onStrokeWidthTextView()
				.check(matches(allOf(isDisplayed(), withText(TEXT_DEFAULT_STROKE_WIDTH))));

		int newStrokeWidth = 80;
		onBrushPickerView().onStrokeWidthSeekBar()
				.perform(setProgress(newStrokeWidth))
				.check(matches(withProgress(newStrokeWidth)));

		onToolBarView()
				.performCloseToolOptions();

		onToolProperties()
				.checkStrokeWidth(80);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testChangeEraserBrushForm() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onToolBarView()
				.performSelectTool(ToolType.ERASER)
				.performOpenToolOptions();

		onBrushPickerView().onStrokeCapSquareView()
				.perform(click());

		onToolBarView()
				.performCloseToolOptions();

		onToolProperties()
				.checkCap(Cap.SQUARE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testRestorePreviousToolSettings() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onToolBarView()
				.performSelectTool(ToolType.ERASER)
				.performOpenToolOptions();

		onBrushPickerView().onStrokeWidthTextView()
				.check(matches(allOf(isDisplayed(), withText(TEXT_DEFAULT_STROKE_WIDTH))));
		onBrushPickerView().onStrokeWidthSeekBar()
				.check(matches(allOf(isDisplayed(), withProgress(DEFAULT_STROKE_WIDTH))));

		int newStrokeWidth = 80;
		onBrushPickerView().onStrokeWidthSeekBar()
				.perform(setProgress(newStrokeWidth))
				.check(matches(withProgress(newStrokeWidth)));

		onBrushPickerView().onStrokeCapSquareView()
				.perform(click());

		onToolProperties()
				.checkStrokeWidth(newStrokeWidth)
				.checkCap(Cap.SQUARE);

		onToolBarView()
				.performCloseToolOptions()
				.performOpenToolOptions();

		onBrushPickerView().onStrokeWidthSeekBar()
				.check(matches(withProgress(newStrokeWidth)));

		int eraserStrokeWidth = 60;
		onBrushPickerView().onStrokeWidthSeekBar()
				.perform(setProgress(eraserStrokeWidth))
				.check(matches(withProgress(eraserStrokeWidth)));

		onBrushPickerView().onStrokeCapRoundView()
				.perform(click());

		onToolProperties()
				.checkStrokeWidth(eraserStrokeWidth)
				.checkCap(Cap.ROUND);

		onToolBarView()
				.performSelectTool(ToolType.BRUSH)
				.performOpenToolOptions();

		onBrushPickerView().onStrokeWidthSeekBar()
				.check(matches(withProgress(eraserStrokeWidth)));
		onBrushPickerView().onStrokeCapRoundView()
				.check(matches(isSelected()));

		onToolProperties()
				.checkCap(Cap.ROUND)
				.checkStrokeWidth(eraserStrokeWidth);
	}

	@Test
	public void testColorPickerIcon() {
		onTopBarView().onPaletteButton()
				.check(matches(isDisplayed()));
		onToolBarView()
				.performSelectTool(ToolType.ERASER);
		onTopBarView().onPaletteButton()
				.check(matches(isDisplayed()));
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
		onTopBarView().onPaletteButton()
				.check(matches(isDisplayed()));
	}
}
