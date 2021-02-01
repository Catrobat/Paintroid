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
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.DEFAULT_STROKE_WIDTH;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.setProgress;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withProgress;
import static org.catrobat.paintroid.test.espresso.util.wrappers.BrushPickerViewInteraction.onBrushPickerView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.hamcrest.Matchers.allOf;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EraserToolIntegrationTest {

	private static final String TEXT_DEFAULT_STROKE_WIDTH = Integer.toString(DEFAULT_STROKE_WIDTH);

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Test
	public void testEraseOnEmptyBitmap() {
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
	public void testEraseSinglePixel() {
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
		int newStrokeWidth = 90;
		onBrushPickerView().onStrokeWidthSeekBar()
				.perform(setProgress(newStrokeWidth))
				.check(matches(withProgress(newStrokeWidth)));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onToolBarView()
				.performSelectTool(ToolType.ERASER)
				.performOpenToolOptionsView();

		onBrushPickerView().onStrokeWidthSeekBar()
				.check(matches(allOf(isDisplayed(), withProgress(newStrokeWidth))));
		onBrushPickerView().onStrokeWidthTextView()
				.check(matches(allOf(isDisplayed(), withText(Integer.toString(newStrokeWidth)))));

		newStrokeWidth = 80;
		onBrushPickerView().onStrokeWidthSeekBar()
				.perform(setProgress(newStrokeWidth))
				.check(matches(withProgress(newStrokeWidth)));

		onToolBarView()
				.performCloseToolOptionsView();

		onToolProperties()
				.checkStrokeWidth(80);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testChangeEraserBrushForm() {
		onBrushPickerView()
				.onStrokeWidthSeekBar()
				.perform(setProgress(70));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onBrushPickerView()
				.onStrokeWidthSeekBar()
				.perform(setProgress(50));

		onToolBarView()
				.performSelectTool(ToolType.ERASER)
				.performOpenToolOptionsView();

		onBrushPickerView().onStrokeCapSquareView()
				.perform(click());

		onToolBarView()
				.performCloseToolOptionsView();

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
				.performOpenToolOptionsView();

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
				.performCloseToolOptionsView()
				.performOpenToolOptionsView();

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
				.performOpenToolOptionsView();

		onBrushPickerView().onStrokeWidthSeekBar()
				.check(matches(withProgress(eraserStrokeWidth)));
		onBrushPickerView().onStrokeCapRoundView()
				.check(matches(isSelected()));

		onToolProperties()
				.checkCap(Cap.ROUND)
				.checkStrokeWidth(eraserStrokeWidth);
	}
}
