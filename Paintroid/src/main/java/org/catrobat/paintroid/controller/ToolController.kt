/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.paintroid.controller

import android.graphics.Bitmap
import org.catrobat.paintroid.colorpicker.OnColorPickedListener
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.tools.ToolType

interface ToolController {
    val isDefaultTool: Boolean
    val toolType: ToolType?
    val toolColor: Int?
    val currentTool: Tool?

    fun setOnColorPickedListener(onColorPickedListener: OnColorPickedListener)

    fun switchTool(toolType: ToolType, backPressed: Boolean)

    fun hideToolOptionsView()

    fun showToolOptionsView()

    fun toolOptionsViewVisible(): Boolean

    fun resetToolInternalState()

    fun resetToolInternalStateOnImageLoaded()

    fun disableToolOptionsView()

    fun disableHideOption()

    fun enableHideOption()

    fun enableToolOptionsView()

    fun createTool()

    fun toggleToolOptionsView()

    fun hasToolOptionsView(): Boolean

    fun setBitmapFromSource(bitmap: Bitmap?)
}
