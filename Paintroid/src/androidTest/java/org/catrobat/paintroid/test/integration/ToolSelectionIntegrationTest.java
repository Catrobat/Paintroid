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

package org.catrobat.paintroid.test.integration;

import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.BottomBar;
import org.junit.Before;

public class ToolSelectionIntegrationTest extends BaseIntegrationTestClass {
	private static final String PRIVATE_ACCESS_BOTTOM_BAR_NAME = "mBottomBar";
	private static final String PRIVATE_ACCESS_TOOL_NAME_TOAST_NAME = "mToolNameToast";

	protected LinearLayout mToolsLayout;
	protected HorizontalScrollView mScrollView;

	public ToolSelectionIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
		mToolsLayout = (LinearLayout) getActivity().findViewById(R.id.tools_layout);
		mScrollView = (HorizontalScrollView) getActivity().findViewById(R.id.bottom_bar_scroll_view);
	}

	public void testDrawingSurfaceDeactivatedWhenToolOptionsAreShown() {
		selectTool(ToolType.FILL);
		openToolOptionsForCurrentTool(ToolType.FILL);

		int pixelBefore = mCurrentDrawingSurfaceBitmap.getPixel(
				mCurrentDrawingSurfaceBitmap.getWidth() / 2, mCurrentDrawingSurfaceBitmap.getHeight() / 2);

		float posX = mScreenWidth / 2.0f;
		float posY = mSolo.getView(R.id.main_tool_options).getY() + getStatusbarHeight() - 10;
		mSolo.clickOnScreen(posX, posY);

		int pixelAfter = mCurrentDrawingSurfaceBitmap.getPixel(
				mCurrentDrawingSurfaceBitmap.getWidth() / 2, mCurrentDrawingSurfaceBitmap.getHeight() / 2);

		assertEquals("Drawing Surface should have been deactivated", pixelBefore, pixelAfter);
		assertFalse("Tool options should not be displayed", toolOptionsAreShown());
	}

	public void testToolOptionsDisappearWhenClickedOutside() {
		openToolOptionsForCurrentTool(ToolType.BRUSH);

		float posX = mScreenWidth / 2.0f;
		float posYInside = mSolo.getView(R.id.main_tool_options).getY() + getStatusbarHeight();
		float posYOutside = mSolo.getView(R.id.main_tool_options).getY() + getStatusbarHeight() - 1;

		mSolo.clickOnScreen(posX, posYInside);
		assertTrue("Tool options should be displayed", toolOptionsAreShown());

		mSolo.clickOnScreen(posX, posYOutside);
		assertFalse("Tool options should not be displayed", toolOptionsAreShown());
	}

	public void testCenterSelectedToolButton() {
		int toolCount = mToolsLayout.getChildCount();
		View toolButton = mToolsLayout.getChildAt(toolCount / 2);
		ToolType toolInMiddle = getToolTypeByButtonId(toolButton.getId());

		if (mToolsLayout.getWidth() > mScrollView.getWidth() + mToolsLayout.getChildAt(0).getWidth()) {
			selectTool(toolInMiddle);
			int screenLocation[] = {0, 0};
			toolButton.getLocationOnScreen(screenLocation);
			assertEquals("Tool button should be centered", mScreenWidth / 2, screenLocation[0] + toolButton.getWidth() / 2);
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

	public void testToolSelectionStartAnimation() {
		int scrollX = mScrollView.getScrollX();
		assertTrue("Scroll position should be > 0 at start", scrollX > 0);

		for (int i = 0; i < 5; i++) {
			assertTrue(mScrollView.getScrollX() <= scrollX);
			scrollX = mScrollView.getScrollX();
			mSolo.sleep(200);
		}
		assertEquals("Animation should be finished after a second", 0, scrollX);
	}

	public void testToastShowsRightToolName() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		BottomBar bottomBar = (BottomBar) PrivateAccess.getMemberValue(MainActivity.class, getActivity(),
				PRIVATE_ACCESS_BOTTOM_BAR_NAME);
		selectTool(ToolType.CURSOR);
		Toast toolNameToast = (Toast) PrivateAccess.getMemberValue(BottomBar.class, bottomBar,
				PRIVATE_ACCESS_TOOL_NAME_TOAST_NAME);
		String toolNameToastString = ((TextView) ((LinearLayout) toolNameToast.getView()).getChildAt(0)).getText().toString();
		assertEquals("Toast should display name of cursor tool", mSolo.getString(ToolType.CURSOR.getNameResource()),
				toolNameToastString);
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

}
