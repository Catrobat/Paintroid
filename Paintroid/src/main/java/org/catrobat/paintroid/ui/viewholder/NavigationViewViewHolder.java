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

import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.MainActivityContracts;

public class NavigationViewViewHolder implements MainActivityContracts.NavigationDrawerViewHolder {
	public final NavigationView navigationView;
	public final MenuItem navigationMenuExitFullscreen;
	public final MenuItem navigationMenuEnterFullscreen;
	public final MenuItem navigationMenuVersion;

	public NavigationViewViewHolder(NavigationView navigationView) {
		this.navigationView = navigationView;

		Menu navigationViewMenu = navigationView.getMenu();
		navigationMenuExitFullscreen = navigationViewMenu.findItem(R.id.pocketpaint_nav_exit_fullscreen_mode);
		navigationMenuEnterFullscreen = navigationViewMenu.findItem(R.id.pocketpaint_nav_fullscreen_mode);
		navigationMenuVersion = navigationViewMenu.findItem(R.id.pocketpaint_nav_version);
	}

	@Override
	public void removeItem(int id) {
		navigationView.getMenu().removeItem(id);
	}

	@Override
	public void setVersion(String versionString) {
		String appName = navigationView.getContext().getString(R.string.pocketpaint_app_name);
		navigationMenuVersion.setTitle(appName + " v" + versionString);
	}

	@Override
	public void showExitFullScreen() {
		navigationMenuExitFullscreen.setVisible(true);
	}

	@Override
	public void hideExitFullScreen() {
		navigationMenuExitFullscreen.setVisible(false);
	}

	@Override
	public void showEnterFullScreen() {
		navigationMenuEnterFullscreen.setVisible(true);
	}

	@Override
	public void hideEnterFullScreen() {
		navigationMenuEnterFullscreen.setVisible(false);
	}
}
