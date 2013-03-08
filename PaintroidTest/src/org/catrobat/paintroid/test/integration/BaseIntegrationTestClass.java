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
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.button.ToolsAdapter;
import org.junit.After;
import org.junit.Before;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;

import com.jayway.android.robotium.solo.Solo;

public class BaseIntegrationTestClass extends ActivityInstrumentationTestCase2<MainActivity> {

	private static final int DEFAULT_BRUSH_WIDTH = 25;
	private static final Cap DEFAULT_BRUSH_CAP = Cap.ROUND;
	private static final int DEFAULT_COLOR = Color.BLACK;

	protected Solo mSolo;
	protected ImageButton mButtonTopUndo;
	protected ImageButton mButtonTopRedo;
	protected ImageButton mButtonTopTool;
	protected ImageButton mButtonTopColor;
	protected View mMenuBottomTool;
	protected View mMenuBottomParameter1;
	protected View mMenuBottomParameter2;
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected static final int TIMEOUT = 2000;
	protected boolean mTestCaseWithActivityFinished = false;
	protected final int VERSION_ICE_CREAM_SANDWICH = 14;
	protected Bitmap mCurrentDrawingSurfaceBitmap;

	public BaseIntegrationTestClass() throws Exception {
		super(MainActivity.class);
	}

	@Override
	@Before
	protected void setUp() {
		int setup = 0;

		try {
			Log.d("Paintroid test", "setup" + setup++);
			super.setUp();
			Log.d("Paintroid test", "setup" + setup++);
			mTestCaseWithActivityFinished = false;
			Log.d("Paintroid test", "setup" + setup++);
			mSolo = new Solo(getInstrumentation(), getActivity());
			Log.d("Paintroid test", "setup" + setup++);
			PaintroidApplication.drawingSurface.destroyDrawingCache();
			Log.d("Paintroid test", "setup" + setup++);
			mButtonTopUndo = (ImageButton) getActivity().findViewById(R.id.btn_status_undo);
			mButtonTopRedo = (ImageButton) getActivity().findViewById(R.id.btn_status_redo);
			mButtonTopTool = (ImageButton) getActivity().findViewById(R.id.btn_status_tool);
			mButtonTopColor = (ImageButton) getActivity().findViewById(R.id.btn_status_color);
			mMenuBottomTool = getActivity().findViewById(R.id.menu_item_tools);
			mMenuBottomParameter1 = getActivity().findViewById(R.id.menu_item_primary_tool_attribute_button);
			mMenuBottomParameter2 = getActivity().findViewById(R.id.menu_item_secondary_tool_attribute_button);
			mScreenWidth = mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
			mScreenHeight = mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
			Log.d("Paintroid test", "setup" + setup++);
			mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
					PaintroidApplication.drawingSurface, "mWorkingBitmap");
		} catch (Exception e) {
			e.printStackTrace();
			fail("setup failed" + e.toString());

		}
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		Log.d(PaintroidApplication.TAG, "set up end");
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		int step = 0;
		Log.i(PaintroidApplication.TAG, "td " + step++);
		if (mTestCaseWithActivityFinished == false)
			PaintroidApplication.drawingSurface.setBitmap(Bitmap.createBitmap(1, 1, Config.ALPHA_8));
		mButtonTopUndo = null;
		mButtonTopRedo = null;
		mButtonTopTool = null;
		mButtonTopColor = null;
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
		resetBrush();

	}

	protected void selectTool(ToolType toolType) {
		int[] toolButtonInfoArray = getToolButtonIDForType(toolType);
		if (toolButtonInfoArray[0] >= 0) {
			Log.i(PaintroidApplication.TAG, "selectTool:" + toolType.toString() + " with ID: " + toolButtonInfoArray[0]
					+ " / " + toolButtonInfoArray[1]);
			mSolo.clickOnView(mMenuBottomTool);
			Log.i(PaintroidApplication.TAG, "clicked on bottom button tool");
			assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));
			if (toolButtonInfoArray[1] != mSolo.getCurrentImageViews().size()) {
				mSolo.sleep(2000);
				assertEquals("Wrong number of images possible fail click on image", toolButtonInfoArray[1], mSolo
						.getCurrentImageViews().size());
			}
			Log.i(PaintroidApplication.TAG, "click on tool image");
			mSolo.clickOnImage(toolButtonInfoArray[0]);

			Log.i(PaintroidApplication.TAG, "clicked on image button for tool");
			assertTrue("Waiting for tool to change -> MainActivity",
					mSolo.waitForActivity(MainActivity.class.getSimpleName(), TIMEOUT));
			mSolo.sleep(500);
			assertEquals("Check switch to correct type", toolType, PaintroidApplication.currentTool.getToolType());
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
		mSolo.clickLongOnView(toolGrid.getChildAt(getToolButtonIDForType(toolType)[0]));

	}

	protected int[] getToolButtonIDForType(ToolType toolType) {
		ToolsAdapter toolButtonAdapter = new ToolsAdapter(getActivity(), false);
		for (int position = 0; position < toolButtonAdapter.getCount(); position++) {
			ToolType currentToolType = toolButtonAdapter.getToolType(position);
			if (currentToolType == toolType) {
				return new int[] { position, toolButtonAdapter.getCount() };
			}
		}
		// fail("no button with tooltype '" + toolType.toString() + "' available!");
		return new int[] { -1, -1 };
	}

	protected void resetBrush() {
		Paint paint = PaintroidApplication.currentTool.getDrawPaint();
		paint.setStrokeWidth(DEFAULT_BRUSH_WIDTH);
		paint.setStrokeCap(DEFAULT_BRUSH_CAP);
		paint.setColor(DEFAULT_COLOR);
		try {
			((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mCanvasPaint"))
					.setStrokeWidth(DEFAULT_BRUSH_WIDTH);
			((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mCanvasPaint"))
					.setStrokeCap(DEFAULT_BRUSH_CAP);
			((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mCanvasPaint"))
					.setColor(DEFAULT_COLOR);

			((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint"))
					.setStrokeWidth(DEFAULT_BRUSH_WIDTH);
			((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint"))
					.setStrokeCap(DEFAULT_BRUSH_CAP);
			((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint"))
					.setColor(DEFAULT_COLOR);

			PrivateAccess.setMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mColorPickerDialog", null);
			PrivateAccess.setMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBrushPickerDialog", null);
		} catch (Exception exception) {
			return;
		}
		// PaintroidApplication.CURRENT_TOOL.changePaintStrokeWidth(DEFAULT_BRUSH_WIDTH);
		// PaintroidApplication.CURRENT_TOOL.changePaintStrokeCap(DEFAULT_BUSH_CAP);
		// PaintroidApplication.CURRENT_TOOL.changePaintColor(DEFAULT_COLOR);
	}
}
