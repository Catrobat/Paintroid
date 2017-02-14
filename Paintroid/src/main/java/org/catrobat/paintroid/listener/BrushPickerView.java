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
import org.catrobat.paintroid.tools.implementation.DrawTool;
import org.catrobat.paintroid.ui.tools.DrawerPreview;

import java.util.ArrayList;

public class BrushPickerView implements View.OnClickListener {
	public static final String NOT_INITIALIZED_ERROR_MESSAGE = "BrushPickerView has not been initialized. Call init() first!";

	private static BrushPickerView instance = null;
	private static View mBrushPickerView;

	private ArrayList<BrushPickerView.OnBrushChangedListener> mBrushChangedListener;
	private Paint mCurrentPaint;
	private Context mContext;
	private TextView mBrushSizeText;
	private SeekBar mBrushWidthSeekBar;
	private RadioButton mRbtnCircle;
	private RadioButton mRbtnRect;
	private static final int MIN_BRUSH_SIZE = 1;
	private int mStrokeWidth;


	private BrushPickerView(Context context) {
		mBrushChangedListener = new ArrayList<BrushPickerView.OnBrushChangedListener>();
		mContext = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mBrushPickerView = inflater.inflate(R.layout.dialog_stroke, null);

		ImageButton btn_circle = (ImageButton) mBrushPickerView.findViewById(R.id.stroke_ibtn_circle);
		btn_circle.setOnClickListener(this);

		ImageButton btn_rect = (ImageButton) mBrushPickerView.findViewById(R.id.stroke_ibtn_rect);
		btn_rect.setOnClickListener(this);

		mRbtnCircle = (RadioButton) mBrushPickerView.findViewById(R.id.stroke_rbtn_circle);
		mRbtnCircle.setOnClickListener(this);

		mRbtnRect = (RadioButton) mBrushPickerView.findViewById(R.id.stroke_rbtn_rect);
		mRbtnRect.setOnClickListener(this);

		mBrushWidthSeekBar = (SeekBar) mBrushPickerView.findViewById(R.id.stroke_width_seek_bar);
		mBrushWidthSeekBar.setOnSeekBarChangeListener(new BrushPickerView.OnBrushChangedWidthSeekBarListener());

		mBrushSizeText = (TextView) mBrushPickerView.findViewById(R.id.stroke_width_width_text);
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
				mRbtnCircle.setChecked(true);
				break;
			case R.id.stroke_ibtn_rect:
				updateStrokeCap(Cap.SQUARE);
				mRbtnRect.setChecked(true);
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

			mBrushSizeText.setText("" + progress);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	}

	public void setCurrentPaint(Paint currentPaint) {
		mCurrentPaint = currentPaint;
		if (mCurrentPaint.getStrokeCap() == Cap.ROUND) {
			mRbtnCircle.setChecked(true);
		} else {
			mRbtnRect.setChecked(true);
		}
		mBrushWidthSeekBar.setProgress((int) mCurrentPaint.getStrokeWidth());
	}

	public void addBrushChangedListener(OnBrushChangedListener listener) {
		mBrushChangedListener.add(listener);
	}

	public void removeBrushChangedListener(OnBrushChangedListener listener) {
		mBrushChangedListener.remove(listener);
	}

	private void updateStrokeChange(int strokeWidth) {
		for (OnBrushChangedListener listener : mBrushChangedListener) {
			if (listener == null) {
				mBrushChangedListener.remove(listener);
			}
			listener.setStroke(strokeWidth);
			mStrokeWidth = strokeWidth;
		}
	}

	private void updateStrokeCap(Cap cap) {
		for (OnBrushChangedListener listener : mBrushChangedListener) {
			if (listener == null) {
				mBrushChangedListener.remove(listener);
			}
			listener.setCap(cap);
		}
	}

	public View getBrushPickerView () {
		return mBrushPickerView;
	}

	public int getStrokeWidth() {
		return mStrokeWidth;
	}
}
