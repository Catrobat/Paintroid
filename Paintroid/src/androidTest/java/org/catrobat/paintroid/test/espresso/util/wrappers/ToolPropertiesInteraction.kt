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

package org.catrobat.paintroid.test.espresso.util.wrappers

import android.graphics.Paint.Cap
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.mainActivity
import org.catrobat.paintroid.tools.Tool
import org.junit.Assert

class ToolPropertiesInteraction private constructor() : CustomViewInteraction(null) {
    fun checkMatchesColor(@ColorInt expectedColor: Int): ToolPropertiesInteraction {
        Assert.assertEquals(expectedColor.toLong(), currentTool.drawPaint.color.toLong())
        return this
    }

    fun checkDoesNotMatchColor(@ColorInt color: Int): ToolPropertiesInteraction {
        Assert.assertNotEquals(color.toLong(), currentTool.drawPaint.color.toLong())
        return this
    }

    fun checkMatchesColorResource(@ColorRes expectedColorRes: Int): ToolPropertiesInteraction {
        val expectedColor = ContextCompat.getColor(
            InstrumentationRegistry.getInstrumentation().targetContext,
            expectedColorRes
        )
        return checkMatchesColor(expectedColor)
    }

    fun checkCap(expectedCap: Cap?): ToolPropertiesInteraction {
        val strokePaint = currentTool.drawPaint
        Assert.assertEquals(expectedCap, strokePaint.strokeCap)
        return this
    }

    fun setCap(expectedCap: Cap?): ToolPropertiesInteraction {
        currentTool.changePaintStrokeCap(expectedCap!!)
        return this
    }

    fun checkStrokeWidth(expectedStrokeWidth: Float): ToolPropertiesInteraction {
        val strokePaint = currentTool.drawPaint
        Assert.assertEquals(expectedStrokeWidth, strokePaint.strokeWidth, Float.MIN_VALUE)
        return this
    }

    fun setStrokeWidth(expectedStrokeWidth: Float): ToolPropertiesInteraction {
        currentTool.changePaintStrokeWidth(expectedStrokeWidth.toInt())
        return this
    }

    fun setColor(color: Int): ToolPropertiesInteraction {
        currentTool.changePaintColor(color)
        return this
    }

    private val currentTool: Tool
        get() = mainActivity.toolReference as Tool

    fun setColorResource(@ColorRes colorResource: Int): ToolPropertiesInteraction {
        val color = ContextCompat.getColor(
            InstrumentationRegistry.getInstrumentation().targetContext,
            colorResource
        )
        return setColor(color)
    }

    companion object {
        @JvmStatic
		fun onToolProperties(): ToolPropertiesInteraction { return ToolPropertiesInteraction() }
    }
}
