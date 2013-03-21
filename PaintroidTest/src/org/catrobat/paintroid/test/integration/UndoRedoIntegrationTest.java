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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;

import android.graphics.Bitmap;
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

		ImageButton undoButton1 = (ImageButton) mSolo.getView(R.id.btn_status_undo);
		Bitmap bitmap1 = ((BitmapDrawable) undoButton1.getDrawable()).getBitmap();

		mSolo.clickOnView(mButtonTopUndo);
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

		ImageButton redoButton1 = (ImageButton) mSolo.getView(R.id.btn_status_redo);
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
		Bitmap bitmap4 = ((BitmapDrawable) redoButton1.getDrawable()).getBitmap();
		assertNotSame(bitmap1, bitmap4);

	}

	public void testPreserveZoomAndMoveAfterUndo() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		// DrawingSurface drawingSurface = (DrawingSurface) getActivity().findViewById(R.id.drawingSurfaceView);
		int xCoord = 100;
		int yCoord = 200;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);
		int colorOriginal = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);

		// fill bitmap
		selectTool(ToolType.FILL);
		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnScreen);
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);
		mSolo.sleep(4000);

		// move & zoom
		float scale = 0.5f;
		PaintroidApplication.perspective.setScale(scale); // done this way since robotium does not support > 1
															// touch event
		mSolo.clickOnView(mButtonTopTool); // switch to move-tool
		mSolo.drag(pointOnScreen.x, pointOnScreen.x + 20, pointOnScreen.y, pointOnScreen.y + 20, 1);

		float translationXBeforeUndo = (Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, PRIVATE_ACCESS_TRANSLATION_X);
		float translationYBeforeUndo = (Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, PRIVATE_ACCESS_TRANSLATION_X);

		// press undo
		mSolo.clickOnView(mButtonTopUndo);
		mSolo.sleep(1000);

		// check perspective and undo
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

	public void testPreserveZoomAndMoveAfterRedo() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		int xCoord = 100;
		int yCoord = 200;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);
		int colorOriginal = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);

		// fill bitmap
		selectTool(ToolType.FILL);
		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnScreen);
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);
		mSolo.sleep(4000);

		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterFill);

		// press undo
		mSolo.clickOnView(mButtonTopUndo);
		mSolo.sleep(1000);

		int colorAfterUndo = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);
		assertEquals("Pixel color should be the same", colorOriginal, colorAfterUndo);

		// move & zoom
		float scale = 0.5f;
		PaintroidApplication.perspective.setScale(scale); // done this way since robotium does not support > 1
															// touch event
		mSolo.clickOnView(mButtonTopTool); // switch to move-tool
		mSolo.drag(pointOnScreen.x, pointOnScreen.x + 20, pointOnScreen.y, pointOnScreen.y + 20, 1);

		float translationXBeforeUndo = (Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, PRIVATE_ACCESS_TRANSLATION_X);
		float translationYBeforeUndo = (Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, PRIVATE_ACCESS_TRANSLATION_X);

		// press redo
		mSolo.clickOnView(mButtonTopRedo);
		mSolo.sleep(3000);

		// check perspective and redo
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
}
