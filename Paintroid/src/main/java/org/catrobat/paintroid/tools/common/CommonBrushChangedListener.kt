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
package org.catrobat.paintroid.tools.common

import android.graphics.Paint.Cap
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.tools.options.BrushToolOptionsView.OnBrushChangedListener

class CommonBrushChangedListener(private val tool: Tool) : OnBrushChangedListener {
    override fun setCap(strokeCap: Cap) {
        tool.changePaintStrokeCap(strokeCap)
    }

    override fun setStrokeWidth(strokeWidth: Int) {
        tool.changePaintStrokeWidth(strokeWidth)
    }
}
