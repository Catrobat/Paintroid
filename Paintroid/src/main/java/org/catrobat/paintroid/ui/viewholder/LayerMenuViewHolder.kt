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
package org.catrobat.paintroid.ui.viewholder

import android.view.View
import com.google.android.material.navigation.NavigationView
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.LayerContracts

class LayerMenuViewHolder(private val layerLayout: NavigationView) : LayerContracts.LayerMenuViewHolder {
    val layerAddButton: View = layerLayout.findViewById(R.id.pocketpaint_layer_side_nav_button_add)
    val layerDeleteButton: View = layerLayout.findViewById(R.id.pocketpaint_layer_side_nav_button_delete)
    val layerVisibilityButton: View = layerLayout.findViewById(R.id.pocketpaint_layer_side_nav_button_visibility)
    val layerOpacityButton: View = layerLayout.findViewById(R.id.pocketpaint_layer_side_nav_button_opacity)

    override fun isShown(): Boolean = layerLayout.isShown

    override fun disableAddLayerButton() {
        layerAddButton.isEnabled = false
    }

    override fun enableAddLayerButton() {
        layerAddButton.isEnabled = true
    }

    override fun disableRemoveLayerButton() {
        layerDeleteButton.isEnabled = false
    }

    override fun enableRemoveLayerButton() {
        layerDeleteButton.isEnabled = true
    }

    override fun disableLayerVisibilityButton() {
        layerVisibilityButton.isEnabled = false
    }

    override fun disableLayerOpacityButton() {
        layerOpacityButton.isEnabled = false
    }
}
