/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.MenuFileActivity;
import org.catrobat.paintroid.MenuFileActivity.ACTION;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

@SuppressLint("ValidFragment")
public class DialogSaveFile extends DialogFragment implements OnClickListener {
	public static final String BUNDLE_SAVEFILENAME = "BUNDLE_SAVEFILENAME";
	private static final String DEFAULT_FILENAME_TIME_FORMAT = "yyyy_mm_dd_hhmmss";
	private static final String FILENAME_REGEX = "[\\w]*";

	public static final String BUNDLE_RET_ACTION = "BUNDLE_RET_ACTION";

	private final MenuFileActivity mContext;
	private final Bundle mBundle;
	private EditText mEditText;
	private String mDefaultFileName;

	private String actualFilename = null;

	public DialogSaveFile(MenuFileActivity context, Bundle bundle) {
		mContext = context;
		mBundle = bundle;
		mDefaultFileName = getDefaultFileName();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater inflater = getActivity().getLayoutInflater();
		AlertDialog.Builder builder;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			builder = new AlertDialog.Builder(mContext);
		} else {
			builder = new AlertDialog.Builder(mContext,
					AlertDialog.THEME_HOLO_DARK);
		}
		builder.setTitle(R.string.dialog_save_title);
		View view = inflater.inflate(R.layout.dialog_save_file, null);
		mEditText = (EditText) view
				.findViewById(R.id.dialog_save_file_edit_text);
		if (actualFilename != null) {
			mEditText.setText(actualFilename);
		} else {
			mEditText.setHint(getDefaultFileName());
		}
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, this);
		builder.setNegativeButton(R.string.cancel, this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case AlertDialog.BUTTON_POSITIVE:
			mBundle.remove(BUNDLE_RET_ACTION);
			mBundle.putString(BUNDLE_RET_ACTION, ACTION.SAVE.toString());
			saveFile();
			break;
		case AlertDialog.BUTTON_NEGATIVE:
			mBundle.putString(BUNDLE_RET_ACTION, ACTION.CANCEL.toString());
			dismiss();
			break;
		}

	}

	private void saveFile() {

		String editTextFilename = mEditText.getText().toString();
		actualFilename = editTextFilename;
		if (!editTextFilename.matches(FILENAME_REGEX)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(R.string.dialog_unallowed_chars_title);
			builder.setMessage(R.string.dialog_unallowed_chars_text);
			builder.setNeutralButton(R.string.ok, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mBundle.putString(BUNDLE_RET_ACTION,
							ACTION.CANCEL.toString());
					dialog.dismiss();
					show(mContext.getSupportFragmentManager(), "dialogsave");
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					show(mContext.getSupportFragmentManager(), "dialogsave");
				}
			});
			builder.create().show();
			return;
		}
		final String filename = editTextFilename.length() < 1 ? mDefaultFileName
				: editTextFilename;
		File testfile = FileIO.createNewEmptyPictureFile(mContext, filename
				+ ".png");

		if (testfile == null) {
			Log.e(PaintroidApplication.TAG, "Cannot save file!");
			dismiss();
		} else if (testfile.exists()) {
			Log.w(PaintroidApplication.TAG, testfile + " already exists."); // TODO
																			// remove
																			// logging

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

			builder.setMessage(
					mContext.getString(R.string.dialog_overwrite_text))
					.setCancelable(false)
					.setPositiveButton(mContext.getString(R.string.yes),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									mContext.saveFile(filename);
									mBundle.putString(BUNDLE_SAVEFILENAME,
											filename);
									dialog.dismiss();
								}
							})
					.setNegativeButton(mContext.getString(R.string.no),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									mBundle.putString(BUNDLE_RET_ACTION,
											ACTION.CANCEL.toString());
									dialog.cancel();
									show(mContext.getSupportFragmentManager(),
											"dialogsave");
								}
							});
			builder.show();

		} else {
			mContext.saveFile(filename);
			mBundle.putString(BUNDLE_SAVEFILENAME, filename);
			dismiss();
		}
	}

	@SuppressLint("SimpleDateFormat")
	private String getDefaultFileName() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				DEFAULT_FILENAME_TIME_FORMAT);
		return simpleDateFormat.format(new Date());
	}

	public void replaceLoadedFile() {
		if (PaintroidApplication.savedBitmapFile != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage(
					mContext.getString(R.string.dialog_overwrite_text))
					.setCancelable(false)
					.setPositiveButton(mContext.getString(R.string.yes),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									PaintroidApplication.overrideFile = true;
									mContext.saveFile(PaintroidApplication.savedBitmapFile
											.getName());
									mBundle.putString(
											BUNDLE_SAVEFILENAME,
											PaintroidApplication.savedBitmapFile
													.getName());
									dialog.dismiss();
								}
							})
					.setNegativeButton(mContext.getString(R.string.no),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									mBundle.putString(BUNDLE_RET_ACTION,
											ACTION.CANCEL.toString());
									dialog.cancel();
									show(mContext.getSupportFragmentManager(),
											"dialogsave");
								}
							});
			builder.show();

		}

	}
}
