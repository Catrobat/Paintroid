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
package org.catrobat.paintroid.test.espresso

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Context
import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.grantPermissionRulesVersionCheck
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.OptionsMenuViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.helper.AdvancedSettingsAlgorithms
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf
import org.junit.After
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
    var activityTestRule = ActivityTestRule(
        MainActivity::class.java
    )

    @JvmField
    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    var defaultPictureName = "moreOptionsImageTest"
    @Before
    fun setUp() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        activityTestRule.activity.getPreferences(Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @After
    fun tearDown() {
        activityTestRule.activity.getPreferences(Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun testMoreOptionsCloseOnBack() {
        Espresso.onView(withText(R.string.menu_load_image))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.pressBack()
        Espresso.onView(withText(R.string.menu_load_image))
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun testMoreOptionsAllItemsExist() {
        OptionsMenuViewInteraction.onOptionsMenu()
            .checkItemExists(R.string.menu_load_image)
            .checkItemExists(R.string.menu_hide_menu)
            .checkItemExists(R.string.help_title)
            .checkItemExists(R.string.pocketpaint_menu_about)
            .checkItemExists(R.string.menu_rate_us)
            .checkItemExists(R.string.menu_save_image)
            .checkItemExists(R.string.menu_save_copy)
            .checkItemExists(R.string.menu_new_image)
            .checkItemExists(R.string.menu_feedback)
            .checkItemExists(R.string.share_image_menu)
            .checkItemExists(R.string.menu_advanced)
            .checkItemDoesNotExist(R.string.menu_discard_image)
            .checkItemDoesNotExist(R.string.menu_export)
    }

    @Test
    fun testMoreOptionsItemHelpClick() {
        Espresso.onView(withText(R.string.help_title)).perform(ViewActions.click())
    }

    @Test
    fun testMoreOptionsItemAboutClick() {
        Espresso.onView(withText(R.string.pocketpaint_about_title)).perform(ViewActions.click())
    }

    @Test
    fun testMoreOptionsShareImageClicked() {
        Espresso.onView(withText(R.string.share_image_menu)).perform(ViewActions.click())
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val uiObject = mDevice.findObject(UiSelector())
        Assert.assertTrue(uiObject.exists())
        mDevice.pressBack()
    }

    @Test
    fun testMoreOptionsItemNewImageClick() {
        Espresso.onView(withText(R.string.menu_new_image)).perform(ViewActions.click())
    }

    @Test
    fun testMoreOptionsItemMenuSaveClick() {
        Espresso.onView(withText(R.string.menu_save_image)).perform(ViewActions.click())
    }

    @Test
    fun testMoreOptionsItemMenuCopyClick() {
        Espresso.onView(withText(R.string.menu_save_copy)).perform(ViewActions.click())
    }

    @Test
    fun testMoreOptionsFeedbackClick() {
        val intent = Intent()
        Intents.init()
        val intentResult = ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)
        Espresso.onView(withText(R.string.menu_feedback)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_SENDTO))
        Intents.release()
    }

    @Test
    fun testShowLikeUsDialogOnFirstSave() {
        Espresso.onView(withText(R.string.menu_save_image)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(ViewActions.replaceText("likeus"))
        Espresso.onView(withText(R.string.save_button_text)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot()).perform(UiInteractions.waitFor(100))
        Espresso.onView(withText(R.string.pocketpaint_like_us))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testSaveDialogAppearsOnSaveImageClick() {
        Espresso.onView(withText(R.string.menu_save_image)).perform(ViewActions.click())
        Espresso.onView(withText(R.string.dialog_save_image_name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testSaveDialogAppearsOnSaveCopyClick() {
        Espresso.onView(withText(R.string.menu_save_copy)).perform(ViewActions.click())
        Espresso.onView(withText(R.string.dialog_save_image_name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testSaveDialogSavesChanges() {
        Espresso.onView(withText(R.string.menu_save_image)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.pocketpaint_save_info_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.pocketpaint_image_name_save_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.pocketpaint_save_dialog_spinner))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.pocketpaint_save_dialog_spinner))
            .perform(ViewActions.click())
        Espresso.onData(
            AllOf.allOf(
                Matchers.`is`(
                    Matchers.instanceOf<Any>(
                        String::class.java
                    )
                ),
                Matchers.`is`<String>("png")
            )
        ).inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())
        Espresso.onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(ViewActions.replaceText(defaultPictureName))
        Espresso.onView(withText(R.string.save_button_text))
            .perform(ViewActions.click())
        Espresso.pressBack()
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        Espresso.onView(withText(R.string.menu_save_image)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("png"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(defaultPictureName))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testShowRateUsDialogOnLikeUsDialogPositiveButtonPressed() {
        Espresso.onView(withText(R.string.menu_save_image)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(ViewActions.replaceText("1"))
        Espresso.onView(withText(R.string.save_button_text)).perform(ViewActions.click())
        Espresso.onView(withText(R.string.pocketpaint_yes)).perform(ViewActions.click())
        Espresso.onView(withText(R.string.pocketpaint_rate_us))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testShowFeedbackDialogOnLikeUsDialogNegativeButtonPressed() {
        Espresso.onView(withText(R.string.menu_save_image)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(ViewActions.replaceText("12"))
        Espresso.onView(withText(R.string.save_button_text)).perform(ViewActions.click())
        Espresso.onView(withText(R.string.pocketpaint_no)).perform(ViewActions.click())
        Espresso.onView(withText(R.string.pocketpaint_feedback))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testLikeUsDialogNotShownOnSecondSave() {
        Espresso.onView(withText(R.string.menu_save_image)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(ViewActions.replaceText("123"))
        Espresso.onView(withText(R.string.save_button_text)).perform(ViewActions.click())
        Espresso.onView(withText(R.string.pocketpaint_like_us))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.pressBack()
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        Espresso.onView(withText(R.string.menu_save_image)).perform(ViewActions.click())
        Espresso.onView(withText(R.string.pocketpaint_like_us)).check(ViewAssertions.doesNotExist())
        Espresso.onView(withText(R.string.save_button_text)).perform(ViewActions.click())
    }

    @Test
    fun testOnOffSmoothOptions() {
        Espresso.onView(withText(R.string.menu_advanced)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.pocketpaint_smoothing)).perform(ViewActions.click())
        Espresso.onView(withText(R.string.pocketpaint_ok)).perform(ViewActions.click())
        Assert.assertFalse("Smoothing is still on!", AdvancedSettingsAlgorithms.smoothing)
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        Espresso.onView(withText(R.string.menu_advanced)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.pocketpaint_smoothing)).perform(ViewActions.click())
        Espresso.onView(withText(R.string.pocketpaint_ok)).perform(ViewActions.click())
        Assert.assertTrue("Smoothing is still off!", AdvancedSettingsAlgorithms.smoothing)
    }

    @Test
    fun testNoChangeOnSmoothingWhenCancel() {
        Espresso.onView(withText(R.string.menu_advanced)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.pocketpaint_smoothing)).perform(ViewActions.click())
        Espresso.onView(withText(R.string.cancel_button_text)).perform(ViewActions.click())
        Assert.assertTrue("Smoothing is off after cancel!", AdvancedSettingsAlgorithms.smoothing)
    }

    companion object {
        @JvmField
        @ClassRule
        var grantPermissionRule = grantPermissionRulesVersionCheck()
    }
}
