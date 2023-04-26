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
package org.catrobat.paintroid.tools

import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes

interface ContextCallback {
    val scrollTolerance: Int
    val orientation: ScreenOrientation?
    val displayMetrics: DisplayMetrics
    val checkeredBitmapShader: Shader?

    fun showNotification(@StringRes resId: Int)

    fun showNotificationWithDuration(@StringRes resId: Int, duration: NotificationDuration)

    fun getFont(@FontRes id: Int): Typeface?

    @ColorInt
    fun getColor(@ColorRes id: Int): Int

    fun getDrawable(@DrawableRes resource: Int): Drawable?

    enum class ScreenOrientation {
        PORTRAIT, LANDSCAPE
    }

    enum class NotificationDuration {
        SHORT, LONG
    }
}
