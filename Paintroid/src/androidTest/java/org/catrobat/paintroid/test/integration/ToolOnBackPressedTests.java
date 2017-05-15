/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.integration;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ToolOnBackPressedTests extends BaseIntegrationTestClass {

	public ToolOnBackPressedTests() throws Exception {
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
	public void testBrushToolBackPressed() {
		mTestCaseWithActivityFinished = true;

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		int numberButtonsAtBeginning = mSolo.getCurrentViews(Button.class).size();

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertTrue("Yes Option should be available", mSolo.searchText(mSolo.getString(R.string.save_button_text)));
		assertTrue("Yes Option should be available", mSolo.searchText(mSolo.getString(R.string.discard_button_text)));
		TextView exitTextView = mSolo.getText(mSolo.getString(R.string.closing_security_question));
		assertNotNull("No exit Text found", exitTextView);

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to close", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Two buttons exit screen should be away", mSolo.getCurrentViews(Button.class).size(),
				numberButtonsAtBeginning);

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		mSolo.clickOnButton(mSolo.getString(R.string.discard_button_text));
		mSolo.sleep(1000);
		assertTrue("Waiting for the exit dialog to finish", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Application finished no buttons left", mSolo.getCurrentViews(Button.class).size(), 0);
	}

	@Test
	public void testBrushToolBackPressedWithSaveAndOverride() throws IOException {
		mTestCaseWithActivityFinished = true;

		String pathToFile = getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
				+ "/" + mSolo.getString(R.string.temp_picture_name) + ".png";

		File tempFile = new File(pathToFile);
		if (tempFile.exists())
			tempFile.delete();

		tempFile.createNewFile();
		PaintroidApplication.savedPictureUri = Uri.fromFile(tempFile);
		long oldSize = tempFile.length();

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.sleep(SHORT_SLEEP);

		mSolo.goBack();
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);

		// assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		mSolo.clickOnButton(mSolo.getString(R.string.save_button_text));
		mSolo.waitForDialogToClose();
		// mSolo.sleep(1000);
		tempFile = new File(pathToFile);
		long newSize = tempFile.length();
		// assertTrue("Waiting for the exit dialog to finish", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertNotSame("Application finished, files not different.", oldSize, newSize);
	}

	@Test
	public void testNotBrushToolBackPressed() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		selectTool(ToolType.CURSOR);

		mSolo.goBack();

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.currentTool.getToolType(), ToolType.BRUSH);
	}

	@Test
	public void testToolOptionsDisappearWhenBackPressed() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		selectTool(ToolType.CURSOR);
		openToolOptionsForCurrentTool(ToolType.CURSOR);
		String toolName = mSolo.getString(getCurrentTool().getToolType().getNameResource());
		assertTrue("Tool name should be found", mSolo.searchText(toolName));

		mSolo.goBack();
		assertEquals("Tool should not have changed", getCurrentTool().getToolType(), ToolType.CURSOR);
		assertFalse("Tool options should not be shown", toolOptionsAreShown());
		assertFalse("Tool name should not be found", mSolo.searchText(toolName));

		mSolo.goBack();
		assertEquals("Tool should have changed", getCurrentTool().getToolType(), ToolType.BRUSH);
	}

	@Test
	public void testBrushToolBackPressedFromCatroidAndUsePicture() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		mTestCaseWithActivityFinished = true;

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		String pathToFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
				+ PaintroidApplication.applicationContext.getString(R.string.ext_storage_directory_name) + "/"
				+ mSolo.getString(R.string.temp_picture_name) + ".png";

		File fileToReturnToCatroid = new File(pathToFile);
		if (fileToReturnToCatroid.exists())
			fileToReturnToCatroid.delete();

		PaintroidApplication.openedFromCatroid = true;
		int numberButtonsAtBeginning = mSolo.getCurrentViews(Button.class).size();

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertTrue("Yes Option should be available", mSolo.searchText(mSolo.getString(R.string.save_button_text)));
		assertTrue("No Option should be available", mSolo.searchText(mSolo.getString(R.string.discard_button_text)));
		TextView exitTextView = mSolo.getText(mSolo.getString(R.string.closing_security_question));
		assertNotNull("No exit Text found", exitTextView);

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to close", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Two buttons exit screen should be away", mSolo.getCurrentViews(Button.class).size(),
				numberButtonsAtBeginning);

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		mSolo.clickOnButton(mSolo.getString(R.string.save_button_text));
		assertTrue("Waiting for the exit dialog to finish", mSolo.waitForActivity("MainActivity", TIMEOUT));
		mSolo.sleep(8000);
		boolean hasStopped = PrivateAccess.getMemberValueBoolean(Activity.class, getActivity(), "mStopped");
		assertTrue("MainActivity should be finished.", hasStopped);
		fileToReturnToCatroid = new File(pathToFile);
		assertTrue("No file was created", fileToReturnToCatroid.exists());
		assertTrue("The created file is empty", (fileToReturnToCatroid.length() > 0));
		fileToReturnToCatroid.delete();
	}

	@Test
	public void testBrushToolBackPressedFromCatroidAndDiscardPicture() {
		mTestCaseWithActivityFinished = true;

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		String pathToFile = getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
				+ "/" + mSolo.getString(R.string.temp_picture_name) + ".png";

		File fileToReturnToCatroid = new File(pathToFile);
		if (fileToReturnToCatroid.exists())
			fileToReturnToCatroid.delete();

		PaintroidApplication.openedFromCatroid = true;
		mSolo.goBack();

		mSolo.waitForText(mSolo.getString(R.string.discard_button_text));
		mSolo.clickOnButton(mSolo.getString(R.string.discard_button_text));
		assertTrue("Exit dialog not closing", mSolo.waitForDialogToClose());
		assertEquals("Application finished, buttons left", mSolo.getCurrentViews(Button.class).size(), 0);

		mSolo.sleep(500);
		fileToReturnToCatroid = new File(pathToFile);
		assertFalse("File was created", fileToReturnToCatroid.exists());
		if (fileToReturnToCatroid.exists())
			fileToReturnToCatroid.delete();
	}

}
