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

package org.catrobat.paintroid.tools.implementation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.VisibleForTesting;
import android.view.View;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.button.ColorButton;

public class EraserTool extends DrawTool {

	@VisibleForTesting
	public ColorButton colorButton;
	private View colorPickerPalette;
	@ColorInt
	private int previousColor;

	public EraserTool(Context context, ToolType toolType) {
		super(context, toolType);

		previousColor = Color.MAGENTA;
		displayEraserInsteadOfSelectedColor();
	}

	private void displayEraserInsteadOfSelectedColor() {
		colorButton = (ColorButton) ((Activity) context).findViewById(R.id.btn_top_color);
		colorButton.setImageResource(R.drawable.icon_menu_eraser);

		colorPickerPalette = ((Activity) context).findViewById(R.id.btn_top_color_palette);
		colorPickerPalette.setVisibility(View.INVISIBLE);

		colorButton.setDrawSelectedColor(false);
	}

	@Override
	public Paint getDrawPaint() {
		Paint paint = super.getDrawPaint();
		paint.setColor(previousColor);
		return paint;
	}

	@Override
	public void setDrawPaint(Paint paint) {
		super.setDrawPaint(paint);
		previousColor = paint.getColor();
	}

	@Override
	public void leaveTool() {
		super.leaveTool();
		colorButton.resetDrawSelectedColor();
		colorButton.setImageResource(0);
		colorPickerPalette.setVisibility(View.VISIBLE);
	}
}
