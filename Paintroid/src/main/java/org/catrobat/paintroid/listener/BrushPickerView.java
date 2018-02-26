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

import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.support.annotation.VisibleForTesting;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.ui.tools.DrawerPreview;

import java.util.ArrayList;
import java.util.Locale;

public final class BrushPickerView implements View.OnClickListener {
	private static final int MIN_BRUSH_SIZE = 1;

	@VisibleForTesting
	public ArrayList<BrushPickerView.OnBrushChangedListener> brushChangedListener;
	private final TextView brushSizeText;
	private final SeekBar brushWidthSeekBar;
	private final RadioButton radioButtonCircle;
	private final RadioButton radioButtonRect;
	private final DrawerPreview drawerPreview;
	private final ColorPickerDialog.OnColorPickedListener onColorPickedListener;

	public BrushPickerView(ViewGroup rootView) {
		brushChangedListener = new ArrayList<>();

		LayoutInflater inflater = LayoutInflater.from(rootView.getContext());
		View brushPickerView = inflater.inflate(R.layout.dialog_stroke, rootView, true);

		ImageButton buttonCircle = (ImageButton) brushPickerView.findViewById(R.id.stroke_ibtn_circle);
		ImageButton buttonRect = (ImageButton) brushPickerView.findViewById(R.id.stroke_ibtn_rect);
		radioButtonCircle = (RadioButton) brushPickerView.findViewById(R.id.stroke_rbtn_circle);
		radioButtonRect = (RadioButton) brushPickerView.findViewById(R.id.stroke_rbtn_rect);
		brushWidthSeekBar = (SeekBar) brushPickerView.findViewById(R.id.stroke_width_seek_bar);
		brushWidthSeekBar.setOnSeekBarChangeListener(new BrushPickerView.OnBrushChangedWidthSeekBarListener());
		brushSizeText = (TextView) brushPickerView.findViewById(R.id.stroke_width_width_text);
		drawerPreview = (DrawerPreview) brushPickerView.findViewById(R.id.drawer_preview);

		buttonCircle.setOnClickListener(this);
		buttonRect.setOnClickListener(this);
		radioButtonCircle.setOnClickListener(this);
		radioButtonRect.setOnClickListener(this);

		onColorPickedListener = new ColorPickerDialog.OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				drawerPreview.invalidate();
			}
		};
		ColorPickerDialog.getInstance().addOnColorPickedListener(onColorPickedListener);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.stroke_ibtn_circle:
				updateStrokeCap(Cap.ROUND);
				radioButtonCircle.setChecked(true);
				break;
			case R.id.stroke_ibtn_rect:
				updateStrokeCap(Cap.SQUARE);
				radioButtonRect.setChecked(true);
				break;
			case R.id.stroke_rbtn_circle:
				updateStrokeCap(Cap.ROUND);
				break;
			case R.id.stroke_rbtn_rect:
				updateStrokeCap(Cap.SQUARE);
				break;
			default:
				break;
		}
		drawerPreview.invalidate();
	}

	public void setCurrentPaint(Paint currentPaint) {
		if (currentPaint.getStrokeCap() == Cap.ROUND) {
			radioButtonCircle.setChecked(true);
		} else {
			radioButtonRect.setChecked(true);
		}
		brushWidthSeekBar.setProgress((int) currentPaint.getStrokeWidth());
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

			brushSizeText.setText(String.format(Locale.getDefault(), "%d", progress));

			drawerPreview.invalidate();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	}
}
