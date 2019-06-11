/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.paintroid.test.espresso;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressMenuKey;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MenuFileActivityIntegrationTest {

	private static ArrayList<File> deletionFileList = null;
	@Rule
	public IntentsTestRule<MainActivity> launchActivityRule = new IntentsTestRule<>(MainActivity.class);

	@ClassRule
	public static GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE);

	private MainActivity activity;

	@Before
	public void setUp() {
		onToolBarView().performSelectTool(ToolType.BRUSH);
		deletionFileList = new ArrayList<>();
		activity = launchActivityRule.getActivity();
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
	public void testNewEmptyDrawingWithSave() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_new_image))
				.perform(click());

		onView(withText(R.string.save_button_text))
				.perform(click());

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testLoadImageDialog() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_load_image))
				.perform(click());

		onView(withText(R.string.menu_load_image))
				.check(matches(isDisplayed()));
		onView(withText(R.string.dialog_warning_new_image))
				.check(matches(isDisplayed()));
		onView(withText(R.string.save_button_text))
				.check(matches(isDisplayed()));
		onView(withText(R.string.discard_button_text))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testLoadImageDialogIntentCancel() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		Instrumentation.ActivityResult resultCancel = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, new Intent());
		intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultCancel);

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_load_image)).perform(click());
		onView(withText(R.string.discard_button_text)).perform(click());
		onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist());

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testLoadImageDialogIntentOK() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));
		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

		Intent intent = new Intent();
		intent.setData(Uri.fromFile(createTestImageFile()));
		Instrumentation.ActivityResult resultOK = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
		intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultOK);

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_load_image)).perform(click());
		onView(withText(R.string.discard_button_text)).perform(click());
		onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist());

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testLoadImageDialogOnBackPressed() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_load_image)).perform(click());

		pressBack();

		onDrawingSurfaceView()
				.check(matches(isDisplayed()));
	}

	@Test
	public void testOnHelp() {
		onTopBarView()
				.onMoreOptionsClicked();
		onView(withText(R.string.help_title)).perform(click());
		intended(hasComponent(hasClassName(WelcomeActivity.class.getName())));
	}

	@Test
	public void testImageUnchangedAfterHelpSkip() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		Bitmap imageBefore = activity.layerModel.getCurrentLayer().getBitmap();
		imageBefore = imageBefore.copy(imageBefore.getConfig(), imageBefore.isMutable());

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.help_title)).perform(click());
		intended(hasComponent(hasClassName(WelcomeActivity.class.getName())));
		onView(withText(R.string.skip)).perform(click());

		Bitmap imageAfter = activity.layerModel.getCurrentLayer().getBitmap();
		assertTrue("Image should not have changed", imageBefore.sameAs(imageAfter));
	}

	@Test
	public void testImageUnchangedAfterHelpAbort() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		Bitmap imageBefore = activity.layerModel.getCurrentLayer().getBitmap();
		imageBefore = imageBefore.copy(imageBefore.getConfig(), imageBefore.isMutable());

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.help_title)).perform(click());
		intended(hasComponent(hasClassName(WelcomeActivity.class.getName())));
		pressBack();

		Bitmap imageAfter = activity.layerModel.getCurrentLayer().getBitmap();
		assertTrue("Image should not have changed", imageBefore.sameAs(imageAfter));
	}

	@Test
	public void testWarningDialogOnNewImage() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_new_image)).perform(click());

		onView(withText(R.string.dialog_warning_new_image)).check(matches(isDisplayed()));
		onView(withText(R.string.save_button_text)).check(matches(isDisplayed()));
		onView(withText(R.string.discard_button_text)).check(matches(isDisplayed()));

		pressBack();

		onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist());
	}

	@Test
	public void testNewEmptyDrawingWithDiscard() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_new_image))
				.perform(click());

		onView(withText(R.string.discard_button_text))
				.perform(click());

		onView(withText(R.string.dialog_warning_new_image))
				.check(doesNotExist());

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testNewEmptyDrawingDialogOnBackPressed() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_new_image)).perform(click());

		onView(withText(R.string.dialog_warning_new_image)).check(matches(isDisplayed()));
		onView(withText(R.string.save_button_text)).check(matches(isDisplayed()));
		onView(withText(R.string.discard_button_text)).check(matches(isDisplayed()));

		pressBack();

		onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist());

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testSavedStateChangeAfterSave() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		assertFalse(activity.model.isSaved());

		pressMenuKey();

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_save_image)).perform(click());

		assertNotNull(activity.model.getSavedPictureUri());

		addUriToDeletionFileList(activity.model.getSavedPictureUri());

		assertTrue(activity.model.isSaved());
	}

	@Test
	public void testSaveImage() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_save_image)).perform(click());

		assertNotNull(activity.model.getSavedPictureUri());

		addUriToDeletionFileList(activity.model.getSavedPictureUri());
	}

	@Test
	public void testSaveCopy() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_save_image)).perform(click());

		assertNotNull(activity.model.getSavedPictureUri());

		addUriToDeletionFileList(activity.model.getSavedPictureUri());

		File oldFile = new File(activity.model.getSavedPictureUri().toString());

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE));

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_save_copy)).perform(click());

		File newFile = new File(activity.model.getSavedPictureUri().toString());

		assertNotSame("Changes to saved", oldFile, newFile);

		assertNotNull(activity.model.getSavedPictureUri());

		addUriToDeletionFileList(activity.model.getSavedPictureUri());
	}

	@Test
	public void testAskForSaveAfterSavedOnce() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_save_image)).perform(click());
		assertNotNull(activity.model.getSavedPictureUri());
		addUriToDeletionFileList(activity.model.getSavedPictureUri());

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		pressBack();
		onView(withText(R.string.menu_quit)).check(matches(isDisplayed()));
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private File createTestImageFile() {
		File imageFile = new File(Environment.getExternalStorageDirectory()
				+ "/PocketCodePaintTest/", "testfile.jpg");
		Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
		try {
			imageFile.getParentFile().mkdirs();
			imageFile.createNewFile();
			OutputStream outputStream = new FileOutputStream(imageFile);
			assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream));
			outputStream.close();
		} catch (IOException e) {
			throw new AssertionError("Picture file could not be created.", e);
		}
		deletionFileList.add(imageFile);
		return imageFile;
	}

	@Test
	public void testLoadImageTransparency() {
		Intent intent = new Intent();
		intent.setData(Uri.fromFile(createTestImageFile()));
		Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
		intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result);

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_load_image))
				.perform(click());

		onToolBarView()
				.performSelectTool(ToolType.ERASER);

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);
	}

	private void addUriToDeletionFileList(Uri uri) {
		deletionFileList.add(new File(Objects.requireNonNull(uri.getPath())));
	}
}
