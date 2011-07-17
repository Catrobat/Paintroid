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

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import at.tugraz.ist.paintroid.R;

public class DialogAbout extends Dialog implements OnClickListener {

	public DialogAbout(Context context) {
		super(context);
		init();
	}

	private void init() {

		setContentView(R.layout.dialog_about);
		setTitle(R.string.about_title);
		setCancelable(true);

		Button button = (Button) findViewById(R.id.about_btn_Cancel);
		button.setOnClickListener(this);

		button = (Button) findViewById(R.id.about_btn_License);
		button.setOnClickListener(this);

		show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.about_btn_License:
				DialogLicense licenseDialog = new DialogLicense(this.getContext());
				licenseDialog.show();
				break;
			case R.id.about_btn_Cancel:
				// close dialog
				this.cancel();
				break;
		}
	}
}
