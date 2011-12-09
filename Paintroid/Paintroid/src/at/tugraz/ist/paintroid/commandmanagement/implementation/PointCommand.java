package at.tugraz.ist.paintroid.commandmanagement.implementation;

import android.graphics.Paint;
import android.graphics.Point;

public class PointCommand extends BaseCommand {

	protected Point point;

	public PointCommand(Paint paint, Point point) {
		super(paint);
		this.point = point;
	}

	@Override
	protected void draw() {
		// TODO Auto-generated method stub

	}

}
