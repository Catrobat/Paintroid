/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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

import android.graphics.Paint
import android.graphics.PointF
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.test.annotation.UiThreadTest
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.helper.Vertex
import org.catrobat.paintroid.tools.implementation.DynamicLineTool
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.Perspective
import org.catrobat.paintroid.ui.viewholder.TopBarViewHolder
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.util.Deque

class DynamicLineToolTest {
    private val toolPaint = Mockito.mock(ToolPaint::class.java)
    private val commandManager = Mockito.mock(CommandManager::class.java)
    private var workspace = Mockito.mock(Workspace::class.java)
    private val brushToolOptions = Mockito.mock(BrushToolOptionsView::class.java)
    private val toolOptionsViewController = Mockito.mock(ToolOptionsViewController::class.java)
    private val contextCallback = Mockito.mock(ContextCallback::class.java)
    private lateinit var tool: DynamicLineTool
    private var screenWidth = 1920
    private var screenHeight = 1080
    private lateinit var idlingResource: CountingIdlingResource
    private val viewMock: View = Mockito.mock(View::class.java)

    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    object MockitoHelper {
        fun <T> anyObject(): T {
            Mockito.any<T>()
            return uninitialized()
        }
        @Suppress("UNCHECKED_CAST")
        fun <T> uninitialized(): T = null as T
    }

    @Before
    fun setUp() {
        idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        Mockito.`when`(workspace.perspective).thenReturn(Perspective(screenWidth, screenHeight))
        Mockito.`when`(workspace.width).thenReturn(screenWidth)
        Mockito.`when`(workspace.height).thenReturn(screenHeight)
        Mockito.`when`(workspace.contains(MockitoHelper.anyObject()))
            .thenAnswer { invocation ->
                val point = invocation.getArgument<PointF>(0)
                point.x >= 0 && point.y >= 0 && point.x < screenWidth && point.y < screenHeight
            }
        val paint = Paint()
        Mockito.`when`(toolPaint.paint).thenReturn(paint)
        val topBarLayout = launchActivityRule.activity.findViewById<ViewGroup>(R.id.pocketpaint_layout_top_bar)
        DynamicLineTool.topBarViewHolder = TopBarViewHolder(topBarLayout)
        val plusButton: ImageButton = launchActivityRule.activity.findViewById(R.id.pocketpaint_btn_top_plus)
        DynamicLineTool.topBarViewHolder!!.plusButton = plusButton
        tool = DynamicLineTool(
            brushToolOptions,
            contextCallback,
            toolOptionsViewController,
            toolPaint,
            workspace,
            idlingResource,
            commandManager,
            0
        )

        Mockito.`when`(brushToolOptions!!.getTopToolOptions()).thenReturn(viewMock)
        Mockito.`when`(brushToolOptions.getBottomToolOptions()).thenReturn(viewMock)
    }
    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    @UiThreadTest
    fun testTwoVerticesCreatedAfterFirstClick() {
        Assert.assertEquals(0, tool.vertexStack.size)

        var firstClickCoordinate = PointF(5f, 5f)
        tool.handleDown(firstClickCoordinate)
        tool.handleUp(firstClickCoordinate)

        Assert.assertEquals(2, tool.vertexStack.size)
    }

    @Test
    @UiThreadTest
    fun testLastVertexCenterChangesAfterClick() {
        var firstClickCoordinate = PointF(5f, 5f)
        tool.handleDown(firstClickCoordinate)
        tool.handleUp(firstClickCoordinate)

        var secondClickCoordinate = PointF(100f, 100f)
        tool.handleDown(secondClickCoordinate)
        tool.handleUp(secondClickCoordinate)

        Assert.assertEquals(2, tool.vertexStack.size)
        Assert.assertEquals(secondClickCoordinate, tool.vertexStack.last.vertexCenter)

        var thirdClickCoordinate = PointF(200f, 200f)
        tool.handleDown(thirdClickCoordinate)
        tool.handleUp(thirdClickCoordinate)

        Assert.assertEquals(2, tool.vertexStack.size)
        Assert.assertEquals(thirdClickCoordinate, tool.vertexStack.last.vertexCenter)
    }

    @Test
    @UiThreadTest
    fun testLastVertexCenterChangesAfterMove() {
        var firstClickCoordinate = PointF(5f, 5f)
        tool.handleDown(firstClickCoordinate)
        tool.handleUp(firstClickCoordinate)

        var secondClickCoordinate = PointF(100f, 100f)
        tool.handleDown(secondClickCoordinate)
        tool.handleUp(secondClickCoordinate)

        Assert.assertEquals(2, tool.vertexStack.size)
        Assert.assertEquals(secondClickCoordinate, tool.vertexStack.last.vertexCenter)

        tool.handleDown(secondClickCoordinate)

        var movingCoordinate = PointF(200f, 200f)
        tool.handleMove(movingCoordinate)
        tool.handleUp(movingCoordinate)

        Assert.assertEquals(2, tool.vertexStack.size)
        Assert.assertEquals(movingCoordinate, tool.vertexStack.last.vertexCenter)
    }

    @Test
    @UiThreadTest
    fun testFirstVertexCenterChangesAfterMove() {
        var firstClickCoordinate = PointF(5f, 5f)
        tool.handleDown(firstClickCoordinate)
        tool.handleUp(firstClickCoordinate)

        var secondClickCoordinate = PointF(100f, 100f)
        tool.handleDown(secondClickCoordinate)
        tool.handleUp(secondClickCoordinate)

        Assert.assertEquals(2, tool.vertexStack.size)
        Assert.assertEquals(firstClickCoordinate, tool.vertexStack.first.vertexCenter)

        tool.handleDown(firstClickCoordinate)

        var movingCoordinate = PointF(200f, 200f)
        tool.handleMove(movingCoordinate)
        tool.handleUp(movingCoordinate)

        Assert.assertEquals(2, tool.vertexStack.size)
        Assert.assertEquals(movingCoordinate, tool.vertexStack.first.vertexCenter)
    }

    @Test
    @UiThreadTest
    fun testPlusButtonIsVisibleAfterFirstDrawnPath() {
        var plusButtonVisibility = DynamicLineTool.topBarViewHolder?.plusButton?.visibility
        Assert.assertEquals(plusButtonVisibility, View.GONE)

        var tapDownCoordinate = PointF(5f, 5f)
        var tapUpCoordinate = PointF(10f, 10f)
        tool.handleDown(tapDownCoordinate)
        tool.handleUp(tapUpCoordinate)

        plusButtonVisibility = DynamicLineTool.topBarViewHolder?.plusButton?.visibility
        Assert.assertEquals(plusButtonVisibility, View.VISIBLE)
    }

    @Test
    @UiThreadTest
    fun testAddPathIsSetAfterClickingPlus() {
        Assert.assertEquals(false, tool.addNewPath)

        var firstClickCoordinate = PointF(5f, 5f)
        tool.handleDown(firstClickCoordinate)
        tool.handleUp(firstClickCoordinate)

        var secondClickCoordinate = PointF(100f, 100f)
        tool.handleDown(secondClickCoordinate)
        tool.handleUp(secondClickCoordinate)

        tool.onClickOnPlus()

        Assert.assertEquals(true, tool.addNewPath)
    }

    @Test
    @UiThreadTest
    fun testThreeVerticesCreatedAfterClickingPlus() {
        Assert.assertEquals(0, tool.vertexStack.size)

        var firstClickCoordinate = PointF(5f, 5f)
        tool.handleDown(firstClickCoordinate)
        tool.handleUp(firstClickCoordinate)

        var secondClickCoordinate = PointF(100f, 100f)
        tool.handleDown(secondClickCoordinate)
        tool.handleUp(secondClickCoordinate)

        tool.onClickOnPlus()

        var thirdClickCoordinate = PointF(200f, 200f)
        tool.handleDown(thirdClickCoordinate)
        tool.handleUp(thirdClickCoordinate)

        Assert.assertEquals(3, tool.vertexStack.size)
    }

    @Test
    @UiThreadTest
    fun testPlusButtonIsNotVisibleAfterClickingCheckmark() {
        var plusButtonVisibility = DynamicLineTool.topBarViewHolder?.plusButton?.visibility
        Assert.assertEquals(plusButtonVisibility, View.GONE)

        var tapDownCoordinate = PointF(5f, 5f)
        var tapUpCoordinate = PointF(10f, 10f)
        tool.handleDown(tapDownCoordinate)
        tool.handleUp(tapUpCoordinate)

        plusButtonVisibility = DynamicLineTool.topBarViewHolder?.plusButton?.visibility
        Assert.assertEquals(plusButtonVisibility, View.VISIBLE)

        tool.onClickOnButton()

        plusButtonVisibility = DynamicLineTool.topBarViewHolder?.plusButton?.visibility
        Assert.assertEquals(plusButtonVisibility, View.GONE)
    }

    @Test
    @UiThreadTest
    fun testLastEndpointIsNextStartPointAfterPlus() {
        var tapCoordinate = PointF(5f, 5f)
        tool.handleDown(tapCoordinate)
        tool.handleUp(tapCoordinate)

        tapCoordinate = PointF(50f, 50f)
        tool.handleDown(tapCoordinate)
        tool.handleUp(tapCoordinate)

        tool.onClickOnPlus()

        Assert.assertEquals(tapCoordinate, tool.vertexStack.last.vertexCenter)
    }

    @Test
    @UiThreadTest
    fun testFourVerticesCreatedAfterClickingPlus() {
        Assert.assertEquals(0, tool.vertexStack.size)

        var tapCoordinate = PointF(5f, 5f)
        tool.handleDown(tapCoordinate)
        tool.handleUp(tapCoordinate)

        tapCoordinate = PointF(100f, 100f)
        tool.handleDown(tapCoordinate)
        tool.handleUp(tapCoordinate)

        tool.onClickOnPlus()

        tapCoordinate = PointF(200f, 200f)
        tool.handleDown(tapCoordinate)
        tool.handleUp(tapCoordinate)

        tool.onClickOnPlus()

        tapCoordinate = PointF(300f, 300f)
        tool.handleDown(tapCoordinate)
        tool.handleUp(tapCoordinate)

        Assert.assertEquals(4, tool.vertexStack.size)
    }

    @Test
    @UiThreadTest
    fun testVertexStackIsClearedAfterCheckmark() {
        Assert.assertEquals(0, tool.vertexStack.size)

        var tapDownCoordinate = PointF(5f, 5f)
        var tapUpCoordinate = PointF(10f, 10f)
        tool.handleDown(tapDownCoordinate)
        tool.handleUp(tapUpCoordinate)

        Assert.assertEquals(2, tool.vertexStack.size)
        tool.onClickOnButton()
        Assert.assertEquals(0, tool.vertexStack.size)
    }

    @Test
    @UiThreadTest
    fun testClickOnVertexSetsMovingVertices() {
        Assert.assertEquals(null, tool.predecessorVertex)
        Assert.assertEquals(null, tool.movingVertex)
        Assert.assertEquals(null, tool.successorVertex)

        var firstVertexCoordinate = PointF(5f, 5f)
        tool.handleDown(firstVertexCoordinate)
        tool.handleUp(firstVertexCoordinate)

        var middleVertexCoordinate = PointF(100f, 100f)
        tool.handleDown(middleVertexCoordinate)
        tool.handleUp(middleVertexCoordinate)

        tool.onClickOnPlus()

        var lastVertexCoordinate = PointF(200f, 200f)
        tool.handleDown(lastVertexCoordinate)
        tool.handleUp(lastVertexCoordinate)

        tool.handleDown(middleVertexCoordinate)

        Assert.assertEquals(tool.vertexStack.first, tool.predecessorVertex)
        Assert.assertEquals(getElementAtIndex(tool.vertexStack, 1), tool.movingVertex)
        Assert.assertEquals(tool.vertexStack.last, tool.successorVertex)
    }

    @Test
    @UiThreadTest
    fun testMovingVerticesAreResetAfterCheckmark() {
        Assert.assertEquals(null, tool.predecessorVertex)
        Assert.assertEquals(null, tool.movingVertex)
        Assert.assertEquals(null, tool.successorVertex)

        var firstVertexCoordinate = PointF(5f, 5f)
        tool.handleDown(firstVertexCoordinate)
        tool.handleUp(firstVertexCoordinate)

        var middleVertexCoordinate = PointF(100f, 100f)
        tool.handleDown(middleVertexCoordinate)
        tool.handleUp(middleVertexCoordinate)

        tool.onClickOnPlus()

        var lastVertexCoordinate = PointF(200f, 200f)
        tool.handleDown(lastVertexCoordinate)
        tool.handleUp(lastVertexCoordinate)

        tool.handleDown(middleVertexCoordinate)

        Assert.assertEquals(tool.vertexStack.first, tool.predecessorVertex)
        Assert.assertEquals(getElementAtIndex(tool.vertexStack, 1), tool.movingVertex)
        Assert.assertEquals(tool.vertexStack.last, tool.successorVertex)

        tool.onClickOnButton()

        Assert.assertEquals(null, tool.predecessorVertex)
        Assert.assertEquals(null, tool.movingVertex)
        Assert.assertEquals(null, tool.successorVertex)
    }

    @Test
    @UiThreadTest
    fun testShouldCallHideWhenDrawing() {
        val tap1 = PointF(7f, 7f)
        Mockito.`when`(toolOptionsViewController.isVisible).thenReturn(true)
        Mockito.`when`(viewMock.visibility).thenReturn(View.VISIBLE)
        tool.handleMove(tap1)

        Mockito.verify(toolOptionsViewController).slideUp(viewMock,
                                                          willHide = true,
                                                          showOptionsView = false
        )
        Mockito.verify(toolOptionsViewController).slideDown(viewMock,
                                                            willHide = true,
                                                            showOptionsView = false
        )
    }

    @Test
    @UiThreadTest
    fun testShouldCallUnhideWhenDrawingFinish() {
        val tap1 = PointF(7f, 7f)
        Mockito.`when`(toolOptionsViewController.isVisible).thenReturn(false)
        Mockito.`when`(viewMock.visibility).thenReturn(View.INVISIBLE)
        tool.handleMove(tap1)
        tool.handleUp(tap1)

        Mockito.verify(toolOptionsViewController).slideDown(viewMock,
                                                            willHide = false,
                                                            showOptionsView = true
        )
        Mockito.verify(toolOptionsViewController).slideUp(viewMock,
                                                          willHide = false,
                                                          showOptionsView = true
        )
    }

    private fun getElementAtIndex(deque: Deque<Vertex>, index: Int): Vertex? {
        for ((currentIndex, element) in deque.withIndex()) {
            if (currentIndex == index) {
                return element
            }
        }
        return null
    }
}
