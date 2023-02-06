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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import org.catrobat.paintroid.R
import org.catrobat.paintroid.databinding.DialogPocketpaintShapesBinding
import org.catrobat.paintroid.databinding.DialogPocketpaintSmudgeToolBinding
import org.catrobat.paintroid.databinding.DialogPocketpaintTextToolBinding
import org.catrobat.paintroid.tools.drawable.DrawableShape
import org.catrobat.paintroid.tools.drawable.DrawableStyle
import org.catrobat.paintroid.tools.helper.DefaultNumberRangeFilter
import org.catrobat.paintroid.tools.options.ShapeToolOptionsView
import java.lang.NumberFormatException
import java.util.Locale

private const val MIN_STROKE_WIDTH = 1
private const val STARTING_OUTLINE_WIDTH = 25
private const val MIN_VAL = 1
private const val MAX_VAL = 100
private lateinit var binding:DialogPocketpaintShapesBinding

class DefaultShapeToolOptionsView(rootView: ViewGroup) : ShapeToolOptionsView {
    private var callback: ShapeToolOptionsView.Callback? = null
    private val squareButton: AppCompatImageButton
    private val circleButton: AppCompatImageButton
    private val heartButton: AppCompatImageButton
    private val starButton: AppCompatImageButton
    private val fillButton: AppCompatImageButton
    private val outlineButton: AppCompatImageButton
    private val outlineView: View
    private val outlineTextView: AppCompatTextView
    private val outlineWidthSeekBar: AppCompatSeekBar
    private val outlineWidthEditText: AppCompatEditText
    private val shapeToolDialogTitle: AppCompatTextView
    private val shapeToolFillOutline: AppCompatTextView

    init {
        val inflater = LayoutInflater.from(rootView.context)
        val shapeToolView = inflater.inflate(R.layout.dialog_pocketpaint_shapes, rootView)
        binding  = DialogPocketpaintShapesBinding.bind(shapeToolView)
        shapeToolView.run {
            squareButton = org.catrobat.paintroid.ui.tools.binding.pocketpaintShapesSquareBtn as AppCompatImageButton
            circleButton = binding.pocketpaintShapesCircleBtn as AppCompatImageButton
            heartButton = binding.pocketpaintShapesHeartBtn as AppCompatImageButton
            starButton = binding.pocketpaintShapesStarBtn as AppCompatImageButton
            fillButton = binding.pocketpaintShapeIbtnFill as AppCompatImageButton
            outlineButton = binding.pocketpaintShapeIbtnOutline as AppCompatImageButton
            shapeToolDialogTitle = binding.pocketpaintShapeToolDialogTitle as AppCompatTextView
            shapeToolFillOutline = binding.pocketpaintShapeToolFillOutline as AppCompatTextView
            outlineView = binding.pocketpaintOutlineViewBorder
            outlineTextView = binding.pocketpaintOutlineViewTextView as AppCompatTextView
            outlineWidthSeekBar = binding.pocketpaintShapeStrokeWidthSeekBar as AppCompatSeekBar
            outlineWidthEditText = binding.pocketpaintShapeOutlineEdit as AppCompatEditText
        }
        outlineWidthEditText.filters =
            arrayOf<InputFilter>(DefaultNumberRangeFilter(MIN_VAL, MAX_VAL))
        outlineWidthEditText.setText(STARTING_OUTLINE_WIDTH.toString())
        outlineWidthSeekBar.progress = STARTING_OUTLINE_WIDTH
        initializeListeners()
        setShapeActivated(DrawableShape.RECTANGLE)
        setDrawTypeActivated(DrawableStyle.FILL)
    }

    private fun initializeListeners() {
        squareButton.setOnClickListener { onShapeClicked(DrawableShape.RECTANGLE) }
        circleButton.setOnClickListener { onShapeClicked(DrawableShape.OVAL) }
        heartButton.setOnClickListener { onShapeClicked(DrawableShape.HEART) }
        starButton.setOnClickListener { onShapeClicked(DrawableShape.STAR) }
        fillButton.setOnClickListener { onDrawTypeClicked(DrawableStyle.FILL) }
        outlineButton.setOnClickListener { onDrawTypeClicked(DrawableStyle.STROKE) }
        outlineWidthSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                var currentProgress = progress
                if (currentProgress < MIN_STROKE_WIDTH) {
                    currentProgress = MIN_STROKE_WIDTH
                }
                if (fromUser) {
                    seekBar.progress = currentProgress
                }
                outlineWidthEditText.setText(currentProgress.toString())
                onOutlineWidthChanged(currentProgress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })
        outlineWidthEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) =
                Unit

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) = Unit

            override fun afterTextChanged(editable: Editable) {
                val sizeText = outlineWidthEditText.text.toString()
                val sizeTextInt: Int = try {
                    sizeText.toInt()
                } catch (exp: NumberFormatException) {
                    MIN_STROKE_WIDTH
                }
                outlineWidthSeekBar.progress = sizeTextInt
            }
        })
    }

    private fun onShapeClicked(shape: DrawableShape) {
        callback?.setToolType(shape)
        setShapeActivated(shape)
    }

    private fun onDrawTypeClicked(drawType: DrawableStyle) {
        callback?.setDrawType(drawType)
        setDrawTypeActivated(drawType)
    }

    private fun onOutlineWidthChanged(outlineWidth: Int) {
        callback?.setOutlineWidth(outlineWidth)
    }

    private fun resetShapeActivated() {
        val buttons = arrayOf<View>(squareButton, circleButton, heartButton, starButton)
        for (button in buttons) {
            button.isSelected = false
        }
    }

    private fun resetDrawTypeActivated() {
        fillButton.isSelected = false
        outlineButton.isSelected = false
    }

    private fun setShapeToolDialogTitle(title: Int) {
        shapeToolDialogTitle.setText(title)
    }

    override fun setShapeActivated(shape: DrawableShape) {
        resetShapeActivated()
        when (shape) {
            DrawableShape.RECTANGLE -> {
                squareButton.isSelected = true
                setShapeToolDialogTitle(R.string.shape_tool_dialog_rect_title)
            }
            DrawableShape.OVAL -> {
                circleButton.isSelected = true
                setShapeToolDialogTitle(R.string.shape_tool_dialog_ellipse_title)
            }
            DrawableShape.HEART -> {
                heartButton.isSelected = true
                setShapeToolDialogTitle(R.string.shape_tool_dialog_heart_title)
            }
            DrawableShape.STAR -> {
                starButton.isSelected = true
                setShapeToolDialogTitle(R.string.shape_tool_dialog_star_title)
            }
        }
    }

    private fun setupUI(
        fillTitle: Int,
        squareButtonResource: Int,
        circleButtonResource: Int,
        heartButtonResource: Int,
        starButtonResource: Int,
        visibility: Int
    ) {
        shapeToolFillOutline.setText(fillTitle)
        squareButton.setImageResource(squareButtonResource)
        circleButton.setImageResource(circleButtonResource)
        heartButton.setImageResource(heartButtonResource)
        starButton.setImageResource(starButtonResource)
        outlineWidthSeekBar.visibility = visibility
        outlineWidthEditText.visibility = visibility
        outlineView.visibility = visibility
        outlineTextView.visibility = visibility
    }

    override fun setDrawTypeActivated(drawType: DrawableStyle) {
        resetDrawTypeActivated()
        when (drawType) {
            DrawableStyle.FILL -> {
                fillButton.isSelected = true
                setupUI(
                    R.string.shape_tool_dialog_fill_title,
                    R.drawable.ic_pocketpaint_rectangle,
                    R.drawable.ic_pocketpaint_circle,
                    R.drawable.ic_pocketpaint_heart,
                    R.drawable.ic_pocketpaint_star,
                    View.GONE
                )
            }
            DrawableStyle.STROKE -> {
                outlineButton.isSelected = true
                setupUI(
                    R.string.shape_tool_dialog_outline_title,
                    R.drawable.ic_pocketpaint_rectangle_out,
                    R.drawable.ic_pocketpaint_circle_out,
                    R.drawable.ic_pocketpaint_heart_out,
                    R.drawable.ic_pocketpaint_star_out,
                    View.VISIBLE
                )
            }
        }
    }

    override fun setShapeOutlineWidth(outlineWidth: Int) {
        outlineWidthSeekBar.progress = outlineWidth
        outlineWidthEditText.setText(String.format(Locale.getDefault(), "%d", outlineWidth))
    }

    override fun setCallback(callback: ShapeToolOptionsView.Callback) {
        this.callback = callback
    }
}
