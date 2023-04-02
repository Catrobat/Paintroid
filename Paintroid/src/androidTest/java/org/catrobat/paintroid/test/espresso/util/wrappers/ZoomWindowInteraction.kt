package org.catrobat.paintroid.test.espresso.util.wrappers

import android.view.View
import android.widget.RelativeLayout
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class ZoomWindowInteraction private constructor() : CustomViewInteraction(Espresso.onView(withId(R.id.pocketpaint_zoom_window_image))) {
    fun checkAlignment(verb: Int): ZoomWindowInteraction {
        check(ViewAssertions.matches(object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(view: View?): Boolean {
                val activity = MainActivityHelper.getMainActivityFromView(view)
                val layoutParams = activity.findViewById<View>(R.id.pocketpaint_zoom_window_image).layoutParams as RelativeLayout.LayoutParams
                val rulesFromLayout = layoutParams.getRule(verb)
                return rulesFromLayout == -1
            }

            override fun describeTo(description: Description) {
                description.appendText("The window's alignment")
            }
        }))
        return this
    }

    fun checkAlignmentBelowM(index: Int): ZoomWindowInteraction {
        check(ViewAssertions.matches(object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(view: View?): Boolean {
                val activity = MainActivityHelper.getMainActivityFromView(view)
                val layoutParams = activity.findViewById<View>(R.id.pocketpaint_zoom_window_image).layoutParams as RelativeLayout.LayoutParams
                val rulesFromLayout = layoutParams.rules
                return rulesFromLayout[index] == -1
            }

            override fun describeTo(description: Description) {
                description.appendText("The window's alignment")
            }
        }))
        return this
    }

    companion object {
        fun onZoomWindow(): ZoomWindowInteraction {
            return ZoomWindowInteraction()
        }
    }
}