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
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.command.serialization.CommandSerializationUtilities
import org.catrobat.paintroid.tools.Workspace
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.Locale

class LoadImage(
    callback: LoadImageCallback,
    private val requestCode: Int,
    private val uri: Uri?,
    context: Context,
    private val scaleImage: Boolean,
    private val workspace: Workspace,
    private val scopeIO: CoroutineScope
) {
    private val callbackRef: WeakReference<LoadImageCallback> = WeakReference(callback)
    private val context: WeakReference<Context> = WeakReference(context)

    private fun getMimeType(uri: Uri, resolver: ContentResolver): String? =
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            resolver.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(fileExtension.toLowerCase(Locale.US))
        }

    private fun getBitmapReturnValue(
        uri: Uri,
        resolver: ContentResolver
    ): BitmapReturnValue {
        val mimeType: String? = getMimeType(uri, resolver)
        return if (mimeType == "application/zip" || mimeType == "application/octet-stream") {
            try {
                BitmapReturnValue(workspace.getCommandSerializationHelper().readFromFile(uri))
            } catch (e: CommandSerializationUtilities.NotCatrobatImageException) {
                Log.e(TAG, "Image might be an ora file instead")
                OpenRasterFileFormatConversion.importOraFile(
                    resolver,
                    uri,
                    context.get()
                )
            }
        } else {
            if (scaleImage) {
                FileIO.getScaledBitmapFromUri(resolver, uri, context.get())
            } else {
                FileIO.getBitmapReturnValueFromUri(resolver, uri, context.get())
            }
        }
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    fun execute() {
        val callback = callbackRef.get()
        if (callback == null || callback.isFinishing) {
            return
        }
        callback.onLoadImagePreExecute(requestCode)

        var returnValue: BitmapReturnValue? = null
        scopeIO.launch {
            if (uri == null) {
                Log.e(TAG, "Can't load image file, uri is null")
            } else {
                try {
                    val resolver = callback.contentResolver
                    FileIO.filename = "image"
                    returnValue = getBitmapReturnValue(uri, resolver)
                } catch (e: IOException) {
                    Log.e(TAG, "Can't load image file", e)
                } catch (e: NullPointerException) {
                    Log.e(TAG, "Can't load image file", e)
                }
            }

            withContext(Dispatchers.Main) {
                if (!callback.isFinishing) {
                    callback.onLoadImagePostExecute(requestCode, uri, returnValue)
                }
            }
        }
    }

    interface LoadImageCallback {
        fun onLoadImagePostExecute(requestCode: Int, uri: Uri?, result: BitmapReturnValue?)
        fun onLoadImagePreExecute(requestCode: Int)
        val contentResolver: ContentResolver
        val isFinishing: Boolean
    }

    companion object {
        private val TAG = LoadImage::class.java.simpleName
    }
}
