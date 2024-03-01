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
package org.catrobat.paintroid.tools.options

import android.view.View
import org.catrobat.paintroid.tools.FontType

interface TextToolOptionsView {
    fun setState(
        bold: Boolean,
        italic: Boolean,
        underlined: Boolean,
        text: String,
        textSize: Int,
        fontType: FontType
    )

    fun setCallback(listener: Callback)

    fun hideKeyboard()

    fun showKeyboard()

    fun getTopLayout(): View

    fun getBottomLayout(): View

    fun setShapeSizeText(shapeSize: String)

    fun toggleShapeSizeVisibility(isVisible: Boolean)

    interface Callback {
        fun setText(text: String)

        fun setFont(fontType: FontType)

        fun setUnderlined(underlined: Boolean)

        fun setItalic(italic: Boolean)

        fun setBold(bold: Boolean)

        fun setTextSize(size: Int)

        fun hideToolOptions()
    }
}
