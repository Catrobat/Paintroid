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
package org.catrobat.paintroid.test.espresso

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.view.Gravity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.paintroid.FileIO.getUriForFilenameInPicturesFolder
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.grantPermissionRulesVersionCheck
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.Companion.onColorPickerView
import org.catrobat.paintroid.test.espresso.util.wrappers.ConfirmQuitDialogInteraction.Companion.onConfirmQuitDialog
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.Companion.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.Companion.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.After
import org.junit.ClassRule
import org.junit.Assert

@RunWith(AndroidJUnit4::class)
class ToolOnBackPressedIntegrationTest {
    @JvmField
    @Rule
    var launchActivityRule = IntentsTestRule(MainActivity::class.java, false, true)

    @JvmField
    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var saveFile: File? = null
    private var toolReference: ToolReference? = null
    private val defaultPictureName = "catroidTemp"
    @Before
    fun setUp() {
        val activity = launchActivityRule.activity
        toolReference = activity.toolReference
        onToolBarView()
                .performSelectTool(ToolType.BRUSH)
    }

    @After
    fun tearDown() {
        if (saveFile != null && saveFile!!.exists()) {
            saveFile!!.delete()
            saveFile = null
        }
        val imagesDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val pathToFile = imagesDirectory + File.separator + defaultPictureName + FILE_ENDING
        val imageFile = File(pathToFile)
        if (imageFile.exists()) {
            imageFile.delete()
        }
    }

    @Test
    fun testBrushToolBackPressed() {
        onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        Espresso.pressBack()
        onConfirmQuitDialog()
                .checkPositiveButton(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .checkNegativeButton(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .checkNeutralButton(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
                .checkMessage(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .checkTitle(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.pressBack()
        onConfirmQuitDialog()
                .checkPositiveButton(ViewAssertions.doesNotExist())
                .checkNegativeButton(ViewAssertions.doesNotExist())
                .checkNeutralButton(ViewAssertions.doesNotExist())
                .checkMessage(ViewAssertions.doesNotExist())
                .checkTitle(ViewAssertions.doesNotExist())
        Espresso.pressBack()
        onConfirmQuitDialog().onNegativeButton()
                .perform(ViewActions.click())
        Assert.assertTrue(launchActivityRule.activity.isFinishing)
    }

    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun testBrushToolBackPressedWithSaveAndOverride() {
        TopBarViewInteraction.onTopBarView()
                .performOpenMoreOptions()
        Espresso.onView(withText(R.string.menu_save_image))
                .perform(ViewActions.click())
        Espresso.onView(withId(R.id.pocketpaint_save_info_title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.pocketpaint_image_name_save_text)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.pocketpaint_save_dialog_spinner)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.pocketpaint_save_dialog_spinner))
                .perform(ViewActions.click())
        Espresso.onData(AllOf.allOf(Matchers.`is`(Matchers.instanceOf<Any>(String::class.java)),
                Matchers.`is`("png"))).inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())
        Espresso.onView(withId(R.id.pocketpaint_image_name_save_text))
                .perform(ViewActions.replaceText(defaultPictureName))
        Espresso.onView(withText(R.string.save_button_text))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot()).perform(UiInteractions.waitFor(3000))
        val filename = defaultPictureName + FILE_ENDING
        val resolver = launchActivityRule.activity.contentResolver
        var uri = getUriForFilenameInPicturesFolder(filename, resolver)
        val options = BitmapFactory.Options()
        Assert.assertNotNull(uri)
        var inputStream = resolver.openInputStream(uri!!)
        val oldBitmap = BitmapFactory.decodeStream(inputStream, null, options)
        Espresso.onView(ViewMatchers.isRoot()).perform(UiInteractions.waitFor(2000))
        onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        Espresso.pressBack()
        onConfirmQuitDialog().onPositiveButton()
                .perform(ViewActions.click())
        Espresso.onView(withId(R.id.pocketpaint_save_info_title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.pocketpaint_image_name_save_text)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.pocketpaint_save_dialog_spinner)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.pocketpaint_save_dialog_spinner))
                .perform(ViewActions.click())
        Espresso.onData(AllOf.allOf(Matchers.`is`(Matchers.instanceOf<Any>(String::class.java)),
                Matchers.`is`("png"))).inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())
        Espresso.onView(withId(R.id.pocketpaint_image_name_save_text))
                .perform(ViewActions.replaceText(defaultPictureName))
        Espresso.onView(withText(R.string.save_button_text))
                .perform(ViewActions.click())
        Espresso.onView(withText(R.string.overwrite_button_text))
                .perform(ViewActions.click())
        while (!launchActivityRule.activity.isFinishing) {
            Thread.sleep(1000)
        }
        uri = getUriForFilenameInPicturesFolder(filename, resolver)
        Assert.assertNotNull(uri)
        inputStream = resolver.openInputStream(uri!!)
        val actualBitmap = BitmapFactory.decodeStream(inputStream, null, options)
        Assert.assertNotNull(oldBitmap)
        Assert.assertNotNull(actualBitmap)
        Assert.assertFalse("Bitmaps are the same, should be different", oldBitmap!!.sameAs(actualBitmap))
    }

    @Test
    fun testNotBrushToolBackPressed() {
        onToolBarView()
                .performSelectTool(ToolType.CURSOR)
        Espresso.pressBack()
        Assert.assertEquals(toolReference!!.tool!!.toolType, ToolType.BRUSH)
    }

    @Test
    fun testToolOptionsGoBackWhenBackPressed() {
        onToolBarView()
                .performSelectTool(ToolType.CURSOR)
        Assert.assertEquals(toolReference!!.tool!!.toolType, ToolType.CURSOR)
        Espresso.pressBack()
        Assert.assertEquals(toolReference!!.tool!!.toolType, ToolType.BRUSH)
    }

    @Test
    @Throws(SecurityException::class, IllegalArgumentException::class, InterruptedException::class)
    fun testBrushToolBackPressedFromCatroidAndUsePicture() {
        onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        val pathToFile = launchActivityRule.activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .toString() + File.separator +
                defaultPictureName +
                FILE_ENDING
        saveFile = File(pathToFile)
        launchActivityRule.activity.model.savedPictureUri = Uri.fromFile(saveFile)
        launchActivityRule.activity.model.isOpenedFromCatroid = true
        Espresso.pressBackUnconditionally()
        while (!launchActivityRule.activity.isFinishing) {
            Thread.sleep(1000)
        }
        Assert.assertTrue(launchActivityRule.activity.isFinishing)
        Assert.assertTrue(saveFile!!.exists())
        Assert.assertThat(saveFile!!.length(), Matchers.`is`(Matchers.greaterThan(0L)))
    }

    @Test
    fun testCloseLayerDialogOnBackPressed() {
        Espresso.onView(withId(R.id.pocketpaint_drawer_layout))
                .perform(DrawerActions.open(Gravity.END))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.pressBack()
        Espresso.onView(withId(R.id.pocketpaint_drawer_layout))
                .check(ViewAssertions.matches(DrawerMatchers.isClosed()))
    }

    @Test
    fun testCloseColorPickerDialogOnBackPressed() {
        onColorPickerView()
                .performOpenColorPicker()
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onColorPickerView()
                .perform(ViewActions.closeSoftKeyboard())
                .perform(ViewActions.pressBack())
                .check(ViewAssertions.doesNotExist())
    }

    companion object {
        private const val FILE_ENDING = ".png"
        @JvmField
        @ClassRule
        var grantPermissionRule = grantPermissionRulesVersionCheck()
    }
}
