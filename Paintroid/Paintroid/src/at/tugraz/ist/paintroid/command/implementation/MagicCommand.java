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

package at.tugraz.ist.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import at.tugraz.ist.paintroid.PaintroidApplication;

public class MagicCommand extends BaseCommand {
	protected Point mColorPixel;

	public MagicCommand(Paint paint, PointF coordinate) {
		super(paint);
		if (coordinate != null) {
			mColorPixel = new Point((int) coordinate.x, (int) coordinate.y);
		} else {
			mColorPixel = new Point(-1, -1);
		}
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		if (bitmap == null) {
			return;
		}
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		if ((bitmapWidth <= mColorPixel.x)
				|| (bitmapHeight <= mColorPixel.y || (0 > mColorPixel.x) || (0 > mColorPixel.y))) {
			Log.w(PaintroidApplication.TAG, "Point is out of range " + this.toString());
			return;
		}

		int pixelColor = bitmap.getPixel(mColorPixel.x, mColorPixel.y);
		int bitmapPixels = bitmapHeight * bitmapWidth;
		int colorToReplaceWith = mPaint.getColor();
		if (colorToReplaceWith == pixelColor) {
			Log.i(PaintroidApplication.TAG, "Same colour nothing to replace");
			return;
		}
		int[] pixelArray = new int[bitmapPixels];

		bitmap.getPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

		for (int index = 0; index < bitmapPixels; index++) {
			if (pixelColor == pixelArray[index]) {
				pixelArray[index] = colorToReplaceWith;
			}
		}

		bitmap.setPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
	}
}
