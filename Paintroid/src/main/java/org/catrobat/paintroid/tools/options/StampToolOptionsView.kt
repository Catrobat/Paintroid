package org.catrobat.paintroid.tools.options

interface StampToolOptionsView {

    fun setCallback(callback: Callback?)

    fun enablePaste(enable: Boolean)

    interface Callback {

        fun copyClicked()

        fun cutClicked()

        fun pasteClicked()
    }
}