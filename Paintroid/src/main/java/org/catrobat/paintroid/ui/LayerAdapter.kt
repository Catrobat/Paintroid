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
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.catrobat.paintroid.LanguageHelper
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.iotasks.OpenRasterFileFormatConversion.Companion.mainActivity
import org.catrobat.paintroid.model.MAX_LAYER_OPACITY_PERCENTAGE
import org.catrobat.paintroid.tools.helper.DefaultNumberRangeFilter

private const val MIN_VAL = 0
private const val MAX_VAL = 100
private const val CORNER_RADIUS = 20f
private const val RESIZE_LENGTH = 400f

class LayerAdapter(
    val presenter: LayerContracts.Presenter,
    val mainActivity: MainActivity,
) : RecyclerView.Adapter<LayerAdapter.LayerViewHolder>(), LayerContracts.Adapter {
    private val viewHolders: SparseArray<LayerContracts.LayerViewHolder> = SparseArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.pocketpaint_item_layer, parent, false)
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
        private val layerBackground: LinearLayout = itemView.findViewById(R.id.pocketpaint_item_layer)
        private val imageView: ImageView = itemView.findViewById(R.id.pocketpaint_item_layer_image)
        private val dragHandle: ImageView = itemView.findViewById(R.id.pocketpaint_layer_drag_handle)
        private val opacitySeekBar: SeekBar = itemView.findViewById(R.id.pocketpaint_layer_opacity_seekbar)
        private val opacityEditText: AppCompatEditText = itemView.findViewById(R.id.pocketpaint_layer_opacity_value)
        private var currentBitmap: Bitmap? = null
        private val layerVisibilityCheckbox: CheckBox = itemView.findViewById(R.id.pocketpaint_checkbox_layer)
        private var isSelected = false

        override val bitmap: Bitmap?
            get() = currentBitmap

        override val view: View
            get() = itemView

        override fun bindView() {
            val layer = layerPresenter.getLayerItem(position) ?: return
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

            opacitySeekBar.progress = layer.opacityPercentage
            opacityEditText.filters = arrayOf<InputFilter>(DefaultNumberRangeFilter(MIN_VAL, MAX_VAL))
            opacityEditText.setText(layer.opacityPercentage.toString())
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
            val background = when (getBackgroundType()) {
                BackgroundType.SINGLE -> getSingleBackground()
                BackgroundType.TOP -> getTopBackground(isSelected)
                BackgroundType.BOTTOM -> getBottomBackground(isSelected)
                BackgroundType.CENTER -> getCenterBackground(isSelected)
            }
            layerBackground.background = background
            this.isSelected = isSelected
        }

        override fun isSelected(): Boolean = isSelected

        override fun getViewLayout(): LinearLayout = layerBackground

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

        private fun getBackgroundType(): BackgroundType {
            if (presenter.layerCount > 2 && this.adapterPosition > 0 && this.adapterPosition < presenter.layerCount - 1) {
                return BackgroundType.CENTER
            }
            if (presenter.layerCount == 1) {
                return BackgroundType.SINGLE
            }
            if (this.adapterPosition == presenter.layerCount - 1) {
                return BackgroundType.BOTTOM
            }
            return BackgroundType.TOP
        }
    }

    companion object {
        private val TAG = LayerAdapter::class.java.simpleName

        fun getSingleBackground(): Drawable {
            val background = mainActivity?.let { ContextCompat.getDrawable(it, R.drawable.layer_item_single_selected) }
            (background as GradientDrawable).cornerRadii = getRadius(BackgroundType.SINGLE)
            return background
        }

        fun getTopBackground(isSelected: Boolean): Drawable {
            val background = if (isSelected) {
                mainActivity?.let { ContextCompat.getDrawable(it, R.drawable.layer_item_top_selected) }
            } else {
                mainActivity?.let { ContextCompat.getDrawable(it, R.drawable.layer_item_top_unselected) }
            }
            (background as GradientDrawable).cornerRadii = getRadius(BackgroundType.TOP)
            return background
        }

        fun getBottomBackground(isSelected: Boolean): Drawable {
            val background = if (isSelected) {
                mainActivity?.let { ContextCompat.getDrawable(it, R.drawable.layer_item_bottom_selected) }
            } else {
                mainActivity?.let { ContextCompat.getDrawable(it, R.drawable.layer_item_bottom_unselected) }
            }
            (background as GradientDrawable).cornerRadii = getRadius(BackgroundType.BOTTOM)
            return background
        }

        fun getCenterBackground(isSelected: Boolean): Drawable? {
            return if (isSelected) {
                mainActivity?.let { ContextCompat.getDrawable(it, R.drawable.layer_item_center_selected) }
            } else {
                mainActivity?.let { ContextCompat.getDrawable(it, R.drawable.layer_item_center_unselected) }
            }
        }

        private fun getRadius(backgroundType: BackgroundType): FloatArray {
            val cornerRadius = mainActivity?.let { CORNER_RADIUS * it.resources.displayMetrics.density } ?: 0f

            return when (backgroundType) {
                BackgroundType.TOP -> if (LanguageHelper.isCurrentLanguageRTL()) {
                    floatArrayOf(0f, 0f, cornerRadius, cornerRadius, 0f, 0f, 0f, 0f)
                } else {
                    floatArrayOf(cornerRadius, cornerRadius, 0f, 0f, 0f, 0f, 0f, 0f)
                }
                BackgroundType.BOTTOM -> if (LanguageHelper.isCurrentLanguageRTL()) {
                    floatArrayOf(0f, 0f, 0f, 0f, cornerRadius, cornerRadius, 0f, 0f)
                } else {
                    floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, cornerRadius, cornerRadius)
                }
                BackgroundType.SINGLE -> if (LanguageHelper.isCurrentLanguageRTL()) {
                    floatArrayOf(0f, 0f, cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0f, 0f)
                } else {
                    floatArrayOf(cornerRadius, cornerRadius, 0f, 0f, 0f, 0f, cornerRadius, cornerRadius)
                }
                else -> floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
            }
        }
    }

    enum class BackgroundType {
        TOP,
        BOTTOM,
        CENTER,
        SINGLE
    }
}
