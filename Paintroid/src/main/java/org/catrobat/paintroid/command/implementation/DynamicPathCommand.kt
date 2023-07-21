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

package org.catrobat.paintroid.command.implementation

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.Log
import androidx.annotation.ColorInt
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.implementation.DynamicLineTool
import java.util.Deque

class DynamicPathCommand(var paint: Paint, path: Path, startPoint: PointF, endPoint: PointF) : Command {

    var path = path
    var startPoint: PointF? = startPoint
    var endPoint: PointF? = endPoint
    var isSourcePath: Boolean = false

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        canvas.drawPath(path, paint)
    }

    override fun freeResources() {
        // No resources to free
    }

    fun updatePath(newPath: Path) {
        this.path = newPath
    }

    fun setAsSourcePath() {
        this.isSourcePath = true
    }

    fun setPaintColor(@ColorInt newColor: Int) {
        this.paint.color = newColor
    }

    fun setPaintStrokeWidth(newStrokeWidth: Float) {
        this.paint.strokeWidth = newStrokeWidth
    }

    fun setPaintStrokeCap(newCap: Paint.Cap) {
        this.paint.strokeCap = newCap
    }

    fun setStartAndEndPoint(startPoint: PointF?, endPoint: PointF?) {
        this.startPoint = startPoint
        this.endPoint = endPoint
    }


    fun setupVertexStackAfterUndo(mainActivity: MainActivity, dynamicPathCommands: Deque<DynamicPathCommand>) {
        mainActivity.runOnUiThread {
            mainActivity.defaultToolController.switchTool(ToolType.DYNAMICLINE)
            var tool = mainActivity.defaultToolController.currentTool as DynamicLineTool
            tool.createSourceAndDestinationVertices(
                dynamicPathCommands.first.startPoint,
                dynamicPathCommands.first.endPoint,
                dynamicPathCommands.first)
            dynamicPathCommands.removeFirst()
            for (command in dynamicPathCommands) {
                tool.createDestinationVertex(command.endPoint, command)
            }
            mainActivity.commandManager.executeAllCommands()
        }
    }
}
