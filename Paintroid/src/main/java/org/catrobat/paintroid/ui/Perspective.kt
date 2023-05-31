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
package org.catrobat.paintroid.ui

import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import androidx.annotation.VisibleForTesting
import org.catrobat.paintroid.MainActivity
import kotlin.jvm.Synchronized
import kotlin.math.max
import kotlin.math.min

const val MIN_SCALE = 0.1f
const val MAX_SCALE = 100f
private const val SCROLL_BORDER = 50f

open class Perspective(private var bitmapWidth: Int, private var bitmapHeight: Int) {
    @JvmField
    var surfaceWidth = 0

    @JvmField
    var surfaceHeight = 0

    @VisibleForTesting
    @JvmField
    var surfaceCenterX = 0f

    @VisibleForTesting
    @JvmField
    var surfaceCenterY = 0f

    @VisibleForTesting
    var surfaceScale = 1f

    @JvmField
    var surfaceTranslationX = 0f

    @JvmField
    var surfaceTranslationY = 0f

    @VisibleForTesting
    var initialTranslationY = 0f

    @set:Synchronized
    var scale: Float
        get() = surfaceScale
        set(scale) {
            surfaceScale = max(MIN_SCALE, min(MAX_SCALE, scale))
        }

    val scaleForCenterBitmap: Float
        get() {
            var ratioDependentScale = 0f
            val displayHeight: Int? = mainActivity?.resources?.displayMetrics?.heightPixels
            var screenSizeRatio = 0f

            if (displayHeight != null) {
                screenSizeRatio = surfaceWidth.toFloat() / displayHeight.toFloat()
            }

            val bitmapSizeRatio = bitmapWidth.toFloat() / bitmapHeight
            if (screenSizeRatio > bitmapSizeRatio) {
                if (displayHeight != null) {
                    ratioDependentScale = displayHeight.toFloat() / bitmapHeight.toFloat()
                }
            } else {
                ratioDependentScale = surfaceWidth.toFloat() / bitmapWidth.toFloat()
            }

            ratioDependentScale = min(ratioDependentScale, 1f)
            ratioDependentScale = max(ratioDependentScale, MIN_SCALE)

            return ratioDependentScale
        }

    // counts to 2 at the start of the app. makes it so that the reset method will
    // be called at the start of the app in Drawingsurface.kt surfaceChanged.
    var callResetScaleAndTransformationOnStartUp = 0
    private var initialTranslationX = 0f
    var oldHeight = 0f
    var mainActivity: MainActivity? = null

    @Synchronized
    fun setSurfaceFrame(surfaceFrame: Rect) {
        if (surfaceHeight == 0) oldHeight = surfaceFrame.bottom.toFloat()
        surfaceFrame.apply {
            surfaceWidth = right
            surfaceCenterX = exactCenterX()
            surfaceHeight = bottom
            surfaceCenterY = getExactCenterYIgnoreWindowResize(surfaceFrame.exactCenterY())
        }
    }

    @Synchronized
    fun setBitmapDimensions(width: Int, height: Int) {
        bitmapWidth = width
        bitmapHeight = height
    }

    @Synchronized
    fun resetScaleAndTranslation() {
        surfaceScale = 1f
        if (surfaceWidth == 0 || surfaceHeight == 0) {
            surfaceTranslationX = 0f
            surfaceTranslationY = 0f
        } else {
            surfaceTranslationX = surfaceWidth / 2f - bitmapWidth / 2f
            initialTranslationX = surfaceTranslationX
            surfaceTranslationY = surfaceHeight / 2f - bitmapHeight / 2f
            initialTranslationY = surfaceTranslationY
        }
        val zoomFactor = calculateZoomFactor()

        surfaceScale = scaleForCenterBitmap * zoomFactor
    }

    @Synchronized
    fun calculateZoomFactor(): Float {
        val displayHeight: Int? = mainActivity?.resources?.displayMetrics?.heightPixels
        if (bitmapHeight > bitmapWidth) {
            if (bitmapHeight > surfaceHeight) {
                return 1.0f
            } else {
                if (displayHeight != null) {
                    return displayHeight.toFloat() / bitmapHeight.toFloat()
                } else {
                    return surfaceHeight.toFloat() / bitmapHeight.toFloat()
                }
            }
        } else {
            if (bitmapWidth >= surfaceWidth) {
                return 1.0f
            } else {
                return surfaceWidth.toFloat() / bitmapWidth.toFloat()
            }
        }
    }

    @Synchronized
    fun multiplyScale(factor: Float) {
        scale = surfaceScale * factor
    }

    @Synchronized
    fun translate(dx: Float, dy: Float) {
        surfaceTranslationX += dx / surfaceScale
        surfaceTranslationY += dy / surfaceScale
        val xmax = bitmapWidth / 2f + (surfaceWidth / 2f - SCROLL_BORDER) / surfaceScale
        if (surfaceTranslationX > xmax + initialTranslationX) {
            surfaceTranslationX = xmax + initialTranslationX
        } else if (surfaceTranslationX < -xmax + initialTranslationX) {
            surfaceTranslationX = -xmax + initialTranslationX
        }
        val ymax = bitmapHeight / 2f + (surfaceHeight / 2f - SCROLL_BORDER) / surfaceScale
        if (surfaceTranslationY > ymax + initialTranslationY) {
            surfaceTranslationY = ymax + initialTranslationY
        } else if (surfaceTranslationY < -ymax + initialTranslationY) {
            surfaceTranslationY = -ymax + initialTranslationY
        }
    }

    @Synchronized
    fun convertToCanvasFromSurface(surfacePoint: PointF) {
        surfacePoint.x =
            (surfacePoint.x - surfaceCenterX) / surfaceScale + surfaceCenterX - surfaceTranslationX
        surfacePoint.y =
            (surfacePoint.y - surfaceCenterY) / surfaceScale + surfaceCenterY - surfaceTranslationY
    }

    @Synchronized
    fun convertToSurfaceFromCanvas(canvasPoint: PointF) {
        canvasPoint.x =
            (canvasPoint.x + surfaceTranslationX - surfaceCenterX) * surfaceScale + surfaceCenterX
        canvasPoint.y =
            (canvasPoint.y + surfaceTranslationY - surfaceCenterY) * surfaceScale + surfaceCenterY
    }

    @Synchronized
    fun getCanvasPointFromSurfacePoint(surfacePoint: PointF): PointF {
        val canvasPoint = PointF(surfacePoint.x, surfacePoint.y)
        convertToCanvasFromSurface(canvasPoint)
        return canvasPoint
    }

    @Synchronized
    fun getSurfacePointFromCanvasPoint(canvasPoint: PointF): PointF {
        val surfacePoint = PointF(canvasPoint.x, canvasPoint.y)
        convertToSurfaceFromCanvas(surfacePoint)
        return surfacePoint
    }

    @Synchronized
    fun applyToCanvas(canvas: Canvas) {
        canvas.scale(surfaceScale, surfaceScale, surfaceCenterX, surfaceCenterY)
        canvas.translate(surfaceTranslationX, surfaceTranslationY)
    }

    private fun getExactCenterYIgnoreWindowResize(actualExactCenterY: Float): Float {
        var exactCenterYIgnoreWindowResize = if (surfaceCenterY != 0.0f && surfaceCenterY > actualExactCenterY) {
            surfaceCenterY
        } else {
            actualExactCenterY
        }
        return exactCenterYIgnoreWindowResize
    }
}
