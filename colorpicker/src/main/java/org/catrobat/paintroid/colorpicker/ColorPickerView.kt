/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.colorpicker

import android.content.Context
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TabHost
import android.widget.TabHost.TabContentFactory
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import java.util.Objects

private const val RGB_TAG = "RGB"
private const val PRE_TAG = "PRE"
private const val HSV_TAG = "HSV"
private const val EXCEPTION = "Invalid TAG"

class ColorPickerView : LinearLayoutCompat {
    private val rgbSelectorView: RgbSelectorView
    private val preSelectorView: PresetSelectorView
    private val hsvSelectorView: HSVSelectorView
    private var alphaSliderView: AlphaSliderView
    private val tabHost: TabHost
    private var selectedColor = Color.BLACK
    var initialColor = 0
    private var listener: OnColorChangedListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        val tabView = inflate(context, R.layout.color_picker_colorselectview, null)
        addView(tabView)
        rgbSelectorView = RgbSelectorView(context)
        preSelectorView = PresetSelectorView(context)
        hsvSelectorView = HSVSelectorView(context)
        alphaSliderView = AlphaSliderView(context)
        tabHost = tabView.findViewById(R.id.color_picker_colorview_tabColors)
        tabHost.setup()
        val factory = ColorTabContentFactory()
        val preTabView = createTabView(context, R.drawable.ic_color_picker_tab_preset)
        val preTab = tabHost.newTabSpec(PRE_TAG)
            .setIndicator(preTabView)
            .setContent(factory)
        val hsvTabView = createTabView(context, R.drawable.ic_color_picker_tab_hsv)
        val hsvTab = tabHost.newTabSpec(HSV_TAG)
            .setIndicator(hsvTabView)
            .setContent(factory)
        val rgbTabView = createTabView(context, R.drawable.ic_color_picker_tab_rgba)
        val rgbTab = tabHost.newTabSpec(RGB_TAG)
            .setIndicator(rgbTabView)
            .setContent(factory)
        tabHost.run {
            addTab(preTab)
            addTab(hsvTab)
            addTab(rgbTab)
            setOnTabChangedListener { tabId ->
                if (tabId == rgbTab.tag) {
                    alphaSliderView.visibility = GONE
                    showKeyboard()
                } else {
                    if (!alphaSliderView.isCatroid) {
                        alphaSliderView.visibility = VISIBLE
                    }
                    hideKeyboard()
                }
            }
        }
    }

    fun setAlphaSlider(alphaSliderView: AlphaSliderView, catroidFlag: Boolean) {
        this.alphaSliderView = alphaSliderView
        this.alphaSliderView.isCatroid = catroidFlag
    }

    private fun createTabView(context: Context, iconResourceId: Int): View {
        val tabView = inflate(context, R.layout.color_picker_tab_image_only, null)
        val tabIcon = tabView.findViewById<AppCompatImageView>(R.id.color_picker_tab_icon)
        tabIcon.setBackgroundResource(iconResourceId)
        return tabView
    }

    fun setSelectedColor(color: Int, sender: View? = null) {
        if (selectedColor == color) {
            return
        }
        selectedColor = color
        if (sender !== rgbSelectorView) {
            rgbSelectorView.selectedColor = color
        }
        if (sender !== preSelectorView) {
            preSelectorView.setSelectedColor(color)
        }
        if (sender !== hsvSelectorView) {
            hsvSelectorView.setSelectedColor(color)
        }
        if (sender != alphaSliderView) {
            alphaSliderView.setSelectedColor(color)
        }
        onColorChanged()
    }

    fun getSelectedColor(): Int = selectedColor

    private fun hideKeyboard() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(rootView.windowToken, 0)
    }

    private fun showKeyboard() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(
            rootView.applicationWindowToken,
            InputMethodManager.SHOW_FORCED,
            0
        )
    }

    private fun onColorChanged() {
        listener?.colorChanged(selectedColor)
    }

    fun setOnColorChangedListener(listener: OnColorChangedListener?) {
        this.listener = listener
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return SavedState(superState, tabHost.currentTabTag)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            tabHost.setCurrentTabByTag(state.currentTabTag)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        preSelectorView.setOnColorChangedListener { color ->
            setSelectedColor(color, preSelectorView)
        }
        hsvSelectorView.hsvColorPickerView.setOnColorChangedListener { color ->
            setSelectedColor(color, hsvSelectorView)
        }
        rgbSelectorView.setOnColorChangedListener { color ->
            setSelectedColor(color, rgbSelectorView)
        }
        Objects.requireNonNull(alphaSliderView.getAlphaSlider())?.setOnColorChangedListener(
            object : AlphaSlider.OnColorChangedListener {
                override fun colorChanged(color: Int) {
                    setSelectedColor(color, alphaSliderView)
                }
            }
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        preSelectorView.setOnColorChangedListener(null)
        hsvSelectorView.hsvColorPickerView.setOnColorChangedListener(null)
        rgbSelectorView.setOnColorChangedListener(null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    internal inner class ColorTabContentFactory : TabContentFactory {
        override fun createTabContent(tag: String): View {
            return when (tag) {
                RGB_TAG -> rgbSelectorView
                PRE_TAG -> preSelectorView
                HSV_TAG -> hsvSelectorView
                else -> throw IllegalArgumentException(EXCEPTION)
            }
        }
    }

    internal class SavedState : BaseSavedState {
        var currentTabTag: String?

        constructor(source: Parcel) : super(source) {
            currentTabTag = source.readString()
        }

        constructor(superState: Parcelable?, currentTabTag: String?) : super(superState) {
            this.currentTabTag = currentTabTag
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeString(currentTabTag)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}
