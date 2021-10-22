/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.paintroid.tools.helper

import android.text.Spanned
import android.util.Log
import org.catrobat.paintroid.ui.tools.NumberRangeFilter

class DefaultNumberRangeFilter(private val min: Int, override var max: Int) : NumberRangeFilter {
    companion object {
        private val TAG = DefaultNumberRangeFilter::class.java.simpleName
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toInt()
            if (input in min..max) {
                return null
            }
        } catch (nfe: NumberFormatException) {
            nfe.localizedMessage?.let {
                Log.d(TAG, it)
            }
        }
        return ""
    }
}
