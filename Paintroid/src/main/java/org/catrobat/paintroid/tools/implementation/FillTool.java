/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.FillCommand;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.LayersDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;

public class FillTool extends BaseTool {
	public static final int DEFAULT_TOLERANCE_IN_PERCENT = 12;
	public static final int MAX_ABSOLUTE_TOLERANCE = 510;

	private float mColorTolerance;
	private SeekBar mColorToleranceSeekBar;
	private EditText mColorToleranceEditText;
	private View mFillToolOptionsView;

	public FillTool(Context context, ToolType toolType) {
		super(context, toolType);
	}

	public void updateColorTolerance(int colorToleranceInPercent) {
		mColorTolerance = getToleranceAbsoluteValue(colorToleranceInPercent);
	}

	public float getToleranceAbsoluteValue(int toleranceInPercent) {
		if (toleranceInPercent == 0) {
			return 0;
		}
		return (MAX_ABSOLUTE_TOLERANCE * toleranceInPercent) / 100.0f;
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		return false;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		return false;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		int bitmapHeight = PaintroidApplication.drawingSurface
				.getBitmapHeight();
		int bitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();

		if ((coordinate.x > bitmapWidth) || (coordinate.y > bitmapHeight)
				|| (coordinate.x < 0) || (coordinate.y < 0)) {
			return false;
		}

		if (mColorTolerance == 0 && mBitmapPaint.getColor() == PaintroidApplication.drawingSurface.getPixel(coordinate)) {
			return false;
		}

		Command command = new FillCommand(new Point((int) coordinate.x, (int) coordinate.y), mBitmapPaint, mColorTolerance);

		IndeterminateProgressDialog.getInstance().show();
		((FillCommand) command).addObserver(this);
		Layer layer = LayerListener.getInstance().getCurrentLayer();
		PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
		return true;
	}

	@Override
	public void resetInternalState() {
	}

	@Override
	public void draw(Canvas canvas) {
	}

	@Override
	public void setupToolOptions() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFillToolOptionsView = inflater.inflate(R.layout.dialog_fill_tool, null);

		mColorToleranceSeekBar = (SeekBar) mFillToolOptionsView.findViewById(R.id.color_tolerance_seek_bar);
		mColorToleranceEditText = (EditText) mFillToolOptionsView.findViewById(R.id.fill_tool_dialog_color_tolerance_input);
		initializeFillOptionsListener();
		updateColorToleranceText(DEFAULT_TOLERANCE_IN_PERCENT);

		mToolSpecificOptionsLayout.addView(mFillToolOptionsView);
	}

	private void initializeFillOptionsListener() {

		mColorToleranceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				updateColorToleranceText(progress);
				mColorToleranceEditText.setCursorVisible(false);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});


		mColorToleranceEditText.setCursorVisible(false);
		mColorToleranceEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				try {
					int colorToleranceInPercent = Integer.parseInt(s.toString());
					if (colorToleranceInPercent > 100) {
						colorToleranceInPercent = 100;
						updateColorToleranceText(colorToleranceInPercent);
					}
					mColorToleranceSeekBar.setProgress(colorToleranceInPercent);
					updateColorTolerance(colorToleranceInPercent);
				} catch (NumberFormatException e) {
					Log.e("Error parsing tolerance", "result was null");
				}
			}
		});
		mColorToleranceEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.fill_tool_dialog_color_tolerance_input) {
					mColorToleranceEditText.setCursorVisible(true);
				}
			}
		});
		mColorToleranceEditText.requestFocus();
	}

	private void updateColorToleranceText(int toleranceInPercent) {
		mColorToleranceEditText.setText(String.valueOf(toleranceInPercent));
		mColorToleranceEditText.setSelection(mColorToleranceEditText.length());
	}
}
