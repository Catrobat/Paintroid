/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow

private const val MAXIMUM_COLORS_IN_HISTORY = 4
private const val COLOR_BUTTON_MARGIN = 2
private const val ALMOST_WHITE_THRESHOLD = 240
private const val ELEVATION_FOR_ALMOST_WHITE = 1.5f
private const val WEIGHT_FOR_COLOR_BUTTONS = 1f / MAXIMUM_COLORS_IN_HISTORY

class ColorHistoryView : LinearLayout {
    private var selectedColor = 0
    private var tableLayout: TableLayout
    private var onColorChangedListener: OnColorInHistoryChangedListener? = null
    private var textLayout: LinearLayout? = null

    var colorHistory: ArrayList<Int> = arrayListOf()

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setWillNotDraw(false)
        tableLayout = TableLayout(context, attrs).apply {
            gravity = Gravity.TOP
            orientation = VERTICAL
            isShrinkAllColumns = true
        }
        tableLayout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        drawHistory()
        addView(tableLayout)
    }

    private fun onColorChanged() {
        onColorChangedListener?.colorInHistoryChanged(selectedColor)
    }

    fun setOnColorInHistoryChangedListener(listener: OnColorInHistoryChangedListener?) {
        onColorChangedListener = listener
    }

    fun setTextLayout(textLayout: LinearLayout) {
        this.textLayout = textLayout
    }

    private fun drawHistory() {
        val presetButtonListener = OnClickListener { v ->
            selectedColor = (v as ColorPickerPresetColorButton).color
            onColorChanged()
        }
        val colorButtonsTableRow = TableRow(context)
        val colorButtonLayoutParameters = TableRow.LayoutParams()
        colorButtonLayoutParameters.setMargins(
            COLOR_BUTTON_MARGIN,
            COLOR_BUTTON_MARGIN,
            COLOR_BUTTON_MARGIN,
            COLOR_BUTTON_MARGIN
        )
        colorButtonLayoutParameters.width = 0
        colorButtonLayoutParameters.height = LayoutParams.WRAP_CONTENT
        colorButtonLayoutParameters.weight = WEIGHT_FOR_COLOR_BUTTONS

        for (color in colorHistory.reversed()) {
            val colorButton: View = ColorPickerPresetColorButton(context, color)
            colorButton.setOnClickListener(presetButtonListener)
            if (Color.red(color) > ALMOST_WHITE_THRESHOLD && Color.green(color) > ALMOST_WHITE_THRESHOLD && Color.blue(color) > ALMOST_WHITE_THRESHOLD) {
                colorButton.elevation = ELEVATION_FOR_ALMOST_WHITE
            }
            colorButtonsTableRow.addView(colorButton, colorButtonLayoutParameters)
        }
        if (colorHistory.isNotEmpty()) {
            repeat(MAXIMUM_COLORS_IN_HISTORY - colorHistory.size) {
                colorButtonsTableRow.addView(ColorPickerPresetColorButton(context, Color.WHITE), colorButtonLayoutParameters)
            }
            textLayout?.visibility = View.VISIBLE
        }
        tableLayout.removeAllViews()
        tableLayout.addView(colorButtonsTableRow)
    }

    fun updateColorHistory(updatedHistory: ArrayList<Int>) {
        colorHistory = updatedHistory
        drawHistory()
    }
}
