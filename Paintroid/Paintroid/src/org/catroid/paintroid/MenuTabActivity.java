/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catroid.paintroid;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MenuTabActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_tab);

		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec tabSpec;
		Intent intent;

		intent = new Intent().setClass(this, MenuFileActivity.class);
		tabSpec = tabHost.newTabSpec("file")
				.setIndicator(getString(R.string.menu_tab_file), res.getDrawable(R.drawable.ic_tab_file))
				.setContent(intent);
		tabHost.addTab(tabSpec);

		intent = new Intent().setClass(this, MenuToolsActivity.class);
		tabSpec = tabHost.newTabSpec("menu")
				.setIndicator(getString(R.string.menu_tab_tools), res.getDrawable(R.drawable.ic_tab_menu))
				.setContent(intent);
		tabHost.addTab(tabSpec);

		tabHost.setCurrentTab(1);
	}

	@Override
	public void finishFromChild(Activity child) {
		super.finish();
		overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
	}
}
