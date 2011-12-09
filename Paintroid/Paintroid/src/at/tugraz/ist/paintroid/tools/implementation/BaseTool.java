package at.tugraz.ist.paintroid.tools.implementation;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
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

	public abstract boolean handleDown(Point coordinate);

	public abstract boolean handleMove(Point deltaMove);

	public abstract boolean handleTab(Point coordinate);

	public abstract boolean handleUp(Point coordinate);

	public void setCommandHandler(CommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	public void setDrawPaint(Paint paint) {
		this.drawPaint = paint;
	}

	public abstract void draw(Canvas canvas);
}
