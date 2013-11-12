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
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.Before;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.graphics.PointF;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class LineToolIntegrationTest extends BaseIntegrationTestClass {

	public LineToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
	}

	public void testVerticalLineColor() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		float clickCoordinateX = mScreenWidth / 2;
		float clickCoordinateY = mScreenHeight / 2;

		selectTool(ToolType.LINE);

		PointF pointOnCanvas = new PointF(clickCoordinateX, clickCoordinateY);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnCanvas);
		int color = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);

		assertEquals("Color before doing anything has to be transparent", Color.TRANSPARENT, color);

		mSolo.drag(clickCoordinateX, clickCoordinateX, clickCoordinateY - 200, clickCoordinateY + 200, 10);

		mSolo.sleep(3000);

		color = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);
		assertEquals("Color after drawing line has to be black", Color.BLACK, color);

	}

	public void testSwitchingBetweenBrushAndEraser() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		int clickCoordinateX = mScreenWidth / 2;
		int clickCoordinateY = mScreenHeight / 2;

		((Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class, PaintroidApplication.drawingSurface,
				"mWorkingBitmap")).eraseColor(Color.BLACK);
		PointF pointOnBitmap = new PointF(org.catrobat.paintroid.test.utils.Utils.convertFromCanvasToScreen(new Point(
				clickCoordinateX, clickCoordinateY), PaintroidApplication.perspective));
		int colorBeforeErase = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		selectTool(ToolType.ERASER);
		Paint strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mBitmapPaint");
		strokePaint.setStrokeWidth(500);
		PrivateAccess.setMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint", strokePaint);
		mSolo.clickOnScreen(clickCoordinateX, clickCoordinateY);
		int colorAfterErase = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals("After erasing, pixel should be transparent again", Color.TRANSPARENT, colorAfterErase);

		selectTool(ToolType.BRUSH);
		strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mBitmapPaint");
		strokePaint.setStrokeWidth(500);
		PrivateAccess.setMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint", strokePaint);
		mSolo.clickOnScreen(clickCoordinateX, clickCoordinateY);
		int colorAfterBrush = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals("Brushing after erase should be black again like before erasing", Color.BLACK, colorAfterBrush);

		selectTool(ToolType.ERASER);
		strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mBitmapPaint");
		strokePaint.setStrokeWidth(500);
		PrivateAccess.setMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint", strokePaint);
		mSolo.clickOnScreen(clickCoordinateX, clickCoordinateY);
		colorAfterErase = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals("After erasing, pixel should be transparent again", Color.TRANSPARENT, colorAfterErase);
	}

	public void testChangeEraserBrushSize() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		int clickCoordinateX = mScreenWidth / 2;
		int clickCoordinateY = mScreenHeight / 2;

		((Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class, PaintroidApplication.drawingSurface,
				"mWorkingBitmap")).eraseColor(Color.BLACK);
		PointF pointOnBitmap = new PointF(org.catrobat.paintroid.test.utils.Utils.convertFromCanvasToScreen(new Point(
				clickCoordinateX, clickCoordinateY), PaintroidApplication.perspective));
		int colorBeforeErase = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		selectTool(ToolType.ERASER);
		mSolo.clickOnView(mMenuBottomParameter1);
		assertTrue("Waiting for Brush Picker Dialog",
				mSolo.waitForText(mSolo.getString(R.string.stroke_title), 1, TIMEOUT));
		TextView brushWidthTextView = mSolo.getText("25");
		String brushWidthText = (String) brushWidthTextView.getText();
		assertEquals("Wrong brush width displayed", Integer.valueOf(brushWidthText), Integer.valueOf(25));

		ArrayList<ProgressBar> progressBars = mSolo.getCurrentProgressBars();
		assertEquals(progressBars.size(), 1);
		SeekBar strokeWidthBar = (SeekBar) progressBars.get(0);
		assertEquals(strokeWidthBar.getProgress(), 25);

		int newStrokeWidth = 80;
		mSolo.setProgressBar(0, newStrokeWidth);
		assertTrue("Waiting for set stroke width ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertEquals(strokeWidthBar.getProgress(), newStrokeWidth);
		mSolo.clickOnButton(mSolo.getString(R.string.done));
		mSolo.sleep(500);
		Paint strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mCanvasPaint");
		int paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		assertEquals(paintStrokeWidth, newStrokeWidth);

		strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mBitmapPaint");
		strokePaint.setStrokeWidth(500);// TODO workaround cause cannot find correct bitmap coords on older devices
		PrivateAccess.setMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint", strokePaint);

		mSolo.clickOnScreen(clickCoordinateX, clickCoordinateY);
		int colorAfterErase = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals("Brushing after erase should be transparent", Color.TRANSPARENT, colorAfterErase);
	}

	public void testChangeEraserBrushForm() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		int clickCoordinateX = mScreenWidth / 2;
		int clickCoordinateY = mScreenHeight / 2;

		((Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class, PaintroidApplication.drawingSurface,
				"mWorkingBitmap")).eraseColor(Color.BLACK);
		PointF pointOnBitmap = new PointF(clickCoordinateX, clickCoordinateY);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnBitmap);
		int colorBeforeErase = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
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
		strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mBitmapPaint");
		strokePaint.setStrokeWidth(500);// TODO workaround cause cannot find correct bitmap coords on older devices
		PrivateAccess.setMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint", strokePaint);

		mSolo.clickOnScreen(clickCoordinateX, clickCoordinateY);
		int colorAfterErase = ((Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap")).getPixel(clickCoordinateX, clickCoordinateY);
		assertEquals("Brushing after erase should be transparent", Color.TRANSPARENT, colorAfterErase);
	}

}
