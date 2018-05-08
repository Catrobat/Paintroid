/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui.tools;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

public class NumberRangeFilter implements InputFilter {
	private static final String TAG = NumberRangeFilter.class.getSimpleName();
	private int min;

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	private int max;

	public NumberRangeFilter(int minVal, int maxVal) {
		this.min = minVal;
		this.max = maxVal;
	}

	@Override
	public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
		try {
			int input = Integer.parseInt(spanned.toString() + charSequence.toString());
			if (input <= max && input >= min) {
				return null;
			}
		} catch (NumberFormatException nfe) {
			Log.d(TAG, nfe.getLocalizedMessage());
		}
		return "";
	}
}
