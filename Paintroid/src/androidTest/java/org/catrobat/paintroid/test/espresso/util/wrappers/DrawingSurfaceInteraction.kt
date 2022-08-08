/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class DrawingSurfaceInteraction private constructor() :
    CustomViewInteraction(Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_drawing_surface_view))) {
    fun checkPixelColor(
        @ColorInt expectedColor: Int,
        coordinateProvider: CoordinatesProvider
    ): DrawingSurfaceInteraction {
        check(ViewAssertions.matches(object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description) {
                description.appendText(
                    "Color at coordinates is " + Integer.toHexString(expectedColor)
                )
            }

            override fun matchesSafely(view: View?): Boolean {
                val activity = MainActivityHelper.getMainActivityFromView(view)
                val currentLayer = activity.layerModel.currentLayer
                val coordinates = coordinateProvider.calculateCoordinates(view)
                val actualColor = currentLayer?.bitmap?.getPixel(
                    coordinates[0].toInt(), coordinates[1].toInt()
                )
                return expectedColor == actualColor
            }
        }))
        return this
    }

    fun checkPixelColor(
        @ColorInt expectedColor: Int,
        x: Float,
        y: Float
    ): DrawingSurfaceInteraction {
        check(ViewAssertions.matches(object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description) {
                description.appendText(
                    "Color at coordinates is " + Integer.toHexString(expectedColor)
                )
            }

            override fun matchesSafely(view: View?): Boolean {
                val activity = MainActivityHelper.getMainActivityFromView(view)
                val currentLayer = activity.layerModel.currentLayer
                val actualColor = currentLayer?.bitmap?.getPixel(x.toInt(), y.toInt())
                return expectedColor == actualColor
            }
        }))
        return this
    }

    fun checkPixelColorResource(
        @ColorRes expectedColorRes: Int,
        coordinateProvider: CoordinatesProvider
    ): DrawingSurfaceInteraction {
        val expectedColor = ContextCompat.getColor(
            InstrumentationRegistry.getInstrumentation().targetContext,
            expectedColorRes
        )
        return checkPixelColor(expectedColor, coordinateProvider)
    }

    companion object {
        @JvmStatic
		fun onDrawingSurfaceView(): DrawingSurfaceInteraction { return DrawingSurfaceInteraction() }
    }
}
