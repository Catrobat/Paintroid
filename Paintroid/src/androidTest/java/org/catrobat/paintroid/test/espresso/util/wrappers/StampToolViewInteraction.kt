package org.catrobat.paintroid.test.espresso.util.wrappers

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.paintroid.R

class StampToolViewInteraction(viewInteraction: ViewInteraction) : CustomViewInteraction(viewInteraction) {

    companion object {
        fun onStampToolViewInteraction(): StampToolViewInteraction {
            return StampToolViewInteraction(onView(withId(R.id.pocketpaint_main_tool_options)))
        }
    }

    fun performCopy(): StampToolViewInteraction {
        onView(withId(R.id.action_copy))
                .perform(click())
        return this
    }

    fun performCut(): StampToolViewInteraction {
        onView(withId(R.id.action_cut))
                .perform(click())
        return this
    }

    fun performPaste(): StampToolViewInteraction {
        onView(withId(R.id.action_paste))
                .perform(click())
        return this
    }
}
