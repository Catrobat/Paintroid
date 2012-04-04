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

package at.tugraz.ist.paintroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import at.tugraz.ist.paintroid.R;

public class BrushPickerDialog extends Dialog implements OnClickListener {

	public interface OnBrushChangedListener {
		public void setCap(Cap cap);

		public void setStroke(int stroke);
	}

	public class OnBrushChangedWidthSeekBarListener implements SeekBar.OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (brushChangedListener != null) {
				brushChangedListener.setStroke(progress);
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

	private OnBrushChangedListener brushChangedListener;
	private Paint mCurrentPaint;
	private Paint mOriginalPaint;
	private ImageView mPreviewBrushImageView;
	private Canvas mPreviewBrushCanvas;
	private Bitmap mPreviewBrushBitmap;
	private TextView mBrushSizeText;
	private final int MAX_STROKE_WIDTH = 100;
	private final int BITMAP_WIDTH_HEIGHT = 120;
	private SeekBar mBrushWidthSeekBar;

	public BrushPickerDialog(Context context, OnBrushChangedListener listener, Paint currentPaintObject) {

		super(context);
		this.brushChangedListener = listener;
		mCurrentPaint = currentPaintObject;
		mOriginalPaint = new Paint(currentPaintObject);

		initComponents();
	}

	private void initComponents() {
		setContentView(R.layout.dialog_stroke);
		setTitle(R.string.stroke_title);
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
		mPreviewBrushBitmap = Bitmap.createBitmap(BITMAP_WIDTH_HEIGHT, BITMAP_WIDTH_HEIGHT, Config.ARGB_4444);
		mPreviewBrushCanvas = new Canvas(mPreviewBrushBitmap);
		mBrushSizeText = (TextView) findViewById(R.id.stroke_width_width_text);
		changeBrushPreview();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.stroke_btn_Cancel:
				this.cancel(); // close Dialog
				break;

			case R.id.stroke_ibtn_circle:
				brushChangedListener.setCap(Cap.ROUND);
				changeBrushPreview();
				break;

			case R.id.stroke_ibtn_rect:
				brushChangedListener.setCap(Cap.SQUARE);
				changeBrushPreview();
				break;
			default:
				break;
		}
	}

	private void changeBrushPreview() {
		if (mPreviewBrushCanvas != null) {
			mPreviewBrushBitmap.eraseColor(Color.TRANSPARENT);
			mPreviewBrushCanvas.drawPoint(BITMAP_WIDTH_HEIGHT / 2, BITMAP_WIDTH_HEIGHT / 2, mCurrentPaint);
			mPreviewBrushImageView.setImageBitmap(mPreviewBrushBitmap);
			Integer strokeWidth = (int) mBrushWidthSeekBar.getProgress();
			mBrushSizeText.setText(strokeWidth.toString());
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mPreviewBrushBitmap == null) {
			mPreviewBrushBitmap = Bitmap.createBitmap(BITMAP_WIDTH_HEIGHT, BITMAP_WIDTH_HEIGHT, Config.ARGB_4444);
			mPreviewBrushCanvas = new Canvas(mPreviewBrushBitmap);
		}
		if (mPreviewBrushCanvas == null) {
			mPreviewBrushCanvas = new Canvas(mPreviewBrushBitmap);
		}
		mBrushWidthSeekBar.setProgress((int) mCurrentPaint.getStrokeWidth());
		mOriginalPaint = new Paint(mCurrentPaint);
		changeBrushPreview();
	}

	@Override
	public void onBackPressed() {
		brushChangedListener.setCap(mOriginalPaint.getStrokeCap());
		brushChangedListener.setStroke((int) mOriginalPaint.getStrokeWidth());
		super.onBackPressed();
	}
}
