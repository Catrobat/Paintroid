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
import android.graphics.DashPathEffect;
import android.graphics.Paint.Cap;

/**
 * Class managing the cursor's behavior
 * 
 * Status: refactored 12.03.2011
 * @author PaintroidTeam
 * @version 6.0.4b
 */
public class Middlepoint extends Tool {		
		

	/**
	 * Constructor
	 * 
	 * sets the default values for the member variables
	 */	
	public Middlepoint()
	{
		super();
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
			return true;
		default:
			break;
		}
		return false;
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
		DrawFunctions.setPaint(drawPaint, Cap.ROUND, CursorStrokeWidth, color, true, null);
		if(Color.red(color) < Color.red(primaryColor)+0x30 &&
		   Color.blue(color) < Color.blue(primaryColor)+0x30 &&
		   Color.green(color) < Color.green(primaryColor)+0x30)
		{
			DrawFunctions.setPaint(linePaint, Cap.ROUND, CursorStrokeWidth, secundaryColor, true, null);
		}
		else
		{
			DrawFunctions.setPaint(linePaint, Cap.ROUND, CursorStrokeWidth, primaryColor, true, null);
		}
		stroke_width *= zoomLevel;
		if(state == ToolState.ACTIVE)
		{
			DrawFunctions.setPaint(linePaint, Cap.ROUND, CursorStrokeWidth, primaryColor, true, null);
			view_canvas.drawLine(position.x-stroke_width-CursorSize, position.y, position.x-stroke_width*3/4, position.y, linePaint);
			view_canvas.drawLine(position.x+stroke_width+CursorSize, position.y, position.x+stroke_width*3/4, position.y, linePaint);
			view_canvas.drawLine(position.x, position.y-stroke_width-CursorSize, position.x, position.y-stroke_width*3/4, linePaint);
			view_canvas.drawLine(position.x, position.y+stroke_width+CursorSize, position.x, position.y+stroke_width*3/4, linePaint);
			DrawFunctions.setPaint(linePaint, Cap.ROUND, CursorStrokeWidth, secundaryColor, true, new DashPathEffect(new float[] { 10, 20 }, 0));
			view_canvas.drawLine(position.x-stroke_width-CursorSize, position.y, position.x-stroke_width*3/4, position.y, linePaint);
			view_canvas.drawLine(position.x+stroke_width+CursorSize, position.y, position.x+stroke_width*3/4, position.y, linePaint);
			view_canvas.drawLine(position.x, position.y-stroke_width-CursorSize, position.x, position.y-stroke_width*3/4, linePaint);
			view_canvas.drawLine(position.x, position.y+stroke_width+CursorSize, position.x, position.y+stroke_width*3/4, linePaint);
		}
	}

}

