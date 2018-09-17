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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Environment;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getWorkingBitmap;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetColorPicker;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.NavigationDrawerInteraction.onNavigationDrawer;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class MenuFileActivityIntegrationTest {

	private static ArrayList<File> deletionFileList = null;
	@Rule
	public IntentsTestRule<MainActivity> launchActivityRule = new IntentsTestRule<>(MainActivity.class);

	@Before
	public void setUp() {
		onToolBarView().performSelectTool(ToolType.BRUSH);
		deletionFileList = new ArrayList<>();
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
		final int xCoordinatePixel = 0;
		final int yCoordinatePixel = 0;

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		getWorkingBitmap().setPixel(xCoordinatePixel, yCoordinatePixel, Color.BLACK);
		assertEquals("Color on drawing surface wrong", Color.BLACK, PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_new_image)).perform(click());

		onView(withText(R.string.save_button_text)).perform(click());

		onView(withText(R.string.menu_new_image_empty_image)).perform(click());

		assertEquals("Color should be Transparent", Color.TRANSPARENT, PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));
	}

	@Test
	public void testLoadImageDialog() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_load_image)).perform(click());

		onView(withText(R.string.menu_load_image)).check(matches(isDisplayed()));
		onView(withText(R.string.dialog_warning_new_image)).check(matches(isDisplayed()));
		onView(withText(R.string.save_button_text)).check(matches(isDisplayed()));
		onView(withText(R.string.discard_button_text)).check(matches(isDisplayed()));
	}

	@Test
	public void testLoadImageDialogIntentCancel() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		Instrumentation.ActivityResult resultCancel = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, new Intent());
		intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultCancel);

		onNavigationDrawer()
				.performOpen();

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

		onNavigationDrawer()
				.performOpen();

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

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_load_image)).perform(click());

		pressBack();

		onView(withId(R.id.pocketpaint_drawing_surface_view)).check(matches(isDisplayed()));
	}

	@Test
	public void testOnHelp() {
		onNavigationDrawer()
				.performOpen();
		onView(withText(R.string.help_title)).perform(click());
		intended(hasComponent(hasClassName(WelcomeActivity.class.getName())));
	}

	@Test
	public void testImageUnchangedAfterHelpSkip() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		Bitmap imageBefore = PaintroidApplication.layerModel.getCurrentLayer().getBitmap();
		imageBefore = imageBefore.copy(imageBefore.getConfig(), imageBefore.isMutable());

		onNavigationDrawer()
				.performOpen();
		onView(withText(R.string.help_title)).perform(click());
		intended(hasComponent(hasClassName(WelcomeActivity.class.getName())));
		onView(withText(R.string.skip)).perform(click());

		Bitmap imageAfter = PaintroidApplication.layerModel.getCurrentLayer().getBitmap();
		assertTrue("Image should not have changed", imageBefore.sameAs(imageAfter));
	}

	@Test
	public void testImageUnchangedAfterHelpAbort() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		Bitmap imageBefore = PaintroidApplication.layerModel.getCurrentLayer().getBitmap();
		imageBefore = imageBefore.copy(imageBefore.getConfig(), imageBefore.isMutable());

		onNavigationDrawer()
				.performOpen();
		onView(withText(R.string.help_title)).perform(click());
		intended(hasComponent(hasClassName(WelcomeActivity.class.getName())));
		pressBack();

		Bitmap imageAfter = PaintroidApplication.layerModel.getCurrentLayer().getBitmap();
		assertTrue("Image should not have changed", imageBefore.sameAs(imageAfter));
	}

	@Test
	public void testWarningDialogOnNewImage() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onNavigationDrawer()
				.performOpen();

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

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_new_image)).perform(click());

		onView(withText(R.string.discard_button_text)).perform(click());

		onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist());

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testNewEmptyDrawingDialogOnBackPressed() {
		resetColorPicker();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onNavigationDrawer()
				.performOpen();

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

		assertFalse(launchActivityRule.getActivity().model.isSaved());

		pressMenuKey();

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_save_image)).perform(click());

		assertNotNull(launchActivityRule.getActivity().model.getSavedPictureUri());

		addUriToDeletionFileList(launchActivityRule.getActivity().model.getSavedPictureUri());

		assertTrue(launchActivityRule.getActivity().model.isSaved());
	}

	@Test
	public void testSaveImage() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_save_image)).perform(click());

		assertNotNull(launchActivityRule.getActivity().model.getSavedPictureUri());

		addUriToDeletionFileList(launchActivityRule.getActivity().model.getSavedPictureUri());
	}

	@Test
	public void testSaveCopy() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_save_image)).perform(click());

		assertNotNull(launchActivityRule.getActivity().model.getSavedPictureUri());

		addUriToDeletionFileList(launchActivityRule.getActivity().model.getSavedPictureUri());

		File oldFile = new File(launchActivityRule.getActivity().model.getSavedPictureUri().toString());

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE));

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_save_copy)).perform(click());

		File newFile = new File(launchActivityRule.getActivity().model.getSavedPictureUri().toString());

		assertNotSame("Changes to saved", oldFile, newFile);

		assertNotNull(launchActivityRule.getActivity().model.getSavedPictureUri());

		addUriToDeletionFileList(launchActivityRule.getActivity().model.getSavedPictureUri());
	}

	@Test
	public void testAskForSaveAfterSavedOnce() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_save_image)).perform(click());
		assertNotNull(launchActivityRule.getActivity().model.getSavedPictureUri());
		addUriToDeletionFileList(launchActivityRule.getActivity().model.getSavedPictureUri());

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		pressBack();
		onView(withText(R.string.menu_quit)).check(matches(isDisplayed()));
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private Uri createTestImageFile() {
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
			fail("Picture file could not be created.");
		}
		deletionFileList.add(imageFile);
		return Uri.fromFile(imageFile);
	}

	@Test
	public void testLoadImageTransparency() {
		Intent intent = new Intent();
		intent.setData(createTestImageFile());
		Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
		intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result);

		onNavigationDrawer()
				.performOpen();

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
		deletionFileList.add(new File(uri.getPath()));
	}
}
