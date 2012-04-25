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
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import at.tugraz.ist.paintroid.R;

public class HsvHueSelectorView extends LinearLayout {

	private Drawable hueValueSlider;
	private ImageView hueValueSliderImage;
	private int minOffset = 0;
	private ImageView hueImageView;

	private float hue = 0;

	private OnHueChangedListener listener;

	public HsvHueSelectorView(Context context) {
		super(context);
		init();
	}

	public HsvHueSelectorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setMinContentOffset(int minOffset) {
		this.minOffset = minOffset;
		LayoutParams params = new LayoutParams(hueImageView.getLayoutParams());
		params.setMargins(0, getOffset(), 0, getSelectorOffset());
		hueImageView.setLayoutParams(params);
	}

	private void init() {
		hueValueSlider = getContext().getResources().getDrawable(R.drawable.ic_cp_sliderselector);
		buildUI();
	}

	private void buildUI() {
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_HORIZONTAL);

		hueValueSliderImage = new ImageView(getContext());
		hueValueSliderImage.setImageDrawable(hueValueSlider);
		LayoutParams paramsSeek = new LayoutParams(hueValueSlider.getIntrinsicWidth(), hueValueSlider
				.getIntrinsicHeight());
		addView(hueValueSliderImage, paramsSeek);

		hueImageView = new ImageView(getContext());
		hueImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_cp_hue));
		hueImageView.setScaleType(ScaleType.FIT_XY);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		params.setMargins(0, getOffset(), 0, getSelectorOffset());
		addView(hueImageView, params);
	}

	private int getOffset() {
		return Math.max(minOffset, getSelectorOffset());
	}

	private int getSelectorOffset() {
		return (int) Math.ceil(hueValueSlider.getIntrinsicHeight() / 2.f);
	}

	private boolean down = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			down = true;
			setPosition((int) event.getY());
			return true;
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			down = false;
			return true;
		}
		if (down && event.getAction() == MotionEvent.ACTION_MOVE) {
			setPosition((int) event.getY());
			return true;
		}
		return super.onTouchEvent(event);
	}

	private void setPosition(int y) {
		int hueY = y - getOffset();

		hue = Math.max(Math.min(360.f - (((float) hueY / hueImageView.getHeight()) * 360.f), 360.f), 0.f);

		placeSelector();

		onHueChanged();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		placeSelector();
	}

	private void placeSelector() {
		int hueY = (int) ((((360.f - hue) / 360.f)) * hueImageView.getHeight());
		hueValueSliderImage.layout(0, hueY + getOffset() - getSelectorOffset(), hueValueSliderImage.getWidth(), hueY
				+ getOffset() - getSelectorOffset() + hueValueSliderImage.getHeight());
	}

	public void setHue(float hue) {
		if (this.hue == hue) {
			return;
		}
		this.hue = hue;
		placeSelector();
	}

	public float getHue() {
		return hue;
	}

	public void setOnHueChangedListener(OnHueChangedListener listener) {
		this.listener = listener;
	}

	private void onHueChanged() {
		if (listener != null) {
			listener.hueChanged(this, hue);
		}
	}

	public interface OnHueChangedListener {
		public void hueChanged(HsvHueSelectorView sender, float hue);
	}
}
