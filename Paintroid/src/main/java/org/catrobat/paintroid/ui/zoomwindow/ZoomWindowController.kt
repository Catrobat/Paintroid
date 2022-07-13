package org.catrobat.paintroid.ui.zoomwindow

import android.graphics.PointF

interface ZoomWindowController {
    fun show(coordinates: PointF)

    fun dismiss()

    fun dismissOnPinch()
}