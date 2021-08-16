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
package org.catrobat.paintroid.tools.implementation

import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Shader
import android.graphics.Shader.TileMode.REPEAT
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import org.catrobat.paintroid.R
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ContextCallback.NotificationDuration
import org.catrobat.paintroid.tools.ContextCallback.NotificationDuration.SHORT
import org.catrobat.paintroid.tools.ContextCallback.ScreenOrientation
import org.catrobat.paintroid.tools.ContextCallback.ScreenOrientation.LANDSCAPE
import org.catrobat.paintroid.tools.ContextCallback.ScreenOrientation.PORTRAIT
import org.catrobat.paintroid.tools.common.SCROLL_TOLERANCE_PERCENTAGE
import org.catrobat.paintroid.ui.ToastFactory

class DefaultContextCallback(private val context: Context) : ContextCallback {
    override val checkeredBitmapShader: Shader?

    init {
        val checkerboard =
            BitmapFactory.decodeResource(context.resources, R.drawable.pocketpaint_checkeredbg)
        checkeredBitmapShader = BitmapShader(checkerboard, REPEAT, REPEAT)
    }

    override fun showNotification(@StringRes resId: Int) {
        showNotificationWithDuration(resId, SHORT)
    }

    override fun showNotificationWithDuration(
        @StringRes resId: Int,
        duration: NotificationDuration
    ) {
        val toastDuration = if (duration == SHORT) LENGTH_SHORT else LENGTH_LONG
        ToastFactory.makeText(context, resId, toastDuration).show()
    }

    override val scrollTolerance: Int =
        (context.resources.displayMetrics.widthPixels * SCROLL_TOLERANCE_PERCENTAGE).toInt()

    override val orientation: ScreenOrientation
        get() {
            val orientation = context.resources.configuration.orientation
            return if (orientation == ORIENTATION_LANDSCAPE) LANDSCAPE else PORTRAIT
        }

    override fun getFont(@FontRes id: Int): Typeface? = ResourcesCompat.getFont(context, id)

    override val displayMetrics: DisplayMetrics = context.resources.displayMetrics

    @ColorInt
    override fun getColor(@ColorRes id: Int): Int = ContextCompat.getColor(context, id)

    override fun getDrawable(@DrawableRes resource: Int): Drawable? =
        AppCompatResources.getDrawable(context, resource)
}
