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
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ConfirmQuitDialogInteraction.onConfirmQuitDialog;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.NavigationDrawerInteraction.onNavigationDrawer;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
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

	private File saveFile = null;

	@Before
	public void setUp() {
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
		long oldFileSize = saveFile.length();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		Espresso.pressBack();

		onConfirmQuitDialog().onPositiveButton()
				.perform(click());

		long actualFileSize = saveFile.length();
		assertNotEquals(oldFileSize, actualFileSize);
	}

	@Test
	public void testNotBrushToolBackPressed() {
		onToolBarView()
				.performSelectTool(ToolType.CURSOR);

		Espresso.pressBack();

		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.BRUSH);
	}

	@Test
	public void testToolOptionsDisappearWhenBackPressed() {
		onToolBarView()
				.performSelectTool(ToolType.CURSOR)
				.performOpenToolOptions();

		onView(withId(R.id.pocketpaint_layout_tool_options_name))
				.check(matches(withText(R.string.button_cursor)));

		Espresso.pressBack();

		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.CURSOR);

		onView(withId(R.id.pocketpaint_main_tool_options)).check(matches(not(isDisplayed())));
		onView(withId(R.id.pocketpaint_layout_tool_options_name)).check(matches(not(isDisplayed())));

		Espresso.pressBack();

		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.BRUSH);
	}

	@Test
	public void testBrushToolBackPressedFromCatroidAndUsePicture() throws SecurityException, IllegalArgumentException {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		String pathToFile =
				Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
						+ Constants.EXT_STORAGE_DIRECTORY_NAME
						+ File.separator
						+ Constants.TEMP_PICTURE_NAME
						+ FILE_ENDING;

		saveFile = new File(pathToFile);
		launchActivityRule.getActivity().model.setSavedPictureUri(Uri.fromFile(saveFile));
		launchActivityRule.getActivity().model.setOpenedFromCatroid(true);

		Espresso.pressBack();

		onConfirmQuitDialog()
				.checkPositiveButton(matches(isDisplayed()))
				.checkNegativeButton(matches(isDisplayed()))
				.checkNeutralButton(matches(not(isDisplayed())))
				.checkMessage(matches(isDisplayed()));

		onView(withText(R.string.closing_catroid_security_question_title))
				.check(matches(isDisplayed()));

		Espresso.pressBack();

		onConfirmQuitDialog()
				.checkPositiveButton(doesNotExist())
				.checkNegativeButton(doesNotExist())
				.checkNeutralButton(doesNotExist())
				.checkMessage(doesNotExist());

		onView(withText(R.string.closing_catroid_security_question_title))
				.check(doesNotExist());

		Espresso.pressBack();

		onConfirmQuitDialog().onPositiveButton()
				.perform(click());

		assertTrue(launchActivityRule.getActivity().isFinishing());
		assertTrue(saveFile.exists());
		assertThat(saveFile.length(), is(greaterThan(0L)));
	}

	@Test
	public void testBrushToolBackPressedFromCatroidAndDiscardPicture() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		String pathToFile = launchActivityRule.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
				+ File.separator
				+ Constants.TEMP_PICTURE_NAME
				+ FILE_ENDING;

		saveFile = new File(pathToFile);
		launchActivityRule.getActivity().model.setSavedPictureUri(Uri.fromFile(saveFile));
		launchActivityRule.getActivity().model.setOpenedFromCatroid(true);

		Espresso.pressBack();

		onConfirmQuitDialog()
				.checkPositiveButton(matches(isDisplayed()))
				.checkNegativeButton(matches(isDisplayed()))
				.checkNeutralButton(matches(not(isDisplayed())))
				.checkMessage(matches(isDisplayed()));

		onView(withText(R.string.closing_catroid_security_question_title))
				.check(matches(isDisplayed()));

		onConfirmQuitDialog().onNegativeButton()
				.perform(click());

		assertTrue(launchActivityRule.getActivity().isFinishing());
		assertFalse(saveFile.exists());
	}

	@Test
	public void testCloseNavigationDrawerOnBackPressed() {
		onNavigationDrawer()
				.perform(open(Gravity.START))
				.check(matches(isOpen()));
		pressBack();
		onNavigationDrawer()
				.check(matches(isClosed()));
	}

	@Test
	public void testCloseLayerDialogOnBackPressed() {
		onNavigationDrawer()
				.perform(open(Gravity.END))
				.check(matches(isDisplayed()));
		pressBack();
		onNavigationDrawer()
				.check(matches(isClosed()));
	}

	@Test
	public void testCloseColorPickerDialogOnBackPressed() {
		onColorPickerView()
				.performOpenColorPicker()
				.check(matches(isDisplayed()));
		pressBack();
		onColorPickerView()
				.check(doesNotExist());
	}

	@Test
	public void testCloseToolOptionOnBackPressed() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onToolBarView().onToolOptions()
				.check(matches(isDisplayed()));
		pressBack();
		onToolBarView().onToolOptions()
				.check(matches(not(isDisplayed())));
	}

	@Test
	public void testCloseToolOptionsOnUndoPressed() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onToolBarView()
				.performSelectTool(ToolType.TEXT);
		onToolBarView().onToolOptions()
				.check(matches(isDisplayed()));
		onTopBarView().onUndoButton()
				.check(matches(allOf(isDisplayed(), isEnabled())))
				.perform(click());
		onToolBarView().onToolOptions()
				.check(matches(not(isDisplayed())));
	}
}
