/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.paintroid.ui;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

public class KeyboardListener {
	private static final int HEIGHT_THRESHOLD = 300;
	private boolean isSoftKeyboardVisible;

	public KeyboardListener(final View activityRootView) {
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int heightDifference = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
				DisplayMetrics displayMetrics = activityRootView.getResources().getDisplayMetrics();
				isSoftKeyboardVisible = heightDifference > dpToPx(HEIGHT_THRESHOLD, displayMetrics);
			}
		});
	}

	private static float dpToPx(int dpValue, DisplayMetrics displayMetrics) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, displayMetrics);
	}

	public boolean isSoftKeyboardVisible() {
		return isSoftKeyboardVisible;
	}
}
