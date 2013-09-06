package org.catrobat.paintroid.test.integration;

import java.io.File;
import java.util.ArrayList;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.app.Activity;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

public class ExitAppDialogIntegrationTest extends BaseIntegrationTestClass {

	private static final String MENU_MORE_TEXT = "More";

	public ExitAppDialogIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testBackPressedToCatroidAndUsePicture() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		mTestCaseWithActivityFinished = true;

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		String pathToFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
				+ PaintroidApplication.applicationContext.getString(R.string.app_name) + "/"
				+ mSolo.getString(R.string.temp_picture_name) + ".png";

		File fileToReturnToCatroid = new File(pathToFile);
		if (fileToReturnToCatroid.exists())
			fileToReturnToCatroid.delete();

		PaintroidApplication.openedFromCatroid = true;
		int numberButtonsAtBeginning = mSolo.getCurrentButtons().size();

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertTrue("Yes Option should be available", mSolo.searchText(mSolo.getString(R.string.save)));
		assertTrue("Yes Option should be available", mSolo.searchText(mSolo.getString(R.string.discard)));
		TextView exitTextView = mSolo.getText(mSolo.getString(R.string.closing_catroid_security_question));
		assertNotNull("No exit Text found", exitTextView);

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to close", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Two buttons exit screen should be away", mSolo.getCurrentButtons().size(),
				numberButtonsAtBeginning);

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		mSolo.clickOnButton(mSolo.getString(R.string.save));
		assertTrue("Waiting for the exit dialog to finish", mSolo.waitForActivity("MainActivity", TIMEOUT));
		mSolo.sleep(2000);
		boolean hasStopped = PrivateAccess.getMemberValueBoolean(Activity.class, getActivity(), "mStopped");
		assertTrue("MainActivity should be finished.", hasStopped);
		fileToReturnToCatroid = new File(pathToFile);
		assertTrue("No file was created", fileToReturnToCatroid.exists());
		assertTrue("The created file is empty", (fileToReturnToCatroid.length() > 0));
		fileToReturnToCatroid.delete();
	}

	@Test
	public void testBackPressedToCatroidAndDiscardPicture() {
		mTestCaseWithActivityFinished = true;

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		String pathToFile = getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
				+ "/" + mSolo.getString(R.string.temp_picture_name) + ".png";

		File fileToReturnToCatroid = new File(pathToFile);
		if (fileToReturnToCatroid.exists())
			fileToReturnToCatroid.delete();

		PaintroidApplication.openedFromCatroid = true;
		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertTrue("Exit Dialog is not opened",
				mSolo.searchText(mSolo.getString(R.string.closing_catroid_security_question_title)));

		mSolo.clickOnButton(mSolo.getString(R.string.discard));
		mSolo.sleep(500);
		assertTrue("Waiting for the exit dialog to finish", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Application finished no buttons left", mSolo.getCurrentButtons().size(), 0);
		mSolo.sleep(500);
		fileToReturnToCatroid = new File(pathToFile);
		assertFalse("File was created", fileToReturnToCatroid.exists());
		if (fileToReturnToCatroid.exists())
			fileToReturnToCatroid.delete();
	}

	public void testBackPressedAndCancelDialog() {
		mTestCaseWithActivityFinished = false;

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.goBack();
		mSolo.sleep(200);

		assertTrue("Exit Dialog is not opened",
				mSolo.searchText(mSolo.getString(R.string.closing_security_question_title)));
		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to disappear", mSolo.waitForActivity("MainActivity", TIMEOUT));
	}

	public void testQuitProgramButtonInMenuWithOk() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		String captionQuit = mSolo.getString(R.string.menu_quit);
		mSolo.clickOnMenuItem(captionQuit);
		mSolo.sleep(500);
		String dialogTextExpected = mSolo.getString(R.string.closing_security_question);

		String buttonSave = mSolo.getString(R.string.save);
		String buttonDiscard = mSolo.getString(R.string.discard);
		assertTrue("Save Option should be available", mSolo.searchText(buttonSave));
		assertTrue("Discard Option should be available", mSolo.searchText(buttonDiscard));

		TextView dialogTextView = mSolo.getText(dialogTextExpected);

		assertNotNull("Quit dialog text not correct, maybe Quit Dialog not started as expected", dialogTextView);

		mSolo.clickOnButton(buttonSave);
		mSolo.sleep(500);

		String dialogSaveExpected = mSolo.getString(R.string.dialog_save_title);

		View inputEditText = mSolo.getView(R.id.dialog_save_file_edit_text);
		TextView dialogSaveView = mSolo.getText(dialogSaveExpected);

		assertNotNull("EditText is not found", inputEditText);
		assertNotNull("Save dialog is not found", dialogSaveView);

		String ButtonOk = mSolo.getString(R.string.ok);
		mSolo.clickOnText(ButtonOk);
		mSolo.sleep(2000);
		boolean hasStopped = PrivateAccess.getMemberValueBoolean(Activity.class, getActivity(), "mStopped");
		assertTrue("MainActivity should be finished.", hasStopped);
	}

	public void testQuitProgramButtonInMenuWithOkAndOverride() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		String pathToFile = getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
				+ "/" + mSolo.getString(R.string.temp_picture_name) + ".png";

		File fileToReturnToCatroid = new File(pathToFile);
		if (fileToReturnToCatroid.exists())
			fileToReturnToCatroid.delete();

		// add file to PaintroidApplication and override it with save

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		String captionQuit = mSolo.getString(R.string.menu_quit);
		mSolo.clickOnMenuItem(captionQuit);
		mSolo.sleep(500);
		String dialogTextExpected = mSolo.getString(R.string.closing_security_question);

		String buttonSave = mSolo.getString(R.string.save);
		String buttonDiscard = mSolo.getString(R.string.discard);
		assertTrue("Save Option should be available", mSolo.searchText(buttonSave));
		assertTrue("Discard Option should be available", mSolo.searchText(buttonDiscard));

		TextView dialogTextView = mSolo.getText(dialogTextExpected);

		assertNotNull("Quit dialog text not correct, maybe Quit Dialog not started as expected", dialogTextView);

		mSolo.clickOnButton(buttonSave);
		mSolo.sleep(500);

		String dialogSaveExpected = mSolo.getString(R.string.dialog_save_title);

		View inputEditText = mSolo.getView(R.id.dialog_save_file_edit_text);
		TextView dialogSaveView = mSolo.getText(dialogSaveExpected);

		assertNotNull("EditText is not found", inputEditText);
		assertNotNull("Save dialog is not found", dialogSaveView);

		String ButtonOk = mSolo.getString(R.string.ok);
		mSolo.clickOnText(ButtonOk);
		mSolo.sleep(2000);
		boolean hasStopped = PrivateAccess.getMemberValueBoolean(Activity.class, getActivity(), "mStopped");
		assertTrue("MainActivity should be finished.", hasStopped);
	}

	public void testQuitProgramButtonInMenuWithCancel() {
		mTestCaseWithActivityFinished = true;

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		String captionQuit = mSolo.getString(R.string.menu_quit);
		mSolo.clickOnMenuItem(captionQuit);
		mSolo.sleep(500);
		String dialogTextExpected = mSolo.getString(R.string.closing_security_question);

		TextView dialogTextView = mSolo.getText(dialogTextExpected);

		assertNotNull("Quit dialog text not correct, maybe Quit Dialog not started as expected", dialogTextView);

		ArrayList<TextView> textViewList = mSolo.getCurrentTextViews(null);
		assertNotSame("Main Activity should still be here and have textviews", 0, textViewList.size());

		String buttonDiscardCaption = mSolo.getString(R.string.discard);
		mSolo.clickOnText(buttonDiscardCaption);
		mSolo.sleep(500);

		assertTrue("Waiting for the exit dialog to finish", mSolo.waitForActivity("MainActivity", TIMEOUT));

	}

}
