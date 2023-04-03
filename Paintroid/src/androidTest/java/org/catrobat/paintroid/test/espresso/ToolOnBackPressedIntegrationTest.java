/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction;
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.hamcrest.core.AllOf;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.waitFor;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ConfirmQuitDialogInteraction.onConfirmQuitDialog;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ToolOnBackPressedIntegrationTest {

	private static final String FILE_ENDING = ".png";

	@Rule
	public IntentsTestRule<MainActivity> launchActivityRule = new IntentsTestRule<>(MainActivity.class, false, true);

	@Rule
	public ScreenshotOnFailRule screenshotOnFailRule = new ScreenshotOnFailRule();

	@ClassRule
	public static GrantPermissionRule grantPermissionRule = EspressoUtils.INSTANCE.grantPermissionRulesVersionCheck();

	private File saveFile = null;
	private ToolReference toolReference;
	public final String defaultPictureName = "catroidTemp";

	@Before
	public void setUp() {
		MainActivity activity = launchActivityRule.getActivity();
		toolReference = activity.toolReference;

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
	}

	@After
	public void tearDown() {
		if (saveFile != null && saveFile.exists()) {
			saveFile.delete();
			saveFile = null;
		}

		String imagesDirectory = String.valueOf(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
		String pathToFile = imagesDirectory + File.separator + defaultPictureName + FILE_ENDING;
		File imageFile = new File(pathToFile);
		if (imageFile.exists()) {
			imageFile.delete();
		}
	}

	@Test
	public void testBrushToolBackPressed() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		Espresso.pressBack();

		onConfirmQuitDialog()
				.checkPositiveButton(matches(isDisplayed()))
				.checkNegativeButton(matches(isDisplayed()))
				.checkNeutralButton(matches(not(isDisplayed())))
				.checkMessage(matches(isDisplayed()))
				.checkTitle(matches(isDisplayed()));

		Espresso.pressBack();

		onConfirmQuitDialog()
				.checkPositiveButton(doesNotExist())
				.checkNegativeButton(doesNotExist())
				.checkNeutralButton(doesNotExist())
				.checkMessage(doesNotExist())
				.checkTitle(doesNotExist());

		Espresso.pressBack();

		onConfirmQuitDialog().onNegativeButton()
				.perform(click());

		assertTrue(launchActivityRule.getActivity().isFinishing());
	}

	@Test
	public void testBrushToolBackPressedWithSaveAndOverride() throws IOException, InterruptedException {
		TopBarViewInteraction.onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.menu_save_image))
				.perform(ViewActions.click());

		onView(withId(R.id.pocketpaint_save_info_title)).check(matches(isDisplayed()));
		onView(withId(R.id.pocketpaint_image_name_save_text)).check(matches(isDisplayed()));
		onView(withId(R.id.pocketpaint_save_dialog_spinner)).check(matches(isDisplayed()));

		onView(withId(R.id.pocketpaint_save_dialog_spinner))
				.perform(click());
		onData(AllOf.allOf(is(instanceOf(String.class)),
				is("png"))).inRoot(isPlatformPopup()).perform(click());
		onView(withId(R.id.pocketpaint_image_name_save_text))
				.perform(replaceText(defaultPictureName));

		onView(withText(R.string.save_button_text))
				.perform(click());

		onView(isRoot()).perform(waitFor(3000));

		String filename = defaultPictureName + FILE_ENDING;
		ContentResolver resolver = launchActivityRule.getActivity().getContentResolver();
		Uri uri = FileIO.INSTANCE.getUriForFilenameInPicturesFolder(filename, resolver);
		BitmapFactory.Options options = new BitmapFactory.Options();
		assertNotNull(uri);
		InputStream inputStream = resolver.openInputStream(uri);
		Bitmap oldBitmap = BitmapFactory.decodeStream(inputStream, null, options);

		onView(isRoot()).perform(waitFor(2000));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		Espresso.pressBack();

		onConfirmQuitDialog().onPositiveButton()
				.perform(click());

		onView(withId(R.id.pocketpaint_save_info_title)).check(matches(isDisplayed()));
		onView(withId(R.id.pocketpaint_image_name_save_text)).check(matches(isDisplayed()));
		onView(withId(R.id.pocketpaint_save_dialog_spinner)).check(matches(isDisplayed()));

		onView(withId(R.id.pocketpaint_save_dialog_spinner))
				.perform(click());
		onData(AllOf.allOf(is(instanceOf(String.class)),
				is("png"))).inRoot(isPlatformPopup()).perform(click());
		onView(withId(R.id.pocketpaint_image_name_save_text))
				.perform(replaceText(defaultPictureName));

		onView(withText(R.string.save_button_text))
				.perform(click());

		onView(withText(R.string.overwrite_button_text))
				.perform(click());

		while (!launchActivityRule.getActivity().isFinishing()) {
			Thread.sleep(1000);
		}

		uri = FileIO.INSTANCE.getUriForFilenameInPicturesFolder(filename, resolver);
		assertNotNull(uri);
		inputStream = resolver.openInputStream(uri);
		Bitmap actualBitmap = BitmapFactory.decodeStream(inputStream, null, options);

		assertNotNull(oldBitmap);
		assertNotNull(actualBitmap);
		assertFalse("Bitmaps are the same, should be different", oldBitmap.sameAs(actualBitmap));
	}

	@Test
	public void testNotBrushToolBackPressed() {
		onToolBarView()
				.performSelectTool(ToolType.CURSOR);

		Espresso.pressBack();

		assertEquals(toolReference.getTool().getToolType(), ToolType.BRUSH);
	}

	@Test
	public void testToolOptionsGoBackWhenBackPressed() {
		onToolBarView()
				.performSelectTool(ToolType.CURSOR);

		assertEquals(toolReference.getTool().getToolType(), ToolType.CURSOR);

		Espresso.pressBack();

		assertEquals(toolReference.getTool().getToolType(), ToolType.BRUSH);
	}

	@Test
	public void testBrushToolBackPressedFromCatroidAndUsePicture() throws SecurityException, IllegalArgumentException, InterruptedException {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		String pathToFile =
				launchActivityRule.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
						+ File.separator
						+ defaultPictureName
						+ FILE_ENDING;

		saveFile = new File(pathToFile);
		launchActivityRule.getActivity().model.setSavedPictureUri(Uri.fromFile(saveFile));
		launchActivityRule.getActivity().model.setOpenedFromCatroid(true);

		Espresso.pressBackUnconditionally();

		while (!launchActivityRule.getActivity().isFinishing()) {
			Thread.sleep(1000);
		}

		assertTrue(launchActivityRule.getActivity().isFinishing());
		assertTrue(saveFile.exists());
		assertThat(saveFile.length(), is(greaterThan(0L)));
	}

	@Test
	public void testCloseLayerDialogOnBackPressed() {
		onView(withId(R.id.pocketpaint_drawer_layout))
				.perform(open(Gravity.END))
				.check(matches(isDisplayed()));
		pressBack();
		onView(withId(R.id.pocketpaint_drawer_layout))
				.check(matches(isClosed()));
	}

	@Test
	public void testCloseColorPickerDialogOnBackPressed() {
		onColorPickerView()
				.performOpenColorPicker()
				.check(matches(isDisplayed()));

		onColorPickerView()
				.perform(closeSoftKeyboard())
				.perform(ViewActions.pressBack())
				.check(doesNotExist());
	}
}
