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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatButton;

public class ColorPickerPresetColorButton extends AppCompatButton {

	private Paint colorPaint = new Paint();

	public ColorPickerPresetColorButton(Context context) {
		this(context, Color.BLACK);
	}

	public ColorPickerPresetColorButton(Context context, @ColorInt int color) {
		super(context);
		colorPaint.setColor(color);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPaint(ColorPickerDialog.backgroundPaint);
		canvas.drawPaint(colorPaint);
	}

	public @ColorInt int getColor() {
		return colorPaint.getColor();
	}
}
