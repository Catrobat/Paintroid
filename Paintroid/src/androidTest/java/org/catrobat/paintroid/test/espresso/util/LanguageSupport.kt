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
package org.catrobat.paintroid.test.espresso.util

import android.content.Context
import android.os.Build
import java.lang.IllegalArgumentException
import java.util.*

class LanguageSupport private constructor() {
    init {
        throw IllegalArgumentException()
    }

    companion object {
        fun setLocale(context: Context, locale: Locale?) {
            if (Build.VERSION.SDK_INT >= 24) {
                Locale.setDefault(Locale.Category.DISPLAY, locale)
            } else {
                Locale.setDefault(locale)
            }
            val resources = context.resources
            val conf = resources.configuration
            conf.setLocale(locale)
            resources.updateConfiguration(conf, resources.displayMetrics)
        }
    }
}