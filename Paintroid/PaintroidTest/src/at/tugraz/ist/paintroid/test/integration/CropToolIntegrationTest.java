/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.GridView;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.tools.implementation.CropTool;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class CropToolIntegrationTest extends BaseIntegrationTestClass {

	private final int CROPPING_TIMOUT = 5000;

	// this test may fail sometimes even if everything is correct (seems to be a threading issue) so run them at least
	// twice :(
	public CropToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testWhenNoPixelIsOnBitmap() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnText(mMainActivity.getString(R.string.button_crop));
		assertTrue("Waiting for tool to change -> MainActivity", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.CROP);

		assertEquals("Zoom factor is wrong", 0.95f, PaintroidApplication.CURRENT_PERSPECTIVE.getScale());

		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnView(mToolBarButtonTwo);
		mSolo.sleep(100);
		assertTrue("nothing to crop text missing",
				mSolo.waitForText(mSolo.getString(R.string.crop_nothing_to_corp), 1, TIMEOUT, true));

	}

	@Test
	public void testIfOnePixelIsFound() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		currentDrawingSurfaceBitmap.setPixel(currentDrawingSurfaceBitmap.getWidth() / 2,
				currentDrawingSurfaceBitmap.getHeight() / 2, Color.BLUE);

		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));

		mSolo.clickOnText(mMainActivity.getString(R.string.button_crop));
		assertTrue("Waiting for tool to change -> MainActivity", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.CROP);

		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnView(mToolBarButtonTwo);
		mSolo.sleep(500);
		currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Wrong width after cropping ", 1, currentDrawingSurfaceBitmap.getWidth());
		assertEquals("Wrong height after cropping ", 1, currentDrawingSurfaceBitmap.getHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE, currentDrawingSurfaceBitmap.getPixel(0, 0));
	}

	@Test
	public void testIfMultiplePixelAreFound() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();

		int originalWidth = currentDrawingSurfaceBitmap.getWidth();
		int originalHeight = currentDrawingSurfaceBitmap.getHeight();
		currentDrawingSurfaceBitmap.setPixel(1, 1, Color.BLUE);
		currentDrawingSurfaceBitmap.setPixel(originalWidth - 2, originalHeight - 2, Color.BLUE);

		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));

		mSolo.clickOnText(mMainActivity.getString(R.string.button_crop));
		assertTrue("Waiting for tool to change -> MainActivity", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.CROP);

		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnView(mToolBarButtonTwo);
		mSolo.sleep(500);
		currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Wrong width after cropping ", originalWidth - 2, currentDrawingSurfaceBitmap.getWidth());
		assertEquals("Wrong height after cropping ", originalHeight - 2, currentDrawingSurfaceBitmap.getHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE, currentDrawingSurfaceBitmap.getPixel(0, 0));
	}

	@Test
	public void testIfDrawingSurfaceBoundsAreFoundAndNotCropped() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();

		int originalWidth = currentDrawingSurfaceBitmap.getWidth();
		int originalHeight = currentDrawingSurfaceBitmap.getHeight();
		currentDrawingSurfaceBitmap.setPixel(originalWidth / 2, 0, Color.BLUE);
		currentDrawingSurfaceBitmap.setPixel(0, originalHeight / 2, Color.BLUE);
		currentDrawingSurfaceBitmap.setPixel(originalWidth - 1, originalHeight / 2, Color.BLUE);
		currentDrawingSurfaceBitmap.setPixel(originalWidth / 2, originalHeight - 1, Color.BLUE);

		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));

		mSolo.clickOnText(mMainActivity.getString(R.string.button_crop));
		assertTrue("Waiting for tool to change -> MainActivity", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.CROP);

		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnView(mToolBarButtonTwo);
		mSolo.sleep(200);
		assertEquals(currentDrawingSurfaceBitmap, PaintroidApplication.DRAWING_SURFACE.getBitmap());

		currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Wrong width after cropping ", originalWidth, currentDrawingSurfaceBitmap.getWidth());
		assertEquals("Wrong height after cropping ", originalHeight, currentDrawingSurfaceBitmap.getHeight());
	}

	@Test
	public void testIfClickOnCanvasDoesNothing() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		currentDrawingSurfaceBitmap.eraseColor(Color.BLACK);
		int drawingSurfaceOriginalWidth = currentDrawingSurfaceBitmap.getWidth();
		int drawingSurfaceOriginalHeight = currentDrawingSurfaceBitmap.getHeight();
		for (int indexWidth = 0; indexWidth < drawingSurfaceOriginalWidth; indexWidth++) {
			currentDrawingSurfaceBitmap.setPixel(indexWidth, 0, Color.TRANSPARENT);
		}

		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));

		mSolo.clickOnText(mMainActivity.getString(R.string.button_crop));
		assertTrue("Waiting for tool to change -> MainActivity", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.CROP);

		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);
		currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Width changed:", drawingSurfaceOriginalWidth, currentDrawingSurfaceBitmap.getWidth());
		assertEquals("Height changed:", drawingSurfaceOriginalHeight, currentDrawingSurfaceBitmap.getHeight());
	}

	@Test
	public void testSmallBitmapCropping() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		currentDrawingSurfaceBitmap.setPixel(currentDrawingSurfaceBitmap.getWidth() / 2,
				currentDrawingSurfaceBitmap.getHeight() / 2, Color.BLUE);

		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));

		mSolo.clickOnText(mMainActivity.getString(R.string.button_crop));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.CROP);

		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnView(mToolBarButtonTwo);
		mSolo.sleep(500);
		currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Wrong width after cropping ", 1, currentDrawingSurfaceBitmap.getWidth());
		assertEquals("Wrong height after cropping ", 1, currentDrawingSurfaceBitmap.getHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE, currentDrawingSurfaceBitmap.getPixel(0, 0));
	}

	@Test
	public void testManualCroppingTakeMiddleOfBorder() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();

		int lineLeangth = (currentDrawingSurfaceBitmap.getWidth() / 2);
		int lineWidth = 10;
		int horizontalLineStartX = (currentDrawingSurfaceBitmap.getWidth() / 4);
		int horizontalLineStartY = (currentDrawingSurfaceBitmap.getHeight() / 2);
		int verticalLineStartX = (currentDrawingSurfaceBitmap.getWidth() / 2);
		int verticalLineStartY = (currentDrawingSurfaceBitmap.getHeight() / 2 - lineLeangth / 2);
		int stepCount = 2;

		int[] pixelsColorArray = new int[lineWidth * lineLeangth];
		for (int indexColorArray = 0; indexColorArray < pixelsColorArray.length; indexColorArray++) {
			pixelsColorArray[indexColorArray] = Color.BLACK;
		}

		currentDrawingSurfaceBitmap.setPixels(pixelsColorArray, 0, lineLeangth, horizontalLineStartX,
				horizontalLineStartY, lineLeangth, lineWidth);

		currentDrawingSurfaceBitmap.setPixels(pixelsColorArray, 0, lineWidth, verticalLineStartX, verticalLineStartY,
				lineWidth, lineLeangth);

		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnText(mMainActivity.getString(R.string.button_crop));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.CROP);
		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.sleep(3);
		int cropBoundWidthXLeft = (Integer) PrivateAccess.getMemberValue(CropTool.class,
				PaintroidApplication.CURRENT_TOOL, "mCropBoundWidthXLeft");
		int cropBoundWidthXRight = (Integer) PrivateAccess.getMemberValue(CropTool.class,
				PaintroidApplication.CURRENT_TOOL, "mCropBoundWidthXRight");
		int cropBoundHeightYTop = (Integer) PrivateAccess.getMemberValue(CropTool.class,
				PaintroidApplication.CURRENT_TOOL, "mCropBoundHeightYTop");
		int cropBoundHeightYBottom = (Integer) PrivateAccess.getMemberValue(CropTool.class,
				PaintroidApplication.CURRENT_TOOL, "mCropBoundHeightYBottom");

		// wait until equals:
		assertEquals("Bottom Bound not correct", cropBoundHeightYBottom, (verticalLineStartY + lineLeangth - 1));
		assertEquals("Top Bound not correct", cropBoundHeightYTop, verticalLineStartY);
		assertEquals("Left Bound not correct", cropBoundWidthXLeft, horizontalLineStartX);
		assertEquals("Right Bound not correct", cropBoundWidthXRight, horizontalLineStartX + lineLeangth - 1);

		int dragBottomBoundToY = currentDrawingSurfaceBitmap.getHeight() - 20;
		int dragTopBoundToY = 20;
		int dragLeftBoundToX = 20;
		int dragRightBoundToX = currentDrawingSurfaceBitmap.getWidth() - 20;

		// drag bottom
		mSolo.drag(currentDrawingSurfaceBitmap.getWidth() / 2, currentDrawingSurfaceBitmap.getWidth() / 2,
				verticalLineStartY + lineLeangth - 2, dragBottomBoundToY, stepCount);
		// top
		mSolo.drag(currentDrawingSurfaceBitmap.getWidth() / 2, currentDrawingSurfaceBitmap.getWidth() / 2,
				verticalLineStartY, dragTopBoundToY, stepCount);
		// left
		mSolo.drag(horizontalLineStartX, dragLeftBoundToX, currentDrawingSurfaceBitmap.getHeight() / 2,
				currentDrawingSurfaceBitmap.getHeight() / 2, stepCount);
		// right
		mSolo.drag(horizontalLineStartX + lineLeangth, dragRightBoundToX, currentDrawingSurfaceBitmap.getHeight() / 2,
				currentDrawingSurfaceBitmap.getHeight() / 2, stepCount);

		// get Bitmap??

		cropBoundHeightYBottom = (Integer) PrivateAccess.getMemberValue(CropTool.class,
				PaintroidApplication.CURRENT_TOOL, "mCropBoundHeightYBottom");
		cropBoundHeightYTop = (Integer) PrivateAccess.getMemberValue(CropTool.class, PaintroidApplication.CURRENT_TOOL,
				"mCropBoundHeightYTop");
		cropBoundWidthXLeft = (Integer) PrivateAccess.getMemberValue(CropTool.class, PaintroidApplication.CURRENT_TOOL,
				"mCropBoundWidthXLeft");
		cropBoundWidthXRight = (Integer) PrivateAccess.getMemberValue(CropTool.class,
				PaintroidApplication.CURRENT_TOOL, "mCropBoundWidthXRight");
		/*
		 * assertEquals("Bottom Bound not correct,after drag_and_drop", cropBoundHeightYBottom, dragBottomBoundToY);
		 * assertEquals("Top Bound not correct,after drag_and_drop", cropBoundHeightYTop, dragTopBoundToY);
		 * assertEquals("Left Bound not correct,after drag_and_drop", cropBoundWidthXLeft, dragLeftBoundToX);
		 * assertEquals("Right Bound not correct,after drag_and_drop", cropBoundWidthXRight, dragRightBoundToX);
		 */
		assertFalse("Bottom Bound same as before", cropBoundHeightYBottom == (verticalLineStartY + lineLeangth - 1));
		// assertFalse("Top Bound same as before", cropBoundHeightYTop == verticalLineStartY);
		assertFalse("Left Bound same as before", cropBoundWidthXLeft == horizontalLineStartX);
		assertFalse("Right Bound same as before", cropBoundWidthXRight == horizontalLineStartX + lineLeangth - 1);

		/*
		 * // just for testing convertFromCanvasToScreen - methode Point point = new Point(100, 100);
		 * PaintroidApplication.CURRENT_PERSPECTIVE.convertFromScreenToCanvas(point); Point screenPoint =
		 * at.tugraz.ist.paintroid.test.utils.Utils.convertFromCanvasToScreen(point,
		 * PaintroidApplication.CURRENT_PERSPECTIVE);
		 */

	}

	@Test
	public void testManualCroppingTakeEdges() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();

		int lineLeangth = (currentDrawingSurfaceBitmap.getWidth() / 2);
		int lineWidth = 10;
		int horizontalLineStartX = (currentDrawingSurfaceBitmap.getWidth() / 4);
		int horizontalLineStartY = (currentDrawingSurfaceBitmap.getHeight() / 2);
		int verticalLineStartX = (currentDrawingSurfaceBitmap.getWidth() / 2);
		int verticalLineStartY = (currentDrawingSurfaceBitmap.getHeight() / 2 - lineLeangth / 2);
		int stepCount = 2;

		int[] pixelsColorArray = new int[lineWidth * lineLeangth];
		for (int indexColorArray = 0; indexColorArray < pixelsColorArray.length; indexColorArray++) {
			pixelsColorArray[indexColorArray] = Color.BLACK;
		}

		currentDrawingSurfaceBitmap.setPixels(pixelsColorArray, 0, lineLeangth, horizontalLineStartX,
				horizontalLineStartY, lineLeangth, lineWidth);

		currentDrawingSurfaceBitmap.setPixels(pixelsColorArray, 0, lineWidth, verticalLineStartX, verticalLineStartY,
				lineWidth, lineLeangth);

		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnText(mMainActivity.getString(R.string.button_crop));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.CROP);
		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		int cropBoundWidthXLeft = (Integer) PrivateAccess.getMemberValue(CropTool.class,
				PaintroidApplication.CURRENT_TOOL, "mCropBoundWidthXLeft");
		int cropBoundWidthXRight = (Integer) PrivateAccess.getMemberValue(CropTool.class,
				PaintroidApplication.CURRENT_TOOL, "mCropBoundWidthXRight");
		int cropBoundHeightYTop = (Integer) PrivateAccess.getMemberValue(CropTool.class,
				PaintroidApplication.CURRENT_TOOL, "mCropBoundHeightYTop");
		int cropBoundHeightYBottom = (Integer) PrivateAccess.getMemberValue(CropTool.class,
				PaintroidApplication.CURRENT_TOOL, "mCropBoundHeightYBottom");

		assertEquals("Bottom Bound not correct", cropBoundHeightYBottom, (verticalLineStartY + lineLeangth - 1));
		assertEquals("Top Bound not correct", cropBoundHeightYTop, verticalLineStartY);
		assertEquals("Left Bound not correct", cropBoundWidthXLeft, horizontalLineStartX);
		assertEquals("Right Bound not correct", cropBoundWidthXRight, horizontalLineStartX + lineLeangth - 1);

		int dragTopLeftToX = 20;
		int dragTopLeftToY = 20;
		int dragBottomRightToX = 20;
		int dragBottomRightToY = 20;

	}

	private int hasCroppingTimedOut() {
		int croppingTimeoutCounter = 0;
		int maximumCroppingTimeoutCounts = 30;
		Integer[][] croppingBounds = new Integer[4][2];
		try {
			croppingBounds[0][0] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundWidthXLeft");
			croppingBounds[1][0] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundWidthXRight");
			croppingBounds[2][0] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundHeightYTop");
			croppingBounds[3][0] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundHeightYBottom");

			for (; croppingTimeoutCounter < maximumCroppingTimeoutCounts; croppingTimeoutCounter++) {
				Thread.yield();
				croppingBounds[0][1] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
						PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundWidthXLeft");
				croppingBounds[1][1] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
						PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundWidthXRight");
				croppingBounds[2][1] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
						PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundHeightYTop");
				croppingBounds[3][1] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
						PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundHeightYBottom");

				if (croppingBounds[0][0].equals(croppingBounds[0][1])
						&& croppingBounds[1][0].equals(croppingBounds[1][1])
						&& croppingBounds[2][0].equals(croppingBounds[2][1])
						&& croppingBounds[3][0].equals(croppingBounds[3][1])) {
					break;
				} else {
					croppingBounds[0][0] = croppingBounds[0][1];
					croppingBounds[1][0] = croppingBounds[1][1];
					croppingBounds[2][0] = croppingBounds[2][1];
					croppingBounds[3][0] = croppingBounds[3][1];

					mSolo.sleep(CROPPING_TIMOUT);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.toString(), false);
		}
		if (croppingTimeoutCounter >= maximumCroppingTimeoutCounts) {
			return croppingTimeoutCounter;
		}
		return -1;
	}
}
