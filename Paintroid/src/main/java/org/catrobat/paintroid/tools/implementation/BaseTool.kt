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

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.Point
import android.graphics.PointF
import android.os.Bundle
import androidx.annotation.ColorInt
import org.catrobat.paintroid.command.CommandFactory
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.tools.Tool.StateChange
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.common.PointScrollBehavior
import org.catrobat.paintroid.tools.common.ScrollBehavior
import org.catrobat.paintroid.tools.options.ToolOptionsViewController

abstract class BaseTool(
    @JvmField
    open var contextCallback: ContextCallback,
    @JvmField
    protected var toolOptionsViewController: ToolOptionsViewController,
    @JvmField
    protected var toolPaint: ToolPaint,
    @JvmField
    protected var workspace: Workspace,
    @JvmField
    protected var commandManager: CommandManager
) : Tool {
    @JvmField
    protected val movedDistance: PointF

    @JvmField
    protected var scrollBehavior: ScrollBehavior

    @JvmField
    protected var previousEventCoordinate: PointF?

    @JvmField
    protected var commandFactory: CommandFactory = DefaultCommandFactory()

    init {
        val scrollTolerance = contextCallback.scrollTolerance
        scrollBehavior = PointScrollBehavior(scrollTolerance)
        movedDistance = PointF(0f, 0f)
        previousEventCoordinate = PointF(0f, 0f)
    }

    override fun onSaveInstanceState(bundle: Bundle?) = Unit

    override fun onRestoreInstanceState(bundle: Bundle?) = Unit

    override fun changePaintColor(@ColorInt color: Int) {
        toolPaint.color = color
    }

    override fun changePaintStrokeWidth(strokeWidth: Int) {
        toolPaint.strokeWidth = strokeWidth.toFloat()
    }

    override fun changePaintStrokeCap(cap: Cap) {
        toolPaint.strokeCap = cap
    }

    override val drawPaint
        get() = Paint(toolPaint.paint)

    abstract override fun draw(canvas: Canvas)

    protected open fun resetInternalState() {}

    override fun resetInternalState(stateChange: StateChange) {
        if (toolType.shouldReactToStateChange(stateChange)) {
            resetInternalState()
        }
    }

    override fun getAutoScrollDirection(
        pointX: Float,
        pointY: Float,
        screenWidth: Int,
        screenHeight: Int
    ): Point = scrollBehavior.getScrollDirection(pointX, pointY, screenWidth, screenHeight)

    override fun handToolMode(): Boolean = false
}
