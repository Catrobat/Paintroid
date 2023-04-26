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
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.contract.LayerContracts

class CutCommand(
    val toolPosition: Point,
    val boxWidth: Float,
    val boxHeight: Float,
    val boxRotation: Float
) : Command {
    private val boxRect = RectF(-boxWidth / 2f, -boxHeight / 2f, boxWidth / 2f, boxHeight / 2f)
    private val paint: Paint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        alpha = 0
    }

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        canvas.save()
        canvas.translate(toolPosition.x.toFloat(), toolPosition.y.toFloat())
        canvas.rotate(boxRotation)
        canvas.drawRect(boxRect, paint)
        canvas.restore()
    }

    override fun freeResources() {
        // No resources to free
    }
}
