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

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.MainActivityContracts.BottomNavigationAppearance
import org.catrobat.paintroid.databinding.PocketpaintLayoutBottomNavigationItemBinding
import org.catrobat.paintroid.tools.ToolType

class BottomNavigationLandscape(context: Context, private val bottomNavigationView: BottomNavigationView) : BottomNavigationAppearance {
    private val bottomNavigationMenuView: BottomNavigationMenuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
    private val binding:PocketpaintLayoutBottomNavigationItemBinding = PocketpaintLayoutBottomNavigationItemBinding.bind(bottomNavigationView)

    init {

        setAppearance(context)
    }

    override fun showCurrentTool(toolType: ToolType) {
        val item = bottomNavigationMenuView.getChildAt(1)
        val icon = binding.icon
        val title = binding.title
        icon.setImageResource(toolType.drawableResource)
        title.setText(toolType.nameResource)
    }

    private fun setAppearance(context: Context) {
        val inflater = LayoutInflater.from(context)
        val menu = bottomNavigationView.menu
        for (i in 0 until menu.size()) {
            val item = bottomNavigationMenuView.getChildAt(i) as BottomNavigationItemView
            val itemBottomNavigation = inflater.inflate(R.layout.pocketpaint_layout_bottom_navigation_item, bottomNavigationMenuView, false)
            val binding:PocketpaintLayoutBottomNavigationItemBinding = PocketpaintLayoutBottomNavigationItemBinding.bind(itemBottomNavigation)
            val icon = binding.icon
            val text = binding.title
            icon.setImageDrawable(menu.getItem(i).icon)
            icon.setColorFilter(ContextCompat.getColor(context, R.color.pocketpaint_welcome_dot_active))
            text.text = menu.getItem(i).title
            item.removeAllViews()
            item.addView(itemBottomNavigation)
        }
    }
}
