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
import android.widget.ListView
import android.widget.SeekBar
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.Tapper
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers

object UiInteractions {

    @JvmStatic
	fun waitFor(millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> { return ViewMatchers.isRoot() }

            override fun getDescription(): String { return "Wait for $millis milliseconds." }

            override fun perform(uiController: UiController, view: View) { uiController.loopMainThreadForAtLeast(millis) }
        }
    }

    @JvmStatic
	fun assertListViewCount(expectedCount: Int): ViewAssertion {
        return ViewAssertion { view: View, noViewFoundException: NoMatchingViewException? ->
            if (noViewFoundException != null) { throw noViewFoundException }
            val adapter = (view as ListView).adapter
            ViewMatchers.assertThat(adapter.count, Matchers.`is`(expectedCount))
        }
    }

    @JvmStatic
	fun setProgress(progress: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> { return ViewMatchers.isAssignableFrom(SeekBar::class.java) }

            override fun getDescription(): String { return "Set a progress" }

            override fun perform(uiController: UiController, view: View) { (view as SeekBar).progress = progress }
        }
    }

    fun touchAt(x: Int, y: Int): ViewAction { return touchAt(x.toFloat(), y.toFloat()) }

    fun touchLongAt(x: Float, y: Float): ViewAction { return touchAt(x, y, Tap.LONG) }

    @JvmOverloads
    fun touchAt(provider: CoordinatesProvider?, tapStyle: Tapper? = Tap.SINGLE): ViewAction {
        return ViewActions.actionWithAssertions(
            GeneralClickAction(tapStyle, provider, Press.FINGER, 0, 0)
        )
    }

    @JvmOverloads
    fun touchAt(coordinates: PointF, tapStyle: Tapper? = Tap.SINGLE): ViewAction { return touchAt(coordinates.x, coordinates.y, tapStyle) }

    @JvmStatic
	@JvmOverloads
    fun touchAt(x: Float, y: Float, tapStyle: Tapper? = Tap.SINGLE): ViewAction {
        return ViewActions.actionWithAssertions(
            GeneralClickAction(tapStyle, PositionCoordinatesProvider.at(x, y), Press.FINGER, 0, 0)
        )
    }

    @JvmStatic
	fun touchCenterLeft(): ViewAction { return GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER_LEFT, Press.FINGER, 0, 0) }

    @JvmStatic
	fun touchCenterMiddle(): ViewAction { return GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER, Press.FINGER, 0, 0) }

    @JvmStatic
	fun touchCenterRight(): ViewAction { return GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER_RIGHT, Press.FINGER, 0, 0) }

    fun swipe(start: PointF, end: PointF): ViewAction { return swipe(start.x.toInt(), start.y.toInt(), end.x.toInt(), end.y.toInt()) }

    private fun swipe(startX: Int, startY: Int, endX: Int, endY: Int): ViewAction {
        return swipe(
            PositionCoordinatesProvider.at(startX.toFloat(), startY.toFloat()),
            PositionCoordinatesProvider.at(endX.toFloat(), endY.toFloat())
        )
    }

    @JvmStatic
	fun swipe(
        startCoordinatesProvider: CoordinatesProvider?,
        endCoordinatesProvider: CoordinatesProvider?
    ): ViewAction {
        return GeneralSwipeAction(
            Swipe.FAST,
            startCoordinatesProvider,
            endCoordinatesProvider,
            Press.FINGER
        )
    }

    @JvmStatic
	fun swipeAccurate(
        startCoordinatesProvider: CoordinatesProvider?,
        endCoordinatesProvider: CoordinatesProvider?
    ): ViewAction {
        return GeneralSwipeAction(
            CustomSwiper.ACCURATE,
            startCoordinatesProvider,
            endCoordinatesProvider,
            Press.FINGER
        )
    }
}
