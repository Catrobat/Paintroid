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

package at.tugraz.ist.paintroid.deprecated.graphic.utilities;

import java.io.File;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Paint.Cap;
import android.util.TypedValue;

@Deprecated
public class DrawFunctions {

	public static final PorterDuffXfermode transparencyXferMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

	public static int dp2px(Context context, int dp) {
		final Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
		return Math.round(px);
	}

	public static void setPaint(Paint paint, final Cap currentBrushType, final int currentStrokeWidth,
			final int currentStrokeColor, boolean antialiasingFlag, PathEffect effect) {
		if (currentStrokeWidth == 1) {
			paint.setAntiAlias(false);
			paint.setStrokeCap(Cap.SQUARE);
		} else {
			paint.setAntiAlias(antialiasingFlag);
			paint.setStrokeCap(currentBrushType);
		}
		paint.setPathEffect(effect);
		paint.setStrokeWidth(currentStrokeWidth);
		paint.setColor(currentStrokeColor);
		if (currentStrokeColor == Color.TRANSPARENT) {
			paint.setXfermode(transparencyXferMode);
		} else {
			paint.setXfermode(null);
		}
	}

	public static Bitmap createBitmapFromUri(String uriString) {
		// First we query the bitmap for dimensions without
		// allocating memory for its pixels.
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		File bitmapFile = new File(uriString);
		if (!bitmapFile.exists()) {
			return null;
		}
		BitmapFactory.decodeFile(uriString, options);

		int width = options.outWidth;
		int height = options.outHeight;

		if (width < 0 || height < 0) {
			return null;
		}

		int size = width > height ? width : height;

		// if the image is too large we subsample it
		if (size > 1000) {

			// we use the thousands digit to dynamically define the sample size
			size = Character.getNumericValue(Integer.toString(size).charAt(0));

			options.inSampleSize = size + 1;
			BitmapFactory.decodeFile(uriString, options);
			width = options.outWidth;
			height = options.outHeight;
		}
		options.inJustDecodeBounds = false;

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		// we have to load each pixel for alpha transparency to work with photos
		int[] pixels = new int[width * height];
		BitmapFactory.decodeFile(uriString, options).getPixels(pixels, 0, width, 0, 0, width, height);

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
}
