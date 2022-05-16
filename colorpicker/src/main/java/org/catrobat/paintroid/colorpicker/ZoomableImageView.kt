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
package org.catrobat.paintroid.colorpicker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import kotlin.math.abs

private const val NONE = 0
private const val DRAG = 1
private const val ZOOM = 2
private const val CLICK = 3
private const val MIN_SCALE = .75f
private const val MAX_SCALE = 50f
private const val MATRIX_SIZE = 9
private const val DELAY_MILLIS = 150L

class ZoomableImageView :
    AppCompatImageView,
    View.OnTouchListener,
    GestureDetector.OnGestureListener {
    private var mode = NONE

    private lateinit var mBitmap: Bitmap
    private lateinit var listener: OnImageViewPointClickedListener

    private val canvasRect = Rect()
    private val boarderPaint = Paint()
    private val checkeredPattern = Paint()
    private var backgroundSurfaceColor = 0

    private var scaleDetector: ScaleGestureDetector
    private var gestureDetector: GestureDetector
    private var mMatrix = Matrix()
    private var matrixValues = FloatArray(MATRIX_SIZE)

    private var saveScale = 1f

    private var origWidth = 0f
    private var origHeight = 0f
    private var viewWidth = 0f
    private var viewHeight = 0f

    private var lastPoint = PointF()
    private var startPoint = PointF()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr)

    init {
        visibility = GONE
        super.setClickable(true)
        scaleDetector = ScaleGestureDetector(context, ScaleListener())

        matrixValues = FloatArray(MATRIX_SIZE)
        imageMatrix = mMatrix
        scaleType = ScaleType.MATRIX

        gestureDetector = GestureDetector(context, this)
        setOnTouchListener(this)

        backgroundSurfaceColor = ContextCompat.getColor(
            context,
            R.color.pocketpaint_color_picker_surface_background
        )

        boarderPaint.color = Color.BLACK
        boarderPaint.style = Paint.Style.STROKE
        boarderPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)

        val checkerboard =
            BitmapFactory.decodeResource(resources, R.drawable.pocketpaint_checkeredbg)
        val shader = BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        checkeredPattern.shader = shader
        checkeredPattern.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
        // delay displaying till the drawing being done
        postDelayed({ visibility = VISIBLE }, DELAY_MILLIS)
    }

    fun setListener(listener: OnImageViewPointClickedListener) {
        this.listener = listener
    }

    override fun setImageBitmap(bitmap: Bitmap) {
        mBitmap = bitmap
        invalidate()
        updateLP()
        super.setImageBitmap(bitmap)
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mode = ZOOM
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var mScaleFactor = detector.scaleFactor
            val prevScale = saveScale
            saveScale *= mScaleFactor
            if (saveScale > MAX_SCALE) {
                saveScale = MAX_SCALE
                mScaleFactor = MAX_SCALE / prevScale
            } else if (saveScale < MIN_SCALE) {
                saveScale = MIN_SCALE
                mScaleFactor = MIN_SCALE / prevScale
            }
            if (origWidth * saveScale <= viewWidth || origHeight * saveScale <= viewHeight) {
                mMatrix.postScale(
                    mScaleFactor,
                    mScaleFactor,
                    viewWidth / 2.toFloat(),
                    viewHeight / 2.toFloat()
                )
            } else {
                mMatrix.postScale(mScaleFactor, mScaleFactor, detector.focusX, detector.focusY)
            }
            correctTranslation()
            return true
        }
    }

    private fun fitToScreen() {
        if (drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0) {
            return
        }
        val imageWidth = drawable.intrinsicWidth.toFloat()
        val imageHeight = drawable.intrinsicHeight.toFloat()
        val scaleX = viewWidth / imageWidth
        val scaleY = viewHeight / imageHeight
        val scale = scaleX.coerceAtMost(scaleY)
        mMatrix.setScale(scale, scale)

        // Center the image
        var redundantYSpace = viewHeight - scale * imageHeight
        var redundantXSpace = viewWidth - scale * imageWidth
        redundantYSpace /= 2.toFloat()
        redundantXSpace /= 2.toFloat()
        mMatrix.postTranslate(redundantXSpace, redundantYSpace)
        origWidth = viewWidth - 2 * redundantXSpace
        origHeight = viewHeight - 2 * redundantYSpace
        imageMatrix = mMatrix
    }

    fun correctTranslation() {
        mMatrix.getValues(matrixValues)
        val transX = matrixValues[Matrix.MTRANS_X]
        val transY = matrixValues[Matrix.MTRANS_Y]
        val fixTransX = getCorrectTranslation(transX, viewWidth, origWidth * saveScale)
        val fixTransY = getCorrectTranslation(transY, viewHeight, origHeight * saveScale)
        if (fixTransX != 0f || fixTransY != 0f) {
            mMatrix.postTranslate(fixTransX, fixTransY)
        }
    }

    private fun getCorrectTranslation(trans: Float, viewSize: Float, contentSize: Float): Float {
        val minTrans: Float
        val maxTrans: Float
        if (contentSize <= viewSize) {
            minTrans = 0f
            maxTrans = viewSize - contentSize
        } else {
            minTrans = viewSize - contentSize
            maxTrans = 0f
        }
        return when {
            trans < minTrans -> -trans + minTrans
            trans > maxTrans -> -trans + maxTrans
            else -> 0f
        }
    }

    private fun getFixDragTrans(delta: Float, viewSize: Float, contentSize: Float): Float {
        return if (contentSize <= viewSize) {
            0F
        } else delta
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        viewHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        if (saveScale == 1f) {
            fitToScreen()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.setMatrix(mMatrix)

        if (this::mBitmap.isInitialized) {
            canvasRect.set(0, 0, mBitmap.width, mBitmap.height)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                canvas.drawColor(backgroundSurfaceColor, PorterDuff.Mode.SRC)
            } else {
                canvas.apply {
                    save()
                    clipOutRect(canvasRect)
                    drawColor(backgroundSurfaceColor, PorterDuff.Mode.SRC)
                    restore()
                }
            }

            canvas.apply {
                drawRect(canvasRect, checkeredPattern)
                drawRect(canvasRect, boarderPaint)
                drawBitmap(mBitmap, 0f, 0f, null)
            }
        }
    }

    override fun onTouch(view: View?, event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        val currentPoint = PointF(event.x, event.y)
        updateLP()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastPoint.set(currentPoint)
                startPoint.set(lastPoint)
                mode = DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                lastPoint.set(currentPoint)
                startPoint.set(lastPoint)
                mode = ZOOM
            }
            MotionEvent.ACTION_MOVE -> {
                if (mode == ZOOM && saveScale > MIN_SCALE) {
                    val dx = currentPoint.x - lastPoint.x
                    val dy = currentPoint.y - lastPoint.y
                    val fixTransX = getFixDragTrans(
                        dx,
                        viewWidth,
                        origWidth * saveScale
                    )
                    val fixTransY = getFixDragTrans(
                        dy,
                        viewHeight,
                        origHeight * saveScale
                    )
                    mMatrix.postTranslate(fixTransX, fixTransY)
                    correctTranslation()
                    lastPoint[currentPoint.x] = currentPoint.y
                }

                if (event.pointerCount == 1 && mode == DRAG) {
                    performColorColorChanged(PointF(event.x, event.y))
                }
            }

            MotionEvent.ACTION_UP -> {
                mode = CLICK
                val xDiff = abs(event.x - startPoint.x).toInt()
                val yDiff = abs(event.y - startPoint.y).toInt()
                if (xDiff < 1 && yDiff < 1) {
                    performClick()
                    performColorColorChanged(PointF(event.x, event.y))
                }
            }

            MotionEvent.ACTION_POINTER_UP ->
                mode = NONE
        }
        imageMatrix = mMatrix
        return false
    }

    private fun updateLP() {
        updateLayoutParams {
            post {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    val windowMetrics = windowManager.currentWindowMetrics
                    val windowInsets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(
                        WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout()
                    )
                    val insetsWidth = windowInsets.right + windowInsets.left
                    val insetsHeight = windowInsets.top + windowInsets.bottom
                    val b = windowMetrics.bounds
                    width = b.width() - insetsWidth
                    height = b.height() - insetsHeight
                } else {
                    val point = Point()
                    display?.getSize(point)
                    height = point.y
                    width = point.x
                }
            }
        }
    }

    private fun performColorColorChanged(point: PointF) {
        val bitmap = mBitmap
        val inverse = Matrix()
        imageMatrix.invert(inverse)
        val touchPoint = floatArrayOf(point.x, point.y)
        inverse.mapPoints(touchPoint)
        val xPixel = touchPoint[0].toInt()
        val yPixel = touchPoint[1].toInt()
        if (xPixel !in 0 until bitmap.width || yPixel !in 0 until bitmap.height) {
            // clicked outside of the image frame
            return
        }

        val touchedPixelRGB = bitmap.getPixel(xPixel, yPixel)
        val colorValue = Color.argb(
            Color.alpha(touchedPixelRGB),
            Color.red(touchedPixelRGB),
            Color.green(touchedPixelRGB),
            Color.blue(touchedPixelRGB)
        )

        listener.colorChanged(colorValue)
    }

    override fun onDown(motionEvent: MotionEvent) = false
    override fun onShowPress(motionEvent: MotionEvent) = Unit
    override fun onSingleTapUp(motionEvent: MotionEvent) = true
    override fun onScroll(mE: MotionEvent, mE1: MotionEvent, v: Float, v1: Float) = false
    override fun onLongPress(motionEvent: MotionEvent) = Unit
    override fun onFling(mE: MotionEvent, mE1: MotionEvent, v: Float, v1: Float) = false
}
