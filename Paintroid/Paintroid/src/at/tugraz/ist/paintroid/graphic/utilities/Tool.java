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
import at.tugraz.ist.paintroid.graphic.DrawingSurface;

/**
 * Base class for special tools like cursor or middlepoint
 * 
 * Status: refactored 12.03.2011
 * @author PaintroidTeam
 * @version 6.0.4b
 */
public abstract class Tool {
	
	protected ToolState state;

	protected Point position;
	protected Point startPosition;
	
	public enum ToolState {
		INACTIVE, ACTIVE, DRAW;
	}
	
	protected Point screenSize;
	
	protected Paint linePaint;
	
	protected final int primaryColor = Color.BLACK;
	
	protected final int secundaryColor = Color.YELLOW;
	
	protected final int toolStrokeWidth = 5;
	
	protected float zoomX;
	
	protected float zoomY;
	
	// distance between tool center and edge of screen when the scrolling should be triggered
	protected int distanceFromScreenEdgeToScroll;
	
	protected final int scrollSpeed = 20;
	
	/**
	 * Constructor
	 * 
	 * @param tool old tool (copies member screensize)
	 */
	public Tool(Tool tool){
		initialize();
		this.screenSize = tool.getScreenSize();
		this.position.x = this.screenSize.x/2;
		this.position.y = this.screenSize.y/2;
		this.distanceFromScreenEdgeToScroll = (int)(this.screenSize.x*0.1);
		this.startPosition.x = this.position.x;
		this.startPosition.y = this.position.y;
	}
	
	/**
	 * Constructor
	 * 
	 */
	public Tool(){
		initialize();
	}
	
	/**
	 * Initializes the member variables
	 * 
	 */
	private void initialize()
	{
		this.position = new Point(0, 0);
		this.startPosition = new Point(0, 0);
		this.state = ToolState.INACTIVE;
		this.screenSize = new Point(0, 0);
		this.linePaint = new Paint();
		this.linePaint.setDither(true);
		this.linePaint.setStyle(Paint.Style.STROKE);
		this.linePaint.setStrokeJoin(Paint.Join.ROUND);
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
	 * @param delta_to_scroll if >0, tool is on edge of the bitmap then scroll bitmap for this amount
	 */
	public void movePosition(float delta_x, float delta_y, Point delta_to_scroll)
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
		if(position.x < distanceFromScreenEdgeToScroll)
		{
			delta_to_scroll.x = -scrollSpeed;
		}
		else if(position.x >= this.screenSize.x-distanceFromScreenEdgeToScroll)
		{
			delta_to_scroll.x = scrollSpeed;
		}
		if(position.y < distanceFromScreenEdgeToScroll)
		{
			delta_to_scroll.y = -scrollSpeed;
		}
		else if(position.y >= this.screenSize.y-distanceFromScreenEdgeToScroll)
		{
			delta_to_scroll.y = scrollSpeed;
		} 
	}
	
	/**
	 * single tap got performed
	 * 
	 * @param drawingSurface Drawing surface
	 * @return true if the event is consumed, else false
	 */	
	public boolean singleTapEvent(DrawingSurface drawingSurface) {
		return false;
	}
	
	/**
	 * double tap got performed
	 * 
	 * @return true if event is used
	 */
	public boolean doubleTapEvent(int x, int y, float zoomX, float zoomY){
		return false;
	}
	
	/**
	 * sets the cursor's state active and sets its position to
	 * the middle of the screen;
	 * 
	 * @param coordinates initial coordinates for the tool
	 */
	public void activate()
	{
		this.state = ToolState.ACTIVE;
		this.position = new Point(screenSize.x / 2, screenSize.y / 2);
		this.startPosition = new Point(screenSize.x / 2, screenSize.y / 2);
	}
	
	/**
	 * sets the cursor's state active and sets its position
	 * 
	 * @param coordinates initial coordinates for the tool
	 */
	public void activate(Point coordinates)
	{
		this.state = ToolState.ACTIVE;
		this.position = coordinates;
		this.startPosition.x = position.x;
		this.startPosition.y = position.y;
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
	
	/**
	 * resets position to startPosition
	 * 
	 */	
	public void reset() {
		position.x = startPosition.x;
		position.y = startPosition.y;
	}	
	
	/**
	 * returns the screen size
	 * 
	 * @return screen size
	 */
	public Point getScreenSize()
	{
		return this.screenSize;
	}
	
	public abstract void draw(Canvas view_canvas, Cap shape, int stroke_width, int color);

	
}
