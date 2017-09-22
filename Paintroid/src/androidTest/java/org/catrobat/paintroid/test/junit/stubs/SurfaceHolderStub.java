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

package org.catrobat.paintroid.test.junit.stubs;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.Surface;
import android.view.SurfaceHolder;

public class SurfaceHolderStub implements SurfaceHolder {
	public static final float WIDTH = 160;
	public static final float HEIGHT = 90;

	private final Canvas canvas;

	public SurfaceHolderStub() {
		canvas = new Canvas(Bitmap.createBitmap((int) WIDTH, (int) HEIGHT, Bitmap.Config.ARGB_8888));
	}

	public Canvas getCanvas() {
		return canvas;
	}

	@Override
	public void addCallback(Callback callback) {

	}

	@Override
	public Surface getSurface() {
		return null;
	}

	@Override
	public Rect getSurfaceFrame() {
		return new Rect(0, 0, (int) WIDTH, (int) HEIGHT);
	}

	@Override
	public boolean isCreating() {
		return false;
	}

	@Override
	public Canvas lockCanvas() {
		return canvas;
	}

	@Override
	public Canvas lockCanvas(Rect dirty) {
		return canvas;
	}

	@Override
	public void removeCallback(Callback callback) {

	}

	@Override
	public void setFixedSize(int width, int height) {

	}

	@Override
	public void setFormat(int format) {

	}

	@Override
	public void setKeepScreenOn(boolean screenOn) {

	}

	@Override
	public void setSizeFromLayout() {

	}

	@Override
	public void setType(int type) {

	}

	@Override
	public void unlockCanvasAndPost(Canvas canvas) {

	};
}
