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

package org.catrobat.paintroid.tools.helper;

import android.text.Spanned;
import android.util.Log;

import org.catrobat.paintroid.ui.tools.NumberRangeFilter;

public class DefaultNumberRangeFilter implements NumberRangeFilter {
	private static final String TAG = DefaultNumberRangeFilter.class.getSimpleName();
	private int min;

	@Override
	public int getMax() {
		return max;
	}

	@Override
	public void setMax(int max) {
		this.max = max;
	}

	private int max;

	public DefaultNumberRangeFilter(int minVal, int maxVal) {
		this.min = minVal;
		this.max = maxVal;
	}

	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		try {
			int input = Integer.parseInt(dest.toString() + source.toString());
			if (input <= max && input >= min) {
				return null;
			}
		} catch (NumberFormatException nfe) {
			Log.d(TAG, nfe.getLocalizedMessage());
		}
		return "";
	}
}
