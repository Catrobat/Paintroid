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
 *   Foobar is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *   
 *   You should have received a copy of the GNU Affero General Public License
 *   along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import at.tugraz.ist.paintroid.FileActivity;
import at.tugraz.ist.paintroid.R;

public class DialogOverwriteFile extends AlertDialog {

	private FileActivity fileActivityClass;

	public DialogOverwriteFile(final Context context, final String filename) {
		super(context);
		fileActivityClass = (FileActivity) context;
		this.setTitle(R.string.dialog_overwrite_title);
		this.setMessage(context.getString(R.string.dialog_overwrite_text));
		this.setButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

				String value = filename;
				fileActivityClass.setSaveName(value);

				dialog.dismiss();
			}
		});

		this.setButton2(context.getString(R.string.dialog_overwrite_button_cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {

						dialog.dismiss();
					}
				});
	}
}
