/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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

import org.junit.runner.RunWith
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import org.catrobat.paintroid.R
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.tools.ToolType
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.assertion.ViewAssertions
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class TextToolFontListTest {
    private val normalStyle = Typeface.NORMAL
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val sansSerifFontFace = Typeface.create(Typeface.SANS_SERIF, normalStyle)
    private val serifFontFace = Typeface.create(Typeface.SERIF, normalStyle)
    private val monospaceFontFace = Typeface.create(Typeface.MONOSPACE, normalStyle)
    private val stcFontFace = ResourcesCompat.getFont(context, R.font.stc_regular)
    private val dubaiFontFace = ResourcesCompat.getFont(context, R.font.dubai)

    @Rule
    var launchActivityRule = ActivityTestRule(
        MainActivity::class.java
    )

    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    @Test
    fun testTextFontFaceOfFontSpinnerEnglish() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.TEXT)
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .check(
                ViewAssertions.matches(
                    UiMatcher.atPosition(
                        0,
                        ViewMatchers.hasDescendant(UiMatcher.hasTypeFace(sansSerifFontFace))
                    )
                )
            )
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .check(
                ViewAssertions.matches(
                    UiMatcher.atPosition(
                        1,
                        ViewMatchers.hasDescendant(UiMatcher.hasTypeFace(monospaceFontFace))
                    )
                )
            )
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(
                ViewAssertions.matches(
                    UiMatcher.atPosition(
                        2,
                        ViewMatchers.hasDescendant(UiMatcher.hasTypeFace(serifFontFace))
                    )
                )
            )
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(3))
            .check(
                ViewAssertions.matches(
                    UiMatcher.atPosition(
                        3,
                        ViewMatchers.hasDescendant(UiMatcher.hasTypeFace(dubaiFontFace))
                    )
                )
            )
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(4))
            .check(
                ViewAssertions.matches(
                    UiMatcher.atPosition(
                        4,
                        ViewMatchers.hasDescendant(UiMatcher.hasTypeFace(stcFontFace))
                    )
                )
            )
    }

    @Test
    fun checkIfSansSerifIsDefaultSpinnerFont() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.TEXT)
        Espresso.onView(ViewMatchers.withText(R.string.text_tool_dialog_font_sans_serif))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
    }
}