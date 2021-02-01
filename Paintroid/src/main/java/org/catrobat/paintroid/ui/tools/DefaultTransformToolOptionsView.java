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

package org.catrobat.paintroid.ui.tools;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.options.TransformToolOptionsView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class DefaultTransformToolOptionsView implements TransformToolOptionsView {
	private static final String TAG = DefaultTransformToolOptionsView.class.getSimpleName();
	private final TransformToolSizeTextWatcher heightTextWatcher;
	private final TransformToolSizeTextWatcher widthTextWatcher;
	private EditText widthEditText;
	private EditText heightEditText;
	private SeekBar resizeSeekBar;
	private TextView percentageText;

	private Callback callback;

	public DefaultTransformToolOptionsView(ViewGroup rootView) {
		LayoutInflater inflater = LayoutInflater.from(rootView.getContext());
		View optionsView = inflater.inflate(R.layout.dialog_pocketpaint_transform_tool, rootView);

		widthEditText = optionsView.findViewById(R.id.pocketpaint_transform_width_value);
		heightEditText = optionsView.findViewById(R.id.pocketpaint_transform_height_value);
		resizeSeekBar = optionsView.findViewById(R.id.pocketpaint_transform_resize_seekbar);
		percentageText = optionsView.findViewById(R.id.pocketpaint_transform_resize_percentage_text);

		widthTextWatcher = new TransformToolSizeTextWatcher() {
			@Override
			protected void setValue(float value) {
				if (callback != null) {
					callback.setBoxWidth(value);
				}
			}
		};
		heightTextWatcher = new TransformToolSizeTextWatcher() {
			@Override
			protected void setValue(float value) {
				if (callback != null) {
					callback.setBoxHeight(value);
				}
			}
		};
		widthEditText.addTextChangedListener(widthTextWatcher);
		heightEditText.addTextChangedListener(heightTextWatcher);

		optionsView.findViewById(R.id.pocketpaint_transform_auto_crop_btn)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.autoCropClicked();
						}
					}
				});
		optionsView.findViewById(R.id.pocketpaint_transform_rotate_left_btn)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.rotateCounterClockwiseClicked();
						}
					}
				});
		optionsView.findViewById(R.id.pocketpaint_transform_rotate_right_btn)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.rotateClockwiseClicked();
						}
					}
				});
		optionsView.findViewById(R.id.pocketpaint_transform_flip_horizontal_btn)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.flipHorizontalClicked();
						}
					}
				});
		optionsView.findViewById(R.id.pocketpaint_transform_flip_vertical_btn)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.flipVerticalClicked();
						}
					}
				});
		optionsView.findViewById(R.id.pocketpaint_transform_apply_resize_btn)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.applyResizeClicked(resizeSeekBar.getProgress());
							resizeSeekBar.setProgress(100);
							callback.hideToolOptions();
						}
					}
				});
		resizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress == 0) {
					seekBar.setProgress(1);
					return;
				}
				percentageText.setText(String.format(Locale.getDefault(), "%d", progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	}

	@Override
	public void setWidthFilter(NumberRangeFilter numberRangeFilter) {
		widthEditText.setFilters(new InputFilter[]{numberRangeFilter});
	}

	@Override
	public void setHeightFilter(NumberRangeFilter numberRangeFilter) {
		heightEditText.setFilters(new InputFilter[]{numberRangeFilter});
	}

	@Override
	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	@Override
	public void setWidth(int width) {
		widthEditText.removeTextChangedListener(widthTextWatcher);
		widthEditText.setText(String.valueOf(width));
		widthEditText.addTextChangedListener(widthTextWatcher);
	}

	@Override
	public void setHeight(int height) {
		heightEditText.removeTextChangedListener(heightTextWatcher);
		heightEditText.setText(String.valueOf(height));
		heightEditText.addTextChangedListener(heightTextWatcher);
	}

	public abstract static class TransformToolSizeTextWatcher implements TextWatcher {
		protected abstract void setValue(float value);

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable editable) {
			String str = editable.toString();
			if (str.isEmpty()) {
				str = "1";
			}
			try {
				int value = NumberFormat.getIntegerInstance().parse(str).intValue();
				setValue(value);
			} catch (ParseException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}
}
