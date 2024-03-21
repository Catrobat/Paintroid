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
package org.catrobat.paintroid.ui.tools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.chip.Chip
import org.catrobat.paintroid.R
import org.catrobat.paintroid.tools.options.ClipboardToolOptionsView

class DefaultClipboardToolOptionsView(rootView: ViewGroup) : ClipboardToolOptionsView {
    private val pasteChip: Chip
    private val copyChip: Chip
    private val cutChip: Chip
    private val shapeSizeChip: Chip
    private val changeSizeShapeSizeChip: Chip
    private val clipboardToolOptionsView: View
    private var changeSizeShapeSizeChipVisible = false

    private var callback: ClipboardToolOptionsView.Callback? = null

    private fun initializeListeners() {
        copyChip.setOnClickListener {
            callback?.copyClicked()
        }

        cutChip.setOnClickListener {
            callback?.cutClicked()
        }

        pasteChip.setOnClickListener {
            callback?.pasteClicked()
        }
    }

    override fun setCallback(callback: ClipboardToolOptionsView.Callback) {
        this.callback = callback
    }

    override fun enablePaste(enable: Boolean) {
        pasteChip.isEnabled = enable
    }

    override fun toggleShapeSizeVisibility(isVisible: Boolean) {
        if (isVisible && !changeSizeShapeSizeChipVisible && clipboardToolOptionsView.visibility == View.INVISIBLE) {
            changeSizeShapeSizeChip.visibility = View.VISIBLE
        } else {
            if (isVisible && clipboardToolOptionsView.visibility == View.GONE) changeSizeShapeSizeChip.visibility = View.VISIBLE
            if (!isVisible) changeSizeShapeSizeChip.visibility = View.GONE
        }
        changeSizeShapeSizeChipVisible = isVisible
    }

    override fun getClipboardToolOptionsLayout(): View = clipboardToolOptionsView

    override fun setShapeSizeText(shapeSize: String) {
        shapeSizeChip.setText(shapeSize)
        changeSizeShapeSizeChip.setText(shapeSize)
    }

    init {
        val inflater = LayoutInflater.from(rootView.context)
        val stampToolOptionsView: View =
            inflater.inflate(R.layout.dialog_pocketpaint_clipboard_tool, rootView)
        copyChip = stampToolOptionsView.findViewById(R.id.action_copy)
        pasteChip = stampToolOptionsView.findViewById(R.id.action_paste)
        cutChip = stampToolOptionsView.findViewById(R.id.action_cut)
        enablePaste(false)
        initializeListeners()
        stampToolOptionsView.run {
            val viewShapeSizeLayout =
                findViewById<LinearLayout>(R.id.pocketpaint_layout_clipboard_tool_options_view_shape_size)
            shapeSizeChip = viewShapeSizeLayout.findViewById(R.id.pocketpaint_fill_shape_size_text)
            val changeShapeSizeLayout =
                findViewById<LinearLayout>(R.id.pocketpaint_layout_clipboard_tool_change_size_shape_size)
            changeSizeShapeSizeChip = changeShapeSizeLayout.findViewById(R.id.pocketpaint_fill_shape_size_text)
            clipboardToolOptionsView = findViewById(R.id.pocketpaint_layout_clipboard_tool_options)
        }
        toggleShapeSizeVisibility(false)
    }
}
