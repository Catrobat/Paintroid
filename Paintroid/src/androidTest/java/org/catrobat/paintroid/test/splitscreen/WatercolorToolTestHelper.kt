package org.catrobat.paintroid.test.splitscreen

import android.content.res.Resources
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.tools.ToolType
import org.junit.Assert

object WatercolorToolTestHelper {

    private lateinit var activity: MainActivity

    fun setupEnvironment(mainActivity: MainActivity) {
        activity = mainActivity
    }

    fun drawOnBitmapThenChangeMaskFilter() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.WATERCOLOR)

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.BOTTOM_MIDDLE))

        val oldFilter = activity.toolPaint.paint.maskFilter

        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()

        Espresso.onView(ViewMatchers.withId(R.id.color_alpha_slider))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())

        ColorPickerViewInteraction.onColorPickerView()
            .onPositiveButton()
            .perform(ViewActions.click())

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.TOP_MIDDLE))

        val newFilter = activity.toolPaint.paint.maskFilter
        Assert.assertTrue("oldFilter and newFilter is the same", oldFilter != newFilter)
    }
}