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

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Color
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.catrobat.paintroid.colorpicker.ColorPickerDialog.Companion.BITMAP_Name_EXTRA
import org.catrobat.paintroid.colorpicker.ColorPickerDialog.Companion.COLOR_EXTRA

class ColorPickerPreviewActivity : AppCompatActivity(), OnImageViewPointClickedListener {

    private lateinit var backAction: ImageView
    private lateinit var previewSurface: ZoomableImageView
    private lateinit var colorPreview: View
    private lateinit var doneAction: ImageView

    private var currentColor = 0
    private var oldChosenColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker_preview)

        if (isFinishing) {
            return
        }

        backAction = findViewById(R.id.backAction)
        previewSurface = findViewById(R.id.previewSurface)
        previewSurface.setBackgroundColor(Color.TRANSPARENT)
        previewSurface.setListener(this)
        colorPreview = findViewById(R.id.colorPreview)
        doneAction = findViewById(R.id.doneAction)

        intent?.extras?.getInt(COLOR_EXTRA)?.let {
            oldChosenColor = it
            setCurrentColor(it)
        }

        intent?.extras?.getString(BITMAP_Name_EXTRA)?.let {
            runBlocking {
                launch {
                    loadBitmapByName(this@ColorPickerPreviewActivity, it)?.let {
                        previewSurface.setImageBitmap(it)
                    }
                }
            }
        }

        doneAction.setOnClickListener {
            saveAndFinish()
        }
        backAction.setOnClickListener { onBackPressed() }
    }

    private fun saveAndFinish() {
        val data = Intent().apply {
            putExtra(COLOR_EXTRA, currentColor)
        }
        setResult(RESULT_OK, data)
        finish()
    }

    private fun setCurrentColor(color: Int) {
        currentColor = color
        val checkeredBitmap = BitmapFactory.decodeResource(resources, R.drawable.pocketpaint_checkeredbg)
        val shader = BitmapShader(checkeredBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        colorPreview.background = ColorPickerDialog.CustomColorDrawable.createDrawable(shader, color)
    }

    override fun onStart() {
        super.onStart()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }

    override fun onBackPressed() {
        if (oldChosenColor != currentColor) {
            showSaveChangesDialog()
        } else
            super.onBackPressed()
    }

    private fun showSaveChangesDialog() {
        MaterialAlertDialogBuilder(this)
                .setTitle(R.string.color_picker_save_dialog_title)
                .setMessage(R.string.color_picker_save_dialog_msg)
                .setNegativeButton(R.string.color_picker_no) { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    super.onBackPressed()
                }
                .setPositiveButton(R.string.color_picker_yes) { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    saveAndFinish()
                }
                .create()
                .show()
    }

    override fun colorChanged(color: Int) {
        setCurrentColor(color)
    }
}
