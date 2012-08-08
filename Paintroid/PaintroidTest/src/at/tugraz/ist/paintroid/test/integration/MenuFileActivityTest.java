package at.tugraz.ist.paintroid.test.integration;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MenuFileActivity;
import at.tugraz.ist.paintroid.R;

import com.jayway.android.robotium.solo.Solo;

public class MenuFileActivityTest extends ActivityInstrumentationTestCase2<MenuFileActivity> {

	Solo mSolo;
	protected final int TIMEOUT = 20000;

	public MenuFileActivityTest() throws Exception {
		super("at.tugraz.ist.paintroid", MenuFileActivity.class);
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mSolo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		mSolo.finishOpenedActivities();
		super.tearDown();
	}

	public void testAboutDialogInFileActivity() {
		int indexAboutText = 2;

		assertTrue("Waiting for MenuFileActivity",
				mSolo.waitForActivity(MenuFileActivity.class.getSimpleName(), TIMEOUT));
		String buttonAbout;
		buttonAbout = getActivity().getString(R.string.about);
		mSolo.clickOnMenuItem(buttonAbout);
		mSolo.sleep(500);

		ArrayList<TextView> textViewList = mSolo.getCurrentTextViews(null);

		String aboutTextExpected = getActivity().getString(R.string.about_content);
		String licenseText = getActivity().getString(R.string.licence_type_paintroid);
		String aboutTextReal = textViewList.get(indexAboutText).getText().toString();

		aboutTextExpected = String.format(aboutTextExpected, licenseText);

		assertEquals("About text not correct, maybe Dialog not started as expected", aboutTextExpected, aboutTextReal);
		mSolo.goBack();
	}

	public void testQuitPaintroidInFileActivity() {

		String buttonQuit;
		buttonQuit = getActivity().getString(R.string.quit);
		mSolo.clickOnMenuItem(buttonQuit);
		mSolo.sleep(500);

		ArrayList<TextView> textViewList = mSolo.getCurrentTextViews(null);

		assertEquals(
				"After closing Filemanageractivity, the actual activity should be none, so no textviews should be present.",
				0, textViewList.size());
	}
}
