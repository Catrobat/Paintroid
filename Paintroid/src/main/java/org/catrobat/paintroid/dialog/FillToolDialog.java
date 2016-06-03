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

package org.catrobat.paintroid.dialog;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import org.catrobat.paintroid.R;

@SuppressLint("ValidFragment")
public final class FillToolDialog extends DialogFragment implements
		View.OnClickListener, DialogInterface.OnClickListener {

	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "FillToolDialog has not been initialized. Call init() first!";
	private static FillToolDialog instance;

	private OnFillToolDialogChangedListener mOnFillToolDialogChangedListener;
	private Context mContext;
	private SeekBar mColorToleranceSeekBar;
	private EditText mColorToleranceEditText;
	private int mColorTolerance = 0;

	public interface OnFillToolDialogChangedListener {
		void updateColorTolerance(int colorTolerance);
	}

	@SuppressLint("ValidFragment")
	private FillToolDialog(Context context) {
		mContext = context;
		mColorTolerance = 0;
	}

	public static FillToolDialog getInstance() {
		if (instance == null) {
			throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
		}
		return instance;
	}

	public static void init(Context context) {
		instance = new FillToolDialog(context);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater inflater = getActivity().getLayoutInflater();
		AlertDialog.Builder builder = new CustomAlertDialogBuilder(mContext);
		builder.setTitle(R.string.fill_tool_dialog_title);
		final View view = inflater.inflate(R.layout.dialog_fill_tool, null);

		mColorToleranceSeekBar = (SeekBar) view.findViewById(R.id.color_tolerance_seek_bar);
		mColorToleranceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mColorTolerance = progress;
				updateColorToleranceText(mColorTolerance);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		mColorToleranceEditText = (EditText) view.findViewById(R.id.fill_tool_dialog_color_tolerance_input);
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
					mColorTolerance = Integer.parseInt(s.toString());
					if (mColorTolerance > 100) {
						mColorTolerance = 100;
						updateColorToleranceText(mColorTolerance);
					}
					mColorToleranceSeekBar.setProgress(mColorTolerance);
					mOnFillToolDialogChangedListener.updateColorTolerance(mColorTolerance);
				} catch (NumberFormatException e) {
					Log.e("Error parsing tolerance", "result was null");
				}
			}
		});

		builder.setView(view);
		builder.setNeutralButton(R.string.done, this);

		return builder.create();
	}

	@Override
	public void onStart() {
		super.onStart();
		mColorToleranceSeekBar.setProgress(mColorTolerance);
		updateColorToleranceText(mColorTolerance);
	}

	private void updateColorToleranceText(int tolerance) {
		mColorToleranceEditText.setText(String.valueOf(tolerance));
		mColorToleranceEditText.setSelection(mColorToleranceEditText.length());
	}

	public void setOnFillToolDialogChangedListener(OnFillToolDialogChangedListener listener) {
		mOnFillToolDialogChangedListener = listener;
	}

	public int getColorTolerance() {
		return mColorTolerance;
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}
}
