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

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSeekBar
import org.catrobat.paintroid.R
import org.catrobat.paintroid.databinding.DialogPocketpaintTransformToolBinding
import org.catrobat.paintroid.tools.helper.DefaultNumberRangeFilter
import org.catrobat.paintroid.tools.options.TransformToolOptionsView
import java.lang.NumberFormatException
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale

private const val HUNDRED = 100
private const val MIN_VAL = 1

class DefaultTransformToolOptionsView(rootView: ViewGroup) : TransformToolOptionsView {
    private val root: ViewGroup = rootView
    private val heightTextWatcher: TransformToolSizeTextWatcher
    private val widthTextWatcher: TransformToolSizeTextWatcher
    private val widthEditText: AppCompatEditText
    private val heightEditText: AppCompatEditText
    private val resizeSeekBar: AppCompatSeekBar
    private val percentageText: AppCompatEditText
    private var callback: TransformToolOptionsView.Callback? = null
    private lateinit var binding: DialogPocketpaintTransformToolBinding

    companion object {
        private val TAG = DefaultTransformToolOptionsView::class.java.simpleName
    }

    init {
        val inflater = LayoutInflater.from(root.context)
        val optionsView = inflater.inflate(R.layout.dialog_pocketpaint_transform_tool, rootView)
        binding = DialogPocketpaintTransformToolBinding.bind(optionsView)
        widthEditText = binding.pocketpaintTransformWidthValue as AppCompatEditText
        heightEditText = binding.pocketpaintTransformHeightValue as AppCompatEditText
        resizeSeekBar = binding.pocketpaintTransformResizeSeekbar as AppCompatSeekBar
        percentageText = binding.pocketpaintTransformResizePercentageText
        widthTextWatcher = object : TransformToolSizeTextWatcher() {
            override fun setValue(value: Float) {
                callback?.setBoxWidth(value)
            }
        }
        heightTextWatcher = object : TransformToolSizeTextWatcher() {
            override fun setValue(value: Float) {
                callback?.setBoxHeight(value)
            }
        }

        widthEditText.addTextChangedListener(widthTextWatcher)
        heightEditText.addTextChangedListener(heightTextWatcher)

        percentageText.filters = arrayOf<InputFilter>(DefaultNumberRangeFilter(MIN_VAL, HUNDRED))
        percentageText.setText(String.format(Locale.getDefault(), "%d", resizeSeekBar.progress))
        percentageText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) =
                Unit

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) =
                Unit

            override fun afterTextChanged(editable: Editable) {
                val percentageTextString = percentageText.text.toString()
                val percentageTextInt: Int = try {
                    percentageTextString.toInt()
                } catch (exp: NumberFormatException) {
                    exp.localizedMessage?.let {
                        Log.d(TAG, it)
                    }
                    MIN_VAL
                }
                resizeSeekBar.progress = percentageTextInt
                percentageText.setSelection(percentageText.length())
            }
        })
        binding.pocketpaintTransformAutoCropBtn
            .setOnClickListener {
                callback?.autoCropClicked()
            }
        binding.pocketpaintTransformSetCenterBtn
            .setOnClickListener {
                callback?.setCenterClicked()
            }
        binding.pocketpaintTransformRotateLeftBtn
            .setOnClickListener {
                callback?.rotateCounterClockwiseClicked()
            }
        binding.pocketpaintTransformRotateRightBtn
            .setOnClickListener {
                callback?.rotateClockwiseClicked()
            }
        binding.pocketpaintTransformFlipHorizontalBtn
            .setOnClickListener {
                callback?.flipHorizontalClicked()
            }
        binding.pocketpaintTransformFlipVerticalBtn
            .setOnClickListener {
                callback?.flipVerticalClicked()
            }
        binding.pocketpaintTransformApplyResizeBtn
            .setOnClickListener {
                hideKeyboard()

                callback?.applyResizeClicked(resizeSeekBar.progress)
                callback?.hideToolOptions()
                resizeSeekBar.progress = HUNDRED
            }
        resizeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress == 0) {
                    seekBar.progress = 1
                    return
                }
                percentageText.setText(String.format(Locale.getDefault(), "%d", progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                percentageText.setText(String.format(Locale.getDefault(), "%d", seekBar.progress))
            }
        })
    }

    override fun setWidthFilter(numberRangeFilter: NumberRangeFilter) {
        widthEditText.filters = arrayOf<InputFilter>(numberRangeFilter)
    }

    override fun setHeightFilter(numberRangeFilter: NumberRangeFilter) {
        heightEditText.filters = arrayOf<InputFilter>(numberRangeFilter)
    }

    override fun setCallback(callback: TransformToolOptionsView.Callback) {
        this.callback = callback
    }

    override fun setWidth(width: Int) {
        widthEditText.apply {
            removeTextChangedListener(widthTextWatcher)
            setText(width.toString())
            addTextChangedListener(widthTextWatcher)
        }
    }

    override fun setHeight(height: Int) {
        heightEditText.apply {
            removeTextChangedListener(heightTextWatcher)
            setText(height.toString())
            addTextChangedListener(heightTextWatcher)
        }
    }

    abstract class TransformToolSizeTextWatcher : TextWatcher {

        protected abstract fun setValue(value: Float)

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(editable: Editable) {
            var str = editable.toString()
            if (str.isEmpty()) {
                str = "1"
            }
            try {
                val value = NumberFormat.getIntegerInstance().parse(str)?.toFloat()
                value?.let { setValue(it) }
            } catch (e: ParseException) {
                e.message?.let { Log.e(TAG, it) }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(root.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}
