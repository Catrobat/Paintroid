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
import androidx.test.espresso.idling.CountingIdlingResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.paintroid.FileIO
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
                idlingResource.increment()
                val bitmap = workspace.bitmapOfAllLayers
                val filename = FileIO.defaultFileName
                currentUri = if (FileIO.fileType == FileIO.FileType.ORA) {
                    val bitmapList = workspace.bitmapLisOfAllLayers
                    if (uri != null && filename.endsWith(FileIO.FileType.ORA.toExtension())) {
                        uri?.let {
                            saveOraFile(bitmapList, it, filename, bitmap, callback.contentResolver)
                        }
                    } else {
                        val imageUri = exportOraFile(bitmapList, filename, bitmap, callback.contentResolver)
                        imageUri
                    }
                } else if (FileIO.fileType == FileIO.FileType.CATROBAT) {
                    if (uri != null) {
                        uri?.let {
                            workspace.getCommandSerializationHelper().overWriteFile(filename, it, callback.contentResolver)
                        }
                    } else {
                        workspace.getCommandSerializationHelper().writeToFile(filename)
                    }
                } else {
                    getImageUri(callback, bitmap)
                }
                idlingResource.decrement()
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

    interface SaveImageCallback {
        val contentResolver: ContentResolver
        val isFinishing: Boolean
        fun onSaveImagePreExecute(requestCode: Int)
        fun onSaveImagePostExecute(requestCode: Int, uri: Uri?, saveAsCopy: Boolean)
    }
}
