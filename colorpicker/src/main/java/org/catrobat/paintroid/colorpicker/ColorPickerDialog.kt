/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.Shader.TileMode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager.LayoutParams.*
import androidx.annotation.ColorInt
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ColorPickerDialog : AppCompatDialogFragment(), OnColorChangedListener {
    @VisibleForTesting
    var onColorPickedListener = mutableListOf<OnColorPickedListener>()
    private lateinit var colorPickerView: ColorPickerView
    private lateinit var currentColorView: View
    private lateinit var newColorView: View
    private lateinit var pipetteBtn: MaterialButton
    private lateinit var checkeredShader: Shader
    private lateinit var currentBitmap: Bitmap

    private var colorToApply = 0

    companion object {
        private const val CURRENT_COLOR = "CurrentColor"
        private const val INITIAL_COLOR = "InitialColor"
        const val REQUEST_CODE = 1
        const val COLOR_EXTRA = "colorExtra"
        const val BITMAP_Name_EXTRA = "bitmapNameExtra"
        const val bitmapName = "temp.png"

        @JvmStatic
        fun newInstance(@ColorInt initialColor: Int): ColorPickerDialog {
            val dialog = ColorPickerDialog()
            val bundle = Bundle()
            bundle.putInt(INITIAL_COLOR, initialColor)
            bundle.putInt(CURRENT_COLOR, initialColor)
            dialog.arguments = bundle
            return dialog
        }
    }

    fun setBitmap(bitmap: Bitmap) {
        currentBitmap = bitmap
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = requireActivity().layoutInflater
                .inflate(R.layout.color_picker_dialog_view, null)
        currentColorView = dialogView.findViewById(R.id.color_picker_current_color_view)
        pipetteBtn = dialogView.findViewById(R.id.color_picker_pipette_btn)
        pipetteBtn.setOnClickListener {
            runBlocking {
                launch {
                    storeBitmapTemporally(currentBitmap, requireContext(), bitmapName)
                    val intent = Intent(it.context, ColorPickerPreviewActivity::class.java)
                    intent.putExtra(COLOR_EXTRA, colorToApply)
                    intent.putExtra(BITMAP_Name_EXTRA, bitmapName)
                    startActivityForResult(intent, REQUEST_CODE)
                }
            }
        }
        newColorView = dialogView.findViewById(R.id.color_picker_new_color_view)
        colorPickerView = dialogView.findViewById(R.id.color_picker_view)
        colorPickerView.setOnColorChangedListener(this)

        if (savedInstanceState != null) {
            setCurrentColor(savedInstanceState.getInt(CURRENT_COLOR, Color.BLACK))
            setInitialColor(savedInstanceState.getInt(INITIAL_COLOR, Color.BLACK))
        } else {
            setCurrentColor(arguments!!.getInt(CURRENT_COLOR, Color.BLACK))
            setInitialColor(arguments!!.getInt(INITIAL_COLOR, Color.BLACK))
        }
        colorToApply = colorPickerView.initialColor
        val materialDialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setNegativeButton(R.string.color_picker_cancel) { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                .setPositiveButton(R.string.color_picker_apply) { _: DialogInterface, _: Int ->
                    updateColorChange(colorToApply)
                    deleteBitmapFile(requireContext(), bitmapName)
                    dismiss()
                }
                .setView(dialogView)
                .create()

        materialDialog.setOnShowListener {
            materialDialog.window?.clearFlags(FLAG_NOT_FOCUSABLE or FLAG_ALT_FOCUSABLE_IM)
            materialDialog.window?.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }

        return materialDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val checkeredBitmap = BitmapFactory.decodeResource(resources, R.drawable.pocketpaint_checkeredbg)
        checkeredShader = BitmapShader(checkeredBitmap, TileMode.REPEAT, TileMode.REPEAT)
    }

    override fun colorChanged(color: Int) {
        setViewColor(newColorView, color)
        colorToApply = color
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_COLOR, colorPickerView.selectedColor)
        outState.putInt(INITIAL_COLOR, colorPickerView.initialColor)
    }

    fun addOnColorPickedListener(listener: OnColorPickedListener) {
        onColorPickedListener.add(listener)
    }

    private fun updateColorChange(color: Int) {
        onColorPickedListener.forEach {
            it.colorChanged(color)
        }
    }

    private fun setInitialColor(color: Int) {
        setViewColor(currentColorView, color)
        colorPickerView.initialColor = color
    }

    private fun setCurrentColor(color: Int) {
        setViewColor(newColorView, color)
        colorPickerView.selectedColor = color
    }

    override fun dismiss() {
        if (showsDialog) {
            super.dismiss()
        } else {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    internal class CustomColorDrawable private constructor(checkeredShader: Shader, @ColorInt color: Int) : ColorDrawable(color) {
        private var backgroundPaint: Paint? = null

        init {
            if (Color.alpha(getColor()) != 0xff) {
                backgroundPaint = Paint()
                backgroundPaint?.shader = checkeredShader
            }
        }

        companion object {
            @JvmStatic
            fun createDrawable(checkeredShader: Shader, @ColorInt color: Int): Drawable {
                return RippleDrawable(ColorStateList.valueOf(Color.WHITE),
                        CustomColorDrawable(checkeredShader, color), null)
            }
        }

        override fun draw(canvas: Canvas) {
            backgroundPaint?.let {
                canvas.drawRect(bounds, it)
            }
            super.draw(canvas)
        }
    }

    private fun setViewColor(view: View, color: Int) {
        view.background = CustomColorDrawable.createDrawable(checkeredShader, color)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            data?.getIntExtra(COLOR_EXTRA, 0)?.let {
                setCurrentColor(it)
            }
        }
    }
}
