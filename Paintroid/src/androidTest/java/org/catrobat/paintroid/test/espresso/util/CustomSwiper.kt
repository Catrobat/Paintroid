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
package org.catrobat.paintroid.test.espresso.util

import android.os.SystemClock
import androidx.test.espresso.action.Swiper
import org.catrobat.paintroid.test.espresso.util.CustomSwiper
import android.view.MotionEvent
import androidx.test.espresso.UiController
import androidx.test.espresso.action.MotionEvents

enum class CustomSwiper : Swiper {
    ACCURATE {
        override fun sendSwipe(
            uiController: UiController,
            startCoordinates: FloatArray,
            endCoordinates: FloatArray,
            precision: FloatArray
        ): Swiper.Status {
            val steps = interpolate(startCoordinates, endCoordinates, STEPS)
            val delayBetweenMovements = DURATION / steps.size
            val downEvent = MotionEvents.sendDown(uiController, startCoordinates, precision).down
            try {
                for (i in steps.indices) {
                    MotionEvents.sendMovement(uiController, downEvent, steps[i])
                    val desiredTime = downEvent.downTime + delayBetweenMovements * i
                    val timeUntilDesired = desiredTime - SystemClock.uptimeMillis()
                    if (timeUntilDesired > 10) {
                        uiController.loopMainThreadForAtLeast(timeUntilDesired)
                    }
                }
                MotionEvents.sendUp(uiController, downEvent, endCoordinates)
            } finally {
                downEvent.recycle()
            }
            return Swiper.Status.SUCCESS
        }
    };

    companion object {
        private const val STEPS = 10
        private const val DURATION = 500
        private fun interpolate(start: FloatArray, end: FloatArray, steps: Int): Array<FloatArray> {
            val res = Array(steps) { FloatArray(2) }
            res[0][0] = start[0]
            res[0][1] = start[1]
            for (i in 2 until steps) {
                res[i - 1][0] = start[0] + (end[0] - start[0]) * i / (steps + 2f)
                res[i - 1][1] = start[1] + (end[1] - start[1]) * i / (steps + 2f)
            }
            res[steps - 1][0] = end[0]
            res[steps - 1][1] = end[1]
            return res
        }
    }
}