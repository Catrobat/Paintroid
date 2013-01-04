/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
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

	private int selectedColor;
	private TypedArray presetColors;
	final float scale = getContext().getResources().getDisplayMetrics().density;
	private int presetButtonHeight = (int) (50.0f * scale + 0.5f);
	private TableLayout mTableLayout;

	private OnColorChangedListener onColorChangedListener;

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
		this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		mTableLayout.setGravity(Gravity.TOP);
		mTableLayout.setOrientation(TableLayout.VERTICAL);
		mTableLayout.setStretchAllColumns(true);

		presetColors = getResources().obtainTypedArray(R.array.preset_colors);

		OnClickListener presetButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectedColor = presetColors.getColor(v.getId(), 0);
				onColorChanged();
			}
		};

		TableRow tr = new TableRow(context);
		for (int i = 0; i < presetColors.length(); i++) {
			Button btn = new Button(context);
			btn.setId(i);
			btn.setHeight(presetButtonHeight);
			btn.setBackgroundColor(presetColors.getColor(i, 0));
			btn.setOnClickListener(presetButtonListener);
			tr.addView(btn);
			if ((i + 1) % 4 == 0) {
				mTableLayout.addView(tr);
				tr = new TableRow(context);
			}
		}
		// finally add transparent button
		/*
		 * if (tr.getChildCount() == 4) { tr = new TableRow(context); } Button
		 * btn = new Button(context); btn.setId(presetColors.length() - 1);
		 * btn.setHeight(presetButtonHeight);
		 * btn.setBackgroundResource(R.drawable.checkeredbg_repeat);
		 * btn.setOnClickListener(presetButtonListener); tr.addView(btn);
		 * mTableLayout.addView(tr);
		 */

		this.addView(mTableLayout);
	}

	public int getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(int color) {
		if (color == this.selectedColor) {
			return;
		}
		this.selectedColor = color;
	}

	private void onColorChanged() {
		if (onColorChangedListener != null) {
			onColorChangedListener.colorChanged(getSelectedColor());
		}
	}

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		this.onColorChangedListener = listener;
	}

	public interface OnColorChangedListener {
		public void colorChanged(int color);
	}
}
