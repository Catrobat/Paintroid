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
package org.catrobat.paintroid.colorpicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.graphics.Shader
import android.view.MotionEvent
import android.view.View

private const val PADDING_SIDE = 5f
private const val PADDING_TOP = 10f
private const val PADDING_BOT = 20f
private const val MIN_PADDING_CONST = 1f
private const val PADDING_SIDE_STANDARD = 15f
private const val PADDING_BOTTOM_STANDARD = 70f
private const val HSV_INITIALIZER = 3
private const val MAX_ALPHA = 0xff
private const val SLIDER_PADDING = 2f
private const val FOUR = 4

class AlphaSlider(
    context: Context
) : View(context) {
    private var boardPaint: Paint = Paint()
    private var checkeredPaint: Paint = Paint()
    private var alphaPaint: Paint = Paint()
    private var hueTrackerPaint: Paint = Paint()
    private var drawingRect: RectF = RectF()
    private var alphaRectangle: RectF = RectF()
    private var alphaShader: Shader? = null
    private var startTouchPoint: Point? = null
    private val sliderTrackerColor = -0xa0a0b
    private var density = 1f

    init {
        density = getContext().resources.displayMetrics.density

        val checkerboard =
            BitmapFactory.decodeResource(resources, R.drawable.pocketpaint_checkeredbg)
        val checkeredShader =
            BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        checkeredPaint.shader = checkeredShader

        hueTrackerPaint.color = sliderTrackerColor
        hueTrackerPaint.style = Paint.Style.STROKE
        hueTrackerPaint.strokeWidth = 2f * density
        hueTrackerPaint.isAntiAlias = true
    }

    companion object {
        private var alphaVal = MAX_ALPHA
        private lateinit var onColorChangedListener: OnColorChangedListener
        private var selectedColor = 0
        private var currentColor = 0
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (drawingRect.width() <= 0 || drawingRect.height() <= 0) {
            return
        }

        drawAlphaRect(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingRect = RectF()
        drawingRect.left = PADDING_SIDE + paddingLeft
        drawingRect.right = w - PADDING_SIDE - paddingRight
        drawingRect.top = PADDING_TOP + paddingTop
        drawingRect.bottom = h + PADDING_BOT - paddingBottom
        setUpAlphaRect()
    }

    private fun setUpAlphaRect() {
        val dRect = drawingRect
        val left = dRect.left + PADDING_SIDE_STANDARD
        val top = dRect.bottom - PADDING_BOTTOM_STANDARD
        val bottom = dRect.bottom - MIN_PADDING_CONST
        val right = dRect.right - PADDING_SIDE_STANDARD
        alphaRectangle[left, top, right] = bottom
    }

    private fun drawAlphaRect(canvas: Canvas) {
        val rect: RectF = alphaRectangle

        boardPaint.color = -0x919192
        canvas.drawRect(
            rect.left - MIN_PADDING_CONST,
            rect.top - MIN_PADDING_CONST,
            rect.right + MIN_PADDING_CONST,
            rect.bottom + MIN_PADDING_CONST,
            boardPaint
        )

        canvas.drawRect(rect, checkeredPaint)
        var color: Int = currentColor

        val hsv = FloatArray(HSV_INITIALIZER)
        Color.colorToHSV(color, hsv)
        color = Color.HSVToColor(MAX_ALPHA, hsv)

        alphaShader = LinearGradient(
            rect.left, rect.top, rect.right,
            rect.top, color, 0, Shader.TileMode.CLAMP
        )
        alphaPaint.shader = alphaShader
        canvas.drawRect(rect, alphaPaint)
        val rectWidth = FOUR * density / SLIDER_PADDING
        val p = alphaToPoint(alphaVal)
        val r = RectF()
        r.left = p.x - rectWidth
        r.right = p.x + rectWidth
        r.top = rect.top - SLIDER_PADDING
        r.bottom = rect.bottom + SLIDER_PADDING
        canvas.drawRoundRect(r, SLIDER_PADDING, SLIDER_PADDING, hueTrackerPaint)
    }

    private fun alphaToPoint(alpha: Int): Point {
        val rect = alphaRectangle
        val width = rect.width()
        val p = Point()
        p.x = (width - alpha * width / MAX_ALPHA + rect.left).toInt()
        p.y = rect.top.toInt()
        return p
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var update = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startTouchPoint = Point(event.x.toInt(), event.y.toInt())
                update = moveTrackersIfNeeded(event)
            }
            MotionEvent.ACTION_MOVE -> update = moveTrackersIfNeeded(event)
            MotionEvent.ACTION_UP -> {
                startTouchPoint = null
                update = moveTrackersIfNeeded(event)
            }
        }
        if (update) {
            invalidate()
            onColorChanged()
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun moveTrackersIfNeeded(event: MotionEvent): Boolean {
        if (startTouchPoint == null) {
            return false
        }
        var update = true
        val startX = startTouchPoint!!.x
        val startY = startTouchPoint!!.y
        if (alphaRectangle.contains(startX.toFloat(), startY.toFloat())) {
            alphaVal = pointToAlpha(event.x.toInt())
        } else {
            update = false
        }
        return update
    }

    private fun clamp(
        curr: Float,
        min: Float,
        max: Float
    ): Float = min.coerceAtLeast(max.coerceAtMost(curr))

    private fun pointToAlpha(y: Int): Int {
        var x = y
        val rect: RectF = alphaRectangle
        val width = rect.width().toInt()
        x = clamp(x - rect.left, 0f, width.toFloat()).toInt()
        return MAX_ALPHA - x * MAX_ALPHA / width
    }

    fun setOnColorChangedListener(listener: OnColorChangedListener) {
        onColorChangedListener = listener
    }

    interface OnColorChangedListener {
        fun colorChanged(color: Int)
    }

    fun getSelectedColor(): Int {
        val hsv = FloatArray(HSV_INITIALIZER)
        Color.colorToHSV(selectedColor, hsv)
        selectedColor = Color.HSVToColor(alphaVal, hsv)

        return selectedColor
    }

    private fun onColorChanged() {
        onColorChangedListener?.colorChanged(getSelectedColor())
    }

    fun setSelectedColor(color: Int) {
        val hsv = FloatArray(HSV_INITIALIZER)
        Color.colorToHSV(color, hsv)
        selectedColor = Color.HSVToColor(alphaVal, hsv)
        currentColor = color

        invalidate()
    }

    fun getAlphaValue(): Int = alphaVal
}
