/*
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

package org.catrobat.paintroid.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.Shader.TileMode;

import androidx.annotation.ColorInt;

import androidx.appcompat.widget.AppCompatImageButton;

public class ColorPickerPresetColorButton extends AppCompatImageButton {

	@ColorInt
	int color;

	public ColorPickerPresetColorButton(Context context) {
		this(context, Color.BLACK);
	}

	public ColorPickerPresetColorButton(Context context, @ColorInt int color) {
		super(context, null, R.attr.borderlessButtonStyle);
		this.color = color;
		Bitmap checkeredBitmap = BitmapFactory.decodeResource(getResources(), org.catrobat.paintroid.colorpicker.R.drawable.pocketpaint_checkeredbg);
		BitmapShader bitmapShader = new BitmapShader(checkeredBitmap, TileMode.REPEAT, TileMode.REPEAT);
		setBackground(ColorPickerDialog.CustomColorDrawable.createDrawable(bitmapShader, color));
	}

	public @ColorInt int getColor() {
		return color;
	}
}
