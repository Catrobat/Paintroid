/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test;

import java.io.File;
import java.util.Locale;

import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.widget.TextView;
import at.tugraz.ist.paintroid.FileIO;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;

import com.jayway.android.robotium.solo.Solo;

public class FileTests extends ActivityInstrumentationTestCase2<MainActivity> {
	private Solo solo;
	private MainActivity mainActivity;

	private TextView toolbarMainButton;

	private static final String TESTNAME1 = "TESTNAME1";

	private String saveText;
	private String doneText;
	private String cancelText;
	private String yesText;

	public FileTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();
		Utils.setLocale(solo, Locale.ENGLISH);

		toolbarMainButton = (TextView) mainActivity.findViewById(R.id.btn_Tool);

		final Resources res = mainActivity.getResources();
		saveText = res.getText(R.string.save).toString();
		doneText = res.getText(R.string.done).toString();
		cancelText = res.getText(R.string.cancel).toString();
		yesText = res.getText(R.string.yes).toString();
	}

	private void openFileManager() {
		solo.clickOnView(toolbarMainButton);
		solo.waitForActivity("MenuTabActivity", 1000);
		solo.clickOnText("File"); // TODO: should be in resources
		solo.waitForActivity("FileActivity", 1000);
	}

	@Smoke
	public void testSaveEmptyPicture() throws Exception {

		File file1 = FileIO.saveBitmap(mainActivity, null, "test_empty");
		assertFalse(file1.exists());

		Utils.saveCurrentPicture(solo, "test_empty");

		assertTrue(file1.exists());
	}

	@Smoke
	public void testSavePicturePath() throws Exception {
		File file1 = FileIO.saveBitmap(mainActivity, null, "TESTNAME1");
		assertFalse(file1.exists());

		Utils.saveCurrentPicture(solo, TESTNAME1);

		assertTrue(solo.waitForActivity("MainActivity", 1000));
		assertTrue(file1.exists());
	}

	@Smoke
	public void testFileOverwriteYes() throws Exception {
		File file1 = FileIO.saveBitmap(mainActivity, null, "overwrite_test");
		if (!file1.exists()) {
			Utils.saveCurrentPicture(solo, "overwrite_test");
		}

		solo.sleep(1000);

		File file2 = FileIO.saveBitmap(mainActivity, null, "overwrite_test");
		assertTrue(file2.exists());

		Utils.saveCurrentPicture(solo, "overwrite_test");

		//		File file3 = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");

		solo.clickOnButton(yesText);
		Log.d("PaintroidTest", "File has been overwriten");

		mainActivity = (MainActivity) solo.getCurrentActivity();
	}

	@Smoke
	public void testFileOverwriteCancel() throws Exception {

		File file1 = FileIO.saveBitmap(mainActivity, null, "overwrite_test");
		if (!file1.exists()) {
			Utils.saveCurrentPicture(solo, "overwrite_test");
		}

		solo.sleep(1000);

		File file2 = FileIO.saveBitmap(mainActivity, null, "overwrite_test");
		assertTrue(file2.exists());

		Utils.saveCurrentPicture(solo, "overwrite_test");

		solo.clickOnButton(cancelText);
		Log.d("PaintroidTest", "File has been overwriten");

		solo.clickOnButton(saveText);
		solo.enterText(0, "overwrite_test_afterCancel");
		solo.clickOnButton(doneText);

		File file3 = FileIO.saveBitmap(mainActivity, null, "overwrite_test_afterCancel");

		if (file3.exists()) {
			solo.clickOnButton(yesText);
		}
	}
}
