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
package org.catrobat.paintroid.contract

import android.graphics.Bitmap
import android.view.View
import androidx.annotation.StringRes
import org.catrobat.paintroid.controller.DefaultToolController
import org.catrobat.paintroid.ui.DrawingSurface
import org.catrobat.paintroid.ui.viewholder.BottomNavigationViewHolder

interface LayerContracts {
    interface Adapter {
        fun notifyDataSetChanged()

        fun getViewHolderAt(position: Int): LayerViewHolder?

        fun setDrawerLayoutOpen(isOpen: Boolean)
    }

    interface Presenter {
        val layerCount: Int
        val presenter: Presenter

        fun onBindLayerViewHolderAtPosition(
            position: Int,
            viewHolder: LayerViewHolder,
            isOpen: Boolean
        )

        fun refreshLayerMenuViewHolder()

        fun getLayerItem(position: Int): Layer

        fun getLayerItemId(position: Int): Long

        fun addLayer()

        fun removeLayer()

        fun hideLayer(position: Int)

        fun unhideLayer(position: Int, viewHolder: LayerViewHolder)

        fun setAdapter(layerAdapter: Adapter)

        fun setDrawingSurface(drawingSurface: DrawingSurface)

        fun invalidate()

        fun setDefaultToolController(defaultToolController: DefaultToolController)

        fun setBottomNavigationViewHolder(bottomNavigationViewHolder: BottomNavigationViewHolder)
    }

    interface LayerViewHolder {
        val bitmap: Bitmap?
        val view: View

        fun setSelected(
            position: Int,
            bottomNavigationViewHolder: BottomNavigationViewHolder?,
            defaultToolController: DefaultToolController?
        )

        fun setSelected()

        fun setDeselected()

        fun updateImageView(bitmap: Bitmap?, isDrawerLayoutOpen: Boolean)

        fun setMergable()

        fun setCheckBox(setTo: Boolean)
    }

    interface LayerMenuViewHolder {
        fun disableAddLayerButton()

        fun enableAddLayerButton()

        fun disableRemoveLayerButton()

        fun enableRemoveLayerButton()
    }

    interface Layer {
        var bitmap: Bitmap?
        var transparentBitmap: Bitmap?
        var checkBox: Boolean

        fun switchBitmaps(isUnhide: Boolean)
    }

    interface Model {
        val layers: List<Layer>
        var currentLayer: Layer?
        var width: Int
        var height: Int
        val layerCount: Int

        fun reset()

        fun getLayerAt(index: Int): Layer?

        fun getLayerIndexOf(layer: Layer): Int

        fun addLayerAt(index: Int, layer: Layer): Boolean

        fun listIterator(index: Int): ListIterator<Layer>

        fun setLayerAt(position: Int, layer: Layer)

        fun removeLayerAt(position: Int): Boolean
    }

    interface Navigator {
        fun showToast(@StringRes id: Int, length: Int)
    }
}
