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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import at.tugraz.ist.paintroid.deprecated.graphic.DrawingSurface;

@Deprecated
public class Cursor extends Tool {

	protected Paint drawPaint;

	protected final int cursorSize = 50;

	public Cursor() {
		super();
		this.drawPaint = new Paint(this.linePaint);
	}

	public Cursor(Tool tool) {
		super(tool);
		this.drawPaint = new Paint(this.linePaint);
	}

	@Override
	public boolean doubleTapEvent(int x, int y) {
		switch (this.state) {
			case INACTIVE:
				this.state = ToolState.ACTIVE;
				this.position.x = x;
				this.position.y = y;
				return true;
			case ACTIVE:
			case DRAW:
				this.state = ToolState.INACTIVE;
				return true;

			default:
				break;
		}
		return false;
	}

	@Override
	public boolean singleTapEvent(DrawingSurface drawingSurface) {
		switch (this.state) {
			case ACTIVE:
				this.state = ToolState.DRAW;
				return true;
			case DRAW:
				this.state = ToolState.ACTIVE;
				return true;

			default:
				break;
		}
		return false;
	}

	@Override
	public void draw(Canvas view_canvas, Cap shape, int stroke_width, int color) {
		DrawFunctions.setPaint(drawPaint, Cap.ROUND, toolStrokeWidth, color, true, null);

		int zoomX = (int) DrawingSurface.Perspective.zoom;
		int zoomY = (int) DrawingSurface.Perspective.zoom;

		if (Color.red(color) < Color.red(primaryColor) + 0x30 && Color.blue(color) < Color.blue(primaryColor) + 0x30
				&& Color.green(color) < Color.green(primaryColor) + 0x30) {
			DrawFunctions.setPaint(linePaint, Cap.ROUND, toolStrokeWidth, secundaryColor, true, null);
		} else {
			DrawFunctions.setPaint(linePaint, Cap.ROUND, toolStrokeWidth, primaryColor, true, null);
		}
		stroke_width /= 2;
		if (state == ToolState.ACTIVE || state == ToolState.DRAW) {
			switch (shape) {
				case ROUND:
					view_canvas.drawCircle(position.x, position.y, stroke_width * zoomX + 2, linePaint);
					view_canvas.drawCircle(position.x, position.y, stroke_width * zoomX, drawPaint);
					break;
				case SQUARE:
					view_canvas.drawRect(position.x - (stroke_width) * zoomX - 2, position.y - (stroke_width) * zoomY
							- 2, position.x + (stroke_width) * zoomX + 2, position.y + (stroke_width) * zoomY + 2,
							linePaint);
					view_canvas.drawRect(position.x - (stroke_width) * zoomX, position.y - (stroke_width) * zoomY,
							position.x + (stroke_width) * zoomX, position.y + (stroke_width) * zoomY, drawPaint);
					break;
				default:
					break;
			}
			DrawFunctions.setPaint(linePaint, Cap.ROUND, toolStrokeWidth, primaryColor, true, null);
			view_canvas.drawLine(position.x - stroke_width * zoomX - cursorSize, position.y, position.x - stroke_width
					* zoomX, position.y, linePaint);
			view_canvas.drawLine(position.x + stroke_width * zoomX + cursorSize, position.y, position.x + stroke_width
					* zoomX, position.y, linePaint);
			view_canvas.drawLine(position.x, position.y - stroke_width * zoomY - cursorSize, position.x, position.y
					- stroke_width * zoomY, linePaint);
			view_canvas.drawLine(position.x, position.y + stroke_width * zoomY + cursorSize, position.x, position.y
					+ stroke_width * zoomY, linePaint);
			DrawFunctions.setPaint(linePaint, Cap.ROUND, toolStrokeWidth, secundaryColor, true, new DashPathEffect(
					new float[] { 10, 20 }, 0));
			view_canvas.drawLine(position.x - stroke_width * zoomX - cursorSize, position.y, position.x - stroke_width
					* zoomX, position.y, linePaint);
			view_canvas.drawLine(position.x + stroke_width * zoomX + cursorSize, position.y, position.x + stroke_width
					* zoomX, position.y, linePaint);
			view_canvas.drawLine(position.x, position.y - stroke_width * zoomY - cursorSize, position.x, position.y
					- stroke_width * zoomY, linePaint);
			view_canvas.drawLine(position.x, position.y + stroke_width * zoomY + cursorSize, position.x, position.y
					+ stroke_width * zoomY, linePaint);
		}
	}
}
