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

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
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

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressMenuKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MenuFileActivityIntegrationTest {

	private static ArrayList<File> deletionFileList = null;
	@Rule
	public IntentsTestRule<MainActivity> launchActivityRule = new IntentsTestRule<>(MainActivity.class);

	@ClassRule
	public static GrantPermissionRule grantPermissionRule = EspressoUtils.grantPermissionRulesVersionCheck();

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
				.performOpenMoreOptions();

		onView(withText(R.string.menu_new_image))
				.perform(click());

		onView(withText(R.string.save_button_text))
				.perform(click());

		onView(withId(R.id.pocketpaint_image_name_save_text))
				.perform(replaceText("test987654"));

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
				.performOpenMoreOptions();

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
				.performOpenMoreOptions();

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
		intent.setData(createTestImageFile());
		Instrumentation.ActivityResult resultOK = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
		intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultOK);

		onTopBarView()
				.performOpenMoreOptions();

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
				.performOpenMoreOptions();

		onView(withText(R.string.menu_load_image)).perform(click());

		pressBack();

		onDrawingSurfaceView()
				.check(matches(isDisplayed()));
	}

	@Test
	public void testOnHelpDisabled() {
		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.help_title)).check(matches(not(isClickable())));
	}

	@Test
	public void testWarningDialogOnNewImage() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.performOpenMoreOptions();

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
				.performOpenMoreOptions();

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
				.performOpenMoreOptions();

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
				.performOpenMoreOptions();

		onView(withText(R.string.menu_save_image)).perform(click());
		onView(withId(R.id.pocketpaint_image_name_save_text))
				.perform(replaceText("test98765"));

		onView(withText(R.string.save_button_text)).perform(click());

		assertNotNull(activity.model.getSavedPictureUri());

		addUriToDeletionFileList(activity.model.getSavedPictureUri());

		assertTrue(activity.model.isSaved());
	}

	@Test
	public void testSaveImage() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.performOpenMoreOptions();

		onView(withText(R.string.menu_save_image)).perform(click());
		onView(withText(R.string.save_button_text)).perform(click());

		assertNotNull(activity.model.getSavedPictureUri());

		addUriToDeletionFileList(activity.model.getSavedPictureUri());
	}

	@Test
	public void testSaveCopy() {
		launchActivityRule.getActivity().getPreferences(Context.MODE_PRIVATE)
				.edit()
				.clear()
				.commit();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.performOpenMoreOptions();

		onView(withText(R.string.menu_save_image)).perform(click());

		onView(withText(R.string.save_button_text)).perform(click());

		onView(withText(R.string.pocketpaint_no)).perform(click());
		onView(withText(R.string.pocketpaint_ok)).perform(click());

		assertNotNull(activity.model.getSavedPictureUri());

		addUriToDeletionFileList(activity.model.getSavedPictureUri());

		File oldFile = new File(activity.model.getSavedPictureUri().toString());

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE));

		onTopBarView()
				.performOpenMoreOptions();

		onView(withText(R.string.menu_save_copy)).perform(click());
		onView(withText(R.string.save_button_text)).perform(click());

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
				.performOpenMoreOptions();

		onView(withText(R.string.menu_save_image)).perform(click());
		onView(withText(R.string.save_button_text)).perform(click());

		assertNotNull(activity.model.getSavedPictureUri());
		addUriToDeletionFileList(activity.model.getSavedPictureUri());

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		pressBack();
		onView(withText(R.string.menu_quit)).check(matches(isDisplayed()));
	}

	@Test
	public void testShowOverwriteDialogAfterSavingAgain() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.menu_save_image)).perform(click());

		onView(withId(R.id.pocketpaint_image_name_save_text))
				.perform(replaceText("12345test12345"));

		onView(withText(R.string.save_button_text)).perform(click());

		assertNotNull(activity.model.getSavedPictureUri());
		addUriToDeletionFileList(activity.model.getSavedPictureUri());

		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.menu_save_image))
				.perform(click());

		onView(withText(R.string.save_button_text))
				.perform(click());

		onView(withText(R.string.overwrite_button_text))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testCheckImageNumberIncrementAfterSaveWithStandardName() {
		FileIO.filename = "image";
		int imageNumber = launchActivityRule.getActivity().getPresenter().getImageNumber();

		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.menu_save_image))
				.perform(click());

		onView(withText(R.string.save_button_text))
				.perform(click());

		assertNotNull(activity.model.getSavedPictureUri());
		addUriToDeletionFileList(activity.model.getSavedPictureUri());

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.menu_save_image))
				.perform(click());

		int newImageNumber = launchActivityRule.getActivity().getPresenter().getImageNumber();
		assertEquals(imageNumber + 1, newImageNumber);
	}

	@Test
	public void testCheckImageNumberSameAfterSaveWithNonStandardName() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.menu_save_image))
				.perform(click());

		int imageNumber = launchActivityRule.getActivity().getPresenter().getImageNumber();

		onView(withId(R.id.pocketpaint_image_name_save_text))
				.perform(replaceText("test9876"));

		onView(withText(R.string.save_button_text))
				.perform(click());

		assertNotNull(activity.model.getSavedPictureUri());
		addUriToDeletionFileList(activity.model.getSavedPictureUri());

		int newImageNumber = launchActivityRule.getActivity().getPresenter().getImageNumber();
		assertEquals(imageNumber, newImageNumber);
	}

	@Test
	public void testCheckSaveFileWithDifferentFormats() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.menu_save_image))
				.perform(click());

		onView(withId(R.id.pocketpaint_save_info_title)).check(matches(isDisplayed()));
		onView(withId(R.id.pocketpaint_image_name_save_text)).check(matches(isDisplayed()));
		onView(withId(R.id.pocketpaint_save_dialog_spinner)).check(matches(isDisplayed()));

		onView(withId(R.id.pocketpaint_save_dialog_spinner))
				.perform(click());
		onData(allOf(is(instanceOf(String.class)),
				is("png"))).inRoot(isPlatformPopup()).perform(click());
		onView(withId(R.id.pocketpaint_image_name_save_text))
				.perform(replaceText(Constants.TEMP_PICTURE_NAME));

		onView(withText(R.string.save_button_text))
				.perform(click());

		assertNotNull(activity.model.getSavedPictureUri());
		addUriToDeletionFileList(activity.model.getSavedPictureUri());

		File oldFile = new File(activity.model.getSavedPictureUri().toString());

		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.menu_save_image))
				.perform(click());

		onView(withId(R.id.pocketpaint_save_dialog_spinner))
				.perform(click());
		onData(allOf(is(instanceOf(String.class)),
				is("jpg"))).inRoot(isPlatformPopup()).perform(click());
		onView(withId(R.id.pocketpaint_image_name_save_text))
				.perform(replaceText(Constants.TEMP_PICTURE_NAME));

		assertNotNull(activity.model.getSavedPictureUri());
		addUriToDeletionFileList(activity.model.getSavedPictureUri());

		File newFile = new File(activity.model.getSavedPictureUri().toString());

		assertNotSame(oldFile, newFile);
	}

	@Test
	public void testCheckSaveImageDialogShowsSavedImageOptions() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.menu_save_image))
				.perform(click());

		String imageName = "test12345";

		onView(withId(R.id.pocketpaint_image_name_save_text))
				.perform(replaceText(imageName));
		onView(withId(R.id.pocketpaint_save_dialog_spinner))
				.perform(click());
		onData(allOf(is(instanceOf(String.class)),
				is("png"))).inRoot(isPlatformPopup()).perform(click());

		onView(withText(R.string.save_button_text))
				.perform(click());

		assertNotNull(activity.model.getSavedPictureUri());
		addUriToDeletionFileList(activity.model.getSavedPictureUri());

		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.menu_save_image))
				.perform(click());

		onView(withText(imageName))
				.check(matches(isDisplayed()));
		onView(withText("png"))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testCheckCopyIsAlwaysDefaultOptions() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.menu_save_copy))
				.perform(click());

		int imageNumber = launchActivityRule.getActivity().getPresenter().getImageNumber();

		onView(withText("jpg"))
				.check(matches(isDisplayed()));
		onView(withText("image" + imageNumber))
				.check(matches(isDisplayed()));

		onView(withId(R.id.pocketpaint_save_dialog_spinner))
				.perform(click());
		onData(allOf(is(instanceOf(String.class)),
				is("jpg"))).inRoot(isPlatformPopup()).perform(click());

		onView(withText(R.string.save_button_text))
				.perform(click());

		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.menu_save_copy))
				.perform(click());

		imageNumber = launchActivityRule.getActivity().getPresenter().getImageNumber();

		onView(withText("jpg"))
				.check(matches(isDisplayed()));
		onView(withText("image" + imageNumber))
				.check(matches(isDisplayed()));
	}

	private Uri createTestImageFile() {
		Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);

		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "testfile.jpg");
		contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
		}

		ContentResolver resolver = activity.getContentResolver();
		Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
		try {
			OutputStream fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
			assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos));
			assert fos != null;
			fos.close();
		} catch (IOException e) {
			throw new AssertionError("Picture file could not be created.", e);
		}

		File imageFile = new File(imageUri.getPath(), "testfile.jpg");

		deletionFileList.add(imageFile);
		return imageUri;
	}

	@Test
	public void testLoadImageTransparency() {
		Intent intent = new Intent();
		intent.setData(createTestImageFile());
		Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
		intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result);

		onTopBarView()
				.performOpenMoreOptions();

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
