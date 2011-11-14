package at.tugraz.ist.paintroid.commandmanagement;

import android.graphics.Canvas;

public interface Command extends Runnable {

	public void setCanvas(Canvas canvas);
}
