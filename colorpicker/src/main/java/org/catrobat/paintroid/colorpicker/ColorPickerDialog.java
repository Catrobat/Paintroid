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

package org.catrobat.paintroid.colorpicker;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public final class ColorPickerDialog extends AppCompatDialogFragment implements ColorPickerView.OnColorChangedListener {
	private static final String CURRENT_COLOR = "CurrentColor";
	private static final String INITIAL_COLOR = "InitialColor";
	private static final String SHOW_ACTION_BAR = "ShowActionBar";
	@VisibleForTesting
	public List<OnColorPickedListener> onColorPickedListener = new ArrayList<>();
	private ColorPickerView colorPickerView;
	private Button buttonApply;
	private Button buttonCancel;
	private int colorToApply;
	private Shader checkeredShader;

	public static ColorPickerDialog newInstance(@ColorInt int initialColor) {
		return newInstance(initialColor, false);
	}

	public static ColorPickerDialog newInstance(@ColorInt int initialColor, boolean showActionBar) {
		ColorPickerDialog dialog = new ColorPickerDialog();
		Bundle bundle = new Bundle();
		bundle.putInt(INITIAL_COLOR, initialColor);
		bundle.putInt(CURRENT_COLOR, initialColor);
		bundle.putBoolean(SHOW_ACTION_BAR, showActionBar);
		dialog.setArguments(bundle);
		return dialog;
	}

	public void addOnColorPickedListener(OnColorPickedListener listener) {
		onColorPickedListener.add(listener);
	}

	private void updateColorChange(int color) {
		for (OnColorPickedListener listener : onColorPickedListener) {
			listener.colorChanged(color);
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bitmap checkeredBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pocketpaint_checkeredbg);
		checkeredShader = new BitmapShader(checkeredBitmap, TileMode.REPEAT, TileMode.REPEAT);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_color_picker, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		buttonApply = view.findViewById(R.id.color_picker_button_ok);
		colorPickerView = view.findViewById(R.id.color_picker_view);
		buttonCancel = view.findViewById(R.id.color_picker_button_cancel);

		buttonApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					updateColorChange(colorToApply);
					dismiss();
				}
			});

		buttonCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});

		colorPickerView.setOnColorChangedListener(this);

		if (savedInstanceState != null) {
			setCurrentColor(savedInstanceState.getInt(CURRENT_COLOR, Color.BLACK));
			setInitialColor(savedInstanceState.getInt(INITIAL_COLOR, Color.BLACK));
		} else {
			Bundle arguments = getArguments();
			setCurrentColor(arguments.getInt(CURRENT_COLOR, Color.BLACK));
			setInitialColor(arguments.getInt(INITIAL_COLOR, Color.BLACK));
		}

		colorToApply = colorPickerView.getInitialColor();

		boolean showActionBar = getArguments().getBoolean(SHOW_ACTION_BAR);
		Toolbar toolbar = view.findViewById(R.id.color_picker_toolbar);

		if (getShowsDialog() || !showActionBar) {
			toolbar.setVisibility(View.GONE);
		} else {
			AppCompatActivity activity = (AppCompatActivity) getActivity();
			activity.setSupportActionBar(toolbar);

			ActionBar supportActionBar = activity.getSupportActionBar();
			if (supportActionBar != null) {
				supportActionBar.setDisplayHomeAsUpEnabled(true);
				supportActionBar.setDisplayShowHomeEnabled(true);
				supportActionBar.setTitle(R.string.color_picker_title);
			}
		}
	}

	@Override
	public void dismiss() {
		if (getShowsDialog()) {
			super.dismiss();
		} else {
			getActivity().getSupportFragmentManager().popBackStack();
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setTitle(R.string.color_picker_title);
		return dialog;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(CURRENT_COLOR, colorPickerView.getSelectedColor());
		outState.putInt(INITIAL_COLOR, colorPickerView.getInitialColor());
	}

	public void setInitialColor(int color) {
		setButtonColor(color, buttonCancel);
		colorPickerView.setInitialColor(color);
	}

	public void setCurrentColor(int color) {
		setButtonColor(color, buttonApply);
		colorPickerView.setSelectedColor(color);
	}

	private void setButtonColor(int color, Button button) {
		button.setBackground(CustomColorDrawable.createDrawable(checkeredShader, color));

		int grayScale = (int) (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color));

		if (grayScale <= 128 && Color.alpha(color) >= 128) {
			button.setTextColor(Color.WHITE);
		} else {
			button.setTextColor(Color.BLACK);
		}
	}

	@Override
	public void colorChanged(int color) {
		setButtonColor(color, buttonApply);
		colorToApply = color;
	}

	public interface OnColorPickedListener {
		void colorChanged(int color);
	}

	static final class CustomColorDrawable extends ColorDrawable {
		private Paint backgroundPaint;

		private CustomColorDrawable(Shader checkeredShader, @ColorInt int color) {
			super(color);

			if (Color.alpha(getColor()) != 0xff) {
				backgroundPaint = new Paint();
				backgroundPaint.setShader(checkeredShader);
			}
		}

		static Drawable createDrawable(Shader checkeredShader, @ColorInt int color) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
				return new CustomColorDrawable(checkeredShader, color);
			} else {
				return new RippleDrawable(ColorStateList.valueOf(Color.WHITE),
						new CustomColorDrawable(checkeredShader, color), null);
			}
		}

		@Override
		public void draw(Canvas canvas) {
			if (backgroundPaint != null) {
				canvas.drawRect(getBounds(), backgroundPaint);
			}
			super.draw(canvas);
		}
	}
}
