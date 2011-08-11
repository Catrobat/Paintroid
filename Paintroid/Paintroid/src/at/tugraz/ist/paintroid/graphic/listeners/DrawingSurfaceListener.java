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
import at.tugraz.ist.paintroid.MainActivity.ToolbarItem;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;

public class DrawingSurfaceListener extends BaseSurfaceListener {

	protected float previousXTouchCoordinate;
	protected float previousYTouchCoordinate;

	public DrawingSurfaceListener(Context context) {
		super(context);
	}

	@Override
	public boolean handleOnTouchEvent(final int action, View view) {
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				previousXTouchCoordinate = actualXTouchCoordinate;
				previousYTouchCoordinate = actualYTouchCoordinate;
				if (controlType == ToolbarItem.BRUSH) {
					drawingSurface.startPath(actualXTouchCoordinate, actualYTouchCoordinate);
				}
				break;

			case MotionEvent.ACTION_MOVE:
				switch (controlType) {

					case ZOOM:
						float zoomDelta = previousYTouchCoordinate - actualYTouchCoordinate;
						DrawingSurface.Perspective.zoom *= (float) Math.pow(20, zoomDelta / view.getHeight());
						DrawingSurface.Perspective.scroll.x -= zoomDelta;
						DrawingSurface.Perspective.scroll.y -= zoomDelta;
						previousXTouchCoordinate = actualXTouchCoordinate;
						previousYTouchCoordinate = actualYTouchCoordinate;

						drawingSurface.invalidate();
						break;

					case HAND:
						float delta_x = previousXTouchCoordinate - actualXTouchCoordinate;
						float delta_y = previousYTouchCoordinate - actualYTouchCoordinate;
						DrawingSurface.Perspective.scroll.x -= delta_x / DrawingSurface.Perspective.zoom;
						DrawingSurface.Perspective.scroll.y -= delta_y / DrawingSurface.Perspective.zoom;
						previousXTouchCoordinate = actualXTouchCoordinate;
						previousYTouchCoordinate = actualYTouchCoordinate;

						drawingSurface.invalidate();
						break;

					case BRUSH:
						drawingSurface.updatePath(actualXTouchCoordinate, actualYTouchCoordinate,
								previousXTouchCoordinate, previousYTouchCoordinate);
						previousXTouchCoordinate = actualXTouchCoordinate;
						previousYTouchCoordinate = actualYTouchCoordinate;
						break;

					case EYEDROPPER:
						drawingSurface.setActionType(ToolbarItem.EYEDROPPER);
						drawingSurface.getPixelColor(actualXTouchCoordinate, actualYTouchCoordinate);
						break;

					case RESET:
						Log.v("DEBUG", "reset");
						break;
				}
				break;

			case MotionEvent.ACTION_UP:
				switch (controlType) {
					case BRUSH:
						drawingSurface.drawPathOnSurface(actualXTouchCoordinate, actualYTouchCoordinate);
						break;
					case EYEDROPPER:
						drawingSurface.setActionType(ToolbarItem.EYEDROPPER);
						drawingSurface.getPixelColor(actualXTouchCoordinate, actualYTouchCoordinate);
						break;
					case MAGICWAND:
						drawingSurface.replaceColorOnSurface(actualXTouchCoordinate, actualYTouchCoordinate);
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
	}
}
