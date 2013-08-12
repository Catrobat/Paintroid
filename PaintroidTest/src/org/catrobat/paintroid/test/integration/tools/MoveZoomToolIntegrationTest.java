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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class MoveZoomToolIntegrationTest extends BaseIntegrationTestClass {
	private static final String DRAWINGSURFACE_MEMBER_BITMAP = "mWorkingBitmap";

	private static final int LOW_DPI_STATUS_BAR_HEIGHT = 19;
	private static final int MEDIUM_DPI_STATUS_BAR_HEIGHT = 25;
	private static final int HIGH_DPI_STATUS_BAR_HEIGHT = 38;
	private static final int X_DPI_STATUS_BAR_HEIGHT = 50;
	private static final int OFFSET = 1;

	private static final int MOVE_STEP_COUNT = 10;

	public MoveZoomToolIntegrationTest() throws Exception {
		super();
	}

	@Test
	public void testBorderAfterZoomOut() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		float actionbarHeight = Utils.getActionbarHeight();
		float statusbarHeight = getStatusBarHeight(getActivity());

		selectTool(ToolType.MOVE);
		moveLeft();
		moveUp();
		selectTool(ToolType.BRUSH);
		mSolo.clickOnScreen(Perspective.SCROLL_BORDER, actionbarHeight + statusbarHeight + Perspective.SCROLL_BORDER);

		selectTool(ToolType.MOVE);
		moveRight();
		moveDown();
		selectTool(ToolType.BRUSH);
		mSolo.clickOnScreen(mScreenWidth - Perspective.SCROLL_BORDER, mScreenHeight - Perspective.SCROLL_BORDER
				- actionbarHeight);

		Bitmap workingBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, DRAWINGSURFACE_MEMBER_BITMAP);

		int width = workingBitmap.getWidth();
		int height = workingBitmap.getHeight();

		int colorPixelUpperLeft = workingBitmap.getPixel(0 + OFFSET, 0 + OFFSET);
		int colorPixelBottomRight = workingBitmap.getPixel(width - 1, height - 1);

		assertEquals("Upper Left Pixel should be black if the borders would have been correct", Color.BLACK,
				colorPixelUpperLeft);
		assertEquals("Bottom Right Pixel should be black if the borders would have been correct", Color.BLACK,
				colorPixelBottomRight);
	}

	@Test
	public void testZoomOut() {
		float scaleBeforeZoom = PaintroidApplication.perspective.getScale();
		selectTool(ToolType.MOVE);
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.sleep(200);
		float scaleAfterZoom = PaintroidApplication.perspective.getScale();

		assertTrue("Zooming-out has not worked", scaleBeforeZoom > scaleAfterZoom);
	}

	@Test
	public void testZoomIn() {
		float scaleBeforeZoom = PaintroidApplication.perspective.getScale();
		selectTool(ToolType.MOVE);

		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.sleep(200);
		float scaleAfterZoom = PaintroidApplication.perspective.getScale();

		assertTrue("Zooming-in has not worked", scaleBeforeZoom < scaleAfterZoom);
	}

	@Test
	public void testToolStaysTheSameAfterSwitch() {
		selectTool(ToolType.RECT);
		mSolo.sleep(500);
		PointF fromPoint = new PointF(((3 * mScreenWidth) / 5), ((3 * mScreenHeight) / 5));
		PointF toPoint = new PointF(((4 * mScreenWidth) / 5), ((4 * mScreenHeight) / 5));
		mSolo.drag(fromPoint.x, toPoint.x, fromPoint.y, toPoint.y, 1);
		mSolo.sleep(1000);

		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.RECT);
		Tool oldTool = PaintroidApplication.currentTool;
		mSolo.clickOnView(mButtonTopTool);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.MOVE);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.RECT);

		Tool newTool = PaintroidApplication.currentTool;
		assertTrue("Tool is a different object after switch", oldTool == newTool);
	}

	@Test
	public void testSwitchingBetweenZoomAndMoveTool() {
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.BRUSH);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.MOVE);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.BRUSH);
		selectTool(ToolType.ZOOM);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.ZOOM);
		selectTool(ToolType.ZOOM);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.ZOOM);
		selectTool(ToolType.MOVE);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.MOVE);
		selectTool(ToolType.MOVE);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.MOVE);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.BRUSH);
		selectTool(ToolType.RECT);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.RECT);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.MOVE);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.RECT);
	}

	private int getStatusBarHeight(Context context) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
				displayMetrics);

		int statusBarHeight;

		switch (displayMetrics.densityDpi) {
			case DisplayMetrics.DENSITY_HIGH:
				statusBarHeight = HIGH_DPI_STATUS_BAR_HEIGHT;
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
				break;
			case DisplayMetrics.DENSITY_LOW:
				statusBarHeight = LOW_DPI_STATUS_BAR_HEIGHT;
				break;
			case DisplayMetrics.DENSITY_XHIGH:
				statusBarHeight = X_DPI_STATUS_BAR_HEIGHT;
				break;
			default:
				statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
		}

		return (statusBarHeight);
	}

	private void moveLeft() {
		for (int i = 0; i < 4; i++) {
			mSolo.drag(mScreenWidth / 2, 0, mScreenHeight / 2, mScreenHeight / 2, MOVE_STEP_COUNT);
		}
	}

	private void moveRight() {
		for (int i = 0; i < 4; i++) {
			mSolo.drag(0, mScreenWidth / 2, mScreenHeight / 2, mScreenHeight / 2, MOVE_STEP_COUNT);
		}
	}

	private void moveUp() {
		for (int i = 0; i < 4; i++) {
			mSolo.drag(mScreenWidth / 2, mScreenWidth / 2, mScreenHeight / 2, 0, MOVE_STEP_COUNT);
		}
	}

	private void moveDown() {
		for (int i = 0; i < 4; i++) {
			mSolo.drag(mScreenWidth / 2, mScreenWidth / 2, mScreenHeight / 2, mScreenHeight, MOVE_STEP_COUNT);
		}
	}

}
