/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.common.Constants
import org.catrobat.paintroid.test.espresso.util.*
import org.catrobat.paintroid.test.espresso.util.wrappers.*
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith
import java.io.File
import kotlin.collections.ArrayList

@RunWith(AndroidJUnit4::class)
class LayerIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @get:Rule
    var grantPermissionRule: GrantPermissionRule = EspressoUtils.grantPermissionRulesVersionCheck()
    
    private var bitmapHeight = 0
    private var bitmapWidth = 0
    private lateinit var deletionFileList: ArrayList<File?>

    @Before
    fun setUp() {
        deletionFileList = ArrayList()
        val workspace = launchActivityRule.activity.workspace
        bitmapHeight = workspace.height
        bitmapWidth = workspace.width
    }

    @After
    fun tearDown() {
        deletionFileList.forEach { file ->
            if (file != null && file.exists()) {
                Assert.assertTrue(file.delete())
            }
        }
    }

    @Test
    fun testShowLayerMenu() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testInitialSetup() {
        LayerMenuViewInteraction.onLayerMenuView()
                .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        LayerMenuViewInteraction.onLayerMenuView().onButtonAdd()
                .check(ViewAssertions.matches(Matchers.allOf(ViewMatchers.isEnabled(), UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_add))))
        LayerMenuViewInteraction.onLayerMenuView().onButtonDelete()
                .check(ViewAssertions.matches(Matchers.allOf(Matchers.not(ViewMatchers.isEnabled()), UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_delete_disabled))))
    }

    @Test
    fun testAddOneLayer() {
        LayerMenuViewInteraction.onLayerMenuView()
                .checkLayerCount(1)
                .performOpen()
                .performAddLayer()
                .checkLayerCount(2)
    }

    @Test
    fun testTryAddMoreLayersThanLimit() {
        LayerMenuViewInteraction.onLayerMenuView()
                .checkLayerCount(1)
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .checkLayerCount(4)
                .performAddLayer()
                .checkLayerCount(4)
    }

    @Test
    fun testButtonsAddOneLayer() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .checkLayerCount(2)
        LayerMenuViewInteraction.onLayerMenuView().onButtonAdd()
                .check(ViewAssertions.matches(Matchers.allOf(ViewMatchers.isEnabled(), UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_add))))
        LayerMenuViewInteraction.onLayerMenuView().onButtonDelete()
                .check(ViewAssertions.matches(Matchers.allOf(ViewMatchers.isEnabled(), UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_delete))))
        LayerMenuViewInteraction.onLayerMenuView()
                .performAddLayer()
                .performAddLayer()
                .checkLayerCount(4)
        LayerMenuViewInteraction.onLayerMenuView().onButtonAdd()
                .check(ViewAssertions.matches(Matchers.allOf(Matchers.not(ViewMatchers.isEnabled()), UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_add_disabled))))
        LayerMenuViewInteraction.onLayerMenuView().onButtonDelete()
                .check(ViewAssertions.matches(Matchers.allOf(ViewMatchers.isEnabled(), UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_delete))))
        LayerMenuViewInteraction.onLayerMenuView()
                .performDeleteLayer()
                .performDeleteLayer()
                .performDeleteLayer()
                .checkLayerCount(1)
        LayerMenuViewInteraction.onLayerMenuView().onButtonAdd()
                .check(ViewAssertions.matches(Matchers.allOf(ViewMatchers.isEnabled(), UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_add))))
        LayerMenuViewInteraction.onLayerMenuView().onButtonDelete()
                .check(ViewAssertions.matches(Matchers.allOf(Matchers.not(ViewMatchers.isEnabled()), UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_delete_disabled))))
    }

    @Test
    fun testButtonsAfterNewImage() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performClose()
                .checkLayerCount(4)
        LayerMenuViewInteraction.onLayerMenuView().onButtonAdd()
                .check(ViewAssertions.matches(Matchers.allOf(Matchers.not(ViewMatchers.isEnabled()), UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_add_disabled))))
        LayerMenuViewInteraction.onLayerMenuView().onButtonDelete()
                .check(ViewAssertions.matches(Matchers.allOf(ViewMatchers.isEnabled(), UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_delete))))
        TopBarViewInteraction.onTopBarView()
                .performOpenMoreOptions()
        onView(withText(R.string.menu_new_image))
                .perform(click())
        onView(withText(R.string.discard_button_text))
                .perform(click())
        LayerMenuViewInteraction.onLayerMenuView().onButtonAdd()
                .check(ViewAssertions.matches(Matchers.allOf(ViewMatchers.isEnabled(), UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_add))))
        LayerMenuViewInteraction.onLayerMenuView().onButtonDelete()
                .check(ViewAssertions.matches(Matchers.allOf(Matchers.not(ViewMatchers.isEnabled()), UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_delete_disabled))))
        LayerMenuViewInteraction.onLayerMenuView()
                .checkLayerCount(1)
    }

    @Test
    fun testUndoRedoLayerAdd() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performClose()
                .checkLayerCount(2)
        TopBarViewInteraction.onTopBarView()
                .performUndo()
        LayerMenuViewInteraction.onLayerMenuView()
                .checkLayerCount(1)
        TopBarViewInteraction.onTopBarView()
                .performRedo()
        LayerMenuViewInteraction.onLayerMenuView()
                .checkLayerCount(2)
    }

    @Test
    fun testDeleteEmptyLayer() {
        LayerMenuViewInteraction.onLayerMenuView()
                .checkLayerCount(1)
                .performOpen()
                .performAddLayer()
                .checkLayerCount(2)
                .performDeleteLayer()
                .checkLayerCount(1)
    }

    @Ignore("Fail due to Pipette")
    @Test
    fun testDeleteFilledLayer() {
        LayerMenuViewInteraction.onLayerMenuView()
                .checkLayerCount(1)
                .performOpen()
                .performAddLayer()
                .performClose()
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(Color.TRANSPARENT)
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.FILL)
        ToolPropertiesInteraction.onToolProperties()
                .setColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(Color.BLACK)
        LayerMenuViewInteraction.onLayerMenuView()
                .checkLayerCount(2)
                .performOpen()
                .performDeleteLayer()
                .performClose()
                .checkLayerCount(1)
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(Color.TRANSPARENT)
    }

    @Test
    fun testTryDeleteOnlyLayer() {
        LayerMenuViewInteraction.onLayerMenuView()
                .checkLayerCount(1)
                .performOpen()
                .performDeleteLayer()
                .checkLayerCount(1)
    }

    @Ignore("Fail due to Pipette")
    @Test
    fun testSwitchBetweenFilledLayers() {
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.FILL)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(Color.BLACK)
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performClose()
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.FILL)
        ToolPropertiesInteraction.onToolProperties()
                .setColor(Color.WHITE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(Color.WHITE)
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performSelectLayer(1)
                .performClose()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(Color.WHITE)
    }

    @Ignore("Fail due to Pipette")
    @Test
    fun testMultipleLayersNewImageDiscardOld() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .checkLayerCount(4)
                .performClose()
        TopBarViewInteraction.onTopBarView()
                .performOpenMoreOptions()
        onView(withText(R.string.menu_new_image))
                .perform(click())
        onView(withText(R.string.discard_button_text))
                .perform(click())
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(Color.TRANSPARENT)
        LayerMenuViewInteraction.onLayerMenuView()
                .checkLayerCount(1)
    }

    @Ignore("Fail due to Pipette")
    @Test
    fun testMultipleLayersNewImageSaveOld() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .checkLayerCount(4)
                .performClose()
        TopBarViewInteraction.onTopBarView()
                .performOpenMoreOptions()
        onView(withText(R.string.menu_new_image))
                .perform(click())
        onView(withText(R.string.save_button_text))
                .perform(click())
        onView(withText(R.string.save_button_text))
                .perform(click())
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(Color.TRANSPARENT)
        LayerMenuViewInteraction.onLayerMenuView()
                .checkLayerCount(1)
    }

    @Test
    fun testResizingThroughAllLayers() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performClose()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.TRANSFORM)
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
                .performAutoCrop()
        TopBarViewInteraction.onTopBarView()
                .performClickCheckmark()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .checkThatLayerDimensions(Matchers.lessThan(bitmapWidth), Matchers.lessThan(bitmapHeight))
        TopBarViewInteraction.onTopBarView()
                .performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .checkLayerDimensions(bitmapWidth, bitmapHeight)
        TopBarViewInteraction.onTopBarView()
                .performRedo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .checkThatLayerDimensions(Matchers.lessThan(bitmapWidth), Matchers.lessThan(bitmapHeight))
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performSelectLayer(2)
                .performClose()
        TopBarViewInteraction.onTopBarView()
                .performUndo()
                .performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .checkLayerDimensions(bitmapWidth, bitmapHeight)
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performSelectLayer(3)
                .performClose()
        ToolBarViewInteraction.onToolBarView()
                .performOpenToolOptionsView()
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
                .performAutoCrop()
        TopBarViewInteraction.onTopBarView()
                .performClickCheckmark()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .checkThatLayerDimensions(Matchers.lessThan(bitmapWidth), Matchers.lessThan(bitmapHeight))
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performDeleteLayer()
                .performAddLayer()
                .performClose()
        TopBarViewInteraction.onTopBarView()
                .performUndo()
                .performUndo()
                .performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .checkLayerDimensions(bitmapWidth, bitmapHeight)
        TopBarViewInteraction.onTopBarView()
                .performRedo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .checkThatLayerDimensions(Matchers.lessThan(bitmapWidth), Matchers.lessThan(bitmapHeight))
    }

    @Ignore("Fail due to Pipette")
    @Test
    fun testRotatingThroughAllLayers() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performClose()
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.FILL)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.TRANSFORM)
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
                .performRotateClockwise()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .checkLayerDimensions(bitmapHeight, bitmapWidth)
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(Color.BLACK)
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.TRANSFORM)
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
                .performRotateCounterClockwise()
                .performRotateCounterClockwise()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .checkLayerDimensions(bitmapHeight, bitmapWidth)
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(Color.BLACK)
    }

    @Ignore("Fail due to Pipette")
    @Test
    fun testReflectingOnlyCurrentLayer() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performClose()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.TRANSFORM)
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
                .performFlipVertical()
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(Color.TRANSPARENT)
        TopBarViewInteraction.onTopBarView()
                .performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(Color.BLACK)
        TopBarViewInteraction.onTopBarView()
                .performRedo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(Color.TRANSPARENT)
    }

    @Test
    fun testUndoRedoLayerDelete() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .checkLayerCount(2)
                .performDeleteLayer()
                .checkLayerCount(1)
                .performClose()
        TopBarViewInteraction.onTopBarView()
                .performUndo()
        LayerMenuViewInteraction.onLayerMenuView()
                .checkLayerCount(2)
        TopBarViewInteraction.onTopBarView()
                .performRedo()
        LayerMenuViewInteraction.onLayerMenuView()
                .checkLayerCount(1)
    }

    @Ignore("Fail due to Pipette")
    @Test
    fun testLayerOrderUndoDelete() {
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.FILL)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .checkLayerCount(2)
                .performClose()
        ToolPropertiesInteraction.onToolProperties()
                .setColorResource(R.color.pocketpaint_color_picker_green1)
                .checkMatchesColorResource(R.color.pocketpaint_color_picker_green1)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performSelectLayer(1)
                .performDeleteLayer()
                .performClose()
                .checkLayerCount(1)
        TopBarViewInteraction.onTopBarView()
                .performUndo()
        LayerMenuViewInteraction.onLayerMenuView()
                .checkLayerCount(2)
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColorResource(R.color.pocketpaint_color_picker_green1)
    }

    @Test
    fun testUndoRedoLayerRotate() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performClose()
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.TRANSFORM)
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
                .performRotateClockwise()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .checkLayerDimensions(bitmapHeight, bitmapWidth)
        ToolBarViewInteraction.onToolBarView()
                .performCloseToolOptionsView()
        TopBarViewInteraction.onTopBarView()
                .performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .checkLayerDimensions(bitmapWidth, bitmapHeight)
        TopBarViewInteraction.onTopBarView()
                .performRedo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .checkLayerDimensions(bitmapHeight, bitmapWidth)
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performDeleteLayer()
                .performAddLayer()
                .performSelectLayer(3)
                .performClose()
        TopBarViewInteraction.onTopBarView()
                .performUndo()
                .performUndo()
                .performUndo()
                .performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .checkLayerDimensions(bitmapWidth, bitmapHeight)
        TopBarViewInteraction.onTopBarView()
                .performRedo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .checkLayerDimensions(bitmapHeight, bitmapWidth)
    }

    @Test
    fun testHideLayer() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .checkLayerCount(2)
                .performClose()
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.FILL)
        DrawingSurfaceInteraction.onDrawingSurfaceView().perform(click())
        DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.BLACK, 1f, 1f)
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .perfomToggleLayerVisibility(0)
                .performClose()
        DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, 1f, 1f)
    }

    @Test
    fun testHideThenUnhideLayer() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .checkLayerCount(2)
                .performClose()
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.FILL)
        DrawingSurfaceInteraction.onDrawingSurfaceView().perform(click())
        DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.BLACK, 1f, 1f)
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .perfomToggleLayerVisibility(0)
                .performClose()
        DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, 1f, 1f)
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .perfomToggleLayerVisibility(0)
                .performClose()
        DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.BLACK, 1f, 1f)
    }

    @Test
    fun testTryMergeOrReorderWhileALayerIsHidden() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .checkLayerCount(2)
                .perfomToggleLayerVisibility(0)
                .performLongClickLayer(0)
        onView(withText(R.string.no_longclick_on_hidden_layer)).inRoot(RootMatchers.withDecorView(Matchers.not(launchActivityRule.activity.window.decorView))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testTryChangeToolWhileALayerIsHidden() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .checkLayerCount(2)
                .perfomToggleLayerVisibility(0)
                .performClose()
        ToolBarViewInteraction.onToolBarView()
                .onToolsClicked()
        onView(withText(R.string.no_tools_on_hidden_layer)).inRoot(RootMatchers.withDecorView(Matchers.not(launchActivityRule.activity.window.decorView))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testLoadLargeBitmapAndAddMaxLayerMenu() {
        val intent = Intent().apply {
            data = createTestImageFile()
        }
        Intents.init()
        val intentResult = ActivityResult(Activity.RESULT_OK, intent)

        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)
        TopBarViewInteraction.onTopBarView()
                .performOpenMoreOptions()
        onView(withText(R.string.menu_load_image)).perform(click())
        Intents.release()
        onView(withText(R.string.dialog_warning_new_image)).check(ViewAssertions.doesNotExist())
        onView(withText(R.string.pocketpaint_ok)).perform(click())
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        LayerMenuViewInteraction.onLayerMenuView()
                .checkLayerCount(1)
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .checkLayerCount(Constants.MAX_LAYERS)
    }

    private fun createTestImageFile(): Uri {
        val bitmap = Bitmap.createBitmap(7000, 7000, Bitmap.Config.ARGB_8888)
        with(Canvas(bitmap)) {
            drawColor(Color.BLACK)
            drawBitmap(bitmap, 0f, 0f, null)
        }
        val imageFile = File(launchActivityRule.activity.getExternalFilesDir(null)!!.absolutePath, "loadImage.jpg")
        val imageUri = Uri.fromFile(imageFile)
        launchActivityRule.activity.myContentResolver.openOutputStream((imageUri)).use { fos ->
            Assert.assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos))
        }
        deletionFileList.add(imageFile)
        return imageUri
    }
}
