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
package org.catrobat.paintroid.colorpicker

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Color
import android.graphics.Shader.TileMode
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageButton
import org.catrobat.paintroid.colorpicker.ColorPickerDialog.CustomColorDrawable.Companion.createDrawable
import kotlin.jvm.JvmOverloads

class ColorPickerPresetColorButton @JvmOverloads constructor(
    context: Context,
    @field:ColorInt @get:ColorInt
    @param:ColorInt var color: Int = Color.BLACK
) : AppCompatImageButton(context, null, R.attr.borderlessButtonStyle) {

    init {
        val checkeredBitmap =
            BitmapFactory.decodeResource(resources, R.drawable.pocketpaint_checkeredbg)
        val bitmapShader = BitmapShader(checkeredBitmap, TileMode.REPEAT, TileMode.REPEAT)
        background = createDrawable(bitmapShader, color)
    }
}
