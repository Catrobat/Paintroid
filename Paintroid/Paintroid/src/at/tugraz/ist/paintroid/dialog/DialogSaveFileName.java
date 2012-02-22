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
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
import at.tugraz.ist.paintroid.FileActivity;
import at.tugraz.ist.paintroid.FileIO;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;

public class DialogSaveFileName extends AlertDialog {

	private FileActivity mFileActivity;

	public DialogSaveFileName(final Context context) {
		super(context);

		if (context instanceof FileActivity) {
			mFileActivity = (FileActivity) context;
		} else {
			throw new IllegalStateException("DialogSaveFileName needs FileActivity context!");
		}

		setTitle(R.string.dialog_save_title);
		setMessage(context.getString(R.string.dialog_save_text));

		final EditText input = new EditText(context);
		setView(input);

		setButton(context.getString(R.string.done), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				String filename = input.getText().toString();
				File testfile = FileIO.createNewEmptyPictureFile(context, filename);
				if (testfile != null && testfile.exists()) {
					Log.w(PaintroidApplication.TAG, testfile + " already exists."); // TODO remove logging
					mFileActivity.startWarningOverwriteDialog(input.getText().toString());
				} else if (testfile != null) {
					Log.d(PaintroidApplication.TAG, testfile + " saved."); // TODO remove logging
					String value = input.getText().toString();
					mFileActivity.setSaveName(value);
				} else {
					Log.e(PaintroidApplication.TAG, "Cannot save file " + filename + "!");
				}

			}
		});
	}
}
