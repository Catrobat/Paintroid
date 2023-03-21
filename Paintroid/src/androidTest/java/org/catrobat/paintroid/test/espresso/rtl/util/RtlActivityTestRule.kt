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
package org.catrobat.paintroid.test.espresso.rtl.util

import android.app.Activity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.test.espresso.util.LanguageSupport
import java.util.*

class RtlActivityTestRule<T : Activity?>(activityClass: Class<T>?, private val language: String) :
    ActivityTestRule<T>(activityClass) {
    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        val locale = Locale(language)
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        LanguageSupport.setLocale(targetContext, locale)
    }

    override fun afterActivityFinished() {
        super.afterActivityFinished()
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        LanguageSupport.setLocale(targetContext, Locale("en"))
    }
}