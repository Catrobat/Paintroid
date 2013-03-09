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

package org.catrobat.paintroid.test.utils;

import org.catrobat.paintroid.ui.Perspective;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;

public class Utils {
	public static final String TAG = "PAINTROID";

	// 50dip in style.xml but need 62 here. must be a 12dip padding somewhere.
	protected static final float ACTION_BAR_HEIGHT = 62.0f;

	public static float getStatusbarHeigt(Activity activity) {
		float actionbarHeight = ACTION_BAR_HEIGHT * activity.getResources().getDisplayMetrics().density;
		return actionbarHeight;
	}

	public static void doWorkaroundSleepForDrawingSurfaceThreadProblem() {
		// This has to be done before every test that leads to a new DrawingSurfaceThread
		// Otherwise two threads (one from before) are running in parallel which sometimes
		// leads to this nasty SEGMENTATION FAULT!
		// try {
		// Thread.sleep(1250);
		// } catch (Exception e) {
		// }
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

		Point screenPoint = new Point();
		screenPoint.x = (int) ((canvasPoint.x + surfaceTranslationX - surfaceCenterX) * surfaceScale + surfaceCenterX);
		screenPoint.y = (int) ((canvasPoint.y + surfaceTranslationY - surfaceCenterY) * surfaceScale + surfaceCenterY);

		return screenPoint;
	}
}
