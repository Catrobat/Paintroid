package org.catrobat.paintroid.ui.zoomwindow

import android.graphics.Bitmap
import android.graphics.PointF

interface ZoomWindowController {
    fun show(coordinates: PointF)

    fun dismiss()

    fun dismissOnPinch()

    fun onMove(coordinates: PointF)

    fun getBitmap(bitmap: Bitmap?)
}
