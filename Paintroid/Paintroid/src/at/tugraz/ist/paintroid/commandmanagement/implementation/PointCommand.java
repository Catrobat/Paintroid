package at.tugraz.ist.paintroid.commandmanagement.implementation;

import android.graphics.Paint;
import android.graphics.PointF;

public class PointCommand extends BaseCommand {

	protected PointF point;

	public PointCommand(Paint paint, PointF point) {
		super(paint);
		this.point = new PointF(point.x, point.y);
	}

	@Override
	protected void draw() {
		// TODO Auto-generated method stub

	}

}
