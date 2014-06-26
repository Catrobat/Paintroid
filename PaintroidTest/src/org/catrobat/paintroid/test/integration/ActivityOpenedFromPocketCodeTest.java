package org.catrobat.paintroid.test.integration;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.PointF;
import android.os.Environment;

import com.jayway.android.robotium.solo.Solo;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ActivityOpenedFromPocketCodeTest extends BaseIntegrationTestClass {

	private File imageFile = null;

	public ActivityOpenedFromPocketCodeTest() throws Exception {
		super();
	}

	@Override
	public void setUp() {
		Intent extras = new Intent();

		imageFile = createImageFile("testFile");

		extras.putExtra("org.catrobat.extra.PAINTROID_PICTURE_PATH", imageFile.getAbsolutePath());
		setActivityIntent(extras);
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		PaintroidApplication.savedPictureUri = null;
		PaintroidApplication.isSaved = false;
		if (imageFile != null) {
			imageFile.delete();
		}
		super.tearDown();
	}

	@Test
	public void testSave() {
		PointF pointOnScreen = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);

		mSolo.sendKey(Solo.MENU);
		assertTrue("click on Back to Catroid", mSolo.searchText(mSolo.getString(R.string.menu_back)));
		mSolo.clickOnText(mSolo.getString(R.string.menu_back));
		assertTrue("Ok Button not found", mSolo.searchButton(mSolo.getString(R.string.save_button_text)));
		assertTrue("No Button not found", mSolo.searchButton(mSolo.getString(R.string.discard_button_text)));

		long lastModifiedBefore = imageFile.lastModified();
		long fileSizeBefore = imageFile.length();
		mSolo.clickOnButton(mSolo.getString(R.string.save_button_text));

		mSolo.waitForDialogToClose(TIMEOUT);

		assertEquals(PaintroidApplication.catroidPicturePath, imageFile.getAbsolutePath());
		assertTrue(imageFile.lastModified() > lastModifiedBefore);
		assertTrue(imageFile.length() > fileSizeBefore);
	}

	@Test
	public void testExportNotTouchingOriginal() {
        PointF pointOnScreen = new PointF(mScreenWidth / 2, mScreenHeight / 2);
        mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);
        mSolo.sleep(100);

        long lastModifiedBefore = imageFile.lastModified();
        long fileSizeBefore = imageFile.length();

        mSolo.sendKey(Solo.MENU);
        assertTrue("click on export", mSolo.searchText(mSolo.getString(R.string.menu_export)));
        mSolo.clickOnText(mSolo.getString(R.string.menu_export));

        mSolo.waitForDialogToClose(TIMEOUT);

        assertEquals(imageFile.lastModified(), lastModifiedBefore);
        assertEquals(imageFile.length(), fileSizeBefore);
	}

	@Test
	public void testBackToPocketCode() {
		PointF pointOnScreen = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);

		mSolo.sendKey(Solo.MENU);
		assertTrue("click on Back to Catroid", mSolo.searchText(mSolo.getString(R.string.menu_back)));
		mSolo.clickOnText(mSolo.getString(R.string.menu_back));
		assertTrue("Ok Button not found", mSolo.searchButton(mSolo.getString(R.string.save_button_text)));
		assertTrue("No Button not found", mSolo.searchButton(mSolo.getString(R.string.discard_button_text)));

		long lastModifiedBefore = imageFile.lastModified();
		long fileSizeBefore = imageFile.length();

		mSolo.clickOnButton(mSolo.getString(R.string.discard_button_text));

		mSolo.waitForDialogToClose(TIMEOUT);

		assertEquals(imageFile.lastModified(), lastModifiedBefore);
		assertEquals(imageFile.length(), fileSizeBefore);

	}

	private File createImageFile(String filename) {
		Bitmap bitmap = Bitmap.createBitmap(480, 800, Config.ARGB_8888);
		File pictureFile = getImageFile(filename);
		try {
			pictureFile.getParentFile().mkdirs();
			pictureFile.createNewFile();
			OutputStream outputStream = new FileOutputStream(pictureFile);
			assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream));
			outputStream.close();
		} catch (IOException e) {
			fail("Picture file could not be created.");
		}

		return pictureFile;
	}

	private File getImageFile(String filename) {
		File imageFile = new File(Environment.getExternalStorageDirectory() + "/PocketCodePaintTest/", filename
				+ ".png");
		return imageFile;
	}
}
