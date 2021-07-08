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

import android.app.Activity
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.paintroid.FileIO
import java.io.File
import java.lang.NullPointerException
import java.lang.ref.WeakReference

class CreateFile(
    callback: CreateFileCallback,
    private val requestCode: Int,
    private val filename: String?,
    private val scopeIO: CoroutineScope
) {
    private val callbackRef: WeakReference<CreateFileCallback> = WeakReference(callback)

    fun execute() {
        val callback = callbackRef.get()
        var file: File? = null
        scopeIO.launch {
            try {
                file = FileIO.createNewEmptyPictureFile(filename, callback?.fileActivity)
            } catch (e: NullPointerException) {
                Log.e(TAG, "Can't create file", e)
            }
            withContext(Dispatchers.Main) {
                if (callback != null && !callback.isFinishing) {
                    callback.onCreateFilePostExecute(requestCode, file)
                }
            }
        }
    }

    interface CreateFileCallback {
        val fileActivity: Activity?
        val isFinishing: Boolean
        fun onCreateFilePostExecute(requestCode: Int, file: File?)
    }

    companion object {
        private val TAG = CreateFile::class.java.simpleName
    }
}
