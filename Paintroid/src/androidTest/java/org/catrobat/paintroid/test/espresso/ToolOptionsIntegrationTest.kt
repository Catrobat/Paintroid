/**
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
import android.net.Uri
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.AssertionError

@RunWith(Parameterized::class)
class ToolOptionsIntegrationTest {
    @Rule
    var activityTestRule: ActivityTestRule<MainActivity> = IntentsTestRule(MainActivity::class.java)

    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Parameterized.Parameter
    var toolType: ToolType? = null

    @Parameterized.Parameter(1)
    var toolOptionsShownInitially = false

    @Parameterized.Parameter(2)
    var hasToolOptionsView = false
    private var testImageFile: File? = null

    @Before
    fun setUp() {
        try {
            testImageFile = File.createTempFile("PocketPaintTest", ".png")
            val bitmap = Bitmap.createBitmap(25, 25, Bitmap.Config.ARGB_8888)
            val outputStream: OutputStream = FileOutputStream(testImageFile)
            Assert.assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream))
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
    fun tearDown() { testImageFile?.delete()?.let { Assert.assertTrue(it) } }

    @Test
    fun testToolOptions() {
        onToolBarView().performSelectTool(toolType)
        if (!toolOptionsShownInitially) {
            onToolBarView().performOpenToolOptionsView()
        }
        if (hasToolOptionsView) {
            onToolBarView().onToolOptionsView().check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        } else {
            onToolBarView().onToolOptionsView()
                .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        }
    }

    companion object {
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return listOf(
                arrayOf(ToolType.BRUSH, true, true),
                arrayOf(ToolType.SHAPE, true, true),
                arrayOf(ToolType.TRANSFORM, true, true),
                arrayOf(ToolType.LINE, true, true),
                arrayOf(ToolType.CURSOR, true, true),
                arrayOf(ToolType.FILL, true, true),
                arrayOf(ToolType.PIPETTE, false, false),
                arrayOf(ToolType.STAMP, true, true),
                arrayOf(ToolType.ERASER, true, true),
                arrayOf(ToolType.TEXT, true, true),
                arrayOf(ToolType.HAND, false, false),
                arrayOf(ToolType.WATERCOLOR, true, true)
            )
        }
    }
}
