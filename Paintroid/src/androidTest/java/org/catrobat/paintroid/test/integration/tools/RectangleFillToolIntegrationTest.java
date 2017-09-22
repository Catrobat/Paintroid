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

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.widget.Button;
import android.widget.TableRow;

import com.robotium.solo.Solo;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.Before;
import org.junit.Test;

public class RectangleFillToolIntegrationTest extends BaseIntegrationTestClass {

	private static final String PRIVATE_ACCESS_STATUSBAR_NAME = "mTopBar";

	private static final String TOOL_MEMBER_WIDTH = "mBoxWidth";
	private static final String TOOL_MEMBER_HEIGHT = "mBoxHeight";
	private static final String TOOL_MEMBER_POSITION = "mToolPosition";
	private static final String TOOL_MEMBER_BITMAP = "mDrawingBitmap";

	public RectangleFillToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp(){
		super.setUp();
		resetColorPicker();
	}

	@Test
	public void testFilledRectIsCreated() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		selectTool(ToolType.SHAPE);
		Tool mRectangleFillTool = getCurrentTool();
		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mRectangleFillTool,
				TOOL_MEMBER_POSITION);

		assertTrue("Width should not be zero", rectWidth != 0.0f);
		assertTrue("Width should not be zero", rectHeight != 0.0f);
		assertNotNull("Position should not be NULL", rectPosition);
	}


	@Test
	public void testEllipseIsDrawnOnBitmap() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		PaintroidApplication.perspective.setScale(1.0f);

		selectTool(ToolType.SHAPE);

		mSolo.clickOnView(mSolo.getView(R.id.shapes_circle_btn));
		mSolo.sleep(50);

		Tool ellipseTool = getCurrentTool();
		PointF centerPointTool = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, ellipseTool,
				TOOL_MEMBER_POSITION);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, ellipseTool,
				TOOL_MEMBER_HEIGHT);
		PointF pointUnderTest = new PointF(centerPointTool.x, centerPointTool.y);
		int colorBeforeDrawing = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);

		closeToolOptionsForCurrentTool();
		mSolo.clickOnScreen(centerPointTool.x - 1, centerPointTool.y - 1);


		mSolo.sleep(50);
		mSolo.goBack();
		mSolo.sleep(50);

		int colorPickerColor = getCurrentTool().getDrawPaint().getColor();

		int colorAfterDrawing = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);

		assertEquals("Pixel should have the same color as currently in color picker", colorPickerColor,
				colorAfterDrawing);

		mSolo.clickOnView(mButtonTopUndo);
		mSolo.sleep(1000);

		int colorAfterUndo = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);
		assertEquals(colorBeforeDrawing, colorAfterUndo);

		mSolo.clickOnView(mButtonTopRedo);
		mSolo.sleep(1000);

		int colorAfterRedo = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);
		assertEquals(colorPickerColor, colorAfterRedo);

		pointUnderTest.x = centerPointTool.x + (rectHeight / 2.5f);
		colorAfterDrawing = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);
		assertEquals("Pixel should have the same color as currently in color picker", colorPickerColor,
				colorAfterDrawing);

		pointUnderTest.y = centerPointTool.y + (rectHeight / 2.5f);
		// now the point under test is diagonal from the center -> if its a circle there should be no color
		colorAfterDrawing = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);
		assertTrue("Pixel should not have been filled for a circle", (colorPickerColor != colorAfterDrawing));
		PaintroidApplication.commandManager.resetAndClear(true);
	}

	@Test
	public void testRectOnBitmapHasSameColorAsInColorPickerAfterColorChange() throws SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		int colorPickerColorBeforeChange = getCurrentTool().getDrawPaint().getColor();
		openColorChooserDialog();

		Button colorButton = mSolo.getButton(5);
		assertTrue(colorButton.getParent() instanceof TableRow);
		mSolo.clickOnButton(5);
		mSolo.sleep(50);
		mSolo.clickOnButton(getActivity().getResources().getString(R.string.done));

		int colorPickerColorAfterChange = getCurrentTool().getDrawPaint().getColor();
		assertTrue("Colors should not be the same", colorPickerColorAfterChange != colorPickerColorBeforeChange);

		selectTool(ToolType.SHAPE);
		int colorInRectangleTool = getCurrentTool().getDrawPaint().getColor();
		assertEquals("Colors should be the same", colorPickerColorAfterChange, colorInRectangleTool);

		Tool mRectangleFillTool = getCurrentTool();
		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_HEIGHT);
		Bitmap drawingBitmap = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class,
				mRectangleFillTool, TOOL_MEMBER_BITMAP);
		int colorInRectangle = drawingBitmap.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		assertEquals("Colors should be the same", colorPickerColorAfterChange, colorInRectangle);
	}

	@Test
	public void testFilledRectChangesColor() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		selectTool(ToolType.SHAPE);
		Tool mRectangleFillTool = getCurrentTool();
		int colorInRectangleTool = getCurrentTool().getDrawPaint().getColor();
		Bitmap drawingBitmap = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class,
				mRectangleFillTool, TOOL_MEMBER_BITMAP);
		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_HEIGHT);
		int colorInRectangle = drawingBitmap.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		assertEquals("Colors should be equal", colorInRectangleTool, colorInRectangle);

		openColorChooserDialog();

		Button colorButton = mSolo.getButton(5);
		assertTrue(colorButton.getParent() instanceof TableRow);
		mSolo.clickOnButton(5);
		mSolo.sleep(50);
		mSolo.clickOnButton(getActivity().getResources().getString(R.string.done));
		mSolo.sleep(50);

		int colorInRectangleToolAfter = getCurrentTool().getDrawPaint().getColor();
		Bitmap drawingBitmapAfter = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class,
				mRectangleFillTool, TOOL_MEMBER_BITMAP);
		int colorInRectangleAfter = drawingBitmapAfter.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		assertTrue("Colors should have changed", colorInRectangle != colorInRectangleAfter);
		assertTrue("Colors should have changed", colorInRectangleTool != colorInRectangleToolAfter);
		assertEquals("Colors should be equal", colorInRectangleTool, colorInRectangle);
	}

	/*
	public void testFilledRectIsDrawnOnBitmap() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		PaintroidApplication.perspective.resetScaleAndTranslation();
		PaintroidApplication.perspective.setScale(1.0f);

		selectTool(ToolType.SHAPE);
		Tool mRectangleFillTool = getCurrentTool();

		PointF point = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mRectangleFillTool,
				TOOL_MEMBER_POSITION);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_HEIGHT);
		PointF pointOnBitmap = new PointF(point.x, (point.y + (rectHeight / 4.0f)));
		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		int colorBeforeDrawing = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);

		closeToolOptionsForCurrentTool();
		mSolo.sleep(500);
		mSolo.clickOnScreen(pointOnScreen.x,pointOnScreen.y,1);

		mSolo.sleep(50);
		mSolo.goBack();
		mSolo.sleep(50);

		int colorAfterDrawing = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		int colorPickerColor = getCurrentTool().getDrawPaint().getColor();
		assertEquals("Pixel should have the same color as currently in color picker", colorPickerColor,
				colorAfterDrawing);

		mSolo.clickOnView(mButtonTopUndo);
		mSolo.sleep(1000);

		int colorAfterUndo = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals(colorBeforeDrawing, colorAfterUndo);

		mSolo.clickOnView(mButtonTopRedo);
		mSolo.sleep(1000);

		int colorAfterRedo = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals(colorPickerColor, colorAfterRedo);
	}*/
}
