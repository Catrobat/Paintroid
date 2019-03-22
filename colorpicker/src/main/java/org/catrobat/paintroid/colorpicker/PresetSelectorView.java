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
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class PresetSelectorView extends LinearLayout {

	private static final int MAXIMUM_COLOR_BUTTONS_IN_COLOR_ROW = 4;
	private static final int COLOR_BUTTON_MARGIN = 2;

	private int selectedColor;
	private TableLayout tableLayout;

	private OnColorChangedListener onColorChangedListener;

	public PresetSelectorView(Context context) {
		super(context);
		tableLayout = new TableLayout(context);
		init(context);
	}

	public PresetSelectorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		tableLayout = new TableLayout(context, attrs);
		init(context);
	}

	private void init(Context context) {
		tableLayout.setGravity(Gravity.TOP);
		tableLayout.setOrientation(TableLayout.VERTICAL);
		tableLayout.setStretchAllColumns(true);
		tableLayout.setShrinkAllColumns(true);

		OnClickListener presetButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectedColor = ((ColorPickerPresetColorButton) v).getColor();
				onColorChanged();
			}
		};

		TypedArray presetColors = getResources().obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors);

		TableRow colorButtonsTableRow = new TableRow(context);
		TableRow.LayoutParams colorButtonLayoutParameters = new TableRow.LayoutParams();
		colorButtonLayoutParameters.setMargins(COLOR_BUTTON_MARGIN,
				COLOR_BUTTON_MARGIN, COLOR_BUTTON_MARGIN, COLOR_BUTTON_MARGIN);
		for (int colorButtonIndexInRow = 0; colorButtonIndexInRow < presetColors
				.length(); colorButtonIndexInRow++) {
			int color = presetColors.getColor(colorButtonIndexInRow, Color.TRANSPARENT);
			View colorButton = new ColorPickerPresetColorButton(context, color);
			colorButton.setOnClickListener(presetButtonListener);
			colorButtonsTableRow.addView(colorButton, colorButtonLayoutParameters);

			if ((colorButtonIndexInRow + 1) % MAXIMUM_COLOR_BUTTONS_IN_COLOR_ROW == 0) {
				tableLayout.addView(colorButtonsTableRow);
				colorButtonsTableRow = new TableRow(context);
			}
		}

		presetColors.recycle();

		addView(tableLayout);
	}

	private int getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(int color) {
		if (color == selectedColor) {
			return;
		}
		selectedColor = color;
	}

	private void onColorChanged() {
		if (onColorChangedListener != null) {
			onColorChangedListener.colorChanged(getSelectedColor());
		}
	}

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		onColorChangedListener = listener;
	}

	interface OnColorChangedListener {
		void colorChanged(int color);
	}
}
