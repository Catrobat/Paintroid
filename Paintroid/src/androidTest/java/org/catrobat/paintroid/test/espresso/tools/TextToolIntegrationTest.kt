/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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

//import org.catrobat.paintroid.tools.ToolReference.tool
//import org.catrobat.paintroid.tools.implementation.TextTool.resetBoxPosition
//import org.catrobat.paintroid.ui.tools.FontListAdapter.getSelectedItem
//import org.catrobat.paintroid.contract.LayerContracts.Model.currentLayer
//import org.catrobat.paintroid.contract.LayerContracts.Layer.bitmap
//import org.catrobat.paintroid.contract.LayerContracts.Model.height
//import org.catrobat.paintroid.contract.LayerContracts.Model.width
//import org.catrobat.paintroid.ui.Perspective.scale
//import org.catrobat.paintroid.MainActivity.refreshDrawingSurface
//import org.catrobat.paintroid.tools.implementation.TextTool.multilineText
import org.junit.runner.RunWith
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper
import org.catrobat.paintroid.tools.implementation.TextTool
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.tools.TextToolIntegrationTest.FormattingOptions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.action.ViewActions
import org.catrobat.paintroid.test.espresso.tools.TextToolIntegrationTest
import androidx.test.espresso.assertion.ViewAssertions
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import androidx.test.espresso.contrib.RecyclerViewActions
import org.catrobat.paintroid.tools.FontType
import org.catrobat.paintroid.ui.tools.FontListAdapter
import android.graphics.PointF
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Paint
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.tools.implementation.BOX_OFFSET
import org.catrobat.paintroid.tools.implementation.MARGIN_TOP
import org.junit.*
import java.util.ArrayList

@RunWith(AndroidJUnit4::class)
class TextToolIntegrationTest {
    @Rule
    var launchActivityRule = ActivityTestRule(
        MainActivity::class.java
    )

    @Rule
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
    @Before
    fun setUp() {
        activity = launchActivityRule.activity
        activityHelper = MainActivityHelper(activity)
        layerModel = activity.layerModel
        toolReference = activity.toolReference
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.TEXT)
        textTool = toolReference!!.tool as TextTool?
        textEditText = activity.findViewById(R.id.pocketpaint_text_tool_dialog_input_text)
        fontList = activity.findViewById(R.id.pocketpaint_text_tool_dialog_list_font)
        underlinedToggleButton =
            activity.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_underlined)
        italicToggleButton = activity.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_italic)
        boldToggleButton = activity.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_bold)
        textSize = activity.findViewById(R.id.pocketpaint_font_size_text)
        textTool!!.resetBoxPosition()
    }

    @Test
    fun testTextToolStillEditableAfterClosingTextTool() {
        selectFormatting(FormattingOptions.ITALIC)
        selectFormatting(FormattingOptions.BOLD)
        selectFormatting(FormattingOptions.UNDERLINE)
        enterTestText()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.TOP_MIDDLE))
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_input_text)).perform(
            ViewActions.replaceText(
                TEST_TEXT_ADVANCED
            )
        )
        Assert.assertTrue(italicToggleButton!!.isChecked)
        Assert.assertTrue(boldToggleButton!!.isChecked)
        Assert.assertTrue(underlinedToggleButton!!.isChecked)
        Assert.assertEquals(TEST_TEXT_ADVANCED, textEditText!!.text.toString())
    }

    @Ignore("Fix bug in own ticket , focus is not correctly implemented in google play either")
    @Test
    fun testDialogKeyboardTextBoxAppearanceOnStartup() {
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_input_text))
            .check(ViewAssertions.matches(ViewMatchers.hasFocus()))
        checkTextBoxDimensionsAndDefaultPosition()
    }

    @Test
    fun testDialogDefaultValues() {
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_input_text))
            .check(ViewAssertions.matches(ViewMatchers.withHint(R.string.text_tool_dialog_input_hint)))
            .check(ViewAssertions.matches(ViewMatchers.withText(textTool!!.text)))
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.TEXT)
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .check(
                ViewAssertions.matches(
                    UiMatcher.atPosition(
                        0,
                        ViewMatchers.hasDescendant(ViewMatchers.isChecked())
                    )
                )
            )
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .check(
                ViewAssertions.matches(
                    UiMatcher.atPosition(
                        1,
                        ViewMatchers.hasDescendant(ViewMatchers.isNotChecked())
                    )
                )
            )
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(
                ViewAssertions.matches(
                    UiMatcher.atPosition(
                        2,
                        ViewMatchers.hasDescendant(ViewMatchers.isNotChecked())
                    )
                )
            )
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(3))
            .check(
                ViewAssertions.matches(
                    UiMatcher.atPosition(
                        3,
                        ViewMatchers.hasDescendant(ViewMatchers.isNotChecked())
                    )
                )
            )
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(4))
            .check(
                ViewAssertions.matches(
                    UiMatcher.atPosition(
                        4,
                        ViewMatchers.hasDescendant(ViewMatchers.isNotChecked())
                    )
                )
            )
        Assert.assertFalse(textTool!!.underlined)
        Assert.assertFalse(textTool.italic)
        Assert.assertFalse(textTool.bold)
    }

    @Test
    fun testDialogToolInteraction() {
        enterTestText()
        Assert.assertEquals(TEST_TEXT, textTool!!.text)
        selectFontType(FontType.SERIF)
        Assert.assertEquals(FontType.SERIF, textTool!!.font)
        Assert.assertEquals(
            FontType.SERIF,
            (fontList!!.adapter as FontListAdapter?)!!.getSelectedItem()
        )
        selectFormatting(FormattingOptions.UNDERLINE)
        Assert.assertTrue(textTool!!.underlined)
        Assert.assertTrue(underlinedToggleButton!!.isChecked)
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.UNDERLINE),
            underlinedToggleButton!!.text.toString()
        )
        selectFormatting(FormattingOptions.UNDERLINE)
        Assert.assertFalse(textTool!!.underlined)
        Assert.assertFalse(underlinedToggleButton!!.isChecked)
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.UNDERLINE),
            underlinedToggleButton!!.text.toString()
        )
        selectFormatting(FormattingOptions.ITALIC)
        Assert.assertTrue(toolMemberItalic)
        Assert.assertTrue(italicToggleButton!!.isChecked)
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.ITALIC),
            italicToggleButton!!.text.toString()
        )
        selectFormatting(FormattingOptions.ITALIC)
        Assert.assertFalse(toolMemberItalic)
        Assert.assertFalse(italicToggleButton!!.isChecked)
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.ITALIC),
            italicToggleButton!!.text.toString()
        )
        selectFormatting(FormattingOptions.BOLD)
        Assert.assertTrue(toolMemberBold)
        Assert.assertTrue(boldToggleButton!!.isChecked)
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.BOLD),
            boldToggleButton!!.text.toString()
        )
        selectFormatting(FormattingOptions.BOLD)
        Assert.assertFalse(toolMemberBold)
        Assert.assertFalse(boldToggleButton!!.isChecked)
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.BOLD),
            boldToggleButton!!.text.toString()
        )
    }

    @Test
    fun testDialogAndTextBoxAfterReopenDialog() {
        enterTestText()
        selectFontType(FontType.SANS_SERIF)
        selectFormatting(FormattingOptions.UNDERLINE)
        selectFormatting(FormattingOptions.ITALIC)
        selectFormatting(FormattingOptions.BOLD)
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        val oldBoxWidth: Float = toolMemberBoxWidth
        val oldBoxHeight: Float = toolMemberBoxHeight
        val boxPosition = toolMemberBoxPosition
        val newBoxPosition = PointF(boxPosition.x + 100, boxPosition.y + 200)
        toolMemberBoxPosition = newBoxPosition
        ToolBarViewInteraction.onToolBarView()
            .performOpenToolOptionsView()
        Assert.assertEquals(TEST_TEXT, textEditText!!.text.toString())
        Assert.assertEquals(
            FontType.SANS_SERIF,
            (fontList!!.adapter as FontListAdapter?)!!.getSelectedItem()
        )
        Assert.assertTrue(underlinedToggleButton!!.isChecked)
        Assert.assertTrue(italicToggleButton!!.isChecked)
        Assert.assertTrue(boldToggleButton!!.isChecked)
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
        val expectedPosition = PointF(toolMemberBoxPosition.x, toolMemberBoxPosition.y)
        textTool = toolReference!!.tool as TextTool?
        val oldBoxWidth: Float = toolMemberBoxWidth
        val oldBoxHeight: Float = toolMemberBoxHeight
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        Assert.assertEquals(TEST_TEXT, textEditText!!.text.toString())
        Assert.assertEquals(
            FontType.SANS_SERIF,
            (fontList!!.adapter as FontListAdapter?)!!.getSelectedItem()
        )
        Assert.assertTrue(underlinedToggleButton!!.isChecked)
        Assert.assertTrue(italicToggleButton!!.isChecked)
        Assert.assertTrue(boldToggleButton!!.isChecked)
        Assert.assertEquals(expectedPosition, toolMemberBoxPosition)
        Assert.assertEquals(oldBoxWidth.toDouble(), toolMemberBoxWidth.toDouble(), EQUALS_DELTA)
        Assert.assertEquals(oldBoxHeight.toDouble(), toolMemberBoxHeight.toDouble(), EQUALS_DELTA)
    }

    @Test
    fun testCheckBoxSizeAndContentAfterFormatting() {
        enterTestText()
        Assert.assertFalse(underlinedToggleButton!!.isChecked)
        Assert.assertFalse(boldToggleButton!!.isChecked)
        Assert.assertFalse(italicToggleButton!!.isChecked)
        val fonts = ArrayList<FontType>()
        fonts.add(FontType.SERIF)
        fonts.add(FontType.SANS_SERIF)
        fonts.add(FontType.MONOSPACE)
        fonts.add(FontType.DUBAI)
        fonts.add(FontType.STC)
        checkTextBoxDimensionsAndDefaultPosition()
        for (font in fonts) {
            layerModel!!.currentLayer!!.bitmap!!.eraseColor(Color.TRANSPARENT)
            val boxWidth: Float = toolMemberBoxWidth
            val boxHeight: Float = toolMemberBoxHeight
            selectFontType(font)
            Assert.assertTrue(boxWidth == toolMemberBoxWidth && boxHeight == toolMemberBoxHeight)
            val canvasPoint = centerBox()
            layerModel!!.currentLayer!!.bitmap!!.eraseColor(Color.TRANSPARENT)
            TopBarViewInteraction.onTopBarView()
                .performClickCheckmark()
            val surfaceBitmapHeight = layerModel!!.height
            val pixelsDrawingSurface = IntArray(surfaceBitmapHeight)
            layerModel!!.currentLayer!!.bitmap!!.getPixels(
                pixelsDrawingSurface,
                0,
                1,
                canvasPoint.x.toInt(),
                0,
                1,
                surfaceBitmapHeight
            )
            var pixelAmountBefore = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK)
            assert(pixelAmountBefore > 0)
            selectFormatting(FormattingOptions.UNDERLINE)
            Assert.assertTrue(underlinedToggleButton!!.isChecked)
            layerModel!!.currentLayer!!.bitmap!!.eraseColor(Color.TRANSPARENT)
            TopBarViewInteraction.onTopBarView()
                .performClickCheckmark()
            layerModel!!.currentLayer!!.bitmap!!.getPixels(
                pixelsDrawingSurface,
                0,
                1,
                canvasPoint.x.toInt(),
                0,
                1,
                surfaceBitmapHeight
            )
            var pixelAmountAfter = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK)
            assert(pixelAmountAfter > 0)
            Assert.assertTrue(pixelAmountBefore < pixelAmountAfter)
            selectFormatting(FormattingOptions.ITALIC)
            Assert.assertTrue(italicToggleButton!!.isChecked)
            Assert.assertTrue(toolMemberItalic)
            layerModel!!.currentLayer!!.bitmap!!.eraseColor(Color.TRANSPARENT)
            TopBarViewInteraction.onTopBarView()
                .performClickCheckmark()
            layerModel!!.currentLayer!!.bitmap!!.getPixels(
                pixelsDrawingSurface,
                0,
                1,
                canvasPoint.x.toInt(),
                0,
                1,
                surfaceBitmapHeight
            )
            pixelAmountBefore = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK)
            assert(pixelAmountBefore > 0)
            selectFormatting(FormattingOptions.BOLD)
            Assert.assertTrue(boldToggleButton!!.isChecked)
            layerModel!!.currentLayer!!.bitmap!!.eraseColor(Color.TRANSPARENT)
            TopBarViewInteraction.onTopBarView()
                .performClickCheckmark()
            layerModel!!.currentLayer!!.bitmap!!.getPixels(
                pixelsDrawingSurface,
                0,
                1,
                canvasPoint.x.toInt(),
                0,
                1,
                surfaceBitmapHeight
            )
            pixelAmountAfter = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK)
            assert(pixelAmountAfter > 0)
            Assert.assertTrue(pixelAmountAfter > pixelAmountBefore)
            selectFormatting(FormattingOptions.UNDERLINE)
            Assert.assertFalse(underlinedToggleButton!!.isChecked)
            selectFormatting(FormattingOptions.ITALIC)
            Assert.assertFalse(italicToggleButton!!.isChecked)
            selectFormatting(FormattingOptions.BOLD)
            Assert.assertFalse(boldToggleButton!!.isChecked)
        }
    }

    @Test
    fun testInputTextAndFormatForTextSize50() {
        enterTestText()
        val boxWidth: Float = toolMemberBoxWidth
        val boxHeight: Float = toolMemberBoxHeight
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_font_size_text))
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
        val boxWidth: Float = toolMemberBoxWidth
        val boxHeight: Float = toolMemberBoxHeight
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_font_size_text))
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
        val boxWidth: Float = toolMemberBoxWidth
        val boxHeight: Float = toolMemberBoxHeight
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_font_size_text))
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
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        val canvasPoint = centerBox()
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        val surfaceBitmapWidth = layerModel!!.width
        val pixelsDrawingSurface = IntArray(surfaceBitmapWidth)
        layerModel!!.currentLayer!!.bitmap!!.getPixels(
            pixelsDrawingSurface,
            0,
            surfaceBitmapWidth,
            0,
            canvasPoint.y.toInt(),
            surfaceBitmapWidth,
            1
        )
        var pixelAmount = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK)
        assert(pixelAmount > 0)
        TopBarViewInteraction.onTopBarView()
            .performUndo()
        layerModel!!.currentLayer!!.bitmap!!.getPixels(
            pixelsDrawingSurface,
            0,
            surfaceBitmapWidth,
            0,
            canvasPoint.y.toInt(),
            surfaceBitmapWidth,
            1
        )
        Assert.assertEquals(0, countPixelsWithColor(pixelsDrawingSurface, Color.BLACK).toLong())
        TopBarViewInteraction.onTopBarView()
            .performRedo()
        layerModel!!.currentLayer!!.bitmap!!.getPixels(
            pixelsDrawingSurface,
            0,
            surfaceBitmapWidth,
            0,
            canvasPoint.y.toInt(),
            surfaceBitmapWidth,
            1
        )
        pixelAmount = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK)
        assert(pixelAmount > 0)
    }

    @Test
    fun testChangeTextColor() {
        enterTestText()
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        val canvasPoint = centerBox()
        ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.WHITE)
        val paint = textTool!!.textPaint
        var selectedColor = paint.color
        Assert.assertEquals(Color.WHITE.toLong(), selectedColor.toLong())
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        val surfaceBitmapWidth = layerModel!!.width
        val pixelsDrawingSurface = IntArray(surfaceBitmapWidth)
        layerModel!!.currentLayer!!.bitmap!!.getPixels(
            pixelsDrawingSurface,
            0,
            surfaceBitmapWidth,
            0,
            canvasPoint.y.toInt(),
            surfaceBitmapWidth,
            1
        )
        var pixelAmount = countPixelsWithColor(pixelsDrawingSurface, Color.WHITE)
        assert(pixelAmount > 0)
        ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.BLACK)
        selectedColor = paint.color
        Assert.assertEquals(Color.BLACK.toLong(), selectedColor.toLong())
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        layerModel!!.currentLayer!!.bitmap!!.getPixels(
            pixelsDrawingSurface,
            0,
            surfaceBitmapWidth,
            0,
            canvasPoint.y.toInt(),
            surfaceBitmapWidth,
            1
        )
        pixelAmount = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK)
        assert(pixelAmount > 0)
    }

    @Test
    fun testChangeToolFromEraser() {
        val color = textTool!!.textPaint.color
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.ERASER)
            .performSelectTool(ToolType.TEXT)
        val newColor = textTool!!.textPaint.color
        Assert.assertEquals(color.toLong(), Color.BLACK.toLong())
        Assert.assertEquals(color.toLong(), newColor.toLong())
    }

    @Test
    fun testMultiLineText() {
        checkTextBoxDimensionsAndDefaultPosition()
        enterMultilineTestText()
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        val expectedTextSplitUp = arrayOf("testing", "multiline", "text", "", "123")
        val actualTextSplitUp: Array<String> = toolMemberMultilineText
        Assert.assertArrayEquals(expectedTextSplitUp, actualTextSplitUp)
    }

    @Test
    fun testTextToolAppliedWhenSelectingOtherTool() {
        enterTestText()
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        val surfaceBitmapWidth = layerModel!!.width
        val pixelsDrawingSurface = IntArray(surfaceBitmapWidth)
        layerModel!!.currentLayer!!.bitmap!!.getPixels(
            pixelsDrawingSurface,
            0,
            surfaceBitmapWidth,
            0,
            textTool!!.toolPosition.y.toInt(),
            surfaceBitmapWidth,
            1
        )
        val numberOfBlackPixels = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK)
        Assert.assertTrue(numberOfBlackPixels > 0)
    }

    @Test
    fun testTextToolNotAppliedWhenPressingBack() {
        enterTestText()
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        Espresso.pressBack()
        val surfaceBitmapWidth = layerModel!!.width
        val pixelsDrawingSurface = IntArray(surfaceBitmapWidth)
        layerModel!!.currentLayer!!.bitmap!!.getPixels(
            pixelsDrawingSurface,
            0,
            surfaceBitmapWidth,
            0,
            textTool!!.toolPosition.y.toInt(),
            surfaceBitmapWidth,
            1
        )
        val numberOfBlackPixels = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK)
        Assert.assertEquals(0, numberOfBlackPixels.toLong())
    }

    @Test
    fun testTextToolDoesNotResetPerspectiveScale() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        val scale = 2.0f
        activity!!.perspective.scale = scale
        activity!!.perspective.surfaceTranslationY = 200f
        activity!!.perspective.surfaceTranslationX = 50f
        activity!!.refreshDrawingSurface()
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.TEXT)
        enterTestText()
        Assert.assertEquals(scale, activity!!.perspective.scale, 0.0001f)
    }

    @Test
    fun testTextToolBoxIsPlacedCorrectlyWhenZoomedIn() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.TEXT)
        enterTestText()
        val initialPosition = toolMemberBoxPosition
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        val scale = 2.0f
        activity!!.perspective.scale = scale
        activity!!.perspective.surfaceTranslationY = 200f
        activity!!.perspective.surfaceTranslationX = 50f
        activity!!.refreshDrawingSurface()
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.TEXT)
        enterTestText()
        textToolAfterZoom = activity!!.toolReference.tool as TextTool?
        val positionAfterZoom = toolMemberBoxPosition
        Assert.assertEquals(scale, activity!!.perspective.scale, 0.0001f)
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        Assert.assertNotEquals(initialPosition, positionAfterZoom)
    }

    @Test
    fun testSettingFontAndFontStyleDoesNotResetBox() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.TEXT)
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
            var boxWidth: Float = toolMemberBoxWidth
            var boxHeight: Float = toolMemberBoxHeight
            boxWidth = boxWidth + 100
            boxHeight = boxHeight + 100
            selectFontType(font)
            selectFormatting(FormattingOptions.ITALIC)
            selectFormatting(FormattingOptions.BOLD)
            selectFormatting(FormattingOptions.UNDERLINE)
            Assert.assertTrue(boxWidth < toolMemberBoxWidth && boxHeight < toolMemberBoxHeight)
        }
    }

    private fun centerBox(): PointF {
        val screenPoint =
            PointF(activityHelper!!.displayWidth / 2.0f, activityHelper!!.displayHeight / 2.0f)
        val canvasPoint = PointF(screenPoint.x, screenPoint.y)
        canvasPoint.x = Math.round(canvasPoint.x).toFloat()
        canvasPoint.y = Math.round(canvasPoint.y).toFloat()
        toolMemberBoxPosition = canvasPoint
        return canvasPoint
    }

    private fun checkTextBoxDimensions() {
        val actualBoxWidth: Float = toolMemberBoxWidth
        val actualBoxHeight: Float = toolMemberBoxHeight
        val italic = italicToggleButton!!.isChecked
        val font = (fontList!!.adapter as FontListAdapter?)!!.getSelectedItem()
        val stringTextSize = textSize!!.text.toString()
        val textSize: Float = stringTextSize.toFloat() * TEXT_SIZE_MAGNIFICATION_FACTOR
        val textPaint = Paint()
        textPaint.isAntiAlias = true
        textPaint.textSize = textSize
        val style = if (italic) Typeface.ITALIC else Typeface.NORMAL
        when (font) {
            FontType.SANS_SERIF -> textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, style)
            FontType.SERIF -> textPaint.typeface = Typeface.create(Typeface.SERIF, style)
            FontType.STC -> textPaint.typeface =
                ResourcesCompat.getFont(launchActivityRule.activity, R.font.stc_regular)
            FontType.DUBAI -> textPaint.typeface =
                ResourcesCompat.getFont(launchActivityRule.activity, R.font.dubai)
            else -> textPaint.typeface = Typeface.create(Typeface.MONOSPACE, style)
        }
        val textDescent = textPaint.descent()
        val textAscent = textPaint.ascent()
        val multilineText: Array<String> = toolMemberMultilineText
        var maxTextWidth = 0f
        for (str in multilineText) {
            val textWidth = textPaint.measureText(str)
            if (textWidth > maxTextWidth) {
                maxTextWidth = textWidth
            }
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
        val boxHeight: Float = toolMemberBoxHeight
        val expectedBoxPositionX = layerModel!!.width / 2.0f
        val expectedBoxPositionY = boxHeight / 2.0f + marginTop
        Assert.assertEquals(
            expectedBoxPositionX.toDouble(),
            actualBoxPosition.x.toDouble(),
            EQUALS_DELTA
        )
        Assert.assertEquals(
            expectedBoxPositionY.toDouble(),
            actualBoxPosition.y.toDouble(),
            EQUALS_DELTA
        )
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
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_input_text))
            .perform(ViewActions.replaceText(textToEnter))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_input_text))
            .check(ViewAssertions.matches(ViewMatchers.withText(textToEnter)))
    }

    private fun enterTestText() {
        enterTextInput(TEST_TEXT)
    }

    private fun enterMultilineTestText() {
        enterTextInput(TEST_TEXT_MULTILINE)
    }

    private fun selectFormatting(format: FormattingOptions) {
        Espresso.onView(ViewMatchers.withText(getFormattingOptionAsString(format)))
            .perform(ViewActions.click())
    }

    private fun selectFontType(fontType: FontType) {
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(getFontTypeAsString(fontType))
                    )
                )
            )
        Espresso.onView(ViewMatchers.withText(getFontTypeAsString(fontType)))
            .perform(ViewActions.click())
    }

    private fun getFontTypeAsString(fontType: FontType): String? {
        return when (fontType) {
            FontType.SANS_SERIF -> activity!!.getString(R.string.text_tool_dialog_font_sans_serif)
            FontType.SERIF -> activity!!.getString(R.string.text_tool_dialog_font_serif)
            FontType.MONOSPACE -> activity!!.getString(R.string.text_tool_dialog_font_monospace)
            FontType.STC -> activity!!.getString(R.string.text_tool_dialog_font_arabic_stc)
            FontType.DUBAI -> activity!!.getString(R.string.text_tool_dialog_font_dubai)
            else -> null
        }
    }

    private fun getFormattingOptionAsString(format: FormattingOptions): String? {
        return when (format) {
            FormattingOptions.UNDERLINE -> activity!!.getString(R.string.text_tool_dialog_underline_shortcut)
            FormattingOptions.ITALIC -> activity!!.getString(R.string.text_tool_dialog_italic_shortcut)
            FormattingOptions.BOLD -> activity!!.getString(R.string.text_tool_dialog_bold_shortcut)
            else -> null
        }
    }

    private fun countPixelsWithColor(pixels: IntArray, color: Int): Int {
        var count = 0
        for (pixel in pixels) {
            if (pixel == color) {
                count++
            }
        }
        return count
    }

    private var toolMemberBoxPosition: PointF
        private get() = if (textToolAfterZoom != null) {
            textToolAfterZoom!!.toolPosition
        } else {
            textTool!!.toolPosition
        }
        private set(position) {
            textTool!!.toolPosition.set(position)
        }

    private enum class FormattingOptions {
        UNDERLINE, ITALIC, BOLD
    }

    companion object {
        private const val TEST_TEXT = "123 www 123"
        private const val TEST_TEXT_ADVANCED = "testing 123 new"
        private const val TEST_TEXT_MULTILINE = "testing\nmultiline\ntext\n\n123"
        private const val EQUALS_DELTA = 0.25
    }
}