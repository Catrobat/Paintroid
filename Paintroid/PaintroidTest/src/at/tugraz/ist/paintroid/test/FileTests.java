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

import com.jayway.android.robotium.solo.Solo;

import android.content.res.Configuration;
import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import at.tugraz.ist.paintroid.MainActivity;


public class FileTests extends ActivityInstrumentationTestCase2<MainActivity> {
	private Solo solo;
	private MainActivity mainActivity;
	
	// Buttonindexes
	final int FILE = 8;

	public FileTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		String languageToLoad_before  = "en";
		Locale locale_before = new Locale(languageToLoad_before);
		Locale.setDefault(locale_before);
		
		Configuration config_before = new Configuration();
		config_before.locale = locale_before;
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources().updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());
	}
	
	public void testSaveEmptyPicture() throws Exception{
		solo.clickOnMenuItem("Clear Drawing");
		
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Save");
		solo.enterText(0, "test_empty");
		solo.clickOnButton("Done");
		
		File file = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/test_empty.png");

		if(file.exists()){
			solo.clickOnButton("Yes");
			Log.d("PaintroidTest", "File has been overwriten");
		}
		
		assertTrue(solo.waitForActivity("MainActivity", 500));
		
	}
	
	public void testSavePicturePath() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Save");
		solo.enterText(0, "test_save");
		solo.clickOnButton("Done");
		
		File file = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/test_save.png");

		if(file.exists()){
			solo.clickOnButton("Yes");
			Log.d("PaintroidTest", "File has been overwriten");
		}
		
		assertTrue(solo.waitForActivity("MainActivity", 500));
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		assertEquals(mainActivity.getSavedFileUriString(), Environment.getExternalStorageDirectory().toString() + "/Paintroid/test_save.png");
		
	}
	
	public void testPictureIsSavedCorrectly() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Save");
		solo.enterText(0, "test_save_2");
		solo.clickOnButton("Done");
		
		File file = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/test_save_2.png");

		if(file.exists()){
			solo.clickOnButton("Yes");
			Log.d("PaintroidTest", "File has been overwriten");
		}
		
		assertTrue(solo.waitForActivity("MainActivity", 500));
		
		File dir = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/test_save_2.png");
		if(dir.exists()) {
		    
		}else{
			assertTrue(false);
		}
	}
	
	public void testFileOverwriteYes() throws Exception{
		File file = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");
		if(!file.exists()){
			solo.clickOnImageButton(FILE);
			solo.clickOnButton("Save");
			solo.enterText(0, "overwrite_test");
			solo.clickOnButton("Done");
		}
		
		Thread.sleep(1000);
		
		file = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");
		assertTrue(file.exists());
		
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Save");
		solo.enterText(0, "overwrite_test");
		solo.clickOnButton("Done");
		
		file = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");

		solo.clickOnButton("Yes");
		Log.d("PaintroidTest", "File has been overwriten");
		
		mainActivity = (MainActivity) solo.getCurrentActivity();

		assertEquals(mainActivity.getSavedFileUriString(), Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");
		
	}
	
	public void testFileOverwriteCancel() throws Exception{
		
		File file = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");
		if(!file.exists()){
			solo.clickOnImageButton(FILE);
			solo.clickOnButton("Save");
			solo.enterText(0, "overwrite_test");
			solo.clickOnButton("Done");
		}
		
		Thread.sleep(1000);
		
		file = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");
		assertTrue(file.exists());
		
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Save");
		solo.enterText(0, "overwrite_test");
		solo.clickOnButton("Done");

		solo.clickOnButton("Cancel");
		Log.d("PaintroidTest", "File has been overwriten");
		
		solo.clickOnButton("Save");
		solo.enterText(0, "overwrite_test_afterCancel");
		solo.clickOnButton("Done");

		File file_after = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test_afterCancel.png");
		
		if(file_after.exists()){
			solo.clickOnButton("Yes");
		}
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		assertEquals(mainActivity.getSavedFileUriString(), Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test_afterCancel.png");
	}
	
	@Override
  public void tearDown() throws Exception {
    solo.clickOnMenuItem("Quit");
    try {
      solo.finalize();
    } catch (Throwable e) {
      e.printStackTrace();
    }
    getActivity().finish();
    super.tearDown();
  }
}
