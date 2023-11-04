package org.catrobat.paintroid.test.espresso.tools

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.implementation.MAXIMUM_BITMAP_SIZE_FACTOR
import org.catrobat.paintroid.tools.implementation.PixelTool
import org.catrobat.paintroid.ui.Perspective
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
    private lateinit var pixelTool: PixelTool

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
    fun inputeTest() {
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
