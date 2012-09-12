package at.tugraz.ist.paintroid.test.junit.stubs;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.view.SurfaceHolder;
import at.tugraz.ist.paintroid.ui.DrawingSurface;

public class DrawingSurfaceStub implements DrawingSurface {

	public Bitmap mBitmap = null;

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetBitmap(Bitmap bitmap) {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}
		mBitmap = bitmap;
	}

	@Override
	public void setBitmap(Bitmap bitmap) {
		mBitmap = bitmap;

	}

	@Override
	public Bitmap getBitmap() {
		return mBitmap;
	}

	@Override
	public int getBitmapColor(PointF coordinate) {
		return mBitmap.getPixel((int) coordinate.x, (int) coordinate.y);
	}

	@Override
	public int getBitmapWidth() {
		return mBitmap.getWidth();
	}

	@Override
	public int getBitmapHeight() {
		return mBitmap.getHeight();
	}

	@Override
	public void getPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
	}

}
