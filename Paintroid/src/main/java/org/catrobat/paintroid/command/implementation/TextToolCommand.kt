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

package org.catrobat.paintroid.command.implementation

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.serialization.SerializableTypeface
import org.catrobat.paintroid.common.ITALIC_FONT_BOX_ADJUSTMENT
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.tools.implementation.OUTLINED_FONT_WIDTH_ADJUSTMENT

const val TEXT_SIZE_MAGNIFICATION_FACTOR = 3f

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
        val textAscent = textPaint.ascent()
        val textDescent = textPaint.descent()
        val textHeight = (textDescent - textAscent) * multilineText.size
        val lineHeight = textHeight / multilineText.size
        var maxTextWidth = multilineText.maxOf { line ->
            textPaint.measureText(line)
        }

        if (typeFaceInfo.italic) {
            maxTextWidth *= ITALIC_FONT_BOX_ADJUSTMENT
        }

        with(canvas) {
            save()
            translate(toolPosition.x, toolPosition.y)
            rotate(rotationAngle)

            val widthScaling = (boxWidth - 2 * boxOffset) / maxTextWidth
            val heightScaling = (boxHeight - 2 * boxOffset) / textHeight
            canvas.scale(widthScaling, heightScaling)

            val scaledHeightOffset = boxOffset / heightScaling
            val scaledWidthOffset = boxOffset / widthScaling
            val scaledBoxWidth = boxWidth / widthScaling
            val scaledBoxHeight = boxHeight / heightScaling

            val fillPaint = Paint(textPaint)
            if (typeFaceInfo.outlined) fillPaint.color = Color.WHITE
            multilineText.forEachIndexed { index, textLine ->
                canvas.drawText(
                    textLine,
                    scaledWidthOffset - scaledBoxWidth / 2 / if (typeFaceInfo.italic) ITALIC_FONT_BOX_ADJUSTMENT else 1f,
                    -(scaledBoxHeight / 2) + scaledHeightOffset - textAscent + lineHeight * index,
                    fillPaint
                )
            }
            if (typeFaceInfo.outlined) {
                val outlinePaint = Paint(textPaint)
                val adjustedStrokeWidth = if (typeFaceInfo.outlineWidth == 0) 0f else java.lang.Float.max(textPaint.textSize / TEXT_SIZE_MAGNIFICATION_FACTOR * (typeFaceInfo.outlineWidth / OUTLINED_FONT_WIDTH_ADJUSTMENT), 1f)
                outlinePaint.style = Paint.Style.STROKE
                outlinePaint.strokeWidth = adjustedStrokeWidth
                multilineText.forEachIndexed { index, textLine ->
                    canvas.drawText(
                        textLine,
                        scaledWidthOffset - scaledBoxWidth / 2 / if (typeFaceInfo.italic) ITALIC_FONT_BOX_ADJUSTMENT else 1f,
                        -(scaledBoxHeight / 2) + scaledHeightOffset - textAscent + lineHeight * index,
                        outlinePaint
                    )
                }
            }
            restore()
        }
    }

    override fun freeResources() {
        // No resources to free
    }
}
