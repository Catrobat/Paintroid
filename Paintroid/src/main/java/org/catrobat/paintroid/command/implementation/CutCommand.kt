package org.catrobat.paintroid.command.implementation

import android.graphics.*
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.contract.LayerContracts

class CutCommand(val toolPosition: Point,
                 val boxWidth: Float,
                 val boxHeight: Float,
                 val boxRotation: Float
) : Command {
    private val boxRect = RectF(-boxWidth / 2f, -boxHeight / 2f, boxWidth / 2f,boxHeight / 2f)
    private val paint: Paint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        alpha = 0
    }

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model?) {
        canvas.save()
        canvas.translate(toolPosition.x.toFloat(), toolPosition.y.toFloat())
        canvas.rotate(boxRotation)
        canvas.drawRect(boxRect, paint)
        canvas.restore()
    }

    override fun freeResources() {
        //No resources to free
    }
}
