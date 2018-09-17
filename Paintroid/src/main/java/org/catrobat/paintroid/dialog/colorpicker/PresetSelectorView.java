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

package org.catrobat.paintroid.dialog.colorpicker;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.catrobat.paintroid.R;

import java.util.ArrayList;
import java.util.List;

public class PresetSelectorView extends LinearLayout implements PresetColorAdapter.Callback,
		ColorPickerContract.ColorPickerView {
	private ColorPickerContract.ColorPickerViewListener onColorChangedListener;

	public PresetSelectorView(Context context) {
		super(context);
		init();
	}

	public PresetSelectorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		inflate(getContext(), R.layout.color_chooser_layout_presetview, this);

		int[] colors = getResources().getIntArray(R.array.pocketpaint_color_chooser_preset_colors);
		PresetColorAdapter adapter = new PresetColorAdapter(this, arrayToIntList(colors));

		RecyclerView recyclerView = findViewById(R.id.color_chooser_preset_recycler);
		recyclerView.setHasFixedSize(true);
		recyclerView.setNestedScrollingEnabled(false);
		recyclerView.setAdapter(adapter);
	}

	private static List<Integer> arrayToIntList(int[] array) {
		List<Integer> list = new ArrayList<>(array.length);
		for (int color : array) {
			list.add(color);
		}
		return list;
	}

	@Override
	public void onColorClicked(@ColorInt int color) {
		if (onColorChangedListener != null) {
			onColorChangedListener.colorChanged(color);
		}
	}

	@Override
	public void setOnColorChangedListener(ColorPickerContract.ColorPickerViewListener listener) {
		onColorChangedListener = listener;
	}

	@Override
	public void setSelectedColor(int color) {
	}
}
