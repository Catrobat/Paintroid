/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.util

import android.view.View
import androidx.test.espresso.action.CoordinatesProvider
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper.Companion.getMainActivityFromView

enum class BitmapLocationProvider : CoordinatesProvider {
    MIDDLE {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .5f, .5f)
    },
    MIDDLE_RIGHT {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, 1f, .5f)
    },
    HALFWAY_RIGHT_MIDDLE {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .75f, .5f)
    },
    HALFWAY_LEFT_MIDDLE {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .25f, .5f)
    },
    HALFWAY_BOTTOM_MIDDLE {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .5f, .75f)
    },
    HALFWAY_TOP_MIDDLE {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .5f, .25f)
    },
    HALFWAY_TOP_LEFT {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .25f, .25f)
    },
    HALFWAY_BOTTOM_RIGHT {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .75f, .75f)
    },
    HALFWAY_BOTTOM_LEFT {
        override fun calculateCoordinates(view: View): FloatArray = calculatePercentageOffset(view, .25f, .75f)
    };

    companion object {
        private fun calculatePercentageOffset(
            view: View,
            percentageX: Float,
            percentageY: Float
        ): FloatArray {
            val mainActivity = getMainActivityFromView(view)
            val workspace = mainActivity.workspace
            val pointX = (workspace.width - 1) * percentageX
            val pointY = workspace.height * percentageY
            return floatArrayOf(pointX, pointY)
        }
    }
}
