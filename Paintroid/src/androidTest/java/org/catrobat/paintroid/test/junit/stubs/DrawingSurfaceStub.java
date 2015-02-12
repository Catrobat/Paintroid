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

package org.catrobat.paintroid.test.junit.stubs;

import org.catrobat.paintroid.ui.DrawingSurface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.view.SurfaceHolder;

public class DrawingSurfaceStub extends DrawingSurface {

	public DrawingSurfaceStub(Context context) {
		super(context);
	}

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
	public Bitmap getBitmapCopy() {
		return mBitmap;
	}

	@Override
	public int getPixel(PointF coordinate) {
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

	// @Override
	// public void requestDoDrawPause() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void requestDoDrawStart() {
	// // TODO Auto-generated method stub
	//
	// }

}
