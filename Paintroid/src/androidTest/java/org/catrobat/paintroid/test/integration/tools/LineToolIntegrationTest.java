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

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import android.test.FlakyTest;
import android.widget.LinearLayout;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.Before;

public class LineToolIntegrationTest extends BaseIntegrationTestClass {

	protected static final int HALF_LINE_LENGTH = 25;
	private final static int SLEEP_TIME = 500;

	public LineToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
	}

    protected void tearDown() throws Exception {
        super.tearDown();
    }

	public void testVerticalLineColor()  {

		// TODO: Refactor tests (lot of copy paste code...)
		// Switching to Fullscreen, this makes pointOnCanvas equal to pointOnScreen

		selectTool(ToolType.LINE);
        mSolo.waitForDialogToClose(TIMEOUT);
		switchToFullscreen();

		float clickCoordinateX = mScreenWidth / 2;
		float clickCoordinateY = mScreenHeight / 2;
		PointF pointOnScreen = new PointF(clickCoordinateX, clickCoordinateY);
		PointF pointOnCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(pointOnScreen);

		int color = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);

		assertEquals("Color before doing anything has to be transparent", Color.TRANSPARENT, color);

		mSolo.drag(clickCoordinateX, clickCoordinateX, clickCoordinateY - HALF_LINE_LENGTH, clickCoordinateY
				+ HALF_LINE_LENGTH, 10);

		mSolo.sleep(SLEEP_TIME);

		color = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);
		assertEquals("Color after drawing line has to be black", Color.BLACK, color);

	}

	public void testHorizontalLineColor()  {
		selectTool(ToolType.LINE);
        mSolo.waitForDialogToClose(TIMEOUT);
		switchToFullscreen();

		float clickCoordinateX = mScreenWidth / 2;
		float clickCoordinateY = mScreenHeight / 2;
		PointF pointOnScreen = new PointF(clickCoordinateX, clickCoordinateY);
		PointF pointOnCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(pointOnScreen);

		int color = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);

		assertEquals("Color before doing anything has to be transparent", Color.TRANSPARENT, color);

		mSolo.drag(clickCoordinateX - HALF_LINE_LENGTH, clickCoordinateX + HALF_LINE_LENGTH, clickCoordinateY,
				clickCoordinateY, 10);

		mSolo.sleep(SLEEP_TIME);

		color = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);
		assertEquals("Color after drawing line has to be black", Color.BLACK, color);

	}

	public void testDiagonaleLineColor() {
		selectTool(ToolType.LINE);
        mSolo.waitForDialogToClose(TIMEOUT);
		switchToFullscreen();

		float clickCoordinateX = mScreenWidth / 2;
		float clickCoordinateY = mScreenHeight / 2;
		PointF pointOnScreen = new PointF(clickCoordinateX, clickCoordinateY);
		PointF pointOnCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(pointOnScreen);

		int color = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);

		assertEquals("Color before doing anything has to be transparent", Color.TRANSPARENT, color);

		mSolo.drag(clickCoordinateX - HALF_LINE_LENGTH, clickCoordinateX + HALF_LINE_LENGTH, clickCoordinateY
				+ HALF_LINE_LENGTH, clickCoordinateY - HALF_LINE_LENGTH, 10);
		mSolo.sleep(SLEEP_TIME);

		color = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);
		assertEquals("Color after drawing line has to be black", Color.BLACK, color);

	}

	public void testChangeLineToolForm() throws NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		int clickCoordinateX = mScreenWidth / 2;
		int clickCoordinateY = mScreenHeight / 2;

		PointF pointOnBitmap = new PointF(clickCoordinateX, clickCoordinateY);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnBitmap);
		int colorBeforeDrawing = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals("Color should be transparent!", Color.TRANSPARENT, colorBeforeDrawing);

		selectTool(ToolType.LINE);
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
		strokePaint.setStrokeWidth(500);
		PrivateAccess.setMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint", strokePaint);
	}

}
