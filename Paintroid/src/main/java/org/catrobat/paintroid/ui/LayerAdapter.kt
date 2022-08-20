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
package org.catrobat.paintroid.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.controller.DefaultToolController
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.ui.viewholder.BottomNavigationViewHolder

class LayerAdapter(val presenter: LayerContracts.Presenter) : BaseAdapter(), LayerContracts.Adapter {
    private val viewHolders: SparseArray<LayerContracts.LayerViewHolder> = SparseArray()

    override fun getCount() = presenter.layerCount

    override fun getItem(position: Int): LayerContracts.Layer = presenter.getLayerItem(position)

    override fun getItemId(position: Int) = presenter.getLayerItemId(position)

    override fun notifyDataSetChanged() {
        viewHolders.clear()
        super.notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var localConvertView = convertView
        val viewHolder: LayerContracts.LayerViewHolder
        if (localConvertView == null) {
            val inflater = LayoutInflater.from(parent.context)
            localConvertView = inflater.inflate(R.layout.pocketpaint_item_layer, parent, false)
            viewHolder = LayerViewHolder(localConvertView, presenter)
            localConvertView.tag = viewHolder
        } else {
            viewHolder = localConvertView.tag as LayerContracts.LayerViewHolder
        }
        viewHolders.put(position, viewHolder)
        val isShown = presenter.isShown()
        presenter.onBindLayerViewHolderAtPosition(position, viewHolder, isShown)
        val checkBox = localConvertView?.findViewById<CheckBox>(R.id.pocketpaint_checkbox_layer)
        checkBox?.setOnClickListener {
            with(presenter) {
                if (checkBox.isChecked) {
                    unhideLayer(position, viewHolder)
                    getLayerItem(position).isVisible = true
                } else {
                    hideLayer(position)
                    getLayerItem(position).isVisible = false
                }
            }
        }

        val dragHandle = localConvertView?.findViewById<AppCompatImageView>(R.id.pocketpaint_layer_drag_handle)
        dragHandle?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> localConvertView?.let {
                    presenter.onStartDragging(position, it)
                }
                MotionEvent.ACTION_UP -> presenter.onStopDragging()
            }

            true
        }

        return localConvertView
    }

    override fun getViewHolderAt(position: Int): LayerContracts.LayerViewHolder? = viewHolders[position]

    internal class LayerViewHolder(private val itemView: View, private val layerPresenter: LayerContracts.Presenter) : LayerContracts.LayerViewHolder {
        private val layerBackground: LinearLayout = itemView.findViewById(R.id.pocketpaint_item_layer_background)
        private val imageView: ImageView = itemView.findViewById(R.id.pocketpaint_item_layer_image)
        private var currentBitmap: Bitmap? = null
        private val layerVisibilityCheckbox: CheckBox = itemView.findViewById(R.id.pocketpaint_checkbox_layer)
        private var isSelected = false

        companion object {
            private const val RESIZE_LENGTH = 400f
        }

        override val bitmap: Bitmap?
            get() = currentBitmap

        override val view: View
            get() = itemView

        override fun setSelected(position: Int, bottomNavigationViewHolder: BottomNavigationViewHolder?, defaultToolController: DefaultToolController?) {
            if (!layerPresenter.getLayerItem(position).isVisible) {
                defaultToolController?.switchTool(ToolType.HAND)
                bottomNavigationViewHolder?.showCurrentTool(ToolType.HAND)
            }
            layerBackground.setBackgroundColor(Color.BLUE)
            isSelected = true
        }

        override fun setSelected() {
            layerBackground.setBackgroundColor(Color.BLUE)
            isSelected = true
        }

        override fun setDeselected() {
            layerBackground.setBackgroundResource(R.color.pocketpaint_colorPrimary)
            isSelected = false
        }

        override fun isSelected() = isSelected

        override fun updateImageView(bitmap: Bitmap?) {
            runBlocking {
                launch {
                    imageView.setImageBitmap(bitmap?.let { resizeBitmap(it) })
                }
            }
            currentBitmap = bitmap
        }

        private fun resizeBitmap(bitmap: Bitmap): Bitmap {
            val newWidth: Float
            val newHeight: Float
            if (bitmap.width > bitmap.height) {
                newWidth = RESIZE_LENGTH
                newHeight = RESIZE_LENGTH * (bitmap.height.toFloat() / bitmap.width.toFloat()) + 1
            } else {
                newWidth = RESIZE_LENGTH * (bitmap.width.toFloat() / bitmap.height.toFloat()) + 1
                newHeight = RESIZE_LENGTH
            }
            return Bitmap.createScaledBitmap(bitmap, newWidth.toInt(), newHeight.toInt(), false)
        }

        override fun setLayerVisibilityCheckbox(setTo: Boolean) {
            layerVisibilityCheckbox.isChecked = setTo
        }

        override fun setMergable() = layerBackground.setBackgroundResource(R.color.pocketpaint_color_merge_layer)
    }
}
