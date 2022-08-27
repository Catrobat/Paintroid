/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http:></http:>//developer.catrobat.org/credits>)
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */

@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso.util

import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.VectorDrawable
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TableRow
import android.widget.TextView
import androidx.core.util.Preconditions
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

object UiMatcher {
    @JvmStatic
    fun atPosition(position: Int, itemMatcher: Matcher<View>): BoundedMatcher<View?, RecyclerView> {
        Preconditions.checkNotNull(itemMatcher)
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: // has no item on such position
                    return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    @JvmStatic
    fun hasTypeFace(typeface: Typeface): TypeSafeMatcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(view: View?): Boolean { return view is TextView && view.typeface === typeface }

            override fun describeTo(
                description: Description
            ) { description.appendText("the selected TextView doesn't have the TypeFace:$typeface") }
        }
    }

    @JvmStatic
    fun hasTablePosition(rowIndex: Int, columnIndex: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(
                description: Description
            ) { description.appendText("is child in cell @($rowIndex|$columnIndex)") }

            public override fun matchesSafely(view: View): Boolean {
                val tableRow = view.parent as? ViewGroup ?: return false
                if (tableRow.indexOfChild(view) != columnIndex) { return false }
                val tableLayout = tableRow.parent
                return if (tableLayout !is ViewGroup) {
                    false
                } else tableLayout.indexOfChild(tableRow as TableRow) == rowIndex
            }
        }
    }

    @JvmStatic
    fun withBackgroundColor(color: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun matchesSafely(view: View): Boolean {
                val background = view.background ?: return false
                if (background is ColorDrawable) {
                    return color == background.color
                } else if (background is LayerDrawable) {
                    val drawable = background.getDrawable(0)
                    return (
                        drawable is ColorDrawable &&
                            color == drawable.color
                        )
                }
                return false
            }

            override fun describeTo(
                description: Description
            ) { description.appendText("with background color: $color") }
        }
    }

    @JvmStatic
    fun withTextColor(color: Int): TypeSafeMatcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(view: View?): Boolean {
                if (view !is TextView) { return false }
                val textColor = view.currentTextColor
                return textColor == color
            }

            override fun describeTo(description: Description) { description.appendText("with text color: $color") }
        }
    }

    @JvmStatic
    fun withProgress(progress: Int): TypeSafeMatcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(view: View?): Boolean {
                if (view !is SeekBar) { return false }
                val seekbarProgress = view.progress
                return seekbarProgress == progress
            }

            override fun describeTo(description: Description) { description.appendText("with progress: $progress") }
        }
    }

    @JvmStatic
    fun withBackground(resourceId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var resourceName: String? = null
            override fun matchesSafely(target: View): Boolean {
                val resources = target.context.resources
                resourceName = resources.getResourceEntryName(resourceId)
                if (target !is ImageView) { return false }
                val expectedDrawable = resources.getDrawable(resourceId)
                val targetDrawable = target.getBackground()
                if (expectedDrawable == null || targetDrawable == null) { return false }
                val expectedBitmap = (expectedDrawable as BitmapDrawable).bitmap
                if (targetDrawable is BitmapDrawable) {
                    val bitmap = targetDrawable.bitmap
                    return bitmap.sameAs(expectedBitmap)
                } else if (targetDrawable is StateListDrawable) {
                    val bitmap = (targetDrawable.getCurrent() as BitmapDrawable).bitmap
                    return bitmap.sameAs(expectedBitmap)
                }
                return false
            }

            override fun describeTo(description: Description) {
                description.appendText("with drawable from resource id: ")
                description.appendValue(resourceId)
                if (resourceName != null) {
                    description.appendText("[")
                    description.appendText(resourceName)
                    description.appendText("]")
                }
            }
        }
    }

    @JvmStatic
    fun withDrawable(resourceId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var resourceName: String? = null
            override fun matchesSafely(target: View): Boolean {
                val resources = target.context.resources
                resourceName = resources.getResourceEntryName(resourceId)
                if (target !is ImageView) { return false }
                val expectedDrawable = resources.getDrawable(resourceId)
                val targetDrawable = target.drawable
                if (expectedDrawable == null || targetDrawable == null) { return false }
                val expectedBitmap: Bitmap
                if (targetDrawable is BitmapDrawable) {
                    val targetBitmap = targetDrawable.bitmap
                    expectedBitmap = (expectedDrawable as BitmapDrawable).bitmap
                    return targetBitmap.sameAs(expectedBitmap)
                } else if (targetDrawable is VectorDrawable || targetDrawable is VectorDrawableCompat) {
                    val targetBitmap = vectorToBitmap(expectedDrawable as VectorDrawable)
                    expectedBitmap = vectorToBitmap(expectedDrawable)
                    return targetBitmap.sameAs(expectedBitmap)
                } else if (targetDrawable is StateListDrawable) {
                    val targetBitmap = vectorToBitmap(expectedDrawable as VectorDrawable)
                    expectedBitmap = vectorToBitmap(expectedDrawable)
                    return targetBitmap.sameAs(expectedBitmap)
                }
                return false
            }

            override fun describeTo(description: Description) {
                description.appendText("with drawable from resource id: ")
                description.appendValue(resourceId)
                if (resourceName != null) {
                    description.appendText("[")
                    description.appendText(resourceName)
                    description.appendText("]")
                }
            }

            private fun vectorToBitmap(vectorDrawable: VectorDrawable): Bitmap {
                return Bitmap.createBitmap(
                    vectorDrawable.intrinsicWidth,
                    vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
                )
            }
        }
    }

    @JvmStatic
    fun withAdaptedData(resourceId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) { description.appendText("with class name: ") }

            public override fun matchesSafely(view: View): Boolean {
                val resourceName: String
                if (view !is AdapterView<*>) { return false }
                val resources = view.getContext().resources
                resourceName = resources.getString(resourceId)
                val adapter = view.adapter
                for (i in 0 until adapter.count) {
                    if (resourceName == (adapter.getItem(i) as MenuItem).title.toString()) { return true }
                }
                return false
            }
        }
    }
}
