/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.paintroid.test.espresso

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressMenuKey
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSpinnerText
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.presenter.MainActivityPresenter
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.grantPermissionRulesVersionCheck
import org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt
import org.catrobat.paintroid.test.espresso.util.UiInteractions.waitFor
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MenuFileActivityIntegrationTest {
    @get:Rule
    var launchActivityRule = IntentsTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    private lateinit var activity: MainActivity
    private var defaultFileName = "menuTestDefaultFile"
    private lateinit var idlingResource: CountingIdlingResource

    companion object {
        private lateinit var deletionFileList: ArrayList<File?>

        @get:ClassRule
        var grantPermissionRule: GrantPermissionRule = grantPermissionRulesVersionCheck()
    }

    @Before
    fun setUp() {
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.BRUSH)
        deletionFileList = ArrayList()
        activity = launchActivityRule.activity
        idlingResource = activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        for (file in deletionFileList) {
            if (file != null && file.exists()) {
                assertTrue(file.delete())
            }
        }
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testNewEmptyDrawingWithSave() {
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_new_image)).perform(click())
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(100))
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText("test987654"))
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(100))
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testLoadImageDialog() {
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_load_image)).perform(click())
        onView(withText(R.string.menu_replace_image)).perform(click())
        onView(withText(R.string.dialog_warning_new_image)).check(matches(isDisplayed()))
        onView(withText(R.string.save_button_text)).check(matches(isDisplayed()))
        onView(withText(R.string.discard_button_text)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoadImageDialogIntentCancel() {
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        val resultCancel = ActivityResult(Activity.RESULT_CANCELED, Intent())
        Intents.intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultCancel)
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_load_image)).perform(click())
        onView(withText(R.string.menu_replace_image)).perform(click())
        onView(withText(R.string.discard_button_text)).perform(click())
        onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist())
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testLoadImageDialogIntentOK() {
        onDrawingSurfaceView().perform(touchAt(HALFWAY_RIGHT_MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        val intent = Intent()
        intent.data = createTestImageFile()
        val resultOK = ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultOK)
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_load_image)).perform(click())
        onView(withText(R.string.menu_replace_image)).perform(click())
        onView(withText(R.string.discard_button_text)).perform(click())
        onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist())
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testLoadImageDialogOnBackPressed() {
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_load_image)).perform(click())
        pressBack()
        onDrawingSurfaceView().check(matches(isDisplayed()))
    }

    @Test
    fun testOnHelpDisabled() {
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.help_title)).check(matches(IsNot.not(isClickable())))
    }

    @Test
    fun testWarningDialogOnNewImage() {
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_new_image)).perform(click())
        onView(withText(R.string.dialog_warning_new_image)).check(matches(isDisplayed()))
        onView(withText(R.string.save_button_text)).check(matches(isDisplayed()))
        onView(withText(R.string.discard_button_text)).check(matches(isDisplayed()))
        pressBack()
        onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist())
    }

    @Test
    fun testNewEmptyDrawingWithDiscard() {
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_new_image)).perform(click())
        onView(withText(R.string.discard_button_text)).perform(click())
        onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist())
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testNewEmptyDrawingDialogOnBackPressed() {
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_new_image)).perform(click())
        onView(withText(R.string.dialog_warning_new_image)).check(matches(isDisplayed()))
        onView(withText(R.string.save_button_text)).check(matches(isDisplayed()))
        onView(withText(R.string.discard_button_text)).check(matches(isDisplayed()))
        pressBack()
        onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist())
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testSavedStateChangeAfterSave() {
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        assertFalse(activity.model.isSaved)
        pressMenuKey()
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText("test98765"))
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(100))
        assertNotNull(activity.model.savedPictureUri)
        addUriToDeletionFileList(activity.model.savedPictureUri)
        assertTrue(activity.model.isSaved)
    }

    @Test
    fun testSaveImage() {
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(100))
        assertNotNull(activity.model.savedPictureUri)
        if (!activity.model.isOpenedFromCatroid) {
            assertNotSame(
                "null",
                MainActivityPresenter.getPathFromUri(activity, activity.model.savedPictureUri!!)
            )
        }
        addUriToDeletionFileList(activity.model.savedPictureUri)
    }

    @Test
    fun testSaveCopy() {
        launchActivityRule.activity.getPreferences(Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText("testSaveCopy"))
        onView(withText(R.string.save_button_text)).perform(click())
        assertNotNull(activity.model.savedPictureUri)
        if (!activity.model.isOpenedFromCatroid) {
            assertNotSame(
                "null",
                MainActivityPresenter.getPathFromUri(activity, activity.model.savedPictureUri!!)
            )
        }
        addUriToDeletionFileList(activity.model.savedPictureUri)
        val oldFile = File(activity.model.savedPictureUri.toString())
        onView(withText(R.string.pocketpaint_no)).perform(click())
        onView(withText(R.string.pocketpaint_ok)).perform(click())
        onDrawingSurfaceView().perform(touchAt(HALFWAY_BOTTOM_MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_copy)).perform(click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText("copy1"))
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(100))
        val newFile = File(activity.model.savedPictureUri.toString())
        assertNotSame("Changes to saved", oldFile, newFile)
        assertNotNull(activity.model.savedPictureUri)
        if (!activity.model.isOpenedFromCatroid) {
            assertNotSame(
                "null",
                MainActivityPresenter.getPathFromUri(activity, activity.model.savedPictureUri!!)
            )
        }
        addUriToDeletionFileList(activity.model.savedPictureUri)
    }

    @Test
    fun testAskForSaveAfterSavedOnce() {
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText("AskForSaveAfterSavedOnce"))
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(100))
        assertNotNull(activity.model.savedPictureUri)
        addUriToDeletionFileList(activity.model.savedPictureUri)
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        pressBack()
        onView(withText(R.string.menu_quit)).check(matches(isDisplayed()))
    }

    @Test
    fun testShowOverwriteDialogAfterSavingAgain() {
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText("12345test12345"))
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(100))
        assertNotNull(activity.model.savedPictureUri)
        addUriToDeletionFileList(activity.model.savedPictureUri)
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(100))
        onView(withText(R.string.overwrite_button_text)).check(matches(isDisplayed()))
    }

    @Test
    fun testCheckImageNumberIncrementAfterSaveWithStandardName() {
        FileIO.filename = "image"
        val imageNumber = launchActivityRule.activity.presenter.imageNumber
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(200))
        assertNotNull(activity.model.savedPictureUri)
        addUriToDeletionFileList(activity.model.savedPictureUri)
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(isRoot()).perform(waitFor(200))
        val newImageNumber = launchActivityRule.activity.presenter.imageNumber
        assertEquals((imageNumber + 1).toLong(), newImageNumber.toLong())
    }

    @Test
    fun testCheckImageNumberSameAfterSaveWithNonStandardName() {
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        val imageNumber = launchActivityRule.activity.presenter.imageNumber
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText("test9876"))
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(100))
        assertNotNull(activity.model.savedPictureUri)
        addUriToDeletionFileList(activity.model.savedPictureUri)
        val newImageNumber = launchActivityRule.activity.presenter.imageNumber
        assertEquals(imageNumber.toLong(), newImageNumber.toLong())
    }

    @Test
    fun testCheckSaveFileWithDifferentFormats() {
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withId(R.id.pocketpaint_save_info_title)).check(matches(isDisplayed()))
        onView(withId(R.id.pocketpaint_image_name_save_text)).check(matches(isDisplayed()))
        onView(withId(R.id.pocketpaint_save_dialog_spinner)).check(matches(isDisplayed()))
        onView(withId(R.id.pocketpaint_save_dialog_spinner)).perform(click())
        onData(allOf(`is`(instanceOf<Any>(String::class.java)), `is`<String>("png")))
            .inRoot(RootMatchers.isPlatformPopup()).perform(click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText(defaultFileName))
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(100))
        assertNotNull(activity.model.savedPictureUri)
        addUriToDeletionFileList(activity.model.savedPictureUri)
        val oldFile = File(activity.model.savedPictureUri.toString())
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withId(R.id.pocketpaint_save_dialog_spinner)).perform(click())
        onData(allOf(`is`(instanceOf<Any>(String::class.java)), `is`<String>("jpg")))
            .inRoot(RootMatchers.isPlatformPopup()).perform(click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText(defaultFileName))
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(100))
        assertNotNull(activity.model.savedPictureUri)
        addUriToDeletionFileList(activity.model.savedPictureUri)
        val newFile = File(activity.model.savedPictureUri.toString())
        assertNotSame(oldFile, newFile)
    }

    @Test
    fun testCheckSaveImageDialogShowJPGSpinnerText() {
        createImageIntent()
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_load_image)).perform(click())
        onView(withText(R.string.menu_replace_image)).perform(click())
        onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist())
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withId(R.id.pocketpaint_save_dialog_spinner))
            .check(matches(withSpinnerText(containsString("jpg"))))
    }

    @Test
    fun testCheckSaveImageDialogShowPNGSpinnerText() {
        FileIO.fileType = FileIO.FileType.PNG
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withId(R.id.pocketpaint_save_dialog_spinner))
            .check(matches(withSpinnerText(containsString("png"))))
    }

    @Test
    fun testCheckSaveImageDialogShowORASpinnerText() {
        FileIO.fileType = FileIO.FileType.ORA
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withId(R.id.pocketpaint_save_dialog_spinner))
            .check(matches(withSpinnerText(containsString("ora"))))
    }

    @Test
    fun testCheckSaveImageDialogShowsSavedImageOptions() {
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        val imageName = "test12345"
        onView(withId(R.id.pocketpaint_image_name_save_text)).perform(replaceText(imageName))
        onView(withId(R.id.pocketpaint_save_dialog_spinner)).perform(click())
        onData(allOf(`is`(instanceOf<Any>(String::class.java)), `is`<String>("png")))
            .inRoot(RootMatchers.isPlatformPopup()).perform(click())
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(100))
        assertNotNull(activity.model.savedPictureUri)
        addUriToDeletionFileList(activity.model.savedPictureUri)
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withText(imageName)).check(matches(isDisplayed()))
        onView(withText("png")).check(matches(isDisplayed()))
    }

    @Test
    fun testCheckCopyIsAlwaysDefaultOptions() {
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_copy)).perform(click())
        var imageNumber = launchActivityRule.activity.presenter.imageNumber
        onView(withText("png")).check(matches(isDisplayed()))
        onView(withText("image$imageNumber")).check(matches(isDisplayed()))
        onView(withId(R.id.pocketpaint_save_dialog_spinner)).perform(click())
        onData(allOf(`is`(instanceOf<Any>(String::class.java)), `is`<String>("png")))
            .inRoot(RootMatchers.isPlatformPopup()).perform(click())
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(100))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_copy)).perform(click())
        imageNumber = launchActivityRule.activity.presenter.imageNumber
        onView(withText("png")).check(matches(isDisplayed()))
        onView(withText("image$imageNumber")).check(matches(isDisplayed()))
    }

    private fun createImageIntent() {
        val intent = Intent()
        intent.data = createTestImageFile()
        val resultOK = ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultOK)
    }

    private fun createTestImageFile(): Uri? {
        val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "testfile.jpg")
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        val resolver = activity.contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        try {
            val fos = imageUri?.let { resolver.openOutputStream(it) }
            assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos))
            assert(fos != null)
            fos?.close()
        } catch (e: IOException) {
            throw AssertionError("Picture file could not be created.", e)
        }
        val imageFile = File(imageUri?.path, "testfile.jpg")
        deletionFileList.add(imageFile)
        return imageUri
    }

    @Test
    fun testLoadImageTransparency() {
        val intent = Intent()
        intent.data = createTestImageFile()
        val result = ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_load_image)).perform(click())
        onView(withText(R.string.menu_replace_image)).perform(click())
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.ERASER)
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testSameFileNameAfterOverwritePng() {
        val name = "testPNG"
        FileIO.filename = name
        FileIO.fileType = FileIO.FileType.PNG
        FileIO.compressFormat = Bitmap.CompressFormat.PNG
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(200))
        val uri = activity.model.savedPictureUri
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(100))
        onView(withText(R.string.overwrite_button_text)).check(matches(isDisplayed()))
        onView(withText(R.string.overwrite_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(500))

        val oldFileName = uri?.path?.let { File(it).name }
        val newFileName = activity.model.savedPictureUri?.path?.let { File(it).name }

        assertEquals(oldFileName, newFileName)
        addUriToDeletionFileList(activity.model.savedPictureUri)
    }

    @Test
    fun testSameFileNameAfterOverwriteJpg() {
        val name = "testJPG"
        FileIO.filename = name
        FileIO.fileType = FileIO.FileType.JPG
        FileIO.compressFormat = Bitmap.CompressFormat.JPEG
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(200))
        val uri = activity.model.savedPictureUri
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(100))
        onView(withText(R.string.overwrite_button_text)).check(matches(isDisplayed()))
        onView(withText(R.string.overwrite_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(500))

        val oldFileName = uri?.path?.let { File(it).name }
        val newFileName = activity.model.savedPictureUri?.path?.let { File(it).name }

        assertEquals(oldFileName, newFileName)
        addUriToDeletionFileList(activity.model.savedPictureUri)
    }

    @Test
    fun testSameFileNameAfterOverwriteOra() {
        val name = "testORA"
        FileIO.filename = name
        FileIO.fileType = FileIO.FileType.ORA
        FileIO.compressFormat = Bitmap.CompressFormat.PNG
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(500))
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(500))
        onView(withText(R.string.overwrite_button_text)).check(matches(isDisplayed()))
        onView(withText(R.string.overwrite_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(500))

        var newFileName = "new"
        val uri = activity.model.savedPictureUri
        if (uri != null) {
            val cursor = activity.contentResolver.query(
                uri,
                arrayOf(MediaStore.Images.ImageColumns.DISPLAY_NAME),
                null, null, null
            )
            cursor?.use {
                if (cursor.moveToFirst()) {
                    newFileName =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
                }
            }
        }

        assertEquals(newFileName, "testORA.ora")
        addUriToDeletionFileList(activity.model.savedPictureUri)
    }

    @Test
    fun testSameFileNameAfterOverwriteCatrobatImage() {
        val name = "testCI"
        FileIO.filename = name
        FileIO.fileType = FileIO.FileType.CATROBAT
        FileIO.compressFormat = Bitmap.CompressFormat.PNG
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(500))
        onDrawingSurfaceView().perform(touchAt(MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(click())
        onView(withText(R.string.save_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(500))
        onView(withText(R.string.overwrite_button_text)).check(matches(isDisplayed()))
        onView(withText(R.string.overwrite_button_text)).perform(click())
        onView(isRoot()).perform(waitFor(500))

        var newFileName = "new"
        val uri = activity.model.savedPictureUri
        if (uri != null) {
            val cursor = activity.contentResolver.query(
                uri,
                arrayOf(MediaStore.Images.ImageColumns.DISPLAY_NAME),
                null, null, null
            )
            cursor?.use {
                if (cursor.moveToFirst()) {
                    newFileName =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
                }
            }
        }

        assertEquals(newFileName, "testCI.catrobat-image")
        addUriToDeletionFileList(activity.model.savedPictureUri)
    }

    private fun addUriToDeletionFileList(uri: Uri?) {
        uri?.path?.let {
            deletionFileList.add(File(it))
        }
    }
}
