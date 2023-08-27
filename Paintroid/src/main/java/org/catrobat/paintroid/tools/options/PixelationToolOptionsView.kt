package org.catrobat.paintroid.tools.options

import android.view.View

interface PixelationToolOptionsView {
    fun invalidate ()

    fun getTopToolOptions(): View

    fun getBottomToolOptions(): View

    fun setPixelPreviewListener(onPixelationPreviewListener: OnPixelationPreviewListener)

    interface OnPixelationPreviewListener{
        val pixelNumWidth : Int
        val pixelNumHeight : Int
        val pixelNumColours : Int
    }
}



