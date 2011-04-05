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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;

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
	// Rotation of the box in degree
	protected float rotation = 0;
	// Tolerance that the resize action is performed if the frame is touched
	protected float frameTolerance = 30;
	// Distance from box frame to rotation symbol
	protected int roationSymbolDistance = 30;
	protected int roationSymbolWidth = 30;
	protected ResizeAction resizeAction;
	protected Bitmap floatingBoxBitmap = null;
	
	public enum FloatingBoxAction {
    NONE, MOVE, RESIZE, ROTATE;
  }
	
	protected enum ResizeAction {
    NONE, TOP, RIGHT, BOTTOM, LEFT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT;
  }
	
	/**
	 * constructor
	 * 
	 * @param tool to copy
	 */
	public FloatingBox(Tool tool) {
		super(tool);
		resizeAction = ResizeAction.NONE;
		reset();
	}

	/**
	 * single tap while in floating box mode
	 * 
	 * @param drawingSurface Drawing surface
	 * @return true if the event is consumed, else false
	 */	
	public boolean singleTapEvent(DrawingSurface drawingSurface) {
		if(state == ToolState.ACTIVE)
		{
			Point minimum = new Point(screenSize);
			Point maximum = new Point(0,0);
			float x_left = position.x-width/2;
			float x_right = position.x+width/2;
			float y_top = position.y-height/2;
			float y_bottom = position.y+height/2;
			PointF[] edges = new PointF[4];
			edges[0] = new PointF(x_left,y_top);
			edges[1] = new PointF(x_left,y_bottom);
			edges[2] = new PointF(x_right,y_top);
			edges[3] = new PointF(x_right,y_bottom);
			
			double rotationRadiant = rotation*Math.PI/180;
			for(int edgePointCounter = 0; edgePointCounter < 4; edgePointCounter++)
			{
				float rotatedX = (float) (this.position.x + Math.cos(rotationRadiant)*(edges[edgePointCounter].x-this.position.x)-Math.sin(rotationRadiant)*(edges[edgePointCounter].y-this.position.y));
				float rotatedY = (float) (this.position.y + Math.sin(rotationRadiant)*(edges[edgePointCounter].x-this.position.x)+Math.cos(rotationRadiant)*(edges[edgePointCounter].y-this.position.y));
				if(minimum.x > rotatedX) minimum.x = (int) rotatedX;
				if(minimum.y > rotatedY) minimum.y = (int) rotatedY;
				if(maximum.x < rotatedX) maximum.x = (int) rotatedX;
				if(maximum.y < rotatedY) maximum.y = (int) rotatedY;
				edges[edgePointCounter] = new PointF(drawingSurface.getPixelCoordinates(rotatedX, rotatedY));
			}
			
			Point bitmap_minimum = drawingSurface.getPixelCoordinates(minimum.x, minimum.y);
			Point bitmap_maximum = drawingSurface.getPixelCoordinates(maximum.x, maximum.y);
			
			Matrix roationMatrix = new Matrix();
			if(rotation != 0)
			{
				roationMatrix.postRotate(-rotation);
			}
			Bitmap rectangleBitmap = Bitmap.createBitmap(drawingSurface.getBitmap(), bitmap_minimum.x, bitmap_minimum.y, bitmap_maximum.x-bitmap_minimum.x, bitmap_maximum.y-bitmap_minimum.y, roationMatrix, true);
			
			for(int edgePointCounter = 0; edgePointCounter < 4; edgePointCounter++)
			{
				edges[edgePointCounter].x -= bitmap_minimum.x;
				edges[edgePointCounter].y -= bitmap_minimum.y;
				float rotatedX = (float) (this.position.x + Math.cos(-rotationRadiant)*(edges[edgePointCounter].x-this.position.x)-Math.sin(-rotationRadiant)*(edges[edgePointCounter].y-this.position.y));
				float rotatedY = (float) (this.position.y + Math.sin(-rotationRadiant)*(edges[edgePointCounter].x-this.position.x)+Math.cos(-rotationRadiant)*(edges[edgePointCounter].y-this.position.y));
				edges[edgePointCounter] = new PointF(drawingSurface.getPixelCoordinates(rotatedX, rotatedY));
			}
			
			roationMatrix = new Matrix();
			if(rotation != 0)
			{
				roationMatrix.postRotate(rotation);
			}
			floatingBoxBitmap = Bitmap.createBitmap(rectangleBitmap, (int) edges[0].x, (int) edges[0].y, (int) (edges[3].x-edges[0].x), (int) (edges[3].y-edges[0].y), roationMatrix, true);
		}
		return true;
	}
	
	/**
	 * double tap while in floating box mode
	 * 
	 * @return true if event is used
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
			if(floatingBoxBitmap != null)
			{
				Paint bitmap_paint = new Paint(Paint.DITHER_FLAG);
				view_canvas.drawBitmap(floatingBoxBitmap, null, new RectF(this.position.x-this.width/2, this.position.y-this.height/2, this.position.x+this.width/2, this.position.y+this.height/2), bitmap_paint);
			}
			
		    view_canvas.translate(position.x, position.y);
		    view_canvas.rotate(rotation);
			DrawFunctions.setPaint(linePaint, Cap.ROUND, toolStrokeWidth, primaryColor, true, null);
			view_canvas.drawRect(-this.width/2, this.height/2, this.width/2, -this.height/2, linePaint);
			view_canvas.drawCircle(-this.width/2-this.roationSymbolDistance-this.roationSymbolWidth/2, -this.height/2-this.roationSymbolDistance-this.roationSymbolWidth/2, this.roationSymbolWidth, linePaint);
			DrawFunctions.setPaint(linePaint, Cap.ROUND, toolStrokeWidth, secundaryColor, true, new DashPathEffect(new float[] { 10, 20 }, 0));
			view_canvas.drawRect(-this.width/2, this.height/2, this.width/2, -this.height/2, linePaint);
			view_canvas.drawCircle(-this.width/2-this.roationSymbolDistance-this.roationSymbolWidth/2, -this.height/2-this.roationSymbolDistance-this.roationSymbolWidth/2, this.roationSymbolWidth, linePaint);
			view_canvas.restore();
		}
	}
	
	/**
	 * Rotates the box
	 * 
	 * @param delta_x move in direction x
   * @param delta_y move in direction y
	 */
	public void rotate(float delta_x, float delta_y)
	{
	  double rotationRadiant = rotation*Math.PI/180;
	  double delta_x_corrected = Math.cos(-rotationRadiant)*(delta_x)-Math.sin(-rotationRadiant)*(delta_y);
    double delta_y_corrected = Math.sin(-rotationRadiant)*(delta_x)+Math.cos(-rotationRadiant)*(delta_y);
	  
	  rotation += (delta_x_corrected-delta_y_corrected)/(5);
	}
	
	/**
   * Resizes the box
   * 
   * @param delta_x resize width
   * @param delta_y resize height
   */
  public void resize(float delta_x, float delta_y)
  {
    double rotationRadian = rotation*Math.PI/180;
    double delta_x_corrected = Math.cos(-rotationRadian)*(delta_x)-Math.sin(-rotationRadian)*(delta_y);
    double delta_y_corrected = Math.sin(-rotationRadian)*(delta_x)+Math.cos(-rotationRadian)*(delta_y);
    
    float resize_x_move_center_x = (float) ((delta_x_corrected/2)*Math.cos(rotationRadian));
    float resize_x_move_center_y = (float) ((delta_x_corrected/2)*Math.sin(rotationRadian));
    float resize_y_move_center_x = (float) ((delta_y_corrected/2)*Math.sin(rotationRadian));
    float resize_y_move_center_y = (float) ((delta_y_corrected/2)*Math.cos(rotationRadian));
    
    switch (resizeAction) {
    case TOP:
    case TOPRIGHT:
    case TOPLEFT:
      this.height -= (int)delta_y_corrected;
      this.position.x -= (int)resize_y_move_center_x;
      this.position.y += (int)resize_y_move_center_y;
      break;
    case BOTTOM:
    case BOTTOMLEFT:
    case BOTTOMRIGHT:
      this.height += (int)delta_y_corrected;
      this.position.x -= (int)resize_y_move_center_x;
      this.position.y += (int)resize_y_move_center_y;
      break;
    default:
      break;
    }
    
    switch (resizeAction) {
    case LEFT:
    case TOPLEFT:
    case BOTTOMLEFT:
      this.width -= (int)delta_x_corrected;
      this.position.x += (int)resize_x_move_center_x;
      this.position.y += (int)resize_x_move_center_y;
      break;
    case RIGHT:
    case TOPRIGHT:
    case BOTTOMRIGHT:
      this.width += (int)delta_x_corrected;
      this.position.x += (int)resize_x_move_center_x;
      this.position.y += (int)resize_x_move_center_y;
      break;
    default:
      break;
    }
    
    //prevent that box gets too small
    if(this.width < frameTolerance)
    {
      this.width = (int) frameTolerance;
    }
    if(this.height < frameTolerance)
    {
      this.height = (int) frameTolerance;
    }
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
	    this.floatingBoxBitmap = null;
	}
	
	/**
	 * Gets the action the user has selected through clicking on a specific
	 * position of the floating box
	 * 
	 * @param clickCoordinates coordinates the user has touched
	 * @return action to perform
	 */
	public FloatingBoxAction getAction(float clickCoordinatesX, float clickCoordinatesY)
	{
	  resizeAction = ResizeAction.NONE;
	  double rotationRadiant = rotation*Math.PI/180;
	  float clickCoordinatesRotatedX = (float) (this.position.x + Math.cos(-rotationRadiant)*(clickCoordinatesX-this.position.x)-Math.sin(-rotationRadiant)*(clickCoordinatesY-this.position.y));
	  float clickCoordinatesRotatedY = (float) (this.position.y + Math.sin(-rotationRadiant)*(clickCoordinatesX-this.position.x)+Math.cos(-rotationRadiant)*(clickCoordinatesY-this.position.y));
	  
	  // Move (within box)
	  if(clickCoordinatesRotatedX < this.position.x+this.width/2-frameTolerance &&
	      clickCoordinatesRotatedX > this.position.x-this.width/2+frameTolerance &&
	      clickCoordinatesRotatedY < this.position.y+this.height/2-frameTolerance &&
	      clickCoordinatesRotatedY > this.position.y-this.height/2+frameTolerance)
	  {
	    return FloatingBoxAction.MOVE;
	  }
	  
	  // Rotate (on symbol)
	  if(clickCoordinatesRotatedX < this.position.x-this.width/2-roationSymbolDistance &&
	      clickCoordinatesRotatedX > this.position.x-this.width/2-roationSymbolDistance-roationSymbolWidth &&
	      clickCoordinatesRotatedY < this.position.y-this.height/2-roationSymbolDistance &&
	      clickCoordinatesRotatedY > this.position.y-this.height/2-roationSymbolDistance-roationSymbolWidth)
    {
      return FloatingBoxAction.ROTATE;
    }
	  
	  // Resize (on frame)
	  if(clickCoordinatesRotatedX < this.position.x+this.width/2+frameTolerance &&
	      clickCoordinatesRotatedX > this.position.x-this.width/2-frameTolerance &&
	      clickCoordinatesRotatedY < this.position.y+this.height/2+frameTolerance &&
	      clickCoordinatesRotatedY > this.position.y-this.height/2-frameTolerance)
    {
	    if(clickCoordinatesRotatedX < this.position.x-this.width/2+frameTolerance)
	    {
	      resizeAction = ResizeAction.LEFT;
	    }
	    else if(clickCoordinatesRotatedX > this.position.x+this.width/2-frameTolerance)
	    {
	      resizeAction = ResizeAction.RIGHT;
	    }
	    if(clickCoordinatesRotatedY < this.position.y-this.height/2+frameTolerance)
      {
	      if(resizeAction == ResizeAction.LEFT)
	      {
	        resizeAction = ResizeAction.TOPLEFT;
	      } else if(resizeAction == ResizeAction.RIGHT)
        {
          resizeAction = ResizeAction.TOPRIGHT;
        } else {
          resizeAction = ResizeAction.TOP;
        }
      }
      else if(clickCoordinatesRotatedY > this.position.y+this.height/2-frameTolerance)
      {
        if(resizeAction == ResizeAction.LEFT)
        {
          resizeAction = ResizeAction.BOTTOMLEFT;
        } else if(resizeAction == ResizeAction.RIGHT)
        {
          resizeAction = ResizeAction.BOTTOMRIGHT;
        } else {
          resizeAction = ResizeAction.BOTTOM;
        }
      }
      return FloatingBoxAction.RESIZE;
    }
	  
	  // No valid click
	  return FloatingBoxAction.NONE;
	}
}

