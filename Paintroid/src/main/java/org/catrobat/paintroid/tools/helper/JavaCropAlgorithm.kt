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
package org.catrobat.paintroid.tools.helper

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.util.Log

private const val TAG = "cropAlgorithmSnail"

class JavaCropAlgorithm : CropAlgorithm {
    private fun containsOpaquePixel(
        pixels: Array<IntArray>,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        for (y in fromY..toY) {
            for (x in fromX..toX) {
                if (pixels[y][x] != Color.TRANSPARENT) {
                    return true
                }
            }
        }
        return false
    }

    private fun adjustTop(bounds: Rect, pixels: Array<IntArray>): Int {
        var top = bounds.top
        while (top <= bounds.bottom) {
            if (containsOpaquePixel(pixels, bounds.left, top, bounds.right, top)) {
                break
            }
            top++
        }
        return top
    }

    private fun adjustBottom(bounds: Rect, pixels: Array<IntArray>): Int {
        var bottom: Int = bounds.bottom
        while (bottom >= bounds.top) {
            if (containsOpaquePixel(pixels, bounds.left, bottom, bounds.right, bottom)) {
                break
            }
            bottom--
        }
        return bottom
    }

    private fun adjustLeft(bounds: Rect, pixels: Array<IntArray>): Int {
        var left: Int = bounds.left
        while (left <= bounds.right) {
            if (containsOpaquePixel(pixels, left, bounds.top, left, bounds.bottom)) {
                break
            }
            left++
        }
        return left
    }

    private fun adjustRight(bounds: Rect, pixels: Array<IntArray>): Int {
        var right: Int = bounds.right
        while (right >= bounds.left) {
            if (containsOpaquePixel(pixels, right, bounds.top, right, bounds.bottom)) {
                break
            }
            right--
        }
        return right
    }

    override fun crop(bitmap: Bitmap?): Rect? {
        if (bitmap == null) {
            Log.e(TAG, "bitmap is null!")
            return null
        }
        val pixels = Array(bitmap.height) { IntArray(bitmap.width) }
        for (i in 0 until bitmap.height) {
            bitmap.getPixels(pixels[i], 0, bitmap.width, 0, i, bitmap.width, 1)
        }
        val bounds = Rect(0, 0, bitmap.width - 1, bitmap.height - 1)
        val top = adjustTop(bounds, pixels)
        if (top > bounds.bottom) {
            Log.i(TAG, "nothing to crop")
            return null
        }
        bounds.top = top
        bounds.bottom = adjustBottom(bounds, pixels)
        bounds.left = adjustLeft(bounds, pixels)
        bounds.right = adjustRight(bounds, pixels)
        return bounds
    }
}
