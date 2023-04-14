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
package org.catrobat.paintroid.tools.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.idling.CountingIdlingResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.SprayToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

private const val BUNDLE_RADIUS = "BUNDLE_RADIUS"
private const val DEFAULT_RADIUS = 30
private const val STROKE_WIDTH = 5f
private const val CONSTANT_1 = 0.5f

class SprayTool(
    var stampToolOptionsView: SprayToolOptionsView,
    override var contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager,
    override var drawTime: Long
) : BaseTool(contextCallback, toolOptionsViewController, toolPaint, workspace, idlingResource, commandManager) {

    @VisibleForTesting
    var sprayToolScope = CoroutineScope(Dispatchers.Main)

    @VisibleForTesting
    var sprayedPoints = ConcurrentLinkedQueue<PointF>()

    @VisibleForTesting
    var sprayActive = false

    override var toolType: ToolType = ToolType.SPRAY
    private var currentCoordinate: PointF? = null
    private var sprayRadius = DEFAULT_RADIUS
    private var previewBitmap: Bitmap =
        Bitmap.createBitmap(workspace.width, workspace.height, Bitmap.Config.ARGB_8888)
    private val previewCanvas = Canvas(previewBitmap)

    init {
        toolPaint.strokeWidth = STROKE_WIDTH

        stampToolOptionsView.setCallback(object : SprayToolOptionsView.Callback {
            override fun radiusChanged(radius: Int) {
                sprayRadius = DEFAULT_RADIUS + radius * 2
            }
        })

        stampToolOptionsView.setCurrentPaint(toolPaint.paint)
        toolOptionsViewController.showDelayed()
    }

    override fun draw(canvas: Canvas) {
        canvas.run {
            save()
            drawBitmap(previewBitmap, 0.0f, 0.0f, null)
            restore()
        }
    }

    private fun hideToolOptions() {
        if (toolOptionsViewController.isVisible &&
            toolOptionsViewController.toolSpecificOptionsLayout.visibility == View.VISIBLE) {
            toolOptionsViewController.slideUp(
                toolOptionsViewController.toolSpecificOptionsLayout,
                willHide = true,
                showOptionsView = false
            )
        }
    }

    private fun showToolOptions() {
        if (!toolOptionsViewController.isVisible &&
            toolOptionsViewController.toolSpecificOptionsLayout.visibility == View.INVISIBLE) {
            toolOptionsViewController.slideDown(
                toolOptionsViewController.toolSpecificOptionsLayout,
                willHide = false,
                showOptionsView = true
            )
        }
    }

    override fun handleUp(coordinate: PointF?): Boolean {
        sprayToolScope.cancel()
        currentCoordinate = coordinate
        addSprayCommand()

        showToolOptions()
        super.handleUp(coordinate)
        return true
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    override fun handleMove(coordinate: PointF?): Boolean {
        hideToolOptions()
        super.handleMove(coordinate)
        currentCoordinate = coordinate
        return true
    }

    override fun handleDown(coordinate: PointF?): Boolean {
        if (sprayActive || coordinate == null) {
            return false
        }
        super.handleDown(coordinate)
        sprayActive = true
        currentCoordinate = coordinate
        createSprayPatternAsync()
        return true
    }

    override fun onSaveInstanceState(bundle: Bundle?) {
        super.onSaveInstanceState(bundle)
        bundle?.putInt(BUNDLE_RADIUS, sprayRadius)
        sprayToolScope.cancel()
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        super.onRestoreInstanceState(bundle)
        bundle?.getInt(BUNDLE_RADIUS)?.let { radius ->
            sprayRadius = radius
            stampToolOptionsView.setRadius(radius)
        }
    }

    private fun addSprayCommand() {
        val pointsList = mutableListOf<PointF>().apply {
            addAll(sprayedPoints)
        }

        val pointsArray = FloatArray(pointsList.size * 2)

        pointsList.forEachIndexed { index, point ->
            pointsArray[index * 2] = point.x
            pointsArray[index * 2 + 1] = point.y
        }

        val command = commandFactory.createSprayCommand(pointsArray, drawPaint)
        commandManager.addCommand(command)
    }

    private fun createSprayPatternAsync() {
        sprayToolScope = CoroutineScope(Dispatchers.Default)
        sprayToolScope.launch {
            while (true) {
                repeat(sprayRadius / DEFAULT_RADIUS) {
                    val point = createRandomPointInCircle()
                    if (workspace.contains(point)) {
                        previewCanvas.drawPoint(point.x, point.y, drawPaint)
                        sprayedPoints.add(point)
                    }
                }

                delay(1)
            }
        }
    }

    override fun resetInternalState() {
        sprayToolScope.cancel()
        sprayActive = false
        sprayedPoints.clear()
        previewBitmap.eraseColor(Color.TRANSPARENT)
    }

    private fun createRandomPointInCircle(): PointF {
        val point = PointF()
        val radius = sprayRadius * Random.nextFloat().pow(CONSTANT_1)
        val theta = Random.nextFloat() * 2f * Math.PI

        point.x = radius * cos(theta).toFloat() + (currentCoordinate?.x ?: 0f)
        point.y = radius * sin(theta).toFloat() + (currentCoordinate?.y ?: 0f)
        return point
    }
}
