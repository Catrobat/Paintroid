/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.integration;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;

import com.robotium.solo.Condition;
import com.robotium.solo.Solo;
import com.robotium.solo.Timeout;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.NavigationDrawerMenuActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.SystemAnimations;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import java.util.concurrent.atomic.AtomicBoolean;

@Ignore
public class BaseIntegrationTestClass extends ActivityInstrumentationTestCase2<MainActivity> {

	private static final int DEFAULT_BRUSH_WIDTH = 25;
	private static final Cap DEFAULT_BRUSH_CAP = Cap.ROUND;
	private static final int DEFAULT_COLOR = Color.BLACK;

	protected Solo mSolo;
	private SystemAnimations systemAnimations;
	protected ImageButton mButtonTopUndo;
	protected ImageButton mButtonTopRedo;
	protected ImageButton mButtonTopColor;
	protected View mButtonTopLayer;
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected static final int SHORT_SLEEP = 50;
	protected static final int SHORT_TIMEOUT = 250;
	protected static final int MEDIUM_TIMEOUT = 1000;
	protected static final int TIMEOUT = 10000;
	protected boolean mTestCaseWithActivityFinished = false;
	protected Bitmap mCurrentDrawingSurfaceBitmap;
	protected View mButtonAddLayer;
	protected ImageButton mButtonDeleteLayer;
	protected ImageButton mButtonMergeLayer;
	protected ImageButton mButtonRenameLayer;
	protected ImageButton mButtonLockLayer;
	protected ImageButton mButtonInvisibleLayer;
	protected DrawerLayout mDrawerLayout;

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

			systemAnimations = new SystemAnimations(getInstrumentation().getContext());
			systemAnimations.disableAll();

			/*
			 * if (Utils.isScreenLocked(mSolo.getCurrentActivity())) { mScreenLocked = true; tearDown();
			 * assertFalse("Screen is locked!", mScreenLocked); return; }
			 */
			Log.d("Paintroid test", "setup" + setup++);

			PaintroidApplication.drawingSurface.destroyDrawingCache();
			Log.d("Paintroid test", "setup" + setup++);
			mButtonTopUndo = (ImageButton) getActivity().findViewById(R.id.btn_top_undo);
			mButtonTopRedo = (ImageButton) getActivity().findViewById(R.id.btn_top_redo);
			mButtonTopColor = (ImageButton) getActivity().findViewById(R.id.btn_top_color);
			mButtonTopLayer = getActivity().findViewById(R.id.btn_top_layers);
			mButtonAddLayer = getActivity().findViewById(R.id.mButtonLayerNew);
			mButtonDeleteLayer = (ImageButton) getActivity().findViewById(R.id.mButtonLayerDelete);
			mButtonMergeLayer = (ImageButton) getActivity().findViewById(R.id.mButtonLayerMerge);
			mButtonRenameLayer = (ImageButton) getActivity().findViewById(R.id.mButtonLayerRename);
			mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);

			mButtonLockLayer = (ImageButton) getActivity().findViewById(R.id.mButtonLayerLock);
			mButtonInvisibleLayer = (ImageButton) getActivity().findViewById(R.id.mButtonLayerVisible);
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

		final AtomicBoolean colorResetted = new AtomicBoolean(false);

		try {
			runTestOnUiThread(new Runnable() {
				@Override
				public void run() {
					ColorPickerDialog.getInstance().updateColorChange(Color.BLACK);
					colorResetted.set(true);
					IndeterminateProgressDialog.getInstance().dismiss();
					ColorPickerDialog.getInstance().dismiss();
				}
			});
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}

		mSolo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return colorResetted.get() &&
						!IndeterminateProgressDialog.getInstance().isShowing() &&
						!ColorPickerDialog.getInstance().isShowing();
			}
		}, TIMEOUT);


		mButtonTopUndo = null;
		mButtonTopRedo = null;
		mButtonTopColor = null;
		mButtonTopLayer = null;

		resetBrush();
		if (mCurrentDrawingSurfaceBitmap != null && !mCurrentDrawingSurfaceBitmap.isRecycled())
			mCurrentDrawingSurfaceBitmap.recycle();
		mCurrentDrawingSurfaceBitmap = null;

		Log.i(PaintroidApplication.TAG, "td " + step++);
		mSolo.finishOpenedActivities();
		Log.i(PaintroidApplication.TAG, "td finish " + step++);
		super.tearDown();
		systemAnimations.enableAll();
		Log.i(PaintroidApplication.TAG, "td finish " + step++);
		mSolo = null;
		System.gc();
	}

	protected void selectTool(ToolType toolType) {
		if (PaintroidApplication.currentTool.getToolType() == toolType) {
			scrollToToolButton(toolType);
			return;

		}

		int orientation = mSolo.getCurrentActivity().getResources().getConfiguration().orientation;
		View toolButtonView = null;
		if(orientation == Configuration.ORIENTATION_PORTRAIT)
			toolButtonView = scrollToToolButton(toolType);
		else if(orientation == Configuration.ORIENTATION_LANDSCAPE)
			toolButtonView = verticalScrollToToolButton(toolType);
		mSolo.clickOnView(toolButtonView);
		waitForToolToSwitch(toolType);
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
		View toolButtonView = scrollToToolButton(toolType);
		mSolo.clickLongOnView(toolButtonView);
	}

	public void openNavigationDrawer(){

		Display display = mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		float xStart = 0 ;
		float xEnd = width / 2;
		mSolo.drag(xStart, xEnd, height / 2, height / 2, 1);
	}

	public void resetCommandManager(){
		PaintroidApplication.commandManager.resetAndClear(true);
	}


	protected View scrollToToolButton(ToolType toolType) {
		HorizontalScrollView scrollView = (HorizontalScrollView) mSolo.getView(R.id.bottom_bar_scroll_view);
		int scrollRight = 1;
		int scrollLeft = -1;
		View toolButtonView = null;

		while (scrollView.canScrollHorizontally(scrollLeft)) {
			scrollToolBarToLeft();
		}

		float scrollPosRight = scrollView.getX() + scrollView.getWidth();
		int[] btnLocation = {0, 0};
		getToolButtonView(toolType).getLocationOnScreen(btnLocation);
		float btnPos = btnLocation[0] + (getToolButtonView(toolType).getWidth() / 2.0f);

		if (btnPos < scrollPosRight) {
			toolButtonView =  getToolButtonView(toolType);
		}

		while (scrollView.canScrollHorizontally(scrollRight) && toolButtonView == null) {
			mSolo.scrollViewToSide(scrollView, Solo.RIGHT);
			getToolButtonView(toolType).getLocationOnScreen(btnLocation);
			btnPos = btnLocation[0] + (getToolButtonView(toolType).getWidth() / 2.0f);
			if (btnPos < scrollPosRight) {
				toolButtonView = getToolButtonView(toolType);
				break;
			}
		}

		assertNotNull("Tool button not found", toolButtonView);
		return toolButtonView;
	}

	protected View verticalScrollToToolButton(ToolType toolType) {
		ScrollView scrollView = (ScrollView) mSolo.getView(R.id.bottom_bar_landscape_scroll_view);
		int scrollBottom = 1;
		int scrollTop = -1;
		View toolButtonView = null;

		while (scrollView.canScrollVertically(scrollTop)) {
			scrollToolBarToTop();
		}

		float scrollPosBottom = scrollView.getY() + scrollView.getHeight();
		int[] btnLocation = {0, 0};
		getToolButtonView(toolType).getLocationOnScreen(btnLocation);
		float btnPos = btnLocation[1] + (getToolButtonView(toolType).getHeight() / 2.0f);

		if (btnPos < scrollPosBottom) {
			toolButtonView =  getToolButtonView(toolType);
		}
		float fromX, toX, fromY, toY = 0;
		int stepCount = 20;
		int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
		while (scrollView.canScrollVertically(scrollBottom) && toolButtonView == null) {
			fromX = screenWidth -  scrollView.getWidth() / 2;
			toX = fromX;
			fromY = screenHeight / 2;
			toY = screenHeight / 4 - screenHeight / 8;
			mSolo.drag(fromX, toX, fromY, toY, stepCount);
			getToolButtonView(toolType).getLocationOnScreen(btnLocation);
			btnPos = btnLocation[1] + (getToolButtonView(toolType).getHeight() / 2.0f);
			if (btnPos < scrollPosBottom) {
				toolButtonView = getToolButtonView(toolType);
				break;
			}
		}

		assertNotNull("Tool button not found", toolButtonView);
		return toolButtonView;
	}

	private void scrollToolBarToTop() {
		ScrollView scrollView = (ScrollView) mSolo.getView(R.id.bottom_bar_landscape_scroll_view);
		int[] screenLocation = {0, 0};
		scrollView.getLocationOnScreen(screenLocation);
		int getAwayFromTop = 42;
		float fromY = screenLocation[1] + getAwayFromTop;
		float toY = scrollView.getHeight();
		float xPos = screenLocation[0] + (scrollView.getWidth() / 2.0f);

		mSolo.drag(xPos, xPos, fromY, toY, 1);
	}

	private void scrollToolBarToLeft() {
		HorizontalScrollView scrollView = (HorizontalScrollView) mSolo.getView(R.id.bottom_bar_scroll_view);
		int[] screenLocation = {0, 0};
		scrollView.getLocationOnScreen(screenLocation);
		int getAwayFromNavigationDrawer = 60;
		float fromX = screenLocation[0] + getAwayFromNavigationDrawer;
		float toX = screenLocation[0] + scrollView.getWidth();
		float yPos = screenLocation[1] + (scrollView.getHeight() / 2.0f);

		mSolo.drag(fromX, toX, yPos, yPos, 1);
	}

	protected View getToolButtonView(ToolType toolType) {
		View view = mSolo.getView(toolType.getToolButtonID());
		return view;
	}

	protected void openToolOptionsForCurrentTool() {
		mSolo.clickOnView(getToolButtonView(getCurrentTool().getToolType()));
		Condition toolOptionsAreShown = new Condition() {
			@Override
			public boolean isSatisfied() {
				if (toolOptionsAreShown()) {
					return true;
				}
				return false;
			}
		};
		assertTrue("opening tool options failed", mSolo.waitForCondition(toolOptionsAreShown, TIMEOUT));
	}

	protected void openToolOptionsForCurrentTool(ToolType expectedCurrentToolType) {
		assertEquals("Wrong tool selected", expectedCurrentToolType, getCurrentTool().getToolType());
		openToolOptionsForCurrentTool();
	}

	protected void closeToolOptionsForCurrentTool() {

		mSolo.clickOnView(getToolButtonView(getCurrentTool().getToolType()));
		Condition toolOptionsNotShown = new Condition() {
			@Override
			public boolean isSatisfied() {
				if (toolOptionsAreShown()) {
					return false;
				}
				return true;
			}
		};
		assertTrue("Closing tool options failed", mSolo.waitForCondition(toolOptionsNotShown, TIMEOUT));
	}

	protected boolean toolOptionsAreShown() {
		return getCurrentTool().getToolOptionsAreShown();
	}

	protected void resetBrush() {
		try {
			PrivateAccess.setMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mColorPickerDialog", null);
			PrivateAccess.setMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBrushPickerDialog", null);
		} catch (Exception exception) {
			return;
		}
	}

	@Deprecated
	protected void assertProgressDialogShowing() {
		mSolo.waitForDialogToOpen();
		assertTrue("Progress Dialog is not showing", IndeterminateProgressDialog.getInstance().isShowing());
		mSolo.waitForDialogToClose();
		assertFalse("Progress Dialog is still showing", IndeterminateProgressDialog.getInstance().isShowing());
	}

	protected void openMenu() {
		float clickCoordinateX = 5;
		float clickCoordinateY = mScreenHeight / 2;

		mSolo.drag(clickCoordinateX, clickCoordinateX + mScreenWidth / 2, clickCoordinateY, clickCoordinateY, 20);

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
		float actionbarHeight = NavigationDrawerMenuActivity.ACTION_BAR_HEIGHT * screenDensity;
		return ((int) actionbarHeight);
	}

	protected PointF getScreenPointFromSurfaceCoordinates(float pointX, float pointY) {
		return new PointF(pointX, pointY + getStatusbarHeight() + getActionbarHeight());
	}

	protected float getSurfaceCenterX() {
		float surfaceCenterX = 0.0f;
		try {
			surfaceCenterX = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective, "mSurfaceCenterX");
		} catch (Exception e) {
			fail("Getting member mSurfaceCenterX failed");
		}
		return (surfaceCenterX);
	}

	protected float getSurfaceCenterY() {
		float surfaceCenterY = 0.0f;
		try {
			surfaceCenterY = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective, "mSurfaceCenterY");
		} catch (Exception e) {
			fail("Getting member mSurfaceCenterY failed");
		}
		return (surfaceCenterY);
	}

	protected void scaleDownTestBitmap(float scaleFactor) {
		mCurrentDrawingSurfaceBitmap = Bitmap.createScaledBitmap(mCurrentDrawingSurfaceBitmap,
				(int) (mCurrentDrawingSurfaceBitmap.getWidth() * scaleFactor),
				(int) (mCurrentDrawingSurfaceBitmap.getHeight() * scaleFactor), false);
		PaintroidApplication.drawingSurface.setBitmap(mCurrentDrawingSurfaceBitmap);
		mSolo.sleep(200);
		PaintroidApplication.perspective.resetScaleAndTranslation();
	}

	protected Tool getCurrentTool() {
		return PaintroidApplication.currentTool;
	}

	protected void openColorChooserDialog() {
		mSolo.clickOnView(mButtonTopColor);
		assertTrue("Color chooser dialog was not opened", mSolo.waitForDialogToOpen());
		assertTrue("Color chooser title not found", mSolo.searchText(mSolo.getString(R.string.color_chooser_title)));
	}

	protected void closeColorChooserDialog() {
		mSolo.clickOnButton(mSolo.getString(R.string.done));
		assertTrue("Color chooser dialog should have been closed", mSolo.waitForDialogToClose());
	}

	protected int getNumberOfNotVisibleTools() {
		LinearLayout toolsLayout = (LinearLayout) mSolo.getView(R.id.tools_layout);
		int toolCount = toolsLayout.getChildCount();
		int numberOfNotVisibleTools = 0;
		for(int i = 0; i < toolCount; i++)
		{
			View toolButton = toolsLayout.getChildAt(i);
			if(!toolButton.isShown())
				numberOfNotVisibleTools++;
		}
		return numberOfNotVisibleTools;
	}

}
