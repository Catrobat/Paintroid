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

package org.catrobat.paintroid.tools;

import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;
import androidx.annotation.StringRes;

public interface ContextCallback {
	void showNotification(@StringRes int resId);

	void showNotification(@StringRes int resId, NotificationDuration duration);

	int getScrollTolerance();

	ScreenOrientation getOrientation();

	Typeface getFont(@FontRes int id);

	DisplayMetrics getDisplayMetrics();

	@ColorInt
	int getColor(@ColorRes int id);

	Shader getCheckeredBitmapShader();

	Drawable getDrawable(@DrawableRes int resource);

	enum ScreenOrientation {
		PORTRAIT,
		LANDSCAPE
	}

	enum NotificationDuration {
		SHORT,
		LONG
	}
}
