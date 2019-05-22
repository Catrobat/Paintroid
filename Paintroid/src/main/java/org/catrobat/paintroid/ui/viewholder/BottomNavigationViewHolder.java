/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.paintroid.ui.viewholder;

import android.content.Context;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.MainActivityContracts;

public class BottomNavigationViewHolder implements MainActivityContracts.BottomNavigationViewHolder {
	public final View layout;
	private final BottomNavigationView bottomNavigationView;
	private final BottomNavigationMenuView bottomNavigationMenuView;

	public BottomNavigationViewHolder(View layout) {
		this.layout = layout;
		this.bottomNavigationView = layout.findViewById(R.id.pocketpaint_bottom_navigation);
		this.bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
	}

	@Override
	public void show() {
		layout.setVisibility(View.VISIBLE);
	}

	@Override
	public void hide() {
		layout.setVisibility(View.GONE);
	}

	public BottomNavigationView getBottomNavigationView() {
		return bottomNavigationView;
	}

	public void setLandscapeStyle(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Menu menu = bottomNavigationView.getMenu();
		for (int i = 0; i < menu.size(); i++) {
			BottomNavigationItemView item = (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(i);
			View itemBottomNavigation = inflater.inflate(R.layout.pocketpaint_layout_bottom_navigation_item, bottomNavigationMenuView, false);
			ImageView icon = itemBottomNavigation.findViewById(R.id.icon);
			TextView text = itemBottomNavigation.findViewById(R.id.title);
			icon.setImageDrawable(menu.getItem(i).getIcon());
			text.setText(menu.getItem(i).getTitle());
			item.removeAllViews();
			item.addView(itemBottomNavigation);
		}
	}
}
