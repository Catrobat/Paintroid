/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2021 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.ui.tools

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import org.catrobat.paintroid.R
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.options.BrushToolOptionsView.OnBrushPreviewListener
import org.catrobat.paintroid.tools.options.BrushToolPreview

private const val CONSTANT_3F = 3f
private const val CONSTANT_16F = 16f
private const val CONSTANT_56F = 56f
private const val CONSTANT_POINT25 = .25

class BrushToolView : View, BrushToolPreview {
    private val canvasPaint: Paint
    private val checkeredPattern: Paint
    private var callback: OnBrushPreviewListener? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        val checkerboard =
            BitmapFactory.decodeResource(resources, R.drawable.pocketpaint_checkeredbg)
        val shader = BitmapShader(
            checkerboard,
            Shader.TileMode.REPEAT, Shader.TileMode.REPEAT
        )
        canvasPaint = Paint()
        checkeredPattern = Paint()
        checkeredPattern.shader = shader
    }

    private fun changePaintColor(color: Int) {
        val strokeWidth = callback?.strokeWidth
        val strokeCap = callback?.strokeCap
        canvasPaint.apply {
            reset()
            style = Paint.Style.STROKE
            strokeWidth?.let { this.strokeWidth = it }
            strokeCap?.let { this.strokeCap = it }
            isAntiAlias = true
            if (Color.alpha(color) == 0x00) {
                shader = checkeredPattern.shader
                this.color = Color.BLACK
            } else {
                this.color = color
            }
        }
    }

    private fun drawEraserPreview(canvas: Canvas) {
        changePaintColor(Color.TRANSPARENT)
        val startX = right - width / CONSTANT_3F
        val startY = top + height - CONSTANT_56F
        val endX = right - width / CONSTANT_16F
        val endY = top + height - CONSTANT_56F
        canvasPaint.color = Color.BLACK
        canvas.drawLine(startX, startY, endX, endY, canvasPaint)
    }

    private fun drawLineOnCanvas(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        canvas: Canvas
    ) {
        canvas.drawLine(startX, startY, endX, endY, canvasPaint)
    }

    private fun drawLinePreview(canvas: Canvas) {
        val currentColor = callback?.color
        if (currentColor != null) {
            changePaintColor(currentColor)
        }

        if (callback?.maskFilter != null) {
            canvasPaint.maskFilter = callback?.maskFilter
        }

        val startX = right - width / CONSTANT_3F
        val startY = top + height - CONSTANT_56F
        val endX = right - width / CONSTANT_16F
        val endY = top + height - CONSTANT_56F
        if (canvasPaint.color == Color.TRANSPARENT) {
            canvasPaint.color = Color.BLACK
            drawLineOnCanvas(startX, startY, endX, endY, canvas)
            canvasPaint.color = Color.TRANSPARENT
        } else {
            drawLineOnCanvas(startX, startY, endX, endY, canvas)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (callback?.toolType) {
            ToolType.BRUSH, ToolType.CURSOR, ToolType.LINE, ToolType.WATERCOLOR -> drawLinePreview(canvas)
            ToolType.ERASER -> drawEraserPreview(canvas)
            else -> {}
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(widthSize, (widthSize * CONSTANT_POINT25).toInt())
    }

    override fun setListener(callback: OnBrushPreviewListener) {
        this.callback = callback
    }
}
