package org.catrobat.paintroid.ui.zoomwindow

import android.graphics.Bitmap
import android.graphics.PointF
import org.catrobat.paintroid.tools.Tool

interface ZoomWindowController {
    fun show(coordinates: PointF)

    fun dismiss()

    fun dismissOnPinch()

    fun onMove(coordinates: PointF)

    fun getBitmap(bitmap: Bitmap?)

    fun checkIfToolCompatibleWithZoomWindow(tool: Tool?): DefaultZoomWindowController.Constants
}
