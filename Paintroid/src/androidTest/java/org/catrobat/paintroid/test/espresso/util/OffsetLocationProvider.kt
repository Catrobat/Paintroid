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
import org.catrobat.paintroid.test.espresso.util.OffsetLocationProvider

class OffsetLocationProvider(
    private val locationProvider: CoordinatesProvider,
    private val xOffset: Int,
    private val yOffset: Int
) : CoordinatesProvider {
    override fun calculateCoordinates(view: View): FloatArray {
        val coordinates = locationProvider.calculateCoordinates(view)
        coordinates[0] += xOffset.toFloat()
        coordinates[1] += yOffset.toFloat()
        return coordinates
    }

    companion object {
        @JvmStatic
        fun withOffset(
            locationProvider: CoordinatesProvider,
            xOffset: Int,
            yOffset: Int
        ): CoordinatesProvider {
            return OffsetLocationProvider(locationProvider, xOffset, yOffset)
        }
    }
}