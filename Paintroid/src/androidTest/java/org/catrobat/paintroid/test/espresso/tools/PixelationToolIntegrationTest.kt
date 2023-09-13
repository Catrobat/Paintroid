package org.catrobat.paintroid.test.espresso.tools

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.*
import android.graphics.Canvas
import android.graphics.PointF
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.helper.PixelPixelAlgorithm
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

        onView(withId(R.id.pocketpaint_pixel_apply_button))
            .perform(click())
        assertEquals(pixelTool.numPixelWidth, width)
        assertEquals(pixelTool.numPixelHeight, height)
    //    assertEquals(pixelTool.numCollors.toInt(),collor )
    }

    fun loadBitmap(id : Int)
    {

    }

    @Test
    fun meanCalculation(){
        var appContext =   InstrumentationRegistry.getInstrumentation().getTargetContext();
        var drawable  = appContext.getResources().getDrawable(R.drawable.blackandwhite, null);
        val bitmap = Bitmap.createBitmap( pixelTool.boxWidth.toInt(), pixelTool.boxHeight.toInt(), ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, pixelTool.boxWidth.toInt(), pixelTool.boxHeight.toInt())
        drawable.draw(canvas)
        val algoUnderTest : PixelPixelAlgorithm = PixelPixelAlgorithm(bitmap, pixelTool.numCollors.toInt(), pixelTool.numPixelWidth.toInt(), pixelTool.boxHeight.toInt())
        var test =  algoUnderTest.getMean(bitmap)
        val Results = Triple(130,130,130)
        assertEquals(test, Results)



        //   pixelTool.drawingBitmap?.copy(import.bi)
    }
    @Test
    fun meanCalculationPepe()
    {
        var appContext =   InstrumentationRegistry.getInstrumentation().getTargetContext();
        var drawable  = appContext.getResources().getDrawable(R.drawable.pepe, null);
        val bitmap = Bitmap.createBitmap( pixelTool.boxWidth.toInt(), pixelTool.boxHeight.toInt(), ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, pixelTool.boxWidth.toInt(), pixelTool.boxHeight.toInt())
        drawable.draw(canvas)
        val algoUnderTest : PixelPixelAlgorithm = PixelPixelAlgorithm(bitmap, pixelTool.numCollors.toInt(), pixelTool.numPixelWidth.toInt(), pixelTool.boxHeight.toInt())
        var meanCollor =  algoUnderTest.getMean(bitmap)
        val Results = Triple(213,218,225)
       assertEquals(meanCollor, Results)
    }
    @Test
    fun meanCalculationPatric()
    {
        var appContext =   InstrumentationRegistry.getInstrumentation().getTargetContext();
        var drawable  = appContext.getResources().getDrawable(R.drawable.patrik, null);
        val bitmap = Bitmap.createBitmap( pixelTool.boxWidth.toInt(), pixelTool.boxHeight.toInt(), ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, pixelTool.boxWidth.toInt(), pixelTool.boxHeight.toInt())
        drawable.draw(canvas)
        val algoUnderTest : PixelPixelAlgorithm = PixelPixelAlgorithm(bitmap, pixelTool.numCollors.toInt(), pixelTool.numPixelWidth.toInt(), pixelTool.boxHeight.toInt())
        val meanCollor =  algoUnderTest.getMean(bitmap)
        val Results = Triple(230,196,197)
        assertEquals(meanCollor, Results)
    }
    @Test
    fun meanCalculationObama()
    {
        var appContext =   InstrumentationRegistry.getInstrumentation().getTargetContext();
        var drawable  = appContext.getResources().getDrawable(R.drawable.obama, null);
        val bitmap = Bitmap.createBitmap( pixelTool.boxWidth.toInt(), pixelTool.boxHeight.toInt(), ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, pixelTool.boxWidth.toInt(), pixelTool.boxHeight.toInt())
        drawable.draw(canvas)
        val algoUnderTest : PixelPixelAlgorithm = PixelPixelAlgorithm(bitmap, pixelTool.numCollors.toInt(), pixelTool.numPixelWidth.toInt(), pixelTool.boxHeight.toInt())
        val meanCollor =  algoUnderTest.getMean(bitmap)
        val Results = Triple(128,127,132)
        assertEquals(meanCollor, Results)
    }
    @Test
    fun meanCalculationPerson1()
    {
        var appContext =   InstrumentationRegistry.getInstrumentation().getTargetContext();
        var drawable  = appContext.getResources().getDrawable(R.drawable.randomp1, null);
        val bitmap = Bitmap.createBitmap( pixelTool.boxWidth.toInt(), pixelTool.boxHeight.toInt(), ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, pixelTool.boxWidth.toInt(), pixelTool.boxHeight.toInt())
        drawable.draw(canvas)
        val algoUnderTest : PixelPixelAlgorithm = PixelPixelAlgorithm(bitmap, pixelTool.numCollors.toInt(), pixelTool.numPixelWidth.toInt(), pixelTool.boxHeight.toInt())
        val meanCollor =  algoUnderTest.getMean(bitmap)
        val Results = Triple(132,115,95)
        assertEquals(meanCollor, Results)
    }
    @Test
    fun meanCalculationPerson2()
    {
        var appContext =   InstrumentationRegistry.getInstrumentation().getTargetContext();
        var drawable  = appContext.getResources().getDrawable(R.drawable.randomp2, null);
        val bitmap = Bitmap.createBitmap( pixelTool.boxWidth.toInt(), pixelTool.boxHeight.toInt(), ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, pixelTool.boxWidth.toInt(), pixelTool.boxHeight.toInt())
        drawable.draw(canvas)
        val algoUnderTest : PixelPixelAlgorithm = PixelPixelAlgorithm(bitmap, pixelTool.numCollors.toInt(), pixelTool.numPixelWidth.toInt(), pixelTool.boxHeight.toInt())
        val meanCollor =  algoUnderTest.getMean(bitmap)
        val Results = Triple(129,110,100)
        assertEquals(meanCollor, Results)
    }
}
