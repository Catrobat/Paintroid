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
import org.catrobat.paintroid.tools.ToolType;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources.NotFoundException;

public class DialogHelp extends AlertDialog implements OnClickListener {

	private ToolType mToolType;

	public DialogHelp(Context context, ToolType toolType) {
		super(context, THEME_HOLO_DARK);
		mToolType = toolType;
		init();
	}

	/**
	 * Show the dialog
	 * 
	 */
	private void init() {
		setTitle(mToolType.getNameResource());
		setCancelable(true);

		CharSequence message;

		try {
			message = getContext().getText(mToolType.getHelpTextResource());
		} catch (NotFoundException ex) {
			message = "";
		}

		setMessage(message);
		setButton(BUTTON_NEUTRAL, getContext().getText(R.string.help_done),
				this);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		cancel();
	}
}
