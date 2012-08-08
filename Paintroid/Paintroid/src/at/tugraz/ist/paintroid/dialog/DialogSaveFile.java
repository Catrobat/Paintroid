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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.paintroid.FileIO;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;

public class DialogSaveFile extends BaseDialog implements View.OnClickListener {
	public static final String BUNDLE_SAVEFILENAME = "BUNDLE_SAVEFILENAME";
	private static final String DEFAULT_FILENAME_TIME_FORMAT = "yyyy-mm-dd-hhmmss";
	private static final String FILENAME_REGEX = "[\\w]*";

	private final Context mContext;
	private final Bundle mBundle;
	private EditText mEditText;
	private String mDefaultFileName;

	public DialogSaveFile(Context context, Bundle bundle) {
		super(context);
		mContext = context;
		mBundle = bundle;
		mDefaultFileName = getDefaultFileName();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_save_file);
		setTitle(R.string.dialog_save_title);

		((Button) findViewById(R.id.dialog_save_file_btn_ok)).setOnClickListener(this);
		((Button) findViewById(R.id.dialog_save_file_btn_cancel)).setOnClickListener(this);

		mEditText = (EditText) findViewById(R.id.dialog_save_file_edit_text);
		mEditText.setHint(getDefaultFileName());

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dialog_save_file_btn_ok:
				saveFile();
				break;
			case R.id.dialog_save_file_btn_cancel:
				cancel();
				break;
		}
	}

	private void saveFile() {

		String editTextFilename = mEditText.getText().toString();
		if (!editTextFilename.matches(FILENAME_REGEX)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(R.string.dialog_unallowed_chars_title);
			builder.setMessage(R.string.dialog_unallowed_chars_text);
			builder.setNeutralButton(R.string.ok, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			return;
		}
		final String filename = editTextFilename.length() < 1 ? mDefaultFileName : editTextFilename;
		File testfile = FileIO.createNewEmptyPictureFile(mContext, filename + ".png");

		if (testfile == null) {
			Log.e(PaintroidApplication.TAG, "Cannot save file!");
			cancel();
		} else if (testfile.exists()) {
			Log.w(PaintroidApplication.TAG, testfile + " already exists."); // TODO remove logging

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage(mContext.getString(R.string.dialog_overwrite_text)).setCancelable(false)
					.setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {

							mBundle.putString(BUNDLE_SAVEFILENAME, filename);
							dialog.dismiss();
							DialogSaveFile.this.dismiss();
						}
					}).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			builder.show();

		} else {
			mBundle.putString(BUNDLE_SAVEFILENAME, filename);
			cancel();
		}
	}

	private String getDefaultFileName() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_FILENAME_TIME_FORMAT);
		return simpleDateFormat.format(new Date());
	}

	// private FileActivity mFileActivity;

	// public DialogSaveFileName(final Context context, final Bundle bundle) {
	// super(context);
	//
	// setTitle(R.string.dialog_save_title);
	// setMessage(context.getString(R.string.dialog_save_text));
	//
	// final EditText input = new EditText(context);
	// setView(input);
	//
	// setButton(context.getString(R.string.done), new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int id) {
	// File testfile = FileIO.createNewEmptyPictureFile(context, input.getText().toString() + ".png");
	//
	// if (testfile == null) {
	// Log.e(PaintroidApplication.TAG, "Cannot save file!");
	// dismiss();
	// } else if (testfile.exists()) {
	// Log.w(PaintroidApplication.TAG, testfile + " already exists."); // TODO remove logging
	//
	// // AlertDialog.Builder builder = new AlertDialog.Builder(context);
	// // builder.setMessage(context.getString(R.string.dialog_overwrite_text)).setCancelable(false)
	// // .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
	// // @Override
	// // public void onClick(DialogInterface dialog, int id) {
	// // bundle.putString(BUNDLE_SAVEFILENAME, input.getText().toString());
	// // dialog.dismiss();
	// // DialogSaveFileName.this.dismiss();
	// // }
	// // }).setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
	// // @Override
	// // public void onClick(DialogInterface dialog, int id) {
	// // dialog.cancel();
	// // }
	// // });
	// // builder.show();
	//
	// } else {
	// bundle.putString(BUNDLE_SAVEFILENAME, input.getText().toString());
	// dismiss();
	// }
	//
	// }
	// });
	// }
}
