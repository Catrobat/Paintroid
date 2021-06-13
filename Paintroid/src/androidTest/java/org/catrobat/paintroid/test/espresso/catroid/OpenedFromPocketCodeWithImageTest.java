/*
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

package org.catrobat.paintroid.test.espresso.catroid;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import id.zelory.compressor.Compressor;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class OpenedFromPocketCodeWithImageTest {

	private static final String IMAGE_NAME = "testFile";
	private static final String IMAGE_TO_LOAD_NAME = "loadFile";

	@Rule
	public IntentsTestRule<MainActivity> launchActivityRule = new IntentsTestRule<>(MainActivity.class, false, true);

	@Rule
	public ScreenshotOnFailRule screenshotOnFailRule = new ScreenshotOnFailRule();

	@ClassRule
	public static GrantPermissionRule grantPermissionRule = EspressoUtils.grantPermissionRulesVersionCheck();

	private File imageFile = null;
	private MainActivity activity;
	private ArrayList<File> deletionFileList = null;

	@Before
	public void setUp() {
		deletionFileList = new ArrayList<>();
		activity = launchActivityRule.getActivity();

		imageFile = getNewImageFile(IMAGE_NAME);
		deletionFileList.add(imageFile);
		launchActivityRule.getActivity().model.setSavedPictureUri(Uri.fromFile(imageFile));
		launchActivityRule.getActivity().model.setOpenedFromCatroid(true);

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
	}

	@After
	public void tearDown() {
		for (File file : deletionFileList) {
			if (file != null && file.exists()) {
				assertTrue(file.delete());
			}
		}
	}

	@Test
	public void testSave() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		long lastModifiedBefore = imageFile.lastModified();
		long fileSizeBefore = imageFile.length();

		Espresso.pressBackUnconditionally();
		verifyImageFile(lastModifiedBefore, fileSizeBefore);
	}

	@Test
	public void testLoadWithoutChange() {
		long lastModifiedBefore = imageFile.lastModified();
		long fileSizeBefore = imageFile.length();
		createImageIntent();

		onTopBarView()
				.performOpenMoreOptions();

		onView(withText(R.string.menu_load_image)).perform(click());
		onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist());

		onDrawingSurfaceView()
				.checkPixelColor(Color.WHITE, BitmapLocationProvider.MIDDLE);

		Espresso.pressBackUnconditionally();
		verifyImageFile(lastModifiedBefore, fileSizeBefore);
	}

	@Test
	public void testLoadWithChange() {
		long lastModifiedBefore = imageFile.lastModified();
		long fileSizeBefore = imageFile.length();
		createImageIntent();

		onTopBarView()
				.performOpenMoreOptions();

		onView(withText(R.string.menu_load_image)).perform(click());
		onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist());

		onDrawingSurfaceView()
				.checkPixelColor(Color.WHITE, BitmapLocationProvider.MIDDLE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		Espresso.pressBackUnconditionally();
		verifyImageFile(lastModifiedBefore, fileSizeBefore);
	}

	@Test
	public void testBackToPocketCode() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		Espresso.pressBackUnconditionally();

		long lastModifiedBefore = imageFile.lastModified();
		long fileSizeBefore = imageFile.length();

		assertThat("Image modified", imageFile.lastModified(), equalTo(lastModifiedBefore));
		assertThat("Saved image length changed", imageFile.length(), equalTo(fileSizeBefore));
	}

	private File getNewImageFile(String filename) {
		try {
			return FileIO.createNewEmptyPictureFile(filename, launchActivityRule.getActivity());
		} catch (NullPointerException e) {
			throw new AssertionError("Could not create temp file", e);
		}
	}

	private void createImageIntent() {
		Intent intent = new Intent();
		intent.setData(createTestImageFile());
		Instrumentation.ActivityResult resultOK = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
		intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultOK);
	}

	private Uri createTestImageFile() {
		Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(bitmap, 0F, 0F, null);

		File uncompressedImageFile = new File(activity.getExternalFilesDir(null).getAbsolutePath(), "uncompressed_" + IMAGE_NAME + ".jpg");

		try {
			Uri uncompressedImageUri = Uri.fromFile(uncompressedImageFile);
			OutputStream fos = activity.getContentResolver().openOutputStream(Objects.requireNonNull(uncompressedImageUri));
			assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos));
			assert fos != null;
			fos.close();
		} catch (IOException e) {
			throw new AssertionError("Picture file could not be created.", e);
		}
		Compressor compressor = new Compressor(launchActivityRule.getActivity());
		compressor.setCompressFormat(Bitmap.CompressFormat.JPEG);
		compressor.setQuality(100);
		compressor.setDestinationDirectoryPath(activity.getExternalFilesDir(null).getAbsolutePath() + "/Pictures");
		deletionFileList.add(uncompressedImageFile);
		try {
			imageFile = compressor.compressToFile(uncompressedImageFile, IMAGE_NAME + ".png");

		} catch (IOException e) {
			throw new AssertionError("Test Picture file could not be created.", e);
		}

		deletionFileList.add(imageFile);
		return Uri.fromFile(imageFile);
	}

	private void verifyImageFile(long lastModifiedBefore, long fileSizeBefore) {
		String path = launchActivityRule.getActivityResult().getResultData().getStringExtra(Constants.PAINTROID_PICTURE_PATH);
		assertEquals(imageFile.getAbsolutePath(), path);

		assertThat("Image modification not saved", imageFile.lastModified(), greaterThan(lastModifiedBefore));
		assertThat("Saved image length not changed", imageFile.length(), greaterThan(fileSizeBefore));
	}
}
