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

import android.net.Uri
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.EspressoUtils
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class CatrobatImageIOIntegrationTest {

    @get:Rule
    val launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    val screenshotOnFailRule = ScreenshotOnFailRule()

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = EspressoUtils.grantPermissionRulesVersionCheck()

    private lateinit var uriFile: Uri
    private lateinit var activity: MainActivity

    companion object {
        private const val IMAGE_NAME = "fileName"
    }

    @Before
    fun setUp() {
        activity = launchActivityRule.activity
    }

    @After
    fun tearDown() {
        with(File(uriFile.path!!)) {
            if (exists()) {
                delete()
            }
        }
    }

    @Test
    fun testWriteAndReadCatrobatImage() {
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_save_image))
            .perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_save_dialog_spinner))
            .perform(ViewActions.click())
        Espresso.onData(
            AllOf.allOf(
                Matchers.`is`(Matchers.instanceOf<Any>(String::class.java)),
                Matchers.`is`<String>(FileIO.FileType.CATROBAT.value)
            )
        ).inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText(IMAGE_NAME))
        onView(withText(R.string.save_button_text)).check(matches(isDisplayed()))
            .perform(ViewActions.click())
        onView(withText(R.string.overwrite_button_text)).check(matches(isDisplayed()))
            .perform(ViewActions.click())
        uriFile = activity.model.savedPictureUri!!
        Assert.assertNotNull(uriFile)
        Assert.assertNotNull(
            activity.workspace.getCommandSerializationHelper().readFromFile(uriFile)
        )
    }
}
