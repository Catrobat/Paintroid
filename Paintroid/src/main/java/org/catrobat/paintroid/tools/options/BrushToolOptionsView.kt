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

import android.graphics.MaskFilter
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.view.View
import org.catrobat.paintroid.tools.ToolType

interface BrushToolOptionsView {
    fun invalidate()

    fun setCurrentPaint(paint: Paint)

    fun setStrokeCapButtonChecked(strokeCap: Cap)

    fun setBrushChangedListener(onBrushChangedListener: OnBrushChangedListener)

    fun setBrushPreviewListener(onBrushPreviewListener: OnBrushPreviewListener)

    fun getTopToolOptions(): View

    fun getBottomToolOptions(): View

    fun hideCaps()

    interface OnBrushChangedListener {
        fun setCap(strokeCap: Cap)

        fun setStrokeWidth(strokeWidth: Int)
    }

    interface OnBrushPreviewListener {
        val strokeWidth: Float
        val strokeCap: Cap
        val color: Int
        val toolType: ToolType
        val maskFilter: MaskFilter?
    }
}
