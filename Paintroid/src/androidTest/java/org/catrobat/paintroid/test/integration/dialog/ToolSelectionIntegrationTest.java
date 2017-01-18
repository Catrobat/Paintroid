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

package org.catrobat.paintroid.test.integration.dialog;

import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;

public class ToolSelectionIntegrationTest extends BaseIntegrationTestClass {
	private static final String PRIVATE_ACCESS_TOOL_NAME_TOAST_NAME = "mToolNameToast";

	public ToolSelectionIntegrationTest() throws Exception {
		super();
	}

	public void testToolSwitch() {
		// TODO for redesign: write new tests with espresso
		// - switch between tools, current tool should change
		// - at least after second click on same item, the tool options should be shown, check also for showing correct tool name
		// - when back button is pressed:
		//     tool options should disappear
		//     otherwise: switch to draw tool should be performed
		// - toast with current tool name should be displayed after tool switch
		// - tool option should disappear when clicked outside tool options
		// - drawing surface should be deactivated, when tool options are shown


		/* before redesign
		selectTool(ToolType.BRUSH);

		mSolo.clickOnView(mMenuBottomTool);
		assertTrue("Tools dialog not visible",
				mSolo.waitForText(mSolo.getString(R.string.dialog_tools_title), 1, TIMEOUT, true));

		mSolo.goBack();
		assertTrue("Tools dialog should have closed", mSolo.waitForDialogToClose(TIMEOUT));

		mSolo.clickOnView(mMenuBottomTool);
		assertTrue("Tools dialog not visible",
				mSolo.waitForText(mSolo.getString(R.string.dialog_tools_title), 1, TIMEOUT, true));
		mSolo.goBack();
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		*/
	}

	/* before redesign
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
	*/

}
