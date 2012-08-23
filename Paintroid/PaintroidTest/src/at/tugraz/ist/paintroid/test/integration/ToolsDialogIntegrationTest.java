package at.tugraz.ist.paintroid.test.integration;

import android.widget.GridView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class ToolsDialogIntegrationTest extends BaseIntegrationTestClass {

	public ToolsDialogIntegrationTest() throws Exception {
		super();
	}

	public void testToolsDialog() {
		// test select tool
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		selectTool(ToolType.CURSOR);

		// test click outside
		mSolo.clickOnView(mButtonTopTool);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight - 1);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		// test click back
		mSolo.clickOnView(mButtonTopTool);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.goBack();
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	}

	public void testToolsDialogFileManagerButtonNotAvailableWithCatrobat() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		try {
			PrivateAccess.setMemberValue(MainActivity.class, getActivity(), "mOpenedWithCatroid", true);
		} catch (Exception e) {
			fail("Could not set member variable: " + e.toString());
		}
		mSolo.clickOnView(mButtonTopTool);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		assertFalse("Should not find FileManagerButton",
				mSolo.searchText(getActivity().getString(R.string.button_filemanager)));
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
