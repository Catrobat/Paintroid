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

import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Color;
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

	@Test
	public void testWhenNoPixelIsOnBitmap() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		selectTool(ToolType.CROP);

		assertEquals("Zoom factor is wrong", 0.95f, PaintroidApplication.CURRENT_PERSPECTIVE.getScale());

		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnView(mMenuBottomParameter2);
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

		selectTool(ToolType.CROP);

		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.sleep(500);

		Bitmap newCurrentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Wrong width after cropping ", 1, newCurrentDrawingSurfaceBitmap.getWidth());
		assertEquals("Wrong height after cropping ", 1, newCurrentDrawingSurfaceBitmap.getHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE, newCurrentDrawingSurfaceBitmap.getPixel(0, 0));
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

		selectTool(ToolType.CROP);

		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.sleep(500);
		Bitmap newCurrentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Wrong width after cropping ", originalWidth - 2, newCurrentDrawingSurfaceBitmap.getWidth());
		assertEquals("Wrong height after cropping ", originalHeight - 2, newCurrentDrawingSurfaceBitmap.getHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE, newCurrentDrawingSurfaceBitmap.getPixel(0, 0));
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

		selectTool(ToolType.CROP);

		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.sleep(200);
		assertEquals(currentDrawingSurfaceBitmap, PaintroidApplication.DRAWING_SURFACE.getBitmap());

		Bitmap newCurrentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Wrong width after cropping ", originalWidth, newCurrentDrawingSurfaceBitmap.getWidth());
		assertEquals("Wrong height after cropping ", originalHeight, newCurrentDrawingSurfaceBitmap.getHeight());
	}

	@Test
	public void testIfClickOnCanvasDoesNothing() {
		try {
			assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

			Bitmap currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
			currentDrawingSurfaceBitmap.eraseColor(Color.BLACK);
			int drawingSurfaceOriginalWidth = currentDrawingSurfaceBitmap.getWidth();
			int drawingSurfaceOriginalHeight = currentDrawingSurfaceBitmap.getHeight();
			for (int indexWidth = 0; indexWidth < drawingSurfaceOriginalWidth; indexWidth++) {
				currentDrawingSurfaceBitmap.setPixel(indexWidth, 0, Color.TRANSPARENT);
			}

			selectTool(ToolType.CROP);

			int croppingTimeoutCounter = hasCroppingTimedOut();
			if (croppingTimeoutCounter >= 0) {
				fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
			}

			mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);
			Bitmap newCurrentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
			assertEquals("Width changed:", drawingSurfaceOriginalWidth, newCurrentDrawingSurfaceBitmap.getWidth());
			assertEquals("Height changed:", drawingSurfaceOriginalHeight, newCurrentDrawingSurfaceBitmap.getHeight());
		} catch (Exception whatever) {
			fail(whatever.toString());
		}
	}

	@Test
	public void testSmallBitmapCropping() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		currentDrawingSurfaceBitmap.setPixel(currentDrawingSurfaceBitmap.getWidth() / 2,
				currentDrawingSurfaceBitmap.getHeight() / 2, Color.BLUE);

		selectTool(ToolType.CROP);

		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.sleep(500);
		Bitmap newCurrentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Wrong width after cropping ", 1, newCurrentDrawingSurfaceBitmap.getWidth());
		assertEquals("Wrong height after cropping ", 1, newCurrentDrawingSurfaceBitmap.getHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE, newCurrentDrawingSurfaceBitmap.getPixel(0, 0));
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
