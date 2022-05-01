/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.paintroid.tools.implementation

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.annotation.VisibleForTesting
import org.catrobat.paintroid.R
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import kotlin.math.max
import kotlin.math.min

private const val BUNDLE_TOOL_POSITION_Y = "TOOL_POSITION_Y"
private const val BUNDLE_TOOL_POSITION_X = "TOOL_POSITION_X"

abstract class BaseToolWithShape @SuppressLint("VisibleForTests") constructor(
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    commandManager: CommandManager
) : BaseTool(
    contextCallback,
    toolOptionsViewController,
    toolPaint,
    workspace,
    commandManager
) {

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    @JvmField
    val toolPosition: PointF

    @JvmField
    var primaryShapeColor: Int =
        contextCallback.getColor(R.color.pocketpaint_main_rectangle_tool_primary_color)

    @JvmField
    var secondaryShapeColor: Int = contextCallback.getColor(R.color.pocketpaint_colorAccent)

    @JvmField
    val linePaint: Paint

    @JvmField
    val metrics: DisplayMetrics = contextCallback.displayMetrics

    init {
        val perspective = workspace.perspective
        toolPosition = if (perspective.scale > 1) {
            PointF(
                perspective.surfaceCenterX - perspective.surfaceTranslationX,
                perspective.surfaceCenterY - perspective.surfaceTranslationY
            )
        } else {
            PointF(workspace.width / 2f, workspace.height / 2f)
        }
        linePaint = Paint()
        linePaint.color = primaryShapeColor
    }

    abstract fun drawShape(canvas: Canvas)

    fun getStrokeWidthForZoom(
        defaultStrokeWidth: Float,
        minStrokeWidth: Float,
        maxStrokeWidth: Float
    ): Float {
        val strokeWidth = defaultStrokeWidth * metrics.density / workspace.scale
        return min(maxStrokeWidth, max(minStrokeWidth, strokeWidth))
    }

    fun getInverselyProportionalSizeForZoom(defaultSize: Float): Float {
        val applicationScale = workspace.scale
        return defaultSize * metrics.density / applicationScale
    }

    override fun onSaveInstanceState(bundle: Bundle?) {
        super.onSaveInstanceState(bundle)
        bundle?.apply {
            putFloat(BUNDLE_TOOL_POSITION_X, toolPosition.x)
            putFloat(BUNDLE_TOOL_POSITION_Y, toolPosition.y)
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        super.onRestoreInstanceState(bundle)
        bundle?.apply {
            toolPosition.x = getFloat(BUNDLE_TOOL_POSITION_X, toolPosition.x)
            toolPosition.y = getFloat(BUNDLE_TOOL_POSITION_Y, toolPosition.y)
        }
    }

    override fun getAutoScrollDirection(
        pointX: Float,
        pointY: Float,
        screenWidth: Int,
        screenHeight: Int
    ): Point {
        val surfaceToolPosition = workspace.getSurfacePointFromCanvasPoint(toolPosition)
        return scrollBehavior.getScrollDirection(
            surfaceToolPosition.x,
            surfaceToolPosition.y,
            screenWidth,
            screenHeight
        )
    }

    abstract fun onClickOnButton()

    protected open fun drawToolSpecifics(canvas: Canvas, boxWidth: Float, boxHeight: Float) {}
}
