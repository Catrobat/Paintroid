package at.tugraz.ist.paintroid.commandmanagement.implementation;

import android.graphics.Canvas;
import android.graphics.Paint;
import at.tugraz.ist.paintroid.commandmanagement.Command;

public abstract class BaseCommand implements Command {
	protected Paint paint;
	protected Canvas canvas;

	public BaseCommand(Paint paint) {
		this.paint = new Paint(paint);
	}

	@Override
	public abstract void run(Canvas canvas);

	@Override
	public boolean isUndoable() {
		return false;
	}
}
