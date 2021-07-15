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

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import org.catrobat.paintroid.R
import org.catrobat.paintroid.tools.ToolPaint

const val STROKE_25 = 25f

class DefaultToolPaint(context: Context) : ToolPaint {
    private val bitmapPaint = Paint()

    override val checkeredShader: Shader

    override val previewPaint: Paint = Paint()

    override val previewColor: Int
        get() = previewPaint.color

    override var color: Int
        get() = bitmapPaint.color
        set(color) {
            bitmapPaint.color = color
            previewPaint.set(bitmapPaint)
            previewPaint.xfermode = null
            if (Color.alpha(color) == 0) {
                previewPaint.shader = checkeredShader
                previewPaint.color = Color.BLACK
                bitmapPaint.xfermode = eraseXfermode
                bitmapPaint.alpha = 0
            } else {
                bitmapPaint.xfermode = null
            }
        }

    override var strokeWidth: Float
        get() = bitmapPaint.strokeWidth
        set(strokeWidth) {
            bitmapPaint.strokeWidth = strokeWidth
            previewPaint.strokeWidth = strokeWidth
            val antiAliasing = strokeWidth > 1
            bitmapPaint.isAntiAlias = antiAliasing
            previewPaint.isAntiAlias = antiAliasing
        }

    override var strokeCap: Cap
        get() = bitmapPaint.strokeCap
        set(strokeCap) {
            bitmapPaint.strokeCap = strokeCap
            previewPaint.strokeCap = strokeCap
        }

    override var paint: Paint
        get() = bitmapPaint
        set(paint) {
            bitmapPaint.set(paint)
            previewPaint.set(paint)
        }

    override val eraseXfermode: PorterDuffXfermode
        get() = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    init {
        val checkerboard =
            BitmapFactory.decodeResource(context.resources, R.drawable.pocketpaint_checkeredbg)
        checkeredShader = BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        bitmapPaint.reset()
        bitmapPaint.isAntiAlias = true
        bitmapPaint.color = Color.BLACK
        bitmapPaint.style = Paint.Style.STROKE
        bitmapPaint.strokeJoin = Paint.Join.ROUND
        bitmapPaint.strokeCap = Cap.ROUND
        bitmapPaint.strokeWidth = STROKE_25
        previewPaint.set(bitmapPaint)
    }
}
