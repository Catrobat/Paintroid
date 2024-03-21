package org.catrobat.paintroid.test.espresso

import android.content.Intent
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.LandingPageActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.WelcomeActivity
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingPageActivityIntentTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(LandingPageActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun teardown() {
        Intents.release()
    }

    @Test
    fun testPlayStoreOpenedOnRateUsClicked() {
        val applicationId = "org.catrobat.paintroid"
        val uri = Uri.parse("market://details?id=$applicationId")
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_rate_us))
            .perform(click())
        intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(uri)
            )
        )
    }

    @Test
    fun testShowHelpClickedThenStartWelcomeActivity() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.help_title))
            .perform(click())
        intended(hasComponent(WelcomeActivity::class.java.name))
    }

    @Test
    fun testMailOpenedOnFeedbackClicked() {
        val uri = Uri.parse("mailto:support-paintroid@catrobat.org")
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_feedback))
            .perform(click())
        intended(
            allOf(
                hasAction(Intent.ACTION_SENDTO),
                hasData(uri)
            )
        )
    }
}
