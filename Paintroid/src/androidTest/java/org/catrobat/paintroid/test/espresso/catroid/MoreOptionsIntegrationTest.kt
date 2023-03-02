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

import android.content.Intent
import android.graphics.Color
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.common.PAINTROID_PICTURE_NAME
import org.catrobat.paintroid.common.PAINTROID_PICTURE_PATH
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.grantPermissionRulesVersionCheck
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.OptionsMenuViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MoreOptionsIntegrationTest {
    @JvmField
    @Rule
    var launchActivityRule = IntentsTestRule(
        MainActivity::class.java, false, false
    )
    @JvmField
    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    @Before
    fun setUp() {
        val intent = Intent()
        intent.putExtra(PAINTROID_PICTURE_PATH, "")
        intent.putExtra(PAINTROID_PICTURE_NAME, "testFile")
        launchActivityRule.launchActivity(intent)
    }

    @Test
    fun testMoreOptionsAllItemsExist() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        OptionsMenuViewInteraction.onOptionsMenu()
            .checkItemExists(R.string.menu_load_image)
            .checkItemExists(R.string.menu_hide_menu)
            .checkItemExists(R.string.help_title)
            .checkItemExists(R.string.pocketpaint_menu_about)
            .checkItemExists(R.string.share_image_menu)
            .checkItemDoesNotExist(R.string.menu_save_image)
            .checkItemDoesNotExist(R.string.menu_save_copy)
            .checkItemDoesNotExist(R.string.menu_new_image)
            .checkItemDoesNotExist(R.string.menu_rate_us)
            .checkItemExists(R.string.menu_discard_image)
            .checkItemExists(R.string.menu_export)
    }

    @Test
    fun testMoreOptionsDiscardImage() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        Espresso.onView(withText(R.string.menu_discard_image))
            .perform(ViewActions.click())
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testMoreOptionsShareImageClick() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        Espresso.onView(withText(R.string.share_image_menu))
            .perform(ViewActions.click())
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val uiObject = mDevice.findObject(UiSelector())
        Assert.assertTrue(uiObject.exists())
        mDevice.pressBack()
    }

    companion object {
        @JvmField
        @ClassRule
        var grantPermissionRule = grantPermissionRulesVersionCheck()
    }
}
