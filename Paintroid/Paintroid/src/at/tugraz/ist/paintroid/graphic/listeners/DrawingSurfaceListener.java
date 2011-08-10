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
import android.view.MotionEvent;
import android.view.View;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;

public class DrawingSurfaceListener extends BaseSurfaceListener {

	protected float previousXTouchCoordinate;
	protected float previousYTouchCoordinate;

	public DrawingSurfaceListener(Context context) {
		super(context);
	}

	@Override
	public boolean handleOnTouchEvent(final int action, View view) {
		if (control_type == ToolType.BRUSH) {
			doAutoScroll();
		}

		switch (action) {

			case MotionEvent.ACTION_DOWN: // When finger touched
				previousXTouchCoordinate = actualXTouchCoordinate;
				previousYTouchCoordinate = actualYTouchCoordinate;
				if (control_type == ToolType.BRUSH) {
					drawingSurface.startPath(actualXTouchCoordinate, actualYTouchCoordinate);
				}
				break;

			case MotionEvent.ACTION_MOVE: // When finger moved
				switch (control_type) {

					case ZOOM:
						float zoomDelta = previousYTouchCoordinate - actualYTouchCoordinate;
						DrawingSurface.Perspective.zoom *= (float) Math.pow(20, zoomDelta / view.getHeight());
						DrawingSurface.Perspective.scroll.x -= zoomDelta;
						DrawingSurface.Perspective.scroll.y -= zoomDelta;
						previousXTouchCoordinate = actualXTouchCoordinate;
						previousYTouchCoordinate = actualYTouchCoordinate;

						drawingSurface.invalidate();
						break;

					case SCROLL:
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

					case PIPETTE:
						drawingSurface.setToolType(ToolType.PIPETTE);
						drawingSurface.getPixelColor(actualXTouchCoordinate, actualYTouchCoordinate);
						break;
				}
				break;
			case MotionEvent.ACTION_UP: // When finger released
				switch (control_type) {
					case BRUSH:
						drawingSurface.drawPathOnSurface(actualXTouchCoordinate, actualYTouchCoordinate);
						break;
					case PIPETTE:
						drawingSurface.setToolType(ToolType.PIPETTE);
						drawingSurface.getPixelColor(actualXTouchCoordinate, actualYTouchCoordinate);
						break;

					default:
						break;
				}
				break;
		}
		return true;
	}
}
