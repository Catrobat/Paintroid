/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui.button;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class ColorButton extends ImageButton implements OnColorPickedListener {

	private static final int RECT_SIDE_LENGTH = 50;
	private static final int RECT_BORDER_SIZE = 2;
	private static final int RECT_BORDER_COLOR = Color.LTGRAY;

	private Paint mColorPaint;
	private Paint mBorderPaint;
	private Paint mBackgroundPaint;
	private Bitmap mBackgroundBitmap;

	private int mHeigth;
	private int mWidth;

	public ColorButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mColorPaint = new Paint();
		mBackgroundPaint = new Paint();
		mBorderPaint = new Paint();
		mBorderPaint.setColor(RECT_BORDER_COLOR);

		mBackgroundBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.checkeredbg);
		BitmapShader backgroundShader = new BitmapShader(mBackgroundBitmap,
				TileMode.REPEAT, TileMode.REPEAT);
		mBackgroundPaint.setShader(backgroundShader);

		ColorPickerDialog.getInstance().addOnColorPickedListener(this);
	}

	@Override
	public void colorChanged(int color) {

		mColorPaint.setColor(color);
		invalidate();
	}

	@Override
	public void draw(Canvas canvas) {
		int rectX = mWidth / 2 - RECT_SIDE_LENGTH / 2;
		int rectY = mHeigth / 2 - RECT_SIDE_LENGTH / 2;

		Rect colorRect = new Rect(rectX, rectY, rectX + RECT_SIDE_LENGTH, rectY
				+ RECT_SIDE_LENGTH);
		Rect borderRect = new Rect(colorRect.left - RECT_BORDER_SIZE,
				colorRect.top - RECT_BORDER_SIZE, colorRect.right
						+ RECT_BORDER_SIZE, colorRect.bottom + RECT_BORDER_SIZE);

		canvas.drawRect(borderRect, mBorderPaint);
		canvas.drawRect(colorRect, mBackgroundPaint);
		canvas.drawRect(colorRect, mColorPaint);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeigth = MeasureSpec.getSize(heightMeasureSpec);
	}
}
