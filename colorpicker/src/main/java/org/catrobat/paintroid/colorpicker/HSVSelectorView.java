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
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class HSVSelectorView extends LinearLayout {

	private HSVColorPickerView hsvColorPickerView;

	public HSVSelectorView(Context context) {
		super(context);
		init();
	}

	public HSVSelectorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		hsvColorPickerView = new HSVColorPickerView(getContext());
		hsvColorPickerView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		addView(hsvColorPickerView);
	}

	public HSVColorPickerView getHsvColorPickerView() {
		return hsvColorPickerView;
	}

	public void setSelectedColor(int color) {
		hsvColorPickerView.setSelectedColor(color);
	}
}
