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

		PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2);

		mSolo.clickOnScreen(point.x, point.y);

		ImageButton undoButton2 = (ImageButton) mSolo.getView(R.id.btn_status_undo);
		Bitmap bitmap2 = ((BitmapDrawable) undoButton2.getDrawable()).getBitmap();
		assertNotSame(bitmap1, bitmap2);

		mSolo.clickOnView(mButtonTopUndo);
		ImageButton undoButton3 = (ImageButton) mSolo.getView(R.id.btn_status_undo);
		Bitmap bitmap3 = ((BitmapDrawable) undoButton3.getDrawable()).getBitmap();
		assertEquals(bitmap1, bitmap3);

	}

	public void testDisableEnableRedo() {

		ImageButton redoButton1 = (ImageButton) mSolo.getView(R.id.btn_status_redo);
		Bitmap bitmap1 = ((BitmapDrawable) redoButton1.getDrawable()).getBitmap();

		PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2);

		mSolo.clickOnScreen(point.x, point.y);

		ImageButton redoButton2 = (ImageButton) mSolo.getView(R.id.btn_status_redo);
		Bitmap bitmap2 = ((BitmapDrawable) redoButton2.getDrawable()).getBitmap();
		assertEquals(bitmap1, bitmap2);

		mSolo.clickOnView(mButtonTopUndo);
		ImageButton redoButton3 = (ImageButton) mSolo.getView(R.id.btn_status_redo);
		Bitmap bitmap3 = ((BitmapDrawable) redoButton3.getDrawable()).getBitmap();
		assertNotSame(bitmap1, bitmap3);

		mSolo.clickOnView(mButtonTopRedo);
		ImageButton redoButton4 = (ImageButton) mSolo.getView(R.id.btn_status_redo);
		Bitmap bitmap4 = ((BitmapDrawable) redoButton4.getDrawable()).getBitmap();
		// assertEquals(bitmap1, bitmap4);

	}
}
