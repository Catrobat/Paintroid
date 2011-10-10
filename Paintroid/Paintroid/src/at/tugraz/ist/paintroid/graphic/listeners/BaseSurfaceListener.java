/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *   
 *   Paintroid is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *   
 *   You should have received a copy of the GNU Affero General Public License
 *   along with Paintroid.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.graphic.listeners;

import android.content.Context;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;

public abstract class BaseSurfaceListener implements View.OnTouchListener {
	static final String TAG = "PAINTROID";

	protected class DrawingGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent event) {
			if (!drawingSurface.singleTapEvent()) {
				drawingSurface.drawPointOnSurface(event.getX(), event.getY());
			}
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent event) {
			switch (control_type) {
				case BRUSH:
				case CURSOR:
					return drawingSurface.doubleTapEvent(event.getX(), event.getY());
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

	protected DrawingSurface drawingSurface;

	protected float actualXTouchCoordinate;
	protected float actualYTouchCoordinate;

	private GestureDetector gestureDetector;

	// Actual Draw Control Type (set to init value ZOOM)
	protected ToolType control_type = ToolType.ZOOM;

	protected boolean downEventOccured = false;

	public BaseSurfaceListener(Context context) {
		gestureDetector = new GestureDetector(context, new DrawingGestureListener());
	}

	public void setSurface(DrawingSurface surface) {
		drawingSurface = surface;
	}

	public void setControlType(ToolType type) {
		control_type = type;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		final int action = event.getAction();
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

	protected boolean handleOnTouchEvent(int action, View view) {
		if (control_type != ToolType.SCROLL && control_type != ToolType.ZOOM) {
			doAutoScroll();
		}
		return true;
	}

	static final int SCROLLSPEED = 10;
	static final int SCROLLBORDER = 50;

	protected void doAutoScroll() {
		final float left = drawingSurface.getRectImage().left;
		final float right = drawingSurface.getRectImage().right;
		final float top = drawingSurface.getRectImage().top;
		final float bottom = drawingSurface.getRectImage().bottom;
		final float zoom = DrawingSurface.Perspective.zoom;

		final float srfcWidth = drawingSurface.getWidth();
		final float srfcHeight = drawingSurface.getHeight();

		int scroll = (int) (SCROLLSPEED / zoom);

		if (actualXTouchCoordinate >= srfcWidth - SCROLLBORDER && right * zoom > srfcWidth - SCROLLBORDER) {
			DrawingSurface.Perspective.scroll.x -= scroll;
			drawingSurface.invalidate();
		} else if (actualXTouchCoordinate <= SCROLLBORDER && left < SCROLLBORDER) {
			DrawingSurface.Perspective.scroll.x += scroll;
			drawingSurface.invalidate();
		}
		if (actualYTouchCoordinate >= srfcHeight - SCROLLBORDER && bottom * zoom > srfcHeight - SCROLLBORDER - 50) {
			DrawingSurface.Perspective.scroll.y -= scroll;
			drawingSurface.invalidate();
		} else if (actualYTouchCoordinate <= SCROLLBORDER && top < SCROLLBORDER) {
			DrawingSurface.Perspective.scroll.y += scroll;
			drawingSurface.invalidate();
		}
	}

	//------------------------------Methods For JUnit TESTING---------------------------------------	
	/*
	 * Because Robotium click coordinates differ from real ones
	 */
	public PointF getLastClickCoordinates() {
		return new PointF(actualXTouchCoordinate, actualYTouchCoordinate);
	}
}
