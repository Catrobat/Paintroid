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

package org.catrobat.paintroid.ui;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.view.SurfaceHolder;

public interface DrawingSurface extends SurfaceHolder.Callback {

	public void resetBitmap(Bitmap bitmap);

	public void setBitmap(Bitmap bitmap);

	public Bitmap getBitmap();

	public int getBitmapColor(PointF coordinate);

	public abstract int getBitmapWidth();

	public abstract int getBitmapHeight();

	public abstract void getPixels(int[] pixels, int offset, int stride, int x,
			int y, int width, int height);

	public abstract void requestDoDrawPause();

	public abstract void requestDoDrawStart();
}
