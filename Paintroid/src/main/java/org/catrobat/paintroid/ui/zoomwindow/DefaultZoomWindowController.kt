package org.catrobat.paintroid.ui.zoomwindow

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.PorterDuffXfermode
import android.graphics.PorterDuff
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Shader
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import kotlin.math.roundToInt

class DefaultZoomWindowController
    (val activity: MainActivity,
    val layerModel: LayerContracts.Model,
    val workspace: Workspace,
    val toolReference: ToolReference) :
    ZoomWindowController {

    private val canvasRect = Rect()
    private val checkeredPattern = Paint()
    private val framePaint = Paint()

    private val windowHeight =
        activity.resources.getDimensionPixelSize(R.dimen.pocketpaint_zoom_window_height)
    private val windowWidth =
        activity.resources.getDimensionPixelSize(R.dimen.pocketpaint_zoom_window_width)

    private val chequeredBackgroundBitmap =
        Bitmap.createBitmap(layerModel.width, layerModel.height, Bitmap.Config.ARGB_8888)

    private val greyBackgroundBitmap =
        Bitmap.createBitmap(
            layerModel.width + windowWidth,
            layerModel.height + windowHeight,
            Bitmap.Config.ARGB_8888
        )

    private val backgroundBitmap =
        Bitmap.createBitmap(
            layerModel.width + windowWidth,
            layerModel.height + windowHeight,
            Bitmap.Config.ARGB_8888
        )

    init {
        framePaint.color = Color.BLACK
        framePaint.style = Paint.Style.STROKE
        framePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
        val checkerboard =
            BitmapFactory.decodeResource(activity.resources, R.drawable.pocketpaint_checkeredbg)
        val shader = BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        checkeredPattern.shader = shader
        checkeredPattern.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)

        val backgroundCanvas: Canvas? = chequeredBackgroundBitmap?.let { Canvas(it) }

        canvasRect.set(0, 0, layerModel.width, layerModel.height)

        backgroundCanvas?.drawRect(canvasRect, checkeredPattern)
        backgroundCanvas?.drawRect(canvasRect, framePaint)

        val greyBackgroundCanvas = Canvas(greyBackgroundBitmap)
        greyBackgroundCanvas.drawColor(
            activity.resources.getColor(R.color.pocketpaint_main_drawing_surface_background)
        )

        val canvasBackground = Canvas(backgroundBitmap)

        canvasBackground.drawBitmap(greyBackgroundBitmap, Matrix(), null)
        canvasBackground.drawBitmap(
            chequeredBackgroundBitmap, windowWidth / 2f, windowHeight / 2f, null)
    }

    private val zoomWindow: RelativeLayout =
        activity.findViewById(R.id.pocketpaint_zoom_window)
    private val zoomWindowImage: ImageView =
        activity.findViewById(R.id.pocketpaint_zoom_window_image)
    private var coordinates: PointF? = null

    override fun show(drawingSurfaceCoordinates: PointF, displayCoordinates: PointF) {
        if (checkIfToolCompatibleWithZoomWindow(toolReference.tool) != Constants.NOT_COMPATIBLE &&
            isPointOnCanvas(drawingSurfaceCoordinates.x, drawingSurfaceCoordinates.y)) {
            setZoomWindowPosition(displayCoordinates)
            zoomWindow.visibility = View.VISIBLE
            zoomWindowImage.setImageBitmap(cropBitmap(workspace.bitmapOfAllLayers, drawingSurfaceCoordinates))
        }
    }

    override fun dismiss() {
        zoomWindow.visibility = View.GONE
    }

    override fun dismissOnPinch() {
        zoomWindow.visibility = View.GONE
    }

    override fun onMove(drawingSurfaceCoordinates: PointF, displayCoordinates: PointF) {
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

    private fun setZoomWindowPosition(displayCoordinates: PointF) {
        if (shouldBeInTheRight(coordinates = displayCoordinates)) {
            setLayoutAlignment(right = true)
        } else {
            setLayoutAlignment(right = false)
        }
    }

    override fun getBitmap(bitmap: Bitmap?) {
        zoomWindowImage.setImageBitmap(coordinates?.let { cropBitmap(bitmap, it) })
    }

    private fun isPointOnCanvas(pointX: Float, pointY: Float): Boolean =
        pointX > 0 && pointX < layerModel.width && pointY > 0 && pointY < layerModel.height

    private fun shouldBeInTheRight(coordinates: PointF): Boolean {
        if (coordinates.x < activity.resources.displayMetrics.widthPixels / 2 &&
            coordinates.y < activity.resources.displayMetrics.heightPixels / 2) {
            return true
        }
        return false
    }

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

    private fun cropBitmap(bitmap: Bitmap?, coordinates: PointF): Bitmap? {

        val bitmapWithBackground: Bitmap? = mergeBackground(bitmap)

        val startX: Int = coordinates.x.roundToInt()
        val startY: Int = coordinates.y.roundToInt()

        val croppedBitmap: Bitmap? =
            Bitmap.createBitmap(windowWidth, windowHeight, Bitmap.Config.ARGB_8888)

        val canvas: Canvas? = croppedBitmap?.let { Canvas(it) }

        val paint = Paint()
        paint.isAntiAlias = true

        val rect = Rect(0, 0, windowWidth, windowHeight)
        val rectF = RectF(rect)

        canvas?.drawOval(rectF, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        bitmapWithBackground?.let {
            canvas?.drawBitmap(it,
                Rect(startX, startY, startX + windowWidth, startY + windowHeight),
                rect,
                paint
            ) }

        return croppedBitmap
    }

    private fun mergeBackground(bitmap: Bitmap?): Bitmap? {

        val bitmapOverlay =
            Bitmap.createBitmap(
                layerModel.width + windowWidth,
                layerModel.height + windowHeight,
                Bitmap.Config.ARGB_8888
            )
        val canvas = Canvas(bitmapOverlay)

        canvas.drawBitmap(backgroundBitmap, Matrix(), null)

        if (toolReference.tool?.toolType?.name.equals(ToolType.LINE.name) ||
            toolReference.tool?.toolType?.name.equals(ToolType.CURSOR.name)) {
            layerModel.currentLayer?.bitmap?.let {
                canvas.drawBitmap(it, windowWidth / 2f, windowHeight / 2f, null)
            }
        }

        bitmap?.let { canvas.drawBitmap(it, windowWidth / 2f, windowHeight / 2f, null) }

        return bitmapOverlay
    }

    override fun checkIfToolCompatibleWithZoomWindow(tool: Tool?): Constants {
        if (
            tool?.toolType?.name.equals(ToolType.HAND.name) ||
            tool?.toolType?.name.equals(ToolType.FILL.name) ||
            tool?.toolType?.name.equals(ToolType.STAMP.name) ||
            tool?.toolType?.name.equals(ToolType.TRANSFORM.name)
        ) {
            return Constants.NOT_COMPATIBLE
        } else if (
            tool?.toolType?.name.equals(ToolType.IMPORTPNG.name) ||
            tool?.toolType?.name.equals(ToolType.SHAPE.name) ||
            tool?.toolType?.name.equals(ToolType.TEXT.name)
        ) {
            return Constants.NOT_COMPATIBLE
        } else if (
            tool?.toolType?.name.equals(ToolType.LINE.name) ||
            tool?.toolType?.name.equals(ToolType.CURSOR.name)
        ) {
            return Constants.COMPATIBLE_NEW
        } else {
            return Constants.COMPATIBLE_ALL
        }
    }

    enum class Constants {
        NOT_COMPATIBLE,
        COMPATIBLE_NEW,
        COMPATIBLE_ALL
    }
}
