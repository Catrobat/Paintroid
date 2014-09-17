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
import org.catrobat.paintroid.test.utils.Utils;
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

		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();

		PointF screenPoint = new PointF(mScreenWidth / 2 - 100, mScreenHeight / 2 - 50);

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

		// PaintroidApplication.perspective.setScale(1.0f);

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
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Waiting for Color Chooser", mSolo.waitForText(mSolo.getString(R.string.done), 1, TIMEOUT * 2));
		Button colorButton = mSolo.getButton(5);
		assertTrue(colorButton.getParent() instanceof TableRow);
		mSolo.clickOnButton(5);
		mSolo.sleep(SHORT_SLEEP);
		mSolo.clickOnButton(getActivity().getResources().getString(R.string.done));
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);

		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertFalse(colorToDrawBorder == colorToFill);
		assertFalse(checkPointStartColor == colorToFill);

		// to fill the bitmap
		mSolo.clickOnScreen(clickScreenPoint.x, clickScreenPoint.y);
		mSolo.sleep(SHORT_SLEEP);
		mSolo.waitForDialogToClose(TIMEOUT);

		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterFill);

		int outsideColorAfterFill = PaintroidApplication.drawingSurface.getPixel(checkOutsideCanvasPoint);
		assertNotSame("Pixel color should be different", colorToFill, outsideColorAfterFill);
	}
}
