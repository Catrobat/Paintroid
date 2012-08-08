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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.paintroid.FileIO;
import at.tugraz.ist.paintroid.MenuFileActivity.ACTION;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;

public class DialogSaveFile extends BaseDialog implements View.OnClickListener {
	public static final String BUNDLE_SAVEFILENAME = "BUNDLE_SAVEFILENAME";
	public static final String BUNDLE_RET_ACTION = "BUNDLE_RET_ACTION";

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
				mBundle.putString(BUNDLE_RET_ACTION, ACTION.SAVE.toString());
				saveFile();
				break;
			case R.id.dialog_save_file_btn_cancel:
				mBundle.putString(BUNDLE_RET_ACTION, ACTION.CANCEL.toString());
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
}
