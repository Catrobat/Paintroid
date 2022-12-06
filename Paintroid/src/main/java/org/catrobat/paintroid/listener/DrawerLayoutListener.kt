/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.paintroid.listener

import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.presenter.LayerPresenter

class DrawerLayoutListener(
    private val activity: MainActivity,
    private val layerPresenter: LayerPresenter
) : DrawerLayout.DrawerListener {
    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        activity.hideKeyboard()
    }

    override fun onDrawerClosed(drawerView: View) {
        layerPresenter.invalidate()
    }

    override fun onDrawerOpened(drawerView: View) = Unit

    override fun onDrawerStateChanged(newState: Int) = Unit
}
