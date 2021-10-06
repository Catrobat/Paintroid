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
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow

private const val MAXIMUM_COLOR_BUTTONS_IN_COLOR_ROW = 4
private const val COLOR_BUTTON_MARGIN = 2

class PresetSelectorView : LinearLayout {
    private var selectedColor = 0
    private var tableLayout: TableLayout
    private var onColorChangedListener: OnColorChangedListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        tableLayout = TableLayout(context, attrs).apply {
            gravity = Gravity.TOP
            orientation = VERTICAL
            isStretchAllColumns = true
            isShrinkAllColumns = true
        }
        val presetButtonListener = OnClickListener { v ->
            selectedColor = (v as ColorPickerPresetColorButton).color
            onColorChanged()
        }
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        var colorButtonsTableRow = TableRow(context)
        val colorButtonLayoutParameters = TableRow.LayoutParams()
        colorButtonLayoutParameters.setMargins(
            COLOR_BUTTON_MARGIN,
            COLOR_BUTTON_MARGIN,
            COLOR_BUTTON_MARGIN,
            COLOR_BUTTON_MARGIN
        )
        for (colorButtonIndexInRow in 0 until presetColors.length()) {
            val color = presetColors.getColor(colorButtonIndexInRow, Color.TRANSPARENT)
            val colorButton: View = ColorPickerPresetColorButton(context, color)
            colorButton.setOnClickListener(presetButtonListener)
            colorButtonsTableRow.addView(colorButton, colorButtonLayoutParameters)
            if ((colorButtonIndexInRow + 1) % MAXIMUM_COLOR_BUTTONS_IN_COLOR_ROW == 0) {
                tableLayout.addView(colorButtonsTableRow)
                colorButtonsTableRow = TableRow(context)
            }
        }
        presetColors.recycle()
        addView(tableLayout)
    }

    private fun getSelectedColor(): Int = selectedColor

    fun setSelectedColor(color: Int) {
        selectedColor = color
    }

    private fun onColorChanged() {
        onColorChangedListener?.colorChanged(getSelectedColor())
    }

    fun setOnColorChangedListener(listener: OnColorChangedListener?) {
        onColorChangedListener = listener
    }

    fun interface OnColorChangedListener {
        fun colorChanged(color: Int)
    }
}
