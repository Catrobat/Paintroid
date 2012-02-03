package at.tugraz.ist.paintroid.commandmanagement.implementation;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import at.tugraz.ist.paintroid.PaintroidApplication;

public class PathCommand extends BaseCommand {

	protected Path path;

	public PathCommand(Paint paint, Path path) {
		super(paint);
		this.path = new Path(path);
	}

	@Override
	public void run(Canvas canvas) {
		Log.d(PaintroidApplication.TAG, "PathCommand.run");
		canvas.drawPath(path, paint);
	}

	@Override
	public boolean isUndoable() {
		return true;
	}

}
