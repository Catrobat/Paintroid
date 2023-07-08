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
package org.catrobat.paintroid.tools.implementation

import android.graphics.Color
import android.graphics.Paint
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.viewholder.BottomNavigationViewHolder

class EraserTool(
        brushToolOptionsView: BrushToolOptionsView,
        contextCallback: ContextCallback,
        toolOptionsViewController: ToolOptionsViewController,
        toolPaint: ToolPaint,
        workspace: Workspace,
        idlingResource: CountingIdlingResource,
        commandManager: CommandManager,
        bottomNavigationViewHolder: BottomNavigationViewHolder,
        drawTime: Long
) : BrushTool(
    brushToolOptionsView,
    contextCallback,
    toolOptionsViewController,
    toolPaint,
    workspace,
    idlingResource,
    commandManager,
    drawTime
) {

    private var savedColor: Int
    private var bottomNavigationViewHolder: BottomNavigationViewHolder

    init {
        this.bottomNavigationViewHolder = bottomNavigationViewHolder
        bottomNavigationViewHolder.enableColorItemView(false)
        bottomNavigationViewHolder.setColorButtonColor(Color.TRANSPARENT)
        savedColor = toolPaint.color
        toolPaint.color = Color.TRANSPARENT
        brushToolOptionsView.setCurrentPaint(toolPaint.paint)
    }
    override val previewPaint: Paint
        get() = Paint().apply {
            set(super.previewPaint)
            color = Color.TRANSPARENT
            shader = toolPaint.checkeredShader
        }

    override val bitmapPaint: Paint
        get() = Paint().apply {
            set(super.bitmapPaint)
            xfermode = toolPaint.eraseXfermode
            alpha = 0
        }

    override val toolType: ToolType
        get() = ToolType.ERASER

    fun setSavedColor() {
        bottomNavigationViewHolder.enableColorItemView(true)
        bottomNavigationViewHolder.setColorButtonColor(savedColor)
        toolPaint.color = savedColor
        brushToolOptionsView.setCurrentPaint(toolPaint.paint)
    }
}
