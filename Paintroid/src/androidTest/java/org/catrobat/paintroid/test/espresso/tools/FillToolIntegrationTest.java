/**
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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.espresso.util.DialogHiddenIdlingResource;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.FillTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.clickSelectedToolButton;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getCanvasPointFromScreenPoint;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getToolMemberColorTolerance;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getWorkingBitmap;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openToolOptionsForCurrentTool;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetColorPicker;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectColorPickerPresetSelectorColor;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.setProgress;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.swipe;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withProgress;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * TODO: fix ignored tests, they work if executed as standalone tests.
 */
@RunWith(AndroidJUnit4.class)
public class FillToolIntegrationTest {

	private static final double TOLERANCE_DELTA = 0.05d;

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private ActivityHelper activityHelper;

	private int displayWidth;
	private int displayHeight;

	private Bitmap workingBitmap;

	private IdlingResource dialogWait;

	private void resetFillTool() {
		openToolOptionsForCurrentTool();
		final ViewInteraction colorToleranceSeekBar = onView(withId(R.id.color_tolerance_seek_bar));
		colorToleranceSeekBar.perform(setProgress(FillTool.DEFAULT_TOLERANCE_IN_PERCENT));
		clickSelectedToolButton();
	}

	@Before
	public void setUp() throws NoSuchFieldException, IllegalAccessException {
		dialogWait = new DialogHiddenIdlingResource(IndeterminateProgressDialog.getInstance());
		Espresso.registerIdlingResources(dialogWait);

		activityHelper = new ActivityHelper(launchActivityRule.getActivity());

		PaintroidApplication.drawingSurface.destroyDrawingCache();

		displayWidth  = activityHelper.getDisplayWidth();
		displayHeight = activityHelper.getDisplayHeight();

		workingBitmap = getWorkingBitmap();

		selectTool(ToolType.FILL);
		resetColorPicker();
		resetFillTool();
	}

	@After
	public void tearDown() {
		Espresso.unregisterIdlingResources(dialogWait);

		activityHelper = null;
		displayWidth = 0;
		displayHeight = 0;

		if(workingBitmap != null && !workingBitmap.isRecycled()) {
			workingBitmap.recycle();
		}

		workingBitmap = null;
	}

	@Test
	@Ignore
	public void testFloodFillIfImageLoaded() {
		PaintroidApplication.savedPictureUri = Uri.fromFile(new File("dummy"));

		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();

		PointF screenPoint = new PointF(displayWidth / 2 - 10, displayHeight / 2 - 5);

		PointF checkScreenPoint = new PointF(displayWidth / 2, displayHeight / 2);
		PointF checkCanvasPoint = getCanvasPointFromScreenPoint(checkScreenPoint);

		onView(isRoot()).perform(touchAt(screenPoint));

		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertEquals("Pixel color should be the same.", colorToFill, colorAfterFill);

		PaintroidApplication.savedPictureUri = null;
	}

	@Test
	@Ignore
	public void testNoFloodFillIfEmpty() {
		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();

		PointF screenPoint = new PointF(displayWidth / 2 - 100, displayHeight / 2 - 50);

		PointF checkScreenPoint = new PointF(displayWidth / 2, displayHeight / 2);
		PointF checkCanvasPoint = getCanvasPointFromScreenPoint(checkScreenPoint);

		onView(isRoot()).perform(touchAt(screenPoint));

		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterFill);
	}

	@Test
	@Ignore
	public void testBitmapIsFilled() {
		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();

		PointF screenPoint = new PointF(displayWidth / 2 - 100, displayHeight / 2 - 50);

		PointF checkScreenPoint = new PointF(displayWidth / 2, displayHeight / 2);
		PointF checkCanvasPoint = getCanvasPointFromScreenPoint(checkScreenPoint);

		onView(isRoot()).perform(touchAt(screenPoint));

		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterFill);
	}

	@Test
	public void testNothingHappensWhenClickedOutsideDrawingArea() {
		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();

		PointF outsideScreenPoint = new PointF(10, displayHeight / 2);
		PointF outsideCanvasPoint = Utils.getCanvasPointFromScreenPoint(outsideScreenPoint);

		PointF insideScreenPoint = new PointF(displayWidth / 2, displayHeight / 2);
		PointF insideCanvasPoint = Utils.getCanvasPointFromScreenPoint(insideScreenPoint);

		onView(isRoot()).perform(touchAt(outsideScreenPoint));

		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(insideCanvasPoint);
		assertNotEquals("Pixel inside color should not be the same", colorToFill, colorAfterFill);

		colorAfterFill = PaintroidApplication.drawingSurface.getPixel(outsideCanvasPoint);
		assertNotEquals("Pixel outside color should not be the same", colorToFill, colorAfterFill);
	}

	@Test
	@Ignore
	public void testOnlyFillInnerArea() {
		selectTool(ToolType.BRUSH);

		int colorToDrawBorder = PaintroidApplication.currentTool.getDrawPaint().getColor();

		PointF clickScreenPoint = new PointF(displayWidth / 2, displayHeight / 2);

		PointF checkScreenPoint = new PointF(displayWidth / 2 - 50, displayHeight / 2);
		PointF checkCanvasPoint = getCanvasPointFromScreenPoint(checkScreenPoint);

		PointF checkOutsideScreenPoint = new PointF(displayWidth / 2 - 100, displayHeight / 2 - 100);
		PointF checkOutsideCanvasPoint = getCanvasPointFromScreenPoint(checkOutsideScreenPoint);

		int checkPointStartColor = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertNotEquals("Color to fill and pixel color should not be the same", checkPointStartColor, colorToDrawBorder);

		// Border endpoints
		PointF leftPointOnScreen = new PointF(clickScreenPoint.x - 100, clickScreenPoint.y);
		PointF upperPointOnScreen = new PointF(clickScreenPoint.x, clickScreenPoint.y - 100);
		PointF rightPointOnScreen = new PointF(clickScreenPoint.x + 100, clickScreenPoint.y);
		PointF bottomPointOnScreen = new PointF(clickScreenPoint.x, clickScreenPoint.y + 100);

		// Draw border
		onView(isRoot()).perform(swipe(leftPointOnScreen, upperPointOnScreen));
		onView(isRoot()).perform(swipe(upperPointOnScreen, rightPointOnScreen));
		onView(isRoot()).perform(swipe(rightPointOnScreen, bottomPointOnScreen));
		onView(isRoot()).perform(swipe(bottomPointOnScreen, leftPointOnScreen));

		selectTool(ToolType.FILL);

		final int buttonPosition = 5;
		selectColorPickerPresetSelectorColor(buttonPosition);

		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();

		assertNotEquals("Color to fill should not be the same as border color", colorToDrawBorder, colorToFill);
		assertNotEquals("Color to fill should not be the same as pixel start color", checkPointStartColor, colorToFill);

		onView(isRoot()).perform(touchAt(clickScreenPoint));

		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterFill);

		int outsideColorAfterFill = PaintroidApplication.drawingSurface.getPixel(checkOutsideCanvasPoint);
		assertNotEquals("Pixel color should be different", colorToFill, outsideColorAfterFill);
	}

	@Test
	public void testFillToolOptionsDialog() throws NoSuchFieldException, IllegalAccessException {
		FillTool fillTool = (FillTool) PaintroidApplication.currentTool;
		assertEquals(
			"Wrong fill tool member value for color tolerance",
			fillTool.getToleranceAbsoluteValue(FillTool.DEFAULT_TOLERANCE_IN_PERCENT),
			getToolMemberColorTolerance(fillTool),
				TOLERANCE_DELTA
		);

		openToolOptionsForCurrentTool();

		final ViewInteraction colorToleranceInput = onView(withId(R.id.fill_tool_dialog_color_tolerance_input));
		final ViewInteraction colorToleranceSeekBar = onView(withId(R.id.color_tolerance_seek_bar));

		String testToleranceText = "100";

		colorToleranceInput.check(matches(withText(Integer.toString(FillTool.DEFAULT_TOLERANCE_IN_PERCENT))));

		colorToleranceInput.perform(typeText(testToleranceText), closeSoftKeyboard());

		colorToleranceInput.check(matches(withText(testToleranceText)));
		colorToleranceSeekBar.check(matches(withProgress(Integer.parseInt(testToleranceText))));

		float expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(100);
		assertEquals("Wrong fill tool member value for color tolerance", expectedAbsoluteTolerance, getToolMemberColorTolerance(fillTool), TOLERANCE_DELTA);

		int seekBarTestValue = 50;
		colorToleranceSeekBar.perform(setProgress(seekBarTestValue));
		colorToleranceSeekBar.check(matches(withProgress(seekBarTestValue)));
		colorToleranceInput.check(matches(withText(Integer.toString(seekBarTestValue))));

		expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(50);
		assertEquals("Wrong fill tool member value for color tolerance", expectedAbsoluteTolerance, getToolMemberColorTolerance(fillTool), TOLERANCE_DELTA);

		// Close tool options
		clickSelectedToolButton();
	}

	@Test
	public void testFillToolDialogAfterToolSwitch() throws NoSuchFieldException, IllegalAccessException {
		FillTool fillTool = (FillTool) PaintroidApplication.currentTool;

		openToolOptionsForCurrentTool();

		final ViewInteraction colorToleranceInput = onView(withId(R.id.fill_tool_dialog_color_tolerance_input));
		final ViewInteraction colorToleranceSeekBar = onView(withId(R.id.color_tolerance_seek_bar));

		int toleranceInPercent = 50;
		float expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(toleranceInPercent);

		colorToleranceSeekBar.perform(setProgress(toleranceInPercent));

		assertEquals("Wrong fill tool member value for color tolerance", expectedAbsoluteTolerance, getToolMemberColorTolerance(fillTool), TOLERANCE_DELTA);

		// Close tool options
		clickSelectedToolButton();

		selectTool(ToolType.BRUSH);

		selectTool(ToolType.FILL);
		openToolOptionsForCurrentTool();

		colorToleranceInput.check(matches(withText(Integer.toString(FillTool.DEFAULT_TOLERANCE_IN_PERCENT))));
		colorToleranceSeekBar.check(matches(withProgress(FillTool.DEFAULT_TOLERANCE_IN_PERCENT)));
	}

	@Test
	public void testFillToolToleranceCursorVisibility() throws NoSuchFieldException, IllegalAccessException {

		FillTool fillTool = (FillTool) PaintroidApplication.currentTool;

		openToolOptionsForCurrentTool();

		final ViewInteraction colorToleranceInput = onView(withId(R.id.fill_tool_dialog_color_tolerance_input));
		final ViewInteraction colorToleranceSeekBar = onView(withId(R.id.color_tolerance_seek_bar));

		EditText colorToleranceEditText = (EditText) activityHelper.findViewById(R.id.fill_tool_dialog_color_tolerance_input);
		assertFalse("Cursor should not be visible", colorToleranceEditText.isCursorVisible());

		colorToleranceInput.perform(click(), closeSoftKeyboard());

		assertTrue("Cursor should not be visible", colorToleranceEditText.isCursorVisible());

		int toleranceInPercent = 50;
		colorToleranceSeekBar.perform(setProgress(toleranceInPercent));
		float expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(toleranceInPercent);
		assertEquals("Wrong fill tool member value for color tolerance", expectedAbsoluteTolerance, getToolMemberColorTolerance(fillTool), TOLERANCE_DELTA);

		assertFalse("Cursor should not be visible", colorToleranceEditText.isCursorVisible());

		// Close tool options
		clickSelectedToolButton();
	}

	@Test
	@Ignore
	public void testFillToolUndoRedoWithTolerance() throws NoSuchFieldException, IllegalAccessException {

		selectTool(ToolType.BRUSH);

		PointF screenPoint = new PointF(displayWidth/2.0f, displayHeight/2.0f);
		PointF canvasPoint = getCanvasPointFromScreenPoint(screenPoint);
		PointF upperLeftPixel = new PointF(0, 0);
		int colorBeforeFill = workingBitmap.getPixel(0, 0);
		int canvasPointColor = Color.argb(0xFF, 0, 0, 0);

		onView(isRoot()).perform(touchAt(screenPoint));

		assertEquals("Pixel should have been replaced", canvasPointColor, PaintroidApplication.drawingSurface.getPixel(canvasPoint));

		final int colorButtonPosition = 5;
		selectColorPickerPresetSelectorColor(colorButtonPosition);

		int fillColor = PaintroidApplication.currentTool.getDrawPaint().getColor();

		selectTool(ToolType.FILL);
		openToolOptionsForCurrentTool();

		final ViewInteraction colorToleranceSeekBar = onView(withId(R.id.color_tolerance_seek_bar));

		final int colorTolerance = 100;
		colorToleranceSeekBar.perform(setProgress(colorTolerance));

		clickSelectedToolButton();

		onView(isRoot()).perform(touchAt(screenPoint));
		assertEquals("Pixel should have been replaced", fillColor, PaintroidApplication.drawingSurface.getPixel(upperLeftPixel));
		assertEquals("Pixel should have been replaced", fillColor, PaintroidApplication.drawingSurface.getPixel(canvasPoint));

		onView(withId(R.id.btn_top_undo)).perform(click());
		assertEquals("Wrong pixel color after undo", colorBeforeFill, PaintroidApplication.drawingSurface.getPixel(upperLeftPixel));
		assertEquals("Wrong pixel color after undo", canvasPointColor, PaintroidApplication.drawingSurface.getPixel(canvasPoint));

		onView(withId(R.id.btn_top_redo)).perform(click());
		assertEquals("Wrong pixel color after redo", fillColor, PaintroidApplication.drawingSurface.getPixel(upperLeftPixel));
		assertEquals("Wrong pixel color after redo", fillColor, PaintroidApplication.drawingSurface.getPixel(canvasPoint));
	}
}
