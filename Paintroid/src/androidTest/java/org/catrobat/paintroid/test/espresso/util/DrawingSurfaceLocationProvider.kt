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

package org.catrobat.paintroid.test.espresso.util

import android.graphics.PointF
import android.view.View
import androidx.test.espresso.action.CoordinatesProvider
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape

enum class DrawingSurfaceLocationProvider : CoordinatesProvider {
    MIDDLE {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .5f, .5f)
    },
    HALFWAY_LEFT_MIDDLE {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .25f, .5f)
    },
    HALFWAY_RIGHT_MIDDLE {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .75f, .5f)
    },
    TOP_MIDDLE {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .5f, 0f)
    },
    HALFWAY_TOP_MIDDLE {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .5f, .25f)
    },
    HALFWAY_BOTTOM_MIDDLE {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .5f, .75f)
    },
    HALFWAY_TOP_LEFT {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .25f, .25f)
    },
    HALFWAY_TOP_RIGHT {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .75f, .25f)
    },
    HALFWAY_BOTTOM_LEFT {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .25f, .75f)
    },
    HALFWAY_BOTTOM_RIGHT {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .75f, .75f)
    },
    BOTTOM_RIGHT_CLOSE_CENTER {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .55f, .55f)
    },
    BOTTOM_RIGHT_CORNER {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, 1f, 1f)
    },
    BOTTOM_MIDDLE {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .5f, 1f)
    },
    OUTSIDE_MIDDLE_RIGHT {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, 1.5f, .5f)
    },
    TOOL_POSITION {
        override fun calculateCoordinates(view: View): FloatArray {
            val mainActivity = MainActivityHelper.getMainActivityFromView(view)
            val workspace = mainActivity.workspace
            val toolPosition =
                (mainActivity.toolReference.tool as BaseToolWithShape?)!!.toolPosition
            val point = workspace.getSurfacePointFromCanvasPoint(toolPosition)
            return PositionCoordinatesProvider.calculateViewOffset(view, point.x, point.y)
        }
    };

    companion object {
        private fun calculatePercentageOffset(
            view: View,
            percentageX: Float,
            percentageY: Float
        ): FloatArray {
            val mainActivity = MainActivityHelper.getMainActivityFromView(view)
            val workspace = mainActivity.workspace
            val pointX = workspace.width * percentageX
            val pointY = workspace.height * percentageY
            val point = workspace.getSurfacePointFromCanvasPoint(PointF(pointX, pointY))
            return PositionCoordinatesProvider.calculateViewOffset(view, point.x, point.y)
        }
    }
}
