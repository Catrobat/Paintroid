/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.util.wrappers

import android.widget.Button
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import org.catrobat.paintroid.R
import org.hamcrest.Matchers

class ConfirmQuitDialogInteraction private constructor() : CustomViewInteraction(
    Espresso.onView(ViewMatchers.withText(R.string.closing_security_question)).inRoot(RootMatchers.isDialog())
) {
    fun onPositiveButton(): ViewInteraction {
        return Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(android.R.id.button1),
                ViewMatchers.withText(R.string.save_button_text),
                ViewMatchers.isAssignableFrom(Button::class.java)
            )
        )
    }

    fun checkPositiveButton(matcher: ViewAssertion?): ConfirmQuitDialogInteraction {
        onPositiveButton().check(matcher)
        return this
    }

    fun onNegativeButton(): ViewInteraction {
        return Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(android.R.id.button2),
                ViewMatchers.withText(R.string.discard_button_text),
                ViewMatchers.isAssignableFrom(Button::class.java)
            )
        )
    }

    fun checkNegativeButton(matcher: ViewAssertion?): ConfirmQuitDialogInteraction {
        onNegativeButton().check(matcher)
        return this
    }

    fun checkNeutralButton(matcher: ViewAssertion?): ConfirmQuitDialogInteraction {
        Espresso.onView(ViewMatchers.withId(android.R.id.button3)).check(matcher)
        return this
    }

    fun checkMessage(matcher: ViewAssertion?): ConfirmQuitDialogInteraction {
        Espresso.onView(ViewMatchers.withText(R.string.closing_security_question)).check(matcher)
        return this
    }

    fun checkTitle(matcher: ViewAssertion?): ConfirmQuitDialogInteraction {
        Espresso.onView(ViewMatchers.withText(R.string.closing_security_question_title)).check(matcher)
        return this
    }

    companion object {
        @JvmStatic
        fun onConfirmQuitDialog(): ConfirmQuitDialogInteraction { return ConfirmQuitDialogInteraction() }
    }
}
