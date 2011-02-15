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

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.ActionType;
import at.tugraz.ist.zoomscroll.ZoomStatus;

/**
 * Watch for on-Touch events on the DrawSurface
 * 
 * Status: refactored 04.02.2011
 * @author PaintroidTeam
 * @version 6.0b
 */
public class DrawingSurfaceListener implements View.OnTouchListener {

	private DrawingSurface surface;
	
	// Coordinates by starting the onTouch event,
	private float start_X;
	private float start_Y;
	
	// While moving this contains the coordinates
	// from the last event
	private float prev_X;
	private float prev_Y;
	
	// Coordinates from last point during move event (needed for Robotium)
	private float actual_X;
	private float actual_Y;

	// ZoomStatus which is used
	private ZoomStatus zoomstatus;
	
	// Actual Draw Control Type (set to init value ZOOM)
	private ActionType control_type = DrawingSurface.ActionType.ZOOM;
	
	// Tolerance in pixel for drawing
	private static final float TOUCH_TOLERANCE = 4;
	
	// -----------------------------------------------------------------------

	// Function to set the controlled zoom-status
	public void setZoomStatus(ZoomStatus status) {
		zoomstatus = status;
	}

	/**
	 * Sets the used drawing surface
	 * 
	 * @param surf The surface to set
	 */
	public void setSurface(DrawingSurface surf) {
		surface = surf;
	}

	/**
	 * Sets the DrawControlType
	 * 
	 * @param type The DrawControlType to set
	 */
	public void setControlType(ActionType type) {
		control_type = type;
	}

	/**
	 * Handles the onTouch events
	 * 
	 */
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		final int action = event.getAction();
		// get the onTouch coordinates
		final float x = event.getX();
		final float y = event.getY();

		switch (action) {

		case MotionEvent.ACTION_DOWN: // When finger touched
			prev_X = x;
			prev_Y = y;
			start_X = x;
			start_Y = y;
			actual_X = x;
			actual_Y = y;
			if(control_type == ActionType.DRAW)
			{
				surface.setPath(x, y);
			}
			break;

		case MotionEvent.ACTION_MOVE: // When finger moved
			// create local variables
			final float delta_x;
			final float delta_y;
			actual_X = x;
			actual_Y = y;
			
			switch (control_type) {
			
			case ZOOM:
				delta_y = (y - prev_Y) / view.getHeight();
				zoomstatus.setZoomLevel(zoomstatus.getZoomLevel()
						* (float) Math.pow(20, -delta_y));
				zoomstatus.notifyObservers();
				prev_X = x;
				prev_Y = y;
				break;
				
			case SCROLL:
				delta_x = (x - prev_X) / view.getWidth();
				delta_y = (y - prev_Y) / view.getHeight();
			    float zoomLevelFactor = (1/zoomstatus.getZoomLevel()); //used for less scrolling on higher zoom level
				zoomstatus.setScrollX(zoomstatus.getScrollX() - delta_x * zoomLevelFactor );
				zoomstatus.setScrollY(zoomstatus.getScrollY() - delta_y * zoomLevelFactor );
				zoomstatus.notifyObservers();
				prev_X = x;
				prev_Y = y;
				break;
				
			case DRAW:
				zoomstatus.setX(x);
				zoomstatus.setY(y);
				zoomstatus.notifyObservers();

				float dx = Math.abs(x - prev_X);
		        float dy = Math.abs(y - prev_Y);
		        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
		        	surface.setPath(x, y, prev_X, prev_Y);
		            prev_X = x;
					prev_Y = y;
		        }
				break;
				
			case CHOOSE:
				// Set onDraw actionType
				surface.setActionType(ActionType.CHOOSE); 
				// Get Pixel and set color in DrawSurface
				surface.getPixelColor(x, y);
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
				if(prev_X == start_X && prev_Y == start_Y)
				{
					surface.drawPaintOnSurface(prev_X, prev_Y);
				}
				else
				{
					surface.drawPathOnSurface(prev_X, prev_Y);
				}
				actual_X = x;
				actual_Y = y;
				break;
			case CHOOSE:
				// Set onDraw actionType
				surface.setActionType(ActionType.CHOOSE); 
				// Get Pixel and set color in DrawSurface
				surface.getPixelColor(x, y);
				break;
			case MAGIC:
				zoomstatus.setX(x);
				zoomstatus.setY(y);
				zoomstatus.notifyObservers();

				surface.replaceColorOnSurface(x, y);
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
