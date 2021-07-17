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

package org.catrobat.paintroid.command.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.serialization.SerializableTypeface
import org.catrobat.paintroid.contract.LayerContracts

class TextToolCommand(
    multilineText: Array<String>,
    textPaint: Paint,
    boxOffset: Float,
    boxWidth: Float,
    boxHeight: Float,
    toolPosition: PointF,
    rotationAngle: Float,
    typeFaceInfo: SerializableTypeface
) : Command {

    var multilineText = multilineText.clone(); private set
    var textPaint = textPaint; private set
    var boxOffset = boxOffset; private set
    var boxWidth = boxWidth; private set
    var boxHeight = boxHeight; private set
    var toolPosition = toolPosition; private set
    var rotationAngle = rotationAngle; private set
    var typeFaceInfo = typeFaceInfo; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        val textDescent = textPaint.descent()
        val textAscent = textPaint.ascent()
        val textHeight = textDescent - textAscent
        val textBoxHeight = textHeight * multilineText.size + 2 * boxOffset
        var maxTextWidth = 0f
        multilineText.forEach { str ->
            val textWidth = textPaint.measureText(str)
            if (textWidth > maxTextWidth) {
                maxTextWidth = textWidth
            }
        }
        val textBoxWidth = maxTextWidth + 2 * boxOffset
        val textBitmap = Bitmap.createBitmap(
            textBoxWidth.toInt(), textBoxHeight.toInt(),
            Bitmap.Config.ARGB_8888
        )
        val textCanvas = Canvas(textBitmap)
        multilineText.forEachIndexed { index, str ->
            textCanvas.drawText(str, boxOffset, boxOffset - textAscent + textHeight * index, textPaint)
        }
        val srcRect = Rect(0, 0, textBoxWidth.toInt(), textBoxHeight.toInt())
        val dstRect = Rect(
            (-boxWidth / 2.0f).toInt(), (-boxHeight / 2.0f).toInt(),
            (boxWidth / 2.0f).toInt(), (boxHeight / 2.0f).toInt()
        )
        with(canvas) {
            save()
            translate(toolPosition.x, toolPosition.y)
            rotate(rotationAngle)
            drawBitmap(textBitmap, srcRect, dstRect, textPaint)
            restore()
        }
    }

    override fun freeResources() {
        // No resources to free
    }
}
