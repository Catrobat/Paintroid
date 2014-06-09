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

import java.io.File;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.Before;

import android.graphics.PointF;
import android.net.Uri;
import android.widget.Button;
import android.widget.TableRow;

public class FillToolIntegrationTest extends BaseIntegrationTestClass {

	private static final int SHORT_WAIT_TRIES = 10;

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

		int xCoord = mScreenWidth / 2;
		int yCoord = mScreenHeight / 2;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnScreen);

		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y); // to fill the bitmap
		assertFalse("Fill timed out", hasProgressDialogFinished(SHORT_WAIT_TRIES));
		PaintroidApplication.savedPictureUri = null;
	}

	public void testNoFloodFillIfEmpty() throws InterruptedException, SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		selectTool(ToolType.FILL);

		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();
		int xCoord = mScreenWidth / 2;
		int yCoord = mScreenHeight / 2;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnScreen);

		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y); // to fill the bitmap
		assertTrue("Fill timed out", hasProgressDialogFinished(SHORT_WAIT_TRIES));
		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterFill);
	}

	public void testBitmapIsFilled() throws InterruptedException, SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		selectTool(ToolType.FILL);

		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();
		int xCoord = mScreenWidth / 2;
		int yCoord = mScreenHeight / 2;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnScreen);

		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y); // to fill the bitmap
		assertTrue("Fill timed out", hasProgressDialogFinished(LONG_WAIT_TRIES));
		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterFill);
	}

	public void testNothingHappensWhenClickedOutsideDrawingArea() throws InterruptedException, SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		selectTool(ToolType.FILL);

		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();
		int xCoord = -100;
		int yCoord = -200;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnScreen);

		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y); // to fill the bitmap
		assertTrue("Fill timed out", hasProgressDialogFinished(LONG_WAIT_TRIES));
		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertFalse("Pixel color should not be the same", (colorToFill == colorAfterFill));

		xCoord = 800;
		yCoord = 800;
		pointOnBitmap = new PointF(xCoord, yCoord);

		pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnScreen);

		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y); // to fill the bitmap
		assertTrue("Fill timed out", hasProgressDialogFinished(LONG_WAIT_TRIES));
		colorAfterFill = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertFalse("Pixel color should not be the same", (colorToFill == colorAfterFill));
	}

	public void testOnlyFillInnerArea() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		PaintroidApplication.perspective.setScale(1.0f);

		DrawingSurface drawingSurface = (DrawingSurface) getActivity().findViewById(R.id.drawingSurfaceView);

		assertEquals("BrushTool should be selected", ToolType.BRUSH, PaintroidApplication.currentTool.getToolType());
		int colorToDrawBorder = PaintroidApplication.currentTool.getDrawPaint().getColor();

		int checkPointXCoord = mScreenWidth / 2;
		int checkPointYCoord = mScreenHeight / 2;
		PointF pointOnBitmap = new PointF(checkPointXCoord, checkPointYCoord);
		int checkPointStartColor = drawingSurface.getPixel(pointOnBitmap);
		assertFalse(colorToDrawBorder == checkPointStartColor);

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnScreen);

		PointF leftPointOnBitmap = new PointF(checkPointXCoord - 150, checkPointYCoord);
		PointF leftPointOnScreen = new PointF(leftPointOnBitmap.x, leftPointOnBitmap.y);
		PointF upperPointOnScreen = new PointF(checkPointXCoord, checkPointYCoord - 150);
		PointF rightPointOnScreen = new PointF(checkPointXCoord + 150, checkPointYCoord);
		PointF bottomPointOnScreen = new PointF(checkPointXCoord, checkPointYCoord + 150);

		PaintroidApplication.perspective.convertFromScreenToCanvas(leftPointOnScreen);
		PaintroidApplication.perspective.convertFromScreenToCanvas(upperPointOnScreen);
		PaintroidApplication.perspective.convertFromScreenToCanvas(rightPointOnScreen);
		PaintroidApplication.perspective.convertFromScreenToCanvas(bottomPointOnScreen);

		mSolo.drag(leftPointOnScreen.x, upperPointOnScreen.x, leftPointOnScreen.y, upperPointOnScreen.y, 1);
		mSolo.sleep(250);
		mSolo.drag(upperPointOnScreen.x, rightPointOnScreen.x, upperPointOnScreen.y, rightPointOnScreen.y, 1);
		mSolo.sleep(250);
		mSolo.drag(rightPointOnScreen.x, bottomPointOnScreen.x, rightPointOnScreen.y, bottomPointOnScreen.y, 1);
		mSolo.sleep(250);
		mSolo.drag(bottomPointOnScreen.x, leftPointOnScreen.x, bottomPointOnScreen.y, leftPointOnScreen.y, 1);

		selectTool(ToolType.FILL);
		// change color
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Waiting for Color Chooser", mSolo.waitForText(mSolo.getString(R.string.done), 1, TIMEOUT * 2));

		Button colorButton = mSolo.getButton(5);
		assertTrue(colorButton.getParent() instanceof TableRow);
		mSolo.clickOnButton(5);
		mSolo.sleep(50);
		mSolo.clickOnButton(getActivity().getResources().getString(R.string.done));

		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertFalse(colorToDrawBorder == colorToFill);
		assertFalse(checkPointStartColor == colorToFill);

		// to fill the bitmap
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);
		assertTrue("Fill timed out", hasProgressDialogFinished(LONG_WAIT_TRIES));

		int colorAfterFill = drawingSurface.getPixel(pointOnBitmap);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterFill);

		int outsideColorAfterFill = drawingSurface.getPixel(new PointF(leftPointOnBitmap.x - 30, leftPointOnBitmap.y));
		assertFalse("Pixel color should be different", colorToFill == outsideColorAfterFill);
	}
}
