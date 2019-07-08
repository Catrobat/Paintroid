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
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import org.catrobat.paintroid.R;

public final class IndeterminateProgressDialog {
	private IndeterminateProgressDialog() {
	}

	@SuppressLint("InflateParams")
	public static AlertDialog newInstance(Context context) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.pocketpaint_layout_indeterminate, null);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			ProgressBar progressBar = layout.findViewById(R.id.pocketpaint_progress_bar);
			if (progressBar != null) {
				int accentColor = getAccentColor(context);
				Drawable drawable = progressBar.getIndeterminateDrawable();
				drawable.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
			}
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.PocketPaintProgressDialog);
		builder.setCancelable(false);
		builder.setView(R.layout.pocketpaint_layout_indeterminate);
		return builder.create();
	}

	private static int getAccentColor(Context context) {
		final TypedValue value = new TypedValue();
		Resources.Theme theme = context.getTheme();
		theme.resolveAttribute(R.attr.colorAccent, value, true);
		return value.data;
	}
}
