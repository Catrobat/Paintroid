package at.tugraz.ist.paintroid.tools;

import android.graphics.Paint;
import android.graphics.Point;

public interface Tool {
	public boolean handleTab(Point coordinate);

	public boolean handleDown(Point coordinate);

	public boolean handleMove(Point deltaMove);

	public boolean handleUp(Point coordinate);

	public void setCommandHandler();

	public void setDrawColor(int color);

	public void setDrawPaint(Paint paint);
}
