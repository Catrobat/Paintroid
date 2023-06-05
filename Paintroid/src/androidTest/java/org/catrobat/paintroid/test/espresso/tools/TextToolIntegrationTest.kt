/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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

@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso.tools

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.FontType
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.implementation.BOX_OFFSET
import org.catrobat.paintroid.tools.implementation.MARGIN_TOP
import org.catrobat.paintroid.tools.implementation.TEXT_SIZE_MAGNIFICATION_FACTOR
import org.catrobat.paintroid.tools.implementation.TextTool
import org.catrobat.paintroid.ui.tools.FontListAdapter
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.roundToInt
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.CoreMatchers.not

@RunWith(AndroidJUnit4::class)
@Suppress("LargeClass")
class TextToolIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var activityHelper: MainActivityHelper? = null
    private var textTool: TextTool? = null
    private var textToolAfterZoom: TextTool? = null
    private var textEditText: EditText? = null
    private var fontList: RecyclerView? = null
    private var underlinedToggleButton: MaterialButton? = null
    private var italicToggleButton: MaterialButton? = null
    private var boldToggleButton: MaterialButton? = null
    private var textSize: EditText? = null
    private var layerModel: LayerContracts.Model? = null
    private var activity: MainActivity? = null
    private var toolReference: ToolReference? = null

    private val surfaceBitmapHeight = layerModel?.height
    private val pixelsDrawingSurface = surfaceBitmapHeight?.let { IntArray(it) }
    private val canvasPoint = centerBox()
    private var pixelAmountBefore = pixelsDrawingSurface?.let { countPixelsWithColor(it, Color.BLACK) }

    @Before
    fun setUp() {
        activity = launchActivityRule.activity
        activityHelper = MainActivityHelper(activity)
        layerModel = activity?.layerModel
        toolReference = activity?.toolReference

        onToolBarView().performSelectTool(ToolType.TEXT)

        textTool = toolReference?.tool as TextTool
        textEditText = activity?.findViewById(R.id.pocketpaint_text_tool_dialog_input_text)
        fontList = activity?.findViewById(R.id.pocketpaint_text_tool_dialog_list_font)
        underlinedToggleButton =
            activity?.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_underlined)
        italicToggleButton = activity?.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_italic)
        boldToggleButton = activity?.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_bold)
        textSize = activity?.findViewById(R.id.pocketpaint_font_size_text)
        textTool?.resetBoxPosition()
    }

    @Test
    fun testTextToolStillEditableAfterClickingInsideTheCanvasTextToolOptionsVisible() {
        selectFormatting(FormattingOptions.ITALIC)
        selectFormatting(FormattingOptions.BOLD)
        selectFormatting(FormattingOptions.UNDERLINE)
        enterTestText()
        onView(withId(R.id.pocketpaint_text_tool_dialog_input_text)).perform(click())
        onView(withId(R.id.pocketpaint_text_tool_dialog_input_text)).perform(
            ViewActions.replaceText(
                TEST_TEXT_ADVANCED
            )
        )
        italicToggleButton?.let { Assert.assertTrue(it.isChecked) }
        boldToggleButton?.let { Assert.assertTrue(it.isChecked) }
        underlinedToggleButton?.let { Assert.assertTrue(it.isChecked) }
        Assert.assertEquals(TEST_TEXT_ADVANCED, textEditText?.text?.toString())
        onView(withId(R.id.pocketpaint_text_tool_dialog_input_text)).check(matches(isDisplayed()))
        onView(withId(R.id.pocketpaint_text_tool_dialog_list_font)).check(matches(isDisplayed()))
        onView(withId(R.id.pocketpaint_text_tool_dialog_toggle_underlined)).check(matches(isDisplayed()))
        onView(withId(R.id.pocketpaint_text_tool_dialog_toggle_italic)).check(matches(isDisplayed()))
        onView(withId(R.id.pocketpaint_text_tool_dialog_toggle_bold)).check(matches(isDisplayed()))
        onView(withId(R.id.pocketpaint_font_size_text)).check(matches(isDisplayed()))
    }

    @Test
    fun testTextToolNotEditableAfterClickingOutsideTheCanvasTextToolOptionsHidden() {
        selectFormatting(FormattingOptions.ITALIC)
        selectFormatting(FormattingOptions.BOLD)
        selectFormatting(FormattingOptions.UNDERLINE)
        enterTestText()
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))

        italicToggleButton?.let { Assert.assertTrue(it.isChecked) }
        boldToggleButton?.let { Assert.assertTrue(it.isChecked) }
        underlinedToggleButton?.let { Assert.assertTrue(it.isChecked) }
        Assert.assertEquals(TEST_TEXT, textEditText?.text?.toString())
        onView(withId(R.id.pocketpaint_text_tool_dialog_input_text))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.pocketpaint_text_tool_dialog_toggle_underlined))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.pocketpaint_text_tool_dialog_toggle_italic))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.pocketpaint_text_tool_dialog_toggle_bold))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.pocketpaint_font_size_text))
            .check(matches(not(isDisplayed())))
    }

    @Ignore("Fix bug in own ticket , focus is not correctly implemented in google play either")
    @Test
    fun testDialogKeyboardTextBoxAppearanceOnStartup() {
        onView(withId(R.id.pocketpaint_text_tool_dialog_input_text))
            .check(ViewAssertions.matches(ViewMatchers.hasFocus()))
        checkTextBoxDimensionsAndDefaultPosition()
    }

    @Suppress("LongMethod")
    @Test
    fun testDialogDefaultValues() {
        onView(withId(R.id.pocketpaint_text_tool_dialog_input_text))
            .check(ViewAssertions.matches(withHint(R.string.text_tool_dialog_input_hint)))
            .check(ViewAssertions.matches(ViewMatchers.withText(textTool?.text)))
        onToolBarView().performSelectTool(ToolType.TEXT)
        onView(withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .check(
                ViewAssertions.matches(
                    UiMatcher.atPosition(
                        0,
                        ViewMatchers.hasDescendant(
                            ViewMatchers.isChecked()
                        )
                    )
                )
            )
        onView(withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .check(
                ViewAssertions.matches(
                    UiMatcher.atPosition(
                        1,
                        ViewMatchers.hasDescendant(
                            ViewMatchers.isNotChecked()
                        )
                    )
                )
            )
        onView(withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(
                ViewAssertions.matches(
                    UiMatcher.atPosition(
                        2,
                        ViewMatchers.hasDescendant(
                            ViewMatchers.isNotChecked()
                        )
                    )
                )
            )
        onView(withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(3))
            .check(
                ViewAssertions.matches(
                    UiMatcher.atPosition(
                        3,
                        ViewMatchers.hasDescendant(
                            ViewMatchers.isNotChecked()
                        )
                    )
                )
            )
        onView(withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(4))
            .check(
                ViewAssertions.matches(
                    UiMatcher.atPosition(
                        4,
                        ViewMatchers.hasDescendant(
                            ViewMatchers.isNotChecked()
                        )
                    )
                )
            )
        textTool?.let { Assert.assertFalse(it.underlined) }
        textTool?.let { Assert.assertFalse(it.italic) }
        textTool?.let { Assert.assertFalse(it.bold) }
    }

    @Test
    fun testDialogToolInteraction() {
        enterTestText()
        Assert.assertEquals(TEST_TEXT, textTool?.text)
        selectFontType(FontType.SERIF)
        Assert.assertEquals(FontType.SERIF, textTool?.font)
        Assert.assertEquals(FontType.SERIF, (fontList?.adapter as FontListAdapter?)!!.getSelectedItem())
        selectFormatting(FormattingOptions.UNDERLINE)
        textTool?.let { Assert.assertTrue(it.underlined) }
        underlinedToggleButton?.let { Assert.assertTrue(it.isChecked) }
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.UNDERLINE),
            underlinedToggleButton?.text.toString()
        )
        selectFormatting(FormattingOptions.UNDERLINE)
        textTool?.let { Assert.assertFalse(it.underlined) }
        underlinedToggleButton?.let { Assert.assertFalse(it.isChecked) }
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.UNDERLINE),
            underlinedToggleButton?.text.toString()
        )
        selectFormatting(FormattingOptions.ITALIC)
        Assert.assertTrue(toolMemberItalic)
        italicToggleButton?.let { Assert.assertTrue(it.isChecked) }
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.ITALIC),
            italicToggleButton?.text.toString()
        )
        selectFormatting(FormattingOptions.ITALIC)
        Assert.assertFalse(toolMemberItalic)
        italicToggleButton?.let { Assert.assertFalse(it.isChecked) }
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.ITALIC),
            italicToggleButton?.text.toString()
        )
        selectFormatting(FormattingOptions.BOLD)
        Assert.assertTrue(toolMemberBold)
        boldToggleButton?.let { Assert.assertTrue(it.isChecked) }
        Assert.assertEquals(getFormattingOptionAsString(FormattingOptions.BOLD), boldToggleButton?.text.toString())
        selectFormatting(FormattingOptions.BOLD)
        Assert.assertFalse(toolMemberBold)
        boldToggleButton?.let { Assert.assertFalse(it.isChecked) }
        Assert.assertEquals(getFormattingOptionAsString(FormattingOptions.BOLD), boldToggleButton?.text.toString())
    }

    @Test
    fun testDialogAndTextBoxAfterReopenDialog() {
        enterTestText()
        selectFontType(FontType.SANS_SERIF)
        selectFormatting(FormattingOptions.UNDERLINE)
        selectFormatting(FormattingOptions.ITALIC)
        selectFormatting(FormattingOptions.BOLD)
        onToolBarView().performCloseToolOptionsView()

        val oldBoxWidth = toolMemberBoxWidth
        val oldBoxHeight = toolMemberBoxHeight
        val boxPosition = toolMemberBoxPosition
        val newBoxPosition = boxPosition?.y?.plus(200)
            ?.let { PointF(boxPosition.x.plus(100), it) }

        toolMemberBoxPosition = newBoxPosition

        onToolBarView().performOpenToolOptionsView()

        Assert.assertEquals(TEST_TEXT, textEditText?.text.toString())
        Assert.assertEquals(FontType.SANS_SERIF, (fontList?.adapter as FontListAdapter?)!!.getSelectedItem())
        underlinedToggleButton?.let { Assert.assertTrue(it.isChecked) }
        italicToggleButton?.let { Assert.assertTrue(it.isChecked) }
        boldToggleButton?.let { Assert.assertTrue(it.isChecked) }
        Assert.assertTrue(oldBoxWidth == toolMemberBoxWidth && oldBoxHeight == toolMemberBoxHeight)
    }

    @Test
    fun testStateRestoredAfterOrientationChange() {
        enterTestText()
        selectFontType(FontType.SANS_SERIF)
        selectFormatting(FormattingOptions.UNDERLINE)
        selectFormatting(FormattingOptions.ITALIC)
        selectFormatting(FormattingOptions.BOLD)

        val toolMemberBoxPosition = toolMemberBoxPosition
        val expectedPosition = toolMemberBoxPosition?.y?.let { PointF(toolMemberBoxPosition.x, it) }

        textTool = toolReference?.tool as TextTool

        val oldBoxWidth = toolMemberBoxWidth
        val oldBoxHeight = toolMemberBoxHeight

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        Assert.assertEquals(TEST_TEXT, textEditText?.text.toString())
        Assert.assertEquals(FontType.SANS_SERIF, (fontList?.adapter as FontListAdapter?)?.getSelectedItem())
        underlinedToggleButton?.let { Assert.assertTrue(it.isChecked) }
        italicToggleButton?.let { Assert.assertTrue(it.isChecked) }
        boldToggleButton?.let { Assert.assertTrue(it.isChecked) }
        Assert.assertEquals(expectedPosition, toolMemberBoxPosition)
        Assert.assertEquals(oldBoxWidth.toDouble(), toolMemberBoxWidth.toDouble(), EQUALS_DELTA)
        Assert.assertEquals(oldBoxHeight.toDouble(), toolMemberBoxHeight.toDouble(), EQUALS_DELTA)
    }

    @Test
    fun testCheckBoxSizeAndContentAfterFormatting() {
        enterTestText()
        underlinedToggleButton?.let { Assert.assertFalse(it.isChecked) }
        boldToggleButton?.let { Assert.assertFalse(it.isChecked) }
        italicToggleButton?.let { Assert.assertFalse(it.isChecked) }

        testFontAndPixels()
    }

    // created in order to reduce the complexity of the testCheckBoxSizeAndContentAfterFormatting test
    private fun testFontAndPixels() {
        val fonts = ArrayList<FontType>()
        fonts.add(FontType.SERIF)
        fonts.add(FontType.SANS_SERIF)
        fonts.add(FontType.MONOSPACE)
        fonts.add(FontType.DUBAI)
        fonts.add(FontType.STC)
        checkTextBoxDimensionsAndDefaultPosition()
        for (font in fonts) {
            layerModel?.currentLayer?.bitmap?.eraseColor(Color.TRANSPARENT)
            val boxWidth = toolMemberBoxWidth
            val boxHeight = toolMemberBoxHeight

            selectFontType(font)
            Assert.assertTrue(boxWidth == toolMemberBoxWidth && boxHeight == toolMemberBoxHeight)

            layerModel?.currentLayer?.bitmap?.eraseColor(Color.TRANSPARENT)
            onTopBarView().performClickCheckmark()

            testVariablesInIfStatementsWithSurfacseBitmap()
            testSurfacseBitmapAndPixelAmountAfter()

            if (surfaceBitmapHeight != null && canvasPoint != null) {
                layerModel?.currentLayer?.bitmap?.getPixels(
                    pixelsDrawingSurface, 0, 1,
                    canvasPoint.x.toInt(), 0, 1, surfaceBitmapHeight
                )
            }

            testPixelAmountBeforeAndPixelDrawingSurface()
            selectFormatting(FormattingOptions.UNDERLINE)
            underlinedToggleButton?.let { Assert.assertFalse(it.isChecked) }
            selectFormatting(FormattingOptions.ITALIC)
            italicToggleButton?.let { Assert.assertFalse(it.isChecked) }
            selectFormatting(FormattingOptions.BOLD)
            boldToggleButton?.let { Assert.assertFalse(it.isChecked) }
        }
    }

    private fun testSurfacseBitmapAndPixelAmountAfter() {
        if (surfaceBitmapHeight != null && canvasPoint != null) {
            layerModel?.currentLayer?.bitmap?.getPixels(
                pixelsDrawingSurface, 0, 1,
                canvasPoint.x.toInt(), 0, 1, surfaceBitmapHeight
            )
        }
        var pixelAmountAfter = pixelsDrawingSurface?.let { countPixelsWithColor(it, Color.BLACK) }
        if (pixelAmountAfter != null) {
            assert(pixelAmountAfter > 0)
        }

        if (pixelAmountBefore != null) {
            Assert.assertTrue(pixelAmountBefore!! < pixelAmountAfter!!)
        }
        selectFormatting(FormattingOptions.ITALIC)
        italicToggleButton?.let { Assert.assertTrue(it.isChecked) }
        Assert.assertTrue(toolMemberItalic)

        layerModel?.currentLayer?.bitmap?.eraseColor(Color.TRANSPARENT)
        onTopBarView().performClickCheckmark()
        pixelAmountAfter = pixelsDrawingSurface?.let { countPixelsWithColor(it, Color.BLACK) }
        if (pixelAmountAfter != null) {
            assert(pixelAmountAfter > 0)
        }

        if (pixelAmountAfter != null) {
            Assert.assertTrue(pixelAmountAfter > pixelAmountBefore!!)
        }
    }

    private fun testPixelAmountBeforeAndPixelDrawingSurface() {
        pixelAmountBefore = pixelsDrawingSurface?.let { countPixelsWithColor(it, Color.BLACK) }
        if (pixelAmountBefore != null) {
            assert(pixelAmountBefore!! > 0)
        }
        selectFormatting(FormattingOptions.BOLD)
        boldToggleButton?.let { Assert.assertTrue(it.isChecked) }

        layerModel?.currentLayer?.bitmap?.eraseColor(Color.TRANSPARENT)
        onTopBarView().performClickCheckmark()
        if (surfaceBitmapHeight != null && canvasPoint != null) {
            layerModel?.currentLayer?.bitmap?.getPixels(
                pixelsDrawingSurface, 0, 1,
                canvasPoint.x.toInt(), 0, 1, surfaceBitmapHeight
            )
        }
    }

    private fun testVariablesInIfStatementsWithSurfacseBitmap() {
        if (surfaceBitmapHeight != null && canvasPoint != null) {
            layerModel?.currentLayer?.bitmap?.getPixels(
                pixelsDrawingSurface, 0, 1,
                canvasPoint.x.toInt(), 0, 1, surfaceBitmapHeight
            )
        }

        if (pixelAmountBefore != null) {
            assert(pixelAmountBefore!! > 0)
        }

        selectFormatting(FormattingOptions.UNDERLINE)

        underlinedToggleButton?.let { Assert.assertTrue(it.isChecked) }
        layerModel?.currentLayer?.bitmap?.eraseColor(Color.TRANSPARENT)
        onTopBarView().performClickCheckmark()
    }

    @Test
    fun testInputTextAndFormatForTextSize50() {
        enterTestText()
        val boxWidth = toolMemberBoxWidth
        val boxHeight = toolMemberBoxHeight
        onView(withId(R.id.pocketpaint_font_size_text))
            .perform(ViewActions.replaceText("50"))
        checkTextBoxDimensions()
        Assert.assertTrue(
            "Text box width should be larger with bigger text size",
            toolMemberBoxWidth > boxWidth
        )
        Assert.assertTrue(
            "Text box height should be larger with bigger text size",
            toolMemberBoxHeight > boxHeight
        )
    }

    @Test
    fun testInputTextAndFormatForTextSize100() {
        enterTestText()
        val boxWidth = toolMemberBoxWidth
        val boxHeight = toolMemberBoxHeight
        onView(withId(R.id.pocketpaint_font_size_text))
            .perform(ViewActions.replaceText("100"))
        checkTextBoxDimensions()
        Assert.assertTrue(
            "Text box width should be larger with bigger text size",
            toolMemberBoxWidth > boxWidth
        )
        Assert.assertTrue(
            "Text box height should be larger with bigger text size",
            toolMemberBoxHeight > boxHeight
        )
    }

    @Test
    fun testInputTextAndFormatForTextSize300() {
        enterTestText()
        val boxWidth = toolMemberBoxWidth
        val boxHeight = toolMemberBoxHeight
        onView(withId(R.id.pocketpaint_font_size_text))
            .perform(ViewActions.replaceText("300"))
        checkTextBoxDimensions()
        Assert.assertTrue(
            "Text box width should be larger with bigger text size",
            toolMemberBoxWidth > boxWidth
        )
        Assert.assertTrue(
            "Text box height should be larger with bigger text size",
            toolMemberBoxHeight > boxHeight
        )
    }

    @Test
    fun testCommandUndoAndRedo() {
        enterMultilineTestText()
        onToolBarView().performCloseToolOptionsView()

        val canvasPoint = centerBox()
        onTopBarView().performClickCheckmark()
        val surfaceBitmapWidth = layerModel?.width
        val pixelsDrawingSurface = surfaceBitmapWidth?.let { IntArray(it) }

        if (surfaceBitmapWidth != null && canvasPoint != null) {
            layerModel?.currentLayer?.bitmap?.getPixels(
                pixelsDrawingSurface, 0, surfaceBitmapWidth, 0,
                canvasPoint.y.toInt(), surfaceBitmapWidth, 1
            )
        }

        var pixelAmount = pixelsDrawingSurface?.let { countPixelsWithColor(it, Color.BLACK) }
        if (pixelAmount != null) {
            assert(pixelAmount > 0)
        }
        onTopBarView().performUndo()
        if (surfaceBitmapWidth != null && canvasPoint != null) {
            layerModel?.currentLayer?.bitmap?.getPixels(
                pixelsDrawingSurface, 0, surfaceBitmapWidth, 0,
                canvasPoint.y.toInt(), surfaceBitmapWidth, 1
            )
        }

        Assert.assertEquals(
            0,
            pixelsDrawingSurface?.let { countPixelsWithColor(it, Color.BLACK) }
        )
        onTopBarView().performRedo()

        if (surfaceBitmapWidth != null && canvasPoint != null) {
            layerModel?.currentLayer?.bitmap?.getPixels(
                pixelsDrawingSurface, 0, surfaceBitmapWidth, 0,
                canvasPoint.y.toInt(), surfaceBitmapWidth, 1
            )
        }

        pixelAmount = pixelsDrawingSurface?.let { countPixelsWithColor(it, Color.BLACK) }
        if (pixelAmount != null) {
            assert(pixelAmount > 0)
        }
    }

    @Test
    fun testChangeTextColor() {
        enterTestText()
        onToolBarView().performCloseToolOptionsView()

        val canvasPoint = centerBox()
        onToolProperties().setColor(Color.WHITE)
        val paint = textTool?.textPaint
        var selectedColor = paint?.color

        if (selectedColor != null) {
            Assert.assertEquals(Color.WHITE.toLong(), selectedColor.toLong())
        }
        onTopBarView().performClickCheckmark()

        val surfaceBitmapWidth = layerModel?.width
        val pixelsDrawingSurface = surfaceBitmapWidth?.let { IntArray(it) }

        if (surfaceBitmapWidth != null && canvasPoint != null) {
            layerModel?.currentLayer?.bitmap?.getPixels(
                pixelsDrawingSurface, 0, surfaceBitmapWidth, 0,
                canvasPoint.y.toInt(), surfaceBitmapWidth, 1
            )
        }

        var pixelAmount = pixelsDrawingSurface?.let { countPixelsWithColor(it, Color.WHITE) }
        if (pixelAmount != null) {
            assert(pixelAmount > 0)
        }
        onToolProperties().setColor(Color.BLACK)
        if (paint != null) {
            selectedColor = paint.color
        }

        Assert.assertEquals(Color.BLACK.toLong(), selectedColor?.toLong())
        onTopBarView().performClickCheckmark()
        if (surfaceBitmapWidth != null && canvasPoint != null) {
            layerModel?.currentLayer?.bitmap?.getPixels(
                pixelsDrawingSurface, 0, surfaceBitmapWidth, 0,
                canvasPoint.y.toInt(), surfaceBitmapWidth, 1
            )
        }

        pixelAmount = pixelsDrawingSurface?.let { countPixelsWithColor(it, Color.BLACK) }
        if (pixelAmount != null) {
            assert(pixelAmount > 0)
        }
    }

    @Test
    fun testChangeToolFromEraser() {
        val color = textTool?.textPaint?.color
        onToolBarView()
            .performSelectTool(ToolType.ERASER)
            .performSelectTool(ToolType.TEXT)
        val newColor = textTool?.textPaint?.color

        Assert.assertEquals(color?.toLong(), Color.BLACK.toLong())
        if (color != null) {
            Assert.assertEquals(color.toLong(), newColor?.toLong())
        }
    }

    @Test
    fun testMultiLineText() {
        checkTextBoxDimensionsAndDefaultPosition()
        enterMultilineTestText()
        onToolBarView()
            .performCloseToolOptionsView()
        val expectedTextSplitUp = arrayOf("testing", "multiline", "text", "", "123")
        val actualTextSplitUp = toolMemberMultilineText
        Assert.assertArrayEquals(expectedTextSplitUp, actualTextSplitUp)
    }

    @Test
    fun testTextToolAppliedWhenSelectingOtherTool() {
        enterTestText()
        onToolBarView().performSelectTool(ToolType.BRUSH)

        val surfaceBitmapWidth = layerModel?.width
        val pixelsDrawingSurface = surfaceBitmapWidth?.let { IntArray(it) }

        if (surfaceBitmapWidth != null) {
            textTool?.toolPosition?.y?.let {
                layerModel?.currentLayer?.bitmap?.getPixels(
                    pixelsDrawingSurface, 0, surfaceBitmapWidth, 0,
                    it.toInt(), surfaceBitmapWidth, 1
                )
            }
        }

        val numberOfBlackPixels = pixelsDrawingSurface?.let { countPixelsWithColor(it, Color.BLACK) }
        if (numberOfBlackPixels != null) {
            Assert.assertTrue(numberOfBlackPixels > 0)
        }
    }

    @Test
    fun testTextToolNotAppliedWhenPressingBack() {
        enterTestText()
        onToolBarView().performCloseToolOptionsView()
        pressBack()
        val surfaceBitmapWidth = layerModel?.width
        val pixelsDrawingSurface = surfaceBitmapWidth?.let { IntArray(it) }

        if (surfaceBitmapWidth != null) {
            textTool?.toolPosition?.y?.let {
                layerModel?.currentLayer?.bitmap?.getPixels(
                    pixelsDrawingSurface, 0, surfaceBitmapWidth, 0,
                    it.toInt(), surfaceBitmapWidth, 1
                )
            }
        }

        val numberOfBlackPixels = pixelsDrawingSurface?.let { countPixelsWithColor(it, Color.BLACK) }
        if (numberOfBlackPixels != null) {
            Assert.assertEquals(0, numberOfBlackPixels.toLong())
        }
    }

    @Test
    fun testTextToolDoesNotResetPerspectiveScale() {
        onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        val scale = 2.0f
        activity?.perspective?.scale = scale
        activity?.perspective?.surfaceTranslationY = 200f
        activity?.perspective?.surfaceTranslationX = 50f
        activity?.refreshDrawingSurface()
        onToolBarView()
            .performSelectTool(ToolType.TEXT)
        runBlocking {
            delay(1500)
        }
        enterTestText()
        activity?.perspective?.let { Assert.assertEquals(scale, it.scale, 0.0001f) }
    }

    @Test
    fun testTextToolBoxIsPlacedCorrectlyWhenZoomedIn() {
        onToolBarView().performSelectTool(ToolType.TEXT)
        enterTestText()

        val initialPosition = toolMemberBoxPosition
        onToolBarView().performSelectTool(ToolType.BRUSH)
        val scale = 2.0f

        activity?.perspective?.scale = scale
        activity?.perspective?.surfaceTranslationY = 200f
        activity?.perspective?.surfaceTranslationX = 50f
        activity?.refreshDrawingSurface()

        onToolBarView().performSelectTool(ToolType.TEXT)
        runBlocking {
            delay(1500)
        }
        enterTestText()

        textToolAfterZoom = activity?.toolReference?.tool as TextTool

        val positionAfterZoom = toolMemberBoxPosition
        Assert.assertEquals(scale, activity!!.perspective.scale, 0.0001f)
        onTopBarView().performClickCheckmark()
        Assert.assertNotEquals(initialPosition, positionAfterZoom)
    }

    @Test
    fun testSettingFontAndFontStyleDoesNotResetBox() {
        onToolBarView().performSelectTool(ToolType.TEXT)
        enterTestText()

        val fonts = ArrayList<FontType>()
        fonts.add(FontType.SANS_SERIF)
        fonts.add(FontType.MONOSPACE)
        fonts.add(FontType.DUBAI)
        fonts.add(FontType.STC)
        for (font in fonts) {
            if (italicToggleButton!!.isChecked) {
                selectFormatting(FormattingOptions.ITALIC)
                selectFormatting(FormattingOptions.BOLD)
                selectFormatting(FormattingOptions.UNDERLINE)
            }
            val boxWidth = toolMemberBoxWidth
            val boxHeight = toolMemberBoxHeight
            toolMemberBoxWidth = boxWidth + 100
            toolMemberBoxHeight = boxHeight + 100
            selectFontType(font)
            selectFormatting(FormattingOptions.ITALIC)
            selectFormatting(FormattingOptions.BOLD)
            selectFormatting(FormattingOptions.UNDERLINE)
            Assert.assertTrue(boxWidth < toolMemberBoxWidth && boxHeight < toolMemberBoxHeight)
        }
    }

    private fun centerBox(): PointF? {
        val screenPoint =
            activityHelper?.displayWidth?.div(2.0f)
                ?.let { PointF(it, activityHelper!!.displayHeight / 2.0f) }
        val canvasPoint = screenPoint?.let { PointF(screenPoint.x, it.y) }
        if (canvasPoint != null) {
            canvasPoint.x = canvasPoint.x.roundToInt().toFloat()
        }
        if (canvasPoint != null) {
            canvasPoint.y = canvasPoint.y.roundToInt().toFloat()
        }
        toolMemberBoxPosition = canvasPoint
        return canvasPoint
    }

    private fun checkTextBoxDimensions() {
        val actualBoxWidth = toolMemberBoxWidth
        val actualBoxHeight = toolMemberBoxHeight
        val italic = italicToggleButton?.isChecked
        val font = (fontList?.adapter as FontListAdapter?)?.getSelectedItem()
        val stringTextSize = textSize?.text.toString()
        val textSize: Float = stringTextSize.toFloat() * TEXT_SIZE_MAGNIFICATION_FACTOR
        val textPaint = Paint()

        textPaint.isAntiAlias = true
        textPaint.textSize = textSize

        val style = if (italic == true) Typeface.ITALIC else Typeface.NORMAL
        when (font) {
            FontType.SANS_SERIF -> textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, style)
            FontType.SERIF -> textPaint.typeface = Typeface.create(Typeface.SERIF, style)
            FontType.STC ->
                textPaint.typeface =
                    ResourcesCompat.getFont(launchActivityRule.activity, R.font.stc_regular)
            FontType.DUBAI ->
                textPaint.typeface = ResourcesCompat.getFont(launchActivityRule.activity, R.font.dubai)
            else -> textPaint.typeface = Typeface.create(Typeface.MONOSPACE, style)
        }
        val textDescent = textPaint.descent()
        val textAscent = textPaint.ascent()
        val multilineText = toolMemberMultilineText
        var maxTextWidth = 0f
        for (str in multilineText) {
            val textWidth = textPaint.measureText(str)
            if (textWidth > maxTextWidth) { maxTextWidth = textWidth }
        }
        val expectedBoxWidth: Float = maxTextWidth + 2 * BOX_OFFSET
        val textHeight = textDescent - textAscent
        val expectedBoxHeight: Float = textHeight * multilineText.size + 2 * BOX_OFFSET

        Assert.assertEquals(expectedBoxWidth.toDouble(), actualBoxWidth.toDouble(), EQUALS_DELTA)
        Assert.assertEquals(expectedBoxHeight.toDouble(), actualBoxHeight.toDouble(), EQUALS_DELTA)
    }

    private fun checkTextBoxDefaultPosition() {
        val marginTop: Float = MARGIN_TOP
        val actualBoxPosition = toolMemberBoxPosition
        val boxHeight = toolMemberBoxHeight
        val expectedBoxPositionX = layerModel?.width?.div(2.0f)
        val expectedBoxPositionY = boxHeight / 2.0f + marginTop

        actualBoxPosition?.x?.toDouble()
            ?.let {
                if (expectedBoxPositionX != null) {
                    Assert.assertEquals(expectedBoxPositionX.toDouble(), it, EQUALS_DELTA)
                }
            }
        actualBoxPosition?.y?.toDouble()
            ?.let { Assert.assertEquals(expectedBoxPositionY.toDouble(), it, EQUALS_DELTA) }
    }

    private fun checkTextBoxDimensionsAndDefaultPosition() {
        checkTextBoxDimensions()
        checkTextBoxDefaultPosition()
    }

    private fun enterTextInput(textToEnter: String) {
        /*
		 * Use replaceText instead of typeText to support the arabic input.
		 *
		 * See:
		 * java.ic_pocketpaint_menu_language.RuntimeException: Failed to get key events for string السلام عليكم 123 (i.e.
		 * current IME does not understand how to translatePerspective the string into key events). As a
		 * workaround, you can use replaceText action to set the text directly in the EditText field.
		 */
        onView(withId(R.id.pocketpaint_text_tool_dialog_input_text))
            .perform(ViewActions.replaceText(textToEnter))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.pocketpaint_text_tool_dialog_input_text))
            .check(ViewAssertions.matches(ViewMatchers.withText(textToEnter)))
    }

    private fun enterTestText() { enterTextInput(TEST_TEXT) }

    private fun enterMultilineTestText() { enterTextInput(TEST_TEXT_MULTILINE) }

    private fun selectFormatting(format: FormattingOptions) {
        onView(ViewMatchers.withText(getFormattingOptionAsString(format))).perform(ViewActions.click())
    }

    private fun selectFontType(fontType: FontType) {
        onView(withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(getFontTypeAsString(fontType))
                    )
                )
            )
        onView(ViewMatchers.withText(getFontTypeAsString(fontType)))
            .perform(ViewActions.click())
    }

    private fun getFontTypeAsString(fontType: FontType): String? {
        return when (fontType) {
            FontType.SANS_SERIF -> activity?.getString(R.string.text_tool_dialog_font_sans_serif)
            FontType.SERIF -> activity?.getString(R.string.text_tool_dialog_font_serif)
            FontType.MONOSPACE -> activity?.getString(R.string.text_tool_dialog_font_monospace)
            FontType.STC -> activity?.getString(R.string.text_tool_dialog_font_arabic_stc)
            FontType.DUBAI -> activity?.getString(R.string.text_tool_dialog_font_dubai)
            else -> null
        }
    }

    private fun getFormattingOptionAsString(format: FormattingOptions): String {
        return when (format) {
            FormattingOptions.UNDERLINE -> activity?.getString(R.string.text_tool_dialog_underline_shortcut)
                .toString()
            FormattingOptions.ITALIC -> activity?.getString(R.string.text_tool_dialog_italic_shortcut)
                .toString()
            FormattingOptions.BOLD -> activity?.getString(R.string.text_tool_dialog_bold_shortcut).toString()
        }
    }

    private fun countPixelsWithColor(pixels: IntArray, color: Int): Int {
        var count = 0
        for (pixel in pixels) { if (pixel == color) { count++ } }
        return count
    }

    private var toolMemberBoxWidth: Float
        get() = textTool!!.boxWidth
        private set(boxWidth) { textTool!!.boxWidth = boxWidth }
    private var toolMemberBoxHeight: Float
        get() = textTool!!.boxHeight
        private set(boxHeight) { textTool!!.boxHeight = boxHeight }
    private var toolMemberBoxPosition: PointF?
        get() = textToolAfterZoom?.toolPosition
        private set(position) {
            if (position != null) {
                textTool?.toolPosition?.set(position)
            }
        }
    private val toolMemberItalic: Boolean
        get() = textTool!!.italic
    private val toolMemberBold: Boolean
        get() = textTool!!.bold
    private val toolMemberMultilineText: Array<String>
        get() = textTool!!.multilineText

    private enum class FormattingOptions { UNDERLINE, ITALIC, BOLD }

    companion object {
        private const val TEST_TEXT = "123 www 123"
        private const val TEST_TEXT_ADVANCED = "testing 123 new"
        private const val TEST_TEXT_MULTILINE = "testing\nmultiline\ntext\n\n123"
        private const val EQUALS_DELTA = 0.25
    }
}
