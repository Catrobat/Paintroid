package org.catrobat.paintroid.test.espresso.api

import android.app.Activity
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.paintroid.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RequiresApi(api = Build.VERSION_CODES.P)
@RunWith(AndroidJUnit4::class)
class CorrectStandbyBucketBehaviourTests {

    private var activity: Activity? = null

    @get:Rule
    var launchActivityRule: ActivityScenarioRule<MainActivity?>? = ActivityScenarioRule(MainActivity::class.java)

    private fun getActivity(): Activity? {
        var activity: Activity? = null
        launchActivityRule?.scenario?.onActivity {
            activity = it
        }
        return activity
    }

    @Before
    fun setUp() {
        activity = getActivity()
    }

    @Test
    fun checkWhenAppStartAndInFrontForActiveBucket() {

        val lUsageStatsManager = activity?.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager?
        val standbyBucketReturn = lUsageStatsManager?.appStandbyBucket

        assertEquals(standbyBucketReturn, UsageStatsManager.STANDBY_BUCKET_ACTIVE)
    }
}
