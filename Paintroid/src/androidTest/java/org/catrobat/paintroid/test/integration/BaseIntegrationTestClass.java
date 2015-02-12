/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
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
import org.catrobat.paintroid.OptionsMenuActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.ToolsDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;
import org.catrobat.paintroid.ui.button.ToolsAdapter;
import org.junit.After;
import org.junit.Before;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageButton;

import com.robotium.solo.Solo;

public class BaseIntegrationTestClass extends ActivityInstrumentationTestCase2<MainActivity> {

	private static final int DEFAULT_BRUSH_WIDTH = 25;
	private static final Cap DEFAULT_BRUSH_CAP = Cap.ROUND;
	private static final int DEFAULT_COLOR = Color.BLACK;

	protected static final int LONG_WAIT_TRIES = 200;
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
	protected static final int SHORT_SLEEP = 50;
	protected static final int SHORT_TIMEOUT = 250;
    protected static final int MEDIUM_TIMEOUT = 1000;
	protected static final int TIMEOUT = 10000;
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

			/*
			 * if (Utils.isScreenLocked(mSolo.getCurrentActivity())) { mScreenLocked = true; tearDown();
			 * assertFalse("Screen is locked!", mScreenLocked); return; }
			 */
			Log.d("Paintroid test", "setup" + setup++);

			PaintroidApplication.drawingSurface.destroyDrawingCache();
			Log.d("Paintroid test", "setup" + setup++);
			mButtonTopUndo = (ImageButton) getActivity().findViewById(R.id.btn_top_undo);
			mButtonTopRedo = (ImageButton) getActivity().findViewById(R.id.btn_top_redo);
			mButtonTopTool = (ImageButton) getActivity().findViewById(R.id.btn_top_toolswitch);
			mButtonTopColor = (ImageButton) getActivity().findViewById(R.id.btn_top_color);
			mMenuBottomTool = getActivity().findViewById(R.id.btn_bottom_tools);
			mMenuBottomParameter1 = getActivity().findViewById(R.id.btn_bottom_attribute1);
			mMenuBottomParameter2 = getActivity().findViewById(R.id.btn_bottom_attribute2);
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

		ToolsDialog.getInstance().dismiss();
		IndeterminateProgressDialog.getInstance().dismiss();
		ColorPickerDialog.getInstance().dismiss();
		// BrushPickerDialog.getInstance().dismiss();

		mSolo.sleep(SHORT_SLEEP);

		mButtonTopUndo = null;
		mButtonTopRedo = null;
		mButtonTopTool = null;
		mButtonTopColor = null;
		mMenuBottomTool = null;
		mMenuBottomParameter1 = null;
		mMenuBottomParameter2 = null;

		resetBrush();// why does this work when mSolo and all open activities are already finished?
		if (mCurrentDrawingSurfaceBitmap != null && !mCurrentDrawingSurfaceBitmap.isRecycled())
			mCurrentDrawingSurfaceBitmap.recycle();
		mCurrentDrawingSurfaceBitmap = null;

		Log.i(PaintroidApplication.TAG, "td " + step++);
		mSolo.finishOpenedActivities();
		Log.i(PaintroidApplication.TAG, "td finish " + step++);
		super.tearDown();
		Log.i(PaintroidApplication.TAG, "td finish " + step++);
		mSolo = null;
		System.gc();

	}

	protected void selectTool(ToolType toolType) {
		int nameRessourceID = toolType.getNameResource();
		if (nameRessourceID == 0)
			return;
		String nameRessourceAsText = mSolo.getString(nameRessourceID);
		assertNotNull("Name Ressource is null", nameRessourceAsText);

		mSolo.clickOnView(mMenuBottomTool);
		Log.i(PaintroidApplication.TAG, "clicked on bottom button tool");
		assertTrue("Tools dialog not visible",
				mSolo.waitForText(mSolo.getString(R.string.dialog_tools_title), 1, TIMEOUT, true));
		mSolo.clickOnText(nameRessourceAsText);
		Log.i(PaintroidApplication.TAG, "clicked on text for tool " + nameRessourceAsText);
		waitForToolToSwitch(toolType);

		// this is the version if there are only image buttons and no text

		// int[] toolButtonInfoArray = getToolButtonIDForType(toolType);
		// if (toolButtonInfoArray[0] >= 0) {
		// Log.i(PaintroidApplication.TAG, "selectTool:" + toolType.toString() + " with ID: " + toolButtonInfoArray[0]
		// + " / " + toolButtonInfoArray[1]);
		// mSolo.clickOnView(mMenuBottomTool);
		// Log.i(PaintroidApplication.TAG, "clicked on bottom button tool");
		// assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		// if (toolButtonInfoArray[1] != mSolo.getCurrentImageViews().size()) {
		// mSolo.sleep(2000);
		// assertEquals("Wrong number of images possible fail click on image", toolButtonInfoArray[1], mSolo
		// .getCurrentImageViews().size());
		// }
		// Log.i(PaintroidApplication.TAG, "click on tool image");
		// mSolo.clickOnImage(toolButtonInfoArray[0]);
		//
		// Log.i(PaintroidApplication.TAG, "clicked on image button for tool");
		// assertTrue("Waiting for tool to change -> MainActivity",
		// mSolo.waitForActivity(MainActivity.class.getSimpleName(), TIMEOUT));
		// mSolo.sleep(500);
		// assertEquals("Check switch to correct type", toolType, PaintroidApplication.currentTool.getToolType());
		// } else {
		// Log.i(PaintroidApplication.TAG, "No tool button id found for " + toolType.toString());
		// }
	}

	private void waitForToolToSwitch(ToolType toolTypeToWaitFor) {

		if (!mSolo.waitForActivity(MainActivity.class.getSimpleName())) {
			mSolo.sleep(2000);
			assertTrue("Waiting for tool to change -> MainActivity",
					mSolo.waitForActivity(MainActivity.class.getSimpleName(), TIMEOUT));
		}

		for (int waitingCounter = 0; waitingCounter < 50; waitingCounter++) {
			if (toolTypeToWaitFor.compareTo(PaintroidApplication.currentTool.getToolType()) != 0)
				mSolo.sleep(150);
			else
				break;
		}
		assertEquals("Check switch to correct type", toolTypeToWaitFor.name(), PaintroidApplication.currentTool
				.getToolType().name());
		mSolo.sleep(1500); // wait for toast to disappear
	}

	protected void clickLongOnTool(ToolType toolType) {
		mSolo.clickOnView(mMenuBottomTool);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		ArrayList<GridView> gridViews = mSolo.getCurrentViews(GridView.class);
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
	}

	// protected boolean hasProgressDialogFinished(int numberOfTries) throws SecurityException,
	// IllegalArgumentException,
	// NoSuchFieldException, IllegalAccessException {
	// mSolo.sleep(500);
	//
	// int waitForDialogSteps = 0;
	// for (; waitForDialogSteps < numberOfTries; waitForDialogSteps++) {
	// if (ProgressIntermediateDialog.getInstance().isShowing())
	// mSolo.sleep(100);
	// else
	// break;
	// }
	// return waitForDialogSteps < numberOfTries ? true : false;
	// }

	@Deprecated
	protected void assertProgressDialogShowing() {
		mSolo.waitForDialogToOpen();
		assertTrue("Progress Dialog is not showing", IndeterminateProgressDialog.getInstance().isShowing());
		mSolo.waitForDialogToClose();
		assertFalse("Progress Dialog is still showing", IndeterminateProgressDialog.getInstance().isShowing());
	}

	protected void clickOnMenuItem(String menuItem) {
		if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
			mSolo.sendKey(Solo.MENU);
			if (mSolo.waitForText(menuItem)) {
				mSolo.clickOnText(menuItem);
			} else {
				String more = getActivity().getString(R.string.more);
				mSolo.clickOnText(more);
				mSolo.waitForText(menuItem);
				mSolo.clickOnText(menuItem);
			}
		} else {
			mSolo.clickOnMenuItem(menuItem);
		}
	}

	protected void switchToFullscreen() {
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu));
        mSolo.sleep(TIMEOUT);
		PaintroidApplication.perspective.resetScaleAndTranslation();
		assertFalse("SupportActionBarStillVisible", getActivity().getSupportActionBar().isShowing());
	}

	protected int getStatusbarHeight() {
		Rect rectangle = new Rect();
		Window window = mSolo.getCurrentActivity().getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
		return (rectangle.top);
	}

	protected int getActionbarHeight() {
		Float screenDensity = 0.0f;
		try {
			screenDensity = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
					"mScreenDensity");
		} catch (Exception e) {
			fail("Getting member mScreenDensity on Perspective failed");
		}
		float actionbarHeight = OptionsMenuActivity.ACTION_BAR_HEIGHT * screenDensity;
		return ((int) actionbarHeight);
	}

	protected PointF getScreenPointFromSurfaceCoordinates(float pointX, float pointY) {
		return new PointF(pointX, pointY + getStatusbarHeight() + getActionbarHeight());
	}

	protected float getSurfaceCenterX() {
		float surfaceCenterX = 0.0f;
		try {
			surfaceCenterX = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
					"mSurfaceCenterX");
		} catch (Exception e) {
			fail("Getting member mSurfaceCenterX failed");
		}
		return (surfaceCenterX);
	}

	protected float getSurfaceCenterY() {
		float surfaceCenterY = 0.0f;
		try {
			surfaceCenterY = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
					"mSurfaceCenterY");
		} catch (Exception e) {
			fail("Getting member mSurfaceCenterY failed");
		}
		return (surfaceCenterY);
	}
}
