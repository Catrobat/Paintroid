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
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class CropToolIntegrationTest extends BaseIntegrationTestClass {

	private final int CROPPING_TIMOUT = 5000;

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
		mSolo.sleep(200);
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
		mSolo.sleep(200);
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
		assertTrue("Waiting for tool to change -> MainActivity", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.CROP);

		int croppingTimeoutCounter = 0;
		int maximumCroppingTimeoutCounts = 30;
		for (; croppingTimeoutCounter < maximumCroppingTimeoutCounts; croppingTimeoutCounter++) {
			if (mSolo.getCurrentProgressBars().size() > 0 || croppingTimeoutCounter <= 1) {
				if (croppingTimeoutCounter != 0)
					if (mSolo.searchText(mSolo.getString(R.string.crop_progress_text), 1, true) == false)
						break;
				mSolo.sleep(CROPPING_TIMOUT);
			} else {
				break;
			}

		}
		if (croppingTimeoutCounter >= maximumCroppingTimeoutCounts) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnView(mToolBarButtonTwo);
		mSolo.sleep(200);
		currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Wrong width after cropping ", 1, currentDrawingSurfaceBitmap.getWidth());
		assertEquals("Wrong height after cropping ", 1, currentDrawingSurfaceBitmap.getHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE, currentDrawingSurfaceBitmap.getPixel(0, 0));
	}

	// @Test
	// public void testCroppingPerformanceTest() throws SecurityException, IllegalArgumentException,
	// NoSuchFieldException,
	// IllegalAccessException {
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	//
	// Bitmap currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
	//
	// int originalWidth = currentDrawingSurfaceBitmap.getWidth();
	// int originalHeight = currentDrawingSurfaceBitmap.getHeight();
	//
	// Random randomGenerator = new Random();
	//
	// mSolo.clickOnView(mToolBarButtonMain);
	// assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));
	//
	// mSolo.clickOnText(mMainActivity.getString(R.string.button_crop));
	// assertTrue("Waiting for tool to change -> MainActivity", mSolo.waitForActivity("MainActivity", TIMEOUT));
	// assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.CROP);
	//
	// int croppingTimeoutCounter = hasCroppingTimedOut();
	// if (croppingTimeoutCounter >= 0) {
	// fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
	// }
	//
	// Long[] algorithmDuration = { 0L, 0L, 0L, 0L };
	//
	// int testRuns = 5;
	//
	// for (int randomPointsRun = 0; randomPointsRun < testRuns; randomPointsRun++) {
	// for (int pointCounts = randomGenerator.nextInt(10); pointCounts >= 0; pointCounts--) {
	// currentDrawingSurfaceBitmap.setPixel(randomGenerator.nextInt(originalWidth - 1),
	// randomGenerator.nextInt(originalHeight - 1), Color.BLUE);
	// }
	// PrivateAccess.setMemberValue(CropTool.class, this, "CROPPING_ALGORITHM",
	// CropTool.CROPPING_ALGORITHM_TYPES.SNAIL_CORRECT);
	// long croppingStartTime = System.currentTimeMillis();
	//
	// for (int algorithmTestCount = 0; algorithmTestCount < testRuns; algorithmTestCount++) {
	// mSolo.clickOnView(mToolBarButtonOne);
	// croppingTimeoutCounter = hasCroppingTimedOut();
	// if (croppingTimeoutCounter >= 0) {
	// fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
	// }
	// }
	// algorithmDuration[0] += System.currentTimeMillis() - croppingStartTime;
	// PrivateAccess.setMemberValue(CropTool.class, this, "CROPPING_ALGORITHM",
	// CropTool.CROPPING_ALGORITHM_TYPES.RANDOM_AND_CORRECT);
	//
	// croppingStartTime = System.currentTimeMillis();
	// for (int algorithmTestCount = 0; algorithmTestCount < testRuns; algorithmTestCount++) {
	// mSolo.clickOnView(mToolBarButtonOne);
	// croppingTimeoutCounter = hasCroppingTimedOut();
	// if (croppingTimeoutCounter >= 0) {
	// fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
	// }
	// }
	// algorithmDuration[1] += System.currentTimeMillis() - croppingStartTime;
	// }
	// fail("Algorithm test count(randompoints/runs): " + testRuns + " Duration 1: " + algorithmDuration[0]
	// + "ms Duration 2: " + algorithmDuration[1] + "ms");
	// }

	private int hasCroppingTimedOut() {
		int croppingTimeoutCounter = 0;
		int maximumCroppingTimeoutCounts = 30;
		for (; croppingTimeoutCounter < maximumCroppingTimeoutCounts; croppingTimeoutCounter++) {
			if (mSolo.getCurrentProgressBars().size() > 0 || croppingTimeoutCounter <= 1) {
				if (croppingTimeoutCounter != 0)
					if (mSolo.searchText(mSolo.getString(R.string.crop_progress_text), 1, true) == false)
						break;
				mSolo.sleep(CROPPING_TIMOUT);
			} else {
				break;
			}

		}
		if (croppingTimeoutCounter >= maximumCroppingTimeoutCounts) {
			return croppingTimeoutCounter;
		}
		return -1;
	}
}
