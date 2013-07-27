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

import java.io.File;
import java.util.Vector;

import junit.framework.AssertionFailedError;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.ui.DrawingSurface;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Environment;
import android.widget.EditText;
import android.widget.LinearLayout;

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

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		mCurrentDrawingSurfaceBitmap.setPixel(xCoordinatePixel, yCoordinatePixel, Color.BLACK);

		assertEquals("Color on drawing surface wrong", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForActivity("AlertActivity", TIMEOUT);
		mSolo.clickOnButton(mSolo.getString(R.string.yes));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		int bitmapPixelColor = PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel,
				yCoordinatePixel));
		assertEquals("Color should be Transbarent", Color.TRANSPARENT, bitmapPixelColor);
	}

	public void testLoadImageDialog() {

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_load_image));
		mSolo.waitForActivity("AlertActivity", TIMEOUT);
		mSolo.clickOnButton(mSolo.getString(R.string.no));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
	}

	public void testLoadImageDialogOnBackPressed() {
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_load_image));
		mSolo.waitForActivity("AlertActivity", TIMEOUT);
		mSolo.goBack();
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

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

	public void testWarningDialogOnNewImageFromCamera() {

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		boolean tryContensedString = false;
		try {
			mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_from_camera));
		} catch (AssertionFailedError assertionFailedError) {
			tryContensedString = true;
			mSolo.goBack();
		}

		if (tryContensedString) {
			mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_from_camera_condensed));
		}
		mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true);
		// mSolo.waitForActivity("AlertActivity", TIMEOUT);
		assertTrue("New drawing warning not found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
		assertTrue("New drawing 'yes' button not found", mSolo.searchButton(mSolo.getString(R.string.yes), true));
		assertTrue("New drawing 'no' button not found", mSolo.searchButton(mSolo.getString(R.string.no), true));
		mSolo.goBack();
		assertFalse("New drawing warning still found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
	}

	public void testCancelNewDrawingDialog() {
		final int xCoordinatePixel = 0;
		final int yCoordinatePixel = 0;

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		mCurrentDrawingSurfaceBitmap.setPixel(xCoordinatePixel, yCoordinatePixel, Color.BLACK);
		assertEquals("Color on drawing surface wrong", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true);
		// mSolo.waitForActivity("AlertActivity", TIMEOUT);
		mSolo.clickOnButton(mSolo.getString(R.string.no));
		assertFalse("New drawing warning still found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
		assertEquals("Bitmap pixel changed:", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));
	}

	public void testNewDrawingDialogOnBackPressed() {
		final int xCoordinatePixel = 0;
		final int yCoordinatePixel = 0;

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		mCurrentDrawingSurfaceBitmap.setPixel(xCoordinatePixel, yCoordinatePixel, Color.BLACK);
		assertEquals("Color on drawing surface wrong", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true);
		// mSolo.waitForActivity("AlertActivity", TIMEOUT);
		assertTrue("New drawing warning not found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
		assertTrue("New drawing 'yes' button not found", mSolo.searchButton(mSolo.getString(R.string.yes), true));
		assertTrue("New drawing 'no' button not found", mSolo.searchButton(mSolo.getString(R.string.no), true));
		mSolo.goBack();
		assertFalse("New drawing warning still found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
		assertEquals("Bitmap pixel changed:", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));

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
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
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
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		assertTrue("image file should exist", imageFile.exists());
		assertTrue("image should be deleted", imageFile.delete());
	}

	public void testSaveImageDialogCorrectFileNameOkPressedFileExistsOverwrite() {
		final int xCoordinatePixel = 100;
		final int yCoordinatePixel = 100;
		FileIO.saveBitmap(getActivity(), PaintroidApplication.drawingSurface.getBitmapCopy(),
				FILENAMES.get(CORRECT_FILENAME_INDEX));

		File imageFile = getImageFile(FILENAMES.get(CORRECT_FILENAME_INDEX));
		long oldFileLength = imageFile.length();

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mCurrentDrawingSurfaceBitmap.setPixel(xCoordinatePixel, yCoordinatePixel, Color.BLACK);
		assertEquals("Color on drawing surface wrong", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		FILENAMES.add(editText.getHint().toString());

		mSolo.enterText(editText, FILENAMES.get(CORRECT_FILENAME_INDEX));
		mSolo.clickOnText(mSolo.getString(R.string.ok));
		assertTrue("wait for overwrite question",
				mSolo.waitForText(mSolo.getString(R.string.dialog_overwrite_text), 1, TIMEOUT));
		mSolo.clickOnButton(mSolo.getString(R.string.yes));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		imageFile = getImageFile(FILENAMES.get(CORRECT_FILENAME_INDEX));
		assertTrue("image file should exist", imageFile.exists());
		long newFileLength = imageFile.length();
		assertTrue("actual file should be bigger", newFileLength > oldFileLength);

		assertTrue("image should be deleted", imageFile.delete());
	}

	public void testSaveImageDialogCorrectFileNameOkPressedFileExitsNotOverwrite() {
		FileIO.saveBitmap(getActivity(), PaintroidApplication.drawingSurface.getBitmapCopy(),
				FILENAMES.get(CORRECT_FILENAME_INDEX));

		File imageFile = getImageFile(FILENAMES.get(CORRECT_FILENAME_INDEX));
		long oldFileLength = imageFile.length();

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
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
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		assertFalse("image file should not exist", imageFile.exists());
	}

	public void testSaveImageDialogOnBackPressed() {
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		FILENAMES.add(editText.getHint().toString());
		File imageFile = getImageFile(editText.getHint().toString());
		mSolo.goBack();
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		assertFalse("image file should not exist", imageFile.exists());
	}

	public void testSaveLoadedImageCorrectFileNameOkPressed() {
		final int xCoordinatePixel = 100;
		final int yCoordinatePixel = 50;

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mCurrentDrawingSurfaceBitmap.setPixel(xCoordinatePixel, yCoordinatePixel, Color.BLACK); //
		mSolo.clickOnScreen(xCoordinatePixel, yCoordinatePixel);
		assertEquals("Color on drawing surface wrong", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));

		FileIO.saveBitmap(getActivity(), PaintroidApplication.drawingSurface.getBitmapCopy(),
				FILENAMES.get(CORRECT_FILENAME_INDEX));

		File imageFile = getImageFile(FILENAMES.get(CORRECT_FILENAME_INDEX));
		long oldFileLength = imageFile.length();

		PaintroidApplication.loadedFileName = FILENAMES.get(CORRECT_FILENAME_INDEX);
		PaintroidApplication.loadedFilePath = imageFile.getAbsolutePath();

		final int newXCoordinatePixel = 100;
		final int newYCoordinatePixel = 150;
		mCurrentDrawingSurfaceBitmap.setPixel(newXCoordinatePixel, newYCoordinatePixel, Color.BLACK);
		assertEquals("Color on drawing surface wrong", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(newXCoordinatePixel, newYCoordinatePixel)));

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		assertTrue("wait for overwrite question",
				mSolo.waitForText(mSolo.getString(R.string.dialog_overwrite_text), 1, TIMEOUT));
		mSolo.clickOnText(mSolo.getString(R.string.yes));

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		imageFile = getImageFile(FILENAMES.get(CORRECT_FILENAME_INDEX));

		assertTrue("image file should exist", imageFile.exists());
		long newFileLength = imageFile.length();
		assertTrue("actual file should be bigger", newFileLength > oldFileLength);
		assertTrue("image should be deleted", imageFile.delete());

	}

	public void testSaveLoadedImageCorrectFileNameNoOverride() {
		final int xCoordinatePixel = 100;
		final int yCoordinatePixel = 50;

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mCurrentDrawingSurfaceBitmap.setPixel(xCoordinatePixel, yCoordinatePixel, Color.BLACK); //
		mSolo.clickOnScreen(xCoordinatePixel, yCoordinatePixel);
		assertEquals("Color on drawing surface wrong", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));

		FileIO.saveBitmap(getActivity(), PaintroidApplication.drawingSurface.getBitmapCopy(),
				FILENAMES.get(CORRECT_FILENAME_INDEX));

		File imageFile = getImageFile(FILENAMES.get(CORRECT_FILENAME_INDEX));
		long oldFileLength = imageFile.length();

		PaintroidApplication.loadedFileName = FILENAMES.get(CORRECT_FILENAME_INDEX);
		PaintroidApplication.loadedFilePath = imageFile.getAbsolutePath();

		final int newXCoordinatePixel = 100;
		final int newYCoordinatePixel = 150;
		mCurrentDrawingSurfaceBitmap.setPixel(newXCoordinatePixel, newYCoordinatePixel, Color.BLACK);
		assertEquals("Color on drawing surface wrong", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(newXCoordinatePixel, newYCoordinatePixel)));

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		assertTrue("wait for overwrite question",
				mSolo.waitForText(mSolo.getString(R.string.dialog_overwrite_text), 1, TIMEOUT));
		mSolo.clickOnText(mSolo.getString(R.string.no));

		EditText editText = (EditText) mSolo.getView(R.id.dialog_save_file_edit_text);
		FILENAMES.add(editText.getHint().toString());

		mSolo.enterText(editText, FILENAMES.get(CORRECT_FILENAME_INDEX));
		mSolo.clickOnText(mSolo.getString(R.string.ok));
		assertTrue("wait for overwrite question",
				mSolo.waitForText(mSolo.getString(R.string.dialog_overwrite_text), 1, TIMEOUT));
		mSolo.clickOnButton(mSolo.getString(R.string.yes));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		imageFile = getImageFile(FILENAMES.get(CORRECT_FILENAME_INDEX));
		assertTrue("image file should exist", imageFile.exists());
		long newFileLength = imageFile.length();
		assertTrue("actual file should be bigger", newFileLength > oldFileLength);

		assertTrue("image should be deleted", imageFile.delete());
	}

	private File getImageFile(String filename) {
		File imageFile = new File(Environment.getExternalStorageDirectory(), "/"
				+ PaintroidApplication.applicationContext.getString(R.string.app_name) + "/" + filename + ".png");
		return imageFile;
	}
}
