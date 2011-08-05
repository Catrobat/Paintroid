/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.graphic.utilities;

import java.io.File;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

public class DrawFunctions {

	public static Vector<Integer> screenToImageCoordinates(float x, float y, Rect rect_bitmap, Rect rect_canvas) {

		float res_x = rect_bitmap.width();
		float res_y = rect_bitmap.height();

		float x_display_now = (x - rect_canvas.left);
		float y_display_now = (y - rect_canvas.top);

		float x_end = rect_canvas.width();
		float y_end = rect_canvas.height();

		float base_x = res_x / x_end;
		float base_y = res_y / y_end;

		float x_on_bitmap = x_display_now * base_x;
		float y_on_bitmap = y_display_now * base_y;

		float x_draw = rect_bitmap.left + x_on_bitmap;
		float y_draw = rect_bitmap.top + y_on_bitmap;

		Vector<Integer> coords = new Vector<Integer>();

		coords.add(0, (int) x_draw);
		coords.add(1, (int) y_draw);

		return coords;
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
		if (currentStrokeColor == Color.TRANSPARENT) {
			paint.setAlpha(0);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		} else {
			paint.setXfermode(null);
			paint.setColor(currentStrokeColor);
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
