/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.test.espresso.tools

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
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
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.implementation.DEFAULT_DRAG_IN_PERCENT
import org.catrobat.paintroid.tools.implementation.DEFAULT_PRESSURE_IN_PERCENT
import org.catrobat.paintroid.tools.implementation.SmudgeTool
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmudgeToolIntegrationTest {

    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    private lateinit var toolReference: ToolReference

    @Before
    fun setUp() {
        toolReference = launchActivityRule.activity.toolReference
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.SMUDGE)
    }

    @Test
    fun testSmudgeToolOptionsDialog() {
        val smudgeTool = toolReference.tool as SmudgeTool
        ToolBarViewInteraction.onToolBarView()
            .performClickSelectedToolButton()

        val pressureInput = onView(withId(R.id.pocketpaint_smudge_tool_dialog_pressure_input))
        val pressureSeekBar = onView(withId(R.id.pocketpaint_pressure_seek_bar))
        val testPressureText = "100"
        pressureInput.check(matches(withText(Integer.toString(DEFAULT_PRESSURE_IN_PERCENT))))
        pressureInput.perform(replaceText(testPressureText), ViewActions.closeSoftKeyboard())
        pressureInput.check(matches(withText(testPressureText)))
        pressureSeekBar.check(matches(withProgress(testPressureText.toInt())))
        val expectedPressure = 1f
        Assert.assertEquals(expectedPressure, smudgeTool.maxPressure)

        val dragInput = onView(withId(R.id.pocketpaint_smudge_tool_dialog_drag_input))
        val dragSeekBar = onView(withId(R.id.pocketpaint_drag_seek_bar))
        val testDragText = "100"
        dragInput.check(matches(withText(Integer.toString(DEFAULT_DRAG_IN_PERCENT))))
        dragInput.perform(replaceText(testDragText), ViewActions.closeSoftKeyboard())
        dragInput.check(matches(withText(testDragText)))
        dragSeekBar.check(matches(withProgress(testDragText.toInt())))
        val expectedMaxSize = 25f
        val expectedMinSize = 25f
        Assert.assertEquals(expectedMaxSize, smudgeTool.maxSmudgeSize)
        Assert.assertEquals(expectedMinSize, smudgeTool.minSmudgeSize)

        // Close tool options
        ToolBarViewInteraction.onToolBarView()
            .performClickSelectedToolButton()
    }
}
