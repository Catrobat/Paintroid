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

package org.catrobat.paintroid.dialog.layerchooser;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Layer {

	Canvas mCanvas;
	Color mColor;
	Paint mPaint;
	int mIndex;
	int mName;
	boolean visible;
	boolean locked;

	public Paint getmPaint() {
		return mPaint;
	}

	public void setmPaint(Paint mPaint) {
		this.mPaint = mPaint;
	}

	public Layer(Canvas mCanvas, Color col) {
		this.mCanvas = mCanvas;
		this.mColor = col;

	}

	public Layer() {
		this.mCanvas = new Canvas();
		this.mColor = new Color();
	}

	public Layer(Paint p) {
		this.mCanvas = new Canvas();
		this.mColor = new Color();
		this.mPaint = p;
	}

	public Color getmColor() {
		return mColor;
	}

	public void setmColor(Color mColor) {
		this.mColor = mColor;
	}

	public void setmCanvas(Canvas mCanvas) {
		this.mCanvas = mCanvas;
	}

	public Canvas getmCanvas() {
		return this.mCanvas;
	}
}
