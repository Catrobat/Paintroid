/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test.utils;

import android.graphics.Bitmap;
import android.graphics.Point;
import at.tugraz.ist.paintroid.ui.Perspective;
import at.tugraz.ist.paintroid.ui.implementation.PerspectiveImplementation;

public class Utils {
	private Utils() {
	}

	public static boolean arrayEquals(int[] a, int[] b) {
		if (a.length != b.length) {
			return false;
		}
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}

	public static boolean bitmapEquals(Bitmap bmp1, Bitmap bmp2) {
		int[] a = bitmapToPixelArray(bmp1);
		int[] b = bitmapToPixelArray(bmp2);
		return arrayEquals(a, b);
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
		Float surfaceCenterX = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				currentPerspective, "mSurfaceCenterX");
		Float surfaceScale = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class, currentPerspective,
				"mSurfaceScale");
		Float surfaceTranslationX = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				currentPerspective, "mSurfaceTranslationX");
		Float surfaceCenterY = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				currentPerspective, "mSurfaceCenterY");
		Float surfaceTranslationY = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				currentPerspective, "mSurfaceTranslationY");

		Point screenPoint = new Point();
		// screenPoint.x = (int) ((p.x - surfaceCenterX) / surfaceScale + surfaceCenterX - surfaceTranslationX);
		// screenPoint.y = (int) ((p.y - surfaceCenterY) / surfaceScale + surfaceCenterY - surfaceTranslationY);

		screenPoint.x = (int) ((canvasPoint.x + surfaceTranslationX - surfaceCenterX) * surfaceScale + surfaceCenterX);
		screenPoint.y = (int) ((canvasPoint.y + surfaceTranslationY - surfaceCenterY) * surfaceScale + surfaceCenterY);

		return screenPoint;
	}

}
