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

package org.catrobat.paintroid.test.junit.tools

import android.graphics.Bitmap
import android.graphics.PointF
import android.os.Looper
import android.util.DisplayMetrics
import android.view.ViewConfiguration
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.implementation.StampTool
import org.catrobat.paintroid.tools.options.StampToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.Perspective
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import kotlin.Throws

@RunWith(MockitoJUnitRunner::class)
class StampToolTest {
    @Mock
    private lateinit var toolPaint: ToolPaint

    @Mock
    private lateinit var commandManager: CommandManager

    @Mock
    private lateinit var workspace: Workspace

    @Mock
    private lateinit var stampToolOptions: StampToolOptionsView

    @Mock
    private lateinit var toolOptionsViewController: ToolOptionsViewController

    @Mock
    private lateinit var contextCallback: ContextCallback

    @Mock
    private val displayMetrics: DisplayMetrics? = null
    private var tool: StampTool? = null
    private lateinit var idlingResource: CountingIdlingResource

    @Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        Mockito.`when`(contextCallback.displayMetrics).thenReturn(displayMetrics)
        displayMetrics?.widthPixels = 200
        displayMetrics?.heightPixels = 300
        Mockito.`when`(workspace.scale).thenReturn(1f)
        Mockito.`when`(workspace.width).thenReturn(200)
        Mockito.`when`(workspace.height).thenReturn(300)
        Mockito.`when`(workspace.perspective).thenReturn(Perspective(200, 300))
        Mockito.`when`(
            workspace.getCanvasPointFromSurfacePoint(ArgumentMatchers.any(PointF::class.java))
        ).then { invocation -> invocation.getArgument<PointF>(0) }
        tool = StampTool(
            stampToolOptions,
            contextCallback,
            toolOptionsViewController,
            toolPaint,
            workspace,
            idlingResource,
            commandManager, 0
        )
    }

    @After
    fun tearDown() { IdlingRegistry.getInstance().unregister(idlingResource) }

    @Test
    @Throws(InterruptedException::class)
    fun testLongClickResetsToolPosition() {
        Mockito.`when`(
            workspace.bitmapOfCurrentLayer
        ).thenReturn(Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888))
        val initialX = tool?.toolPosition?.x
        val initialY = tool?.toolPosition?.y
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            tool?.handleDown(initialX?.let { initialY?.let { it1 -> PointF(it, it1) } })
            if (initialX != null && initialY != null) {
                tool?.handleMove(PointF(initialX + 2, initialY + 3))
            }
        }
        if (initialX != null) { tool?.toolPosition?.x?.let { Assert.assertEquals(initialX + 2, it, Float.MIN_VALUE) } }
        if (initialY != null) { tool?.toolPosition?.y?.let { Assert.assertEquals(initialY + 3, it, Float.MIN_VALUE) } }
        Thread.sleep((ViewConfiguration.getLongPressTimeout() + 50).toLong())
        if (initialX != null) { tool?.toolPosition?.x?.let { Assert.assertEquals(initialX + 2, it, Float.MIN_VALUE) } }
        tool?.toolPosition?.y?.let { if (initialY != null) { Assert.assertEquals(initialY + 3, it, Float.MIN_VALUE) } }
    }

    @Test
    fun testShouldReturnCorrectToolType() { Assert.assertEquals(ToolType.STAMP, tool?.toolType) }

    @Test
    fun testToolPreciseMovementTest() {
        Looper.prepare()
        val initialToolPositionX = tool?.toolPosition?.x
        val initialToolPositionY = tool?.toolPosition?.y
        tool?.handleDown(initialToolPositionX?.let { initialToolPositionY?.let { it1 -> PointF(it, it1) } })
        if (initialToolPositionX != null && initialToolPositionY != null) {
            tool?.handleMove(PointF(initialToolPositionX + 9, initialToolPositionY + 9))
        }
        if (initialToolPositionX != null && initialToolPositionY != null) {
            tool?.handleUp(PointF(initialToolPositionX + 9, initialToolPositionY + 9))
        }
        tool?.toolPosition?.x?.let {
            if (initialToolPositionX != null) {
                Assert.assertEquals(it, initialToolPositionX + 9, 0f)
            }
        }
        tool?.toolPosition?.y?.let {
            if (initialToolPositionY != null) {
                Assert.assertEquals(it, initialToolPositionY + 9, 0f)
            }
        }
    }
}
