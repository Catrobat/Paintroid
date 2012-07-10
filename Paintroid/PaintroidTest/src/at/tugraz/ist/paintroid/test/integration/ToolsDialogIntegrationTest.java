package at.tugraz.ist.paintroid.test.integration;

import android.widget.GridView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class ToolsDialogIntegrationTest extends BaseIntegrationTestClass {

	public ToolsDialogIntegrationTest() throws Exception {
		super();
	}

	public void testToolsDialog() {
		// test select tool
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnText(getActivity().getString(R.string.button_brush));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		// test click outside
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnScreen(50, 50);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		// test click back
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.goBack();
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

	}

	public void testToolsDialogWithCatrobat() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		try {
			PrivateAccess.setMemberValue(MainActivity.class, mMainActivity, "mOpenedWithCatroid", true);
		} catch (Exception e) {
			fail("Could not set member variable: " + e.toString());
		}
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		assertFalse("Should not find FileManagerButton", mSolo.searchText(mMainActivity.getString(R.string.file_title)));
	}

}
