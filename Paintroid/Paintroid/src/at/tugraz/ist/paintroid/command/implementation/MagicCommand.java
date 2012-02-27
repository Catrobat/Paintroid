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
		mColorPixel = new Point((int) coordinate.x, (int) coordinate.y);
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		Log.d(PaintroidApplication.TAG, "MagicCommand.run");
		int pixelColor = bitmap.getPixel(mColorPixel.x, mColorPixel.y);
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		int bitmapPixels = bitmapHeight * bitmapWidth;

		int[] pixelArray = new int[bitmapPixels];

		bitmap.getPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

		for (int index = 0; index < bitmapPixels; index++) {
			if (pixelColor == pixelArray[index]) {
				pixelArray[index] = mPaint.getColor();
			}
		}

		bitmap.setPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
	}

	@Override
	public boolean isUndoable() {
		return false;
	}
}
