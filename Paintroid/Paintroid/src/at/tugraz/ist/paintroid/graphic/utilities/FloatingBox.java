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

/**
 * Class managing the floating box tools behavior
 * 
 * Status: refactored 12.03.2011
 * @author PaintroidTeam
 * @version 6.0.4b
 */
public class FloatingBox extends Tool {
	
	protected int default_width = 200;
	protected int default_height = 200;
	protected int width;
	protected int height;
	protected float rotation = 0;
	protected float frameTolerance = 5;
	
	public enum FloatingBoxAction {
    NONE, MOVE, RESIZE, ROTATE;
  }
	
	/**
	 * constructor
	 * 
	 * @param tool to copy
	 */
	public FloatingBox(Tool tool) {
		super(tool);
	}

	/**
	 * single tap while in floating box mode
	 */
	public boolean singleTapEvent(){
		return true;
	}
	
	/**
	 * double tap while in floating box mode
	 */
	public boolean doubleTapEvent(){
		return true;
	}

	/**
	 * draws the floating box
	 * 
	 * @param view_canvas canvas on which to be drawn
	 * @param shape shape of the cursor to be drawn
	 * @param stroke_width stroke_width of the cursor to be drawn
	 * @param color color of the cursor to be drawn
	 */
	public void draw(Canvas view_canvas, Cap shape, int stroke_width, int color)
	{
		if(state == ToolState.ACTIVE)
		{
			DrawFunctions.setPaint(linePaint, Cap.ROUND, toolStrokeWidth, primaryColor, true, null);
			view_canvas.drawRect(position.x-this.width/2, position.y+this.height/2, position.x+this.width/2, position.y-this.height/2, linePaint);
			DrawFunctions.setPaint(linePaint, Cap.ROUND, toolStrokeWidth, secundaryColor, true, new DashPathEffect(new float[] { 10, 20 }, 0));
			view_canvas.drawRect(position.x-this.width/2, position.y+this.height/2, position.x+this.width/2, position.y-this.height/2, linePaint);
		}
	}
	
	/**
	 * Rotates the box
	 * 
	 * @param delta_degree degrees to rotate
	 */
	public void rotate(float delta_degree)
	{
	  this.rotation += delta_degree;
	}
	
	/**
   * Resizes the box
   * 
   * @param delta_x resize width
   * @param delta_y resize height
   */
  public void resize(float delta_x, float delta_y)
  {
    this.width += delta_x;
    this.height += delta_y;
  }
	
	/**
	 * Resets the box to the default position
	 * 
	 */
	public void reset()
	{
	  this.width = default_width;
	  this.height = default_width;
	  this.position.x = this.screenSize.x/2;
    this.position.y = this.screenSize.y/2;
    this.rotation = 0;
	}
	
	/**
	 * Gets the action the user has selected through clicking on a specific
	 * position of the floating box
	 * 
	 * @param clickCoordinates coordinates the user has touched
	 * @return action to perform
	 */
	public FloatingBoxAction getAction(float clickCoordinatesY, float clickCoordinatesX)
	{
	  // Move (within box)
	  if(clickCoordinatesX+frameTolerance < this.position.x+this.width/2 &&
	      clickCoordinatesX-frameTolerance > this.position.x-this.width/2 &&
	      clickCoordinatesY+frameTolerance < this.position.y+this.height/2 &&
	      clickCoordinatesY-frameTolerance > this.position.y-this.height/2)
	  {
	    return FloatingBoxAction.MOVE;
	  }
	  
	  // Resize (on frame)
	  if(clickCoordinatesX < this.position.x+this.width/2+frameTolerance &&
	      clickCoordinatesX > this.position.x-this.width/2-frameTolerance &&
        clickCoordinatesY < this.position.y+this.height/2+frameTolerance &&
        clickCoordinatesY > this.position.y-this.height/2-frameTolerance)
    {
      return FloatingBoxAction.RESIZE;
    }
	  
	  // Rotate (on symbol)
	  //TODO
	  if(false)
    {
      return FloatingBoxAction.ROTATE;
    }
	  
	  // No valid click
	  return FloatingBoxAction.NONE;
	}
}

