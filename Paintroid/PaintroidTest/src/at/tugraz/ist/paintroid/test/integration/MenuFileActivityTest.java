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

	Solo solo;

	public MenuFileActivityTest() throws Exception {
		super("at.tugraz.ist.paintroid", MenuFileActivity.class);
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void testAboutDialogInFileActivity() {
		String tabFilemanagerCaption = getActivity().getString(R.string.menu_tab_file);
		solo.clickOnText(tabFilemanagerCaption);
		solo.sendKey(Solo.MENU);
		solo.sleep(100);
		String buttonAbout;
		buttonAbout = getActivity().getString(R.string.about);
		solo.clickOnText(buttonAbout);
		solo.sleep(500);

		ArrayList<TextView> textViewList = solo.getCurrentTextViews(null);

		String aboutTextExpected = getActivity().getString(R.string.about_content);
		String licenseText = getActivity().getString(R.string.licence_type_paintroid);
		String aboutTextReal = textViewList.get(2).getText().toString();

		aboutTextExpected = String.format(aboutTextExpected, licenseText);

		assertEquals("About text not correct, maybe Dialog not started as expected", aboutTextExpected, aboutTextReal);
		solo.goBack();
	}

	public void testClosePaintroidInFileActivity() {
		String tabFilemanagerCaption = getActivity().getString(R.string.menu_tab_file);
		solo.clickOnText(tabFilemanagerCaption);
		solo.sendKey(Solo.MENU);
		solo.sleep(100);
		String buttonAbout;
		buttonAbout = getActivity().getString(R.string.quit);
		solo.clickOnText(buttonAbout);
		solo.sleep(500);

		ArrayList<TextView> textViewList = solo.getCurrentTextViews(null);

		assertEquals(
				"After closing Filemanageractivity, the actual activity should be none, so no textviews should be present.",
				0, textViewList.size());
	}
}
