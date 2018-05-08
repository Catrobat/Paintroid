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

package org.catrobat.paintroid.listener;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.support.annotation.VisibleForTesting;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.ui.tools.DrawerPreview;
import org.catrobat.paintroid.ui.tools.NumberRangeFilter;

import java.util.ArrayList;
import java.util.Locale;

public final class BrushPickerView implements View.OnClickListener {
	private static final int MIN_BRUSH_SIZE = 1;
	private static final String TAG = BrushPickerView.class.getSimpleName();

	@VisibleForTesting
	public ArrayList<BrushPickerView.OnBrushChangedListener> brushChangedListener;
	private final EditText brushSizeText;
	private final SeekBar brushWidthSeekBar;
	private final ImageButton buttonCircle;
	private final ImageButton buttonRect;
	private final DrawerPreview drawerPreview;
	private final ColorPickerDialog.OnColorPickedListener onColorPickedListener;

	public BrushPickerView(ViewGroup rootView) {
		brushChangedListener = new ArrayList<>();

		LayoutInflater inflater = LayoutInflater.from(rootView.getContext());
		View brushPickerView = inflater.inflate(R.layout.dialog_stroke, rootView, true);

		buttonCircle = (ImageButton) brushPickerView.findViewById(R.id.stroke_ibtn_circle);
		buttonRect = (ImageButton) brushPickerView.findViewById(R.id.stroke_ibtn_rect);
		brushWidthSeekBar = (SeekBar) brushPickerView.findViewById(R.id.stroke_width_seek_bar);
		brushWidthSeekBar.setOnSeekBarChangeListener(new BrushPickerView.OnBrushChangedWidthSeekBarListener());
		brushSizeText = (EditText) brushPickerView.findViewById(R.id.stroke_width_width_text);
		brushSizeText.setFilters(new InputFilter[]{new NumberRangeFilter(1, 100)});
		brushSizeText.setCursorVisible(false);
		drawerPreview = (DrawerPreview) brushPickerView.findViewById(R.id.drawer_preview);

		buttonCircle.setOnClickListener(this);
		buttonRect.setOnClickListener(this);

		onColorPickedListener = new ColorPickerDialog.OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				drawerPreview.invalidate();
			}
		};
		ColorPickerDialog.getInstance().addOnColorPickedListener(onColorPickedListener);

		brushSizeText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				String sizeText = brushSizeText.getText().toString();
				int sizeTextInt;
				try {
					sizeTextInt = Integer.parseInt(sizeText);
				} catch (NumberFormatException exp) {
					Log.d(TAG, exp.getLocalizedMessage());
					sizeTextInt = MIN_BRUSH_SIZE;
				}
				brushWidthSeekBar.setProgress(sizeTextInt);
			}
		});
		brushSizeText.requestFocus();
		brushSizeText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				brushSizeText.setCursorVisible(true);
			}
		});
		brushSizeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					hideKeyboard();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.stroke_ibtn_circle:
				updateStrokeCap(Cap.ROUND);
				buttonCircle.setSelected(true);
				buttonRect.setSelected(false);
				hideKeyboard();
				break;
			case R.id.stroke_ibtn_rect:
				updateStrokeCap(Cap.SQUARE);
				buttonRect.setSelected(true);
				buttonCircle.setSelected(false);
				hideKeyboard();
				break;
			default:
				break;
		}
		drawerPreview.invalidate();
	}

	public void setCurrentPaint(Paint currentPaint) {
		if (currentPaint.getStrokeCap() == Cap.ROUND) {
			buttonCircle.setSelected(true);
			buttonRect.setSelected(false);
		} else {
			buttonCircle.setSelected(false);
			buttonRect.setSelected(true);
		}
		brushWidthSeekBar.setProgress((int) currentPaint.getStrokeWidth());
		brushSizeText.setText(String.format(Locale.getDefault(), "%d", (int) currentPaint.getStrokeWidth()));
	}

	public void addBrushChangedListener(OnBrushChangedListener listener) {
		brushChangedListener.add(listener);
	}

	public void removeBrushChangedListener(OnBrushChangedListener listener) {
		brushChangedListener.remove(listener);
	}

	public void removeListeners() {
		ColorPickerDialog.getInstance().removeOnColorPickedListener(onColorPickedListener);
	}

	private void updateStrokeChange(int strokeWidth) {
		for (OnBrushChangedListener listener : brushChangedListener) {
			listener.setStroke(strokeWidth);
		}
	}

	private void updateStrokeCap(Cap cap) {
		for (OnBrushChangedListener listener : brushChangedListener) {
			listener.setCap(cap);
		}
	}

	public interface OnBrushChangedListener {
		void setCap(Paint.Cap cap);

		void setStroke(int stroke);
	}

	public class OnBrushChangedWidthSeekBarListener implements
			SeekBar.OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (progress < MIN_BRUSH_SIZE) {
				progress = MIN_BRUSH_SIZE;
				seekBar.setProgress(progress);
			}
			updateStrokeChange(progress);
			if (fromUser) {
				brushSizeText.setText(String.format(Locale.getDefault(), "%d", progress));
				hideKeyboard();
			}

			drawerPreview.invalidate();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			brushSizeText.setText(String.format(Locale.getDefault(), "%d", seekBar.getProgress()));
			hideKeyboard();
		}
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) brushSizeText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(brushSizeText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			brushSizeText.setCursorVisible(false);
		}
	}
}
