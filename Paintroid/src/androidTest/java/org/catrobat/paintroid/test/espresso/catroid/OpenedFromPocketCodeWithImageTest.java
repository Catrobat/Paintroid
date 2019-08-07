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
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Environment;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class OpenedFromPocketCodeWithImageTest {

	@Rule
	public IntentsTestRule<MainActivity> launchActivityRule = new IntentsTestRule<>(MainActivity.class, false, false);

	@ClassRule
	public static GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE);

	private File imageFile = null;

	@Before
	public void setUp() {
		imageFile = createImageFile();

		Intent extras = new Intent();
		extras.putExtra(Constants.PAINTROID_PICTURE_PATH, imageFile.getAbsolutePath());
		launchActivityRule.launchActivity(extras);

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
	}

	@After
	public void tearDown() {
		if (imageFile != null && imageFile.exists()) {
			assertTrue(imageFile.delete());
		}
	}

	@Test
	public void testSave() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_back)).perform(click());

		onView(withText(R.string.save_button_text)).check(matches(isDisplayed()));
		onView(withText(R.string.discard_button_text)).check(matches(isDisplayed()));

		long lastModifiedBefore = imageFile.lastModified();
		long fileSizeBefore = imageFile.length();

		onView(withText(R.string.save_button_text)).perform(click());

		assertTrue(launchActivityRule.getActivity().isFinishing());
		String path = launchActivityRule.getActivityResult().getResultData().getStringExtra(Constants.PAINTROID_PICTURE_PATH);
		assertEquals(imageFile.getAbsolutePath(), path);

		assertThat("Image modification not saved", imageFile.lastModified(), greaterThan(lastModifiedBefore));
		assertThat("Saved image length not changed", imageFile.length(), greaterThan(fileSizeBefore));
	}

	@Test
	public void testBackToPocketCode() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.onMoreOptionsClicked();

		onView(withText(R.string.menu_back)).perform(click());

		onView(withText(R.string.save_button_text)).check(matches(isDisplayed()));
		onView(withText(R.string.discard_button_text)).check(matches(isDisplayed()));

		long lastModifiedBefore = imageFile.lastModified();
		long fileSizeBefore = imageFile.length();

		assertThat("Image modified", imageFile.lastModified(), equalTo(lastModifiedBefore));
		assertThat("Saved image length changed", imageFile.length(), equalTo(fileSizeBefore));
	}

	private File createImageFile() {
		Bitmap bitmap = Bitmap.createBitmap(480, 800, Config.ARGB_8888);
		File pictureFile = getImageFile();
		try {
			pictureFile.getParentFile().mkdirs();
			pictureFile.createNewFile();
			OutputStream outputStream = new FileOutputStream(pictureFile);
			assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream));
			outputStream.close();
		} catch (IOException e) {
			fail("Picture file could not be created.");
		}

		return pictureFile;
	}

	private File getImageFile() {
		return new File(Environment.getExternalStorageDirectory() + "/PocketCodePaintTest/", "testFile.png");
	}
}
