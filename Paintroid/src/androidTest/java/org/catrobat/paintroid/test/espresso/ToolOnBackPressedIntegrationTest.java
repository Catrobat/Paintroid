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

package org.catrobat.paintroid.test.espresso;

import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
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

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ConfirmQuitDialogInteraction.onConfirmQuitDialog;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ToolOnBackPressedIntegrationTest {

	private static final String FILE_ENDING = ".png";

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@ClassRule
	public static GrantPermissionRule grantPermissionRule = EspressoUtils.grantPermissionRulesVersionCheck();

	private File saveFile = null;
	private ToolReference toolReference;

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
	public void testBrushToolBackPressedWithSaveAndOverride() {
		String pathToFile = launchActivityRule.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
				+ File.separator
				+ Constants.TEMP_PICTURE_NAME
				+ FILE_ENDING;

		saveFile = new File(pathToFile);

		launchActivityRule.getActivity().model.setSavedPictureUri(Uri.fromFile(saveFile));
		FileIO.currentFileNamePng = Constants.TEMP_PICTURE_NAME + FILE_ENDING;
		FileIO.uriFilePng = Uri.fromFile(saveFile);
		long oldFileSize = saveFile.length();

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
				.perform(replaceText(Constants.TEMP_PICTURE_NAME));

		onView(withText(R.string.save_button_text))
				.perform(click());

		onView(withText(R.string.overwrite_button_text))
				.perform(click());

		long actualFileSize = saveFile.length();
		assertNotEquals(oldFileSize, actualFileSize);
	}

	@Test
	public void testNotBrushToolBackPressed() {
		onToolBarView()
				.performSelectTool(ToolType.CURSOR);

		Espresso.pressBack();

		assertEquals(toolReference.get().getToolType(), ToolType.BRUSH);
	}

	@Test
	public void testBrushToolBackPressedFromCatroidAndUsePicture() throws SecurityException, IllegalArgumentException {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		String pathToFile =
				launchActivityRule.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
						+ File.separator
						+ Constants.TEMP_PICTURE_NAME
						+ FILE_ENDING;

		saveFile = new File(pathToFile);
		launchActivityRule.getActivity().model.setSavedPictureUri(Uri.fromFile(saveFile));
		launchActivityRule.getActivity().model.setOpenedFromCatroid(true);

		Espresso.pressBackUnconditionally();

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

	@Test
	public void testCloseToolOptionOnBackPressed() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onToolBarView().onToolOptionsView()
				.check(matches(isDisplayed()));
		pressBack();
		onToolBarView().onToolOptionsView()
				.check(matches(not(isDisplayed())));
	}
}
