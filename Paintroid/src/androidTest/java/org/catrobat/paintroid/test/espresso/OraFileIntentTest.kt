/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.FileIO.getBitmapFromUri
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.tools.ToolType
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException
import java.util.Objects

@RunWith(AndroidJUnit4::class)
class OraFileIntentTest {
    @Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    private var resolver: ContentResolver? = null
    @Before
    fun setUp() {
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.BRUSH)
        deletionFileList = ArrayList()
        resolver = launchActivityRule.activity.contentResolver
    }

    @After
    fun tearDown() {
        for (file in deletionFileList) {
            if (file.exists()) { Assert.assertTrue(file.delete()) }
        }
    }

    @Test
    fun testCheckIntentForOraFile() {
        val intent = Intent()
        val receivedUri = createTestImageFile()
        var receivedBitmap: Bitmap? = null
        try {
            receivedBitmap =
                resolver?.let { getBitmapFromUri(it, receivedUri, launchActivityRule.activity.baseContext) }
        } catch (e: Exception) {
            Log.e("Can't Read", "Can't get Bitmap from File")
        }
        Objects.requireNonNull(receivedBitmap)
        intent.action = Intent.ACTION_EDIT
        intent.data = receivedUri
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, receivedUri)
        launchActivityRule.launchActivity(intent)
        val mainActivityIntent = launchActivityRule.activity.intent
        val intentAction = intent.action
        val intentType = intent.type
        val intentBundle = intent.extras
        Objects.requireNonNull(intentBundle)
        val intentUri = intentBundle!![Intent.EXTRA_STREAM] as Uri?
        val mainActivityIntentAction = mainActivityIntent.action
        val mainActivityIntentType = mainActivityIntent.type
        val mainActivityIntentBundle = mainActivityIntent.extras
        Objects.requireNonNull(mainActivityIntentBundle)
        val mainActivityIntentUri = mainActivityIntentBundle!![Intent.EXTRA_STREAM] as Uri?
        var mainActivityIntentBitmap: Bitmap? = null
        Objects.requireNonNull(mainActivityIntentUri)
        try {
            mainActivityIntentBitmap = mainActivityIntentUri?.let {
                resolver?.let { it1 -> getBitmapFromUri(it1, it, launchActivityRule.activity.baseContext) }
            }
        } catch (e: Exception) {
            Log.e("Can't read", "Can't get Bitmap From File")
        }
        Objects.requireNonNull(mainActivityIntentBitmap)
        Assert.assertEquals(intentAction, mainActivityIntentAction)
        Assert.assertEquals(intentType, mainActivityIntentType)
        Assert.assertEquals(intentUri, mainActivityIntentUri)
        Assert.assertEquals(receivedBitmap?.width?.toLong(), mainActivityIntentBitmap?.width?.toLong())
        Assert.assertEquals(receivedBitmap?.height?.toLong(), mainActivityIntentBitmap?.height?.toLong())
    }

    private fun createTestImageFile(): Uri {
        val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "testfile.ora")
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        val imageUri =
            resolver!!.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        try {
            val fos = Objects.requireNonNull(imageUri)?.let { resolver?.openOutputStream(it) }
            Assert.assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos))
            assert(fos != null)
            fos?.close()
        } catch (e: IOException) {
            throw AssertionError("Picture file could not be created.", e)
        }
        val imageFile = File(imageUri!!.path, "testfile.ora")
        deletionFileList.add(imageFile)
        return imageUri
    }

    companion object {
        private lateinit var deletionFileList: ArrayList<File>
    }
}
