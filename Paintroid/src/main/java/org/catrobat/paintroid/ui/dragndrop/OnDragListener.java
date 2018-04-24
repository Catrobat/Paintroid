/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui.dragndrop;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;

public class OnDragListener implements View.OnDragListener {

	private BrickDragAndDrop brick;

	public OnDragListener(BrickDragAndDrop brick) {
		this.brick = brick;
	}

	public boolean onDrag(View v, DragEvent event) {

		final int action = event.getAction();
		switch (action) {

			case DragEvent.ACTION_DRAG_STARTED:
				brick.resetMoveAlreadyAnimated();
				return true;
			case DragEvent.ACTION_DRAG_ENTERED:
				brick.setupProperties();
				brick.goOutsideView(false, event.getX(), event.getY());
				return true;
			case DragEvent.ACTION_DRAG_LOCATION:
				brick.showOptionFromCurrentPosition(event.getX(), event.getY());
				return true;
			case DragEvent.ACTION_DRAG_EXITED:
				brick.goOutsideView(true, event.getX(), event.getY());
				return true;
			case DragEvent.ACTION_DROP:
				brick.moveOrMerge(v, event.getX(), event.getY());
				return true;
			case DragEvent.ACTION_DRAG_ENDED:
				brick.dragEnded();
				return true;
			default:
				Log.e("Drag and Drop: ", "Unknown action type receiver by OnDragListener");
				break;
		}

		return false;
	}
}
