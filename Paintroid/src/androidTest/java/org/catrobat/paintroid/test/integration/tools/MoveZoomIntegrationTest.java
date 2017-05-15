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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
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
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.LinearLayout;


public class MoveZoomIntegrationTest extends BaseIntegrationTestClass {
	private static final String DRAWINGSURFACE_MEMBER_BITMAP = "mWorkingBitmap";

	private static final int LOW_DPI_STATUS_BAR_HEIGHT = 19;
	private static final int MEDIUM_DPI_STATUS_BAR_HEIGHT = 25;
	private static final int HIGH_DPI_STATUS_BAR_HEIGHT = 38;
	private static final int X_DPI_STATUS_BAR_HEIGHT = 50;
	private static final int OFFSET = 1; //0 or 1?

	private static final int MOVE_STEP_COUNT = 10;
	private 		Toolbar tp;

	public MoveZoomIntegrationTest() throws Exception {
		super();
	}

	@Test
	public void testBorderAfterMove() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		moveLeft();
		moveUp();

		moveRight();
		moveDown();


		Bitmap workingBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, DRAWINGSURFACE_MEMBER_BITMAP);

		int width = workingBitmap.getWidth();
		int height = workingBitmap.getHeight();

		int colorPixelUpperLeft = workingBitmap.getPixel(0 + OFFSET, 0 + OFFSET);
		int colorPixelBottomRight = workingBitmap.getPixel(width - 1, height - 1);

		//TODO Refactor this assert after redesign and new dimensions and scales
		//assertEquals("Upper Left Pixel should be black if the borders would have been correct", Color.BLACK,
		//	colorPixelUpperLeft);
		//assertEquals("Bottom Right Pixel should be black if the borders would have been correct", Color.BLACK,
		//		colorPixelBottomRight);
	}

	@Test
	public void testZoomOut() {
		float scaleBeforeZoom = PaintroidApplication.perspective.getScale();

		zoomOut();

		float scaleAfterZoom = PaintroidApplication.perspective.getScale();
		assertTrue("Zooming-out has not worked", scaleBeforeZoom > scaleAfterZoom);
	}

	@Test
	public void testZoomIn() {
		float scaleBeforeZoom = PaintroidApplication.perspective.getScale();

		zoomIn();

		float scaleAfterZoom = PaintroidApplication.perspective.getScale();
		assertTrue("Zooming-in has not worked", scaleBeforeZoom < scaleAfterZoom);
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
			//mSolo.drag(mScreenWidth / 2, 0, mScreenHeight / 2, mScreenHeight / 2, MOVE_STEP_COUNT);
			move(-(mScreenWidth / 2.0f), 0);
		}
	}

	private void moveRight() {
		for (int i = 0; i < 4; i++) {
			//mSolo.drag(0, mScreenWidth / 2, mScreenHeight / 2, mScreenHeight / 2, MOVE_STEP_COUNT);
			move(mScreenWidth / 2.0f, 0);
		}
	}

	private void moveUp() {
		for (int i = 0; i < 4; i++) {
			//mSolo.drag(mScreenWidth / 2, mScreenWidth / 2, mScreenHeight / 2, 0, MOVE_STEP_COUNT);
			move(0, -(mScreenHeight / 2.0f));
		}
	}

	private void moveDown() {
		for (int i = 0; i < 4; i++) {
			//mSolo.drag(mScreenWidth / 2, mScreenWidth / 2, mScreenHeight / 2, mScreenHeight, MOVE_STEP_COUNT);
			move(0, mScreenHeight / 2.0f);
		}
	}

	private void move(float moveX, float moveY) {
		PointF startPointOne = new PointF(mScreenWidth / 2.0f, mScreenHeight / 2.0f - 10);
		PointF startPointTwo = new PointF(mScreenWidth / 2.0f, mScreenHeight / 2.0f + 10);
		PointF endPointOne = new PointF(startPointOne.x + moveX, startPointOne.y + moveY);
		PointF endPointTwo = new PointF(startPointTwo.x + moveX, startPointTwo.y + moveY);
		mSolo.swipe(startPointOne, startPointTwo, endPointOne, endPointTwo);
	}

	private void zoomIn() {
		PointF startPointOne = new PointF(mScreenWidth / 2.0f, mScreenHeight / 2.0f - 10);
		PointF startPointTwo = new PointF(mScreenWidth / 2.0f, mScreenHeight / 2.0f + 10);
		PointF endPointOne = new PointF(startPointOne.x, startPointOne.y - 50);
		PointF endPointTwo = new PointF(startPointTwo.x, startPointTwo.y + 50);
		mSolo.swipe(startPointOne, startPointTwo, endPointOne, endPointTwo);
	}

	private void zoomOut() {
		PointF startPointOne = new PointF(mScreenWidth / 2.0f, mScreenHeight / 2.0f - 60);
		PointF startPointTwo = new PointF(mScreenWidth / 2.0f, mScreenHeight / 2.0f + 60);
		PointF endPointOne = new PointF(startPointOne.x, startPointOne.y + 50);
		PointF endPointTwo = new PointF(startPointTwo.x, startPointTwo.y - 50);
		mSolo.swipe(startPointOne, startPointTwo, endPointOne, endPointTwo);
	}
}
