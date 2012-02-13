/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.ui.implementation;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import at.tugraz.ist.paintroid.ui.Perspective;

/**
 * The purpose of this class is to provide an independent interface to manipulate the scale and translation of the
 * DrawingSurface. The direct manipulation of the Canvas is synchronized on the SurfaceHolder on which the
 * DrawingSurface must also synchronize its own drawing.
 */
public class DrawingSurfacePerspective implements Perspective {
	private static final long serialVersionUID = 7742690846128292452L;

	public static final float MIN_SCALE = 0.5f;
	public static final float MAX_SCALE = 15f;
	public static final float SCROLL_BORDER = 10f;

	private float surfaceWidth;
	private float surfaceHeight;
	private float surfaceCenterX;
	private float surfaceCenterY;
	private float surfaceScale;
	private float surfaceTranslationX;
	private float surfaceTranslationY;

	public DrawingSurfacePerspective(SurfaceHolder holder) {
		setSurfaceHolder(holder);
	}

	@Override
	public void setSurfaceHolder(SurfaceHolder holder) {
		Rect surfaceFrame = holder.getSurfaceFrame();
		surfaceWidth = surfaceFrame.right;
		surfaceHeight = surfaceFrame.bottom;
		surfaceCenterX = surfaceFrame.exactCenterX();
		surfaceCenterY = surfaceFrame.exactCenterY();
		surfaceScale = 1f;
	}

	@Override
	public void resetScaleAndTranslation() {
		surfaceScale = 1f;
		surfaceTranslationX = 0f;
		surfaceTranslationY = 0f;
	}

	@Override
	public void setScale(float scale) {
		if (scale >= MIN_SCALE) {
			surfaceScale = scale;
		} else {
			surfaceScale = MIN_SCALE;
		}
	}

	@Override
	public void multiplyScale(float factor) {
		surfaceScale *= factor;
		if (surfaceScale < MIN_SCALE) {
			surfaceScale = MIN_SCALE;
		} else if (surfaceScale > MAX_SCALE) {
			surfaceScale = MAX_SCALE;
		}
	}

	@Override
	public void translate(float dx, float dy) {
		surfaceTranslationX += dx / surfaceScale;
		surfaceTranslationY += dy / surfaceScale;

		float xmax = (surfaceWidth - surfaceCenterX - SCROLL_BORDER) / surfaceScale + surfaceCenterX;
		if (surfaceTranslationX > xmax) {
			surfaceTranslationX = xmax;
		} else if (surfaceTranslationX < -xmax) {
			surfaceTranslationX = -xmax;
		}

		float ymax = (surfaceHeight - surfaceCenterY - SCROLL_BORDER) / surfaceScale + surfaceCenterY;
		if (surfaceTranslationY > ymax) {
			surfaceTranslationY = ymax;
		} else if (surfaceTranslationY < -ymax) {
			surfaceTranslationY = -ymax;
		}
	}

	@Override
	public void convertFromScreenToCanvas(Point p) {
		p.x = (int) ((p.x - surfaceCenterX) / surfaceScale + surfaceCenterX - surfaceTranslationX);
		p.y = (int) ((p.y - surfaceCenterY) / surfaceScale + surfaceCenterY - surfaceTranslationY);
	}

	@Override
	public void convertFromScreenToCanvas(PointF p) {
		p.x = (p.x - surfaceCenterX) / surfaceScale + surfaceCenterX - surfaceTranslationX;
		p.y = (p.y - surfaceCenterY) / surfaceScale + surfaceCenterY - surfaceTranslationY;
	}

	@Override
	public void applyToCanvas(Canvas canvas) {
		canvas.scale(surfaceScale, surfaceScale, surfaceCenterX, surfaceCenterY);
		canvas.translate(surfaceTranslationX, surfaceTranslationY);
	}
}
