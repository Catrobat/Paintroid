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

package at.tugraz.ist.paintroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import at.tugraz.ist.paintroid.dialog.DialogHelp;
import at.tugraz.ist.paintroid.ui.button.ToolButton;
import at.tugraz.ist.paintroid.ui.button.ToolButtonAdapter;

public class MenuToolsActivity extends Activity implements OnItemClickListener, OnItemLongClickListener {
	public static final String EXTRA_SELECTED_TOOL = "EXTRA_SELECTED_TOOL";

	protected ToolButtonAdapter mButtonAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tools_menu);

		// mButtonAdapter = new ToolButtonAdapter(this);

		GridView gridview = (GridView) findViewById(R.id.gridview_tools_menu);
		gridview.setAdapter(mButtonAdapter);
		gridview.setOnItemClickListener(this);
		gridview.setOnItemLongClickListener(this);
		gridview.setColumnWidth(95);
		gridview.setGravity(Gravity.CENTER);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View button, int position, long id) {
		ToolButton toolButton = mButtonAdapter.getToolButton(position);
		Intent resultIntent = new Intent();
		resultIntent.putExtra(EXTRA_SELECTED_TOOL, toolButton.buttonId.ordinal());
		getParent().setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		ToolButton toolButton = mButtonAdapter.getToolButton(position);
		new DialogHelp(this, toolButton.stringId).show();
		return true;
	}
}
