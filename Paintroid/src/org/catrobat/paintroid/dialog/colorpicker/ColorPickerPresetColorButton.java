package org.catrobat.paintroid.dialog.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.Button;

public class ColorPickerPresetColorButton extends Button {

	private Paint mColorPaint = new Paint();
	private int mWidth = 0;
	private int mHeight = 0;

	public ColorPickerPresetColorButton(Context context, int color) {
		super(context);
		mColorPaint.setColor(color);
		mWidth = getWidth();
		mHeight = getHeight();
	}

	@Override
	public void draw(Canvas canvas) {
		Rect colorRect = new Rect(0, 0, mWidth, mHeight);
		if (ColorPickerDialog.mBackgroundPaint != null) {
			canvas.drawRect(colorRect, ColorPickerDialog.mBackgroundPaint);
		}
		canvas.drawRect(colorRect, mColorPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeight = MeasureSpec.getSize(heightMeasureSpec);
	}

}
