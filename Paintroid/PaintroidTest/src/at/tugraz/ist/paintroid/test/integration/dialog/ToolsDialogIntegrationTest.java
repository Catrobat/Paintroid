package at.tugraz.ist.paintroid.test.integration.dialog;

import android.util.Log;
import android.widget.GridView;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.test.integration.BaseIntegrationTestClass;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class ToolsDialogIntegrationTest extends BaseIntegrationTestClass {

	public ToolsDialogIntegrationTest() throws Exception {
		super();
	}

	public void testToolsDialog() {
		int logState = 0;
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		mSolo.clickOnView(mMenuBottomTool);
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		selectTool(ToolType.BRUSH);
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);

		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		mSolo.clickOnView(mMenuBottomTool);
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight - 1);
		Log.i(PaintroidApplication.TAG, "testToolsDialog " + logState++);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
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
