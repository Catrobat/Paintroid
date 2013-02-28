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
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class RgbSelectorView extends LinearLayout {

	private SeekBar seekBarRed;
	private SeekBar seekBarGreen;
	private SeekBar seekBarBlue;
	private SeekBar seekBarAlpha;
	// private ImageView previewImageView;
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

		addView(rgbView, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// setPreviewImage();
				onColorChanged();
			}
		};

		seekBarRed = (SeekBar) rgbView.findViewById(R.id.color_rgb_seekbar_red);
		seekBarRed.setOnSeekBarChangeListener(listener);
		seekBarGreen = (SeekBar) rgbView
				.findViewById(R.id.color_rgb_seekbar_green);
		seekBarGreen.setOnSeekBarChangeListener(listener);
		seekBarBlue = (SeekBar) rgbView
				.findViewById(R.id.color_rgb_seekbar_blue);
		seekBarBlue.setOnSeekBarChangeListener(listener);
		seekBarAlpha = (SeekBar) rgbView
				.findViewById(R.id.color_rgb_seekbar_alpha);
		seekBarAlpha.setOnSeekBarChangeListener(listener);
		// previewImageView = (ImageView) rgbView
		// .findViewById(R.id.color_rgb_imgpreview);

		setSelectedColor(Color.BLACK);
	}

	// private void setPreviewImage() {
	// Bitmap preview = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
	// preview.setPixel(0, 0, getSelectedColor());
	//
	// previewImageView.setImageBitmap(preview);
	// }

	public int getSelectedColor() {
		return Color.argb(seekBarAlpha.getProgress(), seekBarRed.getProgress(),
				seekBarGreen.getProgress(), seekBarBlue.getProgress());
	}

	public void setSelectedColor(int color) {
		seekBarAlpha.setProgress(Color.alpha(color));
		seekBarRed.setProgress(Color.red(color));
		seekBarGreen.setProgress(Color.green(color));
		seekBarBlue.setProgress(Color.blue(color));
		// setPreviewImage();
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
