/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.test.espresso.tools

import android.graphics.Color
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ShapeToolOptionsViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.drawable.DrawableShape
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ShapeToolEraseIntegrationTest {
    @Parameterized.Parameter
    lateinit var shape: DrawableShape

    @JvmField
    @Rule
    val launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @JvmField
    @Rule
    val screenshotOnFailRule = ScreenshotOnFailRule()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data() = listOf(
            arrayOf(DrawableShape.RECTANGLE),
            arrayOf(DrawableShape.OVAL),
            arrayOf(DrawableShape.HEART),
            arrayOf(DrawableShape.STAR)
        )
    }
    @Test
    fun testEraseWithFilledShape() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.SHAPE)
            .performCloseToolOptionsView()
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.TRANSPARENT)
        ToolBarViewInteraction.onToolBarView()
            .performOpenToolOptionsView()
        ShapeToolOptionsViewInteraction.onShapeToolOptionsView()
            .performSelectShape(shape)
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }
}
