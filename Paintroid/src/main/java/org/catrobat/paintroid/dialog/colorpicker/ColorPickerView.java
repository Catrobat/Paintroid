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
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;

import org.catrobat.paintroid.R;

public class ColorPickerView extends LinearLayoutCompat {
	private static final String RGB_TAG = "RGB";
	private static final String PRE_TAG = "PRE";
	private static final String HSV_TAG = "HSV";

	private RgbSelectorView rgbSelectorView;
	private PresetSelectorView preSelectorView;
	private HSVSelectorView hsvSelectorView;
	private TabHost tabHost;

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

	private static View createTabView(Context context, int iconResourceId) {
		View tabView = inflate(context, R.layout.color_chooser_tab_image_only, null);
		ImageView tabIcon = tabView.findViewById(R.id.color_chooser_tab_icon);
		tabIcon.setBackgroundResource(iconResourceId);
		return tabView;
	}

	@Nullable
	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		return new SavedState(superState, tabHost.getCurrentTabTag());
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof SavedState) {
			SavedState savedState = (SavedState) state;
			super.onRestoreInstanceState(savedState.getSuperState());
			tabHost.setCurrentTabByTag(savedState.currentTabTag);
		} else {
			super.onRestoreInstanceState(state);
		}
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
		View tabView = inflate(getContext(), R.layout.color_chooser_colorselectview, null);
		addView(tabView);
		rgbSelectorView = new RgbSelectorView(getContext());
		preSelectorView = new PresetSelectorView(getContext());
		hsvSelectorView = new HSVSelectorView(getContext());

		tabHost = tabView.findViewById(R.id.color_chooser_colorview_tabColors);
		tabHost.setup();
		ColorTabContentFactory factory = new ColorTabContentFactory();

		View preTabView = createTabView(getContext(), R.drawable.ic_color_chooser_tab_preset);
		TabSpec preTab = tabHost.newTabSpec(PRE_TAG)
				.setIndicator(preTabView)
				.setContent(factory);

		View hsvTabView = createTabView(getContext(), R.drawable.ic_color_chooser_tab_hsv);
		TabSpec hsvTab = tabHost.newTabSpec(HSV_TAG)
				.setIndicator(hsvTabView)
				.setContent(factory);

		View rgbTabView = createTabView(getContext(), R.drawable.ic_color_chooser_tab_rgba);
		TabSpec rgbTab = tabHost.newTabSpec(RGB_TAG)
				.setIndicator(rgbTabView)
				.setContent(factory);

		tabHost.addTab(preTab);
		tabHost.addTab(hsvTab);
		tabHost.addTab(rgbTab);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		preSelectorView.setOnColorChangedListener(
				new PresetSelectorView.OnColorChangedListener() {
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
		rgbSelectorView.setOnColorChangedListener(
				new RgbSelectorView.OnColorChangedListener() {
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
		setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
	}

	public interface OnColorChangedListener {
		void colorChanged(int color);
	}

	class ColorTabContentFactory implements TabContentFactory {
		@Override
		public View createTabContent(String tag) {
			switch (tag) {
				case RGB_TAG:
					return rgbSelectorView;
				case PRE_TAG:
					return preSelectorView;
				case HSV_TAG:
					return hsvSelectorView;
				default:
					throw new IllegalArgumentException();
			}
		}
	}

	static class SavedState extends BaseSavedState {
		String currentTabTag;

		SavedState(Parcel source) {
			super(source);
			currentTabTag = source.readString();
		}

		SavedState(Parcelable superState, String currentTabTag) {
			super(superState);
			this.currentTabTag = currentTabTag;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(currentTabTag);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel source) {
				return new SavedState(source);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}
