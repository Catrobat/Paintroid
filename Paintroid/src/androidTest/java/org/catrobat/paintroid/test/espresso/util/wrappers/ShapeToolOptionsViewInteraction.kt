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
package org.catrobat.paintroid.test.espresso.util.wrappers

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.paintroid.R
import org.catrobat.paintroid.tools.drawable.DrawableShape
import org.catrobat.paintroid.tools.drawable.DrawableStyle

class ShapeToolOptionsViewInteraction private constructor() :
    CustomViewInteraction(Espresso.onView(withId(R.id.pocketpaint_layout_tool_options))) {
    private fun getButtonIdFromBaseShape(baseShape: DrawableShape): Int {
        return when (baseShape) {
            DrawableShape.RECTANGLE -> R.id.pocketpaint_shapes_square_btn
            DrawableShape.OVAL -> R.id.pocketpaint_shapes_circle_btn
            DrawableShape.HEART -> R.id.pocketpaint_shapes_heart_btn
            DrawableShape.STAR -> R.id.pocketpaint_shapes_star_btn
        }
        throw IllegalArgumentException()
    }

    private fun getButtonIdFromShapeDrawType(shapeDrawType: DrawableStyle): Int {
        return when (shapeDrawType) {
            DrawableStyle.STROKE -> R.id.pocketpaint_shape_ibtn_outline
            DrawableStyle.FILL -> R.id.pocketpaint_shape_ibtn_fill
        }
        throw IllegalArgumentException()
    }

    fun performSelectShape(shape: DrawableShape): ShapeToolOptionsViewInteraction {
        Espresso.onView(ViewMatchers.withId(getButtonIdFromBaseShape(shape)))
            .perform(ViewActions.click())
        return this
    }

    fun performSelectShapeDrawType(shapeDrawType: DrawableStyle): ShapeToolOptionsViewInteraction {
        Espresso.onView(ViewMatchers.withId(getButtonIdFromShapeDrawType(shapeDrawType)))
            .perform(ViewActions.click())
        return this
    }

    fun performSetOutlineWidth(setWidth: ViewAction?) {
        Espresso.onView(withId(R.id.pocketpaint_shape_stroke_width_seek_bar))
            .perform(setWidth)
    }

    companion object {
        fun onShapeToolOptionsView(): ShapeToolOptionsViewInteraction {
            return ShapeToolOptionsViewInteraction()
        }
    }
}