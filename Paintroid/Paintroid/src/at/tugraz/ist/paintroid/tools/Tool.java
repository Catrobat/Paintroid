package at.tugraz.ist.paintroid.tools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.commandmanagement.CommandHandler;

public interface Tool {

	public boolean handleTab(PointF coordinate);

	public boolean handleDown(PointF coordinate);

	public boolean handleMove(PointF coordinate);

	public boolean handleUp(PointF coordinate);

	public void setCommandHandler(CommandHandler commandHandler);

	public void setDrawPaint(Paint paint);

	public Paint getDrawPaint();

	public void draw(Canvas canvas);

	public ToolType getToolType();
}
