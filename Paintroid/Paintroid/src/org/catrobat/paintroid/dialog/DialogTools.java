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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.ToolsDialogActivity;
import org.catrobat.paintroid.ui.button.ToolButtonAdapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;

public class DialogTools extends BaseDialog {

	private static final int NUMBER_OF_ICONS = 4;
	private ToolButtonAdapter mToolButtonAdapter;
	private int mActionBarHeight;
	private final ToolsDialogActivity mParent;

	public DialogTools(Context context, ToolsDialogActivity parent, ToolButtonAdapter toolButtonAdapter,
			int actionBarHeight) {
		super(context);
		mParent = parent;
		mToolButtonAdapter = toolButtonAdapter;
		mActionBarHeight = actionBarHeight;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		Log.i(PaintroidApplication.TAG, "onCreate: " + getClass().getName());
		setContentView(R.layout.tools_menu);
		setCanceledOnTouchOutside(true);
		Log.i(PaintroidApplication.TAG, "0: " + getClass().getName());
		getWindow().setBackgroundDrawable(null);
		getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER);
		Log.i(PaintroidApplication.TAG, "1: " + getClass().getName());
		getWindow().setBackgroundDrawableResource(R.color.transparent);
		Log.i(PaintroidApplication.TAG, "2: " + getClass().getName());
		GridView gridView = (GridView) findViewById(R.id.gridview_tools_menu);
		Log.i(PaintroidApplication.TAG, "3: " + getClass().getName());
		gridView.setAdapter(mToolButtonAdapter);
		Log.i(PaintroidApplication.TAG, "4: " + getClass().getName());

		gridView.setOnItemClickListener(mParent);
		Log.i(PaintroidApplication.TAG, "5: " + getClass().getName());
		gridView.setOnItemLongClickListener(mParent);
		Log.i(PaintroidApplication.TAG, "6: " + getClass().getName());

		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		Log.i(PaintroidApplication.TAG, "7: " + getClass().getName());
		layoutParams.y = mActionBarHeight;
		Log.i(PaintroidApplication.TAG, "8: " + getClass().getName());
		layoutParams.x = mParent.getResources().getDisplayMetrics().widthPixels / 2 / NUMBER_OF_ICONS;
		Log.i(PaintroidApplication.TAG, "9: " + getClass().getName());
		getWindow().setAttributes(layoutParams);
		Log.i(PaintroidApplication.TAG, "10: " + getClass().getName());
	}

	@Override
	protected void onStop() {
		Log.i(PaintroidApplication.TAG, getClass().getName() + " onStop()");
		super.onStop();
		Log.i(PaintroidApplication.TAG, getClass().getName() + " super.onStop() ");
		mParent.finish();
		Log.i(PaintroidApplication.TAG, getClass().getName() + " parent.finish()");
	}

}
