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

@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso.tools

import android.graphics.Color
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.ShapeToolOptionsViewInteraction.onShapeToolOptionsView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
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
    var shape: DrawableShape? = null

    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    @Test
    fun testEraseWithFilledShape() {
        onToolBarView()
            .performSelectTool(ToolType.SHAPE)
            .performCloseToolOptionsView()
        onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onToolProperties()
            .setColor(Color.TRANSPARENT)
        onToolBarView()
            .performOpenToolOptionsView()
        onShapeToolOptionsView()
            .performSelectShape(shape)
        onToolBarView()
            .performCloseToolOptionsView()
        onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    companion object {
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return listOf(
                arrayOf(DrawableShape.RECTANGLE),
                arrayOf(DrawableShape.OVAL),
                arrayOf(DrawableShape.HEART),
                arrayOf(DrawableShape.STAR)
            )
        }
    }
}
