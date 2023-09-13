package org.catrobat.paintroid.test.junit.tools

import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.implementation.PixelTool
import org.catrobat.paintroid.ui.Perspective
import org.junit.Rule
import org.junit.Test

class PixelToolTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(
        MainActivity::class.java
    )

/*
    @Test
    fun testMeanCalculation()
    {

    }*/
}