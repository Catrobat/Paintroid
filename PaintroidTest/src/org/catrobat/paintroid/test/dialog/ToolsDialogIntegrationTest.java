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
package org.catrobat.paintroid.test.dialog;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.BaseIntegrationTestClass;
import org.catrobat.paintroid.tools.Tool.ToolType;
import org.catrobat.paintroid.ui.implementation.DrawingSurfaceImplementation;

import android.util.Log;
import android.widget.GridView;

public class ToolsDialogIntegrationTest extends BaseIntegrationTestClass {

	public ToolsDialogIntegrationTest() throws Exception {
		super();
	}

	public void testToolsDialog() {
		int logState = 0;
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		selectTool(ToolType.BRUSH);
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);

		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		mSolo.clickOnView(mMenuBottomTool);
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		mSolo.clickOnScreen(50, mScreenHeight / 2);
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		mSolo.sleep(3000);
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);

		mSolo.clickOnView(mMenuBottomTool);
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		mSolo.goBack();
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
	}

	// ////////////////////////////////////////////////////////
	// The following testcases provoke problems on Jenkins
	// -------------------------------------------------------
	//
	// public void testToolsDialogToolSelection() {
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	// // test cursor
	// mSolo.clickOnView(mToolBarButtonMain);
	// assertTrue("Waiting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
	// mSolo.clickOnText(mSolo.getString(R.string.button_cursor));
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	// ToolType toolType = PaintroidApplication.CURRENT_TOOL.getToolType();
	// assertTrue("Tool Type should be Cursor " + toolType, toolType == ToolType.CURSOR);
	// // test magic
	// mSolo.clickOnView(mToolBarButtonMain);
	// assertTrue("Waiting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
	// mSolo.clickOnText(mSolo.getString(R.string.button_magic));
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	// toolType = PaintroidApplication.CURRENT_TOOL.getToolType();
	// assertTrue("Tool Type should be Cursor " + toolType, toolType == ToolType.MAGIC);
	// // test pipette
	// mSolo.clickOnView(mToolBarButtonMain);
	// assertTrue("Waiting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
	// mSolo.clickOnText(mSolo.getString(R.string.button_pipette));
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	// toolType = PaintroidApplication.CURRENT_TOOL.getToolType();
	// assertTrue("Tool Type should be Cursor " + toolType, toolType == ToolType.PIPETTE);
	// // test brush
	// mSolo.clickOnView(mToolBarButtonMain);
	// assertTrue("Waiting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
	// mSolo.clickOnText(mSolo.getString(R.string.button_brush));
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	// toolType = PaintroidApplication.CURRENT_TOOL.getToolType();
	// assertTrue("Tool Type should be Cursor " + toolType, toolType == ToolType.BRUSH);
	// // test stamp
	// mSolo.clickOnView(mToolBarButtonMain);
	// assertTrue("Waiting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
	// mSolo.clickOnText(mSolo.getString(R.string.button_floating_box));
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	// toolType = PaintroidApplication.CURRENT_TOOL.getToolType();
	// assertTrue("Tool Type should be Cursor " + toolType, toolType == ToolType.STAMP);
	//
	// }

}
