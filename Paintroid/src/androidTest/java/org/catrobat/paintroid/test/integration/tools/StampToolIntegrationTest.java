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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.tools.implementation.StampTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.PointF;

public class StampToolIntegrationTest extends BaseIntegrationTestClass {

	private static final int Y_CLICK_OFFSET = 25;
	private static final int MOVE_TOLERANCE = 10;
	private static final float SCALE_25 = 0.25f;
	private static final float STAMP_RESIZE_FACTOR = 1.5f;
	// Rotation test
	private static final float SQUARE_LENGTH = 300;
	private static final float SQUARE_LENGTH_TINY = 50;
	private static final float MIN_ROTATION = -450f;
	private static final float MAX_ROTATION = 450f;
	private static final float ROTATION_STEPSIZE = 30.0f;
	private static final float ROTATION_TOLERANCE = 10;

	public StampToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		Thread.sleep(1500);
		super.tearDown();
		Thread.sleep(1000);
	}

	@Test
	public void testIconsInitial() {
		selectTool(ToolType.STAMP);
		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_copy,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear_disabled,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testIconsAfterCopyWithButton() {
		selectTool(ToolType.STAMP);
		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.waitForDialogToClose(TIMEOUT);
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_paste,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testIconsAfterCopyWithBox() {
		PointF surfaceCenterPoint = getScreenPointFromSurfaceCoordinates(getSurfaceCenterX(), getSurfaceCenterY());
		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y);
		selectTool(ToolType.STAMP);
		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y);
		mSolo.waitForDialogToClose(TIMEOUT);
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_paste,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testIconsAfterCopyAndPasteWithButton() {
		selectTool(ToolType.STAMP);
        StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.waitForDialogToClose();
		mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.waitForDialogToClose();
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_paste,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testIconsAfterCopyWithButtonAndPasteWithBox() {
		PointF surfaceCenterPoint = getScreenPointFromSurfaceCoordinates(getSurfaceCenterX(), getSurfaceCenterY());
		selectTool(ToolType.STAMP);
		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y);
		mSolo.waitForDialogToClose(TIMEOUT);
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_paste,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testIconsAfterCopyWithBoxAndPasteWithButton() {
		PointF surfaceCenterPoint = getScreenPointFromSurfaceCoordinates(getSurfaceCenterX(), getSurfaceCenterY());
		selectTool(ToolType.STAMP);
		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y);
        mSolo.sleep(SHORT_TIMEOUT);
		mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.waitForDialogToClose();
		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_paste,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testIconsAfterCopyAndPasteWithBox() {
		PointF surfaceCenterPoint = getScreenPointFromSurfaceCoordinates(getSurfaceCenterX(), getSurfaceCenterY());
		selectTool(ToolType.STAMP);
		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y);
        mSolo.waitForDialogToClose();
		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y);
        mSolo.waitForDialogToClose();
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_paste,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testIconsAfterCopyAndClearWithButton() {
		selectTool(ToolType.STAMP);
        mSolo.waitForDialogToClose();
		mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.waitForDialogToClose();
		mSolo.clickOnView(mMenuBottomParameter2);
        mSolo.waitForDialogToClose();
		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_copy,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear_disabled,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testIconsAfterCopyPasteAndClearWithButton() {
		selectTool(ToolType.STAMP);
		mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.waitForDialogToClose();
		mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.waitForDialogToClose();
		mSolo.clickOnView(mMenuBottomParameter2);
        mSolo.waitForDialogToClose();
		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_copy,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear_disabled,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testBoundingboxAlgorithm() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		PaintroidApplication.perspective.setScale(1.0f);

		PointF surfaceCenterPoint = getScreenPointFromSurfaceCoordinates(getSurfaceCenterX(), getSurfaceCenterY());
		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET - (SQUARE_LENGTH / 3));

		mSolo.sleep(500);

		selectTool(ToolType.STAMP);

		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;

		PointF toolPosition = new PointF(surfaceCenterPoint.x, surfaceCenterPoint.y);
		PrivateAccess.setMemberValue(BaseToolWithShape.class, stampTool, "mToolPosition", toolPosition);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxWidth", SQUARE_LENGTH);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxHeight", SQUARE_LENGTH);

		mSolo.sleep(1000);

		Bitmap copyOfToolBitmap = null;

		for (float rotationOfStampBox = MIN_ROTATION; rotationOfStampBox < MAX_ROTATION; rotationOfStampBox = rotationOfStampBox
				+ ROTATION_STEPSIZE) {
			PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxRotation",
					(int) (rotationOfStampBox));

			mSolo.sleep(500);

			invokeCreateAndSetBitmap(stampTool, PaintroidApplication.drawingSurface);

			copyOfToolBitmap = ((Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, stampTool,
					"mDrawingBitmap")).copy(Config.ARGB_8888, false);

			float width = copyOfToolBitmap.getWidth();
			float height = copyOfToolBitmap.getHeight();

			// Find one of the black pixels

			PointF pixelFound = null;
			int[] pixelLine = new int[(int) width + 1];
			for (int drawingBitmapYCoordinate = 0; drawingBitmapYCoordinate < height; drawingBitmapYCoordinate++) {
				copyOfToolBitmap.getPixels(pixelLine, 0, (int) width, 0, drawingBitmapYCoordinate, (int) width, 1);
				for (int drawningBitmapXCoordinate = 0; drawningBitmapXCoordinate < width; drawningBitmapXCoordinate++) {
					int pixelColor = pixelLine[drawningBitmapXCoordinate];
					if (pixelColor != 0) {
						pixelFound = new PointF(drawningBitmapXCoordinate, drawingBitmapYCoordinate);
						break;
					}
				}
				if (pixelFound != null) {
					break;
				}
			}

			copyOfToolBitmap.recycle();
			copyOfToolBitmap = null;
			System.gc();

			assertNotNull(
					"The drawn black spot should be found by the stamp, but was not in the Bitmap after rotation",
					pixelFound);

			// Check if the line from found pixel to center has a fitting rotation value

			// angle of line = (x, y) to vector = (a,b) = (0,1)
			float x = (SQUARE_LENGTH / 2) - pixelFound.x;
			float y = (SQUARE_LENGTH / 2) - pixelFound.y;
			float a = 0f;
			float b = 1f;

			double angle = Math.acos((x * a + y * b) / (Math.sqrt(x * x + y * y) * Math.sqrt(a * a + b * b)));
			angle = Math.toDegrees(angle);

			float rotationPositive = rotationOfStampBox;
			if (rotationPositive < 0.0) {
				rotationPositive = -rotationPositive;
			}

			while (rotationPositive > 360.0) {
				rotationPositive -= 360.0;
			}

			if (rotationPositive > 180.0) {
				rotationPositive = 360 - rotationPositive;
			}

			boolean rotationOk = (rotationPositive + ROTATION_TOLERANCE > angle)
					&& (rotationPositive - ROTATION_TOLERANCE < angle);
			assertEquals("Wrong rotationvalue was calculated", true, rotationOk);
		}

	}

	@Test
	public void testCopyPixel() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		PointF surfaceCenterPoint = getScreenPointFromSurfaceCoordinates(getSurfaceCenterX(), getSurfaceCenterY());
		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET);

		selectTool(ToolType.STAMP);
        mSolo.waitForDialogToClose();

		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		PointF toolPosition = new PointF(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET);
		PrivateAccess.setMemberValue(BaseToolWithShape.class, stampTool, "mToolPosition", toolPosition);

        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.waitForDialogToClose();

		PointF pixelCoordinateToControlColor = new PointF(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET);
		PointF surfacePoint = Utils.getSurfacePointFromScreenPoint(pixelCoordinateToControlColor);
		int pixelToControl = PaintroidApplication.drawingSurface.getPixel(PaintroidApplication.perspective
				.getCanvasPointFromSurfacePoint(surfacePoint));

		assertEquals("First Pixel not Black after using Stamp for copying", Color.BLACK, pixelToControl);

		int moveOffset = 100;

		toolPosition.y = toolPosition.y - moveOffset;
		PrivateAccess.setMemberValue(BaseToolWithShape.class, stampTool, "mToolPosition", toolPosition);
        mSolo.sleep(SHORT_SLEEP);

        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.waitForDialogToClose();

		toolPosition.y = toolPosition.y - moveOffset;
		PrivateAccess.setMemberValue(BaseToolWithShape.class, stampTool, "mToolPosition", toolPosition);
        mSolo.sleep(SHORT_SLEEP);

		pixelCoordinateToControlColor = new PointF(toolPosition.x, toolPosition.y + moveOffset + Y_CLICK_OFFSET);
		surfacePoint = Utils.getSurfacePointFromScreenPoint(pixelCoordinateToControlColor);
		pixelToControl = PaintroidApplication.drawingSurface.getPixel(PaintroidApplication.perspective
				.getCanvasPointFromSurfacePoint(surfacePoint));

		assertEquals("Second Pixel not Black after using Stamp for copying", Color.BLACK, pixelToControl);
	}

	@Test
	public void testStampOutsideDrawingSurface() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnScreen(getSurfaceCenterX(), getSurfaceCenterY() + Utils.getActionbarHeight()
				+ getStatusbarHeight() - Y_CLICK_OFFSET);

		int screenWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
		int screenHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
		PrivateAccess.setMemberValue(Perspective.class, PaintroidApplication.perspective, "mSurfaceScale", SCALE_25);

		mSolo.sleep(500);

		selectTool(ToolType.STAMP);

		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		PointF toolPosition = new PointF(getSurfaceCenterX(), getSurfaceCenterY());
		PrivateAccess.setMemberValue(BaseToolWithShape.class, stampTool, "mToolPosition", toolPosition);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxWidth",
				(int) (screenWidth * STAMP_RESIZE_FACTOR));
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxHeight",
				(int) (screenHeight * STAMP_RESIZE_FACTOR));

		mSolo.clickOnScreen(getSurfaceCenterX(), getSurfaceCenterY() + Utils.getActionbarHeight()
				+ getStatusbarHeight() - Y_CLICK_OFFSET);
		assertTrue("Stamping timed out", mSolo.waitForDialogToClose());

		Bitmap drawingBitmap = ((Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, stampTool,
				"mDrawingBitmap")).copy(Config.ARGB_8888, false);

		assertNotNull("After activating stamp, mDrawingBitmap should not be null anymore", drawingBitmap);

		drawingBitmap.recycle();
		drawingBitmap = null;

	}

	private void invokeCreateAndSetBitmap(Object object, Object parameter) throws NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		Method method = object.getClass().getDeclaredMethod("createAndSetBitmap");
		method.setAccessible(true);

		Object[] parameters = new Object[0];
		method.invoke(object, parameters);
	}

}
