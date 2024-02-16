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
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
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

import org.catrobat.paintroid.FileIO.FileType.PNG
import org.catrobat.paintroid.FileIO.FileType.JPG
import org.catrobat.paintroid.FileIO.FileType.CATROBAT
import org.catrobat.paintroid.FileIO.FileType.ORA
import org.catrobat.paintroid.FileIO.FileType
import org.catrobat.paintroid.PaintroidApplication
import org.catrobat.paintroid.R
import java.util.Locale

import java.io.File
import java.io.FileOutputStream
import java.io.IOException







private const val STANDARD_FILE_NAME = "image"
private const val SET_NAME = "setName"
private const val PERMISSION = "permission"
private const val IS_EXPORT = "isExport"
private lateinit var gifView: View
private lateinit var delaySeekBar: SeekBar
private lateinit var delayTextView: AppCompatTextView



class SaveInformationDialog :
    MainActivityDialogFragment(),
    OnItemSelectedListener,
    OnSeekBarChangeListener {
    private lateinit var spinner: Spinner
    private lateinit var inflater: LayoutInflater
    private lateinit var specificFormatLayout: ViewGroup
    private lateinit var jpgView: View
    private lateinit var delaySeekBar: SeekBar
    private lateinit var percentage: AppCompatTextView
    private lateinit var imageName: AppCompatEditText
    private lateinit var fileName: String
    private var permission = 0
    private var isExport = false

    companion object {
        fun newInstance(
            permissionCode: Int,
            imageNumber: Int,
            isStandard: Boolean,
            isExport: Boolean
        ): SaveInformationDialog {
            if (isStandard) {
                FileIO.filename = STANDARD_FILE_NAME
                FileIO.compressFormat = Bitmap.CompressFormat.PNG
                FileIO.fileType = FileType.PNG
            }
            return SaveInformationDialog().apply {
                arguments = Bundle().apply {
                    if (FileIO.filename == STANDARD_FILE_NAME) {
                        putString(SET_NAME, FileIO.filename + imageNumber)
                    } else {
                        putString(SET_NAME, FileIO.filename)
                    }
                    putInt(PERMISSION, permissionCode)
                    putBoolean(IS_EXPORT, isExport)
                }
            }
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
                val selectedFileType = spinner.selectedItem.toString().toLowerCase(Locale.getDefault())
                if (selectedFileType == "gif") {
                    val delay = delaySeekBar.progress
                    val gifFileName = imageName.text.toString() + ".gif"
                    val gifFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + File.separator + gifFileName
                    val bitmap = PaintroidApplication.DrawingSurface.copyBitmap()
                    saveBitmapAsGif(bitmap, gifFilePath, delay)
                } else {
                    // Handle other file formats
                }
                dismiss()
            }
            .setNegativeButton(R.string.cancel_button_text) { _, _ -> dismiss() }
            .create()
    }

    private fun saveBitmapAsGif(bitmap: Bitmap, filePath: String, delay: Int) {
        val outputStream: FileOutputStream
        try {
            outputStream = FileOutputStream(filePath)

            val encoder = AnimatedGifEncoder()
            encoder.start(outputStream)
            encoder.setDelay(delay * 10)
            encoder.addFrame(bitmap)
            encoder.finish()
            outputStream.close()

        } catch (e: IOException) {
            e.printStackTrace()

        }
    }

    private fun initViews(customLayout: View) {
        initSpecificFormatLayout(customLayout)
        initGifView()
        initDelaySeekBar()
        initDelayTextView()

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

    private fun initGifView() {
        gifView = inflater.inflate(R.layout.dialog_pocketpaint_save_gif_sub_dialog, specificFormatLayout, false)
    }

    private fun initDelaySeekBar() {
        delaySeekBar = gifView.findViewById(R.id.pocketpaint_gif_delay_seekbar_save_info)
        delaySeekBar.setOnSeekBarChangeListener(this)
    }

    private fun initDelayTextView() {
        delayTextView = gifView.findViewById(R.id.pocketpaint_gif_delay_text)
        updateDelayTextView(delaySeekBar.progress)
    }

    @SuppressLint("StringFormatInvalid")
    private fun updateDelayTextView(delay: Int) {
        val delayText = getString(R.string.gif_delay_text, delay)
        delayTextView.text = delayText
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
            when (FileIO.fileType) {
                JPG -> presenter.showJpgInformationDialog()
                ORA -> presenter.showOraInformationDialog()
                CATROBAT -> presenter.showCatrobatInformationDialog()
                FileType.GIF -> presenter.showGifInformationDialog()
                else -> presenter.showPngInformationDialog()
            }
        }
    }



    private fun initSpinner(view: View) {
        spinner = view.findViewById(R.id.pocketpaint_save_dialog_spinner)
        val spinnerArray = FileType.values().map { it.value }
        val adapter = ArrayAdapter(spinner.context, android.R.layout.simple_spinner_item, spinnerArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)   //xml file
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
    }

    private fun initImageName(view: View) {
        imageName = view.findViewById(R.id.pocketpaint_image_name_save_text)
        imageName.setText(fileName)
    }

    private fun setFileDetails(
        compressFormat: Bitmap.CompressFormat,
        fileType: FileType
    ) {
        specificFormatLayout.removeAllViews()
        if (fileType == JPG) {
            specificFormatLayout.addView(jpgView)
        }
        FileIO.compressFormat = compressFormat
        FileIO.fileType = fileType
    }

    private fun setSpinnerSelection() {
        when (FileIO.fileType) {
            JPG -> spinner.setSelection(JPG.ordinal)
            ORA -> spinner.setSelection(ORA.ordinal)
            CATROBAT -> spinner.setSelection(CATROBAT.ordinal)
            FileType.GIF -> spinner.setSelection(FileType.GIF.ordinal)
            else -> spinner.setSelection(PNG.ordinal)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.getItemAtPosition(position).toString().toLowerCase(Locale.getDefault())) {
            JPG.value -> setFileDetails(Bitmap.CompressFormat.JPEG, JPG)
            PNG.value -> setFileDetails(Bitmap.CompressFormat.PNG, PNG)

            ORA.value -> setFileDetails(Bitmap.CompressFormat.PNG, ORA)
            CATROBAT.value -> setFileDetails(Bitmap.CompressFormat.PNG, CATROBAT)
            FileType.GIF.value-> setFileDetails(Bitmap.CompressFormat.PNG, FileType.GIF)
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

object FileIO {
    var filename: String = ""
    var compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
    var fileType: FileType = FileType.PNG
    var compressQuality: Int = 100
}

