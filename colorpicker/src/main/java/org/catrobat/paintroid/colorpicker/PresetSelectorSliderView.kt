package org.catrobat.paintroid.colorpicker

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout

class PresetSelectorSliderView : LinearLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr)

    private var presetSelectorSlider : PresetSelectorSlider = PresetSelectorSlider(context)

    init {
        presetSelectorSlider = PresetSelectorSlider(context)
        presetSelectorSlider.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

        addView(presetSelectorSlider)
    }

    fun getPresetSelectorSlider(): PresetSelectorSlider {
        return presetSelectorSlider;
    }

    fun setSelectedColor(color: Int) {
        presetSelectorSlider.setSelectedColor(color)
    }
}