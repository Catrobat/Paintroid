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

package org.catrobat.paintroid.ui.tools;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.helper.DefaultNumberRangeFilter;
import org.catrobat.paintroid.tools.implementation.FillTool;
import org.catrobat.paintroid.tools.options.FillToolOptionsView;

import java.util.Locale;

public class DefaultFillToolOptionsView implements FillToolOptionsView {
	private SeekBar colorToleranceSeekBar;
	private EditText colorToleranceEditText;
	private Callback callback;

	public DefaultFillToolOptionsView(ViewGroup toolSpecificOptionsLayout) {
		LayoutInflater inflater = LayoutInflater.from(toolSpecificOptionsLayout.getContext());
		View fillToolOptionsView = inflater.inflate(R.layout.dialog_pocketpaint_fill_tool, toolSpecificOptionsLayout);

		colorToleranceSeekBar = fillToolOptionsView.findViewById(R.id.pocketpaint_color_tolerance_seek_bar);
		colorToleranceEditText = fillToolOptionsView.findViewById(R.id.pocketpaint_fill_tool_dialog_color_tolerance_input);
		colorToleranceEditText.setFilters(new InputFilter[]{new DefaultNumberRangeFilter(0, 100)});
		colorToleranceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					setColorToleranceText(progress);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		colorToleranceEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				try {
					int colorToleranceInPercent = Integer.parseInt(s.toString());
					colorToleranceSeekBar.setProgress(colorToleranceInPercent);
					updateColorTolerance(colorToleranceInPercent);
				} catch (NumberFormatException e) {
					Log.e("Error parsing tolerance", "result was null");
				}
			}
		});
		setColorToleranceText(FillTool.DEFAULT_TOLERANCE_IN_PERCENT);
	}

	private void updateColorTolerance(int colorTolerance) {
		if (callback != null) {
			callback.onColorToleranceChanged(colorTolerance);
		}
	}

	private void setColorToleranceText(int toleranceInPercent) {
		colorToleranceEditText.setText(String.format(Locale.getDefault(), "%d", toleranceInPercent));
	}

	@Override
	public void setCallback(Callback callback) {
		this.callback = callback;
	}
}
