package org.catrobat.paintroid.test.espresso.tools

import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape
import org.catrobat.paintroid.tools.implementation.MAXIMUM_BITMAP_SIZE_FACTOR
import org.catrobat.paintroid.ui.Perspective
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.concurrent.thread

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
    }

    @Test
    fun Testtets()
    {
        Thread.sleep(1000)
    }
}