package org.catrobat.paintroid.tools.options

import android.view.View

interface PixelationToolOptionsView {
    fun invalidate ()

    fun getTopToolOptions(): View

    fun getBottomToolOptions(): View

    fun setPixelPreviewListener(onPixelationPreviewListener: OnPixelationPreviewListener)

    interface OnPixelationPreviewListener{
        fun setPixelWidth(widthPixels : Float)

        fun setPixelHeight(heightPixels : Float)

        fun setNumCollor(collorNum : Float)
    }
}



