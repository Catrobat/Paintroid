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

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.MainActivityContracts
import org.catrobat.paintroid.contract.MainActivityContracts.BottomNavigationAppearance
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.ui.BottomNavigationLandscape
import org.catrobat.paintroid.ui.BottomNavigationPortrait

class BottomNavigationViewHolder(
    private val layout: View,
    private val orientation: Int,
    context: Context
) :
    MainActivityContracts.BottomNavigationViewHolder {
    val bottomNavigationView: BottomNavigationView =
        layout.findViewById(R.id.pocketpaint_bottom_navigation)
    private val bottomNavigation: BottomNavigationAppearance
    private val colorButton: ImageView

    init {
        bottomNavigation = setAppearance(context)
        val bottomNavigationMenuView =
            bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        val item = bottomNavigationMenuView.getChildAt(2) as BottomNavigationItemView
        colorButton = item.findViewById(R.id.icon)
        initColorButton()
    }

    override fun show() {
        layout.visibility = View.VISIBLE
    }

    override fun hide() {
        layout.visibility = View.GONE
    }

    override fun showCurrentTool(toolType: ToolType) {
        bottomNavigation.showCurrentTool(toolType)
    }

    override fun setColorButtonColor(color: Int) {
        colorButton.setColorFilter(color)
    }

    private fun initColorButton() {
        colorButton.apply {
            scaleType = ImageView.ScaleType.FIT_XY
            setBackgroundColor(Color.WHITE)
            setPadding(2, 2, 2, 2)
        }
    }

    private fun setAppearance(context: Context): BottomNavigationAppearance =
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            BottomNavigationPortrait(bottomNavigationView)
        } else {
            BottomNavigationLandscape(context, bottomNavigationView)
        }
}
