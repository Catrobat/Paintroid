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
/**
 *    This file incorporates work covered by the following copyright and  
 *    permission notice: 
 *    
 *        Copyright (C) 2011 Devmil (Michael Lamers) 
 *        Mail: develmil@googlemail.com
 *
 *        Licensed under the Apache License, Version 2.0 (the "License");
 *        you may not use this file except in compliance with the License.
 *        You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing, software
 *        distributed under the License is distributed on an "AS IS" BASIS,
 *        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *        See the License for the specific language governing permissions and
 *        limitations under the License.
 */

package org.catrobat.paintroid.dialog.colorpicker;

import org.catrobat.paintroid.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class PresetSelectorView extends LinearLayout {

	private int mSelectedColor;
	private TypedArray mPresetColors;
	final float mScale = getContext().getResources().getDisplayMetrics().density;
	private final static int MAXIMUM_COLOR_BUTTONS_IN_COLOR_ROW = 4;
	private TableLayout mTableLayout;
	private int COLOR_BUTTON_MARGIN = 2;

	private OnColorChangedListener mOnColorChangedListener;

	public PresetSelectorView(Context context) {
		super(context);
		mTableLayout = new TableLayout(context);
		init(context);
	}

	public PresetSelectorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTableLayout = new TableLayout(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mTableLayout.setGravity(Gravity.TOP);
		mTableLayout.setOrientation(TableLayout.VERTICAL);
		mTableLayout.setStretchAllColumns(true);

		mPresetColors = getResources().obtainTypedArray(R.array.preset_colors);

		OnClickListener presetButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSelectedColor = mPresetColors.getColor(v.getId(), 0);
				onColorChanged();
			}
		};

		TableRow colorButtonsTableRow = new TableRow(context);
		TableRow.LayoutParams colorButtonLayoutParameters = new TableRow.LayoutParams();
		colorButtonLayoutParameters.setMargins(COLOR_BUTTON_MARGIN,
				COLOR_BUTTON_MARGIN, COLOR_BUTTON_MARGIN, COLOR_BUTTON_MARGIN);
		for (int colorButtonIndexInRow = 0; colorButtonIndexInRow < mPresetColors
				.length(); colorButtonIndexInRow++) {
			Button colorButton = new ColorPickerPresetColorButton(context,
					mPresetColors.getColor(colorButtonIndexInRow, 0));
			colorButton.setId(colorButtonIndexInRow);
			colorButton.setOnClickListener(presetButtonListener);
			colorButtonsTableRow.addView(colorButton,
					colorButtonLayoutParameters);
			if ((colorButtonIndexInRow + 1)
					% MAXIMUM_COLOR_BUTTONS_IN_COLOR_ROW == 0) {
				mTableLayout.addView(colorButtonsTableRow);
				colorButtonsTableRow = new TableRow(context);
			}
		}
		addView(mTableLayout);
	}

	public int getSelectedColor() {
		return mSelectedColor;
	}

	public void setSelectedColor(int color) {
		if (color == mSelectedColor) {
			return;
		}
		mSelectedColor = color;
	}

	private void onColorChanged() {
		if (mOnColorChangedListener != null) {
			mOnColorChangedListener.colorChanged(getSelectedColor());
		}
	}

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		mOnColorChangedListener = listener;
	}

	public interface OnColorChangedListener {
		public void colorChanged(int color);
	}

}
