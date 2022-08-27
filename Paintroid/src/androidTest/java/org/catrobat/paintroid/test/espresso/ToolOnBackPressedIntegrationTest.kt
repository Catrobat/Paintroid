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
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.paintroid.FileIO.getUriForFilenameInPicturesFolder
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.grantPermissionRulesVersionCheck
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView
import org.catrobat.paintroid.test.espresso.util.wrappers.ConfirmQuitDialogInteraction.onConfirmQuitDialog
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ToolOnBackPressedIntegrationTest {
    @Rule
    var launchActivityRule = IntentsTestRule(MainActivity::class.java, false, true)

    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var saveFile: File? = null
    private var toolReference: ToolReference? = null
    private val defaultPictureName = "catroidTemp"

    @Before
    fun setUp() {
        val activity = launchActivityRule.activity
        toolReference = activity.toolReference
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.BRUSH)
    }

    @After
    fun tearDown() {
        if (saveFile != null && saveFile?.exists() == true) {
            saveFile?.delete()
            saveFile = null
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
        assertTrue(launchActivityRule.activity.isFinishing)
    }

    @Suppress("LongMethod")
    @Test
    @Throws(IOException::class)
    fun testBrushToolBackPressedWithSaveAndOverride() {
        onTopBarView().performOpenMoreOptions()
        onView(ViewMatchers.withText(R.string.menu_save_image))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.pocketpaint_save_info_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.pocketpaint_image_name_save_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.pocketpaint_save_dialog_spinner))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.pocketpaint_save_dialog_spinner))
            .perform(ViewActions.click())
        Espresso.onData(
            AllOf.allOf(Matchers.`is`(Matchers.instanceOf<Any>(String::class.java)), Matchers.`is`<String>("png"))
        ).inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.pocketpaint_image_name_save_text))
            .perform(ViewActions.replaceText(defaultPictureName))
        onView(ViewMatchers.withText(R.string.save_button_text))
            .perform(ViewActions.click())
        onView(ViewMatchers.isRoot()).perform(UiInteractions.waitFor(200))

        val filename = defaultPictureName + FILE_ENDING
        val resolver = launchActivityRule.activity.contentResolver
        var uri = getUriForFilenameInPicturesFolder(filename, resolver)
        val options = BitmapFactory.Options()
        assertNotNull(uri)
        var inputStream = resolver.openInputStream(uri!!)
        val oldBitmap = BitmapFactory.decodeStream(inputStream, null, options)

        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        Espresso.pressBack()
        onConfirmQuitDialog().onPositiveButton()
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.pocketpaint_save_info_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.pocketpaint_image_name_save_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.pocketpaint_save_dialog_spinner))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.pocketpaint_save_dialog_spinner))
            .perform(ViewActions.click())
        Espresso.onData(
            AllOf.allOf(Matchers.`is`(Matchers.instanceOf<Any>(String::class.java)), Matchers.`is`<String>("png"))
        ).inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.pocketpaint_image_name_save_text))
            .perform(ViewActions.replaceText(defaultPictureName))
        onView(ViewMatchers.withText(R.string.save_button_text))
            .perform(ViewActions.click())
        onView(ViewMatchers.isRoot()).perform(UiInteractions.waitFor(200))
        onView(ViewMatchers.withText(R.string.overwrite_button_text))
            .perform(ViewActions.click())
        onView(ViewMatchers.isRoot()).perform(UiInteractions.waitFor(200))
        uri = getUriForFilenameInPicturesFolder(filename, resolver)
        assertNotNull(uri)
        inputStream = uri?.let { resolver.openInputStream(it) }

        val actualBitmap = BitmapFactory.decodeStream(inputStream, null, options)
        assertNotNull(oldBitmap)
        assertNotNull(actualBitmap)
        if (oldBitmap != null) {
            assertFalse(
                "Bitmaps are the same, should be different", oldBitmap.sameAs(actualBitmap)
            )
        }
    }

    @Test
    fun testNotBrushToolBackPressed() {
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.CURSOR)
        Espresso.pressBack()
        assertEquals(toolReference?.tool?.toolType, ToolType.BRUSH)
    }

    @Test
    fun testToolOptionsGoBackWhenBackPressed() {
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.CURSOR)
        assertEquals(toolReference?.tool?.toolType, ToolType.CURSOR)
        Espresso.pressBack()
        assertEquals(toolReference?.tool?.toolType, ToolType.BRUSH)
    }

    @Test
    @Throws(SecurityException::class, IllegalArgumentException::class, InterruptedException::class)
    fun testBrushToolBackPressedFromCatroidAndUsePicture() {
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        val pathToFile =
            (
                launchActivityRule.activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator +
                    defaultPictureName +
                    FILE_ENDING
                )
        saveFile = File(pathToFile)
        launchActivityRule.activity.model.savedPictureUri = Uri.fromFile(saveFile)
        launchActivityRule.activity.model.isOpenedFromCatroid = true
        Espresso.pressBackUnconditionally()
        onView(ViewMatchers.isRoot()).perform(UiInteractions.waitFor(2000))
        assertTrue(launchActivityRule.activity.isFinishing)
        saveFile?.exists()?.let { assertTrue(it) }
        assertThat(saveFile?.length(), Matchers.`is`(Matchers.greaterThan(0L)))
    }

    @Test
    fun testCloseLayerDialogOnBackPressed() {
        onView(ViewMatchers.withId(R.id.pocketpaint_drawer_layout))
            .perform(DrawerActions.open(Gravity.END))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.pressBack()
        onView(ViewMatchers.withId(R.id.pocketpaint_drawer_layout))
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

        @ClassRule
        var grantPermissionRule = grantPermissionRulesVersionCheck()
    }
}
