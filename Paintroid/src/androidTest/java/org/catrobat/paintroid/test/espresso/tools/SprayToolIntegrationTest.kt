package org.catrobat.paintroid.test.espresso.tools

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.UiMatcher.withProgress
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.ui.tools.DefaultSprayToolOptionsView
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SprayToolIntegrationTest {

    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Before
    fun setUp() {
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.SPRAY)
    }

    @Test
    fun testEmptyRadius()
    {
        val emptyString:String = ""
        onView(withId(R.id.pocketpaint_radius_text))
                .perform(replaceText(emptyString))
                .check(matches(withText(emptyString)))

        onView(withId(R.id.pocketpaint_spray_radius_seek_bar))
                .check(matches(withProgress(DefaultSprayToolOptionsView.MIN_RADIUS)))
    }
}