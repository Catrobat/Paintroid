package org.catrobat.paintroid.ui.dragndrop;

import android.view.View;


public abstract class BrickDragAndDrop {

	public abstract void moveOrMerge(View v, float x, float y);

	public abstract void showOptionFromCurrentPosition(float x, float y);

	public abstract void dragEnded();

	public abstract void setViewCoordinates();


}
