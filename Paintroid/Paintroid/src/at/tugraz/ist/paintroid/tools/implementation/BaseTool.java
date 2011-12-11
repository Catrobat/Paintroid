package at.tugraz.ist.paintroid.tools.implementation;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.commandmanagement.CommandHandler;
import at.tugraz.ist.paintroid.tools.Tool;

public abstract class BaseTool implements Tool {

	protected Point position = null;
	protected Paint drawPaint = null;
	protected CommandHandler commandHandler = null;

	public BaseTool(Paint paint) {
		this.drawPaint = paint;
		this.position = new Point(0, 0);
	}

	@Override
	public abstract boolean handleDown(PointF coordinate);

	@Override
	public abstract boolean handleMove(PointF coordinate);

	@Override
	public abstract boolean handleTab(PointF coordinate);

	@Override
	public abstract boolean handleUp(PointF coordinate);

	@Override
	public void setCommandHandler(CommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	@Override
	public void setDrawPaint(Paint paint) {
		this.drawPaint = paint;
	}

	@Override
	public abstract void draw(Canvas canvas);
}
