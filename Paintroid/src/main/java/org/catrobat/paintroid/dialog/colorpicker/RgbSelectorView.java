/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
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
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class RgbSelectorView extends LinearLayout {

	private SeekBar mSeekBarRed;
	private SeekBar mSeekBarGreen;
	private SeekBar mSeekBarBlue;
	private SeekBar mSeekBarAlpha;
	private TextView mTextViewRed;
	private TextView mTextViewGreen;
	private TextView mTextViewBlue;
	private TextView mTextViewAlpha;
	private OnColorChangedListener mOnColorChangedListener;

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

		mSeekBarRed = (SeekBar) rgbView
				.findViewById(R.id.color_rgb_seekbar_red);
		mSeekBarRed.setOnSeekBarChangeListener(seekBarListener);
		mSeekBarGreen = (SeekBar) rgbView
				.findViewById(R.id.color_rgb_seekbar_green);
		mSeekBarGreen.setOnSeekBarChangeListener(seekBarListener);
		mSeekBarBlue = (SeekBar) rgbView
				.findViewById(R.id.color_rgb_seekbar_blue);
		mSeekBarBlue.setOnSeekBarChangeListener(seekBarListener);
		mSeekBarAlpha = (SeekBar) rgbView
				.findViewById(R.id.color_rgb_seekbar_alpha);
		mSeekBarAlpha.setOnSeekBarChangeListener(seekBarListener);

		mTextViewRed = (TextView) rgbView.findViewById(R.id.rgb_red_value);
		mTextViewGreen = (TextView) rgbView.findViewById(R.id.rgb_green_value);
		mTextViewBlue = (TextView) rgbView.findViewById(R.id.rgb_blue_value);
		mTextViewAlpha = (TextView) rgbView.findViewById(R.id.rgb_alpha_value);

		setSelectedColor(Color.BLACK);
	}

	public int getSelectedColor() {
		return Color.argb(mSeekBarAlpha.getProgress(),
				mSeekBarRed.getProgress(), mSeekBarGreen.getProgress(),
				mSeekBarBlue.getProgress());
	}

	public void setSelectedColor(int color) {
		int colorAlpha = Color.alpha(color);
		int colorRed = Color.red(color);
		int colorGreen = Color.green(color);
		int colorBlue = Color.blue(color);
		mSeekBarAlpha.setProgress(colorAlpha);
		mSeekBarRed.setProgress(colorRed);
		mSeekBarGreen.setProgress(colorGreen);
		mSeekBarBlue.setProgress(colorBlue);
		mTextViewRed.setText(Integer.toString(colorRed));
		mTextViewGreen.setText(Integer.toString(colorGreen));
		mTextViewBlue.setText(Integer.toString(colorBlue));
		Integer alphaToPercent = (int) (colorAlpha / 2.55f);
		mTextViewAlpha.setText(alphaToPercent.toString());

	}

	private void onColorChanged() {
		if (mOnColorChangedListener != null) {
			mOnColorChangedListener.colorChanged(getSelectedColor());
		}
	}

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		this.mOnColorChangedListener = listener;
	}

	public interface OnColorChangedListener {
		public void colorChanged(int color);
	}
}
