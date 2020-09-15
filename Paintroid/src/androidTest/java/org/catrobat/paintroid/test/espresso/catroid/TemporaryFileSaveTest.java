package org.catrobat.paintroid.test.espresso.catroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class TemporaryFileSaveTest {

	@Rule
	public IntentsTestRule<MainActivity> launchActivityRule = new IntentsTestRule<>(MainActivity.class, false, true);

	private static File imageFile = null;

	@Before
	public void setUp() {
		imageFile = createImageFile();
		launchActivityRule.finishActivity();

		Intent extras = new Intent();
		launchActivityRule.launchActivity(extras);
	}

	@After
	public void tearDown() {
		if (imageFile != null && imageFile.exists()) {
			assertTrue(imageFile.delete());
		}
	}

	@Test
	public void testTemporaryIsDisplayed() {
		onView(withText(R.string.pocketpaint_temporary_file_dialog))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testTemporaryLoadYes() {
		onView(withText(R.string.pocketpaint_yes))
				.perform(click());

		onDrawingSurfaceView()
				.checkPixelColor(Color.GREEN, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testTemporaryLoadNo() {
		onView(withText(R.string.pocketpaint_no))
				.perform(click());

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);
	}

	private File createImageFile() {
		Bitmap bitmap = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(Color.GREEN);
		File pictureFile = getImageFile();

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

	private File getImageFile() {
		return new File(launchActivityRule.getActivity().getFilesDir() + "/"
				+ Constants.TEMP_PICTURE_DIRECTORY_NAME + "/", Constants.TEMP_DIRECTORY_PICTURE_NAME + ".png");
	}
}
