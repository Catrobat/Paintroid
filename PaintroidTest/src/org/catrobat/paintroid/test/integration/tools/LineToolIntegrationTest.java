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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.graphics.PointF;
import android.widget.LinearLayout;

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
		float surfaceWidth = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
				"mSurfaceWidth");
		float surfaceHeight = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
				"mSurfaceHeight");

		float clickCoordinateX1 = mScreenWidth / 2;
		float clickCoordinateY1 = mScreenHeight / 2;
		float clickCoordinateX = surfaceWidth / 2;
		float clickCoordinateY = surfaceHeight / 2;

		selectTool(ToolType.BRUSH);

		PointF pointOnScreen = new PointF(clickCoordinateX, clickCoordinateY);
		PointF pointOnCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(pointOnScreen); // falsch
		int color = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);

		// mSolo.clickOnScreen(pointOnCanvas.x, pointOnCanvas.y);

		// switchToFullscreen();
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);
		// mSolo.goBack();

		assertEquals("Color before doing anything has to be transparent", Color.TRANSPARENT, color);

		selectTool(ToolType.LINE);

		// switchToFullscreen();
		mSolo.drag(clickCoordinateX, clickCoordinateX, clickCoordinateY - 10, clickCoordinateY + 10, 10);

		mSolo.sleep(1000);

		color = PaintroidApplication.drawingSurface.getPixel(pointOnScreen);
		// color = PaintroidApplication.drawingSurface.getPixel(pointOnScreen);
		assertEquals("Color after drawing line has to be black", Color.BLACK, color);

	}

	public void testHorizontalLineColor() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		float clickCoordinateX = mScreenWidth / 2;
		float clickCoordinateY = mScreenHeight / 2;

		selectTool(ToolType.LINE);

		// PointF pointOnCanvas = new PointF(clickCoordinateX, clickCoordinateY);
		PointF pointOnCanvas = new PointF(org.catrobat.paintroid.test.utils.Utils.convertFromCanvasToScreen(
				new Point(), PaintroidApplication.perspective));
		// PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnCanvas);
		int color = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);

		assertEquals("Color before doing anything has to be transparent", Color.TRANSPARENT, color);

		// mSolo.drag(clickCoordinateX, clickCoordinateX, clickCoordinateY - 200, clickCoordinateY + 200, 10);
		// mSolo.sleep(1000);
		mSolo.drag(clickCoordinateX - 100, clickCoordinateX + 100, clickCoordinateY, clickCoordinateY, 10);
		// TODO: TESTCASE FAILES: WHY???!!!???!?!?!?!?!? AAAAAAAHHHHH

		mSolo.sleep(3000);

		color = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);
		assertEquals("Color after drawing line has to be black", Color.BLACK, color);

	}

	public void testDiagonaleLineColor() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		float clickCoordinateX = mScreenWidth / 2;
		float clickCoordinateY = mScreenHeight / 2;

		selectTool(ToolType.LINE);

		PointF pointOnCanvas = new PointF(clickCoordinateX - 200, clickCoordinateY + 200);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnCanvas);
		int color = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);

		assertEquals("Color before doing anything has to be transparent", Color.TRANSPARENT, color);

		mSolo.drag(clickCoordinateX - 200, clickCoordinateX + 200, clickCoordinateY + 200, clickCoordinateY - 200, 10);
		mSolo.sleep(3000);
		// TODO: TESTCASE FAILES: WHY???!!!???!?!?!?!?!? AAAAAAAHHHHH

		color = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);
		assertEquals("Color after drawing line has to be black", Color.BLACK, color);

	}

	public void testChangeLineToolForm() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
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
