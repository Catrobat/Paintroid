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
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.Locale

class LoadImage(
    callback: LoadImageCallback,
    private val requestCode: Int,
    private val uri: Uri?,
    context: Context,
    private val scaleImage: Boolean,
    private val scopeIO: CoroutineScope
) {
    private val callbackRef: WeakReference<LoadImageCallback> = WeakReference(callback)
    private val context: WeakReference<Context> = WeakReference(context)

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
                returnValue = try {
                    val resolver = callback.contentResolver
                    FileIO.filename = "image"
                    val mimeType: String? = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                        resolver.getType(uri)
                    } else {
                        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                        MimeTypeMap.getSingleton()
                            .getMimeTypeFromExtension(fileExtension.toLowerCase(Locale.US))
                    }
                    val value: BitmapReturnValue =
                        if (mimeType == "application/zip" || mimeType == "application/octet-stream") {
                            OpenRasterFileFormatConversion.importOraFile(
                                resolver,
                                uri,
                                context.get()
                            )
                        } else {
                            if (scaleImage) {
                                FileIO.getScaledBitmapFromUri(resolver, uri, context.get())
                            } else {
                                FileIO.getBitmapReturnValueFromUri(resolver, uri, context.get())
                            }
                        }
                    value
                } catch (e: IOException) {
                    Log.e(TAG, "Can't load image file", e)
                    null
                } catch (e: NullPointerException) {
                    Log.e(TAG, "Can't load image file", e)
                    null
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
