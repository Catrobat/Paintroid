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

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

private const val BORDER_WIDTH_PX = 1f
private const val HUE_PANEL_WIDTH = 30f
private const val PANEL_SPACING = 10f
private const val PALETTE_CIRCLE_TRACKING_RADIUS = 5f
private const val RECTANGLE_TRACKER_OFFSET = 2f
private const val HUE = 360f
private const val CHANNELS = 3
private const val ALPHA_MAX = 0xff

class HSVColorPickerView : View {
    private var panelSpacing = PANEL_SPACING
    private var paletteCircleTrackerRadius = PALETTE_CIRCLE_TRACKING_RADIUS
    private var rectangleTrackerOffset = RECTANGLE_TRACKER_OFFSET
    private var huePanelWidth = HUE_PANEL_WIDTH
    private var density = 1f
    private var alpha = ALPHA_MAX
    private var hue = HUE
    private var sat = 0f
    private var brightness = 0f
    private val sliderTrackerColor = -0xa0a0b
    private val borderColor = -0x919192
    private var drawingOffset = 0f
    private var satValPaint: Paint
    private var satValTrackerPaint: Paint
    private var huePaint: Paint
    private var hueTrackerPaint: Paint
    private var borderPaint: Paint
    private var drawingRect: RectF
    private val satValRect: RectF
    private val hueRect: RectF
    private var onColorChangedListener: OnColorChangedListener? = null
    private var onColorFinallySelectedListener: OnColorFinallySelectedListener? = null
    private var startTouchPoint: Point? = null

    var selectedColor: Int
        get() {
            val hsv = floatArrayOf(hue, sat, brightness)
            return Color.HSVToColor(alpha, hsv)
        }
        set(color) {
            val hsv = FloatArray(CHANNELS)
            Color.colorToHSV(color, hsv)
            alpha = Color.alpha(color)
            hue = hsv[0]
            sat = hsv[1]
            brightness = hsv[2]
            invalidate()
        }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    init {
        density = context.resources.displayMetrics.density
        paletteCircleTrackerRadius *= density
        rectangleTrackerOffset *= density
        huePanelWidth *= density
        panelSpacing *= density
        drawingOffset = paletteCircleTrackerRadius
        satValTrackerPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f * density
            isAntiAlias = true
        }
        hueTrackerPaint = Paint().apply {
            color = sliderTrackerColor
            style = Paint.Style.STROKE
            strokeWidth = 2f * density
            isAntiAlias = true
        }
        satValPaint = Paint()
        huePaint = Paint()
        borderPaint = Paint()
        drawingRect = RectF()
        satValRect = RectF()
        hueRect = RectF()
    }

    private fun clamp(brightness: Float, mn: Float, mx: Float): Float = max(mn, min(mx, brightness))

    private fun drawSatValPanel(canvas: Canvas) {
        borderPaint.color = borderColor
        canvas.drawRect(
            drawingRect.left,
            drawingRect.top,
            satValRect.right + BORDER_WIDTH_PX,
            satValRect.bottom + BORDER_WIDTH_PX,
            borderPaint
        )
        val valShader = LinearGradient(
            satValRect.left,
            satValRect.top,
            satValRect.left,
            satValRect.bottom,
            -0x1,
            -0x1000000,
            Shader.TileMode.CLAMP
        )
        val rgb = Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
        val satShader = LinearGradient(
            satValRect.left,
            satValRect.top,
            satValRect.right,
            satValRect.bottom,
            Color.WHITE,
            rgb,
            Shader.TileMode.CLAMP
        )
        satValPaint.apply {
            xfermode = null
            shader = valShader
            canvas.drawRect(satValRect, this)
            shader = satShader
            xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
            canvas.drawRect(satValRect, this)
        }
        satValToPoint(sat, brightness).apply {
            satValTrackerPaint.color = -0x1000000
            canvas.drawCircle(
                x.toFloat(),
                y.toFloat(),
                paletteCircleTrackerRadius - 1f * density,
                satValTrackerPaint
            )
            satValTrackerPaint.color = -0x222223
            canvas.drawCircle(
                x.toFloat(),
                y.toFloat(),
                paletteCircleTrackerRadius,
                satValTrackerPaint
            )
        }
    }

    private fun drawHuePanel(canvas: Canvas) {
        borderPaint.color = borderColor
        canvas.drawRect(
            hueRect.left - BORDER_WIDTH_PX,
            hueRect.top -
                BORDER_WIDTH_PX,
            hueRect.right + BORDER_WIDTH_PX,
            hueRect.bottom + BORDER_WIDTH_PX,
            borderPaint
        )
        val hueShader = LinearGradient(
            hueRect.left,
            hueRect.top,
            hueRect.left,
            hueRect.bottom,
            buildHueColorArray(),
            null,
            Shader.TileMode.CLAMP
        )
        huePaint.shader = hueShader
        canvas.drawRect(hueRect, huePaint)
        val rectHeight = 2 * density
        hueToPoint(hue).let {
            val r = RectF().apply {
                left = hueRect.left - rectangleTrackerOffset
                right = hueRect.right + rectangleTrackerOffset
                top = it.y - rectHeight
                bottom = it.y + rectHeight
            }
            canvas.drawRoundRect(r, 2f, 2f, hueTrackerPaint)
        }
    }

    private fun moveTrackersIfNeeded(event: MotionEvent): Boolean {
        val startX = startTouchPoint?.x ?: return false
        val startY = startTouchPoint?.y ?: return false
        when {
            hueRect.contains(startX.toFloat(), startY.toFloat()) -> hue = pointToHue(event.y)
            satValRect.contains(startX.toFloat(), startY.toFloat()) -> {
                val result = pointToSatVal(event.x, event.y)
                sat = result[0]
                brightness = result[1]
            }
            else -> return false
        }
        return true
    }

    private fun satValToPoint(sat: Float, brightness: Float): Point {
        val height = satValRect.height()
        val width = satValRect.width()
        return Point().apply {
            x = (sat * width + satValRect.left).toInt()
            y = ((1f - brightness) * height + satValRect.top).toInt()
        }
    }

    private fun hueToPoint(hue: Float): Point {
        val height = hueRect.height()
        return Point().apply {
            x = hueRect.left.toInt()
            y = (height - hue * height / HUE + hueRect.top).toInt()
        }
    }

    private fun pointToSatVal(x: Float, y: Float): FloatArray {
        val width = satValRect.width()
        val height = satValRect.height()
        val curX = clamp(x - satValRect.left, 0f, width)
        val curY = clamp(y - satValRect.top, 0f, height)
        return floatArrayOf(1f / width * curX, 1f - 1f / height * curY)
    }

    private fun pointToHue(y: Float): Float {
        val height = hueRect.height()
        val curY = clamp(y - hueRect.top, 0f, height)
        return HUE - curY * HUE / height
    }

    private fun buildHueColorArray(): IntArray {
        val hue = IntArray(HUE.toInt() + 1)
        var count = 0
        for (i in hue.size - 1 downTo 0) {
            hue[count++] = Color.HSVToColor(floatArrayOf(i.toFloat(), 1f, 1f))
        }
        return hue
    }

    private fun setUpSatValRect() {
        val panelContentHeight =
            drawingRect.height() - 2 * BORDER_WIDTH_PX
        val panelContentWidth =
            drawingRect.width() - 2 * BORDER_WIDTH_PX - panelSpacing - huePanelWidth
        val left = drawingRect.left + BORDER_WIDTH_PX
        val top = drawingRect.top + BORDER_WIDTH_PX
        val bottom = top + panelContentHeight
        val right = left + panelContentWidth
        satValRect.set(left, top, right, bottom)
    }

    private fun setUpHueRect() {
        val left = drawingRect.right - huePanelWidth + BORDER_WIDTH_PX
        val top = drawingRect.top + BORDER_WIDTH_PX
        val bottom = drawingRect.bottom - BORDER_WIDTH_PX
        val right = drawingRect.right - BORDER_WIDTH_PX
        hueRect.set(left, top, right, bottom)
    }

    private fun onColorChanged() {
        onColorChangedListener?.colorChanged(selectedColor)
    }

    fun setOnColorChangedListener(listener: OnColorChangedListener?) {
        onColorChangedListener = listener
    }

    private fun onColorFinallySelected() {
        onColorFinallySelectedListener?.colorFinallySelected(selectedColor)
    }

    fun setOnColorFinallySelectedListener(listener: OnColorFinallySelectedListener?) {
        onColorFinallySelectedListener = listener
    }

    override fun onDraw(canvas: Canvas) {
        if (drawingRect.width() <= 0 || drawingRect.height() <= 0) {
            return
        }
        drawSatValPanel(canvas)
        drawHuePanel(canvas)
    }

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
                onColorFinallySelected()
            }
        }
        if (update) {
            invalidate()
            onColorChanged()
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingRect = RectF().apply {
            left = drawingOffset + paddingLeft
            right = w - drawingOffset - paddingRight
            top = drawingOffset + paddingTop
            bottom = h - drawingOffset - paddingBottom
        }
        setUpSatValRect()
        setUpHueRect()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd
        setMeasuredDimension(size, size)
    }

    fun interface OnColorChangedListener {
        fun colorChanged(color: Int)
    }

    fun interface OnColorFinallySelectedListener {
        fun colorFinallySelected(color: Int)
    }
}
