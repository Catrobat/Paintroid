/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.dialog;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.ToolsDialogActivity;
import org.catrobat.paintroid.ui.button.ToolsAdapter;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;

public class DialogTools extends BaseDialog {

	private static final int NUMBER_OF_ICONS = 4;
	private ToolsAdapter mToolButtonAdapter;
	private int mActionBarHeight;
	private final ToolsDialogActivity mParent;

	public DialogTools(Context context, ToolsDialogActivity parent,
			ToolsAdapter toolButtonAdapter, int actionBarHeight) {
		super(context);
		mParent = parent;
		mToolButtonAdapter = toolButtonAdapter;
		mActionBarHeight = actionBarHeight;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tools_menu);
		setCanceledOnTouchOutside(true);
		getWindow().setBackgroundDrawable(null);
		getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER);
		getWindow().setBackgroundDrawableResource(R.color.transparent);
		GridView gridView = (GridView) findViewById(R.id.gridview_tools_menu);
		gridView.setAdapter(mToolButtonAdapter);

		gridView.setOnItemClickListener(mParent);
		gridView.setOnItemLongClickListener(mParent);

		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.y = mActionBarHeight;
		layoutParams.x = mParent.getResources().getDisplayMetrics().widthPixels
				/ 2 / NUMBER_OF_ICONS;
		getWindow().setAttributes(layoutParams);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mParent.finish();
	}
}
