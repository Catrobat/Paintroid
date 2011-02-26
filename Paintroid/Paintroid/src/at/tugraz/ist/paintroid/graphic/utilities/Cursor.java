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
 * Class managing the cursor's behavior
 * 
 * Status: refactored 24.02.2011
 * @author PaintroidTeam
 * @version 6.0.4b
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
	
	/**
	 * Constructor
	 * 
	 * sets the default values for the member variables
	 */	
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
	/**
	 * sets the cursor's state after a double tap occurred
	 *  
	 * @param x the x-coordinate of the tap
	 * @param y the y-coordinate of the tap
	 * @param zoomLevel the actual zoom-level 
	 * @return true if the event is consumed, else false
	 */
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
	
	/**
	 * sets the cursor's state after a single tap occurred
	 * 
	 * @return true if the event is consumed, else false
	 */	
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
	
	/**
	 * get the cursor's state
	 * 
	 * @return value of state
	 */
	public CursorState getState()
	{
		return state;
	}
	
	/**
	 * draws the cursor
	 * 
	 * @param view_canvas canvas on which to be drawn
	 * @param shape shape of the cursor to be drawn
	 * @param stroke_width stroke_width of the cursor to be drawn
	 * @param color color of the cursor to be drawn
	 */
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
	
	/**
	 * moves the cursor's position (limited by the device's borders)
	 * 
	 * @param delta_x moves in x-direction
	 * @param delta_y moves in y-direction
	 */
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
	
	/**
	 * sets the cursor's state inactive
	 */
	public void deactivate()
	{
		this.state = CursorState.INACTIVE;
	}

	/**
	 * get position of the cursor
	 * 
	 * @return position of the cursor
	 */
	public Point getPosition() {
		return position;
	}

	
	/**
	 * sets screen size which sets cursor's borders
	 * 
	 * @param screenSize screen size of device
	 */
	public void setScreenSize(Point screenSize) {
		this.screenSize = screenSize;
	}

}
