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
import android.graphics.Point;
import android.graphics.PointF;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.test.espresso.util.DialogHiddenIdlingResource;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.BLACK_COLOR_PICKER_BUTTON_POSITION;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.TRANSPARENT_COLOR_PICKER_BUTTON_POSITION;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.clickSelectedToolButton;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getWorkingBitmap;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetColorPicker;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetDrawPaintAndBrushPickerView;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectColorPickerPresetSelectorColor;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RectangleFillToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private IdlingResource dialogWait;

	private Bitmap workingBitmap;

	@Before
	public void setUp() {
		dialogWait = new DialogHiddenIdlingResource(IndeterminateProgressDialog.getInstance());
		Espresso.registerIdlingResources(dialogWait);

		PaintroidApplication.drawingSurface.destroyDrawingCache();

		workingBitmap = getWorkingBitmap();

		selectTool(ToolType.BRUSH);
		resetColorPicker();
		resetDrawPaintAndBrushPickerView();
	}

	@After
	public void tearDown() {
		Espresso.unregisterIdlingResources(dialogWait);

		if (workingBitmap != null && !workingBitmap.isRecycled()) {
			workingBitmap.recycle();
		}
	}

	@Test
	public void testFilledRectIsCreated() {
		selectTool(ToolType.SHAPE);

		BaseToolWithRectangleShape rectangleFillTool = (BaseToolWithRectangleShape) PaintroidApplication.currentTool;
		float rectWidth = rectangleFillTool.boxWidth;
		float rectHeight = rectangleFillTool.boxHeight;
		PointF rectPosition = rectangleFillTool.toolPosition;

		assertNotEquals("Width should not be zero", rectWidth, 0.0f);
		assertNotEquals("Height should not be zero", rectHeight, 0.0f);
		assertNotNull("Position should not be NULL", rectPosition);
	}

	/**
	 * Fails if whole espresso tests run, there lives an artifact in drawing surface:
	 * AssertionError: expected:<0> but was:<-16777216>
	 */
	@Ignore
	@Test
	public void testEllipseIsDrawnOnBitmap() {

		PaintroidApplication.perspective.setScale(1.0f);

		selectTool(ToolType.SHAPE);

		onView(withId(R.id.shapes_circle_btn)).perform(click());

		BaseToolWithRectangleShape ellipseTool = (BaseToolWithRectangleShape) PaintroidApplication.currentTool;
		PointF centerPointTool = ellipseTool.toolPosition;
		float rectHeight = ellipseTool.boxHeight;

		PointF pointUnderTest = new PointF(centerPointTool.x, centerPointTool.y);
		int colorBeforeDrawing = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);

		clickSelectedToolButton();

		onView(isRoot()).perform(touchAt(centerPointTool.x - 1, centerPointTool.y - 1));

		pressBack();

		int colorPickerColor = PaintroidApplication.currentTool.getDrawPaint().getColor();

		int colorAfterDrawing = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);

		assertEquals("Pixel should have the same color as currently in color picker", colorPickerColor, colorAfterDrawing);

		onView(withId(R.id.btn_top_undo)).perform(click());

		int colorAfterUndo = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);
		assertEquals(colorBeforeDrawing, colorAfterUndo);

		onView(withId(R.id.btn_top_redo)).perform(click());

		int colorAfterRedo = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);
		assertEquals(colorPickerColor, colorAfterRedo);

		pointUnderTest.x = centerPointTool.x + (rectHeight / 2.5f);
		colorAfterDrawing = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);
		assertEquals("Pixel should have the same color as currently in color picker", colorPickerColor, colorAfterDrawing);

		pointUnderTest.y = centerPointTool.y + (rectHeight / 2.5f);
		// now the point under test is diagonal from the center -> if its a circle there should be no color
		colorAfterDrawing = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);
		assertTrue("Pixel should not have been filled for a circle", (colorPickerColor != colorAfterDrawing));
		PaintroidApplication.commandManager.resetAndClear(true);
	}

	@Test
	public void testRectOnBitmapHasSameColorAsInColorPickerAfterColorChange() {
		int colorPickerColorBeforeChange = PaintroidApplication.currentTool.getDrawPaint().getColor();

		final int colorButtonPosition = 5;
		selectColorPickerPresetSelectorColor(colorButtonPosition);

		int colorPickerColorAfterChange = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertNotEquals("Colors should not be the same", colorPickerColorAfterChange, colorPickerColorBeforeChange);

		selectTool(ToolType.SHAPE);

		int colorInRectangleTool = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Colors should be the same", colorPickerColorAfterChange, colorInRectangleTool);

		BaseToolWithRectangleShape rectangleFillTool = (BaseToolWithRectangleShape) PaintroidApplication.currentTool;

		float rectWidth = rectangleFillTool.boxWidth;
		float rectHeight = rectangleFillTool.boxHeight;
		Bitmap drawingBitmap = rectangleFillTool.drawingBitmap;

		int colorInRectangle = drawingBitmap.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		assertEquals("Colors should be the same", colorPickerColorAfterChange, colorInRectangle);
	}

	@Test
	public void testFilledRectChangesColor() {
		selectTool(ToolType.SHAPE);

		BaseToolWithRectangleShape rectangleFillTool = (BaseToolWithRectangleShape) PaintroidApplication.currentTool;

		int colorInRectangleTool = rectangleFillTool.getDrawPaint().getColor();

		float rectWidth = rectangleFillTool.boxWidth;
		float rectHeight = rectangleFillTool.boxHeight;
		Bitmap drawingBitmap = rectangleFillTool.drawingBitmap;

		int colorInRectangle = drawingBitmap.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		assertEquals("Colors should be equal", colorInRectangleTool, colorInRectangle);

		final int colorButtonPosition = 5;
		selectColorPickerPresetSelectorColor(colorButtonPosition);

		int colorInRectangleToolAfter = rectangleFillTool.getDrawPaint().getColor();

		Bitmap drawingBitmapAfter = rectangleFillTool.drawingBitmap;

		int colorInRectangleAfter = drawingBitmapAfter.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));

		assertNotEquals("Colors should have changed", colorInRectangle, colorInRectangleAfter);
		assertNotEquals("Colors should have changed", colorInRectangleTool, colorInRectangleToolAfter);
		assertEquals("Colors should be equal", colorInRectangleTool, colorInRectangle);
	}

	@Test
	public void testEraseWithEllipse() {
		selectTool(ToolType.SHAPE);
		selectShapeTypeAndDraw(R.id.shapes_square_btn, false, TRANSPARENT_COLOR_PICKER_BUTTON_POSITION);

		clickSelectedToolButton();

		selectShapeTypeAndDraw(R.id.shapes_circle_btn, true, TRANSPARENT_COLOR_PICKER_BUTTON_POSITION);
	}

	@Test
	public void testDrawWithDrawableShape() {
		selectTool(ToolType.SHAPE);
		selectShapeTypeAndDraw(R.id.shapes_heart_btn, false, BLACK_COLOR_PICKER_BUTTON_POSITION);
	}

	@Test
	public void testCheckeredBackgroundWhenTransparentColorSelected() {
		selectTool(ToolType.SHAPE);

		onView(withId(R.id.shapes_heart_btn)).perform(click());

		clickSelectedToolButton();

		selectColorPickerPresetSelectorColor(TRANSPARENT_COLOR_PICKER_BUTTON_POSITION);

		BaseToolWithRectangleShape tool = (BaseToolWithRectangleShape) PaintroidApplication.currentTool;
		Bitmap drawingBitmap = tool.drawingBitmap;
		int width = drawingBitmap.getWidth();
		int height = drawingBitmap.getHeight();
		Point upperLeftQuarter = new Point((int) (width * 0.25), (int) (height * 0.25));
		Point upperRightQuarter = new Point((int) (width * 0.75), (int) (height * 0.25));
		Point lowerRightQuarter = new Point((int) (width * 0.75), (int) (height * 0.75));
		Point lowerLeftQuarter = new Point((int) (width * 0.25), (int) (height * 0.75));

		int checkeredWhite = Color.WHITE;
		int checkeredGray = 0xFFC0C0C0;

		int pixelColor = drawingBitmap.getPixel(upperLeftQuarter.x, upperLeftQuarter.y);
		assertTrue("Color should correspond to checkered pattern", pixelColor == checkeredGray || pixelColor == checkeredWhite);

		pixelColor = drawingBitmap.getPixel(upperRightQuarter.x, upperRightQuarter.y);
		assertTrue("Color should correspond to checkered pattern", pixelColor == checkeredGray || pixelColor == checkeredWhite);

		pixelColor = drawingBitmap.getPixel(lowerRightQuarter.x, lowerRightQuarter.y);
		assertEquals("Pixel should be transparent", Color.TRANSPARENT, pixelColor);

		pixelColor = drawingBitmap.getPixel(lowerLeftQuarter.x, lowerLeftQuarter.y);
		assertEquals("Pixel should be transparent", Color.TRANSPARENT, pixelColor);
	}

	@Test
	public void testEraseWithHeartShape() {
		PaintroidApplication.perspective.setScale(1.0f);

		selectTool(ToolType.SHAPE);
		BaseToolWithRectangleShape tool = (BaseToolWithRectangleShape) PaintroidApplication.currentTool;
		selectShapeTypeAndDraw(R.id.shapes_square_btn, true, BLACK_COLOR_PICKER_BUTTON_POSITION);
		int backgroundColor = tool.getDrawPaint().getColor();

		clickSelectedToolButton();
		selectShapeTypeAndDraw(R.id.shapes_heart_btn, true, TRANSPARENT_COLOR_PICKER_BUTTON_POSITION);

		Bitmap drawingBitmap = tool.drawingBitmap;
		int boxWidth = drawingBitmap.getWidth();
		int boxHeight = drawingBitmap.getHeight();
		PointF toolPosition = tool.toolPosition;

		Point upperLeftPixel = new Point((int) (toolPosition.x - boxWidth / 4), (int) (toolPosition.y - boxHeight / 4));
		Point upperRightPixel = new Point((int) (toolPosition.x + boxWidth / 4), (int) (toolPosition.y - boxHeight / 4));
		Point lowerRightPixel = new Point((int) (toolPosition.x + boxWidth / 4), (int) (toolPosition.y + boxHeight / 4));
		Point lowerLeftPixel = new Point((int) (toolPosition.x - boxWidth / 4), (int) (toolPosition.y + boxHeight / 4));

		Bitmap bitmap = PaintroidApplication.drawingSurface.getBitmapCopy();

		int pixelColor = bitmap.getPixel(upperLeftPixel.x, upperLeftPixel.y);
		assertEquals("Pixel should have been erased", Color.TRANSPARENT, pixelColor);

		pixelColor = bitmap.getPixel(upperRightPixel.x, upperRightPixel.y);
		assertEquals("Pixel should have been erased", Color.TRANSPARENT, pixelColor);

		pixelColor = bitmap.getPixel(lowerRightPixel.x, lowerRightPixel.y);
		assertEquals("Pixel should not have been erased", backgroundColor, pixelColor);

		pixelColor = bitmap.getPixel(lowerLeftPixel.x, lowerLeftPixel.y);
		assertEquals("Pixel should not have been erased", backgroundColor, pixelColor);
	}

	public void selectShapeTypeAndDraw(int shapeBtnId, boolean changeColor, int colorButtonPosition) {
		onView(withId(shapeBtnId)).perform(click());

		BaseToolWithRectangleShape tool = (BaseToolWithRectangleShape) PaintroidApplication.currentTool;
		PointF centerPointTool = tool.toolPosition;

		PointF pointUnderTest = new PointF(centerPointTool.x, centerPointTool.y);

		clickSelectedToolButton();

		if (changeColor) {
			selectColorPickerPresetSelectorColor(colorButtonPosition);
		}

		float rectWidth = tool.boxWidth;
		float rectHeight = tool.boxHeight;
		Bitmap drawingBitmap = tool.drawingBitmap;

		int colorInRectangleTool = tool.getDrawPaint().getColor();
		int colorInRectangle = drawingBitmap.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		if (Color.alpha(colorInRectangleTool) == 0x00) {
			int checkeredWhite = Color.WHITE;
			int checkeredGray = 0xFFC0C0C0;
			assertTrue("Color should correspond to checkered pattern", colorInRectangle == checkeredGray || colorInRectangle == checkeredWhite);
		} else {
			assertEquals("Colors should be equal", colorInRectangleTool, colorInRectangle);
		}

		onView(isRoot()).perform(touchAt(centerPointTool.x - 1, centerPointTool.y - 1));

		int colorPickerColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		int colorAfterDrawing = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);
		assertEquals("Pixel should have the same color as currently in color picker", colorPickerColor, colorAfterDrawing);
	}
}
