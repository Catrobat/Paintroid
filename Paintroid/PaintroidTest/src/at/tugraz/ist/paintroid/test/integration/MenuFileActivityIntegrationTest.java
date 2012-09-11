package at.tugraz.ist.paintroid.test.integration;

import java.io.File;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Environment;
import android.widget.EditText;
import android.widget.LinearLayout;
import at.tugraz.ist.paintroid.FileIO;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class MenuFileActivityIntegrationTest extends BaseIntegrationTestClass {

	private static final int CORRECT_FILENAME_INDEX = 0;
	private static Vector<String> FILENAMES = null;

	public MenuFileActivityIntegrationTest() throws Exception {
		super();
	}

	@Override
	public void setUp() {
		super.setUp();
		FILENAMES = new Vector<String>();
		FILENAMES.add(CORRECT_FILENAME_INDEX, "ÄÖÜ_TestFile_1");
		FILENAMES.add("T€ST");
		FILENAMES.add("T-est");
		FILENAMES.add(".test");
	}

	@Override
	public void tearDown() throws Exception {
		for (String filename : FILENAMES) {
			if (filename != null && filename.length() > 0)
				getImageFile(filename).delete();
		}
		super.tearDown();
	}

	public void testNewEmptyDrawing() {
		final int xCoordinatePixel = 0;
		final int yCoordinatePixel = 0;
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		PaintroidApplication.DRAWING_SURFACE.getBitmap().setPixel(xCoordinatePixel, yCoordinatePixel, Color.BLACK);
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForActivity("AlertActivity", TIMEOUT);
		mSolo.clickOnButton(mSolo.getString(R.string.yes));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		int newColor = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(xCoordinatePixel, yCoordinatePixel);
		assertEquals("Color should be Transbarent", newColor, Color.TRANSPARENT);
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

	// public void testNewDrawingFromCamera() {
	// mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_from_camera));
	// // FIXME test if cam takes image
	// }

	public void testCancelNewDrawingDialog() {
		final int xCoordinatePixel = 0;
		final int yCoordinatePixel = 0;
		try {
			((Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
					PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap")).setPixel(xCoordinatePixel,
					yCoordinatePixel, Color.BLACK);
		} catch (Exception whatever) {
			whatever.printStackTrace();
		}
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForActivity("AlertActivity", TIMEOUT);
		mSolo.clickOnButton(mSolo.getString(R.string.no));
		assertFalse("New drawing warning still found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
		assertEquals("Bitmap pixel changed:", Color.BLACK,
				PaintroidApplication.DRAWING_SURFACE.getBitmapColor(new PointF(xCoordinatePixel, yCoordinatePixel)));
	}

	public void testNewDrawingDialogOnBackPressed() {
		final int xCoordinatePixel = 0;
		final int yCoordinatePixel = 0;
		try {
			((Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
					PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap")).setPixel(xCoordinatePixel,
					yCoordinatePixel, Color.BLACK);
		} catch (Exception whatever) {
			whatever.printStackTrace();
		}

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForActivity("AlertActivity", TIMEOUT);
		assertTrue("New drawing warning not found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
		assertTrue("New drawing 'yes' button not found", mSolo.searchButton(mSolo.getString(R.string.yes), true));
		assertTrue("New drawing 'no' button not found", mSolo.searchButton(mSolo.getString(R.string.no), true));
		mSolo.goBack();
		assertFalse("New drawing warning still found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
		assertEquals("Bitmap pixel changed:", Color.BLACK,
				PaintroidApplication.DRAWING_SURFACE.getBitmapColor(new PointF(xCoordinatePixel, yCoordinatePixel)));

	}

	// public void testLoadImage() {
	// // mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_load_image));
	// FIXME test if 'app chooser' is visible and Image is loaded
	// }

	public void testSaveImageDialogEmptyFileNameOkPressed() {
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		FILENAMES.add(editText.getHint().toString());
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
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		FILENAMES.add(editText.getHint().toString());

		mSolo.enterText(editText, FILENAMES.get(CORRECT_FILENAME_INDEX));
		File imageFile = getImageFile(editText.getText().toString());
		if (imageFile.exists()) {
			assertTrue("image should be deleted", imageFile.delete());
		}
		mSolo.clickOnButton(mSolo.getString(R.string.ok));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		assertTrue("image file should exist", imageFile.exists());
		assertTrue("image should be deleted", imageFile.delete());
	}

	public void testSaveImageDialogCorrectFileNameOkPressedFileExistsOverwrite() {

		FileIO.saveBitmap(getActivity(), PaintroidApplication.DRAWING_SURFACE.getBitmap(),
				FILENAMES.get(CORRECT_FILENAME_INDEX));

		File imageFile = getImageFile(FILENAMES.get(CORRECT_FILENAME_INDEX));
		long oldFileLength = imageFile.length();

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		try {
			((Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
					PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap")).setPixel(100, 100, Color.BLACK);
		} catch (Exception whatever) {
			whatever.printStackTrace();
		}
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		FILENAMES.add(editText.getHint().toString());

		mSolo.enterText(editText, FILENAMES.get(CORRECT_FILENAME_INDEX));
		mSolo.clickOnText(mSolo.getString(R.string.ok));
		assertTrue("wait for overwrite question",
				mSolo.waitForText(mSolo.getString(R.string.dialog_overwrite_text), 1, TIMEOUT));
		mSolo.clickOnButton(mSolo.getString(R.string.yes));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		imageFile = getImageFile(FILENAMES.get(CORRECT_FILENAME_INDEX));
		assertTrue("image file should exist", imageFile.exists());
		long newFileLength = imageFile.length();
		assertTrue("actual file should be bigger", newFileLength > oldFileLength);

		assertTrue("image should be deleted", imageFile.delete());
	}

	public void testSaveImageDialogCorrectFileNameOkPressedFileExitsNotOverwrite() {
		FileIO.saveBitmap(getActivity(), PaintroidApplication.DRAWING_SURFACE.getBitmap(),
				FILENAMES.get(CORRECT_FILENAME_INDEX));

		File imageFile = getImageFile(FILENAMES.get(CORRECT_FILENAME_INDEX));
		long oldFileLength = imageFile.length();

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnScreen(100, 100);
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		FILENAMES.add(editText.getHint().toString());

		mSolo.enterText(editText, FILENAMES.get(CORRECT_FILENAME_INDEX));
		imageFile = getImageFile(editText.getText().toString());
		mSolo.clickOnText(mSolo.getString(R.string.ok));
		assertTrue("wait for overwrite question",
				mSolo.waitForText(mSolo.getString(R.string.dialog_overwrite_text), 1, TIMEOUT));
		mSolo.clickOnButton(mSolo.getString(R.string.no));
		assertTrue("wait for save file dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertTrue("Looking for save dialog title", mSolo.searchText(mSolo.getString(R.string.dialog_save_title)));
		assertTrue("image file should exist", imageFile.exists());
		long newFileLength = imageFile.length();
		assertEquals("actual file should have same file length", newFileLength, oldFileLength);
		assertTrue("image should be deleted", imageFile.delete());
	}

	public void testSaveImageDialogIncorrectFileNameOkPressed() {
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		FILENAMES.add(editText.getHint().toString());

		mSolo.enterText(editText, FILENAMES.get(CORRECT_FILENAME_INDEX + 1));
		File imageFile = getImageFile(editText.getText().toString());
		if (imageFile.exists()) {
			assertTrue("image should be deleted", imageFile.delete());
		}
		mSolo.clickOnText(mSolo.getString(R.string.ok));
		assertTrue("Waiting for unallowed chars dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertFalse("cancel button should not be found", mSolo.searchText(mSolo.getString(R.string.cancel)));
		assertFalse("image file should not exist", imageFile.exists());
		mSolo.goBack();

		//
		mSolo.clearEditText(editText);
		mSolo.enterText(editText, FILENAMES.get(CORRECT_FILENAME_INDEX + 2));
		imageFile = getImageFile(editText.getText().toString());
		if (imageFile.exists()) {
			assertTrue("image should be deleted", imageFile.delete());
		}
		mSolo.clickOnText(mSolo.getString(R.string.ok));
		assertTrue("Waiting for unallowed chars dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertFalse("cancel button should not be found", mSolo.searchText(mSolo.getString(R.string.cancel)));
		assertFalse("image file should not exist", imageFile.exists());
		mSolo.clickOnText(mSolo.getString(R.string.ok));

		//
		mSolo.clearEditText(editText);
		mSolo.enterText(editText, FILENAMES.get(CORRECT_FILENAME_INDEX + 3));
		imageFile = getImageFile(editText.getText().toString());
		if (imageFile.exists()) {
			assertTrue("image should be deleted", imageFile.delete());
		}
		mSolo.clickOnText(mSolo.getString(R.string.ok));
		assertTrue("Waiting for save dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertFalse("cancel button should not be found", mSolo.searchText(mSolo.getString(R.string.cancel)));
		assertFalse("image file should not exist", imageFile.exists());

	}

	public void testSaveImageDialogOnCancelPressed() {
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		FILENAMES.add(editText.getHint().toString());

		File imageFile = getImageFile(editText.getHint().toString());
		mSolo.clickOnText(mSolo.getString(R.string.cancel));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		assertFalse("image file should not exist", imageFile.exists());
	}

	public void testSaveImageDialogOnBackPressed() {
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		FILENAMES.add(editText.getHint().toString());
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
}
