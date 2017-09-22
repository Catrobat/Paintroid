package org.catrobat.paintroid.ui.dragndrop;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.catrobat.paintroid.listener.LayerListener;


public class BrickDragAndDropLayerMenu extends BrickDragAndDrop {

	private ListView view;
	private int listViewHeight;
	private int heightOneLayer;
	private int numLayer;
	private int currentDragLayerPos;

	public BrickDragAndDropLayerMenu(ListView v) {
		view = v;
	}

	public void setDragStartPosition(int startLayerPos) {
		currentDragLayerPos = startLayerPos;
	}

	public void setViewCoordinates() {
		listViewHeight = view.getHeight();
		numLayer = view.getChildCount();
		if (numLayer > 0)
			heightOneLayer = view.getChildAt(0).getHeight();
	}

	public void showOptionFromCurrentPosition(float x, float y) {

		int numLayerDropPosition = 0;

		if (y >= 0 && y <= listViewHeight) {
			for (int i = 0; i <= listViewHeight; i += heightOneLayer) {
				if (y > i && y < (i + heightOneLayer))
					break;
				else
					numLayerDropPosition++;
			}

			if (numLayerDropPosition != currentDragLayerPos) {

				//lower third of the Layer
				if (y > (((numLayerDropPosition + 1) * heightOneLayer) - (heightOneLayer / 3))) {
					if (view.getChildAt(numLayerDropPosition) != null)
						view.getChildAt(numLayerDropPosition).setBackgroundColor(0);

					if (currentDragLayerPos < numLayerDropPosition) {
						LayerListener.getInstance().moveLayer(currentDragLayerPos, numLayerDropPosition);
						LayerListener.getInstance().refreshView();
						currentDragLayerPos = numLayerDropPosition;
					}
					if (currentDragLayerPos > numLayerDropPosition) {
						LayerListener.getInstance().moveLayer(currentDragLayerPos, numLayerDropPosition + 1);
						LayerListener.getInstance().refreshView();
						currentDragLayerPos = numLayerDropPosition + 1;
					}


				}

				//upper third of the Layer
				if (y < ((numLayerDropPosition * heightOneLayer) + (heightOneLayer / 3))) {
					if (view.getChildAt(numLayerDropPosition) != null)
						view.getChildAt(numLayerDropPosition).setBackgroundColor(0);

					if (currentDragLayerPos > numLayerDropPosition) {
						LayerListener.getInstance().moveLayer(currentDragLayerPos, numLayerDropPosition);
						LayerListener.getInstance().refreshView();
						currentDragLayerPos = numLayerDropPosition;
					}

					if (currentDragLayerPos < numLayerDropPosition) {
						LayerListener.getInstance().moveLayer(currentDragLayerPos, numLayerDropPosition - 1);
						LayerListener.getInstance().refreshView();
						currentDragLayerPos = numLayerDropPosition - 1;
					}

				}

				if (y < (((numLayerDropPosition + 1) * heightOneLayer) - (heightOneLayer / 3)) &&
						y > ((numLayerDropPosition * heightOneLayer) + (heightOneLayer / 3))) {
					if (view.getChildAt(numLayerDropPosition) != null &&
							view.getChildAt(numLayerDropPosition).getDrawingCacheBackgroundColor() != Color.YELLOW)
					view.getChildAt(numLayerDropPosition).setBackgroundColor(Color.YELLOW);
				}

			}
		}
	}

	public void moveOrMerge(View v, float x, float y) {

		int numLayerDropPosition = 0;

		if (y >= 0 && y <= listViewHeight) {
			for (int i = 0; i <= listViewHeight; i += heightOneLayer) {
				if (y > i && y < (i + heightOneLayer))
					break;
				else
					numLayerDropPosition++;
			}

			if (numLayerDropPosition != currentDragLayerPos) {

				if (y < (((numLayerDropPosition + 1) * heightOneLayer) - (heightOneLayer / 3)) &&
						y > ((numLayerDropPosition * heightOneLayer) + (heightOneLayer / 3))) {
					LayerListener.getInstance().mergeLayer(currentDragLayerPos, numLayerDropPosition);
				}
			}
		}

	}

	public void dragEnded(){
		LayerListener.getInstance().selectLayer(LayerListener.getInstance().getCurrentLayer());
	}


}
