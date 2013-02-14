package org.catrobat.paintroid.ui.button;

import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class ColorButton extends ImageButton implements OnColorPickedListener {

	private static final int RECT_SIDE_LENGTH = 50;
	private static final int RECT_BORDER_SIZE = 2;
	private static final int RECT_BORDER_COLOR = Color.LTGRAY;

	private Paint mColorPaint;
	private Paint mBorderPaint;
	private int mHeigth;
	private int mWidth;

	public ColorButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ColorButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mColorPaint = new Paint();
		mBorderPaint = new Paint();
		mBorderPaint.setColor(RECT_BORDER_COLOR);
		ColorPickerDialog.getInstance().addOnColorPickedListener(this);
	}

	@Override
	public void colorChanged(int color) {
		mColorPaint.setColor(color);
		invalidate();
	}

	@Override
	public void draw(Canvas canvas) {
		// super.draw(canvas);
		int rectX = mWidth / 2 - RECT_SIDE_LENGTH / 2;
		int rectY = mHeigth / 2 - RECT_SIDE_LENGTH / 2;

		Rect colorRect = new Rect(rectX, rectY, rectX + RECT_SIDE_LENGTH, rectY
				+ RECT_SIDE_LENGTH);
		Rect borderRect = new Rect(colorRect.left - RECT_BORDER_SIZE,
				colorRect.top - RECT_BORDER_SIZE, colorRect.right
						+ RECT_BORDER_SIZE, colorRect.bottom + RECT_BORDER_SIZE);
		canvas.drawRect(borderRect, mBorderPaint);
		canvas.drawRect(colorRect, mColorPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeigth = MeasureSpec.getSize(heightMeasureSpec);
	}
}
