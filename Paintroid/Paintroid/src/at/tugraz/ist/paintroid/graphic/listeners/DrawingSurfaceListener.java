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

package at.tugraz.ist.paintroid.graphic.listeners;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.ActionType;

/**
 * Watch for on-Touch events on the DrawSurface
 * 
 * Status: refactored 20.02.2011
 * 
 * @author PaintroidTeam
 * @version 0.6.4b
 */
public class DrawingSurfaceListener extends BaseSurfaceOnTouchListener {

	// While moving this contains the coordinates
	// from the last event
	protected float previousXTouchCoordinate;
	protected float previousYTouchCoordinate;
	// True if real move (> touch tolereance) occured
	protected boolean moveOccured = false;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            context to be ste
	 */
	public DrawingSurfaceListener(Context context) {
		super(context);
	}

	/**
	 * Handles the onTouch events
	 * 
	 * @param action
	 *            action that occurred
	 * @param view
	 *            view on which the action is handled
	 * @return true if the event is consumed, else false
	 */
	@Override
	public boolean handleOnTouchEvent(final int action, View view) {

		switch (action) {

			case MotionEvent.ACTION_DOWN: // When finger touched
				moveOccured = false;
				previousXTouchCoordinate = actualXTouchCoordinate;
				previousYTouchCoordinate = actualYTouchCoordinate;
				if (control_type == ActionType.DRAW) {
					surface.setPath(actualXTouchCoordinate, actualYTouchCoordinate);
				}
				break;

			case MotionEvent.ACTION_MOVE: // When finger moved
				final float delta_x;
				final float delta_y;
				float dx = Math.abs(actualXTouchCoordinate - previousXTouchCoordinate);
				float dy = Math.abs(actualYTouchCoordinate - previousYTouchCoordinate);
				if (dx < TOUCH_TOLERANCE && dy < TOUCH_TOLERANCE) {
					break;
				}
				moveOccured = true;
				switch (control_type) {

					case ZOOM:
						delta_y = (actualYTouchCoordinate - previousYTouchCoordinate) / view.getHeight();
						zoomstatus.setZoomLevel(zoomstatus.getZoomLevel() * (float) Math.pow(20, -delta_y));
						zoomstatus.notifyObservers();
						previousXTouchCoordinate = actualXTouchCoordinate;
						previousYTouchCoordinate = actualYTouchCoordinate;
						break;

					case SCROLL:
						delta_x = (actualXTouchCoordinate - previousXTouchCoordinate) / view.getWidth();
						delta_y = (actualYTouchCoordinate - previousYTouchCoordinate) / view.getHeight();
						float zoomLevelFactor = (1 / zoomstatus.getZoomLevel()); //used for less scrolling on higher zoom level
						zoomstatus.setScrollX(zoomstatus.getScrollX() - delta_x * zoomLevelFactor);
						zoomstatus.setScrollY(zoomstatus.getScrollY() - delta_y * zoomLevelFactor);
						zoomstatus.notifyObservers();
						previousXTouchCoordinate = actualXTouchCoordinate;
						previousYTouchCoordinate = actualYTouchCoordinate;
						break;

					case DRAW:
						zoomstatus.setX(actualXTouchCoordinate);
						zoomstatus.setY(actualYTouchCoordinate);
						zoomstatus.notifyObservers();

						surface.setPath(actualXTouchCoordinate, actualYTouchCoordinate, previousXTouchCoordinate,
								previousYTouchCoordinate);
						previousXTouchCoordinate = actualXTouchCoordinate;
						previousYTouchCoordinate = actualYTouchCoordinate;
						break;

					case CHOOSE:
						// Set onDraw actionType
						surface.setActionType(ActionType.CHOOSE);
						// Get Pixel and set color in DrawSurface
						surface.getPixelColor(actualXTouchCoordinate, actualYTouchCoordinate);
						break;

					case RESET:
						Log.v("DEBUG", "reset");
						break;
				}
				break;

			case MotionEvent.ACTION_UP: // When finger released
				switch (control_type) {
					case DRAW:
						if (moveOccured) {
							surface.drawPathOnSurface(actualXTouchCoordinate, actualYTouchCoordinate);
						}
						break;
					case CHOOSE:
						// Set onDraw actionType
						surface.setActionType(ActionType.CHOOSE);
						// Get Pixel and set color in DrawSurface
						surface.getPixelColor(actualXTouchCoordinate, actualYTouchCoordinate);
						break;
					case MAGIC:
						zoomstatus.setX(actualXTouchCoordinate);
						zoomstatus.setY(actualYTouchCoordinate);
						zoomstatus.notifyObservers();

						surface.replaceColorOnSurface(actualXTouchCoordinate, actualYTouchCoordinate);
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
	@Override
	public void getLastClickCoordinates(float[] coordinates) {
		coordinates[0] = actualXTouchCoordinate;
		coordinates[1] = actualYTouchCoordinate;
	}

}
