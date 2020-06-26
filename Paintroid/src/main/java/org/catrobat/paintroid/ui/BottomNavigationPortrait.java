package org.catrobat.paintroid.ui;

import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.tools.ToolType;

public class BottomNavigationPortrait implements MainActivityContracts.BottomNavigationAppearance {
	private BottomNavigationView bottomNavigationView;

	public BottomNavigationPortrait(BottomNavigationView bottomNavigationView) {
		this.bottomNavigationView = bottomNavigationView;
	}

	@Override
	public void showCurrentTool(ToolType toolType) {
		Menu menu = bottomNavigationView.getMenu();
		MenuItem currentTool = menu.findItem(R.id.action_current_tool);
		currentTool.setIcon(toolType.getDrawableResource());
		currentTool.setTitle(toolType.getNameResource());
	}
}
