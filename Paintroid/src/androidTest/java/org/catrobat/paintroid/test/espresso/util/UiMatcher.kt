/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.paintroid.test.espresso.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.VectorDrawable
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.WindowManager
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TableRow
import android.widget.TextView
import androidx.core.util.Preconditions
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.Root
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.util.HumanReadables
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert

object UiMatcher {

    private fun vectorToBitmap(vectorDrawable: VectorDrawable): Bitmap {
        return Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
    }

    fun atPosition(position: Int, itemMatcher: Matcher<View>): BoundedMatcher<View?, RecyclerView> {
        Preconditions.checkNotNull(itemMatcher)
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder: RecyclerView.ViewHolder? =
                    view.findViewHolderForAdapterPosition(position)
                if (viewHolder == null) {
                    // has no item on such position
                    return false
                }
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    fun withIndex(matcher: Matcher<View?>, index: Int): TypeSafeMatcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            var currentIndex: Int = 0
            override fun describeTo(description: Description) {
                description.appendText("with index: ")
                description.appendValue(index)
                matcher.describeTo(description)
            }

            public override fun matchesSafely(view: View?): Boolean {
                return matcher.matches(view) && currentIndex++ == index
            }
        }
    }

    fun hasTypeFace(typeface: Typeface): TypeSafeMatcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(view: View?): Boolean {
                return view is TextView && view.typeface === typeface
            }

            override fun describeTo(description: Description) {
                description.appendText("the selected TextView doesn't have the TypeFace:$typeface")
            }
        }
    }

    fun hasChildPosition(position: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("is child #$position")
            }

            public override fun matchesSafely(view: View): Boolean {
                val viewParent: ViewParent = view.parent
                if (!(viewParent is ViewGroup)) {
                    return false
                }
                return viewParent.indexOfChild(view) == position
            }
        }
    }

    fun hasTablePosition(rowIndex: Int, columnIndex: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("is child in cell @($rowIndex|$columnIndex)")
            }

            public override fun matchesSafely(view: View): Boolean {
                val tableRow: ViewParent = view.parent
                if (!(tableRow is ViewGroup)) {
                    return false
                }
                if (tableRow.indexOfChild(view) != columnIndex) {
                    return false
                }
                val tableLayout: ViewParent = tableRow.getParent()
                if (!(tableLayout is ViewGroup)) {
                    return false
                }
                return tableLayout.indexOfChild(tableRow as TableRow) == rowIndex
            }
        }
    }

    fun withBackgroundColor(colorMatcher: Matcher<Int?>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun matchesSafely(view: View): Boolean {
                val colorDrawable: ColorDrawable? = view.background as ColorDrawable
                if (colorDrawable == null) {
                    return false
                }
                val bgColor: Int = colorDrawable.color
                return colorMatcher.matches(bgColor)
            }

            override fun describeTo(description: Description) {
                description.appendText("with background color: ")
                colorMatcher.describeTo(description)
            }
        }
    }

    fun withBackgroundColor(color: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun matchesSafely(view: View): Boolean {
                val background: Drawable? = view.background
                if (background == null) {
                    return false
                }
                if (background is ColorDrawable) {
                    return color == background.color
                } else if (background is LayerDrawable) {
                    val drawable: Drawable = background.getDrawable(0)
                    return drawable is ColorDrawable &&
                        color == drawable.color
                }
                return false
            }

            override fun describeTo(description: Description) {
                description.appendText("with background color: $color")
            }
        }
    }

    fun withTextColor(colorMatcher: Matcher<Int?>): TypeSafeMatcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(view: View?): Boolean {
                if (!(view is TextView)) {
                    return false
                }
                val textColor: Int = view.currentTextColor
                return colorMatcher.matches(textColor)
            }

            override fun describeTo(description: Description) {
                description.appendText("with text color: ")
                colorMatcher.describeTo(description)
            }
        }
    }

    fun withTextColor(color: Int): TypeSafeMatcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(view: View?): Boolean {
                if (!(view is TextView)) {
                    return false
                }
                val textColor: Int = view.currentTextColor
                return textColor == color
            }

            override fun describeTo(description: Description) {
                description.appendText("with text color: $color")
            }
        }
    }

    fun withProgress(progress: Int): TypeSafeMatcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(view: View?): Boolean {
                if (!(view is SeekBar)) {
                    return false
                }
                val seekbarProgress: Int = view.progress
                return seekbarProgress == progress
            }

            override fun describeTo(description: Description) {
                description.appendText("with progress: $progress")
            }
        }
    }

    fun withBackground(resourceId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var resourceName: String? = null
            override fun matchesSafely(target: View): Boolean {
                val resources: Resources = target.context.resources
                resourceName = resources.getResourceEntryName(resourceId)
                if (!(target is ImageView)) {
                    return false
                }
                val expectedDrawable: Drawable? = resources.getDrawable(resourceId)
                val targetDrawable: Drawable? = target.getBackground()
                if (expectedDrawable == null || targetDrawable == null) {
                    return false
                }
                if (expectedDrawable::class != targetDrawable::class) {
                    return false
                }

                if (targetDrawable is BitmapDrawable) {
                    val targetBitmap: Bitmap = targetDrawable.bitmap
                    val expectedBitmap = (expectedDrawable as BitmapDrawable).bitmap
                    return targetBitmap.sameAs(expectedBitmap)
                } else if (targetDrawable is VectorDrawable) {
                    val targetBitmap: Bitmap = vectorToBitmap(targetDrawable)
                    val expectedBitmap = vectorToBitmap(expectedDrawable as VectorDrawable)
                    return targetBitmap.sameAs(expectedBitmap)
                } else if (targetDrawable is StateListDrawable) {
                    val targetBitmap: Bitmap = (targetDrawable.getCurrent() as BitmapDrawable).bitmap
                    val expectedBitmap = (expectedDrawable as BitmapDrawable).bitmap
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
        }
    }

    fun withChildren(numberOfChildrenMatcher: Matcher<Int?>): TypeSafeMatcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(target: View?): Boolean {
                if (!(target is ViewGroup)) {
                    return false
                }
                return numberOfChildrenMatcher.matches(target.childCount)
            }

            override fun describeTo(description: Description) {
                description.appendText("with children # is ")
                numberOfChildrenMatcher.describeTo(description)
            }
        }
    }

    fun equalsNumberDots(value: Int): BoundedMatcher<Any?, LinearLayout> {
        return object : BoundedMatcher<Any?, LinearLayout>(LinearLayout::class.java) {
            private var layoutCount: String? = null
            override fun describeTo(description: Description) {
                description.appendText("Number of dots does not match.\n")
                description.appendText("Expected: $value")
                if (layoutCount != null) {
                    description.appendText("\nIs: $layoutCount")
                }
            }

            public override fun matchesSafely(layout: LinearLayout): Boolean {
                layoutCount = layout.childCount.toString()
                return layout.childCount == value
            }
        }
    }

    fun checkDotsColors(
        activeIndex: Int, colorActive: Int,
        colorInactive: Int
    ): BoundedMatcher<View?, LinearLayout> {
        return object : BoundedMatcher<View?, LinearLayout>(LinearLayout::class.java) {
            private var errorTextView: String? = null
            private var currentIndex: Int = -1
            private var currentColor: Int = 0
            private var expectedColor: Int = 0
            public override fun matchesSafely(layout: LinearLayout): Boolean {
                currentIndex = 0
                while (currentIndex < layout.childCount) {
                    val textView: TextView? = layout.getChildAt(currentIndex) as TextView
                    if (textView == null) {
                        errorTextView = "DotView is not TextView"
                        return false
                    }
                    currentColor = textView.currentTextColor
                    if (currentIndex == activeIndex) {
                        if (currentColor != colorActive) {
                            expectedColor = colorActive
                            return false
                        }
                    } else {
                        if (currentColor != colorInactive) {
                            expectedColor = colorInactive
                            return false
                        }
                    }
                    currentIndex++
                }
                return true
            }

            override fun describeTo(description: Description) {
                description.appendText("\nAt Index: $currentIndex")
                if (errorTextView != null) {
                    description.appendText("\nIs not a textview")
                    return
                }
                description.appendText("Dot Color does not match ")
                description.appendText("\nExcepted: $expectedColor")
                description.appendText("\nIs: $currentColor")
            }
        }
    }

    fun withDrawable(resourceId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var resourceName: String? = null
            override fun matchesSafely(target: View): Boolean {
                val resources: Resources = target.context.resources
                resourceName = resources.getResourceEntryName(resourceId)
                if (!(target is ImageView)) {
                    return false
                }
                val expectedDrawable: Drawable? = resources.getDrawable(resourceId)
                val targetDrawable: Drawable? = target.drawable
                if (expectedDrawable == null || targetDrawable == null) {
                    return false
                }
                val expectedBitmap: Bitmap
                if (targetDrawable is BitmapDrawable) {
                    val targetBitmap: Bitmap = targetDrawable.bitmap
                    expectedBitmap = (expectedDrawable as BitmapDrawable).bitmap
                    return targetBitmap.sameAs(expectedBitmap)
                } else if (targetDrawable is VectorDrawable || targetDrawable is VectorDrawableCompat) {
                    val targetBitmap: Bitmap = vectorToBitmap(targetDrawable as VectorDrawable)
                    expectedBitmap = vectorToBitmap(expectedDrawable as VectorDrawable)
                    return targetBitmap.sameAs(expectedBitmap)
                } else if (targetDrawable is StateListDrawable) {
                    val targetBitmap: Bitmap = vectorToBitmap(targetDrawable.getCurrent() as VectorDrawable)
                    expectedBitmap = vectorToBitmap(expectedDrawable as VectorDrawable)
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
        }
    }

    /**
     * Matches [Root]s that are toasts (i.e. is not a window of the currently resumed activity).
     *
     * @see androidx.test.espresso.matcher.RootMatchers.isDialog
     */
    val isToast: Matcher<Root>
        get() = object : TypeSafeMatcher<Root>() {
            override fun describeTo(description: Description) {
                description.appendText("is toast")
            }

            public override fun matchesSafely(root: Root): Boolean {
                val type: Int = root.windowLayoutParams.get().type
                return type == WindowManager.LayoutParams.TYPE_TOAST
            }
        }
    val isNotVisible: ViewAssertion
        get() {
            return object : ViewAssertion {
                override fun check(view: View, noView: NoMatchingViewException) {
                    if (view != null) {
                        val isRect: Boolean = view.getGlobalVisibleRect(Rect())
                        val isVisible: Boolean =
                            ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
                                .matches(view)
                        val retVal: Boolean = !(isRect && isVisible)
                        Assert.assertThat(
                            "View is present in the hierarchy: " + HumanReadables.describe(view),
                            retVal, Matchers.`is`(true)
                        )
                    }
                }
            }
        }
    val isOnLeftSide: Matcher<View>
        get() {
            return object : TypeSafeMatcher<View>() {
                override fun describeTo(description: Description) {
                    description.appendText("View is not on the Left Side")
                }

                public override fun matchesSafely(view: View): Boolean {
                    val displayMiddle: Int = Resources.getSystem().displayMetrics.widthPixels / 2
                    val viewStartX: Int = view.x.toInt()
                    val viewEndX: Int = viewStartX + view.width
                    return viewStartX < displayMiddle && viewEndX < displayMiddle
                }
            }
        }
    val isOnRightSide: Matcher<View>
        get() {
            return object : TypeSafeMatcher<View>() {
                override fun describeTo(description: Description) {
                    description.appendText("View is not on the Right Side")
                }

                public override fun matchesSafely(view: View): Boolean {
                    val displayMiddle: Int = Resources.getSystem().displayMetrics.widthPixels / 2
                    val viewStartX: Int = view.x.toInt()
                    val viewEndX: Int = viewStartX + view.width
                    return viewStartX > displayMiddle && viewEndX > displayMiddle
                }
            }
        }

    fun withAdaptedData(resourceId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("with class name: ")
            }

            public override fun matchesSafely(view: View): Boolean {
                val resourceName: String
                if (!(view is AdapterView<*>)) {
                    return false
                }
                val resources: Resources = view.getContext().resources
                resourceName = resources.getString(resourceId)
                val adapter: Adapter = view.adapter
                for (i in 0 until adapter.count) {
                    if (resourceName == (adapter.getItem(i) as MenuItem).title.toString()) {
                        return true
                    }
                }
                return false
            }
        }
    }
}
