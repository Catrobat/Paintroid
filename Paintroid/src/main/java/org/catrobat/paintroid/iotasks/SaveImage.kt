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
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.common.Constants
import org.catrobat.paintroid.tools.Workspace
import java.io.IOException
import java.lang.ref.WeakReference

class SaveImage(
    activity: SaveImageCallback,
    private val requestCode: Int,
    private val workspace: Workspace,
    private var uri: Uri?,
    private val saveAsCopy: Boolean,
    private val scopeIO: CoroutineScope
) {
    private val callbackRef: WeakReference<SaveImageCallback> = WeakReference(activity)

    companion object {
        private val TAG = SaveImage::class.java.simpleName
    }

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
                val fileName = FileIO.getDefaultFileName()
                val fileExistsValue = FileIO.checkIfDifferentFile(fileName)
                currentUri = if (FileIO.isCatrobatImage) {
                    val bitmapList = workspace.bitmapLisOfAllLayers
                    if (uri != null && fileExistsValue == Constants.IS_ORA) {
                        setUriToFormatUri(fileExistsValue)
                        OpenRasterFileFormatConversion.saveOraFileToUri(
                            bitmapList,
                            uri,
                            fileName,
                            bitmap,
                            callback.contentResolver
                        )
                    } else {
                        val imageUri = OpenRasterFileFormatConversion.exportToOraFile(
                            bitmapList,
                            fileName,
                            bitmap,
                            callback.contentResolver
                        )
                        FileIO.currentFileNameOra = fileName
                        FileIO.uriFileOra = imageUri
                        imageUri
                    }
                } else {
                    if (uri != null && (FileIO.catroidFlag || fileExistsValue != Constants.IS_NO_FILE)) {
                        FileIO.saveBitmapToUri(uri, callback.contentResolver, bitmap)
                    } else {
                        val imageUri =
                            FileIO.saveBitmapToFile(fileName, bitmap, callback.contentResolver)
                        if (FileIO.ending == ".png") {
                            FileIO.currentFileNamePng = fileName
                            FileIO.uriFilePng = imageUri
                        } else {
                            FileIO.currentFileNameJpg = fileName
                            FileIO.uriFileJpg = imageUri
                        }
                        imageUri
                    }
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
        if (formatCode == Constants.IS_JPG) {
            if (FileIO.uriFileJpg != null) {
                uri = FileIO.uriFileJpg
            }
        } else if (formatCode == Constants.IS_PNG) {
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
