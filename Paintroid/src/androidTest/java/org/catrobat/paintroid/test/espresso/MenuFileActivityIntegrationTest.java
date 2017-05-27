/**
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

package org.catrobat.paintroid.test.espresso;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.NavigationDrawerMenuActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressMenuKey;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getCanvasPointFromScreenPoint;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getWorkingBitmap;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openNavigationDrawer;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetColorPicker;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.waitMillis;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.swipe;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MenuFileActivityIntegrationTest {

	private static ArrayList<File> deletionFileList = null;
	private PointF screenPoint = null;

	@Rule
	public IntentsTestRule<MainActivity> launchActivityRule = new IntentsTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private ActivityHelper activityHelper;

	@Before
	public void setUp() {

		activityHelper = new ActivityHelper(launchActivityRule.getActivity());

		selectTool(ToolType.BRUSH);

		screenPoint = new PointF(activityHelper.getDisplayWidth() / 2, activityHelper.getDisplayHeight() / 2);
		deletionFileList = new ArrayList<>();
	}

	@After
	public void tearDown() throws Exception {
		PaintroidApplication.savedPictureUri = null;
		PaintroidApplication.isSaved = false;
		for (File file : deletionFileList) {
			if (file != null) {
				boolean deleted = file.delete();
				assertTrue("File has not been deleted correctly", deleted);
			}
		}
	}

	@Test
	public void testNewEmptyDrawingWithSave() throws NoSuchFieldException, IllegalAccessException {
		final int xCoordinatePixel = 0;
		final int yCoordinatePixel = 0;

		onView(isRoot()).perform(touchAt(screenPoint.x, screenPoint.y));

		getWorkingBitmap().setPixel(xCoordinatePixel, yCoordinatePixel, Color.BLACK);
		assertEquals("Color on drawing surface wrong", Color.BLACK, PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));

		openNavigationDrawer();

		onView(withText(R.string.menu_new_image)).perform(click());

		onView(withText(R.string.save_button_text)).perform(click());

		onView(withText(R.string.menu_new_image_empty_image)).perform(click());

		assertEquals("Color should be Transparent", Color.TRANSPARENT, PaintroidApplication.drawingSurface.getPixel(new PointF(xCoordinatePixel, yCoordinatePixel)));
	}

	@Test
	public void testLoadImageDialog() {

		onView(isRoot()).perform(touchAt(screenPoint.x, screenPoint.y));

		openNavigationDrawer();

		onView(withText(R.string.menu_new_image)).perform(click());

		onView(withText(R.string.save_button_text)).check(matches(isDisplayed()));
		onView(withText(R.string.discard_button_text)).check(matches(isDisplayed()));
	}

	@Test
	public void testLoadImageDialogOnBackPressed() {
		onView(isRoot()).perform(touchAt(screenPoint.x, screenPoint.y));

		openNavigationDrawer();

		onView(withText(R.string.menu_load_image)).perform(click());

		pressBack();

		onView(withId(R.id.drawingSurfaceView)).check(matches(isDisplayed()));
	}

	@Test
	public void testWarningDialogOnNewImage() {

		onView(isRoot()).perform(touchAt(screenPoint.x, screenPoint.y));

		openNavigationDrawer();

		onView(withText(R.string.menu_new_image)).perform(click());

		onView(withText(R.string.dialog_warning_new_image)).check(matches(isDisplayed()));
		onView(withText(R.string.save_button_text)).check(matches(isDisplayed()));
		onView(withText(R.string.discard_button_text)).check(matches(isDisplayed()));

		pressBack();

		onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist());
	}

	@Test
	public void testNewEmptyDrawingWithDiscard() {

		onView(isRoot()).perform(touchAt(screenPoint.x, screenPoint.y));

		openNavigationDrawer();

		onView(withText(R.string.menu_new_image)).perform(click());

		onView(withText(R.string.discard_button_text)).perform(click());

		onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist());

		assertEquals("Bitmap pixel not changed", Color.BLACK, PaintroidApplication.drawingSurface.getPixel(getCanvasPointFromScreenPoint(screenPoint)));
	}

	@Test
	public void testNewEmptyDrawingDialogOnBackPressed() {
		selectTool(ToolType.BRUSH);
		resetColorPicker();

		onView(isRoot()).perform(touchAt(screenPoint.x, screenPoint.y));

		openNavigationDrawer();

		onView(withText(R.string.menu_new_image)).perform(click());

		onView(withText(R.string.dialog_warning_new_image)).check(matches(isDisplayed()));
		onView(withText(R.string.save_button_text)).check(matches(isDisplayed()));
		onView(withText(R.string.discard_button_text)).check(matches(isDisplayed()));

		pressBack();

		onView(withText(R.string.dialog_warning_new_image)).check(doesNotExist());

		assertEquals("Bitmap pixel not changed", Color.BLACK, PaintroidApplication.drawingSurface.getPixel(getCanvasPointFromScreenPoint(screenPoint)));
	}

	@Test
	public void testSavedStateChangeAfterSave() {

		onView(isRoot()).perform(touchAt(screenPoint.x, screenPoint.y));

		assertFalse("Image already saved", PaintroidApplication.isSaved);

		pressMenuKey();

		openNavigationDrawer();

		onView(withText(R.string.menu_save_image)).perform(click());

		assertNotNull("Saved picture uri is null", PaintroidApplication.savedPictureUri);

		addUriToDeletionFileList(PaintroidApplication.savedPictureUri);

		assertTrue("Image not saved", PaintroidApplication.isSaved);
	}

	@Test
	public void testSaveImage() {

		onView(isRoot()).perform(touchAt(screenPoint.x, screenPoint.y));

		openNavigationDrawer();

		onView(withText(R.string.menu_save_image)).perform(click());

		assertNotNull("Saved picture uri is null", PaintroidApplication.savedPictureUri);

		addUriToDeletionFileList(PaintroidApplication.savedPictureUri);
	}

	@Test
	public void testSaveCopy() {

		assertNull("Saved picture uri is not null", PaintroidApplication.savedPictureUri);

		onView(isRoot()).perform(touchAt(screenPoint.x, screenPoint.y));

		openNavigationDrawer();

		onView(withText(R.string.menu_save_image)).perform(click());

		assertNotNull("Saved picture uri is null", PaintroidApplication.savedPictureUri);

		addUriToDeletionFileList(PaintroidApplication.savedPictureUri);

		File oldFile = new File(PaintroidApplication.savedPictureUri.toString());

		final int screenTouchYOffset = 100;
		onView(isRoot()).perform(touchAt(screenPoint.x, screenPoint.y + screenTouchYOffset));

		openNavigationDrawer();

		onView(withText(R.string.menu_save_copy)).perform(click());

		File newFile = new File(PaintroidApplication.savedPictureUri.toString());

		assertNotSame("Changes to saved", oldFile, newFile);

		assertNotNull("Saved picture uri is null", PaintroidApplication.savedPictureUri);

		addUriToDeletionFileList(PaintroidApplication.savedPictureUri);
	}

	@Test
	@Ignore // TODO: fails, File is still the same
	public void testSaveLoadedImage() throws URISyntaxException, IOException {

		// Save new image, stub ACTION_GET_CONTENT intent
		Intent intent = new Intent();
		intent.setData(PaintroidApplication.savedPictureUri);
		Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
		intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result);

		final int xOffset = 100;
		onView(isRoot()).perform(swipe(screenPoint, new PointF(screenPoint.x + xOffset, screenPoint.y)));

		openNavigationDrawer();

		onView(withText(R.string.menu_save_image)).perform(click());

		assertNotNull("Saved picture uri is null", PaintroidApplication.savedPictureUri);

		addUriToDeletionFileList(PaintroidApplication.savedPictureUri);

		// Load the saved image
		openNavigationDrawer();

		onView(withText(R.string.menu_load_image)).perform(click());

		assertTrue("Save copy flag not true", PaintroidApplication.saveCopy);

		openNavigationDrawer();

		// Save copy of image
		onView(withText(R.string.menu_save_copy)).perform(click());

		assertNotNull("Saved picture uri is null", PaintroidApplication.savedPictureUri);

		addUriToDeletionFileList(PaintroidApplication.savedPictureUri);

		File saveFile = new File(getRealFilePathFromUri(PaintroidApplication.savedPictureUri));

		final long oldLength = saveFile.length();
		final long firstModified = saveFile.lastModified();

		// Draw and save image
		final int yOffset = 200;
		onView(isRoot()).perform(swipe(screenPoint, new PointF(screenPoint.x, screenPoint.y + yOffset)));

		openNavigationDrawer();

		onView(withText(R.string.menu_save_image)).perform(click());

		File actualSaveFile = new File(getRealFilePathFromUri(PaintroidApplication.savedPictureUri));

		long newLength = actualSaveFile.length();
		long lastModified = actualSaveFile.lastModified();

		assertNotEquals("File is still the same", oldLength, newLength);
		assertNotEquals("File not currently modified", firstModified, lastModified);
	}

	private String getRealFilePathFromUri(Uri uri) {
		String[] fileColumns = { MediaStore.Images.Media.DATA };

		Cursor cursor = launchActivityRule.getActivity().getContentResolver().query(uri, fileColumns, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(fileColumns[0]);
		String realFilePath = cursor.getString(columnIndex);
		cursor.close();

		return realFilePath;
	}

	private void addUriToDeletionFileList(Uri uri) {
		deletionFileList.add(new File(getRealFilePathFromUri(uri)));
	}

}
