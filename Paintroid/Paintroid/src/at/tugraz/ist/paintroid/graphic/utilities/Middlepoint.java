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

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint.Cap;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;

public class Middlepoint extends Tool {

	public Middlepoint(Tool tool) {
		super(tool);
	}

	@Override
	public boolean singleTapEvent(DrawingSurface drawingSurface) {
		return true;
	}

	@Override
	public void draw(Canvas view_canvas, Cap shape, int stroke_width, int color) {
		if (state == ToolState.ACTIVE) {
			DrawFunctions.setPaint(linePaint, Cap.ROUND, toolStrokeWidth, primaryColor, true, null);
			view_canvas.drawLine(position.x, position.y, 0, position.y, linePaint);
			view_canvas.drawLine(position.x, position.y, this.surfaceSize.x, position.y, linePaint);
			view_canvas.drawLine(position.x, position.y, position.x, 0, linePaint);
			view_canvas.drawLine(position.x, position.y, position.x, this.surfaceSize.y, linePaint);
			DrawFunctions.setPaint(linePaint, Cap.ROUND, toolStrokeWidth, secundaryColor, true, new DashPathEffect(
					new float[] { 10, 20 }, 0));
			view_canvas.drawLine(position.x, position.y, 0, position.y, linePaint);
			view_canvas.drawLine(position.x, position.y, this.surfaceSize.x, position.y, linePaint);
			view_canvas.drawLine(position.x, position.y, position.x, 0, linePaint);
			view_canvas.drawLine(position.x, position.y, position.x, this.surfaceSize.y, linePaint);
		}
	}
}
