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

import android.content.Intent;
import android.os.Environment;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.NavigationDrawerInteraction.onNavigationDrawer;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ActivityOpenedFromPocketCodeNewImageTest {

	private static final String IMAGE_NAME = "Look123";

	@Rule
	public IntentsTestRule<MainActivity> launchActivityRule = new IntentsTestRule<>(MainActivity.class, false, false);

	private File imageFile = null;

	@Before
	public void setUp() {
		Intent intent = new Intent();
		intent.putExtra(Constants.PAINTROID_PICTURE_PATH, "");
		intent.putExtra(Constants.PAINTROID_PICTURE_NAME, IMAGE_NAME);

		launchActivityRule.launchActivity(intent);

		imageFile = getImageFile(IMAGE_NAME);
	}

	@After
	public void tearDown() {
		if (imageFile.exists()) {
			imageFile.delete();
		}
	}

	@Test
	public void testSave() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_back))
				.perform(click());

		onView(withText(R.string.save_button_text))
				.check(matches(isDisplayed()));
		onView(withText(R.string.discard_button_text))
				.check(matches(isDisplayed()));

		onView(withText(R.string.save_button_text))
				.perform(click());

		String path = launchActivityRule.getActivityResult().getResultData().getStringExtra(Constants.PAINTROID_PICTURE_PATH);
		assertEquals(imageFile.getAbsolutePath(), path);

		assertTrue(imageFile.exists());
		assertThat(imageFile.length(), greaterThan(0L));
	}

	private File getImageFile(String filename) {
		return new File(Environment.getExternalStorageDirectory(), File.separatorChar
				+ Constants.EXT_STORAGE_DIRECTORY_NAME
				+ File.separatorChar + filename + ".png");
	}
}
