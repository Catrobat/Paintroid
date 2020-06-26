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

package org.catrobat.paintroid.tools.implementation;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.widget.Toast;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.common.Constants;
import org.catrobat.paintroid.ui.ToastFactory;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

public class DefaultContextCallback implements ContextCallback {
	private Shader checkeredBitmapShader;
	private Context context;

	public DefaultContextCallback(Context context) {
		this.context = context;

		Resources resources = context.getResources();
		Bitmap checkerboard = BitmapFactory.decodeResource(resources, R.drawable.pocketpaint_checkeredbg);
		checkeredBitmapShader = new BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
	}

	@Override
	public void showNotification(@StringRes int resId) {
		showNotification(resId, NotificationDuration.SHORT);
	}

	@Override
	public void showNotification(@StringRes int resId, NotificationDuration duration) {
		int toastDuration = duration == NotificationDuration.SHORT ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
		ToastFactory.makeText(context, resId, toastDuration).show();
	}

	@Override
	public int getScrollTolerance() {
		return (int) (context.getResources().getDisplayMetrics().widthPixels
				* Constants.SCROLL_TOLERANCE_PERCENTAGE);
	}

	@Override
	public ScreenOrientation getOrientation() {
		int orientation = context.getResources().getConfiguration().orientation;
		return orientation == Configuration.ORIENTATION_LANDSCAPE ? ScreenOrientation.LANDSCAPE : ScreenOrientation.PORTRAIT;
	}

	@Override
	public Typeface getFont(@FontRes int id) {
		return ResourcesCompat.getFont(context, id);
	}

	@Override
	public DisplayMetrics getDisplayMetrics() {
		return context.getResources().getDisplayMetrics();
	}

	@ColorInt
	@Override
	public int getColor(@ColorRes int id) {
		return ContextCompat.getColor(context, id);
	}

	@Override
	public Shader getCheckeredBitmapShader() {
		return checkeredBitmapShader;
	}

	@Override
	public Drawable getDrawable(@DrawableRes int resource) {
		return AppCompatResources.getDrawable(context, resource);
	}
}
