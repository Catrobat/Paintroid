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

import android.graphics.PointF;

public class PerspectiveWrapper {

	public synchronized PointF getSurfacePointFromCanvasPoint(PointF canvasPoint) {
		return PaintroidApplication.perspective.getSurfacePointFromCanvasPoint(canvasPoint);
	}

	public float getScale() {
		return PaintroidApplication.perspective.getScale();
	}

	public synchronized PointF getCanvasPointFromSurfacePoint(PointF surfacePoint) {
		return PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(surfacePoint);
	}

	public synchronized void resetScaleAndTranslation() {
		PaintroidApplication.perspective.resetScaleAndTranslation();
	}

	public synchronized void setScale(float scale) {
		PaintroidApplication.perspective.setScale(scale);
	}

	public float getScaleForCenterBitmap() {
		return PaintroidApplication.perspective.getScaleForCenterBitmap();
	}

	public synchronized void multiplyScale(float factor) {
		PaintroidApplication.perspective.multiplyScale(factor);
	}

	public void setSurfaceTranslationX(float translationX) {
		PaintroidApplication.perspective.setSurfaceTranslationX(translationX);
	}

	public void setSurfaceTranslationY(float translationY) {
		PaintroidApplication.perspective.setSurfaceTranslationY(translationY);
	}

	public float getSurfaceTranslationX() {
		return PaintroidApplication.perspective.getSurfaceTranslationX();
	}

	public float getSurfaceTranslationY() {
		return PaintroidApplication.perspective.getSurfaceTranslationY();
	}
}
