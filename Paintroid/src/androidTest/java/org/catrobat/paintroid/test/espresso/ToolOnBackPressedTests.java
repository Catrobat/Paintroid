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

import android.net.Uri;
import android.os.Environment;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;
import android.widget.Button;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.NavigationDrawerMenuActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openToolOptionsForCurrentTool;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterMiddle;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ToolOnBackPressedTests {

	private static final String FILE_ENDING = ".png";

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private File saveFile = null;

	@Before
	public void setUp() {
		deleteSaveFileIfExists();

		selectTool(ToolType.BRUSH);
	}

	@After
	public void tearDown() {
		deleteSaveFileIfExists();
	}

	protected void deleteSaveFileIfExists() {
		if (saveFile != null && saveFile.exists()) {
			saveFile.delete();
		}
	}

	protected ViewInteraction getPositiveButtonViewInteraction() {
		return onView(allOf(withId(android.R.id.button1), withText(R.string.save_button_text), isAssignableFrom(Button.class)));
	}

	protected ViewInteraction getNegativeButtonViewInteraction() {
		return onView(allOf(withId(android.R.id.button2), withText(R.string.discard_button_text), isAssignableFrom(Button.class)));
	}

	protected ViewInteraction getNeutralButtonViewInteraction() {
		return onView(withId(android.R.id.button3));
	}

	protected ViewInteraction getDialogTitleViewInteraction() {
		return onView(withText(R.string.closing_security_question_title));
	}

	protected ViewInteraction getDialogCatroidTitleViewInteraction() {
		return onView(withText(R.string.closing_catroid_security_question_title));
	}

	protected ViewInteraction getDialogMessageViewInteraction() {
		return onView(withText(R.string.closing_security_question));
	}

	protected void checkDialogVisible() {
		final ViewInteraction negativeDialogButton = getNegativeButtonViewInteraction();
		final ViewInteraction positiveDialogButton = getPositiveButtonViewInteraction();
		final ViewInteraction neutralDialogButton = getNeutralButtonViewInteraction();

		final ViewInteraction dialogTitle = getDialogTitleViewInteraction();
		final ViewInteraction dialogMessage = getDialogMessageViewInteraction();

		negativeDialogButton.check(matches(isDisplayed()));
		positiveDialogButton.check(matches(isDisplayed()));
		neutralDialogButton.check(matches(not(isDisplayed())));

		dialogMessage.check(matches(isDisplayed()));
		dialogTitle.check(matches(isDisplayed()));
	}

	protected void checkDialogDoesNotExist() {
		final ViewInteraction negativeDialogButton = getNegativeButtonViewInteraction();
		final ViewInteraction positiveDialogButton = getPositiveButtonViewInteraction();
		final ViewInteraction neutralDialogButton = getNeutralButtonViewInteraction();

		final ViewInteraction dialogTitle = getDialogTitleViewInteraction();
		final ViewInteraction dialogMessage = getDialogMessageViewInteraction();

		negativeDialogButton.check(doesNotExist());
		positiveDialogButton.check(doesNotExist());
		neutralDialogButton.check(doesNotExist());

		dialogMessage.check(doesNotExist());
		dialogTitle.check(doesNotExist());
	}

	protected void checkFromCatroidDialogVisible() {
		final ViewInteraction negativeDialogButton = getNegativeButtonViewInteraction();
		final ViewInteraction positiveDialogButton = getPositiveButtonViewInteraction();
		final ViewInteraction neutralDialogButton = getNeutralButtonViewInteraction();

		final ViewInteraction dialogTitle = getDialogCatroidTitleViewInteraction();
		final ViewInteraction dialogMessage = getDialogMessageViewInteraction();

		negativeDialogButton.check(matches(isDisplayed()));
		positiveDialogButton.check(matches(isDisplayed()));
		neutralDialogButton.check(matches(not(isDisplayed())));

		dialogMessage.check(matches(isDisplayed()));
		dialogTitle.check(matches(isDisplayed()));
	}

	protected void checkFromCatroidDialogDoesNotExist() {
		final ViewInteraction negativeDialogButton = getNegativeButtonViewInteraction();
		final ViewInteraction positiveDialogButton = getPositiveButtonViewInteraction();
		final ViewInteraction neutralDialogButton = getNeutralButtonViewInteraction();

		final ViewInteraction dialogTitle = getDialogCatroidTitleViewInteraction();
		final ViewInteraction dialogMessage = getDialogMessageViewInteraction();

		negativeDialogButton.check(doesNotExist());
		positiveDialogButton.check(doesNotExist());
		neutralDialogButton.check(doesNotExist());

		dialogMessage.check(doesNotExist());
		dialogTitle.check(doesNotExist());
	}

	@Test
	public void testBrushToolBackPressed() {

		final ViewInteraction negativeDialogButton = getNegativeButtonViewInteraction();

		// Make change to drawing surface
		onView(withId(R.id.drawingSurfaceView)).perform(touchCenterMiddle());

		// Open dialog
		Espresso.pressBack();

		checkDialogVisible();

		// Close dialog
		Espresso.pressBack();

		checkDialogDoesNotExist();

		// Open dialog
		Espresso.pressBack();

		negativeDialogButton.perform(click());

		assertTrue("Activity is not finishing after discard", launchActivityRule.getActivity().isFinishing());
	}

	@Test
	public void testBrushToolBackPressedWithSaveAndOverride() throws IOException {

		String pathToFile = launchActivityRule.getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
				+ File.separator
				+ Constants.TEMP_PICTURE_NAME
				+ FILE_ENDING;

		saveFile = new File(pathToFile);

		NavigationDrawerMenuActivity.savedPictureUri = Uri.fromFile(saveFile);
		long oldFileSize = saveFile.length();

		onView(withId(R.id.drawingSurfaceView)).perform(touchCenterMiddle());

		Espresso.pressBack();

		final ViewInteraction positiveDialogButton = getPositiveButtonViewInteraction();

		positiveDialogButton.perform(click());

		long actualFileSize = saveFile.length();

		assertNotEquals("Application finished, files not different.", oldFileSize, actualFileSize);
	}

	@Test
	public void testNotBrushToolBackPressed() {

		selectTool(ToolType.CURSOR);

		Espresso.pressBack();

		assertEquals("Switching to another tool", PaintroidApplication.currentTool.getToolType(), ToolType.BRUSH);
	}

	@Test
	public void testToolOptionsDisappearWhenBackPressed() {

		selectTool(ToolType.CURSOR);
		openToolOptionsForCurrentTool();

		String currentToolName = launchActivityRule.getActivity().getString(PaintroidApplication.currentTool.getToolType().getNameResource());

		onView(withId(R.id.layout_tool_options_name)).check(matches(withText(currentToolName)));

		Espresso.pressBack();

		assertEquals("Tool should not have changed", PaintroidApplication.currentTool.getToolType(), ToolType.CURSOR);

		onView(withId(R.id.main_tool_options)).check(matches(not(isDisplayed())));
		onView(withId(R.id.layout_tool_options_name)).check(matches(not(isDisplayed())));

		Espresso.pressBack();

		assertEquals("Tool should not have changed", PaintroidApplication.currentTool.getToolType(), ToolType.BRUSH);
	}

	@Test
	public void testBrushToolBackPressedFromCatroidAndUsePicture() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {

		onView(withId(R.id.drawingSurfaceView)).perform(touchCenterMiddle());

		String pathToFile =
				Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
						+ Constants.EXT_STORAGE_DIRECTORY_NAME
						+ File.separator
						+ Constants.TEMP_PICTURE_NAME
						+ FILE_ENDING;

		saveFile = new File(pathToFile);

		launchActivityRule.getActivity().openedFromCatroid = true;

		Espresso.pressBack();

		checkFromCatroidDialogVisible();

		Espresso.pressBack();

		checkFromCatroidDialogDoesNotExist();

		Espresso.pressBack();

		getPositiveButtonViewInteraction().perform(click());

		assertTrue("Activity is not finishing after save", launchActivityRule.getActivity().isFinishing());
		assertTrue("No file was created", saveFile.exists());
		assertThat("The created file is empty", saveFile.length(), is(greaterThan(0L)));
	}

	@Test
	public void testBrushToolBackPressedFromCatroidAndDiscardPicture() {

		onView(withId(R.id.drawingSurfaceView)).perform(touchCenterMiddle());

		String pathToFile = launchActivityRule.getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
				+ File.separator
				+ Constants.TEMP_PICTURE_NAME
				+ FILE_ENDING;

		saveFile = new File(pathToFile);

		launchActivityRule.getActivity().openedFromCatroid = true;

		Espresso.pressBack();

		checkFromCatroidDialogVisible();

		getNegativeButtonViewInteraction().perform(click());

		assertTrue("Activity is not finishing after save", launchActivityRule.getActivity().isFinishing());
		assertFalse("File was created", saveFile.exists());
	}

	@Test
	public void testCloseNavigationDrawerOnBackPressed() {
		onView(withId(R.id.drawer_layout)).perform(open());
		onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
		pressBack();
		onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
	}

	@Test
	public void testCloseLayerDialogOnBackPressed() {
		onView(withId(R.id.drawer_layout)).perform(open(Gravity.END));
		onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
		pressBack();
		onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
	}

	@Test
	public void testCloseColorPickerDialogOnBackPressed() {
		onView(withId(R.id.btn_top_color)).perform(click());
		onView(withId(R.id.colorchooser_base_layout)).check(matches(isDisplayed()));
		pressBack();
		onView(withId(R.id.colorchooser_base_layout)).check(doesNotExist());
	}

	@Test
	public void testCloseToolOptionOnBackPressed() {
		onView(withId(R.id.tools_rectangle)).perform(click());
		onView(withId(R.id.layout_tool_options)).check(matches(isDisplayed()));
		pressBack();
		onView(withId(R.id.layout_tool_options)).check(matches(not(isDisplayed())));
	}

	@Test
	public void testCloseToolOptionsOnUndoPressed() {
		onView(isRoot())
				.perform(click());
		onToolBarView()
				.performSelectTool(ToolType.TEXT);
		onView(withId(R.id.layout_tool_options))
				.check(matches(isDisplayed()));
		onView(withId(R.id.btn_top_undo))
				.check(matches(allOf(isDisplayed(), isEnabled())))
				.perform(click());
		onView(withId(R.id.layout_tool_options))
				.check(matches(not(isDisplayed())));
	}
}
