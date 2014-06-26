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

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.ProgressIntermediateDialog;
import org.catrobat.paintroid.ui.DrawingSurface;

import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Environment;

public class MenuFileActivityIntegrationTest extends BaseIntegrationTestClass {

	private static Vector<String> filenames = null;

	public MenuFileActivityIntegrationTest() throws Exception {
		super();
	}

	@Override
	public void setUp() {
		super.setUp();
		filenames = new Vector<String>();
	}

	@Override
	public void tearDown() throws Exception {
		PaintroidApplication.savedPictureUri = null;
		PaintroidApplication.isSaved = false;
		for (String filename : filenames) {
			if (filename != null && filename.length() > 0)
				getImageFile(filename).delete();
		}
		super.tearDown();
	}

	public void testNewEmptyDrawingWithSave() {
		final int xCoordinatePixel = 0;
		final int yCoordinatePixel = 0;

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		mCurrentDrawingSurfaceBitmap.setPixel(xCoordinatePixel, yCoordinatePixel, Color.BLACK);

		assertEquals("Color on drawing surface wrong", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForActivity("AlertActivity", TIMEOUT);
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_empty_image));
		mSolo.clickOnButton(mSolo.getString(R.string.save_button_text));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		int bitmapPixelColor = PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel,
				yCoordinatePixel));
		assertEquals("Color should be Transbarent", Color.TRANSPARENT, bitmapPixelColor);
	}

	public void testLoadImageDialog() {

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_load_image));
		mSolo.waitForActivity("AlertActivity", TIMEOUT);
		assertTrue("New drawing 'save' button not found",
				mSolo.searchButton(mSolo.getString(R.string.save_button_text), true));
		assertTrue("New drawing 'discard' button not found",
				mSolo.searchButton(mSolo.getString(R.string.discard_button_text), true));
		mSolo.goBack();
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

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.sleep(500);
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_from_camera));

		mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true);

		assertTrue("New drawing warning not found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
		assertTrue("New drawing 'yes' button not found",
				mSolo.searchButton(mSolo.getString(R.string.save_button_text), true));
		assertTrue("New drawing 'no' button not found",
				mSolo.searchButton(mSolo.getString(R.string.discard_button_text), true));
		mSolo.goBack();
		assertFalse("New drawing warning still found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
	}

	public void testNewEmptyDrawingWithDiscard() {
		final int xCoordinatePixel = 0;
		final int yCoordinatePixel = 0;

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		mCurrentDrawingSurfaceBitmap.setPixel(xCoordinatePixel, yCoordinatePixel, Color.BLACK);
		assertEquals("Color on drawing surface wrong", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForActivity("AlertActivity", TIMEOUT);
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_empty_image));
		mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true);

		mSolo.clickOnButton(mSolo.getString(R.string.discard_button_text));
		assertFalse("New drawing warning still found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
		assertNotSame("Bitmap pixel not changed:", Color.BLACK,
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
		mSolo.waitForActivity("AlertActivity", TIMEOUT);
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_empty_image));
		mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true);

		assertTrue("New drawing warning not found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
		assertTrue("New drawing 'yes' button not found",
				mSolo.searchButton(mSolo.getString(R.string.save_button_text), true));
		assertTrue("New drawing 'no' button not found",
				mSolo.searchButton(mSolo.getString(R.string.discard_button_text), true));
		mSolo.goBack();
		assertFalse("New drawing warning still found",
				mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
		assertEquals("Bitmap pixel changed:", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));

	}

	public void testSavedStateChangeAfterSave() throws InterruptedException, SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {

		int xCoord = mScreenWidth / 2;
		int yCoord = mScreenHeight / 4;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnScreen);

		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);
		mSolo.sleep(1000);
		assertFalse(PaintroidApplication.isSaved);

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));

		mSolo.sleep(1000);

		filenames.add(PaintroidApplication.savedPictureUri.toString());
		assertTrue(PaintroidApplication.isSaved);
		mSolo.goBack();
	}

	public void testSaveImage() {
		int xCoord = mScreenWidth / 2;
		int yCoord = mScreenHeight / 2;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnScreen);

		assertNull(PaintroidApplication.savedPictureUri);
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		mSolo.sleep(1000);
		assertNotNull(PaintroidApplication.savedPictureUri);
		mSolo.sleep(500);

		filenames.add(PaintroidApplication.savedPictureUri.toString());
		mSolo.goBack();
	}

	public void testSaveCopy() {
		FileIO.saveBitmap(getActivity(), PaintroidApplication.drawingSurface.getBitmapCopy(), "TempFile");
		File imageFile = getImageFile("TempFile");
		PaintroidApplication.savedPictureUri = Uri.fromFile(imageFile);
		PaintroidApplication.isSaved = true;

		filenames.add(PaintroidApplication.savedPictureUri.toString());

		int xCoord = mScreenWidth / 2;
		int yCoord = mScreenHeight / 2;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnScreen);

		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_copy));
		mSolo.sleep(1000);
		// TOOD: comparing apples and oranges here...
		assertNotSame(imageFile, PaintroidApplication.savedPictureUri);
		mSolo.sleep(500);

		filenames.add(PaintroidApplication.savedPictureUri.toString());
		mSolo.goBack();
	}

	public void testSaveLoadedImage() {
		PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2);

		mSolo.clickOnScreen(point.x, point.y);

		mSolo.sleep(4000);
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		assertTrue("Progress Dialog is not showing", ProgressIntermediateDialog.getInstance().isShowing());
		mSolo.sleep(1000);
		filenames.add(PaintroidApplication.savedPictureUri.toString());
	}

	private File getImageFile(String filename) {
		File imageFile = new File(Environment.getExternalStorageDirectory(), "/"
				+ PaintroidApplication.applicationContext.getString(R.string.app_name) + "/" + filename + ".png");
		return imageFile;
	}
}
