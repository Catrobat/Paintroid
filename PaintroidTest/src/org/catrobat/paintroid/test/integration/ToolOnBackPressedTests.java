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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.widget.TextView;

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

		int numberButtonsAtBeginning = mSolo.getCurrentButtons().size();

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertTrue("Yes Option should be available", mSolo.searchText(mSolo.getString(R.string.save)));
		assertTrue("Yes Option should be available", mSolo.searchText(mSolo.getString(R.string.discard)));
		TextView exitTextView = mSolo.getText(mSolo.getString(R.string.closing_security_question));
		assertNotNull("No exit Text found", exitTextView);

		mSolo.clickOnButton(mSolo.getString(R.string.discard));
		assertTrue("Waiting for the exit dialog to close", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Two buttons exit screen should be away", mSolo.getCurrentButtons().size(),
				numberButtonsAtBeginning);

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to close", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Two buttons exit screen should be away", mSolo.getCurrentButtons().size(),
				numberButtonsAtBeginning);

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		mSolo.clickOnButton(mSolo.getString(R.string.discard));
		mSolo.sleep(500);
		assertTrue("Waiting for the exit dialog to finish", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Application finished no buttons left", mSolo.getCurrentButtons().size(), 0);
	}

	@Test
	public void testNotBrushToolBackPressed() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		selectTool(ToolType.CURSOR);

		mSolo.goBack();
		// assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.currentTool.getToolType(), ToolType.BRUSH);
	}

}
