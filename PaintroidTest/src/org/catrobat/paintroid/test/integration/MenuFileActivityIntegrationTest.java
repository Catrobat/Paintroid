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

import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Environment;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.ui.DrawingSurface;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

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
        mSolo.sleep(SHORT_SLEEP);

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
		mSolo.sleep(SHORT_TIMEOUT);
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

		PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 4);
		// PointF canvasPoint = Utils.getCanvasPointFromScreenPoint(screenPoint);

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);
		assertFalse(PaintroidApplication.isSaved);

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));

		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.waitForDialogToClose(TIMEOUT);

		filenames.add(PaintroidApplication.savedPictureUri.toString());
		assertTrue(PaintroidApplication.isSaved);

		mSolo.goBack();
	}

	public void testSaveImage() {
		PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);

		assertNull(PaintroidApplication.savedPictureUri);
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.waitForDialogToClose(TIMEOUT);
        mSolo.waitForActivity("MainActivity");
		assertNotNull(PaintroidApplication.savedPictureUri);

		filenames.add(PaintroidApplication.savedPictureUri.toString());
		mSolo.goBack();
	}

	public void testSaveCopy() {
		PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);

		assertNull(PaintroidApplication.savedPictureUri);
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.waitForDialogToClose(TIMEOUT);
		assertNotNull(PaintroidApplication.savedPictureUri);
		filenames.add(PaintroidApplication.savedPictureUri.toString());
		Uri oldUri = PaintroidApplication.savedPictureUri;

		mSolo.clickOnScreen(screenPoint.x + 20, screenPoint.y + 20);

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_copy));

		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.waitForDialogToClose(TIMEOUT);
		// TOOD: comparing apples and oranges here...
		assertNotSame(oldUri, PaintroidApplication.savedPictureUri);
		filenames.add(PaintroidApplication.savedPictureUri.toString());

		mSolo.goBack();
	}

	public void testSaveLoadedImage() throws URISyntaxException, IOException {
        File tmpFile = getImageFile("tmpFile");
        if(!tmpFile.exists()) {
            tmpFile.createNewFile();
        }

		PaintroidApplication.savedPictureUri = Uri.fromFile(new File("tmpFile"));
        PaintroidApplication.isSaved = true;
        assertNotNull(PaintroidApplication.savedPictureUri);

        filenames.add(PaintroidApplication.savedPictureUri.toString());
        long oldlength = tmpFile.length();
        long firstmodified = tmpFile.lastModified();

        PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);

        mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
        mSolo.sleep(SHORT_SLEEP);

        mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
        mSolo.waitForDialogToClose();

        long newlength = tmpFile.length();
        long lastmodified = tmpFile.lastModified();
        assertNotSame("File is still the same", oldlength, newlength);
        assertNotSame("File not currently moified", firstmodified, lastmodified);
	}

	private File getImageFile(String filename) {
		File imageFile = new File(Environment.getExternalStorageDirectory(), "/"
				+ PaintroidApplication.applicationContext.getString(R.string.app_name) + "/" + filename + ".png");
		return imageFile;
	}
}
