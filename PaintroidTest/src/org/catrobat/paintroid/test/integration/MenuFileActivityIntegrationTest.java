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

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.ui.DrawingSurface;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

public class MenuFileActivityIntegrationTest extends BaseIntegrationTestClass {

	private static Vector<String> filenames = null;
    private PointF screenPoint = null;

	public MenuFileActivityIntegrationTest() throws Exception {
		super();
	}

	@Override
	public void setUp() {
		super.setUp();
        screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
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

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
        mSolo.sleep(SHORT_SLEEP);

		mCurrentDrawingSurfaceBitmap.setPixel(xCoordinatePixel, yCoordinatePixel, Color.BLACK);

		assertEquals("Color on drawing surface wrong", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForDialogToOpen();
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_empty_image));
        mSolo.waitForDialogToOpen();
		mSolo.clickOnButton(mSolo.getString(R.string.save_button_text));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		int bitmapPixelColor = PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel,
				yCoordinatePixel));
		assertEquals("Color should be Transbarent", Color.TRANSPARENT, bitmapPixelColor);
	}

	public void testLoadImageDialog() {
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
        mSolo.sleep(SHORT_SLEEP);

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

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_load_image));
		mSolo.waitForDialogToOpen();
		mSolo.goBack();
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

	}

	public void testWarningDialogOnNewImageFromCamera() {

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
        mSolo.sleep(SHORT_SLEEP);

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.sleep(SHORT_TIMEOUT);
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_from_camera));

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
		final int xCoordinatePixel = 0;
		final int yCoordinatePixel = 0;

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForDialogToOpen();
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_empty_image));

        mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true);

		mSolo.clickOnButton(mSolo.getString(R.string.discard_button_text));
        mSolo.waitForDialogToClose();

		assertFalse("New drawing warning still found",
                mSolo.searchText(mSolo.getString(R.string.dialog_warning_new_image), 1, true, true));
		assertNotSame("Bitmap pixel not changed:", Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(Utils.getCanvasPointFromScreenPoint(new PointF(mScreenWidth / 2, mScreenHeight / 2))));
	}

	public void testNewEmptyDrawingDialogOnBackPressed() {
		final int xCoordinatePixel = 0;
		final int yCoordinatePixel = 0;

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForDialogToOpen();
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_empty_image));

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
		mSolo.sleep(SHORT_SLEEP);

		assertFalse(PaintroidApplication.isSaved);
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));

		assertTrue("ProgressDialog not showing", mSolo.waitForDialogToOpen(SHORT_TIMEOUT));
		mSolo.waitForDialogToClose();

		filenames.add(PaintroidApplication.savedPictureUri.toString());
		assertTrue(PaintroidApplication.isSaved);

	}

	public void testSaveImage() {
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.waitForDialogToClose();
        assertEquals("current Activity not MainActivity", MainActivity.class, mSolo.getCurrentActivity().getClass());


		assertNotNull(PaintroidApplication.savedPictureUri);
		filenames.add(PaintroidApplication.savedPictureUri.toString());
	}

	public void testSaveCopy() {


		assertNull(PaintroidApplication.savedPictureUri);
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_image));
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.waitForDialogToClose();
		assertNotNull(PaintroidApplication.savedPictureUri);
		filenames.add(PaintroidApplication.savedPictureUri.toString());
		File oldFile = new File(PaintroidApplication.savedPictureUri.toString());

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y + 100);
        mSolo.sleep(SHORT_SLEEP);

        mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_save_copy));

		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.waitForDialogToClose();

        File newFile = new File(PaintroidApplication.savedPictureUri.toString());
		assertNotSame(oldFile, newFile);
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
