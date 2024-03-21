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
package org.catrobat.paintroid.tools.options

import android.view.View
import android.view.ViewGroup

interface ToolOptionsViewController : ToolOptionsVisibilityController {
    val toolSpecificOptionsLayout: ViewGroup

    fun disable()

    fun enable()

    fun disableHide()

    fun enableHide()

    fun resetToOrigin()

    fun removeToolViews()

    fun showCheckmark()

    fun hideCheckmark()

    fun slideUp(view: View, willHide: Boolean, showOptionsView: Boolean, setViewGone: Boolean = false)

    fun slideDown(view: View, willHide: Boolean, showOptionsView: Boolean, setViewGone: Boolean = false)

    fun animateBottomAndTopNavigation(hide: Boolean)
}
