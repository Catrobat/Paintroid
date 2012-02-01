package at.tugraz.ist.paintroid.tools.implementation;

import java.util.Observable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.tools.Tool;

public abstract class BaseTool extends Observable implements Tool {
	protected Point position = null;
	protected Paint drawPaint = null;
	protected ToolType toolType = null;

	public BaseTool() {
		super();
		drawPaint = new Paint();
		drawPaint.setColor(Color.BLACK);
		drawPaint.setAntiAlias(true);
		drawPaint.setDither(true);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		drawPaint.setStrokeWidth(Tool.stroke25);
		this.position = new Point(0, 0);
		setToolType();
	}

	protected abstract void setToolType();

	@Override
	public abstract boolean handleDown(PointF coordinate);

	@Override
	public abstract boolean handleMove(PointF coordinate);

	@Override
	public abstract boolean handleUp(PointF coordinate);

	@Override
	public void changePaintColor(int color) {
		this.drawPaint.setColor(color);
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public void changePaintStrokeWidth(int strokeWidth) {
		this.drawPaint.setStrokeWidth(strokeWidth);
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public void changePaintStrokeCap(Cap cap) {
		this.drawPaint.setStrokeCap(cap);
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public void setDrawPaint(Paint paint) {
		this.drawPaint = paint;
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public Paint getDrawPaint() {
		return new Paint(this.drawPaint);
	}

	@Override
	public abstract void draw(Canvas canvas);

	@Override
	public ToolType getToolType() {
		return this.toolType;
	}
}
