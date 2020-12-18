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

package org.catrobat.paintroid.test.espresso.tools;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.rtl.util.RtlActivityTestRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.junit.Assert.assertEquals;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ImportToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new RtlActivityTestRule<>(MainActivity.class, "ar");
	private MainActivity mainActivity;

	@Before
	public void setUp() {
		mainActivity = launchActivityRule.getActivity();
		onToolBarView()
				.performSelectTool(ToolType.IMPORTPNG);
	}

	@Test
	public void testImportDialogShownOnImportToolSelected() {
		onView(withId(R.id.pocketpaint_dialog_import_stickers)).check(matches(isDisplayed()));
		onView(withId(R.id.pocketpaint_dialog_import_gallery)).check(matches(isDisplayed()));
	}

	@Test
	public void testImportDialogDismissedOnCancelClicked() {
		onView(withText(R.string.pocketpaint_cancel)).perform(click());

		onView(withId(R.id.pocketpaint_dialog_import_stickers)).check(doesNotExist());
		onView(withId(R.id.pocketpaint_dialog_import_gallery)).check(doesNotExist());
	}

	@Test
	public void testImportDoesNotResetPerspectiveScale() {
		onView(withText(R.string.pocketpaint_cancel)).perform(click());

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		float scale = 2.0f;
		mainActivity.perspective.setScale(scale);
		mainActivity.refreshDrawingSurface();

		onToolBarView()
				.performSelectTool(ToolType.IMPORTPNG);

		onView(withText(R.string.pocketpaint_cancel)).perform(click());

		assertEquals(scale, mainActivity.perspective.getScale(), Float.MIN_VALUE);
	}
}
