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
package org.catrobat.paintroid.listener

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.HorizontalScrollView

class BottomBarScrollListener : HorizontalScrollView {
    private lateinit var previous: View
    private lateinit var next: View

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(previous: View, next: View, context: Context?) : super(context) {
        this.previous = previous
        this.next = next
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        prepare()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (l == 0) {
            onScrollMostLeft()
        } else if (oldl == 0) {
            onScrollFromMostLeft()
        }
        val mostRightL = getChildAt(0).width - width
        if (l >= mostRightL) {
            onScrollMostRight()
        } else if (mostRightL in (l + 1)..oldl) {
            onScrollFromMostRight()
        }
    }

    private fun prepare() {
        val content = getChildAt(0)
        if (content.left >= 0) {
            onScrollMostLeft()
        } else if (content.left < 0) {
            onScrollFromMostLeft()
        }
        if (content.right <= width) {
            onScrollMostRight()
        } else if (content.left > width) {
            onScrollFromMostRight()
        }
    }

    fun onScrollMostRight() {
        next.visibility = GONE
    }

    fun onScrollMostLeft() {
        previous.visibility = GONE
    }

    fun onScrollFromMostLeft() {
        previous.visibility = VISIBLE
    }

    fun onScrollFromMostRight() {
        next.visibility = VISIBLE
    }
}
