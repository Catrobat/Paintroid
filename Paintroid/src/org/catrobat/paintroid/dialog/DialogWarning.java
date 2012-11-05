/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
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

import org.catrobat.paintroid.R;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DialogWarning extends BaseDialog implements OnClickListener {

	public DialogWarning(Context context) {
		super(context);
		init();
	}

	private void init() {
		setContentView(R.layout.dialog_warning);
		setTitle(R.string.warning_title);
		setCancelable(true);

		setText(R.string.warning_content);

		Button button = (Button) findViewById(R.id.warning_btn_Cancel);
		button.setText(R.string.cancel);
		button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.warning_btn_Cancel) {
			this.cancel(); // Close Dialog
		}
	}

	public void setText(int id) {
		TextView text = (TextView) findViewById(R.id.warning_tview_Text);
		text.setText(id);
	}
}
