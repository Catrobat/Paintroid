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
	
	protected float zoomLevel;
	
	protected Point screenSize;
	
	public enum CursorState {
		INACTIVE, ACTIVE, DRAW;
	}
	
	public Cursor()
	{
		this.position = new Point(0, 0);
		this.state = CursorState.INACTIVE;
		this.screenSize = new Point(0, 0);
		this.paint = new Paint();
		this.paint.setDither(true);
		this.paint.setStyle(Paint.Style.STROKE);
		this.paint.setStrokeJoin(Paint.Join.ROUND);
	}
	
	public boolean doubleTapEvent(int x, int y, float zoomLevel)
	{
		switch (this.state) {
		case INACTIVE:
			this.state = CursorState.ACTIVE;
			this.position.x = x;
			this.position.y = y;
			this.zoomLevel = zoomLevel;
			return true;
		case ACTIVE:
		case DRAW:
			this.state = CursorState.INACTIVE;
			return true;

		default:
			break;
		}
		return false;
	}
	
	public boolean singleTapEvent()
	{
		switch (this.state) {
		case ACTIVE:
			this.state = CursorState.DRAW;
			return true;
		case DRAW:
			this.state = CursorState.ACTIVE;
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
		stroke_width *= zoomLevel;
		if(state == CursorState.ACTIVE || state == CursorState.DRAW)
		{
			switch(shape)
			{
			case ROUND:
				view_canvas.drawCircle(position.x, position.y, stroke_width*3/4, paint);
				break;
			case SQUARE:
				view_canvas.drawRect(position.x-stroke_width*3/4, position.y-stroke_width*3/4, position.x+stroke_width*3/4, position.y+stroke_width*3/4, paint);
				break;
			default:
				break;
			}
			view_canvas.drawLine(position.x-stroke_width-CursorSize, position.y, position.x+stroke_width+CursorSize, position.y, paint);
			view_canvas.drawLine(position.x, position.y-stroke_width-CursorSize, position.x, position.y+stroke_width+CursorSize, paint);
		}
	}
	
	public void movePosition(float delta_x, float delta_y)
	{	
		position.x += (int)delta_x;
		position.y += (int)delta_y;
		if(position.x < 0)
		{
			position.x = 0;
		}
		if(position.y < 0)
		{
			position.y = 0;
		}
		if(position.x >= this.screenSize.x)
		{
			position.x = this.screenSize.x-1;
		}
		if(position.y >= this.screenSize.y)
		{
			position.y = this.screenSize.y-1;
		}
	}
	
	public void deactivate()
	{
		this.state = CursorState.INACTIVE;
	}

	public Point getPosition() {
		return position;
	}

	public void setScreenSize(Point screenSize) {
		this.screenSize = screenSize;
	}

}
