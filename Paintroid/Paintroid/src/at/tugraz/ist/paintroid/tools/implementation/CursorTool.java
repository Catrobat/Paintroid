package at.tugraz.ist.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.MainActivity.ToolType;

public class CursorTool extends BaseToolWithShape {

	protected Path pathToDraw;
	protected PointF previousEventCoordinate = new PointF();
	protected PointF initialEventCoordinate = new PointF();
	protected PointF movedDistance = new PointF(0, 0);

	public CursorTool(Context context) {
		super(context);
		pathToDraw = new Path();
		pathToDraw.incReserve(1);
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		initialEventCoordinate.set(coordinate.x, coordinate.y);
		previousEventCoordinate.set(coordinate.x, coordinate.y);
		pathToDraw.moveTo(coordinate.x, coordinate.y);
		movedDistance.set(0, 0);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetInternalState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawShape(Canvas canvas) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setToolType() {
		this.toolType = ToolType.CURSOR;

	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawPath(pathToDraw, drawPaint);
	}

}
