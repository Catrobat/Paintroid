package org.catrobat.paintroid.test.espresso

import android.content.Intent
import android.os.Build
import android.text.Layout
import android.widget.RelativeLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.common.TEMP_IMAGE_PATH
import org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterMiddle
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.tools.ToolType
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.io.File
import org.catrobat.paintroid.R
import org.junit.Test
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions.PressAndReleaseActions.*
import org.catrobat.paintroid.test.espresso.util.UiInteractions.touchLongAt
import org.catrobat.paintroid.test.espresso.util.wrappers.ZoomWindowInteraction.onZoomWindow
import org.junit.After


@RunWith(AndroidJUnit4::class)
class ZoomWindowIntegrationTest {

    @get:Rule
    val launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
    }

    @After
    fun tearDown() {
        tearDownPressAndRelease()
    }

    @Test
    fun windowAppearsWhenDrawingSurfaceIsTouched() {
        onDrawingSurfaceView()
            .perform(pressAction(DrawingSurfaceLocationProvider.MIDDLE))

        onView(withId(R.id.pocketpaint_zoom_window))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onDrawingSurfaceView()
            .perform(releaseAction())
    }

    @Test
    fun windowDisappearsWhenDrawingSurfaceIsPressedAndReleased() {
        onDrawingSurfaceView()
            .perform(pressAction(DrawingSurfaceLocationProvider.MIDDLE))

        onDrawingSurfaceView()
            .perform(releaseAction())

        onView(withId(R.id.pocketpaint_zoom_window))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun windowAppearsOnRightWhenClickedAtTheTopLeft() {
        onDrawingSurfaceView()
            .perform(pressAction(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onZoomWindow()
                .checkAlignment(RelativeLayout.ALIGN_PARENT_RIGHT)
        } else {
            // Layout params rules for ALIGN_PARENT_LEFT and ALIGN_PARENT_RIGHT from
            // https://developer.android.com/reference/android/widget/RelativeLayout.LayoutParams#getRules()
            // for API level less than M
            onZoomWindow()
                .checkAlignmentBelowM(11)
        }

        onDrawingSurfaceView()
            .perform(releaseAction())
    }

    @Test
    fun windowAppearsOnLeftWhenClickedAnywhere() {
        onDrawingSurfaceView()
            .perform(pressAction(DrawingSurfaceLocationProvider.MIDDLE))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onZoomWindow()
                .checkAlignment(RelativeLayout.ALIGN_PARENT_LEFT)
        } else {
            onZoomWindow()
                .checkAlignmentBelowM(9)
        }

        onDrawingSurfaceView()
            .perform(releaseAction())
    }
}