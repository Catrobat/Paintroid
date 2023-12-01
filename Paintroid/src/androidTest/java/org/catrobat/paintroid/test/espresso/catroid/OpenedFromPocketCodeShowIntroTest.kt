package org.catrobat.paintroid.test.espresso.catroid

import android.content.Context
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.common.PAINTROID_PICTURE_NAME
import org.catrobat.paintroid.common.PAINTROID_PICTURE_PATH
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OpenedFromPocketCodeShowIntroTest {
    @get:Rule
    var launchActivityRule = IntentsTestRule(
        MainActivity::class.java, false, false
    )

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Before
    fun setUp() {
        val intent = getOpenedFromCatroidIntent()
        launchActivityRule.launchActivity(intent)
    }

    @After
    fun tearDown() {
        launchActivityRule.activity.getPreferences(Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun testWelcomeActivityShownOnlyOnFirstOpenFromPocketCode() {
        onView(withText(R.string.intro_welcome_text)).check(ViewAssertions.matches(isDisplayed()))
        onView(withText(R.string.welcome_to_pocket_paint)).check(ViewAssertions.matches(isDisplayed()))

        pressBackUnconditionally()
        launchActivityRule.finishActivity()
        val intent = getOpenedFromCatroidIntent()
        launchActivityRule.launchActivity(intent)

        onView(withText(R.string.intro_welcome_text)).check(ViewAssertions.doesNotExist())
        onView(withText(R.string.welcome_to_pocket_paint)).check(ViewAssertions.doesNotExist())
    }

    private fun getOpenedFromCatroidIntent() = Intent().apply {
        putExtra(PAINTROID_PICTURE_PATH, "")
        putExtra(PAINTROID_PICTURE_NAME, "testFile")
    }
}
