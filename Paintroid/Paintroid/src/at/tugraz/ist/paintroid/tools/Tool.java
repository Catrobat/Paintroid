package at.tugraz.ist.paintroid.tools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import at.tugraz.ist.paintroid.commandmanagement.CommandHandler;

public interface Tool {

	public boolean handleTab(Point coordinate);

	public boolean handleDown(Point coordinate);

	public boolean handleMove(Point deltaMove);

	public boolean handleUp(Point coordinate);

	public void setCommandHandler(CommandHandler commandHandler);

	public void setDrawColor(int color);

	public void setDrawPaint(Paint paint);

	public void draw(Canvas canvas);
}
