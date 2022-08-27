package org.catrobat.paintroid.colorpicker

import java.io.Serializable

const val COLOR_HISTORY_SIZE = 4

class ColorHistory : Serializable {
    private val colorHistory: ArrayList<Int> = arrayListOf()

    val colors: ArrayList<Int>
        get() = colorHistory

    fun addColor(color: Int) {
        if (colorHistory.lastOrNull() != color) {
            colorHistory.add(color)
        }

        if (colorHistory.size > COLOR_HISTORY_SIZE) {
            colorHistory.removeFirst()
        }
    }
}
