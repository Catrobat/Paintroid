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

import java.util.ArrayList;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;

import android.widget.TextView;

public class MainActivityIntegrationTest extends BaseIntegrationTestClass {

	public MainActivityIntegrationTest() throws Exception {
		super();
	}

	public void testMenuTermsOfUseAndService() {

		String buttonTermsOfUseAndService = getActivity().getString(R.string.menu_terms_of_use_and_service);
		clickOnMenuItem(buttonTermsOfUseAndService);
		mSolo.sleep(500);

		String termsOfUseAndServiceTextExpected = getActivity().getString(R.string.terms_of_use_and_service_content);

		assertTrue("Terms of Use and Service dialog text not correct, maybe Dialog not started as expected",
				mSolo.waitForText(termsOfUseAndServiceTextExpected, 1, TIMEOUT, true, false));

		mSolo.goBack();
	}

	public void testMenuAbout() {

		String buttonAbout = getActivity().getString(R.string.menu_about);
		clickOnMenuItem(buttonAbout);
		mSolo.sleep(500);

		String aboutTextExpected = getActivity().getString(R.string.about_content);
		String licenseText = getActivity().getString(R.string.license_type_paintroid);
		aboutTextExpected = String.format(aboutTextExpected, licenseText);

		assertTrue("About dialog text not correct, maybe Dialog not started as expected",
				mSolo.waitForText(aboutTextExpected, 1, TIMEOUT, true, false));
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

	public void testHelpDialogForRotate() {
		toolHelpTest(ToolType.ROTATE, R.string.help_content_rotate);
	}

	private void toolHelpTest(ToolType toolToClick, int idExpectedHelptext) {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		clickLongOnTool(toolToClick);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);

		ArrayList<TextView> viewList = mSolo.getCurrentViews(TextView.class);

		assertEquals("There should be exactly 5 text views in the Help dialog", 5, viewList.size());

		String helpTextExpected = mSolo.getString(idExpectedHelptext);
		String buttonDoneTextExpected = mSolo.getString(android.R.string.ok);

		assertTrue("Help text not found", mSolo.searchText(helpTextExpected, true));
		assertTrue("Done button not found", mSolo.searchButton(buttonDoneTextExpected, true));
		mSolo.clickOnButton(buttonDoneTextExpected);

		mSolo.waitForDialogToClose(SHORT_TIMEOUT);
		viewList = mSolo.getCurrentViews(TextView.class);

		assertFalse("Help text still present", mSolo.searchText(helpTextExpected, true));
		assertNotSame("Helpdialog should not be open any more after clicking done", 5, viewList.size());
	}
}
