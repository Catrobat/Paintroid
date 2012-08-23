package at.tugraz.ist.paintroid.test.integration;

import java.io.File;

import junit.framework.AssertionFailedError;
import android.graphics.Color;
import android.os.Environment;
import android.widget.EditText;
import android.widget.LinearLayout;
import at.tugraz.ist.paintroid.FileIO;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class MenuFileActivityIntegrationTest extends BaseIntegrationTestClass {

	private static final String CORRECT_FILENAME = "ÄÖÜ_TestFile_1";
	private static final String INCORRECT_FILENAME_1 = "T€ST";
	private static final String INCORRECT_FILENAME_2 = "T-est";
	private static final String INCORRECT_FILENAME_3 = ".test";

	public MenuFileActivityIntegrationTest() throws Exception {
		super();
	}

	public void testNewEmptyDrawing() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		int clickX = 100;
		int clickY = mScreenHeight / 2;

		mSolo.clickOnScreen(clickX, clickY);
		int statusBarHeight = Utils.getStatusbarHeigt(getActivity());
		int actionBarHeight = getActivity().getSupportActionBar().getHeight();
		int oldColor = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(clickX,
				clickY - statusBarHeight - actionBarHeight);
		assertEquals("Color should be Black", Color.BLACK, oldColor);

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		int newColor = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(clickX,
				clickY - statusBarHeight - actionBarHeight);
		assertEquals("Color should be Transbarent", Color.TRANSPARENT, newColor);
	}

	// ///////////////////////////////////////////
	// FIXME test if cam takes image
	//
	// public void testNewDrawingFromCamera() {
	//
	// }

	// ////////////////////////////////////////////
	// FIXME test if 'app chooser' is visible and Image is loaded
	//
	// public void testLoadImage() {
	// openFileMenu();
	// assertTrue("Search for LoadImage button", mSolo.searchText(mSolo.getString(R.string.load)));
	//
	// }

	public void testSaveImageDialogEmptyFileNameOkPressed() {
		openSaveDialog();
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		File imageFile = getImageFile(editText.getHint().toString());
		if (imageFile.exists()) {
			assertTrue("image should be deleted", imageFile.delete());
		}
		mSolo.clickOnText(mSolo.getString(R.string.ok));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		assertTrue("image file should exist", imageFile.exists());
		assertTrue("image should be deleted", imageFile.delete());
	}

	public void testSaveImageDialogCorrectFileNameOkPressedFileNotExists() {
		openSaveDialog();
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		mSolo.enterText(editText, CORRECT_FILENAME);
		File imageFile = getImageFile(editText.getText().toString());
		if (imageFile.exists()) {
			assertTrue("image should be deleted", imageFile.delete());
		}
		mSolo.clickOnText(mSolo.getString(R.string.ok));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		assertTrue("image file should exist", imageFile.exists());
		assertTrue("image should be deleted", imageFile.delete());
	}

	public void testSaveImageDialogCorrectFileNameOkPressedFileExistsOverwrite() {

		FileIO.saveBitmap(getActivity(), PaintroidApplication.DRAWING_SURFACE.getBitmap(), CORRECT_FILENAME);

		File imageFile = getImageFile(CORRECT_FILENAME);
		long oldFileLength = imageFile.length();
		int clickX = 100;
		int clickY = 100 + getActivity().getSupportActionBar().getHeight();
		// save again
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnScreen(clickX, clickY);
		openSaveDialog();
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		mSolo.enterText(editText, CORRECT_FILENAME);
		imageFile = getImageFile(editText.getText().toString());
		mSolo.clickOnText(mSolo.getString(R.string.ok));
		assertTrue("wait for overwrite question", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		mSolo.clickOnText(mSolo.getString(R.string.yes));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		assertTrue("image file should exist", imageFile.exists());
		long newFileLength = imageFile.length();
		assertTrue("actual file should be bigger", newFileLength > oldFileLength);
		assertTrue("image should be deleted", imageFile.delete());
	}

	public void testSaveImageDialogCorrectFileNameOkPressedFileExitsNotOverwrite() {
		FileIO.saveBitmap(getActivity(), PaintroidApplication.DRAWING_SURFACE.getBitmap(), CORRECT_FILENAME);

		File imageFile = getImageFile(CORRECT_FILENAME);
		long oldFileLength = imageFile.length();

		// save again
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnScreen(100, 100);
		openSaveDialog();
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		mSolo.enterText(editText, CORRECT_FILENAME);
		imageFile = getImageFile(editText.getText().toString());
		mSolo.clickOnText(mSolo.getString(R.string.ok));
		assertTrue("wait for overwrite question", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		mSolo.clickOnText(mSolo.getString(R.string.no));
		assertTrue("wait for save file dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertTrue("Looking for save dialog title", mSolo.searchText(mSolo.getString(R.string.dialog_save_title)));
		assertTrue("image file should exist", imageFile.exists());
		long newFileLength = imageFile.length();
		assertEquals("actual file should me newer", newFileLength, oldFileLength);
		assertTrue("image should be deleted", imageFile.delete());
	}

	public void testSaveImageDialogIncorrectFileNameOkPressed() {
		openSaveDialog();
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);

		// incorrect filename 1
		mSolo.enterText(editText, INCORRECT_FILENAME_1);
		File imageFile = getImageFile(editText.getText().toString());
		if (imageFile.exists()) {
			assertTrue("image should be deleted", imageFile.delete());
		}
		mSolo.clickOnText(mSolo.getString(R.string.ok));
		assertTrue("Waiting for unallowed chars dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		boolean assertionFailedErrorCaught = false;
		try {
			mSolo.clickOnText(mSolo.getString(R.string.cancel));
		} catch (AssertionFailedError error) {
			assertionFailedErrorCaught = true;
		} finally {
			assertTrue("cancel button should not be found", assertionFailedErrorCaught);
		}

		assertFalse("image file should not exist", imageFile.exists());
		mSolo.goBack();

		// incorrect filename 2
		mSolo.clearEditText(editText);
		mSolo.enterText(editText, INCORRECT_FILENAME_2);
		imageFile = getImageFile(editText.getText().toString());
		if (imageFile.exists()) {
			assertTrue("image should be deleted", imageFile.delete());
		}
		mSolo.clickOnText(mSolo.getString(R.string.ok));
		assertTrue("Waiting for unallowed chars dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertionFailedErrorCaught = false;
		try {
			mSolo.clickOnText(mSolo.getString(R.string.cancel));
		} catch (AssertionFailedError error) {
			assertionFailedErrorCaught = true;
		} finally {
			assertTrue("cancel button should not be found", assertionFailedErrorCaught);
		}
		assertFalse("image file should not exist", imageFile.exists());
		mSolo.clickOnText(mSolo.getString(R.string.ok));

		// incorrect filename 3
		mSolo.clearEditText(editText);
		mSolo.enterText(editText, INCORRECT_FILENAME_3);
		imageFile = getImageFile(editText.getText().toString());
		if (imageFile.exists()) {
			assertTrue("image should be deleted", imageFile.delete());
		}
		mSolo.clickOnText(mSolo.getString(R.string.ok));
		assertTrue("Waiting for save dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertionFailedErrorCaught = false;
		try {
			mSolo.clickOnText(mSolo.getString(R.string.cancel));
		} catch (AssertionFailedError error) {
			assertionFailedErrorCaught = true;
		} finally {
			assertTrue("cancel button should not be found", assertionFailedErrorCaught);
		}
		assertFalse("image file should not exist", imageFile.exists());

	}

	public void testSaveImageDialogOnCancelPressed() {
		openSaveDialog();
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		File imageFile = getImageFile(editText.getHint().toString());
		mSolo.clickOnText(mSolo.getString(R.string.cancel));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		assertFalse("image file should not exist", imageFile.exists());
	}

	public void testSaveImageDialogOnBackPressed() {
		openSaveDialog();
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		File imageFile = getImageFile(editText.getHint().toString());
		mSolo.goBack();
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		assertFalse("image file should not exist", imageFile.exists());
	}

	private File getImageFile(String filename) {
		File imageFile = new File(Environment.getExternalStorageDirectory(), "/"
				+ PaintroidApplication.APPLICATION_CONTEXT.getString(R.string.app_name) + "/" + filename + ".png");
		return imageFile;
	}

	private void openSaveDialog() {

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		assertTrue("Waiting for save dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertTrue("Looking for save dialog title", mSolo.searchText(mSolo.getString(R.string.dialog_save_title)));
		assertTrue("Looking for save dialog text", mSolo.searchText(mSolo.getString(R.string.dialog_save_text)));
	}

}
