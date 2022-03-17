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
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.common.CATROBAT_IMAGE_ENDING
import org.catrobat.paintroid.common.IS_JPG
import org.catrobat.paintroid.common.IS_ORA
import org.catrobat.paintroid.common.IS_PNG
import org.catrobat.paintroid.tools.Workspace
import java.io.IOException
import java.lang.ref.WeakReference

class SaveImage(
    activity: SaveImageCallback,
    private val requestCode: Int,
    private val workspace: Workspace,
    private var uri: Uri?,
    private val saveAsCopy: Boolean,
    private val context: Context,
    private val scopeIO: CoroutineScope
) {
    private val callbackRef: WeakReference<SaveImageCallback> = WeakReference(activity)

    companion object {
        private val TAG = SaveImage::class.java.simpleName
    }

    private fun getImageUri(
        callback: SaveImageCallback,
        bitmap: Bitmap?
    ): Uri? {
        val fileName = FileIO.defaultFileName
        val fileExistsValue = FileIO.checkIfDifferentFile(fileName)
        return if (uri == null) {
            val imageUri =
                FileIO.saveBitmapToFile(fileName, bitmap, callback.contentResolver, context)
            if (FileIO.ending == ".png") {
                FileIO.currentFileNamePng = fileName
                FileIO.uriFilePng = imageUri
            } else {
                FileIO.currentFileNameJpg = fileName
                FileIO.uriFileJpg = imageUri
            }
            imageUri
        } else {
            if (!FileIO.catroidFlag) {
                setUriToFormatUri(fileExistsValue)
            }
            uri?.let { FileIO.saveBitmapToUri(it, bitmap, context) }
        }
    }

    private fun saveOraFile(
        bitmapList: List<Bitmap?>,
        uri: Uri,
        fileName: String,
        bitmap: Bitmap?,
        contentResolver: ContentResolver?
    ): Uri? = try {
        OpenRasterFileFormatConversion.saveOraFileToUri(
            bitmapList,
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
        bitmapList: List<Bitmap?>,
        fileName: String,
        bitmap: Bitmap?,
        contentResolver: ContentResolver?
    ): Uri? = try {
        OpenRasterFileFormatConversion.exportToOraFile(
            bitmapList,
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
        scopeIO.launch {
            try {
                val bitmap = workspace.bitmapOfAllLayers
                val fileName = FileIO.defaultFileName
                val fileExistsValue = FileIO.checkIfDifferentFile(fileName)
                currentUri = if (FileIO.isCatrobatImage) {
                    val bitmapList = workspace.bitmapLisOfAllLayers
                    if (uri != null && fileExistsValue == IS_ORA) {
                        setUriToFormatUri(fileExistsValue)
                        uri?.let {
                            saveOraFile(bitmapList, it, fileName, bitmap, callback.contentResolver)
                        }
                    } else {
                        val imageUri =
                            exportOraFile(bitmapList, fileName, bitmap, callback.contentResolver)
                        FileIO.currentFileNameOra = fileName
                        FileIO.uriFileOra = imageUri
                        imageUri
                    }
                } else if (FileIO.ending == ".$CATROBAT_IMAGE_ENDING") {
                    workspace.getCommandSerializationHelper().writeToFile(fileName)
                } else {
                    getImageUri(callback, bitmap)
                }
            } catch (e: Exception) {
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

    private fun setUriToFormatUri(formatCode: Int) {
        if (formatCode == IS_JPG) {
            if (FileIO.uriFileJpg != null) {
                uri = FileIO.uriFileJpg
            }
        } else if (formatCode == IS_PNG) {
            if (FileIO.uriFilePng != null) {
                uri = FileIO.uriFilePng
            }
        } else {
            if (FileIO.uriFileOra != null) {
                uri = FileIO.uriFileOra
            }
        }
    }

    interface SaveImageCallback {
        val contentResolver: ContentResolver
        val isFinishing: Boolean
        fun onSaveImagePreExecute(requestCode: Int)
        fun onSaveImagePostExecute(requestCode: Int, uri: Uri?, saveAsCopy: Boolean)
    }
}
