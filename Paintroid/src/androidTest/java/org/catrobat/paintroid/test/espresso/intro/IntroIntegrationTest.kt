@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso.intro

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.R
import org.catrobat.paintroid.WelcomeActivity
import org.catrobat.paintroid.intro.IntroPageViewAdapter
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IntroIntegrationTest {
    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(WelcomeActivity::class.java)

    @JvmField
    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Test
    fun testIntroWelcomePage() {
        onView(withText(R.string.welcome_to_pocket_paint))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withText(R.string.intro_welcome_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withText(R.string.next))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withText(R.string.skip))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testOnSkipPressedActivityFinished() {
        onView(withId(R.id.pocketpaint_btn_skip)).perform(ViewActions.click())
        Assert.assertTrue(activityTestRule.activity.isFinishing)
    }

    @Test
    fun testOnLetsGoPressedActivityFinished() {
        val viewPager = activityTestRule.activity.viewPager
        val adapter = viewPager.adapter as IntroPageViewAdapter?
        repeat(adapter?.layouts?.size!! - 1) {
            onView(withId(R.id.pocketpaint_btn_next)).perform(ViewActions.click())
        }
        onView(withId(R.id.pocketpaint_btn_next)).perform(ViewActions.click())
        Assert.assertTrue(activityTestRule.activity.isFinishing)
    }

    @Test
    fun testIntroToolsPageShowDescriptionOnPress() {
        val viewPager = activityTestRule.activity.viewPager
        val adapter = viewPager.adapter as IntroPageViewAdapter?
        if (adapter != null) {
            for (layout in adapter.layouts) {
                if (layout == R.layout.pocketpaint_slide_intro_tools_selection) {
                    break
                }
                onView(withId(R.id.pocketpaint_btn_next)).perform(ViewActions.click())
            }
        }
        onView(withId(R.id.pocketpaint_textview_intro_tools_header))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        for (toolType in ToolType.values()) {
            val tool = !(
                toolType == ToolType.UNDO ||
                    toolType == ToolType.REDO ||
                    toolType == ToolType.LAYER ||
                    toolType == ToolType.COLORCHOOSER
                )
            if (tool) {
                onView(withId(toolType.toolButtonID))
                    .perform(ViewActions.click())
                onView(withText(toolType.helpTextResource))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            }
        }
    }

    @Test
    fun testIntroViewPagerSwipeChangePage() {
        onView(withId(R.id.pocketpaint_intro_welcome_head))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.pocketpaint_view_pager)).perform(ViewActions.swipeLeft())
        onView(withId(R.id.pocketpaint_intro_possibilities_head))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.pocketpaint_view_pager)).perform(ViewActions.swipeLeft())
        onView(withId(R.id.pocketpaint_textview_intro_tools_header))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.pocketpaint_view_pager)).perform(ViewActions.swipeLeft())
        onView(withId(R.id.pocketpaint_intro_landscape_head))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.pocketpaint_view_pager)).perform(ViewActions.swipeLeft())
        onView(withId(R.id.pocketpaint_intro_started_head))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testIntroViewPagerNextButtonChangePage() {
        onView(withId(R.id.pocketpaint_intro_welcome_head))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.pocketpaint_btn_next)).perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_intro_possibilities_head))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.pocketpaint_btn_next)).perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_textview_intro_tools_header))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.pocketpaint_btn_next)).perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_intro_landscape_head))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.pocketpaint_btn_next)).perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_intro_started_head))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}
