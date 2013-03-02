package org.catrobat.paintroid.dialog.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CheckeredTransparentLinearLayout extends LinearLayout {

	private Paint mColorPaint = new Paint();

	public CheckeredTransparentLinearLayout(Context context) {
		super(context);
	}

	public CheckeredTransparentLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void updateBackground() {
		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		Bitmap background = Bitmap.createBitmap(getWidth(), getHeight(),
				Config.ARGB_8888);
		background.eraseColor(ColorPickerDialog.mNewColor);
		Canvas checkerdBackgroundCanvas = new Canvas(background);

		Rect colorRect = new Rect(0, 0, getWidth(), getHeight());
		if (ColorPickerDialog.mBackgroundPaint != null) {
			checkerdBackgroundCanvas.drawRect(colorRect,
					ColorPickerDialog.mBackgroundPaint);
		}
		mColorPaint.setColor(ColorPickerDialog.mNewColor);
		checkerdBackgroundCanvas.drawPaint(mColorPaint);
		setBackgroundDrawable(new BitmapDrawable(background));
	}
}
