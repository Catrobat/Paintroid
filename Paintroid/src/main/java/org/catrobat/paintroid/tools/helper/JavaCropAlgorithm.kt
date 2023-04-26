/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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

class JavaCropAlgorithm : CropAlgorithm {
    private lateinit var bounds: Rect
    private lateinit var bitmap: Bitmap

    private fun Rect.yRange() = this.top..this.bottom

    private fun Rect.xRange() = this.left..this.right

    private fun isOpaquePixel(x: Int, y: Int): Boolean = bitmap.getPixel(x, y) != Color.TRANSPARENT

    fun getTopmostCellWithOpaquePixel(): Int? =
        bounds.yRange().firstOrNull { y ->
            bounds.xRange()
                .firstOrNull { x -> isOpaquePixel(x, y) } != null
        }

    fun getBottommostCellWithOpaquePixel(): Int? =
        bounds.yRange().lastOrNull { y ->
            bounds.xRange()
                .firstOrNull { x -> isOpaquePixel(x, y) } != null
        }

    fun getLeftmostCellWithOpaquePixel(): Int? =
        bounds.xRange().firstOrNull { x ->
            bounds.yRange()
                .firstOrNull { y -> isOpaquePixel(x, y) } != null
        }

    fun getRightmostCellWithOpaquePixel(): Int? =
        bounds.xRange().lastOrNull { x ->
            bounds.yRange()
                .firstOrNull { y -> isOpaquePixel(x, y) } != null
        }

    fun init(bitmap: Bitmap) {
        this.bitmap = bitmap
        bounds = Rect(0, 0, bitmap.width - 1, bitmap.height - 1)
    }

    override fun crop(bitmap: Bitmap?): Rect? {
        this.bitmap = bitmap ?: return null
        bounds = Rect(0, 0, bitmap.width - 1, bitmap.height - 1)

        bounds.top = getTopmostCellWithOpaquePixel() ?: return null
        bounds.bottom = getBottommostCellWithOpaquePixel() ?: bounds.top - 1
        bounds.left = getLeftmostCellWithOpaquePixel() ?: bounds.right + 1
        bounds.right = getRightmostCellWithOpaquePixel() ?: bounds.left - 1

        return bounds
    }
}
