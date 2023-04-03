package org.catrobat.paintroid.ui.zoomwindow

import android.graphics.Bitmap
import android.graphics.PointF
import org.catrobat.paintroid.tools.Tool

interface ZoomWindowController {
    fun show(drawingSurfaceCoordinates: PointF, displayCoordinates: PointF)

    fun dismiss()

    fun dismissOnPinch()

    fun onMove(drawingSurfaceCoordinates: PointF, displayCoordinates: PointF)

    fun getBitmap(bitmap: Bitmap?)

    fun checkIfToolCompatibleWithZoomWindow(tool: Tool?): DefaultZoomWindowController.Constants
}
