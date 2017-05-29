/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.paintroid.test.espresso;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.espresso.util.UiInteractions;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.BottomBar;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getStatusbarHeight;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ToolSelectionIntegrationTest {
	private static final String PRIVATE_ACCESS_BOTTOM_BAR_NAME      = "mBottomBar";
	private static final String PRIVATE_ACCESS_TOOL_NAME_TOAST_NAME = "mToolNameToast";
	private static final String PRIVATE_ACCESS_WORKING_BITMAP_NAME  = "mWorkingBitmap";

	protected LinearLayout mToolsLayout;
	protected HorizontalScrollView mScrollView;

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private ActivityHelper activityHelper;

	@Before
	public void setUp() {
		activityHelper = new ActivityHelper(launchActivityRule.getActivity());

		PaintroidApplication.drawingSurface.destroyDrawingCache();

		mToolsLayout = (LinearLayout) launchActivityRule.getActivity().findViewById(R.id.tools_layout);
		mScrollView  = (HorizontalScrollView) launchActivityRule.getActivity().findViewById(R.id.bottom_bar_scroll_view);

		selectTool(ToolType.BRUSH);
	}

	@After
	public void tearDown() {
		IndeterminateProgressDialog.getInstance().dismiss();
		ColorPickerDialog.getInstance().dismiss();

		activityHelper = null;
	}

	protected Tool getCurrentTool() {
		return PaintroidApplication.currentTool;
	}

	protected boolean toolOptionsAreShown() {
		return getCurrentTool().getToolOptionsAreShown();
	}

	protected int getNumberOfNotVisibleTools() {
		LinearLayout toolsLayout = (LinearLayout) launchActivityRule.getActivity().findViewById(R.id.tools_layout);
		int toolCount = toolsLayout.getChildCount();
		int numberOfNotVisibleTools = 0;
		for(int i = 0; i < toolCount; i++) {
			View toolButton = toolsLayout.getChildAt(i);
			if(!toolButton.isShown()) {
				numberOfNotVisibleTools++;
			}
		}
		return numberOfNotVisibleTools;
	}

	public ToolType getToolTypeByButtonId(int id) {
		ToolType retToolType = null;

		for (ToolType toolType : ToolType.values()) {
			if (toolType.getToolButtonID() == id) {
				retToolType =  toolType;
				break;
			}
		}

		assertNotNull(retToolType);

		return retToolType;
	}

	@Test
	public void drawingSurface_deactivatedWhenToolOptionsAreShown() throws NoSuchFieldException, IllegalAccessException {
		Bitmap currentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class, PaintroidApplication.drawingSurface, PRIVATE_ACCESS_WORKING_BITMAP_NAME);

		int pixelBefore = currentDrawingSurfaceBitmap.getPixel(
			currentDrawingSurfaceBitmap.getWidth() / 2,
			currentDrawingSurfaceBitmap.getHeight() / 2
		);

		float posX = activityHelper.getDisplayWidth() / 2.0f;
		float posY = launchActivityRule.getActivity().findViewById(R.id.main_tool_options).getY() + getStatusbarHeight() - 10;

		UiInteractions.touchAt(posX, posY);

		int pixelAfter = currentDrawingSurfaceBitmap.getPixel(
			currentDrawingSurfaceBitmap.getWidth() / 2,
			currentDrawingSurfaceBitmap.getHeight() / 2
		);

		assertEquals("Drawing Surface should have been deactivated", pixelBefore, pixelAfter);
		assertFalse("Tool options should not be displayed", toolOptionsAreShown());
	}

	@Test
	public void toolOptions_disappearWhenClickedOutside() {
		// FAILS with BRUSH
		selectTool(ToolType.TEXT);

		float posX = activityHelper.getDisplayWidth() / 2.0f;
		float mainToolVisualYPosition = launchActivityRule.getActivity().findViewById(R.id.main_tool_options).getY();

		float posYInside  = mainToolVisualYPosition + getStatusbarHeight();
		float posYOutside = mainToolVisualYPosition - 16;

		onView(withId(R.id.drawer_layout)).perform(UiInteractions.touchAt(posX, posYInside));
		assertTrue("Tool options should be displayed", toolOptionsAreShown());

		onView(withId(R.id.drawer_layout)).perform(UiInteractions.touchAt(posX, posYOutside));
		assertFalse("Tool options should not be displayed", toolOptionsAreShown());
	}

	// TODO: Fails now an then, tool view not visible
	@Test
	public void toolButton_checkPosition() {
		int toolCount   = mToolsLayout.getChildCount() - getNumberOfNotVisibleTools();
		View toolButton = mToolsLayout.getChildAt(toolCount / 2);

		ToolType toolInMiddle = getToolTypeByButtonId(toolButton.getId());

		if (mToolsLayout.getWidth() > mScrollView.getWidth() + mToolsLayout.getChildAt(0).getWidth()) {
			selectTool(toolInMiddle);

			int screenLocation[] = new int[2];
			toolButton.getLocationOnScreen(screenLocation);

			assertEquals("Tool button should be centered", activityHelper.getDisplayWidth() / 2, screenLocation[0] + toolButton.getWidth() / 2);
		}

		int scrollRight = 1;
		int scrollLeft = -1;

		View leftMostButton = mToolsLayout.getChildAt(0);
		ToolType leftMostTool = getToolTypeByButtonId(leftMostButton.getId());

		selectTool(leftMostTool);

		assertFalse("Tool button should be most left", mScrollView.canScrollHorizontally(scrollLeft));

		View rightMostButton = mToolsLayout.getChildAt(toolCount - 1);
		ToolType rightMostTool = getToolTypeByButtonId(rightMostButton.getId());

		selectTool(rightMostTool);

		assertFalse("Tool button should be most right", mScrollView.canScrollHorizontally(scrollRight));
	}

	// TODO: how to implement?
	@Test
	@Ignore
	public void testToolSelectionStartAnimation() {
		int scrollX = mScrollView.getScrollX();
		assertTrue("Scroll position should be > 0 at start", scrollX > 0);

		for (int i = 0; i < 5; i++) {
			assertTrue(mScrollView.getScrollX() <= scrollX);
			scrollX = mScrollView.getScrollX();
		}

		assertEquals("Animation should be finished after a second", 0, scrollX);
	}

	@Test
	public void toast_showsCorrectToolName() throws NoSuchFieldException, IllegalAccessException {
		BottomBar bottomBar = (BottomBar) PrivateAccess.getMemberValue(MainActivity.class, launchActivityRule.getActivity(), PRIVATE_ACCESS_BOTTOM_BAR_NAME);
		selectTool(ToolType.CURSOR);
		Toast toolNameToast = (Toast) PrivateAccess.getMemberValue(BottomBar.class, bottomBar, PRIVATE_ACCESS_TOOL_NAME_TOAST_NAME);

		String toolNameToastString = ((TextView) ((LinearLayout) toolNameToast.getView()).getChildAt(0)).getText().toString();

		assertEquals("Toast should display name of cursor tool", launchActivityRule.getActivity().getString(ToolType.CURSOR.getNameResource()), toolNameToastString);
	}
}
