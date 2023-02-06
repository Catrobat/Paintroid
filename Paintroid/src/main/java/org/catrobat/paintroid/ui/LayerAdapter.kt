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
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.databinding.PocketpaintItemLayerBinding
import org.catrobat.paintroid.databinding.PocketpaintLayoutMainMenuLayerBinding
import org.catrobat.paintroid.model.MAX_LAYER_OPACITY_PERCENTAGE
import org.catrobat.paintroid.tools.helper.DefaultNumberRangeFilter

private const val MIN_VAL = 0
private const val MAX_VAL = 100

private const val RESIZE_LENGTH = 400f

class LayerAdapter(
    val presenter: LayerContracts.Presenter,
    val mainActivity: MainActivity,
) : RecyclerView.Adapter<LayerAdapter.LayerViewHolder>(), LayerContracts.Adapter {
    private val viewHolders: SparseArray<LayerContracts.LayerViewHolder> = SparseArray()
    private lateinit var binding:PocketpaintItemLayerBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.pocketpaint_item_layer, parent, false)
        binding = PocketpaintItemLayerBinding.bind(itemView)
        return LayerViewHolder(itemView, presenter)
    }

    override fun onBindViewHolder(holder: LayerViewHolder, position: Int) {
        viewHolders.put(position, holder)
        holder.bindView()
    }

    override fun getItemCount(): Int = presenter.layerCount

    fun clearViewHolders() {
        viewHolders.clear()
    }

    override fun getViewHolderAt(position: Int): LayerContracts.LayerViewHolder? = viewHolders[position]

    inner class LayerViewHolder(
        itemView: View,
        private val layerPresenter: LayerContracts.Presenter
    ) : LayerContracts.LayerViewHolder, RecyclerView.ViewHolder(itemView) {
        private val layerBackground: LinearLayout = binding.pocketpaintItemLayer
        private val imageView: ImageView = binding.pocketpaintItemLayerImage
        private val dragHandle: ImageView = binding.pocketpaintLayerDragHandle
        private val opacitySeekBar: SeekBar = binding.pocketpaintLayerOpacitySeekbar
        private val opacityEditText: AppCompatEditText = binding.pocketpaintLayerOpacityValue
        private var currentBitmap: Bitmap? = null
        private val layerVisibilityCheckbox: CheckBox = binding.pocketpaintCheckboxLayer
        private var isSelected = false

        override val bitmap: Bitmap?
            get() = currentBitmap

        override val view: View
            get() = itemView

        override fun bindView() {
            val layer = layerPresenter.getLayerItem(position)
            val isSelected = layer === layerPresenter.getSelectedLayer()
            setSelected(isSelected)
            setLayerVisibilityCheckbox(layer.isVisible)
            updateImageView(layer)

            layerVisibilityCheckbox.setOnClickListener {
                val isVisible = layerVisibilityCheckbox.isChecked
                layerPresenter.setLayerVisibility(position, isVisible)
            }

            layerBackground.setOnClickListener {
                layerPresenter.setLayerSelected(position)
            }

            dragHandle.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> layerPresenter.onStartDragging(position, itemView)
                    MotionEvent.ACTION_UP -> layerPresenter.onStopDragging()
                }

                true
            }

            opacitySeekBar.progress = layerPresenter.getLayerItem(position).opacityPercentage
            opacityEditText.filters = arrayOf<InputFilter>(DefaultNumberRangeFilter(MIN_VAL, MAX_VAL))
            opacityEditText.setText(layerPresenter.getLayerItem(position).opacityPercentage.toString())
            opacityEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

                override fun afterTextChanged(s: Editable?) {
                    val opacityText = s.toString()
                    val opacityPercentage: Int = try {
                        opacityText.toInt()
                    } catch (exp: NumberFormatException) {
                        exp.localizedMessage?.let {
                            Log.d(TAG, it)
                        }
                        MAX_VAL
                    }

                    if (opacityPercentage != opacitySeekBar.progress) {
                        opacitySeekBar.progress = opacityPercentage
                        layerPresenter.changeLayerOpacity(position, opacityPercentage)
                    }

                    layerPresenter.getLayerItem(position).opacityPercentage = opacityPercentage
                    layerPresenter.refreshDrawingSurface()
                }
            })

            opacitySeekBar.setOnTouchListener { view, _ ->
                view.parent.requestDisallowInterceptTouchEvent(true)
                false
            }

            opacitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        opacityEditText.setText(progress.toString())
                    }

                    imageView.alpha = progress.toFloat() / MAX_LAYER_OPACITY_PERCENTAGE
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    layerPresenter.changeLayerOpacity(position, seekBar.progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
            })
        }

        override fun setSelected(isSelected: Boolean) {
            if (isSelected) {
                layerBackground.setBackgroundColor(Color.BLUE)
            } else {
                layerBackground.setBackgroundResource(R.color.pocketpaint_colorPrimary)
            }
            this.isSelected = isSelected
        }

        override fun isSelected(): Boolean = isSelected

        override fun updateImageView(layer: LayerContracts.Layer) {
            runBlocking {
                launch {
                    imageView.setImageBitmap(resizeBitmap(layer.bitmap))
                    imageView.alpha = layer.opacityPercentage.toFloat() / MAX_LAYER_OPACITY_PERCENTAGE
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

    companion object {
        private val TAG = LayerAdapter::class.java.simpleName
    }
}
