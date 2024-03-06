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

import android.graphics.PointF
import android.graphics.Rect
import android.util.Log
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.ScrollToAction
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.Tapper
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.viewpager.widget.ViewPager
import org.catrobat.paintroid.ui.LayerAdapter
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import java.lang.Boolean
import kotlin.AssertionError
import kotlin.Deprecated
import kotlin.Float
import kotlin.FloatArray
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.floatArrayOf

object UiInteractions {
    fun unconstrainedScrollTo(): ViewAction = ViewActions.actionWithAssertions(UnconstrainedScrollToAction())

    fun waitFor(millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isRoot()
            }

            override fun getDescription(): String {
                return "Wait for $millis milliseconds."
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
    }

    fun assertRecyclerViewCount(expectedCount: Int): ViewAssertion {
        return ViewAssertion { view: View, noViewFoundException: NoMatchingViewException? ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }
            val adapter = (view as RecyclerView).adapter as LayerAdapter?
            if (adapter != null) {
                ViewMatchers.assertThat(
                    adapter.itemCount,
                    Matchers.`is`(expectedCount)
                )
            }
        }
    }

    fun setProgress(progress: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(SeekBar::class.java)
            }

            override fun getDescription(): String {
                return "Set a progress"
            }

            override fun perform(uiController: UiController, view: View) {
                try {
                    val privateSetProgressMethod =
                        ProgressBar::class.java.getDeclaredMethod(
                            "setProgressInternal",
                            Integer.TYPE,
                            Boolean.TYPE,
                            Boolean.TYPE
                        )
                    privateSetProgressMethod.isAccessible = true
                    privateSetProgressMethod.invoke(view, progress, true, true)
                } catch (e: ReflectiveOperationException) {
                    Log.e("SET PROGRESS", "could not set progress")
                }
            }
        }
    }

    fun clickOutside(direction: Direction?): ViewAction? {
        return ViewActions.actionWithAssertions(
            GeneralClickAction(Tap.SINGLE, CoordinatesProvider { view: View ->
                val r = Rect()
                view.getGlobalVisibleRect(r)
                when (direction) {
                    Direction.ABOVE -> return@CoordinatesProvider floatArrayOf(
                        r.centerX().toFloat(),
                        (r.top - 50).toFloat()
                    )
                    Direction.BELOW -> return@CoordinatesProvider floatArrayOf(
                        r.centerX().toFloat(),
                        (r.bottom + 50).toFloat()
                    )
                    Direction.LEFT -> return@CoordinatesProvider floatArrayOf(
                        (r.left - 50).toFloat(),
                        r.centerY().toFloat()
                    )
                    Direction.RIGHT -> return@CoordinatesProvider floatArrayOf(
                        (r.right + 50).toFloat(),
                        r.centerY().toFloat()
                    )
                    else -> throw IllegalArgumentException("Invalid direction: $direction")
                }
                null
            }, Press.FINGER, 0, 1)
        )
    }
    fun touchAt(x: Int, y: Int): ViewAction = touchAt(x.toFloat(), y.toFloat())

    fun touchLongAt(provider: CoordinatesProvider?): ViewAction = touchAt(provider, Tap.LONG)

    fun touchLongAt(coordinates: PointF): ViewAction = touchAt(coordinates, Tap.LONG)

    fun touchLongAt(x: Float, y: Float): ViewAction = touchAt(x, y, Tap.LONG)

    @JvmOverloads
    fun touchAt(provider: CoordinatesProvider?, tapStyle: Tapper? = Tap.SINGLE): ViewAction {
        return ViewActions.actionWithAssertions(
            GeneralClickAction(tapStyle, provider, Press.FINGER, 0, 0)
        )
    }

    @JvmOverloads
    fun touchAt(coordinates: PointF, tapStyle: Tapper? = Tap.SINGLE): ViewAction = touchAt(coordinates.x, coordinates.y, tapStyle)

    @JvmOverloads
    fun touchAt(x: Float, y: Float, tapStyle: Tapper? = Tap.SINGLE): ViewAction {
        return ViewActions.actionWithAssertions(
            GeneralClickAction(tapStyle, PositionCoordinatesProvider.at(x, y), Press.FINGER, 0, 0)
        )
    }

    fun touchCenterLeft(): ViewAction = GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER_LEFT, Press.FINGER, 0, 0)

    fun touchCenterTop(): ViewAction = GeneralClickAction(Tap.SINGLE, GeneralLocation.TOP_CENTER, Press.FINGER, 0, 0)

    fun touchCenterBottom(): ViewAction = GeneralClickAction(Tap.SINGLE, GeneralLocation.BOTTOM_CENTER, Press.FINGER, 0, 0)

    fun touchCenterMiddle(): ViewAction = GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER, Press.FINGER, 0, 0)

    fun touchCenterRight(): ViewAction = GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER_RIGHT, Press.FINGER, 0, 0)

    fun swipe(start: PointF, end: PointF): ViewAction = swipe(start.x.toInt(), start.y.toInt(), end.x.toInt(), end.y.toInt())

    fun swipe(startX: Float, startY: Float, endX: Float, endY: Float): ViewAction = swipe(startX.toInt(), startY.toInt(), endX.toInt(), endY.toInt())

    fun swipe(startX: Int, startY: Int, endX: Int, endY: Int): ViewAction {
        return swipe(
            PositionCoordinatesProvider.at(startX.toFloat(), startY.toFloat()),
            PositionCoordinatesProvider.at(endX.toFloat(), endY.toFloat())
        )
    }

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

    fun selectViewPagerPage(pos: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(ViewPager::class.java)
            }

            override fun getDescription(): String {
                return "select page in ViewPager"
            }

            override fun perform(uiController: UiController, view: View) {
                (view as ViewPager).currentItem = pos
            }
        }
    }

    enum class Direction {
        ABOVE, BELOW, LEFT, RIGHT
    }

    private class UnconstrainedScrollToAction : ViewAction {
        private val action: ViewAction = ScrollToAction()
        override fun getConstraints(): Matcher<View> = ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)

        override fun getDescription(): String = action.description

        override fun perform(uiController: UiController, view: View) = action.perform(uiController, view)
    }

    class DefinedLongTap internal constructor(private val longPressTimeout: Int) : Tapper {
        override fun sendTap(
            uiController: UiController,
            coordinates: FloatArray,
            precision: FloatArray,
            inputDevice: Int,
            buttonState: Int
        ): Tapper.Status {
            val downEvent = MotionEvents.sendDown(
                uiController, coordinates, precision,
                inputDevice, buttonState
            ).down
            try {
                // Duration before a press turns into a long press.
                // Factor 1.5 is needed, otherwise a long press is not safely detected.
                // See android.test.TouchUtils longClickView
                val longPressTimeout = (longPressTimeout * 1.5f).toLong()
                uiController.loopMainThreadForAtLeast(longPressTimeout)
                if (!MotionEvents.sendUp(uiController, downEvent)) {
                    MotionEvents.sendCancel(uiController, downEvent)
                    return Tapper.Status.FAILURE
                }
            } finally {
                downEvent.recycle()
            }
            return Tapper.Status.SUCCESS
        }

        @Deprecated("use other sendTap instead")
        override fun sendTap(
            uiController: UiController,
            coordinates: FloatArray,
            precision: FloatArray
        ): Tapper.Status {
            return sendTap(
                uiController, coordinates, precision, InputDevice.SOURCE_UNKNOWN,
                MotionEvent.BUTTON_PRIMARY
            )
        }

        companion object {
            fun withPressTimeout(longPressTimeout: Int): Tapper = DefinedLongTap(longPressTimeout)
        }
    }

    object PressAndReleaseActions {
        var motionEvent: MotionEvent? = null
        fun pressAction(coordinates: DrawingSurfaceLocationProvider): PressAction = PressAction(coordinates)

        fun releaseAction(): ReleaseAction = ReleaseAction()

        fun tearDownPressAndRelease() {
            motionEvent = null
        }

        class PressAction internal constructor(var coordinates: DrawingSurfaceLocationProvider) :
            ViewAction {

            override fun getConstraints(): Matcher<View> = ViewMatchers.isDisplayingAtLeast(90)

            override fun getDescription(): String = "Press Action"

            override fun perform(uiController: UiController, view: View) {
                if (motionEvent != null) {
                    throw AssertionError("Only one view can be held at a time")
                }
                val coords = coordinates.calculateCoordinates(view)
                val precision = Press.FINGER.describePrecision()
                motionEvent = MotionEvents.sendDown(uiController, coords, precision).down
            }
        }

        class ReleaseAction : ViewAction {
            override fun getConstraints(): Matcher<View> = ViewMatchers.isDisplayingAtLeast(90)

            override fun getDescription(): String = "Release"

            override fun perform(uiController: UiController, view: View) {
                if (motionEvent == null) {
                    throw AssertionError("Only one view can be held at a time")
                }
                MotionEvents.sendUp(uiController, motionEvent)
            }
        }
    }
}
