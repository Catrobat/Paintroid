package org.catrobat.paintroid.test.splitscreen

import android.accessibilityservice.AccessibilityService
import android.app.UiAutomation
import android.content.Intent
import android.os.SystemClock
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.content.ContextCompat.startActivity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.tools.Workspace
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SplitScreenTests {

    private lateinit var uiAutomation: UiAutomation
    private lateinit var mainActivity: MainActivity
    private lateinit var workspace: Workspace

    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        uiAutomation = InstrumentationRegistry.getInstrumentation().getUiAutomation()
        mainActivity = launchActivityRule.activity
        workspace = mainActivity.workspace
        MoreOptionTestsHelper.setupEnvironment(mainActivity)
        ColorPickerTestHelper.setupEnvironment(mainActivity)
        ClippingToolTestHelper.setupEnvironment(mainActivity)
        ShapeToolTestHelper.setupEnvironment(mainActivity)

        startActivity(launchActivityRule.activity, Intent(Intent.ACTION_DIAL), null)
    }

    @Test
    fun a_toggleSplitScreenFirst() {
        uiAutomation.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
    }

    @Test
    fun testWriteAndReadCatrobatImage() {
        focusPocketPaint()
        MoreOptionTestsHelper.testWriteAndReadCatrobatImage()
    }

    @Test
    fun setAntiAliasingNotOnWhenCancelPressed() {
        focusPocketPaint()
        MoreOptionTestsHelper.setAntiAliasingNotOnWhenCancelPressed()
    }

    @Test
    fun testMoreOptionsMenuAboutClosesMoreOptions() {
        focusPocketPaint()
        MoreOptionTestsHelper.testMoreOptionsMenuAboutClosesMoreOptions()
    }

    @Test
    fun testLoadImageDialog() {
        focusPocketPaint()
        MoreOptionTestsHelper.testLoadImageDialog()
    }

    @Test
    fun testAddOneLayer() {
        focusPocketPaint()
        LayerTestHelper.testAddOneLayer()
    }

    @Test
    fun testStandardTabSelected() {
        focusPocketPaint()
        ColorPickerTestHelper.testStandardTabSelected()
    }

    @Test
    fun testTabsAreSelectable() {
        focusPocketPaint()
        ColorPickerTestHelper.testTabsAreSelectable()
    }

    @Test
    fun testColorSelectionChangesNewColorViewColor() {
        focusPocketPaint()
        ColorPickerTestHelper.testColorSelectionChangesNewColorViewColor()
    }

    @Test
    fun testColorHistoryShowsRGBSelectorColors() {
        focusPocketPaint()
        ColorPickerTestHelper.testColorHistoryShowsRGBSelectorColors()
    }

    @Test
    fun testClipOnBlackBitmap() {
        focusPocketPaint()
        ClippingToolTestHelper.testClipOnBlackBitmap()
    }

    @Test
    fun testEraseOnEmptyBitmap() {
        focusPocketPaint()
        EraserToolTestHelper.testEraseOnEmptyBitmap()
    }

    @Test
    fun testEraseSinglePixel() {
        focusPocketPaint()
        EraserToolTestHelper.testEraseSinglePixel()
    }

    @Test
    fun testBitmapIsFilled() {
        focusPocketPaint()
        FillToolTestHelper.testBitmapIsFilled()
    }

    @Test
    fun testOnlyFillInnerArea() {
        focusPocketPaint()
        FillToolTestHelper.testOnlyFillInnerArea()
    }

    @Test
    fun testImportDialogShownOnImportToolSelected() {
        focusPocketPaint()
        ImportToolTestHelper.testImportDialogShownOnImportToolSelected()
    }

    @Test
    fun testVerticalLineColor() {
        focusPocketPaint()
        LineToolTestHelper.testVerticalLineColor()
    }

    @Test
    fun testHorizontalLineColor() {
        focusPocketPaint()
        LineToolTestHelper.testHorizontalLineColor()
    }

    @Test
    fun testDiagonalLineColor() {
        focusPocketPaint()
        LineToolTestHelper.testDiagonalLineColor()
    }

    @Test
    fun testPipetteToolAfterBrushOnSingleLayer() {
        focusPocketPaint()
        PipetteToolTestHelper.testPipetteToolAfterBrushOnSingleLayer()
    }

    @Test
    fun testEraseWithFilledShape() {
        focusPocketPaint()
        ShapeToolEraseTestHelper.testEraseWithFilledShape()
    }

    @Test
    fun testEllipseIsDrawnOnBitmap() {
        focusPocketPaint()
        ShapeToolTestHelper.testEllipseIsDrawnOnBitmap()
    }

    private fun focusPocketPaint() {
        Thread.sleep(500)
        var x: Int = workspace.width / 2
        var y = 2 * workspace.height - workspace.height / 2

        val motionDown = MotionEvent.obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            KeyEvent.ACTION_DOWN,
            x.toFloat(),
            y.toFloat(),
            0
        )
        motionDown.source = InputDevice.SOURCE_TOUCHSCREEN
        uiAutomation.injectInputEvent(motionDown, true)
        motionDown.recycle()
        val motionUp = MotionEvent.obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            KeyEvent.ACTION_UP,
            x.toFloat(),
            y.toFloat(),
            0
        )
        motionUp.source = InputDevice.SOURCE_TOUCHSCREEN
        uiAutomation.injectInputEvent(motionUp, true)
        motionUp.recycle()
    }
}
