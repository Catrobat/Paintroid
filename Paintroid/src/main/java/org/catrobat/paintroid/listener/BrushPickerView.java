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
import android.view.LayoutInflater;
import android.view.View;
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
	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "BrushPickerView has not been initialized. Call init() first!";
	private static final int MIN_BRUSH_SIZE = 1;
	private static BrushPickerView instance = null;

	private View brushPickerView;
	private ArrayList<BrushPickerView.OnBrushChangedListener> brushChangedListener;
	private Paint currentPaint;
	private TextView brushSizeText;
	private SeekBar brushWidthSeekBar;
	private RadioButton radioButtonCircle;
	private RadioButton radioButtonRect;
	private DrawerPreview drawerPreview;
	private int strokeWidth;

	private BrushPickerView(Context context) {
		brushChangedListener = new ArrayList<>();

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		brushPickerView = inflater.inflate(R.layout.dialog_stroke, null);

		ImageButton buttonCircle = (ImageButton) brushPickerView.findViewById(R.id.stroke_ibtn_circle);
		buttonCircle.setOnClickListener(this);

		ImageButton buttonRect = (ImageButton) brushPickerView.findViewById(R.id.stroke_ibtn_rect);
		buttonRect.setOnClickListener(this);

		radioButtonCircle = (RadioButton) brushPickerView.findViewById(R.id.stroke_rbtn_circle);
		radioButtonCircle.setOnClickListener(this);

		radioButtonRect = (RadioButton) brushPickerView.findViewById(R.id.stroke_rbtn_rect);
		radioButtonRect.setOnClickListener(this);

		brushWidthSeekBar = (SeekBar) brushPickerView.findViewById(R.id.stroke_width_seek_bar);
		brushWidthSeekBar.setOnSeekBarChangeListener(new BrushPickerView.OnBrushChangedWidthSeekBarListener());

		brushSizeText = (TextView) brushPickerView.findViewById(R.id.stroke_width_width_text);

		drawerPreview = (DrawerPreview) brushPickerView.findViewById(R.id.drawer_preview);
		ColorPickerDialog.getInstance().addOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				drawerPreview.invalidate();
			}
		});
	}

	public static BrushPickerView getInstance() {
		if (instance == null) {
			throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
		}
		return instance;
	}

	public static void init(Context context) {
		instance = new BrushPickerView(context);
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
		this.currentPaint = currentPaint;
		if (this.currentPaint.getStrokeCap() == Cap.ROUND) {
			radioButtonCircle.setChecked(true);
		} else {
			radioButtonRect.setChecked(true);
		}
		brushWidthSeekBar.setProgress((int) this.currentPaint.getStrokeWidth());
	}

	public void addBrushChangedListener(OnBrushChangedListener listener) {
		brushChangedListener.add(listener);
	}

	public void removeBrushChangedListener(OnBrushChangedListener listener) {
		brushChangedListener.remove(listener);
	}

	private void updateStrokeChange(int strokeWidth) {
		for (OnBrushChangedListener listener : brushChangedListener) {
			listener.setStroke(strokeWidth);
		}
		this.strokeWidth = strokeWidth;
	}

	private void updateStrokeCap(Cap cap) {
		for (OnBrushChangedListener listener : brushChangedListener) {
			listener.setCap(cap);
		}
	}

	public View getBrushPickerView() {
		return brushPickerView;
	}

	public int getStrokeWidth() {
		return strokeWidth;
	}

	public DrawerPreview getDrawerPreview() {
		return drawerPreview;
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
