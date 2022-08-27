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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.BottomNavigationViewInteraction.Companion.onBottomNavigationView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class LayerMenuViewInteraction private constructor() :
    CustomViewInteraction(Espresso.onView(withId(R.id.pocketpaint_nav_view_layer))) {
    fun onButtonAdd(): ViewInteraction = Espresso.onView(withId(R.id.pocketpaint_layer_side_nav_button_add))

    fun onButtonDelete(): ViewInteraction = Espresso.onView(withId(R.id.pocketpaint_layer_side_nav_button_delete))

    private fun onLayerList(): ViewInteraction = Espresso.onView(withId(R.id.pocketpaint_layer_side_nav_list))

    fun checkLayerCount(count: Int): LayerMenuViewInteraction {
        onLayerList().check(UiInteractions.assertRecyclerViewCount(count))
        return this
    }

    private fun onLayerAt(listPosition: Int): ViewInteraction {
        return Espresso.onView(withId(R.id.pocketpaint_layer_side_nav_list)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                listPosition,
                ViewActions.click()
            )
        )
    }

    fun performOpen(): LayerMenuViewInteraction {
        onBottomNavigationView().onLayersClicked()
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        return this
    }

    fun performClose(): LayerMenuViewInteraction {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.pocketpaint_drawer_layout))
            .perform(DrawerActions.close(Gravity.END))
        return this
    }

    fun performSelectLayer(listPosition: Int): LayerMenuViewInteraction {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onLayerAt(listPosition).perform(ViewActions.click())
        return this
    }

    fun performStartDragging(listPosition: Int): LayerMenuViewInteraction {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onLayerAt(listPosition)
        Espresso.onView(withIndex(withId(R.id.pocketpaint_layer_drag_handle), listPosition))
            .perform(ViewActions.click())
        return this
    }

    fun performAddLayer(): LayerMenuViewInteraction {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onButtonAdd().perform(ViewActions.click())
        return this
    }

    fun performDeleteLayer(): LayerMenuViewInteraction {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onButtonDelete().perform(ViewActions.click())
        return this
    }

    fun performToggleLayerVisibility(position: Int): LayerMenuViewInteraction {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withIndex(withId(R.id.pocketpaint_checkbox_layer), position))
            .perform(ViewActions.click())
        return this
    }

    fun checkLayerAtPositionHasTopLeftPixelWithColor(
        listPosition: Int,
        @ColorInt expectedColor: Int
    ): LayerMenuViewInteraction {
        Espresso.onView(withIndex(withId(R.id.pocketpaint_item_layer_image), listPosition))
            .check(
                ViewAssertions.matches(object : TypeSafeMatcher<View>() {
                    override fun describeTo(description: Description) {
                        description.appendText(
                            "Color at coordinates is " + Integer.toHexString(
                                expectedColor
                            )
                        )
                    }

                    override fun matchesSafely(view: View): Boolean {
                        val bitmap = getBitmap((view as ImageView).drawable)
                        val actualColor = bitmap.getPixel(0, 0)
                        return actualColor == expectedColor
                    }
                })
            )
        return this
    }

    private fun getBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    companion object {
        fun onLayerMenuView(): LayerMenuViewInteraction = LayerMenuViewInteraction()

        fun withIndex(matcher: Matcher<View?>, index: Int): TypeSafeMatcher<View?> {
            return object : TypeSafeMatcher<View?>() {
                var currentIndex = 0
                override fun describeTo(description: Description) {
                    description.appendText("with index: ")
                    description.appendValue(index)
                    matcher.describeTo(description)
                }

                public override fun matchesSafely(
                    view: View?
                ): Boolean = matcher.matches(view) && currentIndex++ == index
            }
        }
    }
}
