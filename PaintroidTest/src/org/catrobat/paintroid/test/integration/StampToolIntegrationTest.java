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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.catrobat.paintroid.MenuFileActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.Tool.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.tools.implementation.StampTool;
import org.catrobat.paintroid.ui.button.ToolbarButton.ToolButtonIDs;
import org.catrobat.paintroid.ui.implementation.DrawingSurfaceImplementation;
import org.catrobat.paintroid.ui.implementation.PerspectiveImplementation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.Window;

public class StampToolIntegrationTest extends BaseIntegrationTestClass {

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
		Thread.sleep(1000);
		super.tearDown();
		Thread.sleep(500);
	}

	private void stampTool() {
		selectTool(ToolType.STAMP);
	}

	@Test
	public void testCopyPixel() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		mSolo.clickOnScreen(getSurfaceCenterX(), getSurfaceCenterY() + getActionbarHeight() + getStatusbarHeight() - 25);

		stampTool();

		StampTool stampTool = (StampTool) PaintroidApplication.CURRENT_TOOL;
		PointF toolPosition = new PointF(getSurfaceCenterX(), getSurfaceCenterY());
		PrivateAccess.setMemberValue(BaseToolWithShape.class, stampTool, "mToolPosition", toolPosition);

		mSolo.clickOnScreen(getSurfaceCenterX(), getSurfaceCenterY());
		mSolo.sleep(1000);

		toolPosition.y = toolPosition.y - 100;
		PrivateAccess.setMemberValue(BaseToolWithShape.class, stampTool, "mToolPosition", toolPosition);

		mSolo.sleep(500);
		mSolo.clickOnScreen(getSurfaceCenterX(), getSurfaceCenterY() + getActionbarHeight());
		mSolo.sleep(500);

		Bitmap currentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
				PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap");
		int pixelToControll = currentDrawingSurfaceBitmap.getPixel((int) getSurfaceCenterX(),
				(int) getSurfaceCenterY() - 100);

		assertEquals("Pixel not Black after using Stamp for copying", Color.BLACK, pixelToControll);
	}

	@Test
	public void testStampOutsideDrawingSurface() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
				PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap");

		PrivateAccess.setMemberValue(PerspectiveImplementation.class, PaintroidApplication.CURRENT_PERSPECTIVE,
				"mSurfaceScale", new Float(0.25));

		stampTool();

		StampTool stampTool = (StampTool) PaintroidApplication.CURRENT_TOOL;

		mSolo.sleep(1000);

		int mBitmapWidth = PaintroidApplication.DRAWING_SURFACE.getBitmapWidth();
		int mBitmapHeight = PaintroidApplication.DRAWING_SURFACE.getBitmapHeight();

		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxWidth", mBitmapWidth);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxHeight", mBitmapHeight);
		PointF toolPosition = new PointF(getSurfaceCenterX(), getSurfaceCenterY());

		PrivateAccess.setMemberValue(BaseToolWithShape.class, stampTool, "mToolPosition", toolPosition);

		int statusBarHeight = getStatusbarHeight();

		mSolo.clickOnScreen(toolPosition.x, toolPosition.y + getActionbarHeight() + statusBarHeight);

		mSolo.sleep(2000);

		mSolo.clickOnScreen(toolPosition.x, toolPosition.y + getActionbarHeight() + statusBarHeight);

		mSolo.sleep(5000);
	}

	@Test
	public void testDummyCoverageIncrease() throws IllegalArgumentException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		stampTool();
		StampTool stampTool = (StampTool) PaintroidApplication.CURRENT_TOOL;

		invokeResetInternalState(stampTool);
		invokeAttributeButtonClick(stampTool);
	}

	private float getSurfaceCenterX() {
		float surfaceCenterX = 0.0f;
		try {
			surfaceCenterX = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
					PaintroidApplication.CURRENT_PERSPECTIVE, "mSurfaceCenterX");
		} catch (Exception e) {
			fail("Getting member mSurfaceCenterX failed");
		}
		return (surfaceCenterX);
	}

	private float getSurfaceCenterY() {
		float surfaceCenterY = 0.0f;
		try {
			surfaceCenterY = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
					PaintroidApplication.CURRENT_PERSPECTIVE, "mSurfaceCenterY");
		} catch (Exception e) {
			fail("Getting member mSurfaceCenterY failed");
		}
		return (surfaceCenterY);
	}

	private static void invokeResetInternalState(Object object) throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Method method = object.getClass().getDeclaredMethod("resetInternalState");
		method.setAccessible(true);
		method.invoke(object);
	}

	private void invokeAttributeButtonClick(Object object) throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		Method method = object.getClass().getDeclaredMethod("attributeButtonClick", ToolButtonIDs.class);
		method.setAccessible(true);

		Object[] parameters = new Object[1];
		parameters[0] = ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1;
		method.invoke(object, parameters);
	}

	private int getStatusbarHeight() {
		Rect rectangle = new Rect();
		Window window = mSolo.getCurrentActivity().getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
		return (rectangle.top);
	}

	private int getActionbarHeight() {
		Float screenDensity = 0.0f;
		try {
			screenDensity = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
					PaintroidApplication.CURRENT_PERSPECTIVE, "mScreenDensity");
		} catch (Exception e) {
			fail("Getting member mScreenDensity on Perspective failed");
		}
		float actionbarHeight = MenuFileActivity.ACTION_BAR_HEIGHT * screenDensity;
		return ((int) actionbarHeight);
	}
}