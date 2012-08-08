package at.tugraz.ist.paintroid.test.integration.dialog;

import java.io.File;

import android.os.Environment;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MenuFileActivity;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.test.integration.BaseIntegrationTestClass;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class SaveFileDialogIntegrationTest extends BaseIntegrationTestClass {

	public SaveFileDialogIntegrationTest() throws Exception {
		super();
	}

	public void testSaveFileDialogForExistingButtons() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnText(mSolo.getString(R.string.button_filemanager), 1, true);
		assertTrue("Waiting for File Manager", mSolo.waitForActivity(MenuFileActivity.class.getSimpleName(), TIMEOUT));
		mSolo.clickOnText(mSolo.getString(R.string.save), 1, true);
		assertTrue("Waiting for Save dialog", mSolo.waitForView(TextView.class, 1, TIMEOUT));

		String buttonName = mMainActivity.getResources().getString(R.string.ok);
		Button okButton = mSolo.getButton(buttonName);
		assertTrue("OK button should exist", okButton != null);

		buttonName = mMainActivity.getResources().getString(R.string.cancel);
		Button cancelButton = mSolo.getButton(buttonName);
		assertTrue("Cancel button should exist", cancelButton != null);
	}

	public void testSaveFile() {
		String fileName = "bla";
		File file = new File(Environment.getExternalStorageDirectory() + "/"
				+ PaintroidApplication.APPLICATION_CONTEXT.getString(R.string.app_name) + "/", fileName + ".png");
		if (file.exists())
			file.delete();

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnText(mSolo.getString(R.string.button_filemanager), 1, true);
		assertTrue("Waiting for File Manager", mSolo.waitForActivity(MenuFileActivity.class.getSimpleName(), TIMEOUT));
		mSolo.clickOnText(mSolo.getString(R.string.save), 1, true);
		assertTrue("Waiting for Save dialog", mSolo.waitForView(TextView.class, 1, TIMEOUT));

		int editTextFieldIndex = 0;
		mSolo.enterText(editTextFieldIndex, fileName);
		mSolo.clickOnText(mSolo.getString(R.string.ok), 1, true);

		File newFile = new File(Environment.getExternalStorageDirectory() + "/"
				+ PaintroidApplication.APPLICATION_CONTEXT.getString(R.string.app_name) + "/", fileName + ".png");
		assertNotNull("File should not be null", newFile);
		assertTrue("File should exist", newFile.exists());

		newFile.delete();
	}

	public void testSaveWithInvalidFilename() {
		String fileName = "";
		File file = new File(Environment.getExternalStorageDirectory() + "/"
				+ PaintroidApplication.APPLICATION_CONTEXT.getString(R.string.app_name) + "/", fileName + ".png");
		if (file.exists())
			file.delete();

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnText(mSolo.getString(R.string.button_filemanager), 1, true);
		assertTrue("Waiting for File Manager", mSolo.waitForActivity(MenuFileActivity.class.getSimpleName(), TIMEOUT));
		mSolo.clickOnText(mSolo.getString(R.string.save), 1, true);
		assertTrue("Waiting for Save dialog", mSolo.waitForView(TextView.class, 1, TIMEOUT));

		mSolo.clickOnText(mSolo.getString(R.string.ok), 1, true);
		assertTrue("Waiting for Error message", mSolo.waitForView(TextView.class, 1, TIMEOUT));
		String errorString = mMainActivity.getResources().getString(R.string.dialog_error_invalid_filename_text);
		boolean textFound = mSolo.waitForText(errorString, 1, TIMEOUT, true, false);
		assertTrue("Error text " + errorString + " should exist", textFound);

		mSolo.clickOnButton(0);
		File unnamedFile = new File(Environment.getExternalStorageDirectory() + "/"
				+ PaintroidApplication.APPLICATION_CONTEXT.getString(R.string.app_name) + "/", fileName + ".png");
		assertFalse(unnamedFile.exists());
	}

	// #########################################################
	// The following testcase is working on local devices, but should not be used on Jenkins as remounting problems can
	// occur if the testcase fails
	// To get it work the MOUNT_UNMOUNT_FILESYSTEMS permission has to be set in the AndroidManifest.xml in the Paintroid
	// project!
	//
	// public void testSaveWithoutSDCard() {
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	//
	// String fileName = "bla";
	// File file = new File(Environment.getExternalStorageDirectory() + "/"
	// + PaintroidApplication.APPLICATION_CONTEXT.getString(R.string.app_name) + "/", fileName + ".png");
	// if (file.exists())
	// file.delete();
	//
	// try {
	// Utils.setUSBMassstorageStatus(mSolo, true);
	// } catch (Exception e) {
	// Log.d(PaintroidApplication.TAG, "SD-card could not be unmounted");
	// e.printStackTrace();
	// }
	//
	// mSolo.clickOnView(mToolBarButtonMain);
	// assertTrue("Waiting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
	// mSolo.clickOnText(mSolo.getString(R.string.button_filemanager), 1, true);
	// assertTrue("Waiting for File Manager", mSolo.waitForActivity(MenuFileActivity.class.getSimpleName(), TIMEOUT));
	// mSolo.clickOnText(mSolo.getString(R.string.save), 1, true);
	// assertTrue("Waiting for Save dialog", mSolo.waitForView(TextView.class, 1, TIMEOUT));
	//
	// int editTextFieldIndex = 0;
	// mSolo.enterText(editTextFieldIndex, fileName);
	// mSolo.clickOnText(mSolo.getString(R.string.ok), 1, true);
	// assertTrue("Waiting for Error message", mSolo.waitForView(TextView.class, 1, TIMEOUT));
	// String errorString = mMainActivity.getResources().getString(R.string.dialog_error_sdcard_text);
	// boolean textFound = mSolo.waitForText(errorString, 1, TIMEOUT, true, false);
	// assertTrue("Error text " + errorString + " should exist", textFound);
	//
	// mSolo.clickOnButton(0);
	// try {
	// Utils.setUSBMassstorageStatus(mSolo, false);
	// } catch (Exception e) {
	// Log.d(PaintroidApplication.TAG, "SD-card could not be mounted");
	// e.printStackTrace();
	// }
	// }
}
