package at.tugraz.ist.paintroid.tools.implementation;

import java.util.Observable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.dialog.BrushPickerDialog;
import at.tugraz.ist.paintroid.dialog.BrushPickerDialog.OnBrushChangedListener;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;
import at.tugraz.ist.paintroid.tools.Tool;

public abstract class BaseTool extends Observable implements Tool {
	protected Point position = null;
	protected Paint drawPaint = null;
	protected ToolType toolType = null;
	protected ColorPickerDialog colorPicker = null;
	protected BrushPickerDialog brushPicker = null;
	protected Context context;

	public BaseTool(Context context) {
		super();
		this.context = context;
		drawPaint = new Paint();
		drawPaint.setColor(Color.BLACK);
		drawPaint.setAntiAlias(true);
		drawPaint.setDither(true);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		drawPaint.setStrokeWidth(Tool.stroke25);
		final BaseTool self = this;
		OnColorPickedListener mColor = new OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				self.changePaintColor(color);
			}
		};

		colorPicker = new ColorPickerDialog(context, mColor);
		OnBrushChangedListener mStroke = new OnBrushChangedListener() {
			@Override
			public void setCap(Cap cap) {
				self.changePaintStrokeCap(cap);
			}

			@Override
			public void setStroke(int strokeWidth) {
				self.changePaintStrokeWidth(strokeWidth);
			}
		};

		brushPicker = new BrushPickerDialog(context, mStroke);
		this.position = new Point(0, 0);
		setToolType();
	}

	protected abstract void setToolType();

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

	protected void showColorPicker() {
		colorPicker.show();
		colorPicker.setInitialColor(this.getDrawPaint().getColor());
	}

	protected void showBrushPicker() {
		brushPicker.show();
	}

	@Override
	public void attributeButtonClick(int buttonNumber) {
		switch (buttonNumber) {
		case 1:
			showColorPicker();
			break;
		case 2:
			showBrushPicker();
			break;
		}
	}

	@Override
	public int getAttributeButtonResource(int buttonNumber) {
		if (buttonNumber == 1) {
			if (drawPaint.getColor() == Color.TRANSPARENT) {
				return R.drawable.transparent_64;
			}
		} else if (buttonNumber == 2) {
			int strokeWidth = (int) drawPaint.getStrokeWidth();
			switch (this.getDrawPaint().getStrokeCap()) {
			case SQUARE:
				switch (strokeWidth) {

				case 1:
					return R.drawable.rect_1_32;
				case 5:
					return R.drawable.rect_2_32;
				case 15:
					return R.drawable.rect_3_32;
				case 25:
					return R.drawable.rect_4_32;
				}
				break;
			case ROUND:
				switch (strokeWidth) {

				case 1:
					return R.drawable.circle_1_32;
				case 5:
					return R.drawable.circle_2_32;
				case 15:
					return R.drawable.circle_3_32;
				case 25:
					return R.drawable.circle_4_32;
				}
				break;
			default:
				break;
			}
		}
		return 0;
	}

	@Override
	public int getAttributeButtonColor(int buttonNumber) {
		if (buttonNumber == 1) {
			return drawPaint.getColor();
		}
		return Color.BLACK;
	}
}
