package org.catrobat.paintroid.test.integration;

import org.catrobat.paintroid.R;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageButton;

public class DisableButtonsTest extends BaseIntegrationTestClass {

	public DisableButtonsTest() throws Exception {
		super();
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

		mSolo.clickOnView(mButtonTopRedo);
		Bitmap bitmap5 = ((BitmapDrawable) redoButton1.getDrawable()).getBitmap();
		// assertEquals(bitmap1, bitmap5);

	}
}
