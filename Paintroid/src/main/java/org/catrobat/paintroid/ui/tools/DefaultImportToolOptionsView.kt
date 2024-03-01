/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2024 The Catrobat Team
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
import org.catrobat.paintroid.tools.options.ImportToolOptionsView

class DefaultImportToolOptionsView(rootView: ViewGroup) : ImportToolOptionsView {
    private val shapeSizeChip: Chip
    private val shapeSizeLayout: LinearLayout
    override fun setShapeSizeText(shapeSize: String) {
        shapeSizeLayout.visibility = View.VISIBLE
        shapeSizeChip.setText(shapeSize)
    }

    override fun setShapeSizeInvisble() {
        shapeSizeLayout.visibility = View.INVISIBLE
    }

    override fun toggleShapeSizeVisibility(isVisible: Boolean) {
        shapeSizeChip.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    init {
        val inflater = LayoutInflater.from(rootView.context)
        val importToolOptionsView: View =
            inflater.inflate(R.layout.dialog_pocketpaint_import_tool, rootView)
        shapeSizeChip = importToolOptionsView.findViewById(R.id.pocketpaint_fill_shape_size_text)
        shapeSizeLayout = importToolOptionsView.findViewById(R.id.pocketpaint_layout_import_tool_shape_size)
    }
}
