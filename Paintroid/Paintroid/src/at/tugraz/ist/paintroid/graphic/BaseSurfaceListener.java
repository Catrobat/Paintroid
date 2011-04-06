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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.ActionType;
import at.tugraz.ist.zoomscroll.ZoomStatus;

/**
 * Base class for on-Touch events on the DrawSurface
 * 
 * Status: refactored 20.02.2011
 * @author PaintroidTeam
 * @version 6.0.4b
 */
public abstract class BaseSurfaceListener implements View.OnTouchListener {
	
	/**
	 * inner class for a subset of gestures to listen
	 *
	 */
	class DrawingGestureListener extends GestureDetector.SimpleOnGestureListener {
		
		/**
		 * notified when single tap occurs 
		 * 
		 * @param event the down motion event of the single tap
		 * @return true if the event is consumed, else false 
		 */
		@Override
		public boolean onSingleTapConfirmed(MotionEvent event)
		{
			if(!surface.singleTapEvent())
			{
				surface.drawPaintOnSurface(event.getX(), event.getY());
			}
			return false;
		}
		
		/**
		 * notified when double tap occurs  
		 * 
		 * @param event the down motion event of the first tap of the double tap
		 * @return true if the event is consumed, else false 
		 */
		@Override
		public boolean onDoubleTap(MotionEvent event)
		{
			switch (control_type) {
			case DRAW:
				return surface.doubleTapEvent(event.getX(), event.getY());
			default:
				break;
			}
			return false;
		}
		
		/**
		 * Used to not draw after double tap
		 * 
		 * @param event the motion event that occurred during the double-tap gesture
		 * @return true 
		 */
		@Override
		public boolean onDoubleTapEvent(MotionEvent event)
		{
			return true;
		}
	}

	protected DrawingSurface surface;
	
	// Coordinates from last point during move event (needed for Robotium)
	protected float actual_X;
	protected float actual_Y;

	// ZoomStatus which is used
	protected ZoomStatus zoomstatus;
	
	private GestureDetector gestureDetector;
	
	// Actual Draw Control Type (set to init value ZOOM)
	protected ActionType control_type = DrawingSurface.ActionType.ZOOM;
	
	// Tolerance in pixel for drawing
	protected static final float TOUCH_TOLERANCE = 4;
	
	protected boolean downEventOccured = false;
	
	// -----------------------------------------------------------------------
	
	/**
	 * Constructor
	 * 
	 * @param Context sets the context
	 */
	public BaseSurfaceListener(Context context)
	{
		gestureDetector = new GestureDetector(context, new DrawingGestureListener());
	}

	/**
	 * function to set the controlled zoom-status
	 * 
	 * @param status the actual zoom status
	 */
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
		
		if(gestureDetector.onTouchEvent(event))
		{
			return true;
		}
		
		actual_X = x;
		actual_Y = y;
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			downEventOccured = true;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if(downEventOccured)
			{
				downEventOccured = false;
				break;
			}
		default:
			if(!downEventOccured)
			{
				return false;
			}
			break;
		}
		return handleOnTouchEvent(action, view);
	}// end onTouch
	
	/**
	 * 
	 * @param action action that occurred
	 * @param view  view on which the action is handled
	 * @return
	 */
	protected abstract boolean handleOnTouchEvent(int action, View view);
	
	//------------------------------Methods For JUnit TESTING---------------------------------------	
	public void getLastClickCoordinates(float[] coordinates)
	{
		coordinates[0] = actual_X;
		coordinates[1] = actual_Y;
	}

}
