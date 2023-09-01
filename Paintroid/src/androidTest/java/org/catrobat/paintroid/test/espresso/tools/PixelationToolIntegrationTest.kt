package org.catrobat.paintroid.test.espresso.tools

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.widget.SeekBar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.waitForToast
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.Companion.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.Companion.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.Companion.onTransformToolOptionsView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.implementation.*
import org.catrobat.paintroid.ui.Perspective
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.lessThan
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class PixelationToolIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(
        MainActivity::class.java
    )

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var displayWidth = 0
    private var displayHeight = 0
    private var initialWidth = 0
    private var initialHeight = 0
    private var maxBitmapSize = 0
    private lateinit var perspective: Perspective
    private lateinit var layerModel: LayerContracts.Model
    private lateinit var toolReference: ToolReference
    private lateinit var mainActivity: MainActivity
    private lateinit var activityHelper: MainActivityHelper
    private lateinit var pixelTool : PixelTool

    private var toolSelectionBoxHeight: Float
        get() {
            return (toolReference.tool as BaseToolWithRectangleShape).boxHeight
        }
        private set(height) {
            (toolReference.tool as BaseToolWithRectangleShape).boxHeight = height
        }

    private var toolSelectionBoxWidth: Float
        get() {
            return (toolReference.tool as BaseToolWithRectangleShape).boxWidth
        }
        private set(width) {
            (toolReference.tool as BaseToolWithRectangleShape).boxWidth = width
        }
    @Before
    fun setUp() {
        mainActivity = launchActivityRule.activity
        activityHelper = MainActivityHelper(mainActivity)
        perspective = mainActivity.perspective
        layerModel = mainActivity.layerModel
        toolReference = mainActivity.toolReference
        displayWidth = activityHelper.displayWidth
        displayHeight = activityHelper.displayHeight

        maxBitmapSize = displayHeight * displayWidth * MAXIMUM_BITMAP_SIZE_FACTOR.toInt()
        val workingBitmap = layerModel.currentLayer!!.bitmap
        initialWidth = workingBitmap.width
        initialHeight = workingBitmap.height
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIXEL)
        pixelTool = toolReference.tool as PixelTool
    }

    @Test
    fun inputeTest()
    {

        var width = 80.0f
        var height = 49.0f
        onView(withId(R.id.pocketpaint_pixel_width_value))
            .check(matches(isDisplayed()))
            .perform(replaceText(width.toString()), closeSoftKeyboard())
            .check(matches(withText(width.toString())))

        // Check and set height value
        onView(withId(R.id.pocketpaint_pixel_height_value))
            .check(matches(isDisplayed()))
            .perform(replaceText(height.toString()), closeSoftKeyboard())
            .check(matches(withText(height.toString())))

        // Set SeekBar position for color (e.g., halfway)
       /* onView(withId(R.id.pocketpaint_pixel_color_seekbar))
            .perform(swipeRight()) // Swipe to change SeekBar's position.

        onView(withId(R.id.pocketpaint_transform_pixel_color_text))
            .check(matches(isDisplayed()))
            .perform(replaceText(collor.toString()), closeSoftKeyboard())
            .check(matches(withText(collor.toString())))*/

        // Optionally click the apply button
        onView(withId(R.id.pocketpaint_pixel_apply_button))
            .perform(click())
        assertEquals(pixelTool.numPixelWidth, width)
        assertEquals(pixelTool.numPixelHeight, height)
    //    assertEquals(pixelTool.numCollors.toInt(),collor )
    }
}