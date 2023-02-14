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
package org.catrobat.paintroid.test.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ScreenshotOnFailRule : TestWatcher() {
    private val uiAutomation =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            InstrumentationRegistry.getInstrumentation().uiAutomation
        } else {
            null
        }

    override fun failed(e: Throwable, description: Description) {
        Log.i(LOG_TAG, "taking screenshot of failed test " + description.methodName)
        if (uiAutomation == null) {
            Log.i(LOG_TAG, "api level doesn't support screenshots.")
            return
        }
        val screenshot = uiAutomation.takeScreenshot()
        if (screenshot == null) {
            Log.e(LOG_TAG, "failed to take screenshot")
            return
        }
        if (!isExternalStorageMounted) {
            Log.e(LOG_TAG, "storage device is not mounted")
            return
        }
        if (!hasWritePermission(ApplicationProvider.getApplicationContext())) {
            Log.e(LOG_TAG, "need to have write permissions")
            return
        }
        val path = File(
            ApplicationProvider.getApplicationContext<Context>().getExternalFilesDir(null),
            "/screenshots/"
        )
        if (!path.exists() && !path.mkdirs()) {
            Log.e(LOG_TAG, "failed to create screenshot path")
            return
        }
        val filename = description.className + "-" + description.methodName + ".png"
        saveScreenshot(screenshot, File(path, filename))
    }

    private val isExternalStorageMounted: Boolean
        get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    private fun hasWritePermission(activityContext: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            activityContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun saveScreenshot(screenshot: Bitmap, path: File) {
        var fileStream: BufferedOutputStream? = null
        try {
            fileStream = BufferedOutputStream(FileOutputStream(path))
            screenshot.compress(Bitmap.CompressFormat.PNG, 90, fileStream)
            fileStream.flush()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "failed to save screen shot to file", e)
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close()
                } catch (ioe: IOException) {
                    Log.e(LOG_TAG, ioe.message!!)
                }
            }
            screenshot.recycle()
        }
    }

    companion object {
        private val LOG_TAG = ScreenshotOnFailRule::class.java.simpleName
    }
}
