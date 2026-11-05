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
package org.catrobat.paintroid.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.FileIO.FileType
import org.catrobat.paintroid.FileIO.FileType.CATROBAT
import org.catrobat.paintroid.FileIO.FileType.JPG
import org.catrobat.paintroid.FileIO.FileType.ORA
import org.catrobat.paintroid.FileIO.FileType.PNG
import org.catrobat.paintroid.R
import org.catrobat.paintroid.common.REQUEST_CODE_CREATE_DOCUMENT
import java.util.Locale

private const val ARG_IMAGE_NUMBER = "ARG_IMAGE_NUMBER"
private const val ARG_IS_CATROID = "ARG_IS_CATROID"

class SaveInformationDialog : MainActivityDialogFragment(),
    AdapterView.OnItemSelectedListener,
    SeekBar.OnSeekBarChangeListener {

    private lateinit var inflater: LayoutInflater
    private lateinit var spinner: Spinner
    private lateinit var specificFormatLayout: ViewGroup
    private lateinit var jpgView: View
    private lateinit var percentage: AppCompatTextView
    private lateinit var imageName: AppCompatEditText

    private var imageNumber: Int = 0
    private var isCatroid: Boolean = false

    companion object {
        fun newInstance(imageNumber: Int, isCatroid: Boolean): SaveInformationDialog =
            SaveInformationDialog().apply {
                arguments = bundleOf(
                    ARG_IMAGE_NUMBER to imageNumber,
                    ARG_IS_CATROID to isCatroid
                )
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageNumber = it.getInt(ARG_IMAGE_NUMBER)
            isCatroid = it.getBoolean(ARG_IS_CATROID)
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
                val baseName = imageName.text.toString().ifEmpty { "image$imageNumber" }
                val ext = when (FileIO.fileType) {
                    PNG -> ".png"
                    JPG -> ".jpg"
                    ORA -> ".ora"
                    CATROBAT -> ".catrobat"
                }
                val mime = when (FileIO.fileType) {
                    PNG -> "image/png"
                    JPG -> "image/jpeg"
                    ORA -> "application/x-openraster"
                    CATROBAT -> "application/octet-stream"
                }

                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = mime
                    putExtra(Intent.EXTRA_TITLE, baseName + ext)
                }
                presenter.startCreateDocument(intent, REQUEST_CODE_CREATE_DOCUMENT)
                dismiss()
            }
            .setNegativeButton(R.string.cancel_button_text) { _, _ ->
                dismiss()
            }
            .create()
    }

    private fun initViews(view: View) {
        specificFormatLayout = view.findViewById(R.id.pocketpaint_save_format_specific_options)
        jpgView = inflater.inflate(
            R.layout.dialog_pocketpaint_save_jpg_sub_dialog,
            specificFormatLayout,
            false
        )
        percentage = jpgView.findViewById(R.id.pocketpaint_percentage_save_info)
        val percentageText = context?.getString(R.string.compress_quality_percentage, FileIO.compressQuality)
        percentage.text = percentageText
        jpgView.findViewById<SeekBar>(R.id.pocketpaint_jpg_seekbar_save_info).apply {
            progress = FileIO.compressQuality
            setOnSeekBarChangeListener(this@SaveInformationDialog)
        }
        spinner = view.findViewById(R.id.pocketpaint_save_dialog_spinner)
        ArrayAdapter(
            spinner.context,
            android.R.layout.simple_spinner_item,
            FileType.values().map { it.value }
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = it
        }
        spinner.onItemSelectedListener = this
        view.findViewById<AppCompatImageButton>(R.id.pocketpaint_btn_save_info)
            .setOnClickListener {
                when (FileIO.fileType) {
                    JPG -> presenter.showJpgInformationDialog()
                    ORA -> presenter.showOraInformationDialog()
                    CATROBAT -> presenter.showCatrobatInformationDialog()
                    else -> presenter.showPngInformationDialog()
                }
            }
        imageName = view.findViewById(R.id.pocketpaint_image_name_save_text)
        val defaultName = FileIO.filename.takeIf { it.isNotEmpty() } ?: "image$imageNumber"
        imageName.setText(defaultName)
    }

    private fun setSpinnerSelection() {
        spinner.setSelection(FileIO.fileType.ordinal)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        when (parent?.getItemAtPosition(pos).toString().lowercase(Locale.getDefault())) {
            JPG.value -> setFileDetails(Bitmap.CompressFormat.JPEG, JPG)
            PNG.value -> setFileDetails(Bitmap.CompressFormat.PNG, PNG)
            ORA.value -> setFileDetails(Bitmap.CompressFormat.PNG, ORA)
            CATROBAT.value -> setFileDetails(Bitmap.CompressFormat.PNG, CATROBAT)
        }
    }
    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

    private fun setFileDetails(format: Bitmap.CompressFormat, type: FileType) {
        specificFormatLayout.removeAllViews()
        if (type == JPG) {
            specificFormatLayout.addView(jpgView)
        }
        FileIO.compressFormat = format
        FileIO.fileType = type
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        percentage.text = seekBar.context.getString(R.string.compress_quality_percentage, progress)
        FileIO.compressQuality = progress
    }
    override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
    override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
}
