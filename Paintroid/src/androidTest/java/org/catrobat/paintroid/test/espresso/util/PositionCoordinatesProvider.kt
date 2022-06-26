/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http:></http:>//developer.catrobat.org/credits>)
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.util

import android.view.View
import androidx.test.espresso.action.CoordinatesProvider

class PositionCoordinatesProvider(private val xCoordinate: Float, private val yCoordinate: Float) :
    CoordinatesProvider {
    override fun calculateCoordinates(view: View): FloatArray = calculateViewOffset(view, xCoordinate, yCoordinate)

    companion object {
        @JvmStatic
        fun at(x: Float, y: Float): CoordinatesProvider = PositionCoordinatesProvider(x, y)

        @JvmStatic
        fun calculateViewOffset(view: View, x: Float, y: Float): FloatArray {
            val screenLocation = IntArray(2)
            view.getLocationOnScreen(screenLocation)
            val touchX = screenLocation[0] + x
            val touchY = screenLocation[1] + y
            return floatArrayOf(touchX, touchY)
        }
    }
}
