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
package org.catrobat.paintroid.test.integration.dialog;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageButton;

public class ToolsDialogIntegrationTest extends BaseIntegrationTestClass {

	public ToolsDialogIntegrationTest() throws Exception {
		super();
	}

	public void testToolsDialog() {
		int logState = 0;
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
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
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
	}

	public void testToolChangeChangesStatusbarIcon() {
		ImageButton toolButton1 = (ImageButton) mSolo.getView(R.id.btn_status_tool);
		Bitmap bitmap1 = ((BitmapDrawable) toolButton1.getDrawable()).getBitmap();

		selectTool(ToolType.CURSOR);
		ImageButton toolButton2 = (ImageButton) mSolo.getView(R.id.btn_status_tool);
		Bitmap bitmap2 = ((BitmapDrawable) toolButton2.getDrawable()).getBitmap();
		assertNotSame(bitmap1, bitmap2);

		selectTool(ToolType.BRUSH);
		ImageButton toolButton3 = (ImageButton) mSolo.getView(R.id.btn_status_tool);
		Bitmap bitmap3 = ((BitmapDrawable) toolButton3.getDrawable()).getBitmap();
		assertEquals(bitmap1, bitmap3);

		selectTool(ToolType.CURSOR);
		ImageButton toolButton4 = (ImageButton) mSolo.getView(R.id.btn_status_tool);
		Bitmap bitmap4 = ((BitmapDrawable) toolButton4.getDrawable()).getBitmap();
		assertEquals(bitmap2, bitmap4);

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
