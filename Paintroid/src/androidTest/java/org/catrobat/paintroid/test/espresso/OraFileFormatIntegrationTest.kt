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
import android.content.Intent
import android.net.Uri
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.grantPermissionRulesVersionCheck
import org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt
import org.catrobat.paintroid.test.espresso.util.UiInteractions.waitFor
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.AllOf
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class OraFileFormatIntegrationTest {
    @get:Rule
    var launchActivityRule = IntentsTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private lateinit var activity: MainActivity

    companion object {
        private lateinit var deletionFileList: ArrayList<File?>

        @get:ClassRule
        var grantPermissionRule: GrantPermissionRule = grantPermissionRulesVersionCheck()
    }

    @Before
    fun setUp() {
        deletionFileList = ArrayList()
        activity = launchActivityRule.activity
    }

    @After
    fun tearDown() {
        for (file in deletionFileList) {
            if (file != null && file.exists()) {
                assertTrue(file.delete())
            }
        }
    }

    @Test
    fun testSaveAsOraFile() {
        onDrawingSurfaceView().perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_save_dialog_spinner)).perform(ViewActions.click())
        onData(AllOf.allOf(`is`(instanceOf<Any>(String::class.java)), `is`<String>("ora")))
            .inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(ViewActions.replaceText("test1337"))
        runBlocking {
            onView(withText(R.string.save_button_text)).perform(ViewActions.click())
            delay(500)
        }
        assertNotNull(activity.model.savedPictureUri)
        addUriToDeletionFileList(activity.model.savedPictureUri)
    }

    @Test
    fun testSaveAndOverrideOraFile() {
        onDrawingSurfaceView().perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_save_dialog_spinner)).perform(ViewActions.click())
        onData(AllOf.allOf(`is`(instanceOf<Any>(String::class.java)), `is`<String>("ora")))
            .inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(ViewActions.replaceText("OraOverride"))
        onView(withText(R.string.save_button_text)).perform(ViewActions.click())
        onView(isRoot()).perform(waitFor(100))
        onView(withText(R.string.pocketpaint_no)).perform(ViewActions.click())
        onView(withText(R.string.pocketpaint_ok)).perform(ViewActions.click())
        onDrawingSurfaceView().perform(touchAt(DrawingSurfaceLocationProvider.BOTTOM_MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(ViewActions.click())
        onView(withText(R.string.save_button_text)).perform(ViewActions.click())
        onView(withText(R.string.pocketpaint_overwrite_title)).check(
            ViewAssertions.matches(
                isDisplayed()
            )
        )

        onView(withText(R.string.overwrite_button_text)).perform(ViewActions.click())
        onView(isRoot()).perform(waitFor(500))

        val imageUri = activity.model.savedPictureUri
        assertNotNull(imageUri)
        addUriToDeletionFileList(imageUri)
    }

    @Test
    fun testOraFileWithMultipleLayersSaveAndLoad() {
        onDrawingSurfaceView().perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
            .performClose()
        onDrawingSurfaceView().perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE))
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .checkLayerCount(3)
            .performClose()
        onDrawingSurfaceView().perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE))
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_save_image)).perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_save_dialog_spinner)).perform(ViewActions.click())
        onData(AllOf.allOf(`is`(instanceOf<Any>(String::class.java)), `is`<String>("ora")))
            .inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(ViewActions.replaceText("MoreLayersOraTest"))

        onView(withText(R.string.save_button_text)).perform(ViewActions.click())
        onView(isRoot()).perform(waitFor(1000))

        val fileUri = activity.model.savedPictureUri
        assertNotNull(fileUri)
        addUriToDeletionFileList(fileUri)
        val intent = Intent()
        intent.data = fileUri
        val resultOK = ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultOK)
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_load_image)).perform(ViewActions.click())
        onView(withText(R.string.menu_replace_image)).perform(ViewActions.click())
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .checkLayerCount(3)
    }

    private fun addUriToDeletionFileList(uri: Uri?) {
        uri?.path?.let {
            deletionFileList.add(File(it))
        }
    }
}
