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
package org.catrobat.paintroid.test.junit.tools

import android.graphics.Bitmap
import android.graphics.PointF
import android.os.Looper
import android.util.DisplayMetrics
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.nhaarman.mockitokotlin2.any
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.implementation.ClipboardTool
import org.catrobat.paintroid.tools.options.ClipboardToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.Perspective
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ClipboardToolTest {
    @Mock
    private val toolPaint: ToolPaint? = null

    @Mock
    private val commandManager: CommandManager? = null

    @Mock
    private val workspace: Workspace? = null

    @Mock
    private val clipboardToolOptionsView: ClipboardToolOptionsView? = null

    @Mock
    private val toolOptionsViewController: ToolOptionsViewController? = null

    @Mock
    private val contextCallback: ContextCallback? = null

    @Mock
    private val displayMetrics: DisplayMetrics? = null
    private var tool: ClipboardTool? = null
    private var idlingResource: CountingIdlingResource? = null

    private val viewMock = Mockito.mock(ViewGroup::class.java)
    @Rule
    @JvmField
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        Mockito.`when`(contextCallback!!.displayMetrics).thenReturn(displayMetrics)
        displayMetrics!!.widthPixels = 200
        displayMetrics.heightPixels = 300
        Mockito.`when`(workspace!!.scale).thenReturn(1f)
        Mockito.`when`(workspace.width).thenReturn(200)
        Mockito.`when`(workspace.height).thenReturn(300)
        Mockito.`when`(workspace.perspective).thenReturn(Perspective(200, 300))
        Mockito.`when`(workspace.getCanvasPointFromSurfacePoint(any<PointF>()))
            .then { invocation -> invocation.getArgument<PointF>(0) }
        tool = ClipboardTool(
            clipboardToolOptionsView!!,
            contextCallback,
            toolOptionsViewController!!,
            toolPaint!!,
            workspace,
            idlingResource!!,
            commandManager!!,
            0
        )
        Mockito.`when`(toolOptionsViewController.toolSpecificOptionsLayout).thenReturn(viewMock)
        Mockito.`when`(toolOptionsViewController.toolSpecificOptionsLayout).thenReturn(viewMock)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    @Throws(InterruptedException::class)
    fun testLongClickResetsToolPosition() {
        Mockito.`when`(workspace!!.bitmapOfCurrentLayer).thenReturn(
            Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        )
        val initialX = tool!!.toolPosition.x
        val initialY = tool!!.toolPosition.y
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            tool!!.handleDown(PointF(initialX, initialY))
            tool!!.handleMove(PointF(initialX + 2, initialY + 3))
        }
        Assert.assertEquals(initialX + 2, tool!!.toolPosition.x, Float.MIN_VALUE)
        Assert.assertEquals(initialY + 3, tool!!.toolPosition.y, Float.MIN_VALUE)
        Thread.sleep((ViewConfiguration.getLongPressTimeout() + 50).toLong())
        Assert.assertEquals(initialX + 2, tool!!.toolPosition.x, Float.MIN_VALUE)
        Assert.assertEquals(initialY + 3, tool!!.toolPosition.y, Float.MIN_VALUE)
    }

    @Test
    fun testShouldReturnCorrectToolType() {
        Assert.assertEquals(ToolType.CLIPBOARD, tool!!.toolType)
    }

    @Test
    fun testToolPreciseMovementTest() {
        Looper.prepare()
        val initialToolPositionX = tool!!.toolPosition.x
        val initialToolPositionY = tool!!.toolPosition.y
        tool!!.handleDown(PointF(initialToolPositionX, initialToolPositionY))
        tool!!.handleMove(PointF(initialToolPositionX + 9, initialToolPositionY + 9))
        tool!!.handleUp(PointF(initialToolPositionX + 9, initialToolPositionY + 9))
        Assert.assertEquals(tool!!.toolPosition.x, initialToolPositionX + 9, 0f)
        Assert.assertEquals(tool!!.toolPosition.y, initialToolPositionY + 9, 0f)
    }
}
