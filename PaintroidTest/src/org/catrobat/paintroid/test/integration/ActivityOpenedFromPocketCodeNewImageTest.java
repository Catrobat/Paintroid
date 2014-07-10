package org.catrobat.paintroid.test.integration;

import java.io.File;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.junit.Test;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Environment;

import com.jayway.android.robotium.solo.Solo;

public class ActivityOpenedFromPocketCodeNewImageTest extends BaseIntegrationTestClass {

	private File imageFile = null;
	private final String imageName = "Look123";

	public ActivityOpenedFromPocketCodeNewImageTest() throws Exception {
		super();
	}

	@Override
	public void setUp() {
		Intent extras = new Intent();

		extras.putExtra("org.catrobat.extra.PAINTROID_PICTURE_PATH", "");
		extras.putExtra("org.catrobat.extra.PAINTROID_PICTURE_NAME", imageName);

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

		imageFile = getImageFile(imageName);

		mSolo.sendKey(Solo.MENU);
		assertTrue("click on Back to Catroid", mSolo.searchText(mSolo.getString(R.string.menu_back)));
		mSolo.clickOnText(mSolo.getString(R.string.menu_back));
		assertTrue("Ok Button not found", mSolo.searchButton(mSolo.getString(R.string.save_button_text)));
		assertTrue("No Button not found", mSolo.searchButton(mSolo.getString(R.string.discard_button_text)));

		mSolo.clickOnButton(mSolo.getString(R.string.save_button_text));

		mSolo.waitForDialogToClose(TIMEOUT);

		assertTrue(imageFile.exists());
		assertTrue(imageFile.length() > 0);
	}

	private File getImageFile(String filename) {
		File imageFile = new File(Environment.getExternalStorageDirectory(), "/"
				+ mSolo.getCurrentActivity().getString(R.string.app_name) + "/" + filename + ".png");
		return imageFile;
	}
}
