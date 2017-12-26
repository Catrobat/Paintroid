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
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

import org.catrobat.paintroid.R;

public class ColorButton extends AppCompatImageButton {

	private static final int RECT_SIDE_LENGTH = 25;
	private static final int RECT_BORDER_SIZE = 2;
	private static final int RECT_BORDER_COLOR = Color.LTGRAY;
	private static final boolean DEFAULT_DRAW_SELECTED_COLOR = true;

	private Paint colorPaint;
	private Paint borderPaint;
	private Paint backgroundPaint;

	private RectF rect;

	private boolean drawSelectedColor = DEFAULT_DRAW_SELECTED_COLOR;

	public ColorButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		colorPaint = new Paint();
		backgroundPaint = new Paint();
		borderPaint = new Paint();
		borderPaint.setColor(RECT_BORDER_COLOR);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(RECT_BORDER_SIZE);

		rect = new RectF();

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
		super.draw(canvas);

		if (!drawSelectedColor) {
			return;
		}

		float density = getResources().getDisplayMetrics().density;
		float rectSideLengthDp = RECT_SIDE_LENGTH * density;

		float width = canvas.getWidth();
		float height = canvas.getHeight();

		float rectX = (width - rectSideLengthDp) / 2;
		float rectY = (height - rectSideLengthDp) / 2;
		rect.set(rectX, rectY, rectX + rectSideLengthDp, rectY + rectSideLengthDp);

		if (Color.alpha(colorPaint.getColor()) != 0xff) {
			canvas.drawRect(rect, backgroundPaint);
		}
		canvas.drawRect(rect, colorPaint);
		canvas.drawRect(rect, borderPaint);
	}
}
