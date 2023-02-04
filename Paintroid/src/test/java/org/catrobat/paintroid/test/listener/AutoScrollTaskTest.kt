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
package org.catrobat.paintroid.test.listener

import android.graphics.Point
import android.graphics.PointF
import android.os.Handler
import org.catrobat.paintroid.listener.DrawingSurfaceListener.AutoScrollTask
import org.catrobat.paintroid.listener.DrawingSurfaceListener.AutoScrollTaskCallback
import org.catrobat.paintroid.test.utils.PointFAnswer
import org.catrobat.paintroid.test.utils.PointFMatcher.Companion.pointFEquals
import org.catrobat.paintroid.tools.ToolType
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AutoScrollTaskTest {
    @Mock
    private val handler: Handler? = null

    @Mock
    private val callback: AutoScrollTaskCallback? = null

    @InjectMocks
    private val autoScrollTask: AutoScrollTask? = null
    @Test
    fun testSetUp() {
        Mockito.verifyZeroInteractions(handler, callback)
        Assert.assertFalse(autoScrollTask!!.isRunning)
    }

    @Test
    fun testRun() {
        val autoScrollDirection = Mockito.mock(
            Point::class.java
        )
        Mockito.`when`(
            callback!!.getToolAutoScrollDirection(
                ArgumentMatchers.anyFloat(),
                ArgumentMatchers.anyFloat(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
            )
        )
            .thenReturn(autoScrollDirection)
        autoScrollTask!!.run()
        Mockito.verify(callback).getToolAutoScrollDirection(
            ArgumentMatchers.anyFloat(),
            ArgumentMatchers.anyFloat(),
            ArgumentMatchers.anyInt(),
            ArgumentMatchers.anyInt()
        )
        Mockito.verify(handler)
            ?.postDelayed(ArgumentMatchers.eq(autoScrollTask), ArgumentMatchers.anyLong())
    }

    @Test
    fun testRunAutoScrollLeft() {
        val autoScrollDirection = Mockito.mock(
            Point::class.java
        )
        autoScrollDirection.x = -1
        Mockito.`when`(callback!!.getToolAutoScrollDirection(3f, 5f, 39, 42))
            .thenReturn(autoScrollDirection)
        Mockito.`when`(callback.isPointOnCanvas(7, 11)).thenReturn(true)
        PointFAnswer.setPointFTo(7f, 11f).`when`<AutoScrollTaskCallback?>(callback)
            .convertToCanvasFromSurface(pointFEquals(3f, 5f))
        autoScrollTask!!.setEventPoint(3f, 5f)
        autoScrollTask.setViewDimensions(39, 42)
        autoScrollTask.run()
        Mockito.verify(callback).translatePerspective(-1 * 2f, 0f)
        Mockito.verify<AutoScrollTaskCallback?>(callback).handleToolMove(pointFEquals(7f, 11f))
        Mockito.verify(callback).refreshDrawingSurface()
        Mockito.verify(handler)
            ?.postDelayed(ArgumentMatchers.eq(autoScrollTask), ArgumentMatchers.anyLong())
    }

    @Test
    fun testRunAutoScrollLeftWhenNotOnCanvas() {
        val autoScrollDirection = Mockito.mock(
            Point::class.java
        )
        autoScrollDirection.x = -1
        Mockito.`when`(callback!!.getToolAutoScrollDirection(3f, 5f, 39, 42))
            .thenReturn(autoScrollDirection)
        PointFAnswer.setPointFTo(7f, 11f).`when`<AutoScrollTaskCallback?>(callback)
            .convertToCanvasFromSurface(pointFEquals(3f, 5f))
        autoScrollTask!!.setEventPoint(3f, 5f)
        autoScrollTask.setViewDimensions(39, 42)
        autoScrollTask.run()
        Mockito.verify(callback, Mockito.never())
            .translatePerspective(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
        verify(callback, never()).handleToolMove(any<PointF>())
        Mockito.verify(callback, Mockito.never()).refreshDrawingSurface()
        Mockito.verify(handler)
            ?.postDelayed(ArgumentMatchers.eq(autoScrollTask), ArgumentMatchers.anyLong())
    }

    @Test
    fun testRunAutoScrollUp() {
        val autoScrollDirection = Mockito.mock(
            Point::class.java
        )
        autoScrollDirection.y = -1
        Mockito.`when`(callback!!.getToolAutoScrollDirection(3f, 5f, 39, 42))
            .thenReturn(autoScrollDirection)
        Mockito.`when`(callback.isPointOnCanvas(7, 11)).thenReturn(true)
        PointFAnswer.setPointFTo(7f, 11f).`when`<AutoScrollTaskCallback?>(callback)
            .convertToCanvasFromSurface(pointFEquals(3f, 5f))
        autoScrollTask!!.setEventPoint(3f, 5f)
        autoScrollTask.setViewDimensions(39, 42)
        autoScrollTask.run()
        Mockito.verify(callback).translatePerspective(0f, -1 * 2f)
        Mockito.verify<AutoScrollTaskCallback?>(callback).handleToolMove(pointFEquals(7f, 11f))
        Mockito.verify(callback).refreshDrawingSurface()
        Mockito.verify(handler)
            ?.postDelayed(ArgumentMatchers.eq(autoScrollTask), ArgumentMatchers.anyLong())
    }

    @Test
    fun testRunAutoScrollUpWhenNotOnCanvas() {
        val autoScrollDirection = Mockito.mock(
            Point::class.java
        )
        autoScrollDirection.y = -1
        Mockito.`when`(callback!!.getToolAutoScrollDirection(3f, 5f, 39, 42))
            .thenReturn(autoScrollDirection)
        PointFAnswer.setPointFTo(7f, 11f).`when`<AutoScrollTaskCallback?>(callback)
            .convertToCanvasFromSurface(pointFEquals(3f, 5f))
        autoScrollTask!!.setEventPoint(3f, 5f)
        autoScrollTask.setViewDimensions(39, 42)
        autoScrollTask.run()
        Mockito.verify(callback, Mockito.never())
            .translatePerspective(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
        verify(callback, never()).handleToolMove(any<PointF>())
        Mockito.verify(callback, Mockito.never()).refreshDrawingSurface()
        Mockito.verify(handler)
            ?.postDelayed(ArgumentMatchers.eq(autoScrollTask), ArgumentMatchers.anyLong())
    }

    @Test
    fun testStop() {
        autoScrollTask!!.stop()
        Mockito.verifyZeroInteractions(handler, callback)
        Assert.assertFalse(autoScrollTask.isRunning)
    }

    @Test
    fun testStart() {
        autoScrollTask!!.setEventPoint(3f, 5f)
        autoScrollTask.setViewDimensions(39, 42)
        val autoScrollDirection = Mockito.mock(
            Point::class.java
        )
        Mockito.`when`(
            callback!!.getToolAutoScrollDirection(
                ArgumentMatchers.anyFloat(),
                ArgumentMatchers.anyFloat(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
            )
        )
            .thenReturn(autoScrollDirection)
        autoScrollTask.start()
        Mockito.verify(handler)
            ?.postDelayed(ArgumentMatchers.eq(autoScrollTask), ArgumentMatchers.anyLong())
        Assert.assertTrue(autoScrollTask.isRunning)
    }

    @Test(expected = IllegalStateException::class)
    fun testStartWhenAlreadyRunning() {
        autoScrollTask!!.setEventPoint(3f, 5f)
        autoScrollTask.setViewDimensions(39, 42)
        val autoScrollDirection = Mockito.mock(
            Point::class.java
        )
        Mockito.`when`(
            callback!!.getToolAutoScrollDirection(
                ArgumentMatchers.anyFloat(),
                ArgumentMatchers.anyFloat(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
            )
        )
            .thenReturn(autoScrollDirection)
        autoScrollTask.start()
        autoScrollTask.start()
    }

    @Test(expected = IllegalStateException::class)
    fun testStartWithZeroWidth() {
        autoScrollTask!!.setViewDimensions(0, 42)
        autoScrollTask.start()
    }

    @Test(expected = IllegalStateException::class)
    fun testStartWithZeroHeight() {
        autoScrollTask!!.setViewDimensions(42, 0)
        autoScrollTask.start()
    }

    @Test
    fun testStartIgnoredTools() {
        Mockito.`when`(callback!!.getCurrentToolType())
            .thenReturn(ToolType.FILL, ToolType.TRANSFORM)
        autoScrollTask!!.setEventPoint(3f, 5f)
        autoScrollTask.setViewDimensions(39, 42)
        autoScrollTask.start()
        autoScrollTask.start()
        autoScrollTask.start()
        Assert.assertFalse(autoScrollTask.isRunning)
        Mockito.verifyZeroInteractions(handler)
    }

    @Test
    fun testStopAfterStart() {
        autoScrollTask!!.setEventPoint(3f, 5f)
        autoScrollTask.setViewDimensions(39, 42)
        val autoScrollDirection = Mockito.mock(
            Point::class.java
        )
        Mockito.`when`(
            callback!!.getToolAutoScrollDirection(
                ArgumentMatchers.anyFloat(),
                ArgumentMatchers.anyFloat(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
            )
        )
            .thenReturn(autoScrollDirection)
        autoScrollTask.start()
        autoScrollTask.stop()
        Mockito.verify(handler)
            ?.postDelayed(ArgumentMatchers.eq(autoScrollTask), ArgumentMatchers.anyLong())
        Mockito.verify(handler)?.removeCallbacks(autoScrollTask)
        Assert.assertFalse(autoScrollTask.isRunning)
    }
}