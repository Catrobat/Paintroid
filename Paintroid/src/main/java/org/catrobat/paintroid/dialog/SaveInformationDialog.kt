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
package org.catrobat.paintroid.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.R
import org.catrobat.paintroid.common.Constants.IS_NO_FILE
import org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_COPY

private const val STANDARD_FILE_NAME = "image"
private const val STANDARD_FILE_ENDING = ".png"
private const val SET_NAME = "setName"
private const val PERMISSION = "permission"
private const val IS_EXPORT = "isExport"

class SaveInformationDialog :
    MainActivityDialogFragment(),
    OnItemSelectedListener,
    OnSeekBarChangeListener {
    private lateinit var spinner: Spinner
    private lateinit var inflater: LayoutInflater
    private lateinit var specificFormatLayout: ViewGroup
    private lateinit var jpgView: View
    private lateinit var percentage: AppCompatTextView
    private lateinit var imageName: AppCompatEditText
    private lateinit var fileName: String
    private var permission = 0
    private var isExport = false

    companion object {
        @JvmStatic
        fun newInstance(
            permissionCode: Int,
            imageNumber: Int,
            isStandard: Boolean,
            isExport: Boolean
        ): SaveInformationDialog {
            if (isStandard) {
                FileIO.isCatrobatImage = false
                FileIO.filename = STANDARD_FILE_NAME
                FileIO.compressFormat = Bitmap.CompressFormat.PNG
                FileIO.ending = STANDARD_FILE_ENDING
            }
            val dialog = SaveInformationDialog()
            val bundle = Bundle().apply {
                if (FileIO.filename == STANDARD_FILE_NAME) {
                    putString(SET_NAME, FileIO.filename + imageNumber)
                } else {
                    putString(SET_NAME, FileIO.filename)
                }
                putInt(PERMISSION, permissionCode)
                putBoolean(IS_EXPORT, isExport)
            }
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = requireArguments()
        arguments.apply {
            permission = getInt(PERMISSION)
            fileName = getString(SET_NAME).toString()
            isExport = getBoolean(IS_EXPORT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setSpinnerSelection()
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        inflater = requireActivity().layoutInflater
        val customLayout = inflater.inflate(R.layout.dialog_pocketpaint_save, null)
        onViewCreated(customLayout, savedInstanceState)
        return AlertDialog.Builder(requireContext(), R.style.PocketPaintAlertDialog)
            .setTitle(R.string.dialog_save_image_title)
            .setView(customLayout)
            .setPositiveButton(R.string.save_button_text) { _, _ ->
                FileIO.filename = imageName.text.toString()
                if (permission != PERMISSION_EXTERNAL_STORAGE_SAVE_COPY && FileIO.checkIfDifferentFile(
                        FileIO.getDefaultFileName()
                    ) != IS_NO_FILE
                ) {
                    presenter.showOverwriteDialog(permission, isExport)
                } else {
                    presenter.switchBetweenVersions(permission, isExport)
                }
                dismiss()
            }
            .setNegativeButton(R.string.cancel_button_text) { _, _ -> dismiss() }
            .create()
    }

    private fun initViews(customLayout: View) {
        initSpecificFormatLayout(customLayout)
        initJpgView()
        initSeekBar()
        initPercentage()
        initSpinner(customLayout)
        initInfoButton(customLayout)
        initImageName(customLayout)
    }

    private fun initSpecificFormatLayout(view: View) {
        specificFormatLayout = view.findViewById(R.id.pocketpaint_save_format_specific_options)
    }

    private fun initJpgView() {
        jpgView = inflater.inflate(
            R.layout.dialog_pocketpaint_save_jpg_sub_dialog,
            specificFormatLayout,
            false
        )
    }

    private fun initSeekBar() {
        val seekBar: SeekBar = jpgView.findViewById(R.id.pocketpaint_jpg_seekbar_save_info)
        seekBar.progress = FileIO.compressQuality
        seekBar.setOnSeekBarChangeListener(this)
    }

    private fun initPercentage() {
        percentage = jpgView.findViewById(R.id.pocketpaint_percentage_save_info)
        val percentageString = FileIO.compressQuality.toString().plus('%')
        percentage.text = percentageString
    }

    private fun initInfoButton(view: View) {
        val infoButton: AppCompatImageButton = view.findViewById(R.id.pocketpaint_btn_save_info)
        infoButton.setOnClickListener {
            when {
                FileIO.isCatrobatImage -> presenter.showOraInformationDialog()
                FileIO.compressFormat == Bitmap.CompressFormat.JPEG -> presenter.showJpgInformationDialog()
                else -> presenter.showPngInformationDialog()
            }
        }
    }

    private fun initSpinner(view: View) {
        spinner = view.findViewById(R.id.pocketpaint_save_dialog_spinner)
        val spinnerArray = arrayListOf("png", "jpg", "ora")
        val adapter =
            ArrayAdapter(spinner.context, android.R.layout.simple_spinner_item, spinnerArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
    }

    private fun initImageName(view: View) {
        imageName = view.findViewById(R.id.pocketpaint_image_name_save_text)
        imageName.setText(fileName)
    }

    private fun setFileDetails(
        compressFormat: Bitmap.CompressFormat,
        isCatrobatImage: Boolean,
        ending: String,
        isJpg: Boolean = false
    ) {
        specificFormatLayout.removeAllViews()
        if (isJpg) {
            specificFormatLayout.addView(jpgView)
        }
        FileIO.compressFormat = compressFormat
        FileIO.isCatrobatImage = isCatrobatImage
        FileIO.ending = ending
    }

    private fun setSpinnerSelection() {
        when {
            FileIO.isCatrobatImage -> spinner.setSelection(2)
            FileIO.compressFormat == Bitmap.CompressFormat.PNG -> spinner.setSelection(0)
            else -> spinner.setSelection(1)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        when (parent?.getItemAtPosition(position).toString()) {
            "jpg" -> setFileDetails(Bitmap.CompressFormat.JPEG, false, ".jpg", true)
            "png" -> setFileDetails(Bitmap.CompressFormat.PNG, false, ".png")
            "ora" -> setFileDetails(Bitmap.CompressFormat.PNG, true, ".ora")
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        percentage.text = progress.toString().plus('%')
        FileIO.compressQuality = progress
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
    override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
}
