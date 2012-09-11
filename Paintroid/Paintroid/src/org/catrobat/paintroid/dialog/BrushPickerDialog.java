/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.dialog;

import org.catrobat.paintroid.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class BrushPickerDialog extends BaseDialog implements OnClickListener {

	public interface OnBrushChangedListener {
		public void setCap(Cap cap);

		public void setStroke(int stroke);
	}

	public class OnBrushChangedWidthSeekBarListener implements SeekBar.OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (mBrushChangedListener != null) {
				mBrushChangedListener.setStroke(progress);
			}
			changeBrushPreview();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	}

	private OnBrushChangedListener mBrushChangedListener;
	private Paint mCurrentPaint;
	private ImageView mPreviewBrushImageView;
	private Canvas mPreviewBrushCanvas;
	private Bitmap mPreviewBrushBitmap;
	private TextView mBrushSizeText;
	private SeekBar mBrushWidthSeekBar;
	private final int PREVIEW_BITMAP_SIZE = 120;
	private static final int MIN_BRUSH_SIZE = 1;

	public BrushPickerDialog(Context context, OnBrushChangedListener listener, Paint currentPaintObject) {

		super(context);
		this.mBrushChangedListener = listener;
		mCurrentPaint = currentPaintObject;

		initComponents();
	}

	private void initComponents() {
		setContentView(R.layout.dialog_stroke);
		setTitle(R.string.stroke_title);
		setCanceledOnTouchOutside(true);
		setCancelable(true);

		Button btn_cancel = (Button) findViewById(R.id.stroke_btn_Cancel);
		btn_cancel.setOnClickListener(this);

		ImageButton btn_circle = (ImageButton) findViewById(R.id.stroke_ibtn_circle);
		btn_circle.setOnClickListener(this);

		ImageButton btn_rect = (ImageButton) findViewById(R.id.stroke_ibtn_rect);
		btn_rect.setOnClickListener(this);

		mBrushWidthSeekBar = (SeekBar) findViewById(R.id.stroke_width_seek_bar);

		mBrushWidthSeekBar.setOnSeekBarChangeListener(new OnBrushChangedWidthSeekBarListener());
		mBrushWidthSeekBar.setProgress((int) mCurrentPaint.getStrokeWidth());

		mPreviewBrushImageView = (ImageView) findViewById(R.id.stroke_image_preview);
		mPreviewBrushBitmap = Bitmap.createBitmap(PREVIEW_BITMAP_SIZE, PREVIEW_BITMAP_SIZE, Config.ARGB_4444);
		mPreviewBrushCanvas = new Canvas(mPreviewBrushBitmap);
		mBrushSizeText = (TextView) findViewById(R.id.stroke_width_width_text);
		changeBrushPreview();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.stroke_btn_Cancel:
				super.cancel();
				break;
			case R.id.stroke_ibtn_circle:
				mBrushChangedListener.setCap(Cap.ROUND);
				changeBrushPreview();
				break;

			case R.id.stroke_ibtn_rect:
				mBrushChangedListener.setCap(Cap.SQUARE);
				changeBrushPreview();
				break;
			default:
				break;
		}
	}

	private void changeBrushPreview() {
		if (mPreviewBrushCanvas != null) {
			Integer strokeWidth = (int) mBrushWidthSeekBar.getProgress();
			if (strokeWidth < MIN_BRUSH_SIZE) {
				mBrushWidthSeekBar.setProgress(MIN_BRUSH_SIZE);
				changeBrushPreview();
				return;
			}
			mPreviewBrushBitmap.eraseColor(Color.TRANSPARENT);

			if (Color.alpha(mCurrentPaint.getColor()) == 0) {
				Paint borderPaint = new Paint();
				borderPaint.setColor(Color.BLACK);
				borderPaint.setStrokeWidth(1);
				borderPaint.setStrokeCap(mCurrentPaint.getStrokeCap());
				borderPaint.setStyle(Style.STROKE);

				if (mCurrentPaint.getStrokeCap() == Cap.ROUND) {
					mPreviewBrushCanvas.drawCircle(PREVIEW_BITMAP_SIZE / 2, PREVIEW_BITMAP_SIZE / 2,
							mCurrentPaint.getStrokeWidth() / 2, borderPaint);
				} else if (mCurrentPaint.getStrokeCap() == Cap.SQUARE) {
					Rect rect = new Rect((int) ((PREVIEW_BITMAP_SIZE / 2) - (mCurrentPaint.getStrokeWidth() / 2)),
							(int) ((PREVIEW_BITMAP_SIZE / 2) + (mCurrentPaint.getStrokeWidth() / 2)),
							(int) ((PREVIEW_BITMAP_SIZE / 2) + (mCurrentPaint.getStrokeWidth() / 2)),
							(int) ((PREVIEW_BITMAP_SIZE / 2) - (mCurrentPaint.getStrokeWidth() / 2)));
					mPreviewBrushCanvas.drawRect(rect, borderPaint);
				}
			}

			mPreviewBrushCanvas.drawPoint(PREVIEW_BITMAP_SIZE / 2, PREVIEW_BITMAP_SIZE / 2, mCurrentPaint);
			mPreviewBrushImageView.setImageBitmap(mPreviewBrushBitmap);
			mBrushSizeText.setText(strokeWidth.toString());
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mPreviewBrushBitmap == null) {
			mPreviewBrushBitmap = Bitmap.createBitmap(PREVIEW_BITMAP_SIZE, PREVIEW_BITMAP_SIZE, Config.ARGB_4444);
			mPreviewBrushCanvas = new Canvas(mPreviewBrushBitmap);
		}
		if (mPreviewBrushCanvas == null) {
			mPreviewBrushCanvas = new Canvas(mPreviewBrushBitmap);
		}
		mBrushWidthSeekBar.setProgress((int) mCurrentPaint.getStrokeWidth());
		changeBrushPreview();
	}

	// @Override
	// public void onBackPressed() {
	// super.onBackPressed();
	// }

	// @Override
	// public void onStop() {
	// mPreviewBrushBitmap.recycle();
	// mPreviewBrushBitmap = null;
	// mPreviewBrushCanvas = null;
	// super.onStop();
	// }
}
