/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid;

import android.graphics.Bitmap;
import android.graphics.PointF;

public class DrawingSurfaceWrapper {

	public int getBitmapWidth() {
		return PaintroidApplication.drawingSurface.getBitmapWidth();
	}

	public int getBitmapHeight() {
		return PaintroidApplication.drawingSurface.getBitmapHeight();
	}

	public void refreshDrawingSurface() {
		PaintroidApplication.drawingSurface.refreshDrawingSurface();
	}

	public float getWidth() {
		return PaintroidApplication.drawingSurface.getWidth();
	}

	public float getHeight() {
		return PaintroidApplication.drawingSurface.getHeight();
	}

	public int getPixel(PointF coordinate) {
		return PaintroidApplication.drawingSurface.getPixel(coordinate);
	}

	public synchronized Bitmap getBitmapCopy() {
		return PaintroidApplication.drawingSurface.getBitmapCopy();
	}

	public void destroyDrawingCache() {
		PaintroidApplication.drawingSurface.destroyDrawingCache();
	}

	public synchronized void setBitmap(Bitmap bitmap) {
		PaintroidApplication.drawingSurface.setBitmap(bitmap);
	}
}
