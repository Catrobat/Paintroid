/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *   
 *   Foobar is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *   
 *   You should have received a copy of the GNU Affero General Public License
 *   along with Paintroid.  If not, see <http://www.gnu.org/licenses/>.
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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import at.tugraz.ist.paintroid.R;

public class ColorPickerView extends LinearLayout {

	private final String HSV_TAG;
	private final String RGB_TAG;
	private final String PRE_TAG;

	private HsvSelectorView hsvSelectorView;
	private RgbSelectorView rgbSelectorView;
	private PresetSelectorView preSelectorView;
	private TabHost tabHost;

	private int maxViewWidth = 0;
	private int maxViewHeight = 0;

	private int selectedColor;

	private OnColorChangedListener listener;

	public ColorPickerView(Context context) {
		super(context);
		HSV_TAG = context.getResources().getString(R.string.color_hsv);
		RGB_TAG = context.getResources().getString(R.string.color_rgb);
		PRE_TAG = context.getResources().getString(R.string.color_pre);
		init();
	}

	public ColorPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		HSV_TAG = context.getResources().getString(R.string.color_hsv);
		RGB_TAG = context.getResources().getString(R.string.color_rgb);
		PRE_TAG = context.getResources().getString(R.string.color_pre);
		init();
	}

	public void setSelectedColor(int color) {
		setSelectedColor(color, null);
	}

	private void setSelectedColor(int color, View sender) {
		if (this.selectedColor == color) {
			return;
		}
		this.selectedColor = color;
		if (sender != hsvSelectorView) {
			hsvSelectorView.setSelectedColor(color);
		}
		if (sender != rgbSelectorView) {
			rgbSelectorView.setSelectedColor(color);
		}
		if (sender != preSelectorView) {
			preSelectorView.setSelectedColor(color);
		}
		onColorChanged();
	}

	public int getSelectedColor() {
		return selectedColor;
	}

	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View contentView = inflater.inflate(R.layout.colorpicker_colorselectview, null);

		addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		hsvSelectorView = new HsvSelectorView(getContext());
		hsvSelectorView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		hsvSelectorView.setOnColorChangedListener(new HsvSelectorView.OnColorChangedListener() {
			@Override
			public void colorChanged(int color) {
				setSelectedColor(color);
			}
		});
		rgbSelectorView = new RgbSelectorView(getContext());
		rgbSelectorView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		rgbSelectorView.setOnColorChangedListener(new RgbSelectorView.OnColorChangedListener() {
			@Override
			public void colorChanged(int color) {
				setSelectedColor(color);
			}
		});
		preSelectorView = new PresetSelectorView(getContext());
		preSelectorView.setOnColorChangedListener(new PresetSelectorView.OnColorChangedListener() {
			@Override
			public void colorChanged(int color) {
				setSelectedColor(color);
			}
		});

		tabHost = (TabHost) contentView.findViewById(R.id.colorview_tabColors);
		tabHost.setup();
		ColorTabContentFactory factory = new ColorTabContentFactory();
		TabSpec hsvTab = tabHost.newTabSpec(HSV_TAG)
				.setIndicator(HSV_TAG, getContext().getResources().getDrawable(R.drawable.ic_cp_hsv32))
				.setContent(factory);
		TabSpec rgbTab = tabHost.newTabSpec(RGB_TAG)
				.setIndicator(RGB_TAG, getContext().getResources().getDrawable(R.drawable.ic_cp_rgb32))
				.setContent(factory);
		TabSpec preTab = tabHost.newTabSpec(PRE_TAG)
				.setIndicator(PRE_TAG, getContext().getResources().getDrawable(R.drawable.ic_cp_preset32))
				.setContent(factory);
		tabHost.addTab(hsvTab);
		tabHost.addTab(rgbTab);
		tabHost.addTab(preTab);
	}

	class ColorTabContentFactory implements TabContentFactory {
		@Override
		public View createTabContent(String tag) {
			if (HSV_TAG.equals(tag)) {
				return hsvSelectorView;
			}
			if (RGB_TAG.equals(tag)) {
				return rgbSelectorView;
			}
			if (PRE_TAG.equals(tag)) {
				return preSelectorView;
			}
			return null;
		}
	}

	private void onColorChanged() {
		if (listener != null) {
			listener.colorChanged(getSelectedColor());
		}
	}

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		this.listener = listener;
	}

	public interface OnColorChangedListener {
		public void colorChanged(int color);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (HSV_TAG.equals(tabHost.getCurrentTabTag())) {
			maxViewHeight = getMeasuredHeight();
			maxViewWidth = getMeasuredWidth();
		}
		setMeasuredDimension(maxViewWidth, maxViewHeight);
	}
}
