package at.tugraz.ist.paintroid.test;

import java.io.File;

import com.jayway.android.robotium.solo.Solo;

import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import at.tugraz.ist.paintroid.MainActivity;


public class FileTests extends ActivityInstrumentationTestCase2<MainActivity> {
	private Solo solo;
	private MainActivity mainActivity;
	
	// Buttonindexes
	final int FILE = 7;

	public FileTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
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
		}else{
			solo.clickOnButton("Done");
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
		}else{
			solo.clickOnButton("Done");
		}
		
		
		assertTrue(solo.waitForActivity("MainActivity", 500));
		
		File dir = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/test_save_2.png");
		if(dir.exists()) {
		    solo.clickOnMenuItem("Quit");
		}else{
			assertTrue(false);
		}
			
	}
	
	public void testFileOverwriteYes() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Save");
		solo.enterText(0, "overwrite_test");
		solo.clickOnButton("Done");
		
		File file = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");

		if(file.exists()){
			solo.clickOnButton("Yes");
			Log.d("PaintroidTest", "File has been overwriten");
		}else{
			solo.clickOnButton("Done");
		}
		
		mainActivity = (MainActivity) solo.getCurrentActivity();

		assertEquals(mainActivity.getSavedFileUriString(), Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");
	}
	
	public void testFileOverwriteCancel() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Save");
		solo.enterText(0, "overwrite_test");
		solo.clickOnButton("Done");
		
		File file = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");

		if(file.exists()){
			solo.clickOnButton("Cancel");
			Log.d("PaintroidTest", "File has been overwriten");
		
			solo.clickOnButton("Save");
			solo.enterText(0, "overwrite_test_afterCancel");
			solo.clickOnButton("Done");
			
			File file_after = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test_afterCancel.png");
			
			if(file_after.exists()){
				solo.clickOnButton("Yes");
			}else{
				solo.clickOnButton("Done");
			}
			mainActivity = (MainActivity) solo.getCurrentActivity();
			assertEquals(mainActivity.getSavedFileUriString(), Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test_afterCancel.png");
		}else{
			solo.clickOnButton("Done");
			mainActivity = (MainActivity) solo.getCurrentActivity();
			assertEquals(mainActivity.getSavedFileUriString(), Environment.getExternalStorageDirectory().toString() + "/Paintroid/overwrite_test.png");
			
		}
		solo.clickOnMenuItem("Quit");
		
	}
}
