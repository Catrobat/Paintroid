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

import android.content.Intent;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;

@RunWith(AndroidJUnit4.class)
public class OpenedFromPocketCodeNewImageTest {

	private static final String IMAGE_NAME = "testFile";

	@Rule
	public IntentsTestRule<MainActivity> launchActivityRule = new IntentsTestRule<>(MainActivity.class, false, false);

	@ClassRule
	public static GrantPermissionRule grantPermissionRule = EspressoUtils.grantPermissionRulesVersionCheck();

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

		Espresso.pressBackUnconditionally();

		String path = launchActivityRule.getActivityResult().getResultData().getStringExtra(Constants.PAINTROID_PICTURE_PATH);
		assertEquals(imageFile.getAbsolutePath(), path);

		assertTrue(imageFile.exists());
		assertThat(imageFile.length(), greaterThan(0L));
	}

	private File getImageFile(String filename) {
		try {
			return FileIO.createNewEmptyPictureFile(filename, launchActivityRule.getActivity());
		} catch (NullPointerException e) {
			throw new AssertionError("Could not create temp file", e);
		}
	}
}
