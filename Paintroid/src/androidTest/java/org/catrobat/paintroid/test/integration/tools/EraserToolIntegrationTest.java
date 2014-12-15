/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
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

import java.util.ArrayList;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.Before;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class EraserToolIntegrationTest extends BaseIntegrationTestClass {

	public EraserToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
	}

	public void testEraseNothing() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		DrawingSurface drawingSurface = (DrawingSurface) getActivity().findViewById(R.id.drawingSurfaceView);

		PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PointF canvasPoint = Utils.getCanvasPointFromScreenPoint(screenPoint);

		int colorBeforeErase = drawingSurface.getPixel(canvasPoint);
		assertEquals("Get transparent background color", Color.TRANSPARENT, colorBeforeErase);

		selectTool(ToolType.ERASER);
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);

		int colorAfterErase = drawingSurface.getPixel(canvasPoint);
		assertEquals("Pixel should still be transparent", Color.TRANSPARENT, colorAfterErase);
	}

	public void testErase() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PointF canvasPoint = Utils.getCanvasPointFromScreenPoint(screenPoint);

		((Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class, PaintroidApplication.drawingSurface,
				"mWorkingBitmap")).eraseColor(Color.BLACK);

		int colorBeforeErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		selectTool(ToolType.ERASER);

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);

		int colorAfterErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Brushing after erase should be transparent", Color.TRANSPARENT, colorAfterErase);
	}

	public void testSwitchingBetweenBrushAndEraser() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PointF canvasPoint = Utils.getCanvasPointFromScreenPoint(screenPoint);

		((Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class, PaintroidApplication.drawingSurface,
				"mWorkingBitmap")).eraseColor(Color.BLACK);

		int colorBeforeErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		selectTool(ToolType.ERASER);

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_TIMEOUT);
		int colorAfterErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After erasing, pixel should be transparent again", Color.TRANSPARENT, colorAfterErase);

		selectTool(ToolType.BRUSH);

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_TIMEOUT);
		int colorAfterBrush = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Brushing after erase should be black again like before erasing", Color.BLACK, colorAfterBrush);

		selectTool(ToolType.ERASER);

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_TIMEOUT);
		colorAfterErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After erasing, pixel should be transparent again", Color.TRANSPARENT, colorAfterErase);
	}

	public void testSwitchingBetweenBrushAndEraserAndMoveTool() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PointF canvasPoint = Utils.getCanvasPointFromScreenPoint(screenPoint);

		((Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class, PaintroidApplication.drawingSurface,
				"mWorkingBitmap")).eraseColor(Color.BLACK);

		int colorBeforeErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		selectTool(ToolType.ERASER);

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);
		int colorAfterErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After erasing, pixel should be transparent again", Color.TRANSPARENT, colorAfterErase);

		mSolo.clickOnView(mButtonTopTool);
		mSolo.sleep(SHORT_SLEEP);
		mSolo.clickOnView(mButtonTopTool);

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);
		colorAfterErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After erasing, pixel should be transparent again", Color.TRANSPARENT, colorAfterErase);
	}

	public void testChangeEraserBrushSize() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PointF canvasPoint = Utils.getCanvasPointFromScreenPoint(screenPoint);

		((Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class, PaintroidApplication.drawingSurface,
				"mWorkingBitmap")).eraseColor(Color.BLACK);

		int colorBeforeErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		selectTool(ToolType.ERASER);
		mSolo.clickOnView(mMenuBottomParameter1);
		assertTrue("Waiting for Brush Picker Dialog",
				mSolo.waitForText(mSolo.getString(R.string.stroke_title), 1, TIMEOUT));
		TextView brushWidthTextView = mSolo.getText("25");
		String brushWidthText = (String) brushWidthTextView.getText();
		assertEquals("Wrong brush width displayed", Integer.valueOf(brushWidthText), Integer.valueOf(25));

		ArrayList<ProgressBar> progressBars = mSolo.getCurrentViews(ProgressBar.class);
		assertEquals(progressBars.size(), 1);
		SeekBar strokeWidthBar = (SeekBar) progressBars.get(0);
		assertEquals(strokeWidthBar.getProgress(), 25);

		int newStrokeWidth = 80;
		mSolo.setProgressBar(0, newStrokeWidth);
		assertTrue("Waiting for set stroke width ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertEquals(strokeWidthBar.getProgress(), newStrokeWidth);
		mSolo.clickOnButton(mSolo.getString(R.string.done));
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);
		Paint strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mCanvasPaint");
		int paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		assertEquals(paintStrokeWidth, newStrokeWidth);

		mSolo.clickOnScreen((int) screenPoint.x, (int) screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);
		int colorAfterErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Brushing after erase should be transparent", Color.TRANSPARENT, colorAfterErase);
	}

	public void testChangeEraserBrushForm() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PointF canvasPoint = Utils.getCanvasPointFromScreenPoint(screenPoint);

		((Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class, PaintroidApplication.drawingSurface,
				"mWorkingBitmap")).eraseColor(Color.BLACK);

		int colorBeforeErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		selectTool(ToolType.ERASER);

		mSolo.clickOnView(mMenuBottomParameter1);
		assertTrue("Waiting for Brush Picker Dialog",
				mSolo.waitForText(mSolo.getString(R.string.stroke_title), 1, TIMEOUT));

		mSolo.clickOnImageButton(0);

		assertTrue("Waiting for set stroke cap SQUARE ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		Paint strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mCanvasPaint");
		mSolo.clickOnButton(mSolo.getString(R.string.done));
		assertEquals(strokePaint.getStrokeCap(), Cap.SQUARE);

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);
		int colorAfterErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Brushing after erase should be transparent", Color.TRANSPARENT, colorAfterErase);
	}

	public void testRestorePreviousToolSettings() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mMenuBottomParameter1);
		assertTrue("Waiting for Brush Picker Dialog",
				mSolo.waitForText(mSolo.getString(R.string.stroke_title), 1, TIMEOUT));
		TextView brushWidthTextView = mSolo.getText("25");
		String brushWidthText = (String) brushWidthTextView.getText();
		assertEquals("Wrong brush width displayed", Integer.valueOf(brushWidthText), Integer.valueOf(25));

		ArrayList<ProgressBar> progressBars = mSolo.getCurrentViews(ProgressBar.class);
		assertEquals(progressBars.size(), 1);
		SeekBar strokeWidthBar = (SeekBar) progressBars.get(0);
		assertEquals(strokeWidthBar.getProgress(), 25);

		int newStrokeWidth = 80;
		mSolo.setProgressBar(0, newStrokeWidth);
		assertTrue("Waiting for set stroke width ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertEquals(strokeWidthBar.getProgress(), newStrokeWidth);

		int squarePictureButton = 0;
		mSolo.clickOnImageButton(squarePictureButton);
		assertTrue("Waiting for set stroke cap SQUARE ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		Paint strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mCanvasPaint");
		int paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		assertEquals(paintStrokeWidth, newStrokeWidth);
		mSolo.clickOnButton(mSolo.getString(R.string.done));
		mSolo.sleep(500);
		assertEquals(strokePaint.getStrokeCap(), Cap.SQUARE);

		selectTool(ToolType.ERASER);
		mSolo.clickOnView(mMenuBottomParameter1);
		assertTrue("Waiting for Brush Picker Dialog",
				mSolo.waitForText(mSolo.getString(R.string.stroke_title), 1, TIMEOUT));

		ArrayList<ProgressBar> eraserProgressBars = mSolo.getCurrentViews(ProgressBar.class);
		assertEquals(eraserProgressBars.size(), 1);
		SeekBar eraserStrokeWidthBar = (SeekBar) eraserProgressBars.get(0);
		assertEquals(eraserStrokeWidthBar.getProgress(), newStrokeWidth);

		int eraserStrokeWidth = 60;
		mSolo.setProgressBar(0, eraserStrokeWidth);
		assertTrue("Waiting for set stroke width ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertEquals(eraserStrokeWidthBar.getProgress(), eraserStrokeWidth);

		int roundPictureButton = 1;
		mSolo.clickOnImageButton(roundPictureButton);
		assertTrue("Waiting for set stroke cap ROUND ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		mSolo.clickOnButton(mSolo.getString(R.string.done));
		mSolo.sleep(500);
		Paint eraserStrokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class,
				PaintroidApplication.currentTool, "mCanvasPaint");
		assertEquals(eraserStrokePaint.getStrokeCap(), Cap.ROUND);

		selectTool(ToolType.BRUSH);
		Paint lastStrokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mCanvasPaint");
		assertEquals((int) lastStrokePaint.getStrokeWidth(), newStrokeWidth);
		assertEquals(lastStrokePaint.getStrokeCap(), Cap.SQUARE);

	}

}
