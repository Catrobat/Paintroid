/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.intro.helper;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Locale;

public final class WelcomeActivityHelper {

	private WelcomeActivityHelper() {
	}

	private static int getDpFromDimension(int dimension, DisplayMetrics metrics) {
		return (int) (dimension / metrics.density);
	}

	private static int getDpFromInt(float dimension, DisplayMetrics metrics) {
		return (int) (dimension / metrics.density);
	}

	public static int getSpFromDimension(int dimension, DisplayMetrics metrics) {
		return (int) (dimension / metrics.scaledDensity);
	}

	private static boolean defaultLocaleIsRTL() {
		Locale locale = Locale.getDefault();
		if (locale.toString().isEmpty()) {
			return false;
		}
		final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
		return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT
				|| directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
	}

	public static boolean isRTL(Context context) {
		final int layoutDirection = context.getResources().getConfiguration().getLayoutDirection();
		boolean layoutDirectionIsRTL = (layoutDirection == View.LAYOUT_DIRECTION_RTL);
		return layoutDirectionIsRTL || defaultLocaleIsRTL();
	}

	public static void reverseArray(int[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			int temp = array[i];
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}
	}

	public static int calculateTapTargetRadius(int heightInt, DisplayMetrics metrics, int radiusOffset) {
		return getDpFromInt(heightInt, metrics) / 2 - radiusOffset;
	}

	public static int calculateTapTargetRadius(float heightDim, DisplayMetrics metrics, int radiusOffset) {
		return getDpFromDimension((int) heightDim, metrics) / 2 - radiusOffset;
	}
}
