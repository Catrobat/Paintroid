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

package org.catrobat.paintroid.test.espresso.util;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewInteraction;
import android.view.View;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.tools.implementation.FillTool;
import org.catrobat.paintroid.ui.Perspective;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.selectViewPagerPage;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.isToast;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;

public final class EspressoUtils {
	public static final int DEFAULT_STROKE_WIDTH = 25;

	public static final int GREEN_COLOR_PICKER_BUTTON_POSITION = 2;
	public static final int BROWN2_COLOR_PICKER_BUTTON_POSITION = 5;
	public static final int BLACK_COLOR_PICKER_BUTTON_POSITION = 16;
	public static final int WHITE_COLOR_PICKER_BUTTON_POSITION = 18;
	public static final int TRANSPARENT_COLOR_PICKER_BUTTON_POSITION = 19;

	private EspressoUtils() {
		throw new IllegalArgumentException();
	}

	public static float getActionbarHeight() {
		return Utils.getActionbarHeight();
	}

	public static float getStatusbarHeight() {
		return Utils.getStatusbarHeight();
	}

	public static PointF getSurfacePointFromScreenPoint(PointF screenPoint) {
		return Utils.getSurfacePointFromScreenPoint(screenPoint);
	}

	public static PointF getCanvasPointFromScreenPoint(PointF screenPoint) {
		return Utils.getCanvasPointFromScreenPoint(screenPoint);
	}

	public static PointF getCanvasPointFromSurfacePoint(PointF surfacePoint) {
		return PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(surfacePoint);
	}

	public static PointF convertFromCanvasToScreen(PointF canvasPoint, Perspective currentPerspective) {
		Point screenPoint = Utils.convertFromCanvasToScreen(new Point((int) canvasPoint.x, (int) canvasPoint.y), currentPerspective);
		return new PointF(screenPoint.x, screenPoint.y);
	}

	public static PointF getScreenPointFromSurfaceCoordinates(float pointX, float pointY) {
		return new PointF(pointX, pointY + getStatusbarHeight() + getActionbarHeight());
	}

	public static void resetDrawPaintAndBrushPickerView() {
		BaseTool.reset();
	}

	/**
	 * @deprecated use {@link ToolBarViewInteraction#performSelectTool(ToolType)}
	 */
	@Deprecated
	public static void selectTool(ToolType toolType) {
		onToolBarView()
				.performSelectTool(toolType);
	}

	public static void waitForToast(Matcher<View> viewMatcher, int duration) {
		final long waitTime = System.currentTimeMillis() + duration;
		final ViewInteraction viewInteraction = onView(viewMatcher).inRoot(isToast());

		while (System.currentTimeMillis() < waitTime) {
			try {
				viewInteraction.check(matches(isDisplayed()));
				return;
			} catch (NoMatchingViewException e) {
				waitMillis(250);
			}
		}

		viewInteraction.check(matches(isDisplayed()));
	}

	public static float getSurfaceWidth() {
		return PaintroidApplication.perspective.surfaceWidth;
	}

	public static float getSurfaceHeight() {
		return PaintroidApplication.perspective.surfaceHeight;
	}

	public static Bitmap getWorkingBitmap() {
		return PaintroidApplication.layerModel.getCurrentLayer().getBitmap();
	}

	public static Paint getCurrentToolPaint() {
		return BaseTool.CANVAS_PAINT;
	}

	public static float getToolMemberColorTolerance(FillTool fillTool) {
		return fillTool.colorTolerance;
	}

	public static float getSurfaceCenterX() {
		return PaintroidApplication.perspective.surfaceCenterX;
	}

	public static float getSurfaceCenterY() {
		return PaintroidApplication.perspective.surfaceCenterY;
	}

	/**
	 * @deprecated use {@link ToolBarViewInteraction#performOpenToolOptions()}
	 */
	@Deprecated
	public static void openToolOptionsForCurrentTool() {
		clickSelectedToolButton();
	}

	/**
	 * @deprecated use {@link ToolBarViewInteraction#performClickSelectedToolButton()}}
	 */
	@Deprecated
	public static void clickSelectedToolButton() {
		onToolBarView()
				.performClickSelectedToolButton();
	}

	public static void waitMillis(final long millis) {
		onView(isRoot()).perform(UiInteractions.waitFor(millis));
	}

	/**
	 * @deprecated avoid, set color directly (e.g. {@link BaseTool#reset()})
	 */
	@Deprecated
	public static void selectColorPickerPresetSelectorColor(final int buttonPosition) {
		onColorPickerView()
				.performOpenColorPicker()
				.performClickColorPickerPresetSelectorButton(buttonPosition)
				.performCloseColorPickerWithDialogButton();
	}

	/**
	 * @deprecated avoid, set color directly (e.g. {@link BaseTool#reset()})
	 */
	@Deprecated
	public static void resetColorPicker() {
		selectColorPickerPresetSelectorColor(BLACK_COLOR_PICKER_BUTTON_POSITION);
	}

	public static void changeIntroPage(int page) {
		onView(withId(R.id.pocketpaint_view_pager)).perform(selectViewPagerPage(page));
	}
}
