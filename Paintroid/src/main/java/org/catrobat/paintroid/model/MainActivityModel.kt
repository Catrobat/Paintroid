/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.net.Uri
import org.catrobat.paintroid.colorpicker.ColorHistory
import org.catrobat.paintroid.contract.MainActivityContracts

open class MainActivityModel : MainActivityContracts.Model {
    private var wasInitialAnimationPlayed = false
    override var isOpenedFromCatroid = false
    override var isOpenedFromFormulaEditorInCatroid = false
    override var isFullscreen = false
    override var isSaved = false
    override var savedPictureUri: Uri? = null
    override var cameraImageUri: Uri? = null
    override var colorHistory: ColorHistory = ColorHistory()

    override fun wasInitialAnimationPlayed(): Boolean = wasInitialAnimationPlayed

    override fun setInitialAnimationPlayed(wasInitialAnimationPlayed: Boolean) {
        this.wasInitialAnimationPlayed = wasInitialAnimationPlayed
    }
}
