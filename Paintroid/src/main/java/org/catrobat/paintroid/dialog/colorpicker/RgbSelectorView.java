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

package org.catrobat.paintroid.dialog.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.catrobat.paintroid.R;

import java.util.Locale;

public class RgbSelectorView extends LinearLayout {

	private SeekBar seekBarRed;
	private SeekBar seekBarGreen;
	private SeekBar seekBarBlue;
	private SeekBar seekBarAlpha;
	private TextView textViewRed;
	private TextView textViewGreen;
	private TextView textViewBlue;
	private TextView textViewAlpha;
	private OnColorChangedListener onColorChangedListener;

	public RgbSelectorView(Context context) {
		super(context);
		init();
	}

	public RgbSelectorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rgbView = inflater.inflate(R.layout.colorpicker_rgbview, null);

		addView(rgbView);

		SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				onColorChanged();
			}
		};

		seekBarRed = (SeekBar) rgbView
				.findViewById(R.id.color_rgb_seekbar_red);
		seekBarRed.setOnSeekBarChangeListener(seekBarListener);
		seekBarGreen = (SeekBar) rgbView
				.findViewById(R.id.color_rgb_seekbar_green);
		seekBarGreen.setOnSeekBarChangeListener(seekBarListener);
		seekBarBlue = (SeekBar) rgbView
				.findViewById(R.id.color_rgb_seekbar_blue);
		seekBarBlue.setOnSeekBarChangeListener(seekBarListener);
		seekBarAlpha = (SeekBar) rgbView
				.findViewById(R.id.color_rgb_seekbar_alpha);
		seekBarAlpha.setOnSeekBarChangeListener(seekBarListener);

		textViewRed = (TextView) rgbView.findViewById(R.id.rgb_red_value);
		textViewGreen = (TextView) rgbView.findViewById(R.id.rgb_green_value);
		textViewBlue = (TextView) rgbView.findViewById(R.id.rgb_blue_value);
		textViewAlpha = (TextView) rgbView.findViewById(R.id.rgb_alpha_value);

		setSelectedColor(Color.BLACK);
	}

	public int getSelectedColor() {
		return Color.argb(seekBarAlpha.getProgress(),
				seekBarRed.getProgress(), seekBarGreen.getProgress(),
				seekBarBlue.getProgress());
	}

	public void setSelectedColor(int color) {
		int colorAlpha = Color.alpha(color);
		int colorRed = Color.red(color);
		int colorGreen = Color.green(color);
		int colorBlue = Color.blue(color);
		seekBarAlpha.setProgress(colorAlpha);
		seekBarRed.setProgress(colorRed);
		seekBarGreen.setProgress(colorGreen);
		seekBarBlue.setProgress(colorBlue);
		textViewRed.setText(String.format(Locale.getDefault(), "%d", colorRed));
		textViewGreen.setText(String.format(Locale.getDefault(), "%d", colorGreen));
		textViewBlue.setText(String.format(Locale.getDefault(), "%d", colorBlue));
		Integer alphaToPercent = (int) (colorAlpha / 2.55f);
		textViewAlpha.setText(String.format(Locale.getDefault(), "%d", alphaToPercent));
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
		void colorChanged(int color);
	}
}
