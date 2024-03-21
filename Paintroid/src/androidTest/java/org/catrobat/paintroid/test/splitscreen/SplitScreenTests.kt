package org.catrobat.paintroid.test.splitscreen

import android.accessibilityservice.AccessibilityService
import android.app.UiAutomation
import android.content.Intent
import android.os.Environment
import android.os.SystemClock
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.common.CATROBAT_IMAGE_ENDING
import org.catrobat.paintroid.tools.Workspace
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import java.io.File

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SplitScreenTests {

    private lateinit var uiAutomation: UiAutomation
    private lateinit var workspace: Workspace
    companion object {
        const val IMAGE_NAME = "fileName"
    }

    @get:Rule
    var launchActivityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
        launchActivityRule.scenario.onActivity {
            MoreOptionTestsHelper.setupEnvironment(it)
            ColorPickerTestHelper.setupEnvironment(it)
            ClippingToolTestHelper.setupEnvironment(it)
            ShapeToolTestHelper.setupEnvironment(it)
            TextToolTestHelper.setupEnvironment(it)
            ClipboardToolTestHelper.setupEnvironment(it)
            TransformToolTestHelper.setupEnvironment(it)
            WatercolorToolTestHelper.setupEnvironment(it)
            workspace = it.workspace
            it.startActivity(Intent(Intent.ACTION_DIAL))
        }
    }

    @After
    fun tearDown() {
        val imagesDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val pathToFile = imagesDirectory + File.separator + IMAGE_NAME + "." + CATROBAT_IMAGE_ENDING
        val imageFile = File(pathToFile)
        if (imageFile.exists()) {
            imageFile.delete()
        }
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
    fun testButtonsAddOneLayer() {
        focusPocketPaint()
        LayerTestHelper.testButtonsAddOneLayer()
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

    @Test
    fun testDialogToolInteraction() {
        focusPocketPaint()
        TextToolTestHelper.testDialogToolInteraction()
    }

    @Test
    fun testCutAndPastePixel() {
        focusPocketPaint()
        ClipboardToolTestHelper.testCutAndPastePixel()
    }

    @Test
    fun testMoveCroppingBordersOnEmptyBitmapAndDoCrop() {
        focusPocketPaint()
        TransformToolTestHelper.testMoveCroppingBordersOnEmptyBitmapAndDoCrop()
    }

    @Test
    fun drawOnBitmapThenChangeMaskFilter() {
        focusPocketPaint()
        WatercolorToolTestHelper.drawOnBitmapThenChangeMaskFilter()
    }

    private fun focusPocketPaint() {
        Thread.sleep(500)
        val x: Int = workspace.width / 2
        val y = 2 * workspace.height - workspace.height / 2

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
