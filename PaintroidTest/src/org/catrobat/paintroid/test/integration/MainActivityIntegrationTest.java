/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
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

import java.util.ArrayList;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;

import android.widget.TextView;

public class MainActivityIntegrationTest extends BaseIntegrationTestClass {

	private static final String MENU_MORE_TEXT = "More";

	public MainActivityIntegrationTest() throws Exception {
		super();
	}

	public void testMenuAbout() {

		String buttonAbout = getActivity().getString(R.string.menu_about);
		mSolo.clickOnMenuItem(buttonAbout);
		mSolo.sleep(500);

		String aboutTextExpected = getActivity().getString(R.string.about_content);
		String licenseText = getActivity().getString(R.string.licence_type_paintroid);
		aboutTextExpected = String.format(aboutTextExpected, licenseText);
		String aboutTextFirstHalf = aboutTextExpected.substring(0, aboutTextExpected.length() / 2);

		assertTrue("About text first half not correct, maybe Dialog not started as expected",
				mSolo.waitForText(aboutTextFirstHalf, 1, TIMEOUT, true, false));
		// FIXME 2nd half never found :(
		// assertTrue("About text second half not correct, maybe Dialog not started as expected",
		// mSolo.waitForText(aboutTextSecondHalf, 1, TIMEOUT, true));
		mSolo.goBack();
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

	public void testHelpDialogForBucket() {
		toolHelpTest(ToolType.FILL, R.string.help_content_fill);
	}

	public void testHelpDialogForRectangle() {
		toolHelpTest(ToolType.RECT, R.string.help_content_rectangle);
	}

	public void testHelpDialogForEllipse() {
		toolHelpTest(ToolType.ELLIPSE, R.string.help_content_ellipse);
	}

	public void testHelpDialogForCrop() {
		toolHelpTest(ToolType.CROP, R.string.help_content_crop);
	}

	public void testHelpDialogForEraser() {
		toolHelpTest(ToolType.ERASER, R.string.help_content_eraser);
	}

	public void testHelpDialogForFlip() {
		toolHelpTest(ToolType.FLIP, R.string.help_content_flip);
	}

	public void testHelpDialogForMove() {
		toolHelpTest(ToolType.MOVE, R.string.help_content_move);
	}

	public void testHelpDialogForZoom() {
		toolHelpTest(ToolType.ZOOM, R.string.help_content_zoom);
	}

	public void testHelpDialogForImportImage() {
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
