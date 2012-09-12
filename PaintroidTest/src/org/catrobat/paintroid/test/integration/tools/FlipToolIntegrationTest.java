/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
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
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.Tool.ToolType;
import org.catrobat.paintroid.ui.implementation.DrawingSurfaceImplementation;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;

public class FlipToolIntegrationTest extends BaseIntegrationTestClass {

	private static final int OFFSET = 150;

	public FlipToolIntegrationTest() throws Exception {
		super();
	}

	public void testHorizontalFlip() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		int xPoint = mScreenWidth / 2;
		int yPoint = OFFSET;
		try {
			Bitmap drawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
					PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap");
			drawingSurfaceBitmap.setPixel(xPoint, yPoint, Color.BLACK);
			PrivateAccess.setMemberValue(DrawingSurfaceImplementation.class, PaintroidApplication.DRAWING_SURFACE,
					"mWorkingBitmap", drawingSurfaceBitmap);
		} catch (Exception whatever) {
			whatever.printStackTrace();
			fail("exception: " + whatever.toString());
		}
		// mSolo.clickOnScreen(xPoint, yPoint + Utils.getStatusbarHeigt(getActivity()));
		// mSolo.sleep(500);

		int pixel = PaintroidApplication.DRAWING_SURFACE.getBitmapColor(new PointF(xPoint, yPoint));
		assertEquals("pixel should be black", Color.BLACK, pixel);

		selectTool(ToolType.FLIP);
		mSolo.clickOnView(mMenuBottomParameter1);
		yPoint = PaintroidApplication.DRAWING_SURFACE.getBitmapHeight() - yPoint - 1;
		mSolo.sleep(500);
		pixel = PaintroidApplication.DRAWING_SURFACE.getBitmapColor(new PointF(xPoint, yPoint));
		assertEquals("pixel should be black", Color.BLACK, pixel);
	}

	public void testVerticalFlip() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		int xPoint = OFFSET;
		int yPoint = mScreenHeight / 2;

		try {
			Bitmap drawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
					PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap");
			drawingSurfaceBitmap.setPixel(xPoint, yPoint, Color.BLACK);
			PrivateAccess.setMemberValue(DrawingSurfaceImplementation.class, PaintroidApplication.DRAWING_SURFACE,
					"mWorkingBitmap", drawingSurfaceBitmap);
		} catch (Exception whatever) {
			whatever.printStackTrace();
			fail("exception: " + whatever.toString());
		}
		// mSolo.clickOnScreen(xPoint, yPoint + Utils.getStatusbarHeigt(getActivity()));

		int pixel = PaintroidApplication.DRAWING_SURFACE.getBitmapColor(new PointF(xPoint, yPoint));
		assertEquals("pixel should be black", Color.BLACK, pixel);

		selectTool(ToolType.FLIP);
		mSolo.clickOnView(mMenuBottomParameter2);
		xPoint = PaintroidApplication.DRAWING_SURFACE.getBitmapWidth() - xPoint - 1;
		mSolo.sleep(200);
		pixel = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(xPoint, yPoint);
		assertEquals("pixel should be black", Color.BLACK, pixel);
	}
}
