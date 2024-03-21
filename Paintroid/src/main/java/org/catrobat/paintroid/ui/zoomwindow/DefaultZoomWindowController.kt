package org.catrobat.paintroid.ui.zoomwindow

import android.graphics.PointF
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.UserPreferences
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace

class DefaultZoomWindowController
    (val activity: MainActivity,
    val layerModel: LayerContracts.Model,
    val workspace: Workspace,
    val toolReference: ToolReference,
    val sharedPreferences: UserPreferences) :
    ZoomWindowController {

    private val zoomWindow: RelativeLayout = activity.findViewById(R.id.pocketpaint_zoom_window)
    private val zoomWindowImage: ImageView = activity.findViewById(R.id.pocketpaint_zoom_window_image)
    private var coordinates: PointF? = null
    private val zoomWindowDiameter = activity.resources.getDimensionPixelSize(R.dimen.pocketpaint_zoom_window_diameter)
    private var zoomWindowBitmap: Bitmap? = null

    override fun setBitmap(bitmap: Bitmap?) {
        zoomWindowImage.setImageBitmap(coordinates?.let { cropBitmap(bitmap, it) })
        zoomWindowBitmap = bitmap
    }

    override fun getBitmap(): Bitmap? = zoomWindowBitmap

    private fun cropBitmap(bitmap: Bitmap?, coordinates: PointF): Bitmap? {
        if (bitmap == null) return null

        val radius = getSizeOfZoomWindow() / 2
        val startX: Int = (coordinates.x - radius).toInt()
        val startY: Int = (coordinates.y - radius).toInt()

        val croppedBitmap: Bitmap = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(croppedBitmap)

        val paint = Paint().apply {
            isAntiAlias = true
        }

        val path = Path().apply {
            addCircle(radius.toFloat(), radius.toFloat(), radius.toFloat(), Path.Direction.CW)
        }

        canvas.clipPath(path)

        bitmap.let {
            canvas.drawBitmap(
                it,
                Rect(startX, startY, startX + radius * 2, startY + radius * 2),
                Rect(0, 0, radius * 2, radius * 2),
                paint
            )
        }

        return croppedBitmap
    }

    private fun getSizeOfZoomWindow(): Int {
        val zoomIndex = (sharedPreferences.preferenceZoomWindowZoomPercentage - initialZoomValue) / zoomPercentStepValue
        return zoomWindowDiameter - zoomIndex * zoomFactor
    }

    override fun show(drawingSurfaceCoordinates: PointF, displayCoordinates: PointF) {
        if (checkIfToolCompatibleWithZoomWindow(toolReference.tool) == Constants.COMPATIBLE &&
            isPointOnCanvas(drawingSurfaceCoordinates.x, drawingSurfaceCoordinates.y)) {
            setZoomWindowPosition(displayCoordinates)
            zoomWindow.visibility = View.VISIBLE
        }
    }

    override fun onMove(drawingSurfaceCoordinates: PointF, displayCoordinates: PointF) {
        if (checkIfToolCompatibleWithZoomWindow(toolReference.tool) == Constants.COMPATIBLE) {
            setZoomWindowPosition(displayCoordinates)
            if (isPointOnCanvas(drawingSurfaceCoordinates.x, drawingSurfaceCoordinates.y)) {
                if (zoomWindow.visibility == View.GONE) {
                    zoomWindow.visibility = View.VISIBLE
                }
                this.coordinates = drawingSurfaceCoordinates
            } else {
                dismiss()
            }
        }
    }

    private fun isPointOnCanvas(pointX: Float, pointY: Float): Boolean =
        pointX > 0 && pointX < layerModel.width && pointY > 0 && pointY < layerModel.height

    override fun dismiss() {
        zoomWindow.visibility = View.GONE
    }

    override fun dismissOnPinch() {
        zoomWindow.visibility = View.GONE
    }

    private fun setZoomWindowPosition(displayCoordinates: PointF) {
        if (shouldBeInTheRight(coordinates = displayCoordinates)) {
            setLayoutAlignment(right = true)
        } else {
            setLayoutAlignment(right = false)
        }
    }

    private fun shouldBeInTheRight(coordinates: PointF): Boolean =
        coordinates.x < activity.resources.displayMetrics.widthPixels / 2 &&
            coordinates.y < activity.resources.displayMetrics.heightPixels / 2

    private fun setLayoutAlignment(right: Boolean) {
        val params: RelativeLayout.LayoutParams =
            zoomWindowImage.layoutParams as RelativeLayout.LayoutParams
        if (right) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT)
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        }
        zoomWindowImage.layoutParams = params
    }

    override fun checkIfToolCompatibleWithZoomWindow(tool: Tool?): Constants {
        return when (tool?.toolType?.name) {
            ToolType.LINE.name,
            ToolType.CURSOR.name,
            ToolType.WATERCOLOR.name,
            ToolType.SPRAY.name,
            ToolType.BRUSH.name,
            ToolType.ERASER.name,
            ToolType.PIPETTE.name,
            ToolType.CLIP.name,
            ToolType.SMUDGE.name -> Constants.COMPATIBLE
            else -> Constants.NOT_COMPATIBLE
        }
    }

    enum class Constants {
        NOT_COMPATIBLE,
        COMPATIBLE
    }

    companion object {
        const val zoomFactor: Int = 25
        const val initialZoomValue: Int = 100
        const val zoomPercentStepValue = 50
    }
}
