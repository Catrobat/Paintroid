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

import android.graphics.Paint
import android.graphics.Paint.Cap
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.mainActivity
import org.catrobat.paintroid.tools.Tool
import org.junit.Assert

class ToolPropertiesInteraction private constructor() : CustomViewInteraction(null) {
    fun checkMatchesColor(@ColorInt expectedColor: Int): ToolPropertiesInteraction {
        this.getCurrentTool()?.drawPaint?.getColor()
            ?.let { Assert.assertEquals(expectedColor.toLong(), it.toLong()) }
        return this
    }

    fun checkDoesNotMatchColor(@ColorInt color: Int): ToolPropertiesInteraction {
        this.getCurrentTool()?.drawPaint?.getColor()
            ?.let { Assert.assertNotEquals(color.toLong(), it.toLong()) }
        return this
    }
    fun getCurrentTool(): Tool? {
        return mainActivity.toolReference.tool
    }

    fun checkMatchesColorResource(@ColorRes expectedColorRes: Int): ToolPropertiesInteraction {
        val expectedColor = ContextCompat.getColor(
            InstrumentationRegistry.getInstrumentation().targetContext,
            expectedColorRes
        )
        return checkMatchesColor(expectedColor)
    }

    fun checkCap(expectedCap: Cap): ToolPropertiesInteraction {
        val strokePaint: Paint = this.getCurrentTool()!!.drawPaint
        Assert.assertEquals(expectedCap, strokePaint.strokeCap)
        return this
    }

    fun setCap(expectedCap: Cap): ToolPropertiesInteraction {
        this.getCurrentTool()!!.changePaintStrokeCap(expectedCap)
        return this
    }

    fun checkStrokeWidth(expectedStrokeWidth: Float): ToolPropertiesInteraction {
        val strokePaint: Paint = this.getCurrentTool()!!.drawPaint
        Assert.assertEquals(expectedStrokeWidth, strokePaint.strokeWidth, Float.MIN_VALUE)
        return this
    }

    fun setStrokeWidth(expectedStrokeWidth: Float): ToolPropertiesInteraction {
        this.getCurrentTool()!!.changePaintStrokeWidth(expectedStrokeWidth.toInt())
        return this
    }

    fun setColor(color: Int): ToolPropertiesInteraction {
        this.getCurrentTool()!!.changePaintColor(color)
        return this
    }

    fun setColorResource(@ColorRes colorResource: Int): ToolPropertiesInteraction {
        val color = ContextCompat.getColor(
            InstrumentationRegistry.getInstrumentation().targetContext,
            colorResource
        )
        return setColor(color)
    }

    fun setColorPreset(colorPresetPosition: Int): ToolPropertiesInteraction {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val presetColors =
            targetContext.resources.getIntArray(R.array.pocketpaint_color_picker_preset_colors)
        return setColor(presetColors[colorPresetPosition])
    }

    companion object {
        @JvmStatic
		fun onToolProperties(): ToolPropertiesInteraction {
            return ToolPropertiesInteraction()
        }
    }
}