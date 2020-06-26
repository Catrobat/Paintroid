package org.catrobat.paintroid.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.tools.ToolType;

public class BottomNavigationLandscape implements MainActivityContracts.BottomNavigationAppearance {
	private BottomNavigationMenuView bottomNavigationMenuView;
	private BottomNavigationView bottomNavigationView;

	public BottomNavigationLandscape(Context context, BottomNavigationView bottomNavigationView) {
		this.bottomNavigationView = bottomNavigationView;
		this.bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);

		setAppearance(context);
	}

	@Override
	public void showCurrentTool(ToolType toolType) {
		View item = bottomNavigationMenuView.getChildAt(1);
		ImageView icon = item.findViewById(R.id.icon);
		TextView title = item.findViewById(R.id.title);
		icon.setImageResource(toolType.getDrawableResource());
		title.setText(toolType.getNameResource());
	}

	private void setAppearance(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
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
