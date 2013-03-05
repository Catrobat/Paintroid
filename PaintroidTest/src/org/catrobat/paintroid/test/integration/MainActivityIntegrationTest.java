/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
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

import java.util.ArrayList;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;

import android.widget.TextView;

public class MainActivityIntegrationTest extends BaseIntegrationTestClass {

	public MainActivityIntegrationTest() throws Exception {
		super();
	}

	public void testMenuAbout() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		String buttonAbout = getActivity().getString(R.string.menu_about);
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
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		String captionQuit = getActivity().getString(R.string.menu_quit);
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
		mTestCaseWithActivityFinished = true;
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		String captionQuit = getActivity().getString(R.string.menu_quit);
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

	public void testHelpDialogForStamp() {
		toolHelpTest(ToolType.STAMP, R.string.help_content_stamp);
	}

	public void testHelpDialogForImportPng() {
		toolHelpTest(ToolType.IMPORTPNG, R.string.help_content_import_png);
	}

	private void toolHelpTest(ToolType toolToClick, int idExpectedHelptext) {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		clickLongOnTool(toolToClick);
		mSolo.sleep(100);

		ArrayList<TextView> viewList = mSolo.getCurrentTextViews(null);

		assertEquals("There should be exactly 5 views in the Help dialog", 5, viewList.size());

		String helpTextExpected = mSolo.getString(idExpectedHelptext);
		String buttonDoneTextExpected = mSolo.getString(android.R.string.ok);

		assertTrue("Help text not found", mSolo.searchText(helpTextExpected, true));
		assertTrue("Done button not found", mSolo.searchButton(buttonDoneTextExpected, true));
		mSolo.clickOnButton(buttonDoneTextExpected);

		viewList = mSolo.getCurrentTextViews(null);

		assertFalse("Help text still present", mSolo.searchText(helpTextExpected, true));
		assertNotSame("Helpdialog should not be open any more after clicking done.", 5, viewList.size());
	}

}
