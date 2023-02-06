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
//
//class DefaultSprayToolOptionsView(rootView: ViewGroup) : SprayToolOptionsView {
//
//    private var callback = null
//    private val binding: DialogPocketpaintSprayToolBinding
//    private val radiusText: TextInputEditText
//    private val radiusSeekBar: SeekBar
//
//    companion object {
//        private val TAG = DefaultSprayToolOptionsView::class.java.simpleName
//    }
//
//    init {
//        binding = DataBindingUtil.inflate(LayoutInflater.from(rootView.context),
//            R.layout.dialog_pocketpaint_spray_tool, rootView, false)
//        radiusText = binding.pocketpaintRadiusText
//        radiusSeekBar = binding.pocketpaintSprayRadiusSeekBar
//        initializeListeners()
//    }
//
//    private fun initializeListeners() {
//        radiusSeekBar.progress = DEFAULT_RADIUS
//        radiusText.setText(radiusSeekBar.progress.toString())
//        radiusText.filters = arrayOf<InputFilter>(DefaultNumberRangeFilter(MIN_RADIUS, MAX_RADIUS))
//        radiusText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                val sizeText: String = radiusText.text.toString()
//                val sizeTextInt = try {
//                    sizeText.toInt()
//                } catch (exp: NumberFormatException) {
//                    exp.localizedMessage?.let {
//                        Log.d(TAG, it)
//                    }
//                    MIN_RADIUS
//                }
//                radiusSeekBar.progress = sizeTextInt
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
//                Unit
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
//        })
//
//        radiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//
//            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//                var progressValue = progress
//                if (progressValue


import android.graphics.Paint
import android.renderscript.ScriptGroup.Binding
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.google.android.material.textfield.TextInputEditText
import org.catrobat.paintroid.R
import org.catrobat.paintroid.databinding.DialogPocketpaintSprayToolBinding
import org.catrobat.paintroid.tools.helper.DefaultNumberRangeFilter
import org.catrobat.paintroid.tools.options.SprayToolOptionsView

const val MIN_RADIUS = 1
private const val DEFAULT_RADIUS = 5
private const val MAX_RADIUS = 100

class DefaultSprayToolOptionsView(rootView: ViewGroup) : SprayToolOptionsView {

    private var callback: SprayToolOptionsView.Callback? = null
    private val radiusText: TextInputEditText
    private val radiusSeekBar: SeekBar
    private lateinit var binding:DialogPocketpaintSprayToolBinding
    companion object {
        private val TAG = DefaultSprayToolOptionsView::class.java.simpleName
    }

    init {
        val inflater = LayoutInflater.from(rootView.context)
        val sprayToolOptionsView: View =
            inflater.inflate(R.layout.dialog_pocketpaint_spray_tool , rootView)
        binding = DialogPocketpaintSprayToolBinding.bind(sprayToolOptionsView)
        radiusText = binding.pocketpaintRadiusText
        radiusSeekBar = binding.pocketpaintSprayRadiusSeekBar
        initializeListeners()
    }

    private fun initializeListeners() {
        radiusSeekBar.progress = DEFAULT_RADIUS
        radiusText.setText(radiusSeekBar.progress.toString())
        radiusText.filters = arrayOf<InputFilter>(DefaultNumberRangeFilter(MIN_RADIUS, MAX_RADIUS))
        radiusText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val sizeText: String = radiusText.text.toString()
                val sizeTextInt = try {
                    sizeText.toInt()
                } catch (exp: NumberFormatException) {
                    exp.localizedMessage?.let {
                        Log.d(TAG, it)
                    }
                    MIN_RADIUS
                }
                radiusSeekBar.progress = sizeTextInt
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })

        radiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                var progressValue = progress
                if (progressValue < MIN_RADIUS) {
                    progressValue = MIN_RADIUS
                    radiusSeekBar.progress = progressValue
                }

                if (fromUser) {
                    radiusText.setText(progressValue.toString())
                }

                callback?.radiusChanged(progressValue)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })
    }

    override fun setCallback(callback: SprayToolOptionsView.Callback?) {
        this.callback = callback
    }

    override fun setRadius(radius: Int) {
        radiusSeekBar.progress = radius
    }

    override fun setCurrentPaint(paint: Paint) {
        setRadius(paint.strokeWidth.toInt())
    }
}
