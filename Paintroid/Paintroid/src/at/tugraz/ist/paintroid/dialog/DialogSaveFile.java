/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.dialog;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
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

public class DialogSaveFile extends Dialog implements View.OnClickListener {
	public static final String BUNDLE_SAVEFILENAME = "BUNDLE_SAVEFILENAME";

	private final Context mContext;
	private final Bundle mBundle;
	private EditText mEditText;

	public DialogSaveFile(Context context, Bundle bundle) {
		super(context);
		mContext = context;
		mBundle = bundle;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_save_file);
		setTitle(R.string.dialog_save_title);

		((Button) findViewById(R.id.dialog_save_file_btn_ok)).setOnClickListener(this);
		((Button) findViewById(R.id.dialog_save_file_btn_cancel)).setOnClickListener(this);

		mEditText = (EditText) findViewById(R.id.dialog_save_file_edit_text);
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
		File testfile = FileIO.createNewEmptyPictureFile(mContext, mEditText.getText().toString() + ".png");

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
							mBundle.putString(BUNDLE_SAVEFILENAME, mEditText.getText().toString());
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
			mBundle.putString(BUNDLE_SAVEFILENAME, mEditText.getText().toString());
			cancel();
		}
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
