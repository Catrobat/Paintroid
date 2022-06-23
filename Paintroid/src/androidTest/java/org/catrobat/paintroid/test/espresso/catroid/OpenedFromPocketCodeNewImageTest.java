/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

import static org.catrobat.paintroid.common.ConstantsKt.PAINTROID_PICTURE_NAME;
import static org.catrobat.paintroid.common.ConstantsKt.PAINTROID_PICTURE_PATH;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
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
public class OpenedFromPocketCodeNewImageTest {

	private static final String IMAGE_NAME = "testFile";
	private static final String IMAGE_TO_LOAD_NAME = "loadFile";

	@Rule
	public IntentsTestRule<MainActivity> launchActivityRule = new IntentsTestRule<>(MainActivity.class, false, false);

	@Rule
	public ScreenshotOnFailRule screenshotOnFailRule = new ScreenshotOnFailRule();

	@ClassRule
	public static GrantPermissionRule grantPermissionRule = EspressoUtils.INSTANCE.grantPermissionRulesVersionCheck();

	private File imageFile = null;
	private MainActivity activity;
	private ArrayList<File> deletionFileList = null;

	@Before
	public void setUp() {
		Intent intent = new Intent();
		intent.putExtra(PAINTROID_PICTURE_PATH, "");
		intent.putExtra(PAINTROID_PICTURE_NAME, IMAGE_NAME);

		launchActivityRule.launchActivity(intent);
		deletionFileList = new ArrayList<>();
		activity = launchActivityRule.getActivity();
		imageFile = getNewImageFile(IMAGE_NAME);
		deletionFileList.add(imageFile);

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

		Espresso.pressBackUnconditionally();
		verifyImageFile();
	}

	@Test
	public void testLoadWithoutChange() {
		createImageIntent();

		onTopBarView()
				.performOpenMoreOptions();

		onView(withText(R.string.menu_load_image)).perform(click());
		onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist());

		onDrawingSurfaceView()
				.checkPixelColor(Color.WHITE, BitmapLocationProvider.MIDDLE);

		Espresso.pressBackUnconditionally();
		verifyImageFile();
	}

	@Test
	public void testLoadWithChange() {
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
		verifyImageFile();
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

		File imageFile = new File(activity.getExternalFilesDir(null).getAbsolutePath(), IMAGE_TO_LOAD_NAME + ".jpg");
		Uri imageUri = Uri.fromFile(imageFile);
		try {
			OutputStream fos = activity.getContentResolver().openOutputStream(Objects.requireNonNull(imageUri));
			assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos));
			assert fos != null;
			fos.close();
		} catch (IOException e) {
			throw new AssertionError("Picture file could not be created.", e);
		}

		deletionFileList.add(imageFile);
		return imageUri;
	}

	private void verifyImageFile() {
		String path = launchActivityRule.getActivityResult().getResultData().getStringExtra(PAINTROID_PICTURE_PATH);
		assertEquals(imageFile.getAbsolutePath(), path);

		assertTrue(imageFile.exists());
		assertThat(imageFile.length(), greaterThan(0L));
	}
}
