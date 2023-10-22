package org.catrobat.paintroid.test.espresso.util.wrappers

import android.graphics.Bitmap
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class ZoomWindowInteraction private constructor() : CustomViewInteraction(Espresso.onView(withId(R.id.pocketpaint_zoom_window_image))) {
    fun checkPixelColor(
        @ColorInt expectedColor: Int,
        coordinateProvider: CoordinatesProvider,
        zoomWindowBitmap: Bitmap?
    ): ZoomWindowInteraction {
        check(ViewAssertions.matches(object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description) {
                description.appendText(
                    "Color in Zoom window at coordinates is " + Integer.toHexString(
                        expectedColor
                    )
                )
            }

            override fun matchesSafely(view: View?): Boolean {
                val coordinates = coordinateProvider.calculateCoordinates(view)
                val actualColor = zoomWindowBitmap?.getPixel(coordinates[0].toInt(), coordinates[1].toInt())
                return expectedColor == actualColor
            }
        }))
        return this
    }

    fun checkAlignment(verb: Int): ZoomWindowInteraction {
        check(ViewAssertions.matches(object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(view: View?): Boolean {
                val activity = MainActivityHelper.getMainActivityFromView(view!!)
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
                val activity = MainActivityHelper.getMainActivityFromView(view!!)
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
        fun onZoomWindow(): ZoomWindowInteraction = ZoomWindowInteraction()
    }
}
