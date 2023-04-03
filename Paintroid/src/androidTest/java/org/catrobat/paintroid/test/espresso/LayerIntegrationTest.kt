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
package org.catrobat.paintroid.test.espresso

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import junit.framework.AssertionFailedError
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.EspressoUtils
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiInteractions.waitFor
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TransformToolOptionsViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.test.utils.ToastMatcher
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

private const val FOUR_LAYERS = 4

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
    private lateinit var activity: Activity
    private lateinit var deletionFileList: ArrayList<File?>
    private lateinit var idlingResource: CountingIdlingResource

    @Before
    fun setUp() {
        activity = launchActivityRule.activity
        deletionFileList = ArrayList()
        val workspace = launchActivityRule.activity.workspace
        bitmapHeight = workspace.height
        bitmapWidth = workspace.width
        idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        deletionFileList.forEach { file ->
            if (file != null && file.exists()) {
                Assert.assertTrue(file.delete())
            }
        }
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testShowLayerMenu() {
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .check(matches(isDisplayed()))
    }

    @Test
    fun testInitialSetup() {
        LayerMenuViewInteraction.onLayerMenuView()
            .check(matches(Matchers.not(isDisplayed())))
        assertIfLayerAddButtonIsEnabled()
        assertIfLayerDeleteButtonIsDisabled()
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
    fun testButtonsAddOneLayer() {
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
        LayerMenuViewInteraction.onLayerMenuView().onButtonAdd()
            .check(
                assertIfAddLayerButtonIsEnabled()
            )
        assertIfDeleteLayerButtonIsDisabled()
        LayerMenuViewInteraction.onLayerMenuView()
            .performAddLayer()
            .performAddLayer()
            .checkLayerCount(4)
        LayerMenuViewInteraction.onLayerMenuView()
            .performDeleteLayer()
            .performDeleteLayer()
            .performDeleteLayer()
            .checkLayerCount(1)
    }

    private fun assertIfLayerAddButtonIsEnabled() {
        LayerMenuViewInteraction.onLayerMenuView().onButtonAdd()
            .check(
                matches(
                    Matchers.allOf(
                        isEnabled(),
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_add)
                    )
                )
            )
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
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()

        onView(withText(R.string.menu_new_image))
            .perform(click())
        onView(withText(R.string.discard_button_text))
            .perform(click())
        LayerMenuViewInteraction.onLayerMenuView().onButtonAdd()
            .check(
                assertIfAddLayerButtonIsEnabled()
            )
        assertIfLayerDeleteButtonIsDisabled()
        LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
    }

    private fun assertIfDeleteLayerButtonIsDisabled() {
        LayerMenuViewInteraction.onLayerMenuView().onButtonDelete()
            .check(
                matches(
                    Matchers.allOf(
                        isEnabled(),
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_delete)
                    )
                )
            )
    }

    private fun assertIfLayerAddButtonIsDisabled() {
        LayerMenuViewInteraction.onLayerMenuView().onButtonAdd()
            .check(
                matches(
                    Matchers.allOf(
                        Matchers.not(isEnabled()),
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_add_disabled)
                    )
                )
            )
    }

    private fun assertIfLayerDeleteButtonIsDisabled() {
        LayerMenuViewInteraction.onLayerMenuView().onButtonDelete()
            .check(
                matches(
                    Matchers.allOf(
                        Matchers.not(isEnabled()),
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_delete_disabled)
                    )
                )
            )
    }

    private fun assertIfAddLayerButtonIsEnabled() = matches(
        Matchers.allOf(
            isEnabled(),
            UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_add)
        )
    )

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
        onView(isRoot()).perform(waitFor(100))
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
        ToolBarViewInteraction.onToolBarView()
            .performOpenToolOptionsView()
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(26)
            .checkLayerWidthMatches(26)
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSelectLayer(1)
            .performClose()
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(26)
            .checkLayerWidthMatches(26)
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSelectLayer(2)
            .performClose()
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(26)
            .checkLayerWidthMatches(26)
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performScrollToPositionInLayerNavigation(3)
            .performSelectLayer(3)
            .performClose()
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(26)
            .checkLayerWidthMatches(26)
    }

    @Test
    fun testRotatingThroughAllLayers() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE
                )
            )
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
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        ToolBarViewInteraction.onToolBarView()
            .performOpenToolOptionsView()
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(bitmapWidth)
            .checkLayerWidthMatches(bitmapHeight)
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSelectLayer(1)
            .performClose()
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(bitmapWidth)
            .checkLayerWidthMatches(bitmapHeight)
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSelectLayer(2)
            .performClose()
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(bitmapWidth)
            .checkLayerWidthMatches(bitmapHeight)
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performScrollToPositionInLayerNavigation(3)
            .performSelectLayer(3)
            .performClose()
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(bitmapWidth)
            .checkLayerWidthMatches(bitmapHeight)
    }

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

    @Test
    fun testLayerOpacity() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)

        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSetOpacityTo(50, 0)
            .performClose()

        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)

        val fiftyPercentOpacityBlack = Color.argb(255 / 2, 0, 0, 0)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(fiftyPercentOpacityBlack, BitmapLocationProvider.MIDDLE)

        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSetOpacityTo(0, 0)
            .performClose()

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testOpacityAndVisibilityIconDisabled() {
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()

        onView(withId(R.id.pocketpaint_layer_side_nav_button_visibility))
                .check(matches(Matchers.not(isEnabled())))
        onView(withId(R.id.pocketpaint_layer_side_nav_button_opacity))
                .check(matches(Matchers.not(isEnabled())))
    }

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
            .setColorResource(R.color.pocketpaint_color_merge_layer)
            .checkMatchesColorResource(R.color.pocketpaint_color_merge_layer)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSelectLayer(1)
            .performDeleteLayer()
            .checkLayerCount(1)
            .performClose()
        TopBarViewInteraction.onTopBarView()
            .performUndo()
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .checkLayerCount(2)
            .performClose()
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColorResource(R.color.pocketpaint_color_merge_layer)
    }

    @Test
    fun testLayerPreviewKeepsBitmapAfterOrientationChange() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .checkLayerAtPositionHasTopLeftPixelWithColor(0, Color.BLACK)

        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerAtPositionHasTopLeftPixelWithColor(0, Color.BLACK)
    }

    @Test
    fun testUndoRedoLayerRotate() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .performRotateClockwise()
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        ToolBarViewInteraction.onToolBarView()
            .performOpenToolOptionsView()
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(bitmapWidth)
            .checkLayerWidthMatches(bitmapHeight)
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        TopBarViewInteraction.onTopBarView()
            .performUndo()
        ToolBarViewInteraction.onToolBarView()
            .performOpenToolOptionsView()
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(bitmapHeight)
            .checkLayerWidthMatches(bitmapWidth)
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        TopBarViewInteraction.onTopBarView()
            .performRedo()
        ToolBarViewInteraction.onToolBarView()
            .performOpenToolOptionsView()
        TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(bitmapWidth)
            .checkLayerWidthMatches(bitmapHeight)
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
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
            .performToggleLayerVisibility(0)
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
            .performToggleLayerVisibility(0)
            .performClose()
        DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, 1f, 1f)
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performToggleLayerVisibility(0)
            .performClose()
        DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.BLACK, 1f, 1f)
    }

    @Test
    fun testTryMergeOrReorderWhileALayerIsHidden() {
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
            .performToggleLayerVisibility(0)
            .performStartDragging(0)
        onView(withText(R.string.no_longclick_on_hidden_layer))
            .inRoot(
                ToastMatcher().apply {
                    matches(isDisplayed())
                }
            )
    }

    @Test
    fun testTryChangeToolWhileALayerIsHidden() {
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
            .performToggleLayerVisibility(0)
            .performClose()
        ToolBarViewInteraction.onToolBarView()
            .onToolsClicked()
        onView(
            withText(R.string.no_tools_on_hidden_layer)
        ).inRoot(
            ToastMatcher().apply {
                matches(isDisplayed())
            }
        )
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
        onView(withText(R.string.menu_replace_image)).perform(click())
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
            .checkLayerCount(FOUR_LAYERS)
    }

    @Test
    fun testAddTenLayers() {
        LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
            .performAddLayer()
            .checkLayerCount(3)
            .performAddLayer()
            .checkLayerCount(4)
            .performAddLayer()
            .checkLayerCount(5)
            .performAddLayer()
            .checkLayerCount(6)
            .performAddLayer()
            .checkLayerCount(7)
            .performAddLayer()
            .checkLayerCount(8)
            .performAddLayer()
            .checkLayerCount(9)
            .performAddLayer()
            .checkLayerCount(10)
    }

    @Test
    fun testAddAsManyLayersAsPossibleAndScrollToEndOfList() {
        LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
        var layerCount = 3
        while (true) {
            try {
                LayerMenuViewInteraction.onLayerMenuView()
                    .performAddLayer()
                    .checkLayerCount(layerCount)
                onView(withId(R.id.pocketpaint_layer_side_nav_button_add)).check(matches(isClickable()))
                layerCount++
            } catch (ignore: AssertionFailedError) {
                break
            }
        }
        assertIfLayerAddButtonIsDisabled()
        LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(--layerCount)
        onView(withId(R.id.pocketpaint_layer_side_nav_list)).perform(scrollToPosition<RecyclerView.ViewHolder>(99))
    }

    private fun createTestImageFile(): Uri {
        val bitmap = Bitmap.createBitmap(7000, 7000, Bitmap.Config.ARGB_8888)
        with(Canvas(bitmap)) {
            drawColor(Color.BLACK)
            drawBitmap(bitmap, 0f, 0f, null)
        }
        val imageFile = File(
            launchActivityRule.activity.getExternalFilesDir(null)!!.absolutePath,
            "loadImage.jpg"
        )
        val imageUri = Uri.fromFile(imageFile)
        launchActivityRule.activity.myContentResolver.openOutputStream(imageUri).use { fos ->
            Assert.assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos))
        }
        deletionFileList.add(imageFile)
        return imageUri
    }
}
