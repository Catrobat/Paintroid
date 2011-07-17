/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *    
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

package at.tugraz.ist.paintroid.dialog.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import at.tugraz.ist.paintroid.R;

public class HsvSelectorView extends LinearLayout {

	private HsvAlphaSelectorView alphaSelectorView;
	private HsvSaturationSelectorView saturationSelectorView;
	private HsvHueSelectorView hueSelectorView;

	private int selectedColor;

	private OnColorChangedListener onColorChangedListener;

	public HsvSelectorView(Context context) {
		super(context);
		init();
	}

	public HsvSelectorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		buildUI();
	}

	private void buildUI() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View hsvView = inflater.inflate(R.layout.colorpicker_hsvview, null);
		this.addView(hsvView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		alphaSelectorView = (HsvAlphaSelectorView) hsvView.findViewById(R.id.color_hsv_alpha);
		saturationSelectorView = (HsvSaturationSelectorView) hsvView.findViewById(R.id.color_hsv_value);
		hueSelectorView = (HsvHueSelectorView) hsvView.findViewById(R.id.color_hsv_hue);

		alphaSelectorView.setOnAlphaChangedListener(new HsvAlphaSelectorView.OnAlphaChangedListener() {
			@Override
			public void alphaChanged(HsvAlphaSelectorView sender, int alpha) {
				updateSelectedColor(getCurrentColorFromHSV(true), true);
			}
		});

		saturationSelectorView
				.setOnSaturationOrValueChanged(new HsvSaturationSelectorView.OnSaturationOrValueChanged() {
					@Override
					public void saturationOrValueChanged(HsvSaturationSelectorView sender, float saturation,
							float value, boolean up) {
						alphaSelectorView.setColor(getCurrentColorFromHSV(false));
						updateSelectedColor(getCurrentColorFromHSV(true), up);
					}
				});
		hueSelectorView.setOnHueChangedListener(new HsvHueSelectorView.OnHueChangedListener() {
			@Override
			public void hueChanged(HsvHueSelectorView sender, float hue) {
				saturationSelectorView.setHue(hue);
				alphaSelectorView.setColor(getCurrentColorFromHSV(false));
				updateSelectedColor(getCurrentColorFromHSV(true), true);
			}
		});
		setSelectedColor(Color.BLACK);
	}

	private int getCurrentColorFromHSV(boolean includeAlpha) {
		float[] hsv = new float[3];
		hsv[0] = hueSelectorView.getHue();
		hsv[1] = saturationSelectorView.getSaturation();
		hsv[2] = saturationSelectorView.getValue();
		int alpha = includeAlpha ? alphaSelectorView.getAlpha() : 255;
		return Color.HSVToColor(alpha, hsv);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		LayoutParams paramsAlpha = new LayoutParams(alphaSelectorView.getLayoutParams());
		LayoutParams paramsHue = new LayoutParams(hueSelectorView.getLayoutParams());

		paramsAlpha.height = saturationSelectorView.getHeight();
		paramsHue.height = saturationSelectorView.getHeight();

		hueSelectorView.setMinContentOffset(saturationSelectorView.getBackgroundOffset());
		alphaSelectorView.setMinContentOffset(saturationSelectorView.getBackgroundOffset());

		alphaSelectorView.setLayoutParams(paramsAlpha);
		hueSelectorView.setLayoutParams(paramsHue);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public int getSelectedColor() {
		return selectedColor;
	}

	private void updateSelectedColor(int color, boolean fire) {
		this.selectedColor = color;
		if (fire) {
			onColorChanged();
		}
	}

	public void setSelectedColor(int color) {
		int alpha = Color.alpha(color);
		alphaSelectorView.setAlpha(alpha);
		int colorWithoutAlpha = color | 0xFF000000;
		float[] hsv = new float[3];
		Color.colorToHSV(colorWithoutAlpha, hsv);
		hueSelectorView.setHue(hsv[0]);
		saturationSelectorView.setHue(hsv[0]);
		saturationSelectorView.setSaturation(hsv[1]);
		saturationSelectorView.setValue(hsv[2]);
		alphaSelectorView.setColor(color);
		updateSelectedColor(color, this.selectedColor != color);
	}

	private void onColorChanged() {
		if (onColorChangedListener != null) {
			onColorChangedListener.colorChanged(selectedColor);
		}
	}

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		this.onColorChangedListener = listener;
	}

	public interface OnColorChangedListener {
		public void colorChanged(int color);
	}
}
