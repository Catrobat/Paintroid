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
package org.catrobat.paintroid.listener

import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.tools.Tool.StateChange
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.DrawingSurface
import kotlin.collections.ArrayList
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.math.hypot

private const val DRAWER_EDGE_SIZE = 20f
private const val CONSTANT_1 = 0.5f
private const val JITTER_DELAY_THRESHOLD: Long = 30
private const val JITTER_DISTANCE_THRESHOLD = 50f

open class DrawingSurfaceListener(
    private val callback: DrawingSurfaceListenerCallback,
    private val displayDensity: Float
) : OnTouchListener {
    private var touchMode: TouchMode
    private var pointerDistance = 0f
    private var xMidPoint = 0f
    private var yMidPoint = 0f
    private var eventX = 0f
    private var eventY = 0f
    private val canvasTouchPoint: PointF
    private val eventTouchPoint: PointF
    private val drawerEdgeSize: Int = (DRAWER_EDGE_SIZE * displayDensity + CONSTANT_1).toInt()
    private var timerStartDraw = 0.toLong()

    private var recentTouchEventsData: MutableList<TouchEventData> = mutableListOf()

    private data class TouchEventData constructor(val timeStamp: Long, val xCoordinate: Float, val yCoordinate: Float)

    internal enum class TouchMode {
        DRAW, PINCH
    }

    init {
        touchMode = TouchMode.DRAW
        canvasTouchPoint = PointF()
        eventTouchPoint = PointF()
    }

    private fun newHandEvent(x: Float, y: Float) {
        eventX = x
        eventY = y
    }

    private fun calculatePointerDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return hypot(x, y)
    }

    private fun calculateMidPoint(event: MotionEvent) {
        xMidPoint = (event.getX(0) + event.getX(1)) / 2f
        yMidPoint = (event.getY(0) + event.getY(1)) / 2f
    }

    private fun handleActionMove(currentTool: Tool?, event: MotionEvent) {
        val xOld: Float
        val yOld: Float
        if (event.pointerCount == 1) {
            currentTool ?: return
            recentTouchEventsData.add(TouchEventData(event.eventTime, event.x, event.y))
            removeObsoleteTouchEventsData(event.eventTime)
            if (currentTool.handToolMode()) {
                if (touchMode == TouchMode.PINCH) {
                    xOld = 0f
                    yOld = 0f
                    touchMode = TouchMode.DRAW
                } else {
                    xOld = eventX
                    yOld = eventY
                }
                newHandEvent(event.x, event.y)
                if (xOld > 0 && eventX != xOld || yOld > 0 && eventY != yOld) {
                    callback.translatePerspective(eventX - xOld, eventY - yOld)
                }
            } else if (touchMode != TouchMode.PINCH) {
                touchMode = TouchMode.DRAW
                currentTool.handleMove(canvasTouchPoint)
            }
        } else {
            if (touchMode == TouchMode.DRAW) {
                currentTool?.resetInternalState(StateChange.MOVE_CANCELED)
            }
            touchMode = TouchMode.PINCH
            val pointerDistanceOld = pointerDistance
            pointerDistance = calculatePointerDistance(event)
            if (pointerDistanceOld > 0 && pointerDistanceOld != pointerDistance) {
                val scale = pointerDistance / pointerDistanceOld
                callback.multiplyPerspectiveScale(scale)
            }
            xOld = xMidPoint
            yOld = yMidPoint
            calculateMidPoint(event)
            if (xOld > 0 && xMidPoint != xOld || yOld > 0 && yMidPoint != yOld) {
                callback.translatePerspective(xMidPoint - xOld, yMidPoint - yOld)
            }
        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val drawingSurface = view as DrawingSurface
        val currentTool = callback.getCurrentTool()
        canvasTouchPoint.x = event.x
        canvasTouchPoint.y = event.y
        eventTouchPoint.x = canvasTouchPoint.x
        eventTouchPoint.y = canvasTouchPoint.y
        callback.convertToCanvasFromSurface(canvasTouchPoint)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (eventTouchPoint.x < drawerEdgeSize || view.getWidth() - eventTouchPoint.x < drawerEdgeSize) {
                    return false
                }
                timerStartDraw = System.currentTimeMillis()
                recentTouchEventsData.add(TouchEventData(event.eventTime, event.x, event.y))
                currentTool?.handleDown(canvasTouchPoint)
            }
            MotionEvent.ACTION_MOVE -> handleActionMove(currentTool, event)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (touchMode == TouchMode.DRAW) {
                    val drawingTime = System.currentTimeMillis() - timerStartDraw
                    removeObsoleteTouchEventsData(event.eventTime)
                    var dX = 0f
                    var dY = 0f
                    if (recentTouchEventsData.size > 1) {
                        val oldestEntry = recentTouchEventsData[0]
                        val distanceCorrectionX = event.x - oldestEntry.xCoordinate
                        val distanceCorrectionY = event.y - oldestEntry.yCoordinate
                        val distance = distanceCorrectionX * distanceCorrectionX + distanceCorrectionY * distanceCorrectionY
                        if (distance < JITTER_DISTANCE_THRESHOLD * displayDensity && distance != 0f) {
                            dX = distanceCorrectionX
                            dY = distanceCorrectionY
                        }
                    }
                    canvasTouchPoint.x = event.x - dX
                    canvasTouchPoint.y = event.y - dY
                    callback.convertToCanvasFromSurface(canvasTouchPoint)
                    currentTool?.drawTime = drawingTime
                    currentTool?.handleUp(canvasTouchPoint)
                } else {
                    currentTool?.resetInternalState(StateChange.MOVE_CANCELED)
                }
                pointerDistance = 0f
                xMidPoint = 0f
                yMidPoint = 0f
                eventX = 0f
                eventY = 0f
                touchMode = TouchMode.DRAW
            }
        }
        drawingSurface.refreshDrawingSurface()
        return true
    }

    private fun removeObsoleteTouchEventsData(timeStamp: Long) {
        val obsoleteTouchEventsData: MutableList<TouchEventData> = ArrayList()
        for (touchEventData in recentTouchEventsData) {
            if (timeStamp - touchEventData.timeStamp > JITTER_DELAY_THRESHOLD) {
                obsoleteTouchEventsData.add(touchEventData)
            } else {
                break
            }
        }
        recentTouchEventsData.removeAll(obsoleteTouchEventsData)
    }

    interface DrawingSurfaceListenerCallback {
        fun getCurrentTool(): Tool?

        fun multiplyPerspectiveScale(factor: Float)

        fun translatePerspective(x: Float, y: Float)

        fun convertToCanvasFromSurface(surfacePoint: PointF)

        fun getToolOptionsViewController(): ToolOptionsViewController
    }
}
