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

package at.tugraz.ist.paintroid.listener;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.ui.Perspective;

public class DrawingSurfaceListener implements OnTouchListener {
	private final Perspective drawingSurfacePerspective;

	public DrawingSurfaceListener(Perspective perspective) {
		drawingSurfacePerspective = perspective;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		Point touchCoordinate = new Point((int) event.getX(), (int) event.getY());
		drawingSurfacePerspective.translateScreenToCanvas(touchCoordinate);
		PointF canvasTouchCoordinate = new PointF(touchCoordinate);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceListener.onTouch DOWN");
			PaintroidApplication.CURRENT_TOOL.handleDown(canvasTouchCoordinate);
			return true;
		case MotionEvent.ACTION_MOVE:
			// Log.d(PaintroidApplication.TAG, "DrawingSurfaceListener.onTouch MOVE");
			PaintroidApplication.CURRENT_TOOL.handleMove(canvasTouchCoordinate);
			return true;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceListener.onTouch UP");
			PaintroidApplication.CURRENT_TOOL.handleUp(canvasTouchCoordinate);
			// falls brush und kein move konsumiert
			// currentTool.handleTab(coordinate);
			return true;
		default:
			return false;
		}
	}
}
