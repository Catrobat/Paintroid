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
package org.catrobat.paintroid.iotasks

import android.graphics.Bitmap
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.colorpicker.ColorHistory
import org.catrobat.paintroid.model.CommandManagerModel

data class BitmapReturnValue(
    @JvmField
    var model: CommandManagerModel?,
    @JvmField
    var layerList: List<LayerContracts.Layer>?,
    @JvmField
    var bitmap: Bitmap?,
    @JvmField
    var toBeScaled: Boolean,
    @JvmField
    var colorHistory: ColorHistory?
) {
    constructor(bitmapList: List<LayerContracts.Layer>?, bitmap: Bitmap?, toBeScaled: Boolean) : this(
        null,
        bitmapList,
        bitmap,
        toBeScaled,
        null
    )

    constructor(model: CommandManagerModel?, colorHistory: ColorHistory?) : this(model, null, null, false, colorHistory)
}
