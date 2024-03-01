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
package org.catrobat.paintroid.tools.options

import android.view.View
import org.catrobat.paintroid.tools.drawable.DrawableShape
import org.catrobat.paintroid.tools.drawable.DrawableStyle

interface ShapeToolOptionsView {
    fun setShapeActivated(shape: DrawableShape)

    fun setDrawTypeActivated(drawType: DrawableStyle)

    fun setShapeOutlineWidth(outlineWidth: Int)

    fun setCallback(callback: Callback)

    fun setShapeSizeText(shapeSize: String)

    fun toggleShapeSizeVisibility(isVisible: Boolean)

    fun getShapeToolOptionsLayout(): View

    interface Callback {
        fun setToolType(shape: DrawableShape)

        fun setDrawType(drawType: DrawableStyle)

        fun setOutlineWidth(outlineWidth: Int)
    }
}
