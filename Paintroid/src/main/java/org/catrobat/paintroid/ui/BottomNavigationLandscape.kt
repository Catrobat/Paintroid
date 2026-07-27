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
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.MainActivityContracts.BottomNavigationAppearance
import org.catrobat.paintroid.tools.ToolType
import androidx.core.view.size
import androidx.core.view.get

class BottomNavigationLandscape(context: Context, private val bottomNavigationView: BottomNavigationView) : BottomNavigationAppearance {

    init {
        setAppearance(context)
    }

    override fun showCurrentTool(toolType: ToolType) {
        val item = bottomNavigationView.menu[1]
        item.icon = ContextCompat.getDrawable(bottomNavigationView.context, toolType.drawableResource)
        item.title = bottomNavigationView.context.getString(toolType.nameResource)
    }

    private fun setAppearance(context: Context) {
        val inflater = LayoutInflater.from(context)
        val menu = bottomNavigationView.menu
        for (i in 0 until menu.size) {
            val item = menu[i]
            val itemBottomNavigation = inflater.inflate(R.layout.pocketpaint_layout_bottom_navigation_item, bottomNavigationView, false)
            val icon = itemBottomNavigation.findViewById<ImageView>(R.id.icon)
            val text = itemBottomNavigation.findViewById<TextView>(R.id.title)
            icon.setImageDrawable(item.icon)
            icon.setColorFilter(ContextCompat.getColor(context, R.color.pocketpaint_welcome_dot_active))
            text.text = item.title
            if (item.actionView != null) {
                bottomNavigationView.removeView(item.actionView)
            }
            item.actionView = itemBottomNavigation
        }
    }
}
