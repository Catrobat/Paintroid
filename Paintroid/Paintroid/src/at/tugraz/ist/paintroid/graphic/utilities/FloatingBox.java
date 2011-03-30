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
	
	protected int width = 200;
	protected int height = 200;
	
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

}

