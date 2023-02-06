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

import android.graphics.Paint
import android.graphics.Paint.Cap
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.google.android.material.chip.Chip
import org.catrobat.paintroid.R
import org.catrobat.paintroid.databinding.DialogPocketpaintStrokeBinding
import org.catrobat.paintroid.tools.helper.DefaultNumberRangeFilter
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.BrushToolOptionsView.OnBrushChangedListener
import org.catrobat.paintroid.tools.options.BrushToolOptionsView.OnBrushPreviewListener
import org.catrobat.paintroid.tools.options.BrushToolPreview
import java.lang.NumberFormatException
import java.util.Locale

private const val MIN_BRUSH_SIZE = 1
private const val MIN_VAL = 1
private const val MAX_VAL = 100

class DefaultBrushToolOptionsView(rootView: ViewGroup) : BrushToolOptionsView {
    private var brushSizeText: EditText
    private var brushWidthSeekBar: SeekBar
    private var buttonCircle: Chip
    private val buttonRect: Chip
    private var brushToolPreview: BrushToolPreview
    private var brushChangedListener: OnBrushChangedListener? = null
    private var currentView = rootView
    private val capsView: View
    private lateinit var binding:DialogPocketpaintStrokeBinding

    companion object {
        private val TAG = DefaultBrushToolOptionsView::class.java.simpleName
    }

    init {
        val inflater = LayoutInflater.from(rootView.context)
        val brushPickerView = inflater.inflate(R.layout.dialog_pocketpaint_stroke, rootView, true)
        binding = DialogPocketpaintStrokeBinding.bind(brushPickerView)
        brushPickerView.apply {
            buttonCircle = binding.pocketpaintStrokeIbtnCircle
            buttonRect = binding.pocketpaintStrokeIbtnRect
            brushWidthSeekBar = binding.pocketpaintStrokeWidthSeekBar
            brushWidthSeekBar.setOnSeekBarChangeListener(OnBrushChangedWidthSeekBarListener())
            brushSizeText = binding.pocketpaintStrokeWidthWidthText
            brushToolPreview = binding.pocketpaintBrushToolPreview
            capsView = binding.pocketpaintStrokeTypes
        }
        brushSizeText.filters = arrayOf<InputFilter>(DefaultNumberRangeFilter(MIN_VAL, MAX_VAL))
        buttonCircle.setOnClickListener { onCircleButtonClicked() }
        buttonRect.setOnClickListener { onRectButtonClicked() }
        brushSizeText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) =
                Unit

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) = Unit

            override fun afterTextChanged(editable: Editable) {
                val sizeText = brushSizeText.text.toString()
                val sizeTextInt: Int = try {
                    sizeText.toInt()
                } catch (exp: NumberFormatException) {
                    exp.localizedMessage?.let {
                        Log.d(TAG, it)
                    }
                    MIN_BRUSH_SIZE
                }
                brushWidthSeekBar.progress = sizeTextInt
            }
        })
    }

    private fun onRectButtonClicked() {
        updateStrokeCap(Cap.SQUARE)
        buttonRect.isSelected = true
        buttonCircle.isSelected = false
        invalidate()
    }

    override fun hideCaps() {
        capsView.visibility = View.GONE
    }

    private fun onCircleButtonClicked() {
        updateStrokeCap(Cap.ROUND)
        buttonCircle.isSelected = true
        buttonRect.isSelected = false
        invalidate()
    }

    fun adjustOptionsView() {
        val inflater = LayoutInflater.from(currentView.context)
        val brushPickerView = inflater.inflate(R.layout.dialog_pocketpaint_stroke, currentView, true)
        val binding2 = DialogPocketpaintStrokeBinding.bind(brushPickerView)
        brushPickerView.apply {
            brushWidthSeekBar = binding2.pocketpaintStrokeWidthSeekBar
            brushWidthSeekBar.setOnSeekBarChangeListener(OnBrushChangedWidthSeekBarListener())
            brushSizeText = binding2.pocketpaintStrokeWidthWidthText
            brushToolPreview = binding2.pocketpaintBrushToolPreview
        }
    }

    override fun setCurrentPaint(paint: Paint) {
        if (paint.strokeCap == Cap.ROUND) {
            buttonCircle.isSelected = true
            buttonRect.isSelected = false
        } else {
            buttonCircle.isSelected = false
            buttonRect.isSelected = true
        }
        brushWidthSeekBar.progress = paint.strokeWidth.toInt()
        brushSizeText.setText(
            String.format(
                Locale.getDefault(), "%d",
                paint.strokeWidth
                    .toInt()
            )
        )
    }

    override fun setBrushChangedListener(onBrushChangedListener: OnBrushChangedListener) {
        this.brushChangedListener = onBrushChangedListener
    }

    override fun setBrushPreviewListener(onBrushPreviewListener: OnBrushPreviewListener) {
        brushToolPreview.setListener(onBrushPreviewListener)
        brushToolPreview.invalidate()
    }

    private fun updateStrokeWidthChange(strokeWidth: Int) {
        brushChangedListener?.setStrokeWidth(strokeWidth)
    }

    private fun updateStrokeCap(cap: Cap) {
        brushChangedListener?.setCap(cap)
    }

    override fun invalidate() {
        brushToolPreview.invalidate()
    }

    inner class OnBrushChangedWidthSeekBarListener : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            var currentProgress = progress
            if (currentProgress < MIN_BRUSH_SIZE) {
                currentProgress = MIN_BRUSH_SIZE
                seekBar.progress = currentProgress
            }
            updateStrokeWidthChange(currentProgress)
            if (fromUser) {
                brushSizeText.setText(String.format(Locale.getDefault(), "%d", currentProgress))
            }
            brushToolPreview.invalidate()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            brushSizeText.setText(String.format(Locale.getDefault(), "%d", seekBar.progress))
        }
    }
}
