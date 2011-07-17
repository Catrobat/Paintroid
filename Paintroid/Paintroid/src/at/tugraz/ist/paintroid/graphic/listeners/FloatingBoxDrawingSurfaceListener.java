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
import android.view.MotionEvent;
import android.view.View;
import at.tugraz.ist.paintroid.graphic.utilities.FloatingBox;
import at.tugraz.ist.paintroid.graphic.utilities.FloatingBox.FloatingBoxAction;
import at.tugraz.ist.paintroid.graphic.utilities.Tool.ToolState;

public class FloatingBoxDrawingSurfaceListener extends BaseSurfaceListener {

	protected FloatingBox floatingBox;
	protected FloatingBoxAction floatingBoxAction;

	protected float previousXTouchCoordinate;
	protected float previousYTouchCoordinate;

	public FloatingBoxDrawingSurfaceListener(Context context, FloatingBox floatingBox) {
		super(context);
		this.floatingBox = floatingBox;
		this.floatingBoxAction = FloatingBoxAction.NONE;
	}

	@Override
	public boolean handleOnTouchEvent(final int action, View view) {
		float delta_x;
		float delta_y;
		Point delta_to_scroll = new Point();
		switch (action) {

			case MotionEvent.ACTION_DOWN:
				previousXTouchCoordinate = actualXTouchCoordinate;
				previousYTouchCoordinate = actualYTouchCoordinate;
				this.floatingBoxAction = floatingBox.getAction(actualXTouchCoordinate, actualYTouchCoordinate);
				break;

			case MotionEvent.ACTION_MOVE:
				delta_x = (actualXTouchCoordinate - previousXTouchCoordinate);
				delta_y = (actualYTouchCoordinate - previousYTouchCoordinate);
				switch (this.floatingBoxAction) {

					case MOVE:
						this.floatingBox.movePosition(delta_x, delta_y, delta_to_scroll);
						scroll(delta_to_scroll, view);
						break;
					case RESIZE:
						this.floatingBox.resize(delta_x, delta_y);
						break;
					case ROTATE:
						this.floatingBox.rotate(delta_x, delta_y);
						break;
					default:
						break;
				}
				previousXTouchCoordinate = actualXTouchCoordinate;
				previousYTouchCoordinate = actualYTouchCoordinate;
				break;
			case MotionEvent.ACTION_UP:
				delta_x = (actualXTouchCoordinate - previousXTouchCoordinate);
				delta_y = (actualYTouchCoordinate - previousYTouchCoordinate);
				switch (this.floatingBoxAction) {

					case MOVE:
						floatingBox.movePosition(delta_x, delta_y, delta_to_scroll);
						scroll(delta_to_scroll, view);
						if (floatingBox.getState() == ToolState.DRAW) {
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
	}
}
