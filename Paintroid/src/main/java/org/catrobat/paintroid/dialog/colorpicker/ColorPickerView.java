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

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;

import org.catrobat.paintroid.R;

public class ColorPickerView extends LinearLayoutCompat {

	private final String rgbTag = getContext().getString(R.string.color_rgb);
	private final String preTag = getContext().getString(R.string.color_pre);
	private final String circleTag = getContext().getString(R.string.color_hsv);

	private RgbSelectorView rgbSelectorView;
	private PresetSelectorView preSelectorView;
	private HSVSelectorView hsvSelectorView;
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

	private static View createTabView(final Context context, final int iconResourceId) {
		View tabView = inflate(context, R.layout.tab_image_only, null);
		ImageView tabIcon = (ImageView) tabView.findViewById(R.id.tab_icon);
		tabIcon.setBackgroundResource(iconResourceId);
		return tabView;
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
		if (sender != hsvSelectorView) {
			hsvSelectorView.setSelectedColor(color);
		}
		onColorChanged();
	}

	public int getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(int color) {
		setSelectedColor(color, null);
	}

	private void init() {
		View tabView = inflate(getContext(), R.layout.colorpicker_colorselectview, null);
		addView(tabView);
		rgbSelectorView = new RgbSelectorView(getContext());
		preSelectorView = new PresetSelectorView(getContext());
		hsvSelectorView = new HSVSelectorView(getContext());

		tabHost = (TabHost) tabView.findViewById(R.id.colorview_tabColors);
		tabHost.setup();
		ColorTabContentFactory factory = new ColorTabContentFactory();

		View preTabView = createTabView(getContext(),
				R.drawable.icon_color_chooser_tab_palette);
		TabSpec preTab = tabHost.newTabSpec(preTag)
				.setIndicator(preTabView)
				.setContent(factory);

		View hsvTabView = createTabView(getContext(),
				R.drawable.icon_color_chooser_tab_circle);
		TabSpec hsvTab = tabHost.newTabSpec(circleTag)
				.setIndicator(hsvTabView)
				.setContent(factory);

		View rgbTabView = createTabView(getContext(),
				R.drawable.icon_color_chooser_tab_rgba);
		TabSpec rgbTab = tabHost.newTabSpec(rgbTag).setIndicator(rgbTabView)
				.setContent(factory);
		tabHost.addTab(preTab);
		tabHost.addTab(hsvTab);
		tabHost.addTab(rgbTab);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		preSelectorView
				.setOnColorChangedListener(new PresetSelectorView.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						setSelectedColor(color, preSelectorView);
					}
				});
		hsvSelectorView.getHsvColorPickerView().setOnColorChangedListener(
				new HSVColorPickerView.OnColorChangedListener() {

					@Override
					public void colorChanged(int color) {
						setSelectedColor(color, hsvSelectorView);
					}
				});
		rgbSelectorView
				.setOnColorChangedListener(new RgbSelectorView.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						setSelectedColor(color, rgbSelectorView);
					}
				});
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		preSelectorView.setOnColorChangedListener(null);
		hsvSelectorView.getHsvColorPickerView().setOnColorChangedListener(null);
		rgbSelectorView.setOnColorChangedListener(null);
	}

	private void onColorChanged() {
		if (listener != null) {
			listener.colorChanged(getSelectedColor());
		}
	}

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (preTag.equals(tabHost.getCurrentTabTag())) {
			maxViewHeight = getMeasuredHeight();
			maxViewWidth = getMeasuredWidth();
		} else if (rgbTag.equals(tabHost.getCurrentTabTag())) {
			maxViewHeight = getMeasuredHeight();
			maxViewWidth = getMeasuredWidth();
		} else if (circleTag.equals(tabHost.getCurrentTabTag())) {
			maxViewHeight = getMeasuredHeight();
			maxViewWidth = getMeasuredWidth();
		}
		setMeasuredDimension(maxViewWidth, maxViewHeight);
	}

	public interface OnColorChangedListener {
		void colorChanged(int color);
	}

	class ColorTabContentFactory implements TabContentFactory {
		@Override
		public View createTabContent(String tag) {

			if (rgbTag.equals(tag)) {
				return rgbSelectorView;
			}
			if (preTag.equals(tag)) {
				return preSelectorView;
			}
			if (circleTag.equals(tag)) {
				return hsvSelectorView;
			}
			return null;
		}
	}
}
