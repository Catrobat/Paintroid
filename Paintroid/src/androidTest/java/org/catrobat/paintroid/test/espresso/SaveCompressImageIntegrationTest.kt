/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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

@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.FileIO.decodeBitmapFromUri
import org.catrobat.paintroid.FileIO.getBitmapFromFile
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Objects
import java.util.Random
import java.util.UUID

class SaveCompressImageIntegrationTest {
    @get:Rule
    var activityTestRule: ActivityTestRule<MainActivity> = IntentsTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var testImageFile: File? = null
    private var activity: MainActivity? = null

    @Before
    fun setUp() {
        try {
            activity = activityTestRule.activity
            testImageFile = File.createTempFile("PocketPaintTest", ".jpg")
            deletionFileList = ArrayList()
            deletionFileList?.add(testImageFile)
            val bitmap = createTestBitmap()
            val outputStream: OutputStream = FileOutputStream(testImageFile)

            Assert.assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream))
            outputStream.close()
        } catch (e: IOException) {
            throw AssertionError("Could not create temp file", e)
        }
        val intent = Intent()
        intent.data = Uri.fromFile(testImageFile)
        val resultOK = ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultOK)
    }

    @After
    fun tearDown() {
        for (file in deletionFileList!!) {
            if (file != null && file.exists()) {
                Assert.assertTrue(file.delete())
            }
        }
    }

    @Test
    @Throws(IOException::class)
    fun testSaveImage() {
        val testName = UUID.randomUUID().toString()
        onTopBarView().performOpenMoreOptions()
        onView(ViewMatchers.withText(R.string.menu_load_image)).perform(ViewActions.click())
        onTopBarView().performOpenMoreOptions()
        onView(ViewMatchers.withText(R.string.menu_save_image)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.pocketpaint_image_name_save_text))
            .perform(ViewActions.replaceText(testName))
        onView(ViewMatchers.withId(R.id.pocketpaint_save_dialog_spinner))
            .perform(ViewActions.click())
        Espresso.onData(
            AllOf.allOf(
                Matchers.`is`(Matchers.instanceOf<Any>(String::class.java)),
                Matchers.`is`<String>("jpg")
            )
        ).inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.save_button_text)).perform(ViewActions.click())
        onView(ViewMatchers.isRoot()).perform(UiInteractions.waitFor(100))

        val options = BitmapFactory.Options()
        options.inMutable = true
        val compressedBitmap = Objects.requireNonNull(
            activity?.model?.savedPictureUri
        )?.let {
            activity?.let { it1 ->
                decodeBitmapFromUri(it1.contentResolver, it, options, activity?.applicationContext)
            }
        }
        val testBitmap = getBitmapFromFile(testImageFile)

        Assert.assertThat(
            compressedBitmap?.width,
            Matchers.`is`(Matchers.equalTo(testBitmap?.width))
        )
        Assert.assertThat(
            compressedBitmap?.height,
            Matchers.`is`(Matchers.equalTo(testBitmap?.height))
        )
    }

    private fun createTestBitmap(): Bitmap {
        val bitmap: Bitmap
        val width = 720
        val height = 1280
        val bitmapConfig = Bitmap.Config.ARGB_8888
        val bytesPerPixel = 4
        val b = ByteArray(width * height * bytesPerPixel)
        val r = Random()
        r.setSeed(0)
        r.nextBytes(b)
        bitmap = Bitmap.createBitmap(width, height, bitmapConfig)
        val canvas = Canvas(bitmap)
        val byteIndex = 0
        val paint = Paint()
        for (i in 0 until height) {
            for (j in 0 until width) {
                val color: Int =
                    Color.argb(
                        b[byteIndex].toInt(),
                        b[byteIndex].toInt(),
                        b[byteIndex].toInt(),
                        b[byteIndex].toInt()
                    )
                paint.color = color
                canvas.drawPoint(j.toFloat(), i.toFloat(), paint)
            }
        }
        return bitmap
    }

    companion object {
        private var deletionFileList: ArrayList<File?>? = null
    }
}
