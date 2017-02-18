/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.provider.MediaStore;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.NavigationDrawerMenuActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.ui.DrawingSurface;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class MenuFileActivityIntegrationTest extends BaseIntegrationTestClass {

	private static ArrayList<File> deletionFileList = null;
	private PointF screenPoint = null;

	public MenuFileActivityIntegrationTest() throws Exception {
		super();
	}

	@Override
	public void setUp() {
		super.setUp();
		screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		deletionFileList = new ArrayList<File>();
	}

	@Override
	public void tearDown() throws Exception {
		PaintroidApplication.savedPictureUri = null;
		PaintroidApplication.isSaved = false;
		for (File file : deletionFileList) {
			if (file != null) {
				boolean deleted = file.delete();
				assertTrue("File has not been deleted correctly", deleted);
			}
		}
		super.tearDown();
	}

	public void testNewEmptyDrawingWithSave() {
		final int xCoordinatePixel = 0;
		final int yCoordinatePixel = 0;

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);

		mCurrentDrawingSurfaceBitmap.setPixel(xCoordinatePixel, yCoordinatePixel, Color.BLACK);

		assertEquals("Color on drawing surface wrong", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));
		openMenu();
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForDialogToOpen();
		mSolo.clickOnButton(mSolo.getString(R.string.save_button_text));

		mSolo.waitForText(mSolo.getString(R.string.saved));

		mSolo.waitForDialogToOpen();

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_empty_image));
		mSolo.waitForDialogToClose();

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		int bitmapPixelColor = PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel,
				yCoordinatePixel));
		assertEquals("Color should be Transparent", Color.TRANSPARENT, bitmapPixelColor);
	}

	public void testLoadImageDialog() {
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);

		openMenu();
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_load_image));
		mSolo.waitForDialogToOpen();
		assertTrue("New drawing 'save' button not found",
				mSolo.searchButton(mSolo.getString(R.string.save_button_text), true));
		assertTrue("New drawing 'discard' button not found",
				mSolo.searchButton(mSolo.getString(R.string.discard_button_text), true));
	}

	public void testLoadImageDialogOnBackPressed() {
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);

		openMenu();
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_load_image));
		mSolo.waitForDialogToOpen();
		mSolo.goBack();
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

	}

	public void testWarningDialogOnNewImage() {

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);

		openMenu();
		mSolo.clickOnText(mSolo.getString(R.string.menu_new_image));

			assertTrue("Not-saved Dialog does not appear", mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image)));

		assertTrue("New drawing 'yes' button not found",
				mSolo.searchButton(mSolo.getString(R.string.save_button_text), true));
		assertTrue("New drawing 'no' button not found",
				mSolo.searchButton(mSolo.getString(R.string.discard_button_text), true));
		mSolo.goBack();
		assertFalse("New drawing warning still found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
	}

	public void testNewEmptyDrawingWithDiscard() {

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);

		openMenu();
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		//mSolo.waitForDialogToOpen();
		//mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_empty_image));

		mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true);

		mSolo.clickOnButton(mSolo.getString(R.string.discard_button_text));
		mSolo.waitForDialogToClose();

		assertFalse("New drawing warning still found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
		assertNotSame("Bitmap pixel not changed:", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(Utils.getCanvasPointFromScreenPoint(new PointF(mScreenWidth / 2, mScreenHeight / 2))));
	}

	public void testNewEmptyDrawingDialogOnBackPressed() {

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);

		openMenu();
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));

		assertTrue(mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true));

		assertTrue("New drawing 'yes' button not found",
				mSolo.searchButton(mSolo.getString(R.string.save_button_text), true));
		assertTrue("New drawing 'no' button not found",
				mSolo.searchButton(mSolo.getString(R.string.discard_button_text), true));
		mSolo.goBack();
		assertFalse("New drawing warning still found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
		assertEquals("Bitmap pixel changed:", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(Utils.getCanvasPointFromScreenPoint(new PointF(mScreenWidth / 2, mScreenHeight / 2))));

	}

	public void testSavedStateChangeAfterSave() {
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_TIMEOUT);

		assertFalse(PaintroidApplication.isSaved);
		mSolo.sendKey(mSolo.MENU);
		mSolo.sleep(SHORT_SLEEP);
		openMenu();
		mSolo.clickOnText(mSolo.getString(R.string.menu_save_image));

		mSolo.waitForDialogToClose();

		addUriToDeletionFileList(PaintroidApplication.savedPictureUri);
		assertTrue(PaintroidApplication.isSaved);

	}

	public void testSaveImage() {
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_TIMEOUT);

		openMenu();
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.waitForDialogToClose();
		assertEquals("current Activity not MainActivity", MainActivity.class, mSolo.getCurrentActivity().getClass());


		assertNotNull(PaintroidApplication.savedPictureUri);
		addUriToDeletionFileList(PaintroidApplication.savedPictureUri);
	}

	public void testSaveCopy() {
		assertNull(PaintroidApplication.savedPictureUri);
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_TIMEOUT);

		openMenu();
		mSolo.clickOnText(mSolo.getString(R.string.menu_save_image));

		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.waitForDialogToClose();
		assertNotNull(PaintroidApplication.savedPictureUri);
		addUriToDeletionFileList(PaintroidApplication.savedPictureUri);
		File oldFile = new File(PaintroidApplication.savedPictureUri.toString());

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y + 100);
		mSolo.sleep(SHORT_SLEEP);

		openMenu();
		mSolo.clickOnText(mSolo.getString(R.string.menu_save_copy));

		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.waitForDialogToClose();

		File newFile = new File(PaintroidApplication.savedPictureUri.toString());
		assertNotSame(oldFile, newFile);
		addUriToDeletionFileList(PaintroidApplication.savedPictureUri);

		mSolo.goBack();
	}

	public void testSaveLoadedImage() throws URISyntaxException, IOException {
		final NavigationDrawerMenuActivity activityToTest = new NavigationDrawerMenuActivity() {
			@Override
			public void onActivityResult(int requestCode, int resultCode, Intent data) {
				super.onActivityResult(requestCode, resultCode, data);
			}
		};

		final int requestCodeLoadPicture = 2;
		final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		try {
			runTestOnUiThread(new Runnable() {
				@Override
				public void run() {
					activityToTest.onActivityResult(requestCodeLoadPicture, Activity.RESULT_OK, intent);
				}
			});
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}

		openMenu();
		assertTrue("Save copy flag should be set to true", PaintroidApplication.saveCopy);

		mSolo.clickOnText(mSolo.getString(R.string.menu_save_copy));
		mSolo.waitForDialogToClose();
		assertNotNull(PaintroidApplication.savedPictureUri);

		File saveFile = new File(getRealFilePathFromUri(PaintroidApplication.savedPictureUri));
		addUriToDeletionFileList(PaintroidApplication.savedPictureUri);
		long oldLength = saveFile.length();
		long firstModified = saveFile.lastModified();

		PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_TIMEOUT);

		openMenu();
		assertTrue("Save image button should be visible again", mSolo.searchText(mSolo.getString(R.string.menu_save_image)));
		mSolo.clickOnText(mSolo.getString(R.string.menu_save_image));
		mSolo.waitForDialogToClose();

		long newLength = saveFile.length();
		long lastModified = saveFile.lastModified();
		assertNotSame("File is still the same", oldLength, newLength);
		assertNotSame("File not currently modified", firstModified, lastModified);
	}


	private String getRealFilePathFromUri(Uri uri) {
		String[] fileColumns = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().getContentResolver().query(PaintroidApplication.savedPictureUri, fileColumns, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(fileColumns[0]);
		String realFilePath = cursor.getString(columnIndex);
		cursor.close();
		return realFilePath;
	}

	private void addUriToDeletionFileList(Uri uri) {
		deletionFileList.add(new File(getRealFilePathFromUri(uri)));
	}

}
