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

package org.catrobat.paintroid.test.utils;

import org.catrobat.paintroid.OptionsMenuActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.ui.Perspective;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class Utils {
	public static final String TAG = "PAINTROID";

	protected static final float ACTION_BAR_HEIGHT = 50.0f;

	public static float getStatusbarHeight(Activity activity) {
		float actionbarHeight = ACTION_BAR_HEIGHT * activity.getResources().getDisplayMetrics().density;
		return actionbarHeight;
	}

	public static int[] bitmapToPixelArray(Bitmap bitmap) {
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		int pixelArray[] = new int[bitmapWidth * bitmapHeight];
		bitmap.getPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
		return pixelArray;
	}

	public static synchronized Point convertFromCanvasToScreen(Point canvasPoint, Perspective currentPerspective)
			throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		Float surfaceCenterX = (Float) PrivateAccess.getMemberValue(Perspective.class, currentPerspective,
				"mSurfaceCenterX");
		Float surfaceScale = (Float) PrivateAccess.getMemberValue(Perspective.class, currentPerspective,
				"mSurfaceScale");
		Float surfaceTranslationX = (Float) PrivateAccess.getMemberValue(Perspective.class, currentPerspective,
				"mSurfaceTranslationX");
		Float surfaceCenterY = (Float) PrivateAccess.getMemberValue(Perspective.class, currentPerspective,
				"mSurfaceCenterY");
		Float surfaceTranslationY = (Float) PrivateAccess.getMemberValue(Perspective.class, currentPerspective,
				"mSurfaceTranslationY");

		Float mInitialTranslationY = (Float) PrivateAccess.getMemberValue(Perspective.class, currentPerspective,
				"mInitialTranslationY");

		Point screenPoint = new Point();
		screenPoint.x = (int) ((canvasPoint.x + surfaceTranslationX - surfaceCenterX) * surfaceScale + surfaceCenterX);
		screenPoint.y = (int) ((canvasPoint.y + surfaceTranslationY - surfaceCenterY) * surfaceScale + surfaceCenterY + Math
				.abs(mInitialTranslationY));

		return screenPoint;
	}

	public static boolean isScreenLocked(Context context) {
		KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		return keyguardManager.inKeyguardRestrictedInputMode();
	}

	public static float getActionbarHeight() {
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = ((WindowManager) PaintroidApplication.applicationContext
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		display.getMetrics(metrics);
		float density = metrics.density;
		return (OptionsMenuActivity.ACTION_BAR_HEIGHT * density);
	}

	public static float getStatusbarHeight() {

		int statusbarheight = 0;
		int resourceId = PaintroidApplication.applicationContext.getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			statusbarheight = PaintroidApplication.applicationContext.getResources().getDimensionPixelSize(resourceId);
		}
		return statusbarheight;

	}

	public static PointF getSurfacePointFromScreenPoint(PointF screenPoint) {

		return new PointF(screenPoint.x, screenPoint.y - getActionbarHeight() - getStatusbarHeight());
	}

	public static PointF getCanvasPointFromScreenPoint(PointF screenPoint) {
		return PaintroidApplication.perspective
				.getCanvasPointFromSurfacePoint(getSurfacePointFromScreenPoint(screenPoint));
	}
}
