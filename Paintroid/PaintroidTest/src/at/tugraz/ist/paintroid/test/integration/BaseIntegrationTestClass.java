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
package at.tugraz.ist.paintroid.test.integration;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.ui.button.ToolButtonAdapter;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

import com.jayway.android.robotium.solo.Solo;

public class BaseIntegrationTestClass extends ActivityInstrumentationTestCase2<MainActivity> {

	protected Solo mSolo;
	protected Button mButtonTopUndo;
	protected Button mButtonTopRedo;
	protected TextView mButtonTopTool;
	protected TextView mButtonParameterTop1;
	protected TextView mButtonParameterTop2;
	protected View mMenuBottomTool;
	protected View mMenuBottomParameter1;
	protected View mMenuBottomParameter2;
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected static final int TIMEOUT = 20000;
	protected boolean mTestCaseWithActivityFinished = false;

	public BaseIntegrationTestClass() throws Exception {
		super(MainActivity.class);
	}

	@Override
	@Before
	protected void setUp() {
		int setup = 0;

		try {
			Log.d("Paintroid test", "setup" + setup++);
			// at.tugraz.ist.paintroid.test.utils.Utils.doWorkaroundSleepForDrawingSurfaceThreadProblem();
			super.setUp();
			Log.d("Paintroid test", "setup" + setup++);
			mTestCaseWithActivityFinished = false;
			Log.d("Paintroid test", "setup" + setup++);
			mSolo = new Solo(getInstrumentation(), getActivity());
			Log.d("Paintroid test", "setup" + setup++);
			// at.tugraz.ist.paintroid.test.utils.Utils.doWorkaroundSleepForDrawingSurfaceThreadProblem();
			Log.d("Paintroid test", "setup" + setup++);
			((DrawingSurfaceImplementation) PaintroidApplication.DRAWING_SURFACE).destroyDrawingCache();
			Log.d("Paintroid test", "setup" + setup++);
			mButtonTopUndo = (Button) getActivity().findViewById(R.id.btn_status_undo);
			mButtonTopRedo = (Button) getActivity().findViewById(R.id.btn_status_redo);
			mButtonTopTool = (TextView) getActivity().findViewById(R.id.btn_status_tool);
			mButtonParameterTop1 = (TextView) getActivity().findViewById(R.id.btn_status_parameter1);
			mButtonParameterTop2 = (TextView) getActivity().findViewById(R.id.btn_status_parameter2);
			mMenuBottomTool = getActivity().findViewById(R.id.menu_item_tools);
			mMenuBottomParameter1 = getActivity().findViewById(R.id.menu_item_primary_tool_attribute_button);
			mMenuBottomParameter2 = getActivity().findViewById(R.id.menu_item_secondary_tool_attribute_button);
			mScreenWidth = mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
			mScreenHeight = mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
			Log.d("Paintroid test", "setup" + setup++);
		} catch (Exception e) {
			e.printStackTrace();
			fail("setup failed" + e.toString());

		}
		Log.d(PaintroidApplication.TAG, "set up end");
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		int step = 0;
		Log.i(PaintroidApplication.TAG, "td " + step++);
		if (mTestCaseWithActivityFinished == false)
			PaintroidApplication.DRAWING_SURFACE.setBitmap(Bitmap.createBitmap(1, 1, Config.ALPHA_8));
		mButtonTopUndo = null;
		mButtonTopRedo = null;
		mButtonTopTool = null;
		mButtonParameterTop1 = null;
		mButtonParameterTop2 = null;
		mMenuBottomTool = null;
		mMenuBottomParameter1 = null;
		mMenuBottomParameter2 = null;
		// int teardown = 0;
		// Log.d("Paintroid test", "tt" + teardown++);
		Log.i(PaintroidApplication.TAG, "td " + step++);
		if (mSolo.getAllOpenedActivities().size() > 0) {
			Log.i(PaintroidApplication.TAG, "td finish " + step++);
			mSolo.finishOpenedActivities();
		}
		Log.i(PaintroidApplication.TAG, "td finish " + step++);
		super.tearDown();
		Log.i(PaintroidApplication.TAG, "td finish " + step++);
		// Log.d("Paintroid test", "tt" + teardown++);
		// boolean hasStopped = PrivateAccess.getMemberValueBoolean(Activity.class, getActivity(), "mStopped");
		// if (getActivity().isFinishing() == false || hasStopped == true)
		// PaintroidApplication.DRAWING_SURFACE.setBitmap(Bitmap.createBitmap(1, 1, Config.ALPHA_8));
		// Log.d("Paintroid test", "tt" + teardown++);
		// mSolo.sleep(500);
		// mSolo.finishOpenedActivities();
		// Log.d("Paintroid test", "tt" + teardown++);
		// getActivity().finish();
		mSolo = null;

	}

	protected void selectTool(ToolType toolType) {
		mSolo.clickOnView(mMenuBottomTool);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnImage(getToolButtonIDForType(toolType));
		assertTrue("Waiting for tool to change -> MainActivity", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Check switch to correct type", PaintroidApplication.CURRENT_TOOL.getToolType(), toolType);
	}

	protected void clickLongOnTool(ToolType toolType) {
		mSolo.clickOnView(mMenuBottomTool);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		ArrayList<GridView> gridViews = mSolo.getCurrentGridViews();
		assertEquals("One GridView should be visible", gridViews.size(), 1);
		GridView toolGrid = gridViews.get(0);
		assertEquals("GridView is Tools Gridview", toolGrid.getId(), R.id.gridview_tools_menu);
		mSolo.clickLongOnView(toolGrid.getChildAt(getToolButtonIDForType(toolType)));

	}

	private int getToolButtonIDForType(ToolType toolType) {
		ToolButtonAdapter toolButtonAdapter = new ToolButtonAdapter(getActivity(), false);
		for (int position = 0; position < toolButtonAdapter.getCount(); position++) {
			ToolType currentToolType = toolButtonAdapter.getToolButton(position).buttonId;
			if (currentToolType == toolType) {
				return position;
			}
		}
		fail("no button with tooltype '" + toolType.toString() + "' available!");
		return -1;
	}

}
