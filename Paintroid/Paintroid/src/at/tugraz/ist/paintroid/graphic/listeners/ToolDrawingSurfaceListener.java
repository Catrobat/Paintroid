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
import at.tugraz.ist.paintroid.graphic.utilities.Tool;
import at.tugraz.ist.paintroid.graphic.utilities.Tool.ToolState;

/**
 * Watch for on-Touch events on the DrawSurface
 * 
 * Status: refactored 20.02.2011
 * 
 * @author PaintroidTeam
 * @version 6.0.4b
 */
public class ToolDrawingSurfaceListener extends BaseSurfaceListener {

	protected Tool tool;

	// While moving this contains the coordinates
	// from the last event
	protected float previousXTouchCoordinate;
	protected float previousYTouchCoordinate;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            context to be set
	 * @param tool
	 *            tool to be set
	 */
	public ToolDrawingSurfaceListener(Context context, Tool tool) {
		super(context);
		this.tool = tool;
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
		float delta_x;
		float delta_y;
		Point delta_to_scroll = new Point();
		switch (action) {

			case MotionEvent.ACTION_DOWN: // When finger touched
				previousXTouchCoordinate = actualXTouchCoordinate;
				previousYTouchCoordinate = actualYTouchCoordinate;
				if (tool.getState() == ToolState.DRAW) {
					Point toolPosition = tool.getPosition();
					surface.setPath(toolPosition.x, toolPosition.y);
				}
				break;

			case MotionEvent.ACTION_MOVE: // When finger moved
				delta_x = (actualXTouchCoordinate - previousXTouchCoordinate);
				delta_y = (actualYTouchCoordinate - previousYTouchCoordinate);
				Point previousToolPosition = new Point(tool.getPosition());
				tool.movePosition(delta_x, delta_y, delta_to_scroll);
				scroll(delta_to_scroll, view);
				if (tool.getState() == ToolState.DRAW) {
					zoomstatus.setX(actualXTouchCoordinate);
					zoomstatus.setY(actualYTouchCoordinate);
					zoomstatus.notifyObservers();
					Point toolPosition = tool.getPosition();
					surface.setPath(toolPosition.x, toolPosition.y, previousToolPosition.x, previousToolPosition.y);
					previousXTouchCoordinate = actualXTouchCoordinate;
					previousYTouchCoordinate = actualYTouchCoordinate;
				}
				previousXTouchCoordinate = actualXTouchCoordinate;
				previousYTouchCoordinate = actualYTouchCoordinate;
				break;
			case MotionEvent.ACTION_UP: // When finger released
				delta_x = (actualXTouchCoordinate - previousXTouchCoordinate);
				delta_y = (actualYTouchCoordinate - previousYTouchCoordinate);
				tool.movePosition(delta_x, delta_y, delta_to_scroll);
				scroll(delta_to_scroll, view);
				if (tool.getState() == ToolState.DRAW) {
					Point toolPosition = tool.getPosition();
					surface.drawPathOnSurface(toolPosition.x, toolPosition.y);
				}
			default:
				break;
		}
		view.invalidate();
		return true;
	}// end onTouch

}
