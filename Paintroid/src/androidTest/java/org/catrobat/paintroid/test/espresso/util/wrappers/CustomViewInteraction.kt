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
package org.catrobat.paintroid.test.espresso.util.wrappers

import androidx.test.espresso.FailureHandler
import androidx.test.espresso.Root
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import org.hamcrest.Matcher

open class CustomViewInteraction protected constructor(private var viewInteraction: ViewInteraction) {
    fun perform(vararg viewActions: ViewAction?): ViewInteraction = viewInteraction.perform(*viewActions)

    fun withFailureHandler(var1: FailureHandler?): ViewInteraction = viewInteraction.withFailureHandler(var1)

    fun inRoot(var1: Matcher<Root?>?): ViewInteraction = viewInteraction.inRoot(var1)

    fun noActivity(): ViewInteraction = viewInteraction.noActivity()

    fun check(viewAssert: ViewAssertion?): ViewInteraction = viewInteraction.check(viewAssert)
    }
