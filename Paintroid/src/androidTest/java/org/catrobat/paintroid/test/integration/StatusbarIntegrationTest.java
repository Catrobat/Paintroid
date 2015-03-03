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

import java.util.ArrayList;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.TopBar;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StatusbarIntegrationTest extends BaseIntegrationTestClass {

	private static final String PRIVATE_ACCESS_STATUSBAR_NAME = "mTopBar";
	private static final String PRIVATE_ACCESS_TOOL_NAME_TOAST_NAME = "mToolNameToast";

	public StatusbarIntegrationTest() throws Exception {
		super();
	}

	public void testAllButtonsAreVisible() {
		ArrayList<Integer> expectedButtons = new ArrayList<Integer>();
		expectedButtons.add(R.id.btn_top_undo);
		expectedButtons.add(R.id.btn_top_redo);
		expectedButtons.add(R.id.btn_top_color);
		expectedButtons.add(R.id.btn_top_toolswitch);

		ArrayList<ImageButton> imageButtons = mSolo.getCurrentViews(ImageButton.class);
		for (ImageButton button : imageButtons) {
			expectedButtons.remove((Object) button.getId());
		}

		assertEquals("all buttons should be found", 0, expectedButtons.size());
	}

	public void testSwitchToMoveOnSwitchToolIconClicked() {
		mSolo.clickOnView(mButtonTopTool);
		assertEquals("tool should be move tool now", ToolType.MOVE, PaintroidApplication.currentTool.getToolType());
	}

	public void testSwitchBackToPreviousToolOnSwitchToolIconClickedTwice() {
		selectTool(ToolType.CURSOR);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals("tool should be move tool now", ToolType.MOVE, PaintroidApplication.currentTool.getToolType());
		mSolo.clickOnView(mButtonTopTool);
		assertEquals("tool should be cursor tool now", ToolType.CURSOR, PaintroidApplication.currentTool.getToolType());

	}

	public void testPreviousToolIsNotMoveTool() {
		selectTool(ToolType.MOVE);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals("tool should be brush tool now", ToolType.BRUSH, PaintroidApplication.currentTool.getToolType());
	}

	public void testPreviousToolIsNotZoomTool() {
		selectTool(ToolType.ZOOM);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals("tool should be brush tool now", ToolType.BRUSH, PaintroidApplication.currentTool.getToolType());
	}

	public void testToastShowsRightToolName() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		TopBar topBar = (TopBar) PrivateAccess.getMemberValue(MainActivity.class, getActivity(),
				PRIVATE_ACCESS_STATUSBAR_NAME);
		mSolo.clickOnView(mButtonTopTool);
		Toast toolNameToast = (Toast) PrivateAccess.getMemberValue(TopBar.class, topBar,
				PRIVATE_ACCESS_TOOL_NAME_TOAST_NAME);
		String toolNameToastString = ((TextView) ((LinearLayout) toolNameToast.getView()).getChildAt(0)).getText()
				.toString();
		assertEquals("toast should display name of moveTool", mSolo.getString(ToolType.MOVE.getNameResource()),
				toolNameToastString);
	}
}
