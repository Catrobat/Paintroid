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

import java.util.Vector;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * This static class provides functions for drawing
 *       
 * Status: refactored 20.02.2011
 * @author PaintroidTeam
 * @version 0.6.4b
 */
public class DrawFunctions {

	/**
	 * Get the chosen pixel on bitmap
	 * 
	 * @param x Screen coordinate
	 * @param y Screen coordinate
	 * @param rec_bitmap Bitmap size
	 * @param rect_canvas Canvas size
	 * 
	 * @return Coordinates on the bitmap
	 */
	public static Vector<Integer> RealCoordinateValue(float x, float y,
			Rect rect_bitmap, Rect rect_canvas) {

		// The actual viewed bitmap-section resolution(!) in pixel
		float res_x = rect_bitmap.width();
		float res_y = rect_bitmap.height();

		// Actual touched x/y display coordinates
		float x_display_now = (x - rect_canvas.left);
		float y_display_now = (y - rect_canvas.top);

		// End coordinates from canvas
		float x_end = rect_canvas.width();
		float y_end = rect_canvas.height();

		// Base factor
		// Resolution from actual bitmap view * canvas
		float base_x = res_x / x_end;
		float base_y = res_y / y_end;

		// Actual touched coordinates
		// Display coordinates multiplied with base
		float x_on_bitmap = x_display_now * base_x;
		float y_on_bitmap = y_display_now * base_y;

		// Final coordinates
		// Left-Top corner from actual view + actual touched coordinates
		float x_draw = rect_bitmap.left + x_on_bitmap;
		float y_draw = rect_bitmap.top + y_on_bitmap;

		Vector<Integer> coords = new Vector<Integer>();
		
		coords.add(0, (int) x_draw);
		coords.add(1, (int) y_draw);

		return coords;
	}
	
	public static void setPaint(Paint paint, final Cap currentBrushType,
			final int currentStrokeWidth, final int currentStrokeColor, boolean antialiasingFlag, PathEffect effect)
	{
		if(currentStrokeWidth == 1)
		{
			paint.setAntiAlias(false);
			paint.setStrokeCap(Cap.SQUARE);
		}
		else
		{
			paint.setAntiAlias(antialiasingFlag);
			paint.setStrokeCap(currentBrushType);
		}
		paint.setPathEffect(effect);
		paint.setStrokeWidth(currentStrokeWidth);
		if(currentStrokeColor == Color.TRANSPARENT) {
			paint.setAlpha(0);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		} else {
			paint.setXfermode(null);
			paint.setColor(currentStrokeColor);
		}
	}
}
