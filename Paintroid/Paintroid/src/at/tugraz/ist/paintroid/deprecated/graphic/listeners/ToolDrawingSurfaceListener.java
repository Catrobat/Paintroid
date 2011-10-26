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
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.deprecated.graphic.listeners;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import at.tugraz.ist.paintroid.deprecated.graphic.utilities.Tool;
import at.tugraz.ist.paintroid.deprecated.graphic.utilities.Tool.ToolState;

@Deprecated
public class ToolDrawingSurfaceListener extends BaseSurfaceListener {

	protected Tool tool;
	protected float previousXTouchCoordinate;
	protected float previousYTouchCoordinate;

	public ToolDrawingSurfaceListener(Context context, Tool tool) {
		super(context);
		this.tool = tool;
	}

	@Override
	public boolean handleOnTouchEvent(final int action, View view) {
		super.handleOnTouchEvent(action, view);
		float delta_x;
		float delta_y;
		Point delta_to_scroll = new Point();
		switch (action) {

			case MotionEvent.ACTION_DOWN:
				previousXTouchCoordinate = actualXTouchCoordinate;
				previousYTouchCoordinate = actualYTouchCoordinate;
				if (tool.getState() == ToolState.DRAW) {
					Point toolPosition = tool.getPosition();
					drawingSurface.startPath(toolPosition.x, toolPosition.y);
				}
				break;

			case MotionEvent.ACTION_MOVE:
				delta_x = (actualXTouchCoordinate - previousXTouchCoordinate);
				delta_y = (actualYTouchCoordinate - previousYTouchCoordinate);
				Point previousToolPosition = new Point(tool.getPosition());
				tool.movePosition(delta_x, delta_y, delta_to_scroll);
				if (tool.getState() == ToolState.DRAW) {
					Point toolPosition = tool.getPosition();
					drawingSurface.updatePath(toolPosition.x, toolPosition.y, previousToolPosition.x,
							previousToolPosition.y);
					previousXTouchCoordinate = actualXTouchCoordinate;
					previousYTouchCoordinate = actualYTouchCoordinate;
				}
				previousXTouchCoordinate = actualXTouchCoordinate;
				previousYTouchCoordinate = actualYTouchCoordinate;
				break;
			case MotionEvent.ACTION_UP:
				delta_x = (actualXTouchCoordinate - previousXTouchCoordinate);
				delta_y = (actualYTouchCoordinate - previousYTouchCoordinate);
				tool.movePosition(delta_x, delta_y, delta_to_scroll);
				if (tool.getState() == ToolState.DRAW) {
					Point toolPosition = tool.getPosition();
					drawingSurface.drawPathOnSurface(toolPosition.x, toolPosition.y);
				}
			default:
				break;
		}
		view.invalidate();
		return true;
	}
}
