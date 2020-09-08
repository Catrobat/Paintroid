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
import android.content.Context;
import android.content.Intent;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertTrue;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import static org.catrobat.paintroid.test.espresso.util.wrappers.OptionsMenuViewInteraction.onOptionsMenu;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MoreOptionsIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@ClassRule
	public static GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE);

	@Before
	public void setUp() {
		onTopBarView()
				.performOpenMoreOptions();

		activityTestRule.getActivity().getPreferences(Context.MODE_PRIVATE)
				.edit()
				.clear()
				.commit();
	}

	@After
	public void tearDown() {
		activityTestRule.getActivity().getPreferences(Context.MODE_PRIVATE)
				.edit()
				.clear()
				.commit();
	}

	@Test
	public void testMoreOptionsCloseOnBack() {
		onView(withText(R.string.menu_load_image))
				.check(matches(isDisplayed()));

		pressBack();
		onView(withText(R.string.menu_load_image))
				.check(doesNotExist());
	}

	@Test
	public void testMoreOptionsAllItemsExist() {
		onOptionsMenu()
				.checkItemExists(R.string.menu_load_image)
				.checkItemExists(R.string.menu_hide_menu)
				.checkItemExists(R.string.help_title)
				.checkItemExists(R.string.pocketpaint_menu_about)
				.checkItemExists(R.string.menu_rate_us)
				.checkItemExists(R.string.menu_save_image)
				.checkItemExists(R.string.menu_save_copy)
				.checkItemExists(R.string.menu_new_image)
				.checkItemExists(R.string.menu_feedback)
				.checkItemExists(R.string.share_image_menu)

				.checkItemDoesNotExist(R.string.menu_discard_image)
				.checkItemDoesNotExist(R.string.menu_export);
	}

	@Test
	public void testMoreOptionsItemHelpClick() {
		onView(withText(R.string.help_title)).perform(click());
	}

	@Test
	public void testMoreOptionsItemAboutClick() {
		onView(withText(R.string.pocketpaint_about_title)).perform(click());
	}

	@Test
	public void testMoreOptionsShareImageClicked() {
		onView(withText(R.string.share_image_menu)).perform(click());
		UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
		UiObject uiObject = mDevice.findObject(new UiSelector());
		assertTrue(uiObject.exists());
		mDevice.pressBack();
	}

	@Test
	public void testMoreOptionsItemNewImageClick() {
		onView(withText(R.string.menu_new_image)).perform(click());
	}

	@Test
	public void testMoreOptionsItemMenuSaveClick() {
		onView(withText(R.string.menu_save_image)).perform(click());
	}

	@Test
	public void testMoreOptionsItemMenuCopyClick() {
		onView(withText(R.string.menu_save_copy)).perform(click());
	}
	@Test
	public void testMoreOptionsFeedbackClick() {
		Intent intent = new Intent();
		Intents.init();
		Instrumentation.ActivityResult intentResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);

		Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult);

		onView(withText(R.string.menu_feedback)).perform(click());

		Intents.intended(IntentMatchers.hasAction(Intent.ACTION_SENDTO));
		Intents.release();
	}

	@Test
	public void testShowLikeUsDialogOnFirstSave() {
		onView(withText(R.string.menu_save_image)).perform(click());
		onView(withText(R.string.pocketpaint_like_us)).check(matches(isDisplayed()));
	}

	@Test
	public void testShowRateUsDialogOnLikeUsDialogPositiveButtonPressed() {
		onView(withText(R.string.menu_save_image)).perform(click());
		onView(withText(R.string.pocketpaint_yes)).perform(click());
		onView(withText(R.string.pocketpaint_rate_us)).check(matches(isDisplayed()));
	}

	@Test
	public void testShowFeedbackDialogOnLikeUsDialogNegativeButtonPressed() {
		onView(withText(R.string.menu_save_image)).perform(click());
		onView(withText(R.string.pocketpaint_no)).perform(click());
		onView(withText(R.string.pocketpaint_feedback)).check(matches(isDisplayed()));
	}

	@Test
	public void testLikeUsDialogNotShownOnSecondSave() {
		onView(withText(R.string.menu_save_image)).perform(click());
		onView(withText(R.string.pocketpaint_like_us)).check(matches(isDisplayed()));
		pressBack();

		onTopBarView()
				.performOpenMoreOptions();

		onView(withText(R.string.menu_save_image)).perform(click());
		onView(withText(R.string.pocketpaint_like_us)).check(doesNotExist());
	}
}
