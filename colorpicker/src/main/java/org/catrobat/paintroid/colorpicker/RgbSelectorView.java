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
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import static org.catrobat.paintroid.colorpicker.Constants.NOT_A_HEX_VALUE;

public class RgbSelectorView extends LinearLayout {

	private SeekBar seekBarRed;
	private SeekBar seekBarGreen;
	private SeekBar seekBarBlue;
	private SeekBar seekBarAlpha;
	private TextView textViewRed;
	private TextView textViewGreen;
	private TextView textViewBlue;
	private TextView textViewAlpha;
	private EditText editTextHex;
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
		View rgbView = inflate(getContext(), R.layout.color_picker_layout_rgbview, null);

		addView(rgbView);

		seekBarRed = (SeekBar) rgbView.findViewById(R.id.color_picker_color_rgb_seekbar_red);
		seekBarGreen = (SeekBar) rgbView.findViewById(R.id.color_picker_color_rgb_seekbar_green);
		seekBarBlue = (SeekBar) rgbView.findViewById(R.id.color_picker_color_rgb_seekbar_blue);
		seekBarAlpha = (SeekBar) rgbView.findViewById(R.id.color_picker_color_rgb_seekbar_alpha);

		textViewRed = (TextView) rgbView.findViewById(R.id.color_picker_rgb_red_value);
		textViewGreen = (TextView) rgbView.findViewById(R.id.color_picker_rgb_green_value);
		textViewBlue = (TextView) rgbView.findViewById(R.id.color_picker_rgb_blue_value);
		textViewAlpha = (TextView) rgbView.findViewById(R.id.color_picker_rgb_alpha_value);

		editTextHex = (EditText) rgbView.findViewById(R.id.color_picker_color_rgb_hex);
		resetTextColor();

		setSelectedColor(Color.BLACK);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int color = getSelectedColor();
				setSelectedColor(color);
				if (fromUser) {
					onColorChanged(color);
				}
			}
		};

		seekBarRed.setOnSeekBarChangeListener(seekBarListener);
		seekBarGreen.setOnSeekBarChangeListener(seekBarListener);
		seekBarBlue.setOnSeekBarChangeListener(seekBarListener);
		seekBarAlpha.setOnSeekBarChangeListener(seekBarListener);

		editTextHex.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }

			@Override
			public void afterTextChanged(Editable newText) {
				if (editTextHex.getTag() == null) {
					int color = parseInputToCheckIfHEX(newText.toString());
					if (color != NOT_A_HEX_VALUE) {
						setSelectedColor(color);
						onColorChanged(color);
						resetTextColor();
					} else {
						setTextColorToRed();
					}
				} else {
					resetTextColor();
				}
			}
		});
	}

	private int parseInputToCheckIfHEX(String newText) {
		if (newText.length() != 9 || !newText.substring(0, 1).equals("#")) {
			return NOT_A_HEX_VALUE;
		}

		try {
			return Color.parseColor(newText);
		} catch (IllegalArgumentException e) {
			return NOT_A_HEX_VALUE;
		}
	}

	private void resetTextColor() {
		editTextHex.setTextColor(getResources().getColor(R.color.pocketpaint_color_picker_hex_correct_black));
	}

	private void setTextColorToRed() {
		editTextHex.setTextColor(getResources().getColor(R.color.pocketpaint_color_picker_hex_wrong_value_red));
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		seekBarRed.setOnSeekBarChangeListener(null);
		seekBarGreen.setOnSeekBarChangeListener(null);
		seekBarBlue.setOnSeekBarChangeListener(null);
		seekBarAlpha.setOnSeekBarChangeListener(null);
	}

	public int getSelectedColor() {
		return Color.argb(seekBarAlpha.getProgress(),
				seekBarRed.getProgress(), seekBarGreen.getProgress(),
				seekBarBlue.getProgress());
	}

	public void setSelectedColor(int color) {
		int colorRed = Color.red(color);
		int colorGreen = Color.green(color);
		int colorBlue = Color.blue(color);
		int colorAlpha = Color.alpha(color);

		seekBarAlpha.setProgress(colorAlpha);
		seekBarRed.setProgress(colorRed);
		seekBarGreen.setProgress(colorGreen);
		seekBarBlue.setProgress(colorBlue);

		int currentCursorPosition = editTextHex.getSelectionStart();
		editTextHex.setTag("changed programmatically");
		editTextHex.setText(String.format("#%02X%02X%02X%02X", colorAlpha, colorRed, colorGreen, colorBlue));
		editTextHex.setTag(null);

		int editTextHexLength = editTextHex.getText().length();
		if(currentCursorPosition > editTextHexLength) {
			currentCursorPosition = editTextHexLength;
		}

		editTextHex.setSelection(currentCursorPosition);

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
		textViewAlpha.setText(String.format(Locale.getDefault(), "%d", alphaToPercent));
	}

	private void onColorChanged(int color) {
		if (onColorChangedListener != null) {
			onColorChangedListener.colorChanged(color);
		}
	}

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		this.onColorChangedListener = listener;
	}

	public interface OnColorChangedListener {
		void colorChanged(int color);
	}
}
