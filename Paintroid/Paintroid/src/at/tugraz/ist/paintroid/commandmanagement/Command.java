package at.tugraz.ist.paintroid.commandmanagement;

import android.graphics.Canvas;

public interface Command {

	public void run(Canvas canvas);

	public boolean isUndoable();
}
