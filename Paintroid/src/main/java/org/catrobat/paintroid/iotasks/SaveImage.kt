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
package org.catrobat.paintroid.iotasks

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.test.espresso.idling.CountingIdlingResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.LandingPageActivity.Companion.projectAdapter
import org.catrobat.paintroid.LandingPageActivity.Companion.projectDB
import org.catrobat.paintroid.MainActivity.Companion.projectImagePreviewUri
import org.catrobat.paintroid.MainActivity.Companion.projectName
import org.catrobat.paintroid.MainActivity.Companion.projectUri
import org.catrobat.paintroid.command.serialization.CommandSerializer
import org.catrobat.paintroid.common.PNG_IMAGE_ENDING
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.model.Project
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

class SaveImage(
    activity: SaveImageCallback,
    private val requestCode: Int,
    private val layerModel: LayerContracts.Model,
    private val commandSerializer: CommandSerializer,
    private var uri: Uri?,
    private var imagePreviewUri: Uri?,
    private val saveAsCopy: Boolean,
    private val saveProject: Boolean,
    private val context: Context,
    private val scopeIO: CoroutineScope,
    private val idlingResource: CountingIdlingResource
) {
    private val callbackRef: WeakReference<SaveImageCallback> = WeakReference(activity)

    companion object {
        private val TAG = SaveImage::class.java.simpleName
    }

    private fun getImageUri(
        callback: SaveImageCallback,
        bitmap: Bitmap?
    ): Uri? {
        val filename = FileIO.defaultFileName
        return if (uri == null) {
            val imageUri = FileIO.saveBitmapToFile(filename, bitmap, callback.contentResolver, context)
            imageUri
        } else {
            uri?.let { FileIO.saveBitmapToUri(it, bitmap, context) }
        }
    }

    private fun getImagePreviewUri(
        callback: SaveImageCallback,
        bitmap: Bitmap?
    ): Uri? {
        val filename = FileIO.defaultFileName
        val imagesDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val pathToFile = imagesDirectory + File.separator + filename + "." + PNG_IMAGE_ENDING
        val imageFile = File(pathToFile)
        return if (imagePreviewUri == null || !imageFile.exists()) {
            val imagePreviewFilename = filename.replace(".catrobat-image", ".png")
            val imageUri = FileIO.saveBitmapToFile(imagePreviewFilename, bitmap, callback.contentResolver, context)
            imageUri
        } else {
            imagePreviewUri?.let { FileIO.saveBitmapToUri(it, bitmap, context) }
        }
    }

    private fun saveOraFile(
        layers: List<LayerContracts.Layer>,
        uri: Uri,
        fileName: String,
        bitmap: Bitmap?,
        contentResolver: ContentResolver?
    ): Uri? = try {
        OpenRasterFileFormatConversion.saveOraFileToUri(
            layers,
            uri,
            fileName,
            bitmap,
            contentResolver
        )
    } catch (e: IOException) {
        Log.d(TAG, "Can't save image file ${e.message}")
        null
    }

    private fun exportOraFile(
        layers: List<LayerContracts.Layer>,
        fileName: String,
        bitmap: Bitmap?,
        contentResolver: ContentResolver?
    ): Uri? = try {
        OpenRasterFileFormatConversion.exportToOraFile(
            layers,
            fileName,
            bitmap,
            contentResolver
        )
    } catch (e: IOException) {
        Log.d(TAG, "Can't save image file ${e.message}")
        null
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    fun execute() {
        val callback = callbackRef.get()
        if (callback == null || callback.isFinishing) {
            return
        } else {
            callback.onSaveImagePreExecute(requestCode)
        }

        var currentUri: Uri? = null
        var imagePreviewPath: Uri? = null
        scopeIO.launch {
            try {
                idlingResource.increment()
                val bitmap = layerModel.getBitmapOfAllLayers()
                val filename = FileIO.defaultFileName
                if (saveProject) {
                    FileIO.fileType = FileIO.FileType.CATROBAT
                    currentUri = if (uri != null) {
                        uri?.let {
                            commandSerializer.overWriteFile(filename, it, callback.contentResolver)
                        }
                    } else {
                        commandSerializer.writeToFile(filename)
                    }
                    imagePreviewPath = getImagePreviewUri(callback, bitmap)
                    val date = Calendar.getInstance().timeInMillis
                    if(uri != null){
                        uri?.let {
                            projectDB.dao.updateProject(filename, imagePreviewPath.toString(), currentUri.toString(), date)
                            projectAdapter.updateProject(filename, imagePreviewPath.toString(), currentUri.toString(), date)
                        }
                    } else {
                        val dimensions = getImageDimensions(imagePreviewPath)
                        val size = getImageSize(imagePreviewPath)
                        val project = Project(
                            filename,
                            currentUri.toString(),
                            date,
                            date,
                            "${dimensions?.first} x ${dimensions?.second}",
                            FileIO.fileType.toString(),
                            size,
                            imagePreviewPath.toString()
                        )
                        projectDB.dao.insertProject(project)
                        projectAdapter.insertProject(project)
                        projectName = filename
                        projectUri = currentUri.toString()
                        projectImagePreviewUri = imagePreviewPath.toString()
                    }
                } else {
                    currentUri = if (FileIO.fileType == FileIO.FileType.ORA) {
                        val layers = layerModel.layers
                        if (uri != null && filename.endsWith(FileIO.FileType.ORA.toExtension())) {
                            uri?.let {
                                saveOraFile(layers, it, filename, bitmap, callback.contentResolver)
                            }
                        } else {
                            val imageUri =
                                exportOraFile(layers, filename, bitmap, callback.contentResolver)
                            imageUri
                        }
                    } else if (FileIO.fileType == FileIO.FileType.CATROBAT) {
                        if (uri != null) {
                            uri?.let {
                                commandSerializer.overWriteFile(
                                    filename,
                                    it,
                                    callback.contentResolver
                                )
                            }
                        } else {
                            commandSerializer.writeToFile(filename)
                        }
                    } else {
                        getImageUri(callback, bitmap)
                    }
                    idlingResource.decrement()
                }
            } catch (e: Exception) {
                idlingResource.decrement()
                when (e) {
                    is IOException -> Log.d(TAG, "Can't save image file", e)
                    is NullPointerException -> Log.e(TAG, "Can't load image file", e)
                }
            }

            withContext(Dispatchers.Main) {
                if (!callback.isFinishing) {
                    callback.onSaveImagePostExecute(requestCode, currentUri, saveAsCopy)
                }
            }
        }
    }

    private fun getImageDimensions(uri: Uri?): Pair<Int, Int>? {
        val inputStream = uri?.let { context.contentResolver.openInputStream(it) }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()

        if (options.outWidth != -1 && options.outHeight != -1) {
            return Pair(options.outWidth, options.outHeight)
        }

        return null
    }

    private fun getImageSize(uri: Uri?): Double {
        var size = 0.0
        try {
            val inputStream = uri?.let { context.contentResolver.openInputStream(it) }
            val bytes = inputStream?.available()?.toLong() ?: 0
            size = bytes.toDouble() / (1024 * 1024)
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return size
    }

    interface SaveImageCallback {
        val contentResolver: ContentResolver
        val isFinishing: Boolean
        fun onSaveImagePreExecute(requestCode: Int)
        fun onSaveImagePostExecute(requestCode: Int, uri: Uri?, saveAsCopy: Boolean)
    }
}
