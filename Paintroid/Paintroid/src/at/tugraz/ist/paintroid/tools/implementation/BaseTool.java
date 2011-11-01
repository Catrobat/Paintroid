package at.tugraz.ist.paintroid.tools.implementation;

import android.graphics.Paint;
import android.graphics.Point;
import at.tugraz.ist.paintroid.tools.Tool;

public abstract class BaseTool implements Tool {

	protected Point position;
	protected int drawColor;
	protected Paint drawPaint;

	public boolean handleDown(Point coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean handleMove(Point deltaMove) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean handleTab(Point coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean handleUp(Point coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setCommandHandler() {

	}

	public void setDrawColor(int color) {
		this.drawColor = color;
	}

	public void setDrawPaint(Paint paint) {
		this.drawPaint = paint;
	}
}
