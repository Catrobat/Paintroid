package at.tugraz.ist.paintroid.test.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.widget.GridView;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class NewDrawingWithoutSDCardTest extends BaseIntegrationTestClass {

	public NewDrawingWithoutSDCardTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testNewDrawingWithSDCard() {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));

		mSolo.clickOnText(mMainActivity.getString(R.string.menu_tab_file));
		mSolo.sleep(2000);

		mSolo.clickOnText(mMainActivity.getString(R.string.file_new));
		mSolo.sleep(2000);

		mSolo.clickOnText(mMainActivity.getString(R.string.dialog_newdrawing_btn_fromcam));

		mSolo.sleep(5000);
		mSolo.goBack();

	}

	@Test
	public void testNewDrawingWithoutSDCard() {
		fail("failed");

	}

}
