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
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher

class DrawingSurfaceInteraction private constructor() :
    CustomViewInteraction(Espresso.onView(withId(R.id.pocketpaint_drawing_surface_view))) {
    fun checkPixelColor(
        @ColorInt expectedColor: Int,
        coordinateProvider: CoordinatesProvider
    ): DrawingSurfaceInteraction {
        check(
            ViewAssertions.matches(object : TypeSafeMatcher<View?>() {
                override fun describeTo(description: Description) {
                    description.appendText(
                        "Color at coordinates is " + Integer.toHexString(
                            expectedColor
                        )
                    )
                }

                override fun matchesSafely(view: View?): Boolean {
                    val activity = MainActivityHelper.getMainActivityFromView(view)
                    val currentLayer = activity.layerModel.currentLayer
                    val coordinates = coordinateProvider.calculateCoordinates(view)
                    val actualColor =
                        currentLayer?.bitmap?.getPixel(coordinates[0].toInt(), coordinates[1].toInt())
                    return expectedColor == actualColor
                }
            })
        )
        return this
    }

    fun checkPixelColor(
        @ColorInt expectedColor: Int,
        x: Float,
        y: Float
    ): DrawingSurfaceInteraction {
        check(
            ViewAssertions.matches(object : TypeSafeMatcher<View?>() {
                override fun describeTo(description: Description) {
                    description.appendText(
                        "Color at coordinates is " + Integer.toHexString(
                            expectedColor
                        )
                    )
                }

                override fun matchesSafely(view: View?): Boolean {
                    val activity = MainActivityHelper.getMainActivityFromView(view)
                    val currentLayer = activity.layerModel.currentLayer
                    val actualColor = currentLayer?.bitmap?.getPixel(x.toInt(), y.toInt())
                    return expectedColor == actualColor
                }
            })
        )
        return this
    }

    fun checkBitmapDimension(expectedWidth: Int, expectedHeight: Int): DrawingSurfaceInteraction {
        check(
            ViewAssertions.matches(object : TypeSafeMatcher<View?>() {
                override fun describeTo(description: Description) {
                    description.appendText(
                        "Bitmap has is size " +
                            expectedWidth + "x and " +
                            expectedHeight + "y"
                    )
                }

                override fun matchesSafely(view: View?): Boolean {
                    val activity = MainActivityHelper.getMainActivityFromView(view)
                    val layerModel = activity.layerModel
                    val bitmap = layerModel.currentLayer!!.bitmap
                    return expectedWidth == bitmap!!.width && expectedHeight == bitmap.height
                }
            })
        )
        return this
    }

    fun checkLayerDimensions(expectedWidth: Int, expectedHeight: Int): DrawingSurfaceInteraction {
        checkThatLayerDimensions(Matchers.`is`(expectedWidth), Matchers.`is`(expectedHeight))
        return this
    }

    private fun checkThatLayerDimensions(
        matchesWidth: Matcher<Int>,
        matchesHeight: Matcher<Int>
    ): DrawingSurfaceInteraction {
        check(
            ViewAssertions.matches(object : TypeSafeMatcher<View?>() {
                override fun describeTo(description: Description) {
                    description.appendText("All layers have expected size")
                }

                override fun matchesSafely(view: View?): Boolean {
                    val activity = MainActivityHelper.getMainActivityFromView(view)
                    val layerModel = activity.layerModel
                    for (layer: LayerContracts.Layer in layerModel.layers) {
                        val bitmap = layer.bitmap
                        if (!matchesWidth.matches(bitmap!!.width) || !matchesHeight.matches(bitmap.height)) {
                            return false
                        }
                    }
                    return true
                }
            })
        )
        return this
    }

    companion object {
        fun onDrawingSurfaceView(): DrawingSurfaceInteraction = DrawingSurfaceInteraction()
    }
}
