//package at.tugraz.ist.paintroid.test.integration;
//
//import java.util.ArrayList;
//
//import android.widget.TextView;
//import at.tugraz.ist.paintroid.R;
//import at.tugraz.ist.paintroid.tools.Tool.ToolType;
//
//public class MainActivityIntegrationTest extends BaseIntegrationTestClass {
//
//	public MainActivityIntegrationTest() throws Exception {
//		super();
//	}
//
//	public void testMenuAbout() {
//		String buttonAbout;
//		buttonAbout = mSolo.getString(R.string.menu_about);
//		mSolo.clickOnMenuItem(buttonAbout, true);
//		mSolo.sleep(2000);
//
//		String aboutTextExpected = getActivity().getString(R.string.about_content);
//		String licenseText = getActivity().getString(R.string.licence_type_paintroid);
//		aboutTextExpected = String.format(aboutTextExpected, licenseText);
//		// ArrayList<TextView> textViews = mSolo.getCurrentTextViews(null);
//		assertTrue("About text not found", mSolo.searchText(aboutTextExpected, 1, true, false));
//		// assertTrue("License text not found", mSolo.searchText(licenseText, 1, true, false));
//		// String aboutTextReal = null;
//		// assertTrue("textviews should be visible", textViews.size() > 0);
//		// for (TextView textView : textViews) {
//		// Log.e("paintroid", "textview " + textView.getId() + " - " + R.id.about_tview_Text);
//		// if (textView.getId() == R.id.about_tview_Text) {
//		// aboutTextReal = textView.getText().toString();
//		// }
//		// }
//
//		// assertEquals("expected text and real text should be equal", aboutTextExpected, aboutTextReal);
//
//		mSolo.goBack();
//	}
//
//	public void testQuitProgramButtonInMenuWithNo() {
//		String captionQuit;
//		captionQuit = mSolo.getString(R.string.menu_quit);
//		mSolo.clickOnMenuItem(captionQuit, true);
//		mSolo.sleep(2000);
//		String dialogTextExpected = getActivity().getString(R.string.closing_security_question);
//
//		TextView dialogTextView = mSolo.getText(dialogTextExpected);
//
//		assertNotNull("Quit dialog text not correct, maybe Quit Dialog not started as expected", dialogTextView);
//
//		String buttonNoCaption = getActivity().getString(R.string.closing_security_question_not);
//		mSolo.clickOnText(buttonNoCaption);
//		mSolo.sleep(2000);
//
//		ArrayList<TextView> textViewList = mSolo.getCurrentTextViews(null);
//		for (TextView textView : textViewList) {
//			String dialogTextReal = textView.getText().toString();
//			assertNotSame("About should be closed by now", dialogTextExpected, dialogTextReal);
//		}
//	}
//
//	public void testQuitProgramButtonInMenuWithYes() {
//		mTestCaseWithActivityFinished = true;
//		String captionQuit;
//		captionQuit = mSolo.getString(R.string.menu_quit);
//		mSolo.clickOnMenuItem(captionQuit, true);
//		mSolo.sleep(500);
//		String dialogTextExpected = getActivity().getString(R.string.closing_security_question);
//
//		TextView dialogTextView = mSolo.getText(dialogTextExpected);
//
//		assertNotNull("Quit dialog text not correct, maybe Quit Dialog not started as expected", dialogTextView);
//
//		ArrayList<TextView> textViewList = mSolo.getCurrentTextViews(null);
//		assertNotSame("Main Activity should still be here and have textviews", 0, textViewList.size());
//
//		String buttonYesCaption = getActivity().getString(R.string.closing_security_question_yes);
//		mSolo.clickOnText(buttonYesCaption);
//		mSolo.sleep(500);
//
//		textViewList = mSolo.getCurrentTextViews(null);
//		assertEquals("Main Activity should be gone by now", 0, textViewList.size());
//	}
//
//	public void testHelpDialogForBrush() {
//		toolHelpTest(ToolType.BRUSH, R.string.help_content_brush);
//	}
//
//	public void testHelpDialogForCursor() {
//		toolHelpTest(ToolType.CURSOR, R.string.help_content_cursor);
//	}
//
//	public void testHelpDialogForPipette() {
//		toolHelpTest(ToolType.PIPETTE, R.string.help_content_eyedropper);
//	}
//
//	public void testHelpDialogForWand() {
//		toolHelpTest(ToolType.MAGIC, R.string.help_content_wand);
//	}
//
//	// ////////////////////////////////////////////////////////////////////////////////////////
//	// FIXME Not available in current version
//	//
//	// public void testHelpDialogForUndo() {
//	// toolHelpTest(R.string.button_undo, R.string.help_content_undo);
//	// }
//	//
//	// public void testHelpDialogForRedo() {
//	// toolHelpTest(R.string.button_redo, R.string.help_content_redo);
//	// }
//
//	public void testHelpDialogForStamp() {
//		toolHelpTest(ToolType.STAMP, R.string.help_content_stamp);
//	}
//
//	public void testHelpDialogForImportPng() {
//		toolHelpTest(ToolType.IMPORTPNG, R.string.help_content_import_png);
//	}
//
//	private void toolHelpTest(ToolType toolType, int idExpectedHelptext) {
//		int indexHelpText = 1;
//		int indexDoneButton = 2;
//		try {
//			clickLongOnTool(toolType);
//			mSolo.sleep(1000);
//
//			ArrayList<TextView> viewList = mSolo.getCurrentTextViews(null);
//
//			assertEquals("There should be exactly 3 views in the Help dialog", 3, viewList.size());
//
//			String helpText = mSolo.getCurrentTextViews(null).get(indexHelpText).getText().toString();
//			String buttonDoneText = viewList.get(indexDoneButton).getText().toString();
//
//			String helpTextExpected = mSolo.getString(idExpectedHelptext);
//			String buttonDoneTextExpected = mSolo.getString(R.string.done);
//
//			assertEquals("Text of help dialog not ok, maybe dialog not opened correctly", helpTextExpected, helpText);
//			assertEquals("Button for closing help not present", buttonDoneTextExpected, buttonDoneText);
//
//			mSolo.clickOnButton(buttonDoneText);
//
//			viewList = mSolo.getCurrentTextViews(null);
//			mSolo.goBack();
//
//			assertNotSame("Helpdialog should not be open any more after clicking done.", 3, viewList.size());
//		} catch (Exception whatever) {
//			fail(whatever.toString());
//		}
//	}
//
// }
