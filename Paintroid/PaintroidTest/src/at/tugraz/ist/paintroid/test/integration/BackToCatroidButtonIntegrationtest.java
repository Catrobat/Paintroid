package at.tugraz.ist.paintroid.test.integration;

import android.widget.GridView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class BackToCatroidButtonIntegrationtest extends BaseIntegrationTestClass {

	public BackToCatroidButtonIntegrationtest() throws Exception {
		super();
	}

	public void testBackToCatroidButtonNotVisibleWhenNotStartedFromCatroid() throws SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		PrivateAccess.setMemberValue(MainActivity.class, mMainActivity, "mOpenedWithCatroid", false);
		mSolo.clickOnView(mButtonTopTool);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		assertFalse("Back to Catroid button should not be visible",
				mSolo.searchText(mSolo.getString(R.string.button_back_to_catroid)));
	}

	public void testSecurityQuestionShowsOnBackToCatroid() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		PrivateAccess.setMemberValue(MainActivity.class, mMainActivity, "mOpenedWithCatroid", true);
		mSolo.clickOnView(mButtonTopTool);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		assertTrue("Back to Catroid button should be visible",
				mSolo.searchText(mSolo.getString(R.string.button_back_to_catroid)));
		mSolo.clickOnText(mSolo.getString(R.string.button_back_to_catroid));
		assertTrue(mSolo.searchText(mSolo.getString(R.string.closing_catroid_security_question)));
	}

}
