package org.catrobat.paintroid.test.espresso

import android.content.Context
import androidx.appcompat.widget.Toolbar
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.LandingPageActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.wrappers.OptionsMenuViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.hamcrest.core.IsNot
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
    fun testMoreOptionsAllItemsExist() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        OptionsMenuViewInteraction.onOptionsMenu()
            .checkItemExists(R.string.menu_rate_us)
            .checkItemExists(R.string.help_title)
            .checkItemExists(R.string.pocketpaint_menu_about)
            .checkItemExists(R.string.menu_feedback)
    }

    @Test
    fun testMoreOptionsItemHelpClick() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.help_title))
            .perform(click())
    }

    @Test
    fun testOnHelpDisabled() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.help_title))
            .check(matches(IsNot.not(ViewMatchers.isClickable())))
    }

    @Test
    fun testMoreOptionsItemAboutClick() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.pocketpaint_about_title))
            .perform(click())
    }

    @Test
    fun testShowAboutClickedThenShowAboutDialog() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.pocketpaint_menu_about))
            .perform(click())
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val aboutTextExpected: String = context.getString(
            R.string.pocketpaint_about_content,
            context.getString(R.string.pocketpaint_about_license)
        )
        onView(withText(aboutTextExpected))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testMoreOptionsMenuAboutClosesMoreOptions() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.pocketpaint_menu_about))
            .perform(click())
        Espresso.pressBack()
        onView(withText(R.string.pocketpaint_menu_about))
            .check(ViewAssertions.doesNotExist())
    }
}
