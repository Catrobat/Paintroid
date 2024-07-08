package org.catrobat.paintroid.test.espresso

import android.graphics.Color
import android.os.Build
import android.widget.RelativeLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.tools.ToolType
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.catrobat.paintroid.R
import org.junit.Test
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiInteractions.PressAndReleaseActions.tearDownPressAndRelease
import org.catrobat.paintroid.test.espresso.util.UiInteractions.PressAndReleaseActions.pressAction
import org.catrobat.paintroid.test.espresso.util.UiInteractions.PressAndReleaseActions.releaseAction
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.Companion.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.Companion.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ZoomWindowInteraction.Companion.onZoomWindow
import org.junit.After

@RunWith(AndroidJUnit4::class)
class ZoomWindowIntegrationTest {

    @get:Rule
    val launchActivityRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var mainActivity: MainActivity
    @Before
    fun setUp() {
        mainActivity = launchActivityRule.activity
        onToolBarView().performSelectTool(ToolType.BRUSH)
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
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

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
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun windowAppearsOnRightWhenClickedAtTheTopLeft() {
        onDrawingSurfaceView()
            .perform(pressAction(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onZoomWindow()
                .checkAlignment(RelativeLayout.ALIGN_PARENT_RIGHT)
        } else {
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

    @Test
    fun checkBackgroundOfZoomWindowOnlyOneLayer() {
        onToolBarView().performSelectTool(ToolType.FILL)
        ToolPropertiesInteraction.onToolProperties().checkMatchesColor(Color.BLACK)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)

        onToolBarView().performSelectTool(ToolType.BRUSH)
        onDrawingSurfaceView().perform(pressAction(DrawingSurfaceLocationProvider.MIDDLE))
        onView(withId(R.id.pocketpaint_zoom_window))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

        val zoomWindowBitmap = mainActivity.zoomWindowController.getBitmap()
        onZoomWindow().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE, zoomWindowBitmap)

        onDrawingSurfaceView().perform(releaseAction())
    }

    @Test
    fun checkBackgroundOfZoomWindowWithMultipleLayers() {
        LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
            .performSelectLayer(0)
            .performClose()

        onToolBarView().performSelectTool(ToolType.FILL)
        ToolPropertiesInteraction.onToolProperties().checkMatchesColor(Color.BLACK)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)

        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSelectLayer(1)
            .performClose()

        onToolBarView().performSelectTool(ToolType.BRUSH)
        onDrawingSurfaceView().perform(pressAction(DrawingSurfaceLocationProvider.MIDDLE))
        onView(withId(R.id.pocketpaint_zoom_window))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

        val zoomWindowBitmap = mainActivity.zoomWindowController.getBitmap()
        onZoomWindow().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE, zoomWindowBitmap)

        onDrawingSurfaceView().perform(releaseAction())
    }
}
