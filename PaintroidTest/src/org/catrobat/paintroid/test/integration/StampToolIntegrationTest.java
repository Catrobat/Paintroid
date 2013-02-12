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

package org.catrobat.paintroid.test.integration;

import org.catrobat.paintroid.MenuFileActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.Tool.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.tools.implementation.StampTool;
import org.catrobat.paintroid.ui.implementation.DrawingSurfaceImplementation;
import org.catrobat.paintroid.ui.implementation.PerspectiveImplementation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.Window;

public class StampToolIntegrationTest extends BaseIntegrationTestClass {

	private static final float ACTION_BAR_HEIGHT = MenuFileActivity.ACTION_BAR_HEIGHT;

	private Bitmap mCurrentDrawingSurfaceBitmap;

	public StampToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
		try {
			mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
					PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap");
		} catch (Exception whatever) {
			whatever.printStackTrace();
			fail(whatever.toString());
		}

	}

	@Override
	@After
	protected void tearDown() throws Exception {
		Thread.sleep(1000);
		super.tearDown();
		Thread.sleep(500);
	}

	private void stampTool() {
		selectTool(ToolType.STAMP);
	}

	@Test
	public void testStampOutsideDrawingSurface() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
				PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap");

		PrivateAccess.setMemberValue(PerspectiveImplementation.class, PaintroidApplication.CURRENT_PERSPECTIVE,
				"mSurfaceScale", new Float(0.25));

		Float screenDensity = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				PaintroidApplication.CURRENT_PERSPECTIVE, "mScreenDensity");

		stampTool();

		Tool currentTool = PaintroidApplication.CURRENT_TOOL;
		StampTool stampTool = (StampTool) currentTool;

		mSolo.sleep(1000);

		int mBitmapWidth = PaintroidApplication.DRAWING_SURFACE.getBitmapWidth();
		int mBitmapHeight = PaintroidApplication.DRAWING_SURFACE.getBitmapHeight();
		float mSurfaceCenterX = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				PaintroidApplication.CURRENT_PERSPECTIVE, "mSurfaceCenterX");
		float mSurfaceCenterY = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				PaintroidApplication.CURRENT_PERSPECTIVE, "mSurfaceCenterY");

		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxWidth", mBitmapWidth);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxHeight", mBitmapHeight);
		PointF toolPosition = new PointF(mSurfaceCenterX, mSurfaceCenterY);

		PrivateAccess.setMemberValue(BaseToolWithShape.class, stampTool, "mToolPosition", toolPosition);

		float actionbarHeight = ACTION_BAR_HEIGHT * screenDensity;

		int statusBarHeight = getStatusbarHeight();

		mSolo.clickOnScreen(toolPosition.x, toolPosition.y + actionbarHeight + statusBarHeight);

		mSolo.sleep(5000);
	}

	private int getStatusbarHeight() {
		Rect rectangle = new Rect();
		Window window = mSolo.getCurrentActivity().getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
		return (rectangle.top);
	}
}