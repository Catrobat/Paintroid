package at.tugraz.ist.paintroid.tools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.commandmanagement.CommandHandler;

public interface Tool {
	// standard stroke widths in pixels
	public static final int stroke1 = 1;
	public static final int stroke5 = 5;
	public static final int stroke15 = 15;
	public static final int stroke25 = 25;

	public boolean handleTab(PointF coordinate);

	public boolean handleDown(PointF coordinate);

	public boolean handleMove(PointF coordinate);

	public boolean handleUp(PointF coordinate);

	public void setCommandHandler(CommandHandler commandHandler);

	public void changePaintColor(int color);

	public void changePaintStrokeWidth(int strokeWidth);

	public void changePaintStrokeCap(Cap cap);

	public void setDrawPaint(Paint paint);

	public Paint getDrawPaint();

	public void draw(Canvas canvas);

	public ToolType getToolType();
}
