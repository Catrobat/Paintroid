/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.dialog;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import at.tugraz.ist.paintroid.FileActivity;
import at.tugraz.ist.paintroid.R;

/**
 * This dialog displays a text input field for
 * the name of saved file.
 * 
 * Status: refactored 20.02.2011
 * 
 * @author PaintroidTeam
 * @version 0.6.4b
 */
public class DialogSaveFileName extends AlertDialog {

	private FileActivity fileActivityClass;

	public DialogSaveFileName(final Context context) {
		super(context);
		fileActivityClass = (FileActivity) context;
		this.setTitle(R.string.dialog_save_title);
		this.setMessage(context.getString(R.string.dialog_save_text));

		// Set an EditText view to get user input    
		final EditText input = new EditText(context);
		this.setView(input);

		this.setButton(context.getString(R.string.dialog_save_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				File file = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/"
						+ input.getText().toString() + ".png");
				Log.d("PAINTROID", "FILE: " + String.valueOf(file.exists()));

				if (file.exists()) {
					Log.d("PAINTROID", "File already exists");
					dialog.dismiss();
					fileActivityClass.startWarningOverwriteDialog(input.getText().toString());
				} else {
					Log.d("PAINTROID", "File saved new");
					String value = input.getText().toString();
					fileActivityClass.setSaveName(value);
				}

			}
		});
	}
}
