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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;

public class ColorPickerView extends LinearLayout {

	private final String RGB_TAG = getContext().getString(R.string.color_rgb);
	private final String PRE_TAG = getContext().getString(R.string.color_pre);

	private RgbSelectorView rgbSelectorView;
	private PresetSelectorView preSelectorView;
	private TabHost tabHost;

	private int maxViewWidth = 0;
	private int maxViewHeight = 0;

	private int selectedColor;

	private OnColorChangedListener listener;

	public ColorPickerView(Context context) {
		super(context);
		init();
	}

	public ColorPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
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
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View tabView = inflater.inflate(R.layout.colorpicker_colorselectview,
				null);
		addView(tabView);
		rgbSelectorView = new RgbSelectorView(getContext());
		rgbSelectorView
				.setOnColorChangedListener(new RgbSelectorView.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						setSelectedColor(color);
					}
				});
		preSelectorView = new PresetSelectorView(getContext());
		preSelectorView
				.setOnColorChangedListener(new PresetSelectorView.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						setSelectedColor(color);
					}
				});

		tabHost = (TabHost) tabView.findViewById(R.id.colorview_tabColors);
		tabHost.setup();
		ColorTabContentFactory factory = new ColorTabContentFactory();

		View preTabView = createTabView(getContext(), R.drawable.ic_cp_preset32);
		TabSpec preTab = tabHost.newTabSpec(PRE_TAG).setIndicator(preTabView)
				.setContent(factory);

		View rgbTabView = createTabView(getContext(),
				R.drawable.icon_action_settings);
		TabSpec rgbTab = tabHost.newTabSpec(RGB_TAG).setIndicator(rgbTabView)
				.setContent(factory);
		tabHost.addTab(preTab);
		tabHost.addTab(rgbTab);
	}

	private static View createTabView(final Context context,
			final int iconResourceId) {
		View tabView = LayoutInflater.from(context).inflate(
				R.layout.tab_image_only, null);
		ImageView tabIcon = (ImageView) tabView.findViewById(R.id.tab_icon);
		tabIcon.setBackgroundResource(iconResourceId);
		return tabView;
	}

	class ColorTabContentFactory implements TabContentFactory {
		@Override
		public View createTabContent(String tag) {

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
		if (PRE_TAG.equals(tabHost.getCurrentTabTag())) {
			maxViewHeight = getMeasuredHeight();
			maxViewWidth = getMeasuredWidth();

		} else if (RGB_TAG.equals(tabHost.getCurrentTabTag())) {
			maxViewHeight = getMeasuredHeight();
			maxViewWidth = getMeasuredWidth();
		}
		setMeasuredDimension(maxViewWidth, maxViewHeight);
	}
}
