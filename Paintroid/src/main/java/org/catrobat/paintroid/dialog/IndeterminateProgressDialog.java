/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
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

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;

public final class IndeterminateProgressDialog extends AlertDialog {

	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "IndeterminateProgressDialog has not been initialized. Call init() first!";

	private static Dialog instance;

	public IndeterminateProgressDialog(Context context) {
		super(context, R.style.CustomProgressDialog);
	}

	public static Dialog getInstance() {
		if (instance == null) {
			throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
		}
		return instance;
	}

	public static void init(MainActivity mainActivity) {
		instance = new IndeterminateProgressDialog(mainActivity);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.custom_progress_dialog);

		// Remove this section once AppCompat supports tinting Progressbars
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
			if (progressBar != null) {
				Drawable drawable = progressBar.getIndeterminateDrawable();
				drawable.setColorFilter(
						ContextCompat.getColor(getContext(), R.color.tools_text_color), PorterDuff.Mode.SRC_IN);
			}
		}

		setCancelable(false);
	}
}
