package org.catrobat.paintroid.tools.options

interface PixelationToolOptionsView {

    fun setPixelPreviewListener(onPixelationPreviewListener: OnPixelationPreviewListener)

    interface OnPixelationPreviewListener {
        fun setPixelWidth(widthPixels: Float)

        fun setPixelHeight(heightPixels: Float)

        fun setNumCollor(collorNum: Float)
    }
}
