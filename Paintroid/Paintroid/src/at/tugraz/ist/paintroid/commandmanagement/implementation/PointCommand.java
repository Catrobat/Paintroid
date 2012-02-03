package at.tugraz.ist.paintroid.commandmanagement.implementation;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import at.tugraz.ist.paintroid.PaintroidApplication;

public class PointCommand extends BaseCommand {

	protected PointF point;

	public PointCommand(Paint paint, PointF point) {
		super(paint);
		this.point = new PointF(point.x, point.y);
	}

	@Override
	public void run(Canvas canvas) {
		Log.d(PaintroidApplication.TAG, "PointCommand.run");
		canvas.drawPoint(point.x, point.y, paint);
	}

	@Override
	public boolean isUndoable() {
		return true;
	}

}
