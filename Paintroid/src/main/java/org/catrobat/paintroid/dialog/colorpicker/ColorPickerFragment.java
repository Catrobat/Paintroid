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

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ColorPickerFragment extends Fragment implements ColorPickerContract.ColorPickerFragment,
		ColorPickerContract.ColorPickerViewListener {

	public static final String COLOR_PICKER_FRAGMENT_LAYOUT = "COLOR_PICKER_FRAGMENT_LAYOUT";
	private ColorPickerContract.ColorPickerView colorPickerView;

	@LayoutRes
	private int fragmentLayout;
	private int selectedColor;
	private ColorPickerContract.ColorPickerParentFragment dialog;

	public ColorPickerFragment() {
	}

	public static ColorPickerFragment newInstance(@LayoutRes int layout) {
		Bundle args = new Bundle();
		args.putInt(COLOR_PICKER_FRAGMENT_LAYOUT, layout);

		ColorPickerFragment fragment = new ColorPickerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = (ColorPickerContract.ColorPickerParentFragment) getParentFragment();
		if (dialog == null) {
			throw new IllegalArgumentException(ColorPickerFragment.class.getSimpleName()
					+ " must have a ColorPickerDialog parent fragment");
		}

		dialog.registerColorPickerFragment(this);

		Bundle arguments = getArguments();
		if (arguments != null) {
			fragmentLayout = arguments.getInt(COLOR_PICKER_FRAGMENT_LAYOUT);
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		selectedColor = dialog.getCurrentColor();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dialog.unregisterColorPickerFragment(this);
		dialog = null;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(fragmentLayout, container, false);
		colorPickerView = (ColorPickerContract.ColorPickerView) view;
		return view;
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		colorPickerView.setSelectedColor(selectedColor);
		colorPickerView.setOnColorChangedListener(this);
	}

	@Override
	public void setColor(int color) {
		selectedColor = color;
		if (colorPickerView != null) {
			colorPickerView.setSelectedColor(color);
		}
	}

	@Override
	public void colorChanged(int color) {
		dialog.colorChanged(this, color);
	}
}
