/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.round

class ZoomableImageView : AppCompatImageView {

    companion object {
        private const val MAX_ZOOM = 10f
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
        private const val CLICK = 3
    }

    private var mMatrix = Matrix()
    private val values = FloatArray(9)

    private val startPoint = PointF()
    private val lastPoint = PointF()

    private var mBitmapWidth: Float = 0f
    private var mBitmapHeight: Float = 0f
    private var mImageViewWidth: Float = 0f
    private var mImageViewHeight: Float = 0f

    private var scaledWidth: Float = 0f
    private var scaledHeight: Float = 0f
    private var mScaleFactor: Float = 1f

    private val mMinZoom: Float = 1f
    private var widthDiff: Float = 0f
    private var heightDiff: Float = 0f

    private var mEventState = NONE

    private lateinit var mScaleDetector: ScaleGestureDetector
    private lateinit var listener: OnImageViewPointClickedListener

    constructor(context: Context, attr: AttributeSet?) : super(context, attr) {
        init(context)
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        super.setClickable(true)
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mMatrix.setTranslate(mScaleFactor, mScaleFactor)
        imageMatrix = mMatrix
        scaleType = ScaleType.MATRIX
    }

    fun setListener(listener: OnImageViewPointClickedListener) {
        this.listener = listener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mScaleDetector.onTouchEvent(event)
        mMatrix.getValues(values)
        val x = values[Matrix.MTRANS_X]
        val y = values[Matrix.MTRANS_Y]
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastPoint[event.x] = event.y
                startPoint.set(lastPoint)
                mEventState = DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                lastPoint[event.x] = event.y
                startPoint.set(lastPoint)
                mEventState = ZOOM
            }
            MotionEvent.ACTION_MOVE -> {
                if (mEventState == ZOOM || mEventState == DRAG && mScaleFactor > mMinZoom) {
                    var deltaX = event.x - lastPoint.x
                    var deltaY = event.y - lastPoint.y
                    scaledWidth = round(mImageViewWidth * mScaleFactor)
                    scaledHeight = round(mImageViewHeight * mScaleFactor)
                    if (scaledWidth < mImageViewWidth) {
                        deltaX = 0f
                        if (y + deltaY > 0) {
                            deltaY = -y
                        } else if (y + deltaY < -heightDiff) {
                            deltaY = -(y + heightDiff)
                        }
                    } else if (scaledHeight < mImageViewHeight) {
                        deltaY = 0f
                        if (x + deltaX > 0) {
                            deltaX = -x
                        } else if (x + deltaX < -widthDiff) {
                            deltaX = -(x + widthDiff)
                        }
                    } else {
                        if (x + deltaX > 0) {
                            deltaX = -x
                        } else if (x + deltaX < -widthDiff) {
                            deltaX = -(x + widthDiff)
                        }
                        if (y + deltaY > 0) {
                            deltaY = -y
                        } else if (y + deltaY < -heightDiff) {
                            deltaY = -(y + heightDiff)
                        }
                    }
                    mMatrix.postTranslate(deltaX, deltaY)
                    lastPoint[event.x] = event.y
                }
            }

            MotionEvent.ACTION_UP -> {
                mEventState = CLICK
                val xDiff = abs(event.x - startPoint.x).toInt()
                val yDiff = abs(event.y - startPoint.y).toInt()
                if (xDiff < CLICK && yDiff < CLICK) {
                    performClick()
                    performColorColorChanged(PointF(event.x, event.y))
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                mEventState = NONE
            }
        }
        imageMatrix = mMatrix
        invalidate()
        return true
    }

    private fun performColorColorChanged(point: PointF) {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val inverse = Matrix()
        imageMatrix.invert(inverse)
        val touchPoint = floatArrayOf(point.x, point.y)
        inverse.mapPoints(touchPoint)
        val xPixel = touchPoint[0].toInt()
        val yPixel = touchPoint[1].toInt()
        if (xPixel > bitmap.width || yPixel > bitmap.height || xPixel < 0 || yPixel < 0) {
            // clicked outside of the image frame
            return
        }

        val touchedPixelRGB = bitmap.getPixel(xPixel, yPixel)
        val colorValue = Color.argb(Color.alpha(touchedPixelRGB),
                Color.red(touchedPixelRGB),
                Color.green(touchedPixelRGB),
                Color.blue(touchedPixelRGB))

        listener.colorChanged(colorValue)
    }

    override fun setImageBitmap(bitmap: Bitmap) {
        super.setImageBitmap(bitmap)
        mBitmapWidth = bitmap.width.toFloat()
        mBitmapHeight = bitmap.height.toFloat()
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mEventState = ZOOM
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var scaleFactor = detector.scaleFactor
            val origScale = mScaleFactor
            mScaleFactor *= scaleFactor
            if (mScaleFactor > MAX_ZOOM) {
                mScaleFactor = MAX_ZOOM
                scaleFactor = MAX_ZOOM / origScale
            } else if (mScaleFactor < mMinZoom) {
                mScaleFactor = mMinZoom
                scaleFactor = mMinZoom / origScale
            }
            scaledWidth = round(mImageViewWidth * mScaleFactor)
            scaledHeight = round(mImageViewHeight * mScaleFactor)
            widthDiff = scaledWidth - mImageViewWidth
            heightDiff = scaledHeight - mImageViewHeight
            if (scaledWidth <= mImageViewWidth || scaledHeight <= mImageViewHeight) {
                mMatrix.postScale(scaleFactor, scaleFactor, mImageViewWidth / 2, mImageViewHeight / 2)
                if (scaleFactor < 1) {
                    mMatrix.getValues(values)
                    val x = values[Matrix.MTRANS_X]
                    val y = values[Matrix.MTRANS_Y]
                    if (scaleFactor < 1) {
                        if (round(scaledWidth) < mImageViewWidth) {
                            if (y < -heightDiff) mMatrix.postTranslate(0f, -(y + heightDiff)) else if (y > 0) mMatrix.postTranslate(0f, -y)
                        } else {
                            if (x < -widthDiff) mMatrix.postTranslate(-(x + widthDiff), 0f) else if (x > 0) mMatrix.postTranslate(-x, 0f)
                        }
                    }
                }
            } else {
                mMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                mMatrix.getValues(values)
                val x = values[Matrix.MTRANS_X]
                val y = values[Matrix.MTRANS_Y]
                if (scaleFactor < 1) {
                    if (x < -widthDiff) mMatrix.postTranslate(-(x + widthDiff), 0f) else if (x > 0) mMatrix.postTranslate(-x, 0f)
                    if (y < -heightDiff) mMatrix.postTranslate(0f, -(y + heightDiff)) else if (y > 0) mMatrix.postTranslate(0f, -y)
                }
            }
            return true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mImageViewWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        mImageViewHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        //Fit to screen.
        val scaleX = mImageViewWidth / mBitmapWidth
        val scaleY = mImageViewHeight / mBitmapHeight
        mMatrix.setScale(scaleX, scaleY)
        imageMatrix = mMatrix
    }
}
