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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;

/**
 * 
 * @author stefan
 *
 */
public class Cursor {
	
	protected CursorState state;
	
	protected Point position;
	
	protected Paint paint;
	
	protected final int CursorSize = 50;
	
	protected final int CursorStrokeWidth = 5;
	
	public enum CursorState {
		INACTIVE, ACTIVE, DRAW;
	}
	
	public Cursor()
	{
		position = new Point(0, 0);
		state = CursorState.INACTIVE;
		paint = new Paint();
		paint.setDither(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
	}
	
	public boolean doubleTapEvent(int x, int y)
	{
		switch (state) {
		case INACTIVE:
			state = CursorState.ACTIVE;
			position.x = x;
			position.y = y;
			return true;
		case ACTIVE:
		case DRAW:
			state = CursorState.INACTIVE;
			return true;

		default:
			break;
		}
		return false;
	}
	
	public boolean singleTapEvent()
	{
		switch (state) {
		case ACTIVE:
			state = CursorState.DRAW;
			return true;
		case DRAW:
			state = CursorState.ACTIVE;
			return true;

		default:
			break;
		}
		return false;
	}
	
	public CursorState getState()
	{
		return state;
	}
	
	public void draw(Canvas view_canvas, Cap shape, int stroke_width, int color)
	{
		DrawFunctions.setPaint(paint, Cap.ROUND, CursorStrokeWidth, color, true);
		if(state == CursorState.ACTIVE || state == CursorState.DRAW)
		{
			switch(shape)
			{
			case ROUND:
				view_canvas.drawCircle(position.x, position.y, stroke_width, paint);
				break;
			case SQUARE:
				view_canvas.drawRect(position.x-stroke_width, position.y-stroke_width, position.x+stroke_width, position.y+stroke_width, paint);
				break;
			default:
				break;
			}
			view_canvas.drawLine(position.x-CursorSize, position.y, position.x+CursorSize, position.y, paint);
			view_canvas.drawLine(position.x, position.y-CursorSize, position.x, position.y+CursorSize, paint);
		}
	}

}
