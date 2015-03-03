/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.paintroid.dialog.colorpicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.Button;

@SuppressLint("ViewConstructor")
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
