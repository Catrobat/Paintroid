/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui.button;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

import org.catrobat.paintroid.R;

public class ColorButton extends AppCompatImageButton {

	private static final int RECT_SIDE_LENGTH = 50;
	private static final int RECT_BORDER_SIZE = 2;
	private static final int RECT_BORDER_COLOR = Color.LTGRAY;
	private static final boolean DEFAULT_DRAW_SELECTED_COLOR = true;

	private Paint colorPaint;
	private Paint borderPaint;
	private Paint backgroundPaint;

	private int height;
	private int width;

	private boolean drawSelectedColor = DEFAULT_DRAW_SELECTED_COLOR;

	public ColorButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) {
			init();
		}
	}

	private void init() {
		colorPaint = new Paint();
		backgroundPaint = new Paint();
		borderPaint = new Paint();
		borderPaint.setColor(RECT_BORDER_COLOR);

		Bitmap mBackgroundBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.checkeredbg);
		BitmapShader backgroundShader = new BitmapShader(mBackgroundBitmap,
				TileMode.REPEAT, TileMode.REPEAT);
		backgroundPaint.setShader(backgroundShader);
	}

	public void resetDrawSelectedColor() {
		drawSelectedColor = DEFAULT_DRAW_SELECTED_COLOR;
	}

	public boolean getDrawSelectedColor() {
		return drawSelectedColor;
	}

	public void setDrawSelectedColor(boolean drawSelectedColor) {
		this.drawSelectedColor = drawSelectedColor;
	}

	public void colorChanged(int color) {
		colorPaint.setColor(color);
		invalidate();
	}

	@Override
	public void draw(Canvas canvas) {

		if (!drawSelectedColor) {
			super.draw(canvas);
		} else {
			int rectX = width / 2 - RECT_SIDE_LENGTH / 2;
			int rectY = height / 2 - RECT_SIDE_LENGTH / 2;
			Rect colorRect = new Rect(rectX, rectY, rectX + RECT_SIDE_LENGTH, rectY
					+ RECT_SIDE_LENGTH);
			Rect borderRect = new Rect(colorRect.left - RECT_BORDER_SIZE,
					colorRect.top - RECT_BORDER_SIZE, colorRect.right
					+ RECT_BORDER_SIZE, colorRect.bottom + RECT_BORDER_SIZE);

			if (!isInEditMode()) {
				canvas.drawRect(borderRect, borderPaint);
				canvas.drawRect(colorRect, backgroundPaint);
				canvas.drawRect(colorRect, colorPaint);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
	}
}
