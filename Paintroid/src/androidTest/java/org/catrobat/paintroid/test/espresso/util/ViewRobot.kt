package org.catrobat.paintroid.test.espresso.util

import android.os.SystemClock.sleep
import android.util.Log
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.searchFor
import org.hamcrest.Matcher

open class ViewRobot {

    fun doOnView(matcher: Matcher<View>, vararg actions: ViewAction) {
        actions.forEach {
            waitForView(matcher).perform(it)
        }
    }

    fun assertOnView(matcher: Matcher<View>, vararg assertions: ViewAssertion) {
        assertions.forEach {
            waitForView(matcher).check(it)
        }
    }

    @SuppressWarnings("SwallowedException", "TooGenericExceptionThrown")
    private fun waitForView(viewMatcher: Matcher<View>, waitMillis: Int = 5000, waitMillisPerTry: Long = 50): ViewInteraction {
        val endTime = System.currentTimeMillis() + waitMillis
        while (System.currentTimeMillis() < endTime) {
            try {
                onView(isRoot()).perform(searchFor(viewMatcher))
                return onView(viewMatcher)
            } catch (e: Exception) {
                Log.d("asdf", "sleeping...")
                sleep(waitMillisPerTry)
            }
        }
        throw Exception("Error finding a view matching $viewMatcher after ${waitMillis}ms milliseconds")
    }

    @SuppressWarnings("SwallowedException", "TooGenericExceptionThrown")
    fun waitForViewToDisappear(viewMatcher: Matcher<View>, waitMillis: Int = 5000, waitMillisPerTry: Long = 50): ViewInteraction {
        val endTime = System.currentTimeMillis() + waitMillis
        while (System.currentTimeMillis() < endTime) {
            try {
                onView(isRoot()).perform(searchFor(viewMatcher))
                sleep(waitMillisPerTry)
            } catch (e: Exception) {
                return onView(viewMatcher)
            }
        }
        onView(isRoot()).perform(searchFor(viewMatcher))
        throw Exception("View $viewMatcher did not disappear after ${waitMillis}ms")
    }
}
