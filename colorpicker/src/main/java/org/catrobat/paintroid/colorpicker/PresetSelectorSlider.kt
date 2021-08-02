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
import androidx.core.graphics.toColor

class PresetSelectorSlider(
    context: Context
    )
: View(context) {
    private var waterColorStrength: RectF = RectF()
    private var checkeredPaint: Paint = Paint()
    private var boardPaint: Paint = Paint()
    private var drawingRect: RectF = RectF()
    private var watercolorPaint: Paint = Paint()
    private var hueTrackerPaint: Paint = Paint()
    private var watercolorShader: Shader? = null

    private var startTouchPoint: Point? = null
    private val sliderTrackerColor = -0xa0a0b
    private var density = 1f

    init {
        setWillNotDraw(false)
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
        private var alphaVal = 0xff
        private lateinit var onColorChangedListener: OnColorChangedListener
        private var selectedColor = 0
        private var currentColor = 0
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (drawingRect.width() <= 0 || drawingRect.height() <= 0) {
            return
        }

        drawWaterColorStrength(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingRect = RectF()
        drawingRect.left = 5f + paddingLeft
        drawingRect.right = w - 5f - paddingRight
        drawingRect.top = 10f + paddingTop
        drawingRect.bottom = h + 20f - paddingBottom
        setUpWatercolorStrengthRect()
    }

    private fun setUpWatercolorStrengthRect() {
        val dRect = drawingRect
        val left = dRect.left + 15
        val top = dRect.bottom - 70f
        val bottom = dRect.bottom - 1f
        val right = dRect.right - 15
        waterColorStrength[left, top, right] = bottom
    }

    private fun drawWaterColorStrength(canvas: Canvas) {
        val rect: RectF = waterColorStrength

        //border color 0xff6E6E6E
        boardPaint.color = -0x919192
        canvas.drawRect(
            rect.left - 1, rect.top
                - 1, rect.right + 1,
            rect.bottom + 1, boardPaint
        )
        canvas.drawRect(rect, checkeredPaint)
        var color: Int = currentColor

        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        color = Color.HSVToColor(255, hsv)

        watercolorShader = LinearGradient(
            rect.left, rect.top, rect.right,
            rect.top, color, 0, Shader.TileMode.CLAMP
        )
        watercolorPaint.shader = watercolorShader
        canvas.drawRect(rect, watercolorPaint)
        val rectWidth = 4 * density / 2
        val p = watercolorToPoint(alphaVal)
        val r = RectF()
        r.left = p.x - rectWidth
        r.right = p.x + rectWidth
        r.top = rect.top - 2f
        r.bottom = rect.bottom + 2f
        canvas.drawRoundRect(r, 2f, 2f, hueTrackerPaint)
    }

    private fun watercolorToPoint(alpha: Int): Point {
        val rect = waterColorStrength
        val width = rect.width()
        val p = Point()
        p.x = (width - alpha * width / 0xff + rect.left).toInt()
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
        if (waterColorStrength.contains(startX.toFloat(), startY.toFloat())) {
            alphaVal = pointToAlpha(event.x.toInt())
            //
        } else {
            update = false
        }
        return update
    }

    private fun clamp(
        `val`: Float,
        min: Float,
        max: Float
    ): Float {
        return min.coerceAtLeast(max.coerceAtMost(`val`))
    }

    private fun pointToAlpha(y: Int): Int {
        var x = y
        val rect: RectF = waterColorStrength
        val width = rect.width().toInt()
        x = clamp(x - rect.left, 0f, width.toFloat()).toInt()
        return 0xff - x * 0xff / width
    }

    fun setOnColorChangedListener(listener : OnColorChangedListener) {
        onColorChangedListener = listener
    }

    interface OnColorChangedListener {
        fun colorChanged(color: Int)
    }

    fun getSelectedColor(): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(selectedColor, hsv);
        selectedColor = Color.HSVToColor(alphaVal, hsv);

        return selectedColor
    }

    private fun onColorChanged() {
        onColorChangedListener.colorChanged(getSelectedColor())
    }

    fun setSelectedColor(color: Int) {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv);
        selectedColor = Color.HSVToColor(alphaVal, hsv)
        currentColor = color

        invalidate()
    }

    fun getAlphaValue(): Int {
        return alphaVal
    }
}