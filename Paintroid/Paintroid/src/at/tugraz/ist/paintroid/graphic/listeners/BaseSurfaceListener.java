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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import at.tugraz.ist.paintroid.MainActivity.ToolbarItem;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;

public abstract class BaseSurfaceListener implements View.OnTouchListener {
	static final String TAG = "PAINTROID";

	protected DrawingSurface drawingSurface;
	protected float srfcWidth;
	protected float srfcHeight;
	protected float actualXTouchCoordinate;
	protected float actualYTouchCoordinate;
	protected ToolbarItem controlType = ToolbarItem.ZOOM;
	protected boolean downEventOccured = false;

	private GestureDetector gestureDetector;

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
			switch (controlType) {
				case BRUSH:
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

	public BaseSurfaceListener(Context context) {
		gestureDetector = new GestureDetector(context, new DrawingGestureListener());
	}

	public void setSurface(DrawingSurface surface) {
		drawingSurface = surface;
		srfcWidth = drawingSurface.getWidth();
		srfcHeight = drawingSurface.getHeight();
	}

	public void setControlType(ToolbarItem type) {
		controlType = type;
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
		if (controlType == ToolbarItem.BRUSH) {
			doAutoScroll();
		}
		return handleOnTouchEvent(action, view);
	}

	protected abstract boolean handleOnTouchEvent(int action, View view);

	static final int SCROLLSPEED = 10;
	static final int SCROLLBORDER = 50;

	protected void doAutoScroll() {
		final float left = drawingSurface.getRectImage().left;
		final float right = drawingSurface.getRectImage().right;
		final float top = drawingSurface.getRectImage().top;
		final float bottom = drawingSurface.getRectImage().bottom;
		final float zoom = DrawingSurface.Perspective.zoom;

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
}
