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
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
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
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.FIELD_NAME_BOX_HEIGHT;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.FIELD_NAME_BOX_WIDTH;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.FIELD_NAME_DRAWING_BITMAP;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.FIELD_NAME_TOOL_POSITION;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.clickSelectedToolButton;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getWorkingBitmap;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetColorPicker;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetDrawPaintAndBrushPickerView;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectColorPickerPresetSelectorColor;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.junit.Assert.assertEquals;
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
	public void setUp() throws NoSuchFieldException, IllegalAccessException {
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

		if(workingBitmap != null && !workingBitmap.isRecycled()) {
			workingBitmap.recycle();
		}
	}

	@Test
	public void testFilledRectIsCreated() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		selectTool(ToolType.SHAPE);

		Tool mRectangleFillTool = PaintroidApplication.currentTool;
		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_BOX_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_BOX_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mRectangleFillTool, FIELD_NAME_TOOL_POSITION);

		assertTrue("Width should not be zero", rectWidth != 0.0f);
		assertTrue("Width should not be zero", rectHeight != 0.0f);
		assertNotNull("Position should not be NULL", rectPosition);
	}

	/**
	 * Fails if whole espresso tests run, there lives an artifact in drawing surface:
	 * AssertionError: expected:<0> but was:<-16777216>
	 */
	@Ignore
	@Test
	public void testEllipseIsDrawnOnBitmap() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {

		PaintroidApplication.perspective.setScale(1.0f);

		selectTool(ToolType.SHAPE);

		onView(withId(R.id.shapes_circle_btn)).perform(click());

		Tool ellipseTool = PaintroidApplication.currentTool;
		PointF centerPointTool = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, ellipseTool, FIELD_NAME_TOOL_POSITION);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, ellipseTool, FIELD_NAME_BOX_HEIGHT);

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
	public void testRectOnBitmapHasSameColorAsInColorPickerAfterColorChange() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		int colorPickerColorBeforeChange = PaintroidApplication.currentTool.getDrawPaint().getColor();

		final int colorButtonPosition = 5;
		selectColorPickerPresetSelectorColor(colorButtonPosition);

		int colorPickerColorAfterChange = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertTrue("Colors should not be the same", colorPickerColorAfterChange != colorPickerColorBeforeChange);

		selectTool(ToolType.SHAPE);

		int colorInRectangleTool = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Colors should be the same", colorPickerColorAfterChange, colorInRectangleTool);

		Tool mRectangleFillTool = PaintroidApplication.currentTool;

		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_BOX_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_BOX_HEIGHT);
		Bitmap drawingBitmap = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_DRAWING_BITMAP);

		int colorInRectangle = drawingBitmap.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		assertEquals("Colors should be the same", colorPickerColorAfterChange, colorInRectangle);
	}

	@Test
	public void testFilledRectChangesColor() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		selectTool(ToolType.SHAPE);

		Tool mRectangleFillTool = PaintroidApplication.currentTool;

		int colorInRectangleTool = mRectangleFillTool.getDrawPaint().getColor();

		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_BOX_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_BOX_HEIGHT);
		Bitmap drawingBitmap = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_DRAWING_BITMAP);

		int colorInRectangle = drawingBitmap.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		assertEquals("Colors should be equal", colorInRectangleTool, colorInRectangle);

		final int colorButtonPosition = 5;
		selectColorPickerPresetSelectorColor(colorButtonPosition);

		int colorInRectangleToolAfter = mRectangleFillTool.getDrawPaint().getColor();

		Bitmap drawingBitmapAfter = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_DRAWING_BITMAP);

		int colorInRectangleAfter = drawingBitmapAfter.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));

		assertTrue("Colors should have changed", colorInRectangle != colorInRectangleAfter);
		assertTrue("Colors should have changed", colorInRectangleTool != colorInRectangleToolAfter);
		assertEquals("Colors should be equal", colorInRectangleTool, colorInRectangle);
	}
}
