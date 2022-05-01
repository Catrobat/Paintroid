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
import android.graphics.RectF
import android.os.Bundle
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.drawable.DrawableFactory
import org.catrobat.paintroid.tools.drawable.DrawableShape
import org.catrobat.paintroid.tools.drawable.DrawableStyle
import org.catrobat.paintroid.tools.helper.toPoint
import org.catrobat.paintroid.tools.options.ShapeToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController

private const val SHAPE_OFFSET = 10f
private const val DEFAULT_OUTLINE_WIDTH = 25
private const val BUNDLE_BASE_SHAPE = "BASE_SHAPE"
private const val BUNDLE_SHAPE_DRAW_TYPE = "SHAPE_DRAW_TYPE"
private const val BUNDLE_OUTLINE_WIDTH = "OUTLINE_WIDTH"

class ShapeTool(
    shapeToolOptionsView: ShapeToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    commandManager: CommandManager,
    override var drawTime: Long
) : BaseToolWithRectangleShape(
    contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager
) {
    private val shapeToolOptionsView: ShapeToolOptionsView
    private val shapePreviewPaint = Paint()
    private val shapePreviewRect = RectF()
    private val drawableFactory = DrawableFactory()
    private var baseShape = DrawableShape.RECTANGLE
    private var shapeDrawType = DrawableStyle.FILL
    private var shapeDrawable = drawableFactory.createDrawable(baseShape)
    private var shapeOutlineWidth = DEFAULT_OUTLINE_WIDTH
    val shapeBitmapPaint = Paint()

    override val toolType: ToolType
        get() = ToolType.SHAPE

    init {
        rotationEnabled = true
        this.shapeToolOptionsView = shapeToolOptionsView
        this.shapeToolOptionsView.setCallback(
            object : ShapeToolOptionsView.Callback {
                override fun setToolType(shape: DrawableShape) {
                    setBaseShape(shape)
                    workspace.invalidate()
                }

                override fun setDrawType(drawType: DrawableStyle) {
                    shapeDrawType = drawType
                    workspace.invalidate()
                }

                override fun setOutlineWidth(outlineWidth: Int) {
                    shapeOutlineWidth = outlineWidth
                    workspace.invalidate()
                }
            })
        toolOptionsViewController.showDelayed()
    }

    fun getBaseShape(): DrawableShape = baseShape

    fun setBaseShape(shape: DrawableShape) {
        baseShape = shape
        shapeDrawable = drawableFactory.createDrawable(shape)
    }

    override fun changePaintColor(color: Int) {
        super.changePaintColor(color)
        workspace.invalidate()
    }

    override fun onSaveInstanceState(bundle: Bundle?) {
        super.onSaveInstanceState(bundle)
        bundle?.apply {
            putSerializable(BUNDLE_BASE_SHAPE, baseShape)
            putSerializable(BUNDLE_SHAPE_DRAW_TYPE, shapeDrawType)
            putInt(BUNDLE_OUTLINE_WIDTH, shapeOutlineWidth)
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        super.onRestoreInstanceState(bundle)
        val newBaseShape = bundle?.getSerializable(BUNDLE_BASE_SHAPE) as DrawableShape? ?: return
        val newShapeDrawType =
            bundle?.getSerializable(BUNDLE_SHAPE_DRAW_TYPE) as DrawableStyle? ?: return
        val newShapeOutlineWidth = bundle?.getInt(BUNDLE_OUTLINE_WIDTH) ?: return
        if (baseShape != newBaseShape || shapeDrawType != newShapeDrawType) {
            baseShape = newBaseShape
            shapeDrawType = newShapeDrawType
            shapeOutlineWidth = newShapeOutlineWidth
            shapeDrawable = drawableFactory.createDrawable(newBaseShape)
            shapeToolOptionsView.setShapeActivated(newBaseShape)
            shapeToolOptionsView.setDrawTypeActivated(newShapeDrawType)
            shapeToolOptionsView.setShapeOutlineWidth(newShapeOutlineWidth)
        }
    }

    override fun drawBitmap(canvas: Canvas, boxWidth: Float, boxHeight: Float) {
        shapePreviewPaint.set(toolPaint.previewPaint)
        preparePaint(shapePreviewPaint)
        prepareShapeRectangle(shapePreviewRect, boxWidth, boxHeight)
        shapeDrawable.draw(canvas, shapePreviewRect, shapePreviewPaint)
    }

    private fun prepareShapeRectangle(shapeRect: RectF, boxWidth: Float, boxHeight: Float) {
        shapeRect.setEmpty()
        shapeRect.inset(SHAPE_OFFSET - boxWidth / 2, SHAPE_OFFSET - boxHeight / 2)
        if (shapePreviewPaint.style == Paint.Style.STROKE) {
            shapeRect.inset(shapeOutlineWidth / 2f, shapeOutlineWidth / 2f)
        }
    }

    private fun preparePaint(paint: Paint) {
        when (shapeDrawType) {
            DrawableStyle.FILL -> {
                paint.style = Paint.Style.FILL
                paint.strokeJoin = Paint.Join.MITER
            }
            DrawableStyle.STROKE -> {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = shapeOutlineWidth.toFloat()
                paint.strokeCap = Paint.Cap.BUTT
                paint.strokeJoin = Paint.Join.MITER
                val antiAlias = shapeOutlineWidth > 1
                paint.isAntiAlias = antiAlias
            }
        }
    }

    override fun onClickOnButton() {
        if (toolPosition.x in -boxWidth / 2..workspace.width + boxWidth / 2 && toolPosition.y in -boxHeight / 2..workspace.height + boxHeight / 2) {
            shapeBitmapPaint.set(toolPaint.paint)
            val shapeRect = RectF()
            preparePaint(shapeBitmapPaint)
            prepareShapeRectangle(shapeRect, boxWidth, boxHeight)
            val command = commandFactory.createGeometricFillCommand(
                shapeDrawable,
                toPoint(toolPosition),
                shapeRect,
                boxRotation,
                shapeBitmapPaint
            )
            commandManager.addCommand(command)
            highlightBox()
        }
    }
}
