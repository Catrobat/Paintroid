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

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;

import android.content.Context;
import android.view.Window;

public final class IndeterminateProgressDialog extends BaseDialog {

	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "IndeterminateProgressDialog has not been initialized. Call init() first!";

	private static IndeterminateProgressDialog instance;

	private IndeterminateProgressDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_progress_dialog);
		// setIndeterminate(true);
		// setProgressStyle(ProgressDialog.STYLE_SPINNER);
		setCancelable(false);
	}

	public static IndeterminateProgressDialog getInstance() {
		if (instance == null) {
			throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
		}
		return instance;
	}

	public static void init(MainActivity mainActivity) {
		instance = new IndeterminateProgressDialog(mainActivity);
	}
}
