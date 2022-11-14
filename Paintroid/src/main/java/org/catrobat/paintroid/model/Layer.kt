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
package org.catrobat.paintroid.model

import android.graphics.Bitmap
import org.catrobat.paintroid.contract.LayerContracts

const val MAX_LAYER_OPACITY_PERCENTAGE = 100
const val MAX_LAYER_OPACITY_VALUE = 255

open class Layer(override var bitmap: Bitmap) : LayerContracts.Layer {
    override var isVisible: Boolean = true
    override var opacityPercentage: Int = MAX_LAYER_OPACITY_PERCENTAGE

    override fun getValueForOpacityPercentage(): Int = (opacityPercentage.toFloat() / MAX_LAYER_OPACITY_PERCENTAGE * MAX_LAYER_OPACITY_VALUE).toInt()
}
