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
package org.catrobat.paintroid.iotasks

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.common.Constants
import org.catrobat.paintroid.tools.Workspace
import java.io.IOException
import java.lang.ref.WeakReference

@Suppress("ForbiddenVoid")
class SaveImageAsync(
    activity: SaveImageCallback,
    private val requestCode: Int,
    workspace: Workspace,
    uri: Uri?,
    saveAsCopy: Boolean
) : AsyncTask<Void?, Void?, Uri?>() {
    private val callbackRef: WeakReference<SaveImageCallback> = WeakReference(activity)
    private var uri: Uri?
    private val saveAsCopy: Boolean
    private val workspace: Workspace
    override fun onPreExecute() {
        val callback = callbackRef.get()
        if (callback == null || callback.isFinishing) {
            cancel(false)
        } else {
            callback.onSaveImagePreExecute(requestCode)
        }
    }

    private fun getImageUri(
        callback: SaveImageCallback,
        bitmap: Bitmap?
    ): Uri? {
        val fileName = FileIO.getDefaultFileName()
        val fileExistsValue = FileIO.checkIfDifferentFile(fileName)
        return if (uri == null) {
            val imageUri = FileIO.saveBitmapToFile(fileName, bitmap, callback.contentResolver)
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
            FileIO.saveBitmapToUri(uri, callback.contentResolver, bitmap)
        }
    }

    private fun saveOraFile(
        bitmapList: List<Bitmap?>,
        uri: Uri,
        fileName: String,
        bitmap: Bitmap?,
        contentResolver: ContentResolver?
    ): Uri? {
        return try {
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
    }

    private fun exportOraFile(
        bitmapList: List<Bitmap?>,
        fileName: String,
        bitmap: Bitmap?,
        contentResolver: ContentResolver?
    ): Uri? {
        return try {
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
    }

    @Suppress("Detekt.TooGenericExceptionCaught")
    override fun doInBackground(vararg params: Void?): Uri? {
        val callback = callbackRef.get()
        if (callback == null || callback.isFinishing) {
            return null
        }
        try {
            val bitmap = workspace.bitmapOfAllLayers
            val fileName = FileIO.getDefaultFileName()
            val fileExistsValue = FileIO.checkIfDifferentFile(fileName)
            return if (FileIO.isCatrobatImage) {
                val bitmapList = workspace.bitmapLisOfAllLayers
                return if (uri != null && fileExistsValue == Constants.IS_ORA) {
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
            } else {
                getImageUri(callback, bitmap)
            }
        } catch (e: IOException) {
            Log.d(TAG, "Can't save image file", e)
        } catch (e: NullPointerException) {
            Log.e(TAG, "Can't load image file", e)
        }
        return null
    }

    private fun setUriToFormatUri(formatcode: Int) {
        if (formatcode == Constants.IS_JPG) {
            if (FileIO.uriFileJpg != null) {
                uri = FileIO.uriFileJpg
            }
        } else if (formatcode == Constants.IS_PNG) {
            if (FileIO.uriFilePng != null) {
                uri = FileIO.uriFilePng
            }
        } else {
            if (FileIO.uriFileOra != null) {
                uri = FileIO.uriFileOra
            }
        }
    }

    override fun onPostExecute(uri: Uri?) {
        val callback = callbackRef.get()
        if (callback != null && !callback.isFinishing) {
            callback.onSaveImagePostExecute(requestCode, uri, saveAsCopy)
        }
    }

    interface SaveImageCallback {
        fun onSaveImagePreExecute(requestCode: Int)
        fun onSaveImagePostExecute(requestCode: Int, uri: Uri?, saveAsCopy: Boolean)
        val contentResolver: ContentResolver?
        val isFinishing: Boolean
    }

    companion object {
        private val TAG = SaveImageAsync::class.java.simpleName
    }

    init {
        this.uri = uri
        this.saveAsCopy = saveAsCopy
        this.workspace = workspace
    }
}
