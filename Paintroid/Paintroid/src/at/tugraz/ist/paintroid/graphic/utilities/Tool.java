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
import android.graphics.Point;
import android.graphics.Paint.Cap;

/**
 * Class managing the cursor's behavior
 * 
 * Status: refactored 12.03.2011
 * @author PaintroidTeam
 * @version 6.0.4b
 */
public abstract class Tool {
	
	protected ToolState state;

	protected Point position;
	
	public enum ToolState {
		INACTIVE, ACTIVE, DRAW;
	}
	
	protected Point screenSize;
	
	protected Paint drawPaint;
	
	protected Paint linePaint;
	
	protected final int primaryColor = Color.BLACK;
	
	protected final int secundaryColor = Color.YELLOW;
	
	protected final int CursorSize = 50;
	
	protected final int CursorStrokeWidth = 5;
	
	protected float zoomLevel;
	
	public Tool(){
		this.position = new Point(0, 0);
		this.state = ToolState.INACTIVE;
		this.screenSize = new Point(0, 0);
		this.drawPaint = new Paint();
		this.drawPaint.setDither(true);
		this.drawPaint.setStyle(Paint.Style.STROKE);
		this.drawPaint.setStrokeJoin(Paint.Join.ROUND);
		this.linePaint = new Paint(this.drawPaint);
	}
	
	
	/**
	 * get the cursor's state
	 * 
	 * @return value of state
	 */
	public ToolState getState()
	{
		return state;
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
	
	public boolean singleTapEvent(){
		return false;
	}
	
	public boolean doubleTapEvent(int x, int y, float zoomLevel){
		return false;
	}
	
	/**
	 * sets the cursor's state inactive
	 */
	public void deactivate()
	{
		this.state = ToolState.INACTIVE;
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
	
	public void draw(Canvas view_canvas, Cap shape, int stroke_width, int color){}
	
}
