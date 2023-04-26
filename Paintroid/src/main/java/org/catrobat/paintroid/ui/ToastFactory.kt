/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.paintroid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

@SuppressLint("ShowToast")
object ToastFactory {
    private lateinit var currentToast: Toast

    private fun cancelToast() {
        if (this::currentToast.isInitialized) {
            currentToast.cancel()
        }
    }

    @JvmStatic
    fun makeText(context: Context, @StringRes resId: Int, duration: Int): Toast {
        cancelToast()
        currentToast = Toast.makeText(context, resId, duration)
        return currentToast
    }

    @JvmStatic
    fun makeText(context: Context, msg: String, duration: Int): Toast {
        cancelToast()
        currentToast = Toast.makeText(context, msg, duration)
        return currentToast
    }
}
