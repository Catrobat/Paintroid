package at.tugraz.ist.paintroid.commandmanagement.implementation;

import android.graphics.Canvas;
import android.graphics.Paint;
import at.tugraz.ist.paintroid.commandmanagement.Command;

public abstract class BaseCommand implements Command {
	protected Paint paint;
	protected Canvas canvas = null;

	public BaseCommand(Paint paint) {
		this.paint = paint;
	}

	@Override
	public void run() {
		if (this.canvas != null) {
			draw();
		}
	}

	@Override
	public void setCanvas(Canvas canvas) {
		// TODO Auto-generated method stub

	}

	protected abstract void draw();
}
