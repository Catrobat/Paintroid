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
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import java.lang.IllegalArgumentException
import java.util.Locale
import kotlin.math.min

private const val HEX_COLOR_CODE_LENGTH = 9
private const val PERCENT_MAX_ALPHA = 2.55f

class RgbSelectorView : LinearLayoutCompat {
    private var seekBarRed: SeekBar
    private var seekBarGreen: SeekBar
    private var seekBarBlue: SeekBar
    private var seekBarAlpha: SeekBar
    private var textViewRed: AppCompatTextView
    private var textViewGreen: AppCompatTextView
    private var textViewBlue: AppCompatTextView
    private var textViewAlpha: AppCompatTextView
    private var editTextHex: AppCompatEditText
    private var onColorChangedListener: OnColorChangedListener? = null

    var selectedColor: Int
        get() = Color.argb(
            seekBarAlpha.progress,
            seekBarRed.progress,
            seekBarGreen.progress,
            seekBarBlue.progress
        )
        set(color) {
            val colorRed = Color.red(color)
            val colorGreen = Color.green(color)
            val colorBlue = Color.blue(color)
            val colorAlpha = Color.alpha(color)
            seekBarAlpha.progress = colorAlpha
            seekBarRed.progress = colorRed
            seekBarGreen.progress = colorGreen
            seekBarBlue.progress = colorBlue
            var currentCursorPosition = editTextHex.selectionStart
            editTextHex.tag = "changed programmatically"
            editTextHex.setText(
                String.format(
                    "#%02X%02X%02X%02X",
                    colorAlpha,
                    colorRed,
                    colorGreen,
                    colorBlue
                )
            )
            editTextHex.tag = null
            val editTextHexLength = editTextHex.text.toString().length
            currentCursorPosition = min(currentCursorPosition, editTextHexLength)
            editTextHex.setSelection(currentCursorPosition)
            setSelectedColorText(color)
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        val rgbView = inflate(context, R.layout.color_picker_layout_rgbview, null)
        addView(rgbView)
        with(rgbView) {
            seekBarRed = findViewById<View>(R.id.color_picker_color_rgb_seekbar_red) as SeekBar
            seekBarGreen = findViewById<View>(R.id.color_picker_color_rgb_seekbar_green) as SeekBar
            seekBarBlue = findViewById<View>(R.id.color_picker_color_rgb_seekbar_blue) as SeekBar
            seekBarAlpha = findViewById<View>(R.id.color_picker_color_rgb_seekbar_alpha) as SeekBar
            textViewRed = findViewById<View>(R.id.color_picker_rgb_red_value) as AppCompatTextView
            textViewBlue = findViewById<View>(R.id.color_picker_rgb_blue_value) as AppCompatTextView
            editTextHex = findViewById<View>(R.id.color_picker_color_rgb_hex) as AppCompatEditText
            textViewGreen =
                findViewById<View>(R.id.color_picker_rgb_green_value) as AppCompatTextView
            textViewAlpha =
                findViewById<View>(R.id.color_picker_rgb_alpha_value) as AppCompatTextView
        }
        resetTextColor()
        selectedColor = Color.BLACK
    }

    @SuppressWarnings("SwallowedException")
    private fun parseInputToCheckIfHEX(newText: String): Int =
        if (newText.length != HEX_COLOR_CODE_LENGTH || newText.substring(0, 1) != "#") {
            Constants.NOT_A_HEX_VALUE
        } else {
            try {
                Color.parseColor(newText)
            } catch (e: IllegalArgumentException) {
                Constants.NOT_A_HEX_VALUE
            }
        }

    private fun resetTextColor() {
        editTextHex.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.pocketpaint_color_picker_hex_correct_black
            )
        )
    }

    private fun setTextColorToRed() {
        editTextHex.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.pocketpaint_color_picker_hex_wrong_value_red
            )
        )
    }

    private fun setSelectedColorText(color: Int) {
        val colorRed = Color.red(color)
        val colorGreen = Color.green(color)
        val colorBlue = Color.blue(color)
        val alphaToPercent = (Color.alpha(color) / PERCENT_MAX_ALPHA).toInt()
        textViewRed.text = String.format(Locale.getDefault(), "%d", colorRed)
        textViewGreen.text = String.format(Locale.getDefault(), "%d", colorGreen)
        textViewBlue.text = String.format(Locale.getDefault(), "%d", colorBlue)
        textViewAlpha.text = String.format(Locale.getDefault(), "%d", alphaToPercent)
    }

    private fun onColorChanged(color: Int) {
        onColorChangedListener?.colorChanged(color)
    }

    fun setOnColorChangedListener(listener: OnColorChangedListener?) {
        onColorChangedListener = listener
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val seekBarListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val color = selectedColor
                selectedColor = color
                if (fromUser) {
                    onColorChanged(color)
                }
            }
        }
        seekBarRed.setOnSeekBarChangeListener(seekBarListener)
        seekBarGreen.setOnSeekBarChangeListener(seekBarListener)
        seekBarBlue.setOnSeekBarChangeListener(seekBarListener)
        seekBarAlpha.setOnSeekBarChangeListener(seekBarListener)
        editTextHex.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(newText: Editable) {
                if (editTextHex.tag == null) {
                    val color = parseInputToCheckIfHEX(newText.toString())
                    if (color != Constants.NOT_A_HEX_VALUE) {
                        selectedColor = color
                        onColorChanged(color)
                        resetTextColor()
                    } else {
                        setTextColorToRed()
                    }
                } else {
                    resetTextColor()
                }
            }
        })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        seekBarRed.setOnSeekBarChangeListener(null)
        seekBarGreen.setOnSeekBarChangeListener(null)
        seekBarBlue.setOnSeekBarChangeListener(null)
        seekBarAlpha.setOnSeekBarChangeListener(null)
    }

    interface OnColorChangedListener {
        fun colorChanged(color: Int)
    }
}
