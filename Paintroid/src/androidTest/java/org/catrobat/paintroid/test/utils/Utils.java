/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.utils;

import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.test.InstrumentationRegistry;
import android.util.DisplayMetrics;

import org.catrobat.paintroid.NavigationDrawerMenuActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.ui.Perspective;

public final class Utils {

	private Utils() {
	}

	public static synchronized Point convertFromCanvasToScreen(Point canvasPoint, Perspective currentPerspective) {
		Float surfaceCenterX = currentPerspective.surfaceCenterX;
		Float surfaceScale = currentPerspective.surfaceScale;
		Float surfaceTranslationX = currentPerspective.surfaceTranslationX;
		Float surfaceCenterY = currentPerspective.surfaceCenterY;
		Float surfaceTranslationY = currentPerspective.surfaceTranslationY;

		Float mInitialTranslationY = currentPerspective.initialTranslationY;

		Point screenPoint = new Point();
		screenPoint.x = (int) ((canvasPoint.x + surfaceTranslationX - surfaceCenterX) * surfaceScale + surfaceCenterX);
		screenPoint.y = (int) ((canvasPoint.y + surfaceTranslationY - surfaceCenterY) * surfaceScale + surfaceCenterY
				+ Math.abs(mInitialTranslationY));

		return screenPoint;
	}

	public static float getActionbarHeight() {
		Resources resources = InstrumentationRegistry.getTargetContext().getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float density = metrics.density;
		return (NavigationDrawerMenuActivity.ACTION_BAR_HEIGHT * density);
	}

	public static float getStatusbarHeight() {
		int statusBarHeight = 0;
		Resources resources = InstrumentationRegistry.getTargetContext().getResources();
		int resourceId = resources.getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = resources.getDimensionPixelSize(resourceId);
		}
		return statusBarHeight;
	}

	public static PointF getSurfacePointFromScreenPoint(PointF screenPoint) {

		return new PointF(screenPoint.x, screenPoint.y - getActionbarHeight() - getStatusbarHeight());
	}

	public static PointF getCanvasPointFromScreenPoint(PointF screenPoint) {
		return PaintroidApplication.perspective
				.getCanvasPointFromSurfacePoint(getSurfacePointFromScreenPoint(screenPoint));
	}
}
