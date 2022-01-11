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
package org.catrobat.paintroid.test.espresso.catroid

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import id.zelory.compressor.Compressor
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.catrobat.paintroid.FileIO.createNewEmptyPictureFile
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.common.PAINTROID_PICTURE_PATH
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.EspressoUtils
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException
import java.lang.AssertionError
import java.lang.NullPointerException
import java.util.Objects
import kotlin.collections.ArrayList

private const val IMAGE_NAME = "testFile"

@RunWith(AndroidJUnit4::class)
class OpenedFromPocketCodeWithImageTest {
    @get:Rule
    var launchActivityRule = IntentsTestRule(
        MainActivity::class.java, false, true
    )

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    private var imageFile: File? = null
    private var activity: MainActivity? = null
    private var deletionFileList: ArrayList<File?>? = null

    @Before
    fun setUp() {
        deletionFileList = ArrayList()
        activity = launchActivityRule.activity
        imageFile = getNewImageFile(IMAGE_NAME)
        deletionFileList!!.add(imageFile)
        launchActivityRule.activity.model.savedPictureUri = Uri.fromFile(imageFile)
        launchActivityRule.activity.model.isOpenedFromCatroid = true
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
    }

    @After
    fun tearDown() {
        for (file in deletionFileList!!) {
            if (file != null && file.exists()) {
                assertTrue(file.delete())
            }
        }
    }

    @Test
    fun testSave() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        val lastModifiedBefore = imageFile!!.lastModified()
        val fileSizeBefore = imageFile!!.length()
        pressBackUnconditionally()
        runBlocking {
            delay(500)
        }
        verifyImageFile(lastModifiedBefore, fileSizeBefore)
    }

    @Test
    fun testLoadWithoutChange() {
        val lastModifiedBefore = imageFile!!.lastModified()
        val fileSizeBefore = imageFile!!.length()
        createImageIntent()
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(ViewMatchers.withText(R.string.menu_load_image))
            .perform(click())
        onView(ViewMatchers.withText(R.string.dialog_warning_new_image))
            .check(ViewAssertions.doesNotExist())
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.WHITE, BitmapLocationProvider.MIDDLE)
        pressBackUnconditionally()
        verifyImageFile(lastModifiedBefore, fileSizeBefore)
    }

    @Test
    fun testLoadWithChange() {
        val lastModifiedBefore = imageFile!!.lastModified()
        val fileSizeBefore = imageFile!!.length()
        createImageIntent()
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(ViewMatchers.withText(R.string.menu_load_image))
            .perform(click())
        onView(ViewMatchers.withText(R.string.dialog_warning_new_image))
            .check(ViewAssertions.doesNotExist())
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.WHITE, BitmapLocationProvider.MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        pressBackUnconditionally()
        verifyImageFile(lastModifiedBefore, fileSizeBefore)
    }

    @Test
    fun testBackToPocketCode() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        pressBackUnconditionally()
        val lastModifiedBefore = imageFile!!.lastModified()
        val fileSizeBefore = imageFile!!.length()
        assertThat(
            "Image modified",
            imageFile!!.lastModified(),
            Matchers.equalTo(lastModifiedBefore)
        )
        assertThat(
            "Saved image length changed",
            imageFile!!.length(),
            Matchers.equalTo(fileSizeBefore)
        )
    }

    private fun getNewImageFile(filename: String): File {
        return try {
            createNewEmptyPictureFile(filename, launchActivityRule.activity)
        } catch (e: NullPointerException) {
            throw AssertionError("Could not create temp file", e)
        }
    }

    private fun createImageIntent() {
        val intent = Intent()
        intent.data = createTestImageFile()
        val resultOK = ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultOK)
    }

    private fun createTestImageFile(): Uri {
        val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val uncompressedImageFile = File(
            activity!!.getExternalFilesDir(null)!!.absolutePath,
            "uncompressed_$IMAGE_NAME.jpg"
        )
        try {
            val uncompressedImageUri = Uri.fromFile(uncompressedImageFile)
            val fos = activity!!.contentResolver.openOutputStream(
                Objects.requireNonNull(uncompressedImageUri)
            )
            assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos))
            assert(fos != null)
            fos!!.close()
        } catch (e: IOException) {
            throw AssertionError("Picture file could not be created.", e)
        }
        val compressor = Compressor(launchActivityRule.activity)
        compressor.setCompressFormat(Bitmap.CompressFormat.JPEG)
        compressor.setQuality(100)
        compressor.setDestinationDirectoryPath(activity!!.getExternalFilesDir(null)!!.absolutePath + "/Pictures")
        deletionFileList!!.add(uncompressedImageFile)
        imageFile = try {
            compressor.compressToFile(uncompressedImageFile, "$IMAGE_NAME.png")
        } catch (e: IOException) {
            throw AssertionError("Test Picture file could not be created.", e)
        }
        deletionFileList!!.add(imageFile)
        return Uri.fromFile(imageFile)
    }

    private fun verifyImageFile(lastModifiedBefore: Long, fileSizeBefore: Long) {
        val path =
            launchActivityRule.activityResult.resultData.getStringExtra(PAINTROID_PICTURE_PATH)
        assertEquals(imageFile!!.absolutePath, path)
        assertThat(
            "Image modification not saved",
            imageFile!!.lastModified(),
            Matchers.greaterThan(lastModifiedBefore)
        )
        assertThat(
            "Saved image length not changed",
            imageFile!!.length(),
            Matchers.greaterThan(fileSizeBefore)
        )
    }

    companion object {
        @get:ClassRule
        var grantPermissionRule = EspressoUtils.grantPermissionRulesVersionCheck()
    }
}
