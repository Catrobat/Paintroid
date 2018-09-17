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
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.catrobat.paintroid.R;

import java.util.Locale;

public class RgbSelectorView extends LinearLayout implements ColorPickerContract.ColorPickerView {
	private SeekBar seekBarRed;
	private SeekBar seekBarGreen;
	private SeekBar seekBarBlue;
	private SeekBar seekBarAlpha;
	private TextView textViewRed;
	private TextView textViewGreen;
	private TextView textViewBlue;
	private TextView textViewAlpha;

	private ColorPickerContract.ColorPickerViewListener onColorChangedListener;

	public RgbSelectorView(Context context) {
		super(context);
		init();
	}

	public RgbSelectorView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RgbSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		inflate(getContext(), R.layout.color_chooser_layout_rgbview, this);

		seekBarRed = findViewById(R.id.color_chooser_color_rgb_seekbar_red);
		seekBarGreen = findViewById(R.id.color_chooser_color_rgb_seekbar_green);
		seekBarBlue = findViewById(R.id.color_chooser_color_rgb_seekbar_blue);
		seekBarAlpha = findViewById(R.id.color_chooser_color_rgb_seekbar_alpha);

		textViewRed = findViewById(R.id.color_chooser_rgb_red_value);
		textViewGreen = findViewById(R.id.color_chooser_rgb_green_value);
		textViewBlue = findViewById(R.id.color_chooser_rgb_blue_value);
		textViewAlpha = findViewById(R.id.color_chooser_rgb_alpha_value);

		SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					int color = getSelectedColor();
					setSelectedColorText(color);
					onColorChanged(color);
				}
			}
		};

		setSelectedColor(Color.BLACK);

		seekBarRed.setOnSeekBarChangeListener(seekBarListener);
		seekBarGreen.setOnSeekBarChangeListener(seekBarListener);
		seekBarBlue.setOnSeekBarChangeListener(seekBarListener);
		seekBarAlpha.setOnSeekBarChangeListener(seekBarListener);
	}

	private int getSelectedColor() {
		return Color.argb(seekBarAlpha.getProgress(), seekBarRed.getProgress(), seekBarGreen.getProgress(),
				seekBarBlue.getProgress());
	}

	@Override
	public void setSelectedColor(int color) {
		int colorRed = Color.red(color);
		int colorGreen = Color.green(color);
		int colorBlue = Color.blue(color);
		int colorAlpha = Color.alpha(color);

		seekBarAlpha.setProgress(colorAlpha);
		seekBarRed.setProgress(colorRed);
		seekBarGreen.setProgress(colorGreen);
		seekBarBlue.setProgress(colorBlue);

		setSelectedColorText(color);
	}

	private void setSelectedColorText(int color) {
		int colorRed = Color.red(color);
		int colorGreen = Color.green(color);
		int colorBlue = Color.blue(color);
		int alphaToPercent = (int) (Color.alpha(color) / 2.55f);

		textViewRed.setText(String.format(Locale.getDefault(), "%d", colorRed));
		textViewGreen.setText(String.format(Locale.getDefault(), "%d", colorGreen));
		textViewBlue.setText(String.format(Locale.getDefault(), "%d", colorBlue));
		textViewAlpha.setText(String.format(Locale.getDefault(), "%d%%", alphaToPercent));
	}

	private void onColorChanged(int color) {
		if (onColorChangedListener != null) {
			onColorChangedListener.colorChanged(color);
		}
	}

	@Override
	public void setOnColorChangedListener(ColorPickerContract.ColorPickerViewListener listener) {
		this.onColorChangedListener = listener;
	}
}
