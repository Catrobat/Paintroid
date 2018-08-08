/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import org.catrobat.paintroid.R;

public class IndeterminateProgressDialog extends AppCompatDialogFragment {

	public static IndeterminateProgressDialog newInstance() {
		return new IndeterminateProgressDialog();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(false);
	}

	@NonNull
	@Override
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(R.layout.pocketpaint_layout_indeterminate, null);

		// Remove this section once AppCompat supports tinting Progressbars
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			ProgressBar progressBar = layout.findViewById(R.id.pocketpaint_progress_bar);
			if (progressBar != null) {
				Drawable drawable = progressBar.getIndeterminateDrawable();
				int toolTextColor = ContextCompat.getColor(getContext(), R.color.pocketpaint_colorAccent);
				drawable.setColorFilter(toolTextColor, PorterDuff.Mode.SRC_IN);
			}
		}
		Dialog dialog = new AppCompatDialog(getContext(), R.style.PocketPaintProgressDialog);
		dialog.setContentView(layout);
		return dialog;
	}
}
