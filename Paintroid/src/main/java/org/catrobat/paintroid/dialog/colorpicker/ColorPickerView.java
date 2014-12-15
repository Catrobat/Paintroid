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

	private RgbSelectorView mRGBSelectorView;
	private PresetSelectorView mPreSelectorView;
	private TabHost mTabHost;

	private int maxViewWidth = 0;
	private int maxViewHeight = 0;

	private int mSelectedColor;

	private OnColorChangedListener mListener;

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
		if (this.mSelectedColor == color) {
			return;
		}
		this.mSelectedColor = color;
		if (sender != mRGBSelectorView) {
			mRGBSelectorView.setSelectedColor(color);
		}
		if (sender != mPreSelectorView) {
			mPreSelectorView.setSelectedColor(color);
		}
		onColorChanged();
	}

	public int getSelectedColor() {
		return mSelectedColor;
	}

	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View tabView = inflater.inflate(R.layout.colorpicker_colorselectview,
				null);
		addView(tabView);
		mRGBSelectorView = new RgbSelectorView(getContext());
		mRGBSelectorView
				.setOnColorChangedListener(new RgbSelectorView.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						setSelectedColor(color);
					}
				});
		mPreSelectorView = new PresetSelectorView(getContext());
		mPreSelectorView
				.setOnColorChangedListener(new PresetSelectorView.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						setSelectedColor(color);
					}
				});

		mTabHost = (TabHost) tabView.findViewById(R.id.colorview_tabColors);
		mTabHost.setup();
		ColorTabContentFactory factory = new ColorTabContentFactory();

		View preTabView = createTabView(getContext(),
				R.drawable.icon_color_chooser_tab_palette);
		TabSpec preTab = mTabHost.newTabSpec(PRE_TAG).setIndicator(preTabView)
				.setContent(factory);

		View rgbTabView = createTabView(getContext(),
				R.drawable.icon_color_chooser_tab_rgba);
		TabSpec rgbTab = mTabHost.newTabSpec(RGB_TAG).setIndicator(rgbTabView)
				.setContent(factory);
		mTabHost.addTab(preTab);
		mTabHost.addTab(rgbTab);
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
				return mRGBSelectorView;
			}
			if (PRE_TAG.equals(tag)) {
				return mPreSelectorView;
			}
			return null;
		}
	}

	private void onColorChanged() {
		if (mListener != null) {
			mListener.colorChanged(getSelectedColor());
		}
	}

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		this.mListener = listener;
	}

	public interface OnColorChangedListener {
		public void colorChanged(int color);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (PRE_TAG.equals(mTabHost.getCurrentTabTag())) {
			maxViewHeight = getMeasuredHeight();
			maxViewWidth = getMeasuredWidth();

		} else if (RGB_TAG.equals(mTabHost.getCurrentTabTag())) {
			maxViewHeight = getMeasuredHeight();
			maxViewWidth = getMeasuredWidth();
		}
		setMeasuredDimension(maxViewWidth, maxViewHeight);
	}
}
