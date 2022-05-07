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
package org.catrobat.paintroid.ui.tools

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import org.catrobat.paintroid.R
import org.catrobat.paintroid.tools.options.TransformToolOptionsView
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale

private const val HUNDRED = 100

class DefaultTransformToolOptionsView(rootView: ViewGroup) : TransformToolOptionsView {
    private val heightTextWatcher: TransformToolSizeTextWatcher
    private val widthTextWatcher: TransformToolSizeTextWatcher
    private val widthEditText: AppCompatEditText
    private val heightEditText: AppCompatEditText
    private val resizeSeekBar: AppCompatSeekBar
    private val percentageText: AppCompatTextView
    private var callback: TransformToolOptionsView.Callback? = null

    companion object {
        private val TAG = DefaultTransformToolOptionsView::class.java.simpleName
    }

    init {
        val inflater = LayoutInflater.from(rootView.context)
        val optionsView = inflater.inflate(R.layout.dialog_pocketpaint_transform_tool, rootView)
        widthEditText = optionsView.findViewById(R.id.pocketpaint_transform_width_value)
        heightEditText = optionsView.findViewById(R.id.pocketpaint_transform_height_value)
        resizeSeekBar = optionsView.findViewById(R.id.pocketpaint_transform_resize_seekbar)
        percentageText = optionsView.findViewById(R.id.pocketpaint_transform_resize_percentage_text)
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
        optionsView.findViewById<View>(R.id.pocketpaint_transform_auto_crop_btn)
            .setOnClickListener {
                callback?.autoCropClicked()
            }
        optionsView.findViewById<View>(R.id.pocketpaint_transform_set_center_btn)
            .setOnClickListener {
                callback?.setCenterClicked()
            }
        optionsView.findViewById<View>(R.id.pocketpaint_transform_rotate_left_btn)
            .setOnClickListener {
                callback?.rotateCounterClockwiseClicked()
            }
        optionsView.findViewById<View>(R.id.pocketpaint_transform_rotate_right_btn)
            .setOnClickListener {
                callback?.rotateClockwiseClicked()
            }
        optionsView.findViewById<View>(R.id.pocketpaint_transform_flip_horizontal_btn)
            .setOnClickListener {
                callback?.flipHorizontalClicked()
            }
        optionsView.findViewById<View>(R.id.pocketpaint_transform_flip_vertical_btn)
            .setOnClickListener {
                callback?.flipVerticalClicked()
            }
        optionsView.findViewById<View>(R.id.pocketpaint_transform_apply_resize_btn)
            .setOnClickListener {
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
                percentageText.text = String.format(Locale.getDefault(), "%d", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
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
}
