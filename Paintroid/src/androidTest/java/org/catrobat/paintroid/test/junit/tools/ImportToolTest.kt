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

import org.junit.runner.RunWith
import org.mockito.Mock
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.tools.ContextCallback
import android.util.DisplayMetrics
import org.catrobat.paintroid.tools.implementation.ImportTool
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import androidx.test.annotation.UiThreadTest
import org.junit.Before
import androidx.test.espresso.IdlingRegistry
import org.mockito.Mockito
import org.catrobat.paintroid.ui.Perspective
import android.graphics.Bitmap
import org.catrobat.paintroid.tools.implementation.DEFAULT_BOX_RESIZE_MARGIN
import org.catrobat.paintroid.tools.implementation.MAXIMUM_BORDER_RATIO
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner.Silent::class)
class ImportToolTest {
    @Mock
    private lateinit var commandManager: CommandManager

    @Mock
    private lateinit var workspace: Workspace

    @Mock
    private lateinit var toolPaint: ToolPaint

    @Mock
    private lateinit var toolOptionsViewController: ToolOptionsViewController

    @Mock
    private lateinit var contextCallback: ContextCallback

    @Mock
    private val displayMetrics: DisplayMetrics? = null
    private var drawingSurfaceWidth = 0
    private var drawingSurfaceHeight = 0
    private lateinit var tool: ImportTool
    private lateinit var idlingResource: CountingIdlingResource

    @Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @UiThreadTest
    @Before
    fun setUp() {
        idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        drawingSurfaceWidth = 20
        drawingSurfaceHeight = 30
        displayMetrics?.heightPixels = 1920
        displayMetrics?.widthPixels = 1080
        displayMetrics?.density = 1f
        Mockito.`when`(contextCallback.displayMetrics).thenReturn(displayMetrics)
        Mockito.`when`(workspace.width).thenReturn(20)
        Mockito.`when`(workspace.height).thenReturn(30)
        Mockito.`when`(workspace.scale).thenReturn(1f)
        Mockito.`when`(workspace.perspective).thenReturn(Perspective(20, 30))
        tool = ImportTool(contextCallback, toolOptionsViewController, toolPaint, workspace,
                          idlingResource, commandManager, 0)
    }

    @After
    fun tearDown() { IdlingRegistry.getInstance().unregister(idlingResource) }

    @Test
    fun testImport() {
        val width = drawingSurfaceWidth
        val height = drawingSurfaceHeight
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        tool.setBitmapFromSource(bitmap)
        tool.boxWidth.let { Assert.assertEquals(width.toFloat(), it, Float.MIN_VALUE) }
        tool.boxHeight.let { Assert.assertEquals(height.toFloat(), it, Float.MIN_VALUE) }
    }

    @Test
    fun testImportTooSmall() {
        val width = 1
        val height = 1
        val minSize: Int = DEFAULT_BOX_RESIZE_MARGIN
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        tool.setBitmapFromSource(bitmap)
        tool.boxWidth.let { Assert.assertEquals(minSize.toFloat(), it, Float.MIN_VALUE) }
        tool.boxHeight.let { Assert.assertEquals(minSize.toFloat(), it, Float.MIN_VALUE) }
    }

    @Test
    fun testImportTooLarge() {
        val width = (drawingSurfaceWidth * MAXIMUM_BORDER_RATIO).toInt()
        val height = (drawingSurfaceHeight * MAXIMUM_BORDER_RATIO).toInt()
        val bitmap = Bitmap.createBitmap(width + 1, height + 1, Bitmap.Config.ARGB_8888)
        tool.setBitmapFromSource(bitmap)
        tool.boxWidth.let { Assert.assertEquals(width.toFloat(), it, Float.MIN_VALUE) }
        tool.let { Assert.assertEquals(height.toFloat(), it.boxHeight, Float.MIN_VALUE) }
    }

    @Test
    fun testImportGetsDownscaledWhenNotEnoughMemory() {
        drawingSurfaceWidth = 1080
        drawingSurfaceHeight = 1920
        Mockito.`when`(workspace.height).thenReturn(1920)
        Mockito.`when`(workspace.width).thenReturn(1080)
        val width = drawingSurfaceWidth * 8
        val height = drawingSurfaceHeight * 8
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        tool.setBitmapFromSource(bitmap)
        Assert.assertTrue(tool.boxHeight < height)
        Assert.assertTrue(tool.boxWidth < width)
    }
}
