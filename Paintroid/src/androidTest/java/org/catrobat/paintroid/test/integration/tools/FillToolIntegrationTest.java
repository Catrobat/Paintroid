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

package org.catrobat.paintroid.test.integration.tools;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.net.Uri;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TableRow;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.tools.implementation.FillTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.Before;

import java.io.File;

public class FillToolIntegrationTest extends BaseIntegrationTestClass {

	public FillToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
		resetBrush();
	}

	public void testFloodFillIfImageLoaded() throws InterruptedException, SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		PaintroidApplication.savedPictureUri = Uri.fromFile(new File("dummy"));

		selectTool(ToolType.FILL);

		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();

		PointF screenPoint = new PointF(mScreenWidth / 2 - 10, mScreenHeight / 2 - 5);

		PointF checkScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PointF checkCanvasPoint = Utils.getCanvasPointFromScreenPoint(checkScreenPoint);

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.waitForDialogToClose(TIMEOUT);
		mSolo.sleep(SHORT_SLEEP);
		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertEquals("Pixel color should be the same.", colorToFill, colorAfterFill);

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		assertTrue("Fill timed out", mSolo.waitForDialogToClose(TIMEOUT));
		PaintroidApplication.savedPictureUri = null;

	}

	public void testNoFloodFillIfEmpty() throws InterruptedException, SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		selectTool(ToolType.FILL);

		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();

		PointF screenPoint = new PointF(mScreenWidth / 2 - 100, mScreenHeight / 2 - 50);

		PointF checkScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PointF checkCanvasPoint = Utils.getCanvasPointFromScreenPoint(checkScreenPoint);

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y); // to fill the bitmap
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);
		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterFill);
	}

	public void testBitmapIsFilled() throws InterruptedException, SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		selectTool(ToolType.FILL);

		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();

		PointF screenPoint = new PointF(mScreenWidth / 2 - 100, mScreenHeight / 2 - 50);

		PointF checkScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PointF checkCanvasPoint = Utils.getCanvasPointFromScreenPoint(checkScreenPoint);

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y); // to fill the bitmap
		mSolo.sleep(SHORT_SLEEP);
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);
		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);

		assertEquals("Pixel color should be the same", colorToFill, colorAfterFill);
	}

	public void testNothingHappensWhenClickedOutsideDrawingArea() throws InterruptedException, SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		selectTool(ToolType.FILL);

		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();

		PointF outsideScreenPoint = new PointF(10, mScreenHeight / 2);
		PointF outsideCanvasPoint = Utils.getCanvasPointFromScreenPoint(outsideScreenPoint);

		PointF insideScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PointF insideCanvasPoint = Utils.getCanvasPointFromScreenPoint(insideScreenPoint);

		mSolo.clickOnScreen(outsideScreenPoint.x, outsideScreenPoint.y);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);

		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(insideCanvasPoint);
		assertFalse("Pixel color should not be the same", (colorToFill == colorAfterFill));
		colorAfterFill = PaintroidApplication.drawingSurface.getPixel(outsideCanvasPoint);
		assertFalse("Pixel color should not be the same", (colorToFill == colorAfterFill));
	}

	public void testOnlyFillInnerArea() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		assertEquals("BrushTool should be selected", ToolType.BRUSH, PaintroidApplication.currentTool.getToolType());
		int colorToDrawBorder = PaintroidApplication.currentTool.getDrawPaint().getColor();

		PointF clickScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);

		PointF checkScreenPoint = new PointF(mScreenWidth / 2 - 50, mScreenHeight / 2);
		PointF checkCanvasPoint = Utils.getCanvasPointFromScreenPoint(checkScreenPoint);

		PointF checkOutsideScreenPoint = new PointF(mScreenWidth / 2 - 100, mScreenHeight / 2 - 100);
		PointF checkOutsideCanvasPoint = Utils.getCanvasPointFromScreenPoint(checkOutsideScreenPoint);

		int checkPointStartColor = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertFalse(colorToDrawBorder == checkPointStartColor);

		PointF leftPointOnScreen = new PointF(clickScreenPoint.x - 100, clickScreenPoint.y);
		PointF upperPointOnScreen = new PointF(clickScreenPoint.x, clickScreenPoint.y - 100);
		PointF rightPointOnScreen = new PointF(clickScreenPoint.x + 100, clickScreenPoint.y);
		PointF bottomPointOnScreen = new PointF(clickScreenPoint.x, clickScreenPoint.y + 100);

		mSolo.drag(leftPointOnScreen.x, upperPointOnScreen.x, leftPointOnScreen.y, upperPointOnScreen.y, 1);
		mSolo.sleep(SHORT_SLEEP);
		mSolo.drag(upperPointOnScreen.x, rightPointOnScreen.x, upperPointOnScreen.y, rightPointOnScreen.y, 1);
		mSolo.sleep(SHORT_SLEEP);
		mSolo.drag(rightPointOnScreen.x, bottomPointOnScreen.x, rightPointOnScreen.y, bottomPointOnScreen.y, 1);
		mSolo.sleep(SHORT_SLEEP);
		mSolo.drag(bottomPointOnScreen.x, leftPointOnScreen.x, bottomPointOnScreen.y, leftPointOnScreen.y, 1);
		mSolo.sleep(SHORT_SLEEP);

		selectTool(ToolType.FILL);
		openColorChooserDialog();
		Button colorButton = mSolo.getButton(5);
		assertTrue(colorButton.getParent() instanceof TableRow);
		mSolo.clickOnButton(5);
		mSolo.sleep(SHORT_SLEEP);
		mSolo.clickOnButton(getActivity().getResources().getString(R.string.done));
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);

		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertFalse(colorToDrawBorder == colorToFill);
		assertFalse(checkPointStartColor == colorToFill);

		mSolo.clickOnScreen(clickScreenPoint.x, clickScreenPoint.y);
		mSolo.sleep(SHORT_SLEEP);
		mSolo.waitForDialogToClose(TIMEOUT);

		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterFill);

		int outsideColorAfterFill = PaintroidApplication.drawingSurface.getPixel(checkOutsideCanvasPoint);
		assertNotSame("Pixel color should be different", colorToFill, outsideColorAfterFill);
	}

	public void testFillToolOptionsDialog() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		selectTool(ToolType.FILL);
		FillTool fillTool = (FillTool) PaintroidApplication.currentTool;
		assertEquals("Wrong fill tool member value for color tolerance",
				fillTool.getToleranceAbsoluteValue(fillTool.DEFAULT_TOLERANCE_IN_PERCENT), getToolMemberColorTolerance(fillTool));

		openToolOptionsForCurrentTool(ToolType.FILL);

		EditText colorToleranceEditText = (EditText) mSolo.getView(R.id.fill_tool_dialog_color_tolerance_input);
		SeekBar colorToleranceSeekBar = (SeekBar) mSolo.getView(R.id.color_tolerance_seek_bar);
		assertEquals("Default color tolerance should be " + String.valueOf(fillTool.DEFAULT_TOLERANCE_IN_PERCENT),
				String.valueOf(fillTool.DEFAULT_TOLERANCE_IN_PERCENT), colorToleranceEditText.getText().toString());

		String testToleranceText = "100";
		getInstrumentation().sendStringSync(testToleranceText);
		assertEquals("Wrong value for tolerance text after keyboard input", testToleranceText, colorToleranceEditText.getText().toString());
		assertEquals("Wrong seek bar position after keyboard input", 100, colorToleranceSeekBar.getProgress());
		float expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(100);
		assertEquals("Wrong fill tool member value for color tolerance", expectedAbsoluteTolerance, getToolMemberColorTolerance(fillTool));

		int seekBarTestValue = 50;
		colorToleranceSeekBar.setProgress(seekBarTestValue);
		mSolo.sleep(SHORT_SLEEP);
		assertEquals("Wrong seek bar position", seekBarTestValue, colorToleranceSeekBar.getProgress());
		assertEquals("Wrong tolerance text after seek bar change", String.valueOf(seekBarTestValue), colorToleranceEditText.getText().toString());
		expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(50);
		assertEquals("Wrong fill tool member value for color tolerance", expectedAbsoluteTolerance, getToolMemberColorTolerance(fillTool));

		closeToolOptionsForCurrentTool();
	}

	public void testFillToolDialogAfterToolSwitch() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		selectTool(ToolType.FILL);
		FillTool fillTool = (FillTool) PaintroidApplication.currentTool;

		openToolOptionsForCurrentTool(ToolType.FILL);
		EditText colorToleranceEditText = (EditText) mSolo.getView(R.id.fill_tool_dialog_color_tolerance_input);
		SeekBar colorToleranceSeekBar = (SeekBar) mSolo.getView(R.id.color_tolerance_seek_bar);

		int toleranceInPercent = 50;
		colorToleranceSeekBar.setProgress(toleranceInPercent);
		mSolo.sleep(SHORT_SLEEP);
		float expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(toleranceInPercent);
		assertEquals("Wrong fill tool member value for color tolerance", expectedAbsoluteTolerance, getToolMemberColorTolerance(fillTool));

		closeToolOptionsForCurrentTool();
		selectTool(ToolType.BRUSH);

		selectTool(ToolType.FILL);
		assertEquals("Wrong fill tool member value for color tolerance", expectedAbsoluteTolerance, getToolMemberColorTolerance(fillTool));
		openToolOptionsForCurrentTool(ToolType.FILL);
		assertEquals("Wrong seek bar position", toleranceInPercent, colorToleranceSeekBar.getProgress());
		assertEquals("Wrong tolerance text", String.valueOf(toleranceInPercent), colorToleranceEditText.getText().toString());

	}

	public void testFillToolToleranceCursorVisibility() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		selectTool(ToolType.FILL);
		FillTool fillTool = (FillTool) PaintroidApplication.currentTool;

		openToolOptionsForCurrentTool(ToolType.FILL);
		EditText colorToleranceEditText = (EditText) mSolo.getView(R.id.fill_tool_dialog_color_tolerance_input);
		SeekBar colorToleranceSeekBar = (SeekBar) mSolo.getView(R.id.color_tolerance_seek_bar);

		assertFalse("Cursor should not be visible", colorToleranceEditText.isCursorVisible());

		mSolo.clickOnView(colorToleranceEditText);
		mSolo.sleep(SHORT_SLEEP);
		assertTrue("Cursor should be visible", colorToleranceEditText.isCursorVisible());

		int toleranceInPercent = 50;
		colorToleranceSeekBar.setProgress(toleranceInPercent);
		mSolo.sleep(SHORT_SLEEP);
		float expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(toleranceInPercent);
		assertEquals("Wrong fill tool member value for color tolerance", expectedAbsoluteTolerance, getToolMemberColorTolerance(fillTool));

		assertEquals("Cursor should not be visible", false, colorToleranceEditText.isCursorVisible());

		closeToolOptionsForCurrentTool();
	}

	public void testFillToolUndoRedoWithTolerance() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		PointF screenPoint = new PointF(mScreenWidth/2.0f, mScreenHeight/2.0f);
		PointF canvasPoint = Utils.getCanvasPointFromScreenPoint(screenPoint);
		PointF upperLeftPixel = new PointF(0, 0);
		int colorBeforeFill = mCurrentDrawingSurfaceBitmap.getPixel(0, 0);
		int canvasPointColor = Color.argb(0xFF, 0, 0, 0);

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_TIMEOUT);
		assertEquals("Pixel should have been replaced", canvasPointColor, PaintroidApplication.drawingSurface.getPixel(canvasPoint));

		openColorChooserDialog();
		Button colorButton = mSolo.getButton(5);
		assertTrue(colorButton.getParent() instanceof TableRow);
		mSolo.clickOnButton(5);
		mSolo.sleep(SHORT_SLEEP);
		closeColorChooserDialog();
		int fillColor = ((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint")).getColor();

		selectTool(ToolType.FILL);
		openToolOptionsForCurrentTool(ToolType.FILL);
		EditText colorToleranceEditText = (EditText) mSolo.getView(R.id.fill_tool_dialog_color_tolerance_input);
		SeekBar colorToleranceSeekBar = (SeekBar) mSolo.getView(R.id.color_tolerance_seek_bar);

		colorToleranceSeekBar.setProgress(100);
		mSolo.sleep(SHORT_SLEEP);
		assertEquals("Wrong tolerance text", String.valueOf(100), colorToleranceEditText.getText().toString());
		closeToolOptionsForCurrentTool();

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		assertTrue("Progress dialog did not close", mSolo.waitForDialogToClose(20000));
		assertEquals("Pixel should have been replaced", fillColor, PaintroidApplication.drawingSurface.getPixel(upperLeftPixel));
		assertEquals("Pixel should have been replaced", fillColor, PaintroidApplication.drawingSurface.getPixel(canvasPoint));

		ImageButton undoButton = (ImageButton) getActivity().findViewById(R.id.btn_top_undo);
		mSolo.clickOnView(undoButton);
		assertTrue("Progress dialog did not close", mSolo.waitForDialogToClose(20000));
		assertEquals("Wrong pixel color after undo", colorBeforeFill, PaintroidApplication.drawingSurface.getPixel(upperLeftPixel));
		assertEquals("Wrong pixel color after undo", canvasPointColor, PaintroidApplication.drawingSurface.getPixel(canvasPoint));

		ImageButton redoButton = (ImageButton) getActivity().findViewById(R.id.btn_top_redo);
		mSolo.clickOnView(redoButton);
		assertTrue("Progress dialog did not close", mSolo.waitForDialogToClose(20000));
		assertEquals("Wrong pixel color after redo", fillColor, PaintroidApplication.drawingSurface.getPixel(upperLeftPixel));
		assertEquals("Wrong pixel color after redo", fillColor, PaintroidApplication.drawingSurface.getPixel(canvasPoint));
	}


	protected float getToolMemberColorTolerance(FillTool fillTool) throws NoSuchFieldException, IllegalAccessException {
		return (Float) PrivateAccess.getMemberValue(FillTool.class, fillTool, "mColorTolerance");
	}

}
