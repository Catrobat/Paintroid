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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.ActionType;

/**
 * Watch for on-Touch events on the DrawSurface
 * 
 * Status: refactored 20.02.2011
 * @author PaintroidTeam
 * @version 6.0.4b
 */
public class DrawingSurfaceListener extends BaseSurfaceListener {

	// While moving this contains the coordinates
	// from the last event
	protected float prev_X;
	protected float prev_Y;

	/**
	 * Constructor
	 * 
	 * @param context context to be ste
	 */
	public DrawingSurfaceListener(Context context) {
		super(context);
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

		switch (action) {

		case MotionEvent.ACTION_DOWN: // When finger touched
			prev_X = actual_X;
			prev_Y = actual_Y;
			if(control_type == ActionType.DRAW)
			{
				surface.setPath(actual_X, actual_Y);
			}
			break;

		case MotionEvent.ACTION_MOVE: // When finger moved
			// create local variables
			final float delta_x;
			final float delta_y;
			
			switch (control_type) {
			
			case ZOOM:
				delta_y = (actual_Y - prev_Y) / view.getHeight();
				zoomstatus.setZoomLevel(zoomstatus.getZoomLevel()
						* (float) Math.pow(20, -delta_y));
				zoomstatus.notifyObservers();
				prev_X = actual_X;
				prev_Y = actual_Y;
				break;
				
			case SCROLL:
				delta_x = (actual_X - prev_X) / view.getWidth();
				delta_y = (actual_Y - prev_Y) / view.getHeight();
			    float zoomLevelFactor = (1/zoomstatus.getZoomLevel()); //used for less scrolling on higher zoom level
				zoomstatus.setScrollX(zoomstatus.getScrollX() - delta_x * zoomLevelFactor );
				zoomstatus.setScrollY(zoomstatus.getScrollY() - delta_y * zoomLevelFactor );
				zoomstatus.notifyObservers();
				prev_X = actual_X;
				prev_Y = actual_Y;
				break;
				
			case DRAW:
				zoomstatus.setX(actual_X);
				zoomstatus.setY(actual_Y);
				zoomstatus.notifyObservers();

				float dx = Math.abs(actual_X - prev_X);
		        float dy = Math.abs(actual_Y - prev_Y);
		        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
		        	surface.setPath(actual_X, actual_Y, prev_X, prev_Y);
		            prev_X = actual_X;
					prev_Y = actual_Y;
		        }
				break;
				
			case CHOOSE: 
				// Set onDraw actionType
				surface.setActionType(ActionType.CHOOSE); 
				// Get Pixel and set color in DrawSurface
				surface.getPixelColor(actual_X, actual_Y);
				break;
				
			case RESET:
				Log.v("DEBUG", "reset");
				break;
			}
			break;

		case MotionEvent.ACTION_UP: // When finger released
			switch(control_type)
			{
			case DRAW:
				surface.drawPathOnSurface(actual_X, actual_Y);
				break;
			case CHOOSE:
				// Set onDraw actionType
				surface.setActionType(ActionType.CHOOSE); 
				// Get Pixel and set color in DrawSurface
				surface.getPixelColor(actual_X, actual_Y);
				break;
			case MAGIC:
				zoomstatus.setX(actual_X);
				zoomstatus.setY(actual_Y);
				zoomstatus.notifyObservers();

				surface.replaceColorOnSurface(actual_X, actual_Y);
				break;
			case RESET:
				Log.v("DEBUG", "reset");
				break;
			}
			break;

		default:
			break;
		}
		return true;
	}// end onTouch
	
	//------------------------------Methods For JUnit TESTING---------------------------------------	
	public void getLastClickCoordinates(float[] coordinates)
	{
		coordinates[0] = actual_X;
		coordinates[1] = actual_Y;
	}

}
