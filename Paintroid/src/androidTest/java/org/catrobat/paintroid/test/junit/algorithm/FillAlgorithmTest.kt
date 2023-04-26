/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.paintroid.test.junit.algorithm

import android.graphics.Bitmap
import android.graphics.Point
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.paintroid.tools.helper.JavaFillAlgorithm
import org.catrobat.paintroid.tools.implementation.MAX_ABSOLUTE_TOLERANCE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

private const val HALF_TOLERANCE = MAX_ABSOLUTE_TOLERANCE / 2.0f

@RunWith(AndroidJUnit4::class)
class FillAlgorithmTest {
    @Test
    fun testFillAlgorithmMembers() {
        val width = 10
        val height = 20
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val clickedPixel = Point(width / 2, height / 2)
        val targetColor = 16_777_215
        val replacementColor = 0
        val fillAlgorithm = JavaFillAlgorithm()
        fillAlgorithm.setParameters(
            bitmap,
            clickedPixel,
            targetColor,
            replacementColor,
            HALF_TOLERANCE
        )
        val algorithmPixels = fillAlgorithm.pixels
        assertEquals("Wrong array size", height, algorithmPixels.size)
        assertEquals("Wrong array size", width, algorithmPixels[0].size)
        val algorithmTargetColor = fillAlgorithm.targetColor
        val algorithmReplacementColor = fillAlgorithm.replacementColor
        val algorithmColorTolerance = fillAlgorithm.colorToleranceThresholdSquared.toFloat()
        assertEquals(
            "Wrong target color",
            targetColor,
            algorithmTargetColor
        )
        assertEquals(
            "Wrong replacement color",
            replacementColor,
            algorithmReplacementColor
        )
        assertEquals(
            "Wrong color tolerance",
            HALF_TOLERANCE * HALF_TOLERANCE,
            algorithmColorTolerance
        )
        val algorithmClickedPixel = fillAlgorithm.clickedPixel
        assertEquals("Wrong point for clicked pixel", clickedPixel, algorithmClickedPixel)
        val algorithmRanges = fillAlgorithm.ranges
        assertTrue("Queue for ranges should be empty", algorithmRanges.isEmpty())
    }
}
