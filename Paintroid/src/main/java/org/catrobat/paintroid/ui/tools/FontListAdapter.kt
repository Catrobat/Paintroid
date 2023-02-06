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

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import org.catrobat.paintroid.R
import org.catrobat.paintroid.databinding.PocketpaintItemFontBinding
import org.catrobat.paintroid.tools.FontType

class FontListAdapter internal constructor(
    context: Context,
    private val fontTypes: List<FontType>,
    private val onFontChanged: (FontType) -> Unit
) : RecyclerView.Adapter<FontListAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var selectedIndex = 0
    private lateinit var binding:PocketpaintItemFontBinding

    private val sansSerif = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    private val monospace = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    private val serif = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
    private val dubai = ResourcesCompat.getFont(context, R.font.dubai)
    private val stc = ResourcesCompat.getFont(context, R.font.stc_regular)

    private val typeFaces = arrayOf(
        sansSerif,
        monospace,
        serif,
        dubai,
        stc
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.pocketpaint_item_font, parent, false)
        binding = PocketpaintItemFontBinding.bind(view)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val font = fontTypes[position]
        holder.fontChip.setText(font.nameResource)
        holder.fontChip.typeface = typeFaces[position]
        holder.fontChip.isChecked = position == selectedIndex
    }

    override fun getItemCount(): Int = fontTypes.size

    fun setSelectedIndex(selectedIndex: Int) {
        val oldSelectedIndex = this.selectedIndex
        this.selectedIndex = selectedIndex
        notifyItemChanged(oldSelectedIndex)
        notifyItemChanged(selectedIndex)
    }

    fun getSelectedItem(): FontType = fontTypes[selectedIndex]

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var fontChip: Chip = binding.pocketpaintFontType

        init {
            fontChip.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            setSelectedIndex(layoutPosition)
            onFontChanged(fontTypes[layoutPosition])
        }
    }
}
