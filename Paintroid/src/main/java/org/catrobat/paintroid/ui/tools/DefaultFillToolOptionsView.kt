/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.paintroid.ui.tools

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSeekBar
import org.catrobat.paintroid.R
import org.catrobat.paintroid.databinding.DialogPocketpaintFillToolBinding
import org.catrobat.paintroid.tools.helper.DefaultNumberRangeFilter
import org.catrobat.paintroid.tools.implementation.DEFAULT_TOLERANCE_IN_PERCENT
import org.catrobat.paintroid.tools.options.FillToolOptionsView
import java.lang.NumberFormatException
import java.util.Locale

private const val MIN_VAL = 0
private const val MAX_VAL = 100
private lateinit var binding: DialogPocketpaintFillToolBinding

class DefaultFillToolOptionsView(toolSpecificOptionsLayout: ViewGroup) : FillToolOptionsView {
    private val colorToleranceSeekBar: AppCompatSeekBar
    private val colorToleranceEditText: AppCompatEditText
    private var callback: FillToolOptionsView.Callback? = null

    init {
        val inflater = LayoutInflater.from(toolSpecificOptionsLayout.context)

        val fillToolOptionsView =
            inflater.inflate(R.layout.dialog_pocketpaint_fill_tool, toolSpecificOptionsLayout)
        binding = DialogPocketpaintFillToolBinding.bind(fillToolOptionsView)
        colorToleranceSeekBar =
            binding.pocketpaintColorToleranceSeekBar as AppCompatSeekBar
        colorToleranceEditText =
            binding.pocketpaintFillToolDialogColorToleranceInput as AppCompatEditText
        colorToleranceEditText.filters =
            arrayOf<InputFilter>(DefaultNumberRangeFilter(MIN_VAL, MAX_VAL))
        colorToleranceSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    setColorToleranceText(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })

        colorToleranceEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable) {
                try {
                    val colorToleranceInPercent = s.toString().toInt()
                    colorToleranceSeekBar.progress = colorToleranceInPercent
                    updateColorTolerance(colorToleranceInPercent)
                } catch (e: NumberFormatException) {
                    Log.e("Error parsing tolerance", "result was null")
                }
            }
        })

        setColorToleranceText(DEFAULT_TOLERANCE_IN_PERCENT)
    }

    private fun updateColorTolerance(colorTolerance: Int) {
        callback?.onColorToleranceChanged(colorTolerance)
    }

    private fun setColorToleranceText(toleranceInPercent: Int) {
        colorToleranceEditText.setText(String.format(Locale.getDefault(), "%d", toleranceInPercent))
    }

    override fun setCallback(callback: FillToolOptionsView.Callback) {
        this.callback = callback
    }
}
