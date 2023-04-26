package org.catrobat.paintroid.command.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.contract.LayerContracts

class ClippingCommand(bitmap: Bitmap, pathBitmap: Bitmap) : Command {

    var bitmap: Bitmap? = bitmap.copy(bitmap.config, true); private set
    var pathBitmap: Bitmap? = pathBitmap.copy(pathBitmap.config, true); private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        val bitmapToDraw = bitmap
        bitmapToDraw ?: return
        val wholeRect = Rect(0, 0, bitmapToDraw.width, bitmapToDraw.height)
        layerModel.currentLayer?.bitmap?.eraseColor(Color.TRANSPARENT)
        val paint = Paint()
        with(canvas) {
            save()
            pathBitmap?.let { drawBitmap(it, null, wholeRect, null) }
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            bitmap?.let { drawBitmap(it, null, wholeRect, paint) }
            restore()
        }
    }
    override fun freeResources() {
        bitmap?.recycle()
        pathBitmap?.recycle()
    }
}
