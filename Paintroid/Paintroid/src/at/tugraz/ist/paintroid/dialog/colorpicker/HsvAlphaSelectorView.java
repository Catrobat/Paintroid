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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import at.tugraz.ist.paintroid.R;

public class HsvAlphaSelectorView extends LinearLayout {

	private Drawable alphaValueSlider;
	private ImageView alphaValueSliderImage;
	private int minContentOffset = 0;
	private ImageView alphaImageView;

	private int alpha = 0;
	private int color = Color.WHITE;

	private boolean dirty = true;

	private OnAlphaChangedListener listener;

	public HsvAlphaSelectorView(Context context) {
		super(context);
		init();
	}

	public HsvAlphaSelectorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setMinContentOffset(int minOffset) {
		minContentOffset = minOffset;
		LayoutParams params = new LayoutParams(alphaImageView.getLayoutParams());
		params.setMargins(0, getOffset(), 0, getSelectorOffset());
		alphaImageView.setLayoutParams(params);
	}

	private void init() {
		alphaValueSlider = getContext().getResources().getDrawable(R.drawable.ic_cp_sliderselector);
		buildUI();
	}

	private void buildUI() {
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_HORIZONTAL);
		setWillNotDraw(false);

		alphaValueSliderImage = new ImageView(getContext());
		alphaValueSliderImage.setImageDrawable(alphaValueSlider);
		LayoutParams paramsSeek = new LayoutParams(alphaValueSlider.getIntrinsicWidth(),
				alphaValueSlider.getIntrinsicHeight());
		addView(alphaValueSliderImage, paramsSeek);

		alphaImageView = new ImageView(getContext());
		alphaImageView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.transparentrepeat));
		alphaImageView.setScaleType(ScaleType.FIT_XY);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		params.setMargins(0, getOffset(), 0, getSelectorOffset());
		addView(alphaImageView, params);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (dirty) {
			dirty = false;
			setAlphaImage();
		}
		super.onDraw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		placeSelector();
	}

	private void placeSelector() {
		int alphaY = (int) (((255 - alpha) / 255.f) * alphaImageView.getHeight());

		int halfHeight = getSelectorOffset();
		int vertOffset = alphaImageView.getTop();

		alphaValueSliderImage.layout(0, alphaY + vertOffset - halfHeight, alphaValueSliderImage.getWidth(), alphaY
				+ vertOffset - halfHeight + alphaValueSliderImage.getHeight());
	}

	private int getSelectorOffset() {
		return (int) Math.ceil(alphaValueSliderImage.getHeight() / 2.f);
	}

	private int getOffset() {
		return Math.max(minContentOffset, (int) Math.ceil((double) alphaValueSlider.getIntrinsicHeight() / 2));
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
		int alphaY = y - alphaImageView.getTop();
		alpha = 255 - Math.min(255, Math.max(0, (int) (((float) alphaY / alphaImageView.getHeight()) * 255.f)));

		placeSelector();

		onAlphaChanged();
	}

	public void setAlpha(int alpha) {
		if (this.alpha == alpha) {
			return;
		}
		this.alpha = alpha;
		placeSelector();
	}

	public int getAlpha() {
		return alpha;
	}

	public void setColor(int color) {
		if (this.color == color) {
			return;
		}
		this.color = color;
		setAlphaImage();
	}

	private void setAlphaImage() {
		if (alphaImageView.getHeight() <= 0) {
			dirty = true;
			invalidate();
			return;
		}
		Paint paint = new Paint();
		Shader shader;
		Bitmap drawCache = null;
		if (drawCache == null) {

			int colorFullAlpha = color | 0xFF000000;
			int colorNoAlpha = color & 0x00FFFFFF;

			shader = new LinearGradient(0, alphaImageView.getHeight(), 0, 0, colorNoAlpha, colorFullAlpha,
					TileMode.CLAMP);

			paint.setShader(shader);

			drawCache = Bitmap.createBitmap(alphaImageView.getWidth(), alphaImageView.getHeight(),
					Bitmap.Config.ARGB_8888);
			Canvas cacheCanvas = new Canvas(drawCache);
			cacheCanvas.drawRect(0.f, 0.f, alphaImageView.getWidth(), alphaImageView.getHeight(), paint);

			alphaImageView.setImageBitmap(drawCache);
		}
	}

	public void setOnAlphaChangedListener(OnAlphaChangedListener listener) {
		this.listener = listener;
	}

	private void onAlphaChanged() {
		if (listener != null) {
			listener.alphaChanged(this, alpha);
		}
	}

	public interface OnAlphaChangedListener {
		public void alphaChanged(HsvAlphaSelectorView sender, int alpha);
	}
}
