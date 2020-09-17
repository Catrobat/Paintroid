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

import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.helper.DefaultNumberRangeFilter;
import org.catrobat.paintroid.tools.options.BrushToolOptionsView;
import org.catrobat.paintroid.tools.options.BrushToolPreview;

import java.util.Locale;

import androidx.annotation.VisibleForTesting;

public final class DefaultBrushToolOptionsView implements BrushToolOptionsView {
	private static final int MIN_BRUSH_SIZE = 1;
	private static final String TAG = DefaultBrushToolOptionsView.class.getSimpleName();

	private final EditText brushSizeText;
	private final SeekBar brushWidthSeekBar;
	private final ImageButton buttonCircle;
	private final ImageButton buttonRect;
	private final BrushToolPreview brushToolPreview;
	@VisibleForTesting
	public BrushToolOptionsView.OnBrushChangedListener brushChangedListener;

	public DefaultBrushToolOptionsView(ViewGroup rootView) {
		LayoutInflater inflater = LayoutInflater.from(rootView.getContext());
		View brushPickerView = inflater.inflate(R.layout.dialog_pocketpaint_stroke, rootView, true);

		buttonCircle = brushPickerView.findViewById(R.id.pocketpaint_stroke_ibtn_circle);
		buttonRect = brushPickerView.findViewById(R.id.pocketpaint_stroke_ibtn_rect);
		brushWidthSeekBar = brushPickerView.findViewById(R.id.pocketpaint_stroke_width_seek_bar);
		brushWidthSeekBar.setOnSeekBarChangeListener(new DefaultBrushToolOptionsView.OnBrushChangedWidthSeekBarListener());
		brushSizeText = brushPickerView.findViewById(R.id.pocketpaint_stroke_width_width_text);
		brushSizeText.setFilters(new InputFilter[]{new DefaultNumberRangeFilter(1, 100)});
		brushToolPreview = brushPickerView.findViewById(R.id.pocketpaint_brush_tool_preview);

		buttonCircle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onCircleButtonClicked();
			}
		});
		buttonRect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onRectButtonClicked();
			}
		});

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
	}

	private void onRectButtonClicked() {
		updateStrokeCap(Cap.SQUARE);
		buttonRect.setSelected(true);
		buttonCircle.setSelected(false);
		invalidate();
	}

	private void onCircleButtonClicked() {
		updateStrokeCap(Cap.ROUND);
		buttonCircle.setSelected(true);
		buttonRect.setSelected(false);
		invalidate();
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

	@Override
	public void setBrushChangedListener(BrushToolOptionsView.OnBrushChangedListener brushChangedListener) {
		this.brushChangedListener = brushChangedListener;
	}

	@Override
	public void setBrushPreviewListener(OnBrushPreviewListener onBrushPreviewListener) {
		brushToolPreview.setListener(onBrushPreviewListener);
		brushToolPreview.invalidate();
	}

	private void updateStrokeWidthChange(int strokeWidth) {
		if (brushChangedListener != null) {
			brushChangedListener.setStrokeWidth(strokeWidth);
		}
	}

	private void updateStrokeCap(Cap cap) {
		if (brushChangedListener != null) {
			brushChangedListener.setCap(cap);
		}
	}

	public void invalidate() {
		brushToolPreview.invalidate();
	}

	public class OnBrushChangedWidthSeekBarListener implements SeekBar.OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (progress < MIN_BRUSH_SIZE) {
				progress = MIN_BRUSH_SIZE;
				seekBar.setProgress(progress);
			}
			updateStrokeWidthChange(progress);
			if (fromUser) {
				brushSizeText.setText(String.format(Locale.getDefault(), "%d", progress));
			}

			brushToolPreview.invalidate();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			brushSizeText.setText(String.format(Locale.getDefault(), "%d", seekBar.getProgress()));
		}
	}
}
