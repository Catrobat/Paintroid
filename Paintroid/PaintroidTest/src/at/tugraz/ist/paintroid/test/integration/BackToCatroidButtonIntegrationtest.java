package at.tugraz.ist.paintroid.test.integration;

import android.view.View;
import at.tugraz.ist.paintroid.MainActivity;
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
		View homeButton = mSolo.getView(android.R.id.home);
		assertFalse("Back to Catroid button should not be visible", homeButton.isActivated());
	}

	// /////////////////////////////////////////////////////////
	// FIXME find out how to get start the catroid mode.
	//
	// public void testSecurityQuestionShowsOnBackToCatroid() throws SecurityException, IllegalArgumentException,
	// NoSuchFieldException, IllegalAccessException {
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	// PrivateAccess.setMemberValue(MainActivity.class, mMainActivity, "mOpenedWithCatroid", true);
	// View homeButton = mSolo.getView(android.R.id.home);
	// // assertTrue("Back to Catroid button should be visible", homeButton.isActivated());
	// mSolo.clickOnView(homeButton);
	// mSolo.sleep(1000);
	// // assertTrue(mSolo.searchText(mSolo.getString(R.string.closing_catroid_security_question)));
	// }

}
