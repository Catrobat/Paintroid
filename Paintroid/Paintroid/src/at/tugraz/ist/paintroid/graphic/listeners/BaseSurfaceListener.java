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
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.ActionType;
import at.tugraz.ist.zoomscroll.ZoomStatus;

public abstract class BaseSurfaceListener implements View.OnTouchListener {

	class DrawingGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent event) {
			if (!surface.singleTapEvent()) {
				surface.drawPointOnSurface(event.getX(), event.getY());
			}
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent event) {
			switch (control_type) {
				case DRAW:
					return surface.doubleTapEvent(event.getX(), event.getY());
				default:
					break;
			}
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent event) {
			return true;
		}
	}

	protected DrawingSurface surface;

	// Coordinates from last point during move event (needed for Robotium)
	protected float actualXTouchCoordinate;
	protected float actualYTouchCoordinate;

	protected ZoomStatus zoomstatus;

	private GestureDetector gestureDetector;

	protected ActionType control_type = DrawingSurface.ActionType.ZOOM;

	protected static final float TOUCH_TOLERANCE = 4;

	protected boolean downEventOccured = false;

	public BaseSurfaceListener(Context context) {
		gestureDetector = new GestureDetector(context, new DrawingGestureListener());
	}

	public void setZoomStatus(ZoomStatus status) {
		zoomstatus = status;
	}

	public void setSurface(DrawingSurface surf) {
		surface = surf;
	}

	public void setControlType(ActionType type) {
		control_type = type;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		final int action = event.getAction();
		// get the onTouch coordinates
		final float xTouchCoordinate = event.getX();
		final float yTouchCoordinate = event.getY();

		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}

		actualXTouchCoordinate = xTouchCoordinate;
		actualYTouchCoordinate = yTouchCoordinate;

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				downEventOccured = true;
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (downEventOccured) {
					downEventOccured = false;
					break;
				}
			default:
				if (!downEventOccured) {
					return false;
				}
				break;
		}
		return handleOnTouchEvent(action, view);
	}

	protected abstract boolean handleOnTouchEvent(int action, View view);

	protected void scroll(Point delta_to_scroll, View view) {
		if (delta_to_scroll.x == 0 && delta_to_scroll.y == 0) {
			return;
		}
		float delta_x = (float) (delta_to_scroll.x) / (float) (view.getWidth());
		float delta_y = (float) (delta_to_scroll.y) / (float) (view.getHeight());
		float zoomLevelFactor = (1 / zoomstatus.getZoomLevel()); //used for less scrolling on higher zoom level
		zoomstatus.setScrollX(zoomstatus.getScrollX() + delta_x * zoomLevelFactor);
		zoomstatus.setScrollY(zoomstatus.getScrollY() + delta_y * zoomLevelFactor);
		zoomstatus.notifyObservers();
	}

	//------------------------------Methods For JUnit TESTING---------------------------------------	
	public void getLastClickCoordinates(float[] coordinates) {
		coordinates[0] = actualXTouchCoordinate;
		coordinates[1] = actualYTouchCoordinate;
	}
}
