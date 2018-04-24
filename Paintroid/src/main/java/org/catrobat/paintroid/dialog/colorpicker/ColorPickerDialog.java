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
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.widget.Button;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.BaseDialog;

import java.util.ArrayList;

public final class ColorPickerDialog extends BaseDialog {

	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "ColorPickerDialog has not been initialized. Call init() first!";
	static Paint backgroundPaint = new Paint();
	private static ColorPickerDialog instance;
	private ColorPickerView colorPickerView;
	@VisibleForTesting
	public ArrayList<OnColorPickedListener> onColorPickedListener;
	private Button buttonNewColor;

	private ColorPickerDialog(Context context) {
		super(context);
		onColorPickedListener = new ArrayList<>();
	}

	public static ColorPickerDialog getInstance() {
		if (instance == null) {
			throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
		}
		return instance;
	}

	public static void init(Context context) {
		instance = new ColorPickerDialog(context);
	}

	public void addOnColorPickedListener(OnColorPickedListener listener) {
		onColorPickedListener.add(listener);
	}

	public void removeOnColorPickedListener(OnColorPickedListener listener) {
		onColorPickedListener.remove(listener);
	}

	private void updateColorChange(int color) {
		for (OnColorPickedListener listener : onColorPickedListener) {
			listener.colorChanged(color);
		}
		MainActivity.colorPickerInitialColor = color;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.colorpicker_dialog);
		setTitle(R.string.color_chooser_title);

		Bitmap backgroundBitmap = BitmapFactory.decodeResource(getContext()
				.getResources(), R.drawable.checkeredbg);
		BitmapShader mBackgroundShader = new BitmapShader(backgroundBitmap,
				TileMode.REPEAT, TileMode.REPEAT);

		backgroundPaint.setShader(mBackgroundShader);
		buttonNewColor = (Button) findViewById(R.id.btn_colorchooser_ok);
		colorPickerView = (ColorPickerView) findViewById(R.id.view_colorpicker);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		buttonNewColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		colorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
			@Override
			public void colorChanged(int color) {
				changeNewColor(color);
				updateColorChange(color);
			}
		});
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		buttonNewColor.setOnClickListener(null);
		colorPickerView.setOnColorChangedListener(null);
	}

	public void setInitialColor(int color) {
		updateColorChange(color);
		if ((buttonNewColor != null) && (colorPickerView != null)) {
			changeNewColor(color);
			colorPickerView.setSelectedColor(color);
		}
	}

	private void changeNewColor(int color) {
		buttonNewColor.setBackground(CustomColorDrawable.createDrawable(color));

		int referenceColor = (Color.red(color) + Color.blue(color) + Color.green(color)) / 3;
		if (referenceColor <= 128 && Color.alpha(color) > 5) {
			buttonNewColor.setTextColor(Color.WHITE);
		} else {
			buttonNewColor.setTextColor(Color.BLACK);
		}
	}

	static final class CustomColorDrawable extends ColorDrawable {
		private CustomColorDrawable(@ColorInt int color) {
			super(color);
		}

		@Override
		public void draw(Canvas canvas) {
			if (Color.alpha(getColor()) != 0xff) {
				canvas.drawRect(getBounds(), backgroundPaint);
			}
			super.draw(canvas);
		}

		static Drawable createDrawable(@ColorInt int color) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
				return new CustomColorDrawable(color);
			} else {
				return new RippleDrawable(ColorStateList.valueOf(Color.WHITE),
						new CustomColorDrawable(color), null);
			}
		}
	}

	public interface OnColorPickedListener {
		void colorChanged(int color);
	}
}
