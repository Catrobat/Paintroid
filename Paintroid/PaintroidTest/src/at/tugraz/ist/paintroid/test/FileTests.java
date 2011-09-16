/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test;

import java.io.File;
import java.util.Locale;

import android.content.res.Resources;
import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.TextView;
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

	@Override
	public void tearDown() throws Exception {
		deleteFiles();
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

	private static void deleteFiles() {
		File dir = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid");
		String[] children = dir.list();
		for (int i = 0; i < children.length; i++) {
			new File(dir, children[i]).delete();
		}
	}

	public void testSaveEmptyPicture() throws Exception {
		Utils.saveCurrentPicture(solo, "test_empty");

		File file1 = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/test_empty.png");

		if (file1.exists()) {
			solo.clickOnButton(yesText);
		}

		assertTrue(solo.waitForActivity("MainActivity", 500));
	}

	public void testSavePicturePath() throws Exception {
		File file1 = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/" + TESTNAME1 + ".png");
		assertFalse(file1.exists());

		Utils.saveCurrentPicture(solo, TESTNAME1);

		assertTrue(solo.waitForActivity("MainActivity", 1000));
		assertTrue(file1.exists());
	}

	public void testPictureIsSavedCorrectly() throws Exception {
		Utils.saveCurrentPicture(solo, "test_save_2");

		File file1 = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/test_save_2.png");

		if (file1.exists()) {
			solo.clickOnButton(yesText);
			Log.d("PaintroidTest", "File has been overwriten");
		}

		assertTrue(solo.waitForActivity("MainActivity", 500));

		File file2 = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/test_save_2.png");
		if (file2.exists()) {

		} else {
			assertTrue(false);
		}
	}

	public void testFileOverwriteYes() throws Exception {
		File file1 = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");
		if (!file1.exists()) {
			Utils.saveCurrentPicture(solo, "overwrite_test");
		}

		solo.sleep(1000);

		File file2 = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");
		assertTrue(file2.exists());

		Utils.saveCurrentPicture(solo, "overwrite_test");

		//		File file3 = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");

		solo.clickOnButton(yesText);
		Log.d("PaintroidTest", "File has been overwriten");

		mainActivity = (MainActivity) solo.getCurrentActivity();
	}

	public void testFileOverwriteCancel() throws Exception {

		File file1 = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");
		if (!file1.exists()) {
			Utils.saveCurrentPicture(solo, "overwrite_test");
		}

		solo.sleep(1000);

		File file2 = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");
		assertTrue(file2.exists());

		Utils.saveCurrentPicture(solo, "overwrite_test");

		solo.clickOnButton(cancelText);
		Log.d("PaintroidTest", "File has been overwriten");

		solo.clickOnButton(saveText);
		solo.enterText(0, "overwrite_test_afterCancel");
		solo.clickOnButton(doneText);

		File file3 = new File(Environment.getExternalStorageDirectory().toString()
				+ "/Paintroid/overwrite_test_afterCancel.png");

		if (file3.exists()) {
			solo.clickOnButton(yesText);
		}
	}
}
