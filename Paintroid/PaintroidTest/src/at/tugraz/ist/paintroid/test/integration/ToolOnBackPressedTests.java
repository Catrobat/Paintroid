/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.paintroid.test.integration;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.app.Activity;
import android.os.Environment;
import android.widget.GridView;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class ToolOnBackPressedTests extends BaseIntegrationTestClass {

	public ToolOnBackPressedTests() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testBrushToolBackPressed() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		int numberButtonsAtBeginning = mSolo.getCurrentButtons().size();

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Two more buttons for the exit screen not found", mSolo.getCurrentButtons().size(),
				numberButtonsAtBeginning + 2);
		TextView exitTextView = mSolo.getText(mSolo.getString(R.string.closing_security_question));
		assertNotNull("No exit Text found", exitTextView);

		mSolo.clickOnButton(mSolo.getString(R.string.closing_security_question_not));
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
		mSolo.clickOnButton(mSolo.getString(R.string.closing_security_question_yes));
		assertTrue("Waiting for the exit dialog to finish", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Application finished no buttons left", mSolo.getCurrentButtons().size(), 0);
	}

	@Test
	public void testNotBrushToolBackPressed() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));

		mSolo.clickOnText(mMainActivity.getString(R.string.button_cursor));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		// assertTrue("Waiting for Tool to Change -> MainActivity", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.CURSOR);

		mSolo.goBack();
		// assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.BRUSH);
	}

	@Test
	public void testBrushToolBackPressedFromCatroidAndUsePicture() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		String pathToFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
				+ PaintroidApplication.APPLICATION_CONTEXT.getString(R.string.app_name) + "/"
				+ mSolo.getString(R.string.temp_picture_name) + ".png";

		File fileToReturnToCatroid = new File(pathToFile);
		if (fileToReturnToCatroid.exists())
			fileToReturnToCatroid.delete();

		try {
			PrivateAccess.setMemberValue(MainActivity.class, mMainActivity, "mOpenedWithCatroid", true);
		} catch (Exception e) {
			fail("Could not set member variable: " + e.toString());
		}
		int numberButtonsAtBeginning = mSolo.getCurrentButtons().size();

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Two more buttons for the exit screen not found", mSolo.getCurrentButtons().size(),
				numberButtonsAtBeginning + 2);
		TextView exitTextView = mSolo.getText(mSolo.getString(R.string.closing_catroid_security_question));
		assertNotNull("No exit Text found", exitTextView);

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to close", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Two buttons exit screen should be away", mSolo.getCurrentButtons().size(),
				numberButtonsAtBeginning);

		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));
		mSolo.clickOnButton(mSolo.getString(R.string.closing_catroid_security_question_use_picture));
		assertTrue("Waiting for the exit dialog to finish", mSolo.waitForActivity("MainActivity", TIMEOUT));
		mSolo.sleep(2000);
		boolean hasStopped = PrivateAccess.getMemberValueBoolean(Activity.class, mMainActivity, "mStopped");
		assertTrue("MainActivity should be finished.", hasStopped);
		fileToReturnToCatroid = new File(pathToFile);
		assertTrue("No file was created", fileToReturnToCatroid.exists());
		assertTrue("The created file is empty", (fileToReturnToCatroid.length() > 0));
		fileToReturnToCatroid.delete();
	}

	@Test
	public void testBrushToolBackPressedFromCatroidAndDiscardPicture() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		String pathToFile = mMainActivity.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
				+ "/" + mSolo.getString(R.string.temp_picture_name) + ".png";

		File fileToReturnToCatroid = new File(pathToFile);
		if (fileToReturnToCatroid.exists())
			fileToReturnToCatroid.delete();

		try {
			PrivateAccess.setMemberValue(MainActivity.class, mMainActivity, "mOpenedWithCatroid", true);
		} catch (Exception e) {
			fail("Could not set member variable: " + e.toString());
		}
		mSolo.goBack();
		assertTrue("Waiting for the exit dialog to appear", mSolo.waitForActivity("MainActivity", TIMEOUT));

		mSolo.clickOnButton(mSolo.getString(R.string.closing_catroid_security_question_discard_picture));
		assertTrue("Waiting for the exit dialog to finish", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Application finished no buttons left", mSolo.getCurrentButtons().size(), 0);
		mSolo.sleep(500);
		fileToReturnToCatroid = new File(pathToFile);
		assertFalse("File was created", fileToReturnToCatroid.exists());
		if (fileToReturnToCatroid.exists())
			fileToReturnToCatroid.delete();
	}

}
