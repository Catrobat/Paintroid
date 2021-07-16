package org.catrobat.paintroid.colorpicker

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout

class AlphaSliderView : LinearLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr)

    companion object {
        private var alphaSlider: AlphaSlider? = null
    }

    init {
        alphaSlider = AlphaSlider(context)
        if (alphaSlider != null) {
            alphaSlider?.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        addView(alphaSlider)
    }

    fun getAlphaSlider(): AlphaSlider? = alphaSlider

    fun setSelectedColor(color: Int) {
        alphaSlider?.setSelectedColor(color)
    }
}
