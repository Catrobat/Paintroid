package org.catrobat.paintroid.test.espresso

import androidx.appcompat.widget.Toolbar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.LandingPageActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingPageActivityIntegrationTest {

    @get:Rule
    var launchActivityRule = ActivityTestRule(LandingPageActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    private lateinit var activity: LandingPageActivity

    @Before
    fun setUp() {
        activity = launchActivityRule.activity
    }

    @Test
    fun testTopAppBarDisplayed() {
        onView(isAssignableFrom(Toolbar::class.java))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAppBarTitleDisplayPocketPaint() {
        onView(withText("Pocket Paint"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testShowAboutClickedThenShowAboutDialog() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.pocketpaint_about_title))
            .perform(click())
        onView(withId(R.id.pocketpaint_about_license_url))
            .inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))
    }
}
