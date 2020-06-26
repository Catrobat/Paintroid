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
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.BottomNavigationLandscape;
import org.catrobat.paintroid.ui.BottomNavigationPortrait;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

public class BottomNavigationViewHolder implements MainActivityContracts.BottomNavigationViewHolder {
	private final View layout;
	private final BottomNavigationView bottomNavigationView;
	private int orientation;
	private MainActivityContracts.BottomNavigationAppearance bottomNavigation;
	private ImageView colorButton;

	public BottomNavigationViewHolder(View layout, int orientation, Context context) {
		this.layout = layout;
		this.bottomNavigationView = layout.findViewById(R.id.pocketpaint_bottom_navigation);
		this.orientation = orientation;

		setAppearance(context);

		BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
		BottomNavigationItemView item = (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(2);
		colorButton = item.findViewById(R.id.icon);

		initColorButton();
	}

	@Override
	public void show() {
		layout.setVisibility(View.VISIBLE);
	}

	@Override
	public void hide() {
		layout.setVisibility(View.GONE);
	}

	@Override
	public void showCurrentTool(ToolType toolType) {
		bottomNavigation.showCurrentTool(toolType);
	}

	@Override
	public void setColorButtonColor(int color) {
		colorButton.setColorFilter(color);
	}

	private void initColorButton() {
		colorButton.setScaleType(ImageView.ScaleType.FIT_XY);
		colorButton.setBackgroundColor(Color.WHITE);
		colorButton.setPadding(2, 2, 2, 2);
	}

	public BottomNavigationView getBottomNavigationView() {
		return bottomNavigationView;
	}

	private void setAppearance(Context context) {
		if (orientation == SCREEN_ORIENTATION_PORTRAIT) {
			bottomNavigation = new BottomNavigationPortrait(bottomNavigationView);
		} else {
			bottomNavigation = new BottomNavigationLandscape(context, bottomNavigationView);
		}
	}
}
