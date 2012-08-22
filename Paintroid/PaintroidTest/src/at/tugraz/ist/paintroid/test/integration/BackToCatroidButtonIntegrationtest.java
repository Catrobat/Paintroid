package at.tugraz.ist.paintroid.test.integration;

public class BackToCatroidButtonIntegrationtest extends BaseIntegrationTestClass {

	public BackToCatroidButtonIntegrationtest() throws Exception {
		super();
	}

	// /////////////////////////////////////////////////////////
	// FIXME find out how to get start the catroid mode.
	// button is also activated and clickable in normal mode, but not
	// visible
	//
	// public void testBackToCatroidButtonNotVisibleWhenNotStartedFromCatroid() throws SecurityException,
	// IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	// PrivateAccess.setMemberValue(MainActivity.class, mMainActivity, "mOpenedWithCatroid", false);
	// View homeButton = mSolo.getView(android.R.id.home);
	// assertFalse("Back to Catroid button should not be visible", homeButton.isActivated());
	// }
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
