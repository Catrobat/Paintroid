package at.tugraz.ist.paintroid.test.integration;

import java.util.ArrayList;

import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import at.tugraz.ist.paintroid.R;

import com.jayway.android.robotium.solo.Solo;

public class MainActivityTest extends BaseIntegrationTestClass {

	public MainActivityTest() throws Exception {
		super();
	}

	public void testMenuAbout() {
		String buttonAbout;
		buttonAbout = mMainActivity.getString(R.string.about);
		mSolo.clickOnMenuItem(buttonAbout);
		mSolo.sleep(500);

		ArrayList<TextView> textViewList = mSolo.getCurrentTextViews(null);

		String aboutTextExpected = getActivity().getString(R.string.about_content);
		String licenseText = getActivity().getString(R.string.licence_type_paintroid);
		String aboutTextReal = textViewList.get(2).getText().toString();

		aboutTextExpected = String.format(aboutTextExpected, licenseText);

		assertEquals("About text not correct, maybe Dialog not started as expected", aboutTextExpected, aboutTextReal);
		mSolo.goBack();
	}

	public void testQuitProgramButtonInMenuWithNo() {
		String captionQuit;
		captionQuit = mMainActivity.getString(R.string.quit);
		mSolo.clickOnMenuItem(captionQuit);
		mSolo.sleep(500);
		String dialogTextExpected = getActivity().getString(R.string.closing_security_question);

		TextView dialogTextView = mSolo.getText(dialogTextExpected);

		assertNotNull("Quit dialog text not correct, maybe Quit Dialog not started as expected", dialogTextView);

		String buttonNoCaption = getActivity().getString(R.string.closing_security_question_not);
		mSolo.clickOnText(buttonNoCaption);
		mSolo.sleep(500);

		ArrayList<TextView> textViewList = mSolo.getCurrentTextViews(null);
		for (TextView textView : textViewList) {
			String dialogTextReal = textView.getText().toString();
			assertNotSame("About should be closed by now", dialogTextExpected, dialogTextReal);
		}
	}

	public void testQuitProgramButtonInMenuWithYes() {
		String captionQuit;
		captionQuit = mMainActivity.getString(R.string.quit);
		mSolo.clickOnMenuItem(captionQuit);
		mSolo.sleep(500);
		String dialogTextExpected = getActivity().getString(R.string.closing_security_question);

		TextView dialogTextView = mSolo.getText(dialogTextExpected);

		assertNotNull("Quit dialog text not correct, maybe Quit Dialog not started as expected", dialogTextView);

		ArrayList<TextView> textViewList = mSolo.getCurrentTextViews(null);
		assertNotSame("Main Activity should still be here and have textviews", 0, textViewList.size());

		String buttonYesCaption = getActivity().getString(R.string.closing_security_question_yes);
		mSolo.clickOnText(buttonYesCaption);
		mSolo.sleep(500);

		textViewList = mSolo.getCurrentTextViews(null);
		assertEquals("Main Activity should be gone by now", 0, textViewList.size());
	}

	public void testHideMenuAndShowAgain() {
		RelativeLayout toolbarLayout = (RelativeLayout) getActivity().findViewById(R.id.BottomRelativeLayout);
		int visibilityToolbar = toolbarLayout.getVisibility();
		assertEquals("Toolbarmenu should be visible after starting paintroid", View.VISIBLE, visibilityToolbar);

		String buttonHideMenu;
		buttonHideMenu = mMainActivity.getString(R.string.hide_menu);
		mSolo.clickOnMenuItem(buttonHideMenu);
		mSolo.sleep(400);

		visibilityToolbar = toolbarLayout.getVisibility();
		assertEquals("Toolbarmenu should be invisible after hiding it", View.INVISIBLE, visibilityToolbar);

		mSolo.sendKey(Solo.MENU);
		mSolo.sleep(400);

		visibilityToolbar = toolbarLayout.getVisibility();
		assertEquals("Toolbarmenu should be visible again after pressing Menu Key", View.VISIBLE, visibilityToolbar);
	}

	public void testHelpDialogForBrush() {
		toolHelpTest(R.string.button_brush, R.string.help_content_brush);
	}

	public void testHelpDialogForCursor() {
		toolHelpTest(R.string.button_cursor, R.string.help_content_cursor);
	}

	public void testHelpDialogForPipette() {
		toolHelpTest(R.string.button_pipette, R.string.help_content_eyedropper);
	}

	public void testHelpDialogForWand() {
		toolHelpTest(R.string.button_magic, R.string.help_content_wand);
	}

	public void testHelpDialogForUndo() {
		toolHelpTest(R.string.button_undo, R.string.help_content_undo);
	}

	public void testHelpDialogForRedo() {
		toolHelpTest(R.string.button_redo, R.string.help_content_redo);
	}

	public void testHelpDialogForStamp() {
		toolHelpTest(R.string.button_stamp, R.string.help_content_stamp);
	}

	public void testHelpDialogForImportPng() {
		toolHelpTest(R.string.button_import_image, R.string.help_content_import_png);
	}

	private void toolHelpTest(int idStringOfTool, int idExpectedHelptext) {
		int indexHelpText = 1;
		int indexDoneButton = 2;

		mSolo.clickOnView(mButtonTopTool);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));

		String buttonBrush = mSolo.getString(idStringOfTool);
		mSolo.clickLongOnText(buttonBrush);
		mSolo.sleep(100);

		ArrayList<TextView> viewList = mSolo.getCurrentTextViews(null);

		assertEquals("There should be exactly 3 views in the Help dialog", 3, viewList.size());

		String helpText = mSolo.getCurrentTextViews(null).get(indexHelpText).getText().toString();
		String buttonDoneText = viewList.get(indexDoneButton).getText().toString();

		String helpTextExpected = mSolo.getString(idExpectedHelptext);
		String buttonDoneTextExpected = mSolo.getString(R.string.done);

		assertEquals("Text of help dialog not ok, maybe dialog not opened correctly", helpTextExpected, helpText);
		assertEquals("Button for closing help not present", buttonDoneTextExpected, buttonDoneText);

		mSolo.clickOnButton(buttonDoneText);

		viewList = mSolo.getCurrentTextViews(null);

		assertNotSame("Helpdialog should not be open any more after clicking done.", 3, viewList.size());
	}

}
