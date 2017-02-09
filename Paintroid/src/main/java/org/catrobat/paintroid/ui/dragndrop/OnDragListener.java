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
		Log.e("---onDrag called: ", "---");
		switch (action) {

			case DragEvent.ACTION_DRAG_STARTED:
				Log.e("Drag and Drop: ","ACTION_DRAG_STARTED: x: " + event.getX() + " y: " + event.getY());
				return true;
			case DragEvent.ACTION_DRAG_ENTERED:
				brick.setViewCoordinates();
				Log.e("Drag and Drop: ","ACTION_DRAG_ENTERED");
				return true;
			case DragEvent.ACTION_DRAG_LOCATION:
				brick.showOptionFromCurrentPosition(event.getX(), event.getY());
				Log.e("Drag and Drop: ","ACTION_DRAG_LOCATION: x: " + event.getX() + " y: " + event.getY());
				return true;
			case DragEvent.ACTION_DRAG_EXITED:
				Log.e("Drag and Drop: ","ACTION_DRAG_EXITED");
				return true;
			case DragEvent.ACTION_DROP:
				brick.moveOrMerge(v, event.getX(), event.getY());
				Log.e("Drag and Drop: ","ACTION_DROP: x: " + event.getX() + " y: " + event.getY());
				return true;
			case DragEvent.ACTION_DRAG_ENDED:
				brick.dragEnded();
				Log.e("Drag and Drop: ","ACTION_DRAG_ENDED");
				return true;
			default:
				Log.e("Drag and Drop: ","Unknown action type receiver by OnDragListener");
				break;

		}

		return false;
	}


}
