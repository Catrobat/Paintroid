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
package org.catrobat.paintroid.tools

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.Point
import android.graphics.PointF
import android.os.Bundle

interface Tool {
    val toolType: ToolType

    val drawPaint: Paint

    var drawTime: Long

    fun handToolMode(): Boolean

    fun handleDown(coordinate: PointF?): Boolean

    fun handleMove(coordinate: PointF?, shouldAnimate: Boolean = false): Boolean

    fun handleUp(coordinate: PointF?): Boolean

    fun changePaintColor(color: Int, invalidate: Boolean = true)

    fun changePaintStrokeWidth(strokeWidth: Int)

    fun changePaintStrokeCap(cap: Cap)

    fun draw(canvas: Canvas)

    fun resetInternalState(stateChange: StateChange)

    fun getAutoScrollDirection(
        pointX: Float,
        pointY: Float,
        screenWidth: Int,
        screenHeight: Int
    ): Point

    fun handleUpAnimations(coordinate: PointF?)
    fun handleDownAnimations(coordinate: PointF?)
    fun onSaveInstanceState(bundle: Bundle?)

    fun onRestoreInstanceState(bundle: Bundle?)

    enum class StateChange {
        ALL, RESET_INTERNAL_STATE, NEW_IMAGE_LOADED, MOVE_CANCELED
    }

    fun toolPositionCoordinates(coordinate: PointF): PointF
}
