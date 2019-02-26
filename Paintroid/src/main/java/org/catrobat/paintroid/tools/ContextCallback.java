package org.catrobat.paintroid.tools;

import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.FontRes;
import android.support.annotation.StringRes;
import android.util.DisplayMetrics;

public interface ContextCallback {
	void showNotification(@StringRes int resId);

	void showNotification(@StringRes int resId, NotificationDuration duration);

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
