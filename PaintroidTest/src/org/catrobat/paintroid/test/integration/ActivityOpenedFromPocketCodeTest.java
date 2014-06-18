package org.catrobat.paintroid.test.integration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.junit.Test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.PointF;
import android.os.Environment;

import com.jayway.android.robotium.solo.Solo;

public class ActivityOpenedFromPocketCodeTest extends BaseIntegrationTestClass {

	// TODO: refactor
	private static Vector<String> FILENAMES = null;

	public ActivityOpenedFromPocketCodeTest() throws Exception {
		super();
	}

	@Override
	public void setUp() {

		FILENAMES = new Vector<String>();
		Intent extras = new Intent();

		File imageFile = createImageFile("testFile");

		// TODO: 2nd test class for empty path
		extras.putExtra("org.catrobat.extra.PAINTROID_PICTURE_PATH", imageFile.getAbsolutePath());
		setActivityIntent(extras);
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		PaintroidApplication.savedPictureUri = null;
		PaintroidApplication.isSaved = false;
		for (String filename : FILENAMES) {
			if (filename != null && filename.length() > 0) {
				getImageFile(filename).delete();
			}
		}
		super.tearDown();
	}

	@Test
	public void testSave() {

	}

	@Test
	public void testSaveCopy() {

	}

	@Test
	public void testBackToPocketCode() {

		int xCoord = mScreenWidth / 2;
		int yCoord = mScreenHeight / 2;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnScreen);

		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);

		mSolo.sendKey(Solo.MENU);
		assertTrue("click on Back to Catroid", mSolo.searchText(mSolo.getString(R.string.menu_back)));
		mSolo.clickOnText(mSolo.getString(R.string.menu_back));
		assertTrue("Ok Button not found", mSolo.searchButton(mSolo.getString(R.string.save_button_text)));
		assertTrue("No Button not found", mSolo.searchButton(mSolo.getString(R.string.discard_button_text)));

		mSolo.clickOnButton(mSolo.getString(R.string.discard_button_text));

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
