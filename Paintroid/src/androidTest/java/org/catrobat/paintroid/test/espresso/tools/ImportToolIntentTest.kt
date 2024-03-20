/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2024 The Catrobat Team
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

package org.catrobat.paintroid.test.espresso.tools

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.test.espresso.util.EspressoUtils
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.implementation.MAXIMUM_BITMAP_SIZE_FACTOR
import org.catrobat.paintroid.ui.Perspective
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ImportToolIntentTest {

    private val SAMPLE_IMAGE_NAME = "import_tool_test_sample_image.png"

    @get:Rule
    var intentsTestRule = IntentsTestRule(MainActivity::class.java)

    @get:Rule
    var grantPermissionRule: GrantPermissionRule = EspressoUtils.grantPermissionRulesVersionCheck()
    private var toolReference: ToolReference? = null
    private var displayWidth = 0
    private var displayHeight = 0
    private var initialWidth = 0
    private var initialHeight = 0
    private var maxBitmapSize = 0
    private var initialBitmapHeight = 0
    private var initialBitmapWidth = 0
    private var maxWidth = 0
    private lateinit var mainActivity: MainActivity
    private lateinit var activityHelper: MainActivityHelper
    private lateinit var layerModel: LayerContracts.Model
    private lateinit var perspective: Perspective
    private lateinit var workspace: Workspace
    @Before
    fun setUp() {
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.IMPORTPNG)
        mainActivity = intentsTestRule.activity
        toolReference = mainActivity.toolReference
        activityHelper = MainActivityHelper(mainActivity)
        displayWidth = activityHelper.displayWidth
        displayHeight = activityHelper.displayHeight
        maxBitmapSize = displayHeight * displayWidth * MAXIMUM_BITMAP_SIZE_FACTOR.toInt()
        layerModel = mainActivity.layerModel
        val workingBitmap = layerModel.currentLayer!!.bitmap
        initialWidth = workingBitmap.width
        initialHeight = workingBitmap.height
        perspective = mainActivity.perspective
        workspace = mainActivity.workspace
        maxWidth = maxBitmapSize / initialHeight
        initialBitmapWidth = workspace.width
        initialBitmapHeight = workspace.height
        perspective.multiplyScale(.25f)
        saveTestImage()
        val imgGalleryResult = createImageGallerySetResultStub(mainActivity)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(imgGalleryResult)
        onView(withId(R.id.pocketpaint_dialog_import_gallery))
            .perform(click())
    }

    @After
    fun tearDown() {
        deleteTestImage(mainActivity)
    }

    @Test
    fun testEnlargeCanvas() {
        var dragFrom = perspective.getSurfacePointFromCanvasPoint(
            PointF(initialBitmapWidth.toFloat(), initialBitmapHeight.toFloat()))
        var dragTo = perspective.getSurfacePointFromCanvasPoint(
            PointF(maxWidth + 10f, initialHeight.toFloat()))

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.swipe(dragFrom, dragTo))
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
        onView(withText(R.string.dialog_import_image_enlarge_image)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withText(R.string.pocketpaint_enlarge)).perform(click())
        Assert.assertTrue(initialBitmapHeight * initialBitmapWidth < workspace.height * workspace.width)
        initialBitmapHeight = workspace.height
        initialBitmapWidth = workspace.width

        TopBarViewInteraction.onTopBarView().performClickCheckmark()
        onView(withText(R.string.dialog_import_image_enlarge_image)).inRoot(
            not(isDialog())).check(doesNotExist())
        dragFrom = perspective.getSurfacePointFromCanvasPoint(
            PointF(dragTo.x, dragTo.y))
        dragTo = perspective.getSurfacePointFromCanvasPoint(
            PointF(maxWidth * 2f, initialHeight.toFloat()))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.swipe(dragFrom, dragTo))
        TopBarViewInteraction.onTopBarView().performClickCheckmark()

        onView(withText(R.string.dialog_import_image_enlarge_image)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withText(R.string.pocketpaint_enlarge)).perform(click())
        onView(withText(R.string.dialog_import_image_canvas_too_large)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withText(R.string.pocketpaint_truncate)).perform(click())
        onView(withText(R.string.dialog_import_image_canvas_too_large)).inRoot(
            not(isDialog())).check(doesNotExist())
        Assert.assertEquals(
            initialBitmapHeight * initialBitmapWidth, workspace.height * workspace.width)
    }

    @Test
    fun testEnlargeWhenSwitchingTool() {
        val dragFrom = perspective.getSurfacePointFromCanvasPoint(
            PointF(initialBitmapWidth.toFloat(), initialBitmapHeight.toFloat()))
        val dragTo = perspective.getSurfacePointFromCanvasPoint(
            PointF(maxWidth + 10f, initialHeight.toFloat()))

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.swipe(dragFrom, dragTo))
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)

        onView(withText(R.string.dialog_import_image_enlarge_image)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withText(R.string.pocketpaint_enlarge)).perform(click())
        Assert.assertTrue(initialBitmapHeight * initialBitmapWidth < workspace.height * workspace.width)
    }

    private fun saveTestImage() {
        val bm = BitmapFactory.decodeResource(intentsTestRule.activity.resources, R.drawable.pocketpaint_logo)
        val dir = intentsTestRule.activity.externalCacheDir
        val file = File(dir?.path, SAMPLE_IMAGE_NAME)
        val outStream: FileOutputStream?
        try {
            outStream = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            with(outStream) {
                flush()
                close()
            }
        } catch (e: FileNotFoundException) {
            throw AssertionError("Could not save temp file", e)
        } catch (e: IOException) {
            throw AssertionError("Could not save temp file", e)
        }
    }

    private fun createImageGallerySetResultStub(activity: Activity): Instrumentation.ActivityResult {
        val dir = activity.externalCacheDir
        val file = File(dir?.path, SAMPLE_IMAGE_NAME)
        val imageUri = Uri.fromFile(file)
        val resultIntent = Intent()
        resultIntent.data = imageUri
        resultIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        return Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent)
    }

    private fun deleteTestImage(activity: Activity) {
        val dir = activity.externalCacheDir
        val file = File(dir?.path, SAMPLE_IMAGE_NAME)
        file.delete()
    }
}
