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

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.paintroid.R

class TopBarViewInteraction : CustomViewInteraction(Espresso.onView(withId(R.id.pocketpaint_layout_top_bar))) {
    fun onUndoButton(): ViewInteraction = Espresso.onView(withId(R.id.pocketpaint_btn_top_undo))

    fun onRedoButton(): ViewInteraction = Espresso.onView(withId(R.id.pocketpaint_btn_top_redo))

    fun onCheckmarkButton(): ViewInteraction = Espresso.onView(withId(R.id.pocketpaint_btn_top_checkmark))

    fun onPlusButton(): ViewInteraction = Espresso.onView(withId(R.id.pocketpaint_btn_top_plus))

    fun performUndo(): TopBarViewInteraction {
        onUndoButton()
                .perform(ViewActions.click())
        return this
    }

    fun performRedo(): TopBarViewInteraction {
        onRedoButton()
                .perform(ViewActions.click())
        return this
    }

    fun performClickCheckmark(): TopBarViewInteraction {
        onCheckmarkButton()
                .perform(ViewActions.click())
        return this
    }

    fun performClickPlus(): TopBarViewInteraction {
        onPlusButton()
                .perform(ViewActions.click())
        return this
    }

    fun performOpenMoreOptions(): TopBarViewInteraction {
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        return this
    }

    fun onHomeClicked(): TopBarViewInteraction {
        Espresso.pressBack()
        return this
    }

    companion object {
        fun onTopBarView(): TopBarViewInteraction = TopBarViewInteraction()
    }
}
