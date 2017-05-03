/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.integration;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageButton;

public class UndoRedoIntegrationTest extends BaseIntegrationTestClass {

	private static final String PRIVATE_ACCESS_TRANSLATION_X = "mSurfaceTranslationX";

	public UndoRedoIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
	}

	public void testDisableEnableUndo() {

		ImageButton undoButton1 = (ImageButton) mSolo.getView(R.id.btn_top_undo);
		Bitmap bitmap1 = ((BitmapDrawable) undoButton1.getDrawable()).getBitmap();

		mSolo.clickOnView(mButtonTopUndo);
		mSolo.waitForDialogToClose();

		Bitmap bitmap2 = ((BitmapDrawable) undoButton1.getDrawable()).getBitmap();
		assertEquals(bitmap1, bitmap2);

		PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2);

		mSolo.clickOnScreen(point.x, point.y);

		Bitmap bitmap3 = ((BitmapDrawable) undoButton1.getDrawable()).getBitmap();
		assertNotSame(bitmap1, bitmap3);

		mSolo.clickOnView(mButtonTopUndo);
		Bitmap bitmap4 = ((BitmapDrawable) undoButton1.getDrawable()).getBitmap();
		assertEquals(bitmap1, bitmap4);

	}

	public void testDisableEnableRedo() {

		ImageButton redoButton1 = (ImageButton) mSolo.getView(R.id.btn_top_redo);
		Bitmap bitmap1 = ((BitmapDrawable) redoButton1.getDrawable()).getBitmap();

		mSolo.clickOnView(mButtonTopRedo);
		Bitmap bitmap2 = ((BitmapDrawable) redoButton1.getDrawable()).getBitmap();
		assertEquals(bitmap1, bitmap2);

		PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2);

		mSolo.clickOnScreen(point.x, point.y);

		Bitmap bitmap3 = ((BitmapDrawable) redoButton1.getDrawable()).getBitmap();
		assertEquals(bitmap1, bitmap3);

		mSolo.clickOnView(mButtonTopUndo);
		mSolo.waitForDialogToClose();
		Bitmap bitmap4 = ((BitmapDrawable) redoButton1.getDrawable()).getBitmap();
		assertEquals(bitmap1, bitmap4);

	}

	public void testPreserveZoomAndMoveAfterUndo() throws SecurityException, NoSuchFieldException,
			IllegalAccessException {

		int xCoord = 100;
		int yCoord = 200;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);
		int colorOriginal = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);

		selectTool(ToolType.FILL);

		Point pointOnScreen = Utils.convertFromCanvasToScreen(new Point((int)pointOnBitmap.x, (int)pointOnBitmap.y), PaintroidApplication.perspective);
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);

		float scale = 0.5f;
		PaintroidApplication.perspective.setScale(scale); // done this way since robotium does not support > 1

		float moveX = 20.0f;
		float moveY = 20.0f;
		PointF startPointOne = new PointF(pointOnScreen.x, pointOnScreen.y);
		PointF startPointTwo = new PointF(pointOnScreen.x, pointOnScreen.y + 10);
		PointF endPointOne = new PointF(startPointOne.x + moveX, startPointOne.y + moveY);
		PointF endPointTwo = new PointF(startPointTwo.x + moveY, startPointTwo.y + moveY);
		mSolo.swipe(startPointOne, startPointTwo, endPointOne, endPointTwo);

		float translationXBeforeUndo = (Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, PRIVATE_ACCESS_TRANSLATION_X);
		float translationYBeforeUndo = (Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, PRIVATE_ACCESS_TRANSLATION_X);

		mSolo.clickOnView(mButtonTopUndo);
		mSolo.waitForDialogToClose();

		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals("Pixel color should be the same", colorOriginal, colorAfterFill);

		float translationXAfterUndo = (Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, PRIVATE_ACCESS_TRANSLATION_X);
		assertEquals("X-Translation should stay the same after undo", translationXBeforeUndo, translationXAfterUndo);

		float translationYAfterUndo = (Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, PRIVATE_ACCESS_TRANSLATION_X);
		assertEquals("Y-Translation should stay the same after undo", translationYBeforeUndo, translationYAfterUndo);
		assertEquals("Scale should stay the same after undo", PaintroidApplication.perspective.getScale(), scale);
	}

	public void testPreserveZoomAndMoveAfterRedo() throws SecurityException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		PaintroidApplication.perspective.setScale(1.0f);

		int xCoord = mScreenHeight / 2;
		int yCoord = mScreenWidth / 2;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);
		int colorOriginal = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);

		selectTool(ToolType.FILL);
		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();

		Point pointOnScreen = Utils.convertFromCanvasToScreen(new Point((int)pointOnBitmap.x, (int)pointOnBitmap.y), PaintroidApplication.perspective);
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);
		mSolo.waitForDialogToClose(TIMEOUT);

		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterFill);

		mSolo.clickOnView(mButtonTopUndo);
		mSolo.waitForDialogToClose();

		int colorAfterUndo = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals("Pixel color should be the same", colorOriginal, colorAfterUndo);

		float scale = 0.5f;
		PaintroidApplication.perspective.setScale(scale); // done this way since robotium does not support > 1

		float moveX = 20.0f;
		float moveY = 20.0f;
		PointF startPointOne = new PointF(pointOnScreen.x, pointOnScreen.y);
		PointF startPointTwo = new PointF(pointOnScreen.x, pointOnScreen.y + 10);
		PointF endPointOne = new PointF(startPointOne.x + moveX, startPointOne.y + moveY);
		PointF endPointTwo = new PointF(startPointTwo.x + moveY, startPointTwo.y + moveY);
		mSolo.swipe(startPointOne, startPointTwo, endPointOne, endPointTwo);

		float translationXBeforeUndo = (Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, PRIVATE_ACCESS_TRANSLATION_X);
		float translationYBeforeUndo = (Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, PRIVATE_ACCESS_TRANSLATION_X);

		mSolo.clickOnView(mButtonTopRedo);
		mSolo.waitForDialogToClose();

		int colorAfterRedo = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterRedo);

		float translationXAfterUndo = (Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, PRIVATE_ACCESS_TRANSLATION_X);
		assertEquals("X-Translation should stay the same after undo", translationXBeforeUndo, translationXAfterUndo);

		float translationYAfterUndo = (Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, PRIVATE_ACCESS_TRANSLATION_X);
		assertEquals("Y-Translation should stay the same after undo", translationYBeforeUndo, translationYAfterUndo);
		assertEquals("Scale should stay the same after undo", PaintroidApplication.perspective.getScale(), scale);
	}

	public void testUndoProgressDialogIsShowing() {
		assertTrue("This test does not check progress dialog correctly", false); // TODO: check/write also other progress dialog tests for correct testing!!
		ImageButton undoButton = (ImageButton) mSolo.getView(R.id.btn_top_undo);

		PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2);
		mSolo.clickOnScreen(point.x, point.y);
		mSolo.waitForView(undoButton);
		mSolo.clickOnView(undoButton);

		mSolo.waitForDialogToClose();
		assertFalse("Progress Dialog is still showing", IndeterminateProgressDialog.getInstance().isShowing());
	}

	public void testRedoProgressDialogIsClosing() {
		ImageButton undoButton = (ImageButton) mSolo.getView(R.id.btn_top_undo);
		ImageButton redoButton = (ImageButton) mSolo.getView(R.id.btn_top_redo);

		PointF point = PaintroidApplication.perspective.getSurfacePointFromCanvasPoint(
				new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2, mCurrentDrawingSurfaceBitmap.getHeight() / 2));
		PointF screenPoint = getScreenPointFromSurfaceCoordinates(point.x, point.y);

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);

		selectTool(ToolType.FILL);
		PaintroidApplication.currentTool.changePaintColor(Color.BLUE);
		point = PaintroidApplication.perspective.getSurfacePointFromCanvasPoint(
				new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 4, mCurrentDrawingSurfaceBitmap.getHeight() / 4));
		screenPoint = getScreenPointFromSurfaceCoordinates(point.x, point.y);
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);

		mSolo.waitForView(undoButton);
		mSolo.clickOnView(undoButton);

		mSolo.waitForView(redoButton);
		mSolo.clickOnView(redoButton);

		mSolo.waitForDialogToClose();
		assertFalse("Progress Dialog is still showing", IndeterminateProgressDialog.getInstance().isShowing());

	}
}