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

package org.catrobat.paintroid.test.integration;

import java.util.ArrayList;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.Tool.ToolType;
import org.catrobat.paintroid.ui.button.ToolButtonAdapter;
import org.catrobat.paintroid.ui.implementation.DrawingSurfaceImplementation;
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
	protected final int VERSION_ICE_CREAM_SANDWICH = 14;

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
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		Log.d(PaintroidApplication.TAG, "set up end");
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		int step = 0;
		mSolo.goBackToActivity("MainActivity");
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
		Log.i(PaintroidApplication.TAG, "td " + step++);
		if (mSolo.getAllOpenedActivities().size() > 0) {
			Log.i(PaintroidApplication.TAG, "td finish " + step++);
			mSolo.finishOpenedActivities();
		}
		Log.i(PaintroidApplication.TAG, "td finish " + step++);
		super.tearDown();
		Log.i(PaintroidApplication.TAG, "td finish " + step++);
		mSolo = null;

	}

	protected void selectTool(ToolType toolType) {
		int[] toolButtonInfoArray = getToolButtonIDForType(toolType);
		if (toolButtonInfoArray[0] >= 0) {
			Log.i(PaintroidApplication.TAG, "selectTool:" + toolType.toString() + " with ID: " + toolButtonInfoArray);
			mSolo.clickOnView(mMenuBottomTool);
			assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));
			if (toolButtonInfoArray[1] != mSolo.getCurrentImageViews().size()) {
				mSolo.sleep(2000);
				assertEquals("Wrong number of images possible fail click on image", toolButtonInfoArray[1], mSolo
						.getCurrentImageViews().size());
			}
			mSolo.clickOnImage(toolButtonInfoArray[0]);
			assertTrue("Waiting for tool to change -> MainActivity", mSolo.waitForActivity("MainActivity", TIMEOUT));
			assertEquals("Check switch to correct type", toolType, PaintroidApplication.CURRENT_TOOL.getToolType());
		} else {
			Log.i(PaintroidApplication.TAG, "No tool button id found for " + toolType.toString());
		}
	}

	protected void clickLongOnTool(ToolType toolType) {
		mSolo.clickOnView(mMenuBottomTool);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		ArrayList<GridView> gridViews = mSolo.getCurrentGridViews();
		assertEquals("One GridView should be visible", gridViews.size(), 1);
		GridView toolGrid = gridViews.get(0);
		assertEquals("GridView is Tools Gridview", toolGrid.getId(), R.id.gridview_tools_menu);
		int count = -1;
		mSolo.clickLongOnView(toolGrid.getChildAt(getToolButtonIDForType(toolType)[0]));

	}

	protected int[] getToolButtonIDForType(ToolType toolType) {
		ToolButtonAdapter toolButtonAdapter = new ToolButtonAdapter(getActivity(), false);
		for (int position = 0; position < toolButtonAdapter.getCount(); position++) {
			ToolType currentToolType = toolButtonAdapter.getToolButton(position).buttonId;
			if (currentToolType == toolType) {
				return new int[] { position, toolButtonAdapter.getCount() };
			}
		}
		// fail("no button with tooltype '" + toolType.toString() + "' available!");
		return new int[] { -1, -1 };
	}
}
