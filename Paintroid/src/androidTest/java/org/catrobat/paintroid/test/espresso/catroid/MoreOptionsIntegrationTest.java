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

package org.catrobat.paintroid.test.espresso.catroid;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;

@RunWith(AndroidJUnit4.class)
public class MoreOptionsIntegrationTest {

	@Rule
	public IntentsTestRule<MainActivity> launchActivityRule = new IntentsTestRule<>(MainActivity.class, false, false);

	@ClassRule
	public static GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE);

	@Before
	public void setUp() {
		Intent intent = new Intent();
		intent.putExtra(Constants.PAINTROID_PICTURE_PATH, "");
		intent.putExtra(Constants.PAINTROID_PICTURE_NAME, "Look456");
		launchActivityRule.launchActivity(intent);
	}

	@Test
	public void testMoreOptionsAllItemsExist() {
		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_load_image)).check(matches(isDisplayed()));
		onView(withText(R.string.menu_hide_menu)).check(matches(isDisplayed()));
		onView(withText(R.string.help_title)).check(matches(isDisplayed()));
		onView(withText(R.string.pocketpaint_menu_about)).check(matches(isDisplayed()));

		onView(withText(R.string.menu_back)).check(matches(isDisplayed()));
		onView(withText(R.string.menu_discard_image)).check(matches(isDisplayed()));
		onView(withText(R.string.menu_export)).check(matches(isDisplayed()));

		onView(withText(R.string.menu_save_image)).check(doesNotExist());
		onView(withText(R.string.menu_save_copy)).check(doesNotExist());
		onView(withText(R.string.menu_new_image)).check(doesNotExist());
	}

	@Test
	public void testMoreOptionsDiscardImage() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
		onTopBarView()
				.onMoreOptionsClicked();
		onView(withText(R.string.menu_discard_image))
				.perform(click());
		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);
	}
}
