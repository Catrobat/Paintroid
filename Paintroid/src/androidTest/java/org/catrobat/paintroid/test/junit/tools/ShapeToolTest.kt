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

import android.util.DisplayMetrics
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.drawable.DrawableShape
import org.catrobat.paintroid.tools.implementation.ShapeTool
import org.catrobat.paintroid.tools.options.ShapeToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.Perspective
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit

@RunWith(Parameterized::class)
class ShapeToolTest {
    @Rule
    @JvmField
    var mockito = MockitoJUnit.rule()

    @JvmField
    @Parameterized.Parameter
    var shape: DrawableShape? = null

    @Mock
    private val commandManager: CommandManager? = null

    @Mock
    private val shapeToolOptions: ShapeToolOptionsView? = null

    @Mock
    private val toolOptionsViewController: ToolOptionsViewController? = null

    @Mock
    private val contextCallback: ContextCallback? = null

    @Mock
    private val workspace: Workspace? = null

    @Mock
    private val toolPaint: ToolPaint? = null

    @Mock
    private val displayMetrics: DisplayMetrics? = null
    private var shapeTool: ShapeTool? = null
    private var idlingResource: CountingIdlingResource? = null

    @Rule
    @JvmField
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        Mockito.`when`(workspace!!.width).thenReturn(100)
        Mockito.`when`(workspace.height).thenReturn(100)
        Mockito.`when`(workspace.scale).thenReturn(1f)
        Mockito.`when`(workspace.perspective).thenReturn(Perspective(100, 100))
        Mockito.`when`(contextCallback!!.displayMetrics).thenReturn(displayMetrics)
        displayMetrics!!.widthPixels = 100
        displayMetrics.heightPixels = 100
        shapeTool = ShapeTool(
            shapeToolOptions!!,
            contextCallback, toolOptionsViewController!!, toolPaint!!, workspace,
            idlingResource!!, commandManager!!, 0
        )
        shapeTool!!.setBaseShape(shape!!)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testShouldReturnCorrectToolType() {
        val toolType = shapeTool!!.toolType
        Assert.assertEquals(ToolType.SHAPE, toolType)
    }

    @Test
    fun testShouldReturnCorrectBaseShape() {
        val baseShape = shapeTool!!.getBaseShape()
        Assert.assertEquals(shape, baseShape)
    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<DrawableShape> {
            return listOf(
                DrawableShape.RECTANGLE,
                DrawableShape.OVAL,
                DrawableShape.HEART,
                DrawableShape.STAR
            )
        }
    }
}
