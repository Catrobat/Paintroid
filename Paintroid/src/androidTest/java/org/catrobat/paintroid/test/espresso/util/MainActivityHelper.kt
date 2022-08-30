/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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

@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso.util

import android.content.ContextWrapper
import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.WindowInsets
import org.catrobat.paintroid.MainActivity

data class MainActivityHelper(private val activity: MainActivity) {
    private val displaySize: Point
        get() {
            val displaySize = Point()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowManager = activity.windowManager
                val windowMetrics = windowManager.currentWindowMetrics
                val windowInsets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(
                    WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout()
                )
                val insetsWidth = (windowInsets.right + windowInsets.left).toFloat()
                val insetsHeight = (windowInsets.top + windowInsets.bottom).toFloat()
                val b = windowMetrics.bounds
                val width = b.width() - insetsWidth
                val height = b.height() - insetsHeight
                displaySize.x = width.toInt()
                displaySize.y = height.toInt()
            } else {
                activity.windowManager.defaultDisplay.getSize(displaySize)
            }
            return displaySize
        }
    val displayWidth: Int
        get() = displaySize.x
    val displayHeight: Int
        get() = displaySize.y
    var screenOrientation: Int
        get() = activity.requestedOrientation
        set(orientation) { activity.requestedOrientation = orientation }

    companion object {
        @JvmStatic
        fun getMainActivityFromView(view: View): MainActivity {
            var context = view.context
            while (context is ContextWrapper) {
                if (context is MainActivity) { return context }
                context = context.baseContext
            }
            throw NullPointerException("View context does not implement MainActivity")
        }
    }
}
