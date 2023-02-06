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
import com.google.android.material.chip.Chip
import org.catrobat.paintroid.R
import org.catrobat.paintroid.databinding.DialogPocketpaintStampToolBinding
import org.catrobat.paintroid.tools.options.StampToolOptionsView
private lateinit var binding: DialogPocketpaintStampToolBinding
class DefaultStampToolOptionsView(rootView: ViewGroup) : StampToolOptionsView {
    private val pasteChip: Chip
    private val copyChip: Chip
    private val cutChip: Chip
    private var callback: StampToolOptionsView.Callback? = null

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

    override fun setCallback(callback: StampToolOptionsView.Callback) {
        this.callback = callback
    }

    override fun enablePaste(enable: Boolean) {
        pasteChip.isEnabled = enable
    }

    init {
        val inflater = LayoutInflater.from(rootView.context)
        val stampToolOptionsView: View =
            inflater.inflate(R.layout.dialog_pocketpaint_stamp_tool, rootView)
        binding = DialogPocketpaintStampToolBinding.bind(stampToolOptionsView)
        copyChip = binding.actionCopy
        pasteChip = binding.actionPaste
        cutChip = binding.actionCut
        enablePaste(false)
        initializeListeners()
    }
}
