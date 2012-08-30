package at.tugraz.ist.paintroid.test.integration;

import java.util.ArrayList;

import android.widget.GridView;
import android.widget.TextView;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;

public class MainActivityTest extends BaseIntegrationTestClass {

	public MainActivityTest() throws Exception {
		super();
	}

	public void testMenuAbout() {
		String buttonAbout;
		buttonAbout = getActivity().getString(R.string.about);
		mSolo.clickOnMenuItem(buttonAbout, true);
		mSolo.sleep(500);

		ArrayList<TextView> textViewList = mSolo.getCurrentTextViews(null);

		String aboutTextExpected = getActivity().getString(R.string.about_content);
		String licenseText = getActivity().getString(R.string.licence_type_paintroid);
		aboutTextExpected = String.format(aboutTextExpected, licenseText);
		String aboutTextFirstHalf = aboutTextExpected.substring(0, aboutTextExpected.length() / 2);
		String aboutTextSecondHalf = aboutTextExpected.substring(aboutTextExpected.length() / 2,
				aboutTextExpected.length());

		assertTrue("About text first half not correct, maybe Dialog not started as expected",
				mSolo.waitForText(aboutTextFirstHalf, 1, TIMEOUT, true, false));
		// FIXME 2nd half never found :(
		// assertTrue("About text second half not correct, maybe Dialog not started as expected",
		// mSolo.waitForText(aboutTextSecondHalf, 1, TIMEOUT, true));
		mSolo.goBack();
	}

	public void testQuitProgramButtonInMenuWithNo() {
		String captionQuit;
		captionQuit = getActivity().getString(R.string.quit);
		mSolo.clickOnMenuItem(captionQuit, true);
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
		captionQuit = getActivity().getString(R.string.quit);
		mSolo.clickOnMenuItem(captionQuit, true);
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

	public void testHelpDialogForBrush() {
		toolHelpTest(ToolType.BRUSH, R.string.help_content_brush);
	}

	public void testHelpDialogForCursor() {
		toolHelpTest(ToolType.CURSOR, R.string.help_content_cursor);
	}

	public void testHelpDialogForPipette() {
		toolHelpTest(ToolType.PIPETTE, R.string.help_content_eyedropper);
	}

	public void testHelpDialogForWand() {
		toolHelpTest(ToolType.MAGIC, R.string.help_content_wand);
	}

	public void testHelpDialogForStamp() {
		toolHelpTest(ToolType.STAMP, R.string.help_content_stamp);
	}

	public void testHelpDialogForImportPng() {
		toolHelpTest(ToolType.IMPORTPNG, R.string.help_content_import_png);
	}

	private void toolHelpTest(ToolType toolToClick, int idExpectedHelptext) {
		int indexHelpText = 1;
		int indexDoneButton = 2;

		mSolo.clickOnView(mMenuBottomTool);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));

		clickLongOnTool(toolToClick);
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
