/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.MainActivityContracts

class TopBarViewHolder(val layout: ViewGroup) : MainActivityContracts.TopBarViewHolder {
    private val toolbar: Toolbar = layout.findViewById(R.id.pocketpaint_toolbar)
    val undoButton: ImageButton = layout.findViewById(R.id.pocketpaint_btn_top_undo)
    val redoButton: ImageButton = layout.findViewById(R.id.pocketpaint_btn_top_redo)
    val checkmarkButton: ImageButton = layout.findViewById(R.id.pocketpaint_btn_top_checkmark)
    var plusButton: ImageButton = layout.findViewById(R.id.pocketpaint_btn_top_plus)

    override val height: Int
        get() = layout.height

    override fun enableUndoButton() {
        undoButton.isEnabled = true
    }

    override fun disableUndoButton() {
        undoButton.isEnabled = false
    }

    override fun enableRedoButton() {
        redoButton.isEnabled = true
    }

    override fun disableRedoButton() {
        redoButton.isEnabled = false
    }

    override fun hide() {
        layout.visibility = View.GONE
    }

    override fun show() {
        layout.visibility = View.VISIBLE
    }

    fun hidePlusButton() {
        plusButton.visibility = View.GONE
    }

    fun showPlusButton() {
        plusButton.visibility = View.VISIBLE
    }

    override fun removeStandaloneMenuItems(menu: Menu?) {
        menu?.apply {
            removeItem(R.id.pocketpaint_options_save_image)
            removeItem(R.id.pocketpaint_options_save_duplicate)
            removeItem(R.id.pocketpaint_options_new_image)
            removeItem(R.id.pocketpaint_options_rate_us)
        }
    }

    override fun removeCatroidMenuItems(menu: Menu?) {
        menu?.apply {
            removeItem(R.id.pocketpaint_options_export)
            removeItem(R.id.pocketpaint_options_discard_image)
        }
    }

    override fun hideTitleIfNotStandalone() {
        toolbar.title = ""
    }
}
