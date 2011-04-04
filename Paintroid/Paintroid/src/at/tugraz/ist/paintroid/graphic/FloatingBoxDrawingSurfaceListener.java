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

package at.tugraz.ist.paintroid.graphic;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import at.tugraz.ist.paintroid.graphic.utilities.FloatingBox;
import at.tugraz.ist.paintroid.graphic.utilities.Tool;
import at.tugraz.ist.paintroid.graphic.utilities.FloatingBox.FloatingBoxAction;
import at.tugraz.ist.paintroid.graphic.utilities.Tool.ToolState;

/**
 * Watch for on-Touch events on the DrawSurface
 * 
 * Status: refactored 20.02.2011
 * @author PaintroidTeam
 * @version 6.0.4b
 */
public class FloatingBoxDrawingSurfaceListener extends BaseSurfaceListener {
  
  protected FloatingBox floatingBox;
  protected FloatingBoxAction floatingBoxAction;
  
  //While moving this contains the coordinates
  // from the last event
  protected float prev_X;
  protected float prev_Y;
	
	/**
	 * Constructor
	 * 
	 * @param context context to be set
	 * @param tool tool to be set
	 */
  public FloatingBoxDrawingSurfaceListener(Context context, FloatingBox floatingBox)
  {
    super(context);
    this.floatingBox = floatingBox;
    this.floatingBoxAction = FloatingBoxAction.NONE;
  }

	/**
	 * Handles the onTouch events
	 * 
	 * @param action action that occurred
	 * @param view  view on which the action is handled
	 * @return true if the event is consumed, else false
	 */
	@Override
	public boolean handleOnTouchEvent(final int action, View view) {
		float delta_x;
		float delta_y;
		switch (action) {

		case MotionEvent.ACTION_DOWN: // When finger touched
			prev_X = actual_X;
			prev_Y = actual_Y;
			this.floatingBoxAction = floatingBox.getAction(actual_X, actual_Y);
			break;

		case MotionEvent.ACTION_MOVE: // When finger moved
			delta_x = (actual_X - prev_X);
			delta_y = (actual_Y - prev_Y);
			switch (this.floatingBoxAction) {
			
			case MOVE:
			  this.floatingBox.movePosition(delta_x, delta_y);
			  break;
			case RESIZE:
			  this.floatingBox.resize(delta_x, delta_y);
			  break;
			default:
	      break;
			}
			prev_X = actual_X;
      prev_Y = actual_Y;
			break;
		case MotionEvent.ACTION_UP: // When finger released
			delta_x = (actual_X - prev_X);
			delta_y = (actual_Y - prev_Y);
			switch (this.floatingBoxAction) {
      
      case MOVE:
  			floatingBox.movePosition(delta_x, delta_y);
  			if(floatingBox.getState() == ToolState.DRAW)
  			{
  				Point toolPosition = floatingBox.getPosition();
  				surface.drawPathOnSurface(toolPosition.x, toolPosition.y);
  			}
  			break;
      default:
        break;
			}
			break;
		default:
			break;
		}
		view.invalidate();
		return true;
	}// end onTouch

}
