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

import android.net.Uri;
import android.os.Environment;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.tools.ToolType;
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
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class OpenedFromPocketCodeWithImageTest {

	private static final String IMAGE_NAME = "testFile";
	private static final String FILE_ENDING = ".png";

	@Rule
	public IntentsTestRule<MainActivity> launchActivityRule = new IntentsTestRule<>(MainActivity.class, false, true);

	@ClassRule
	public static GrantPermissionRule grantPermissionRule = EspressoUtils.grantPermissionRulesVersionCheck();

	private File imageFile = null;

	@Before
	public void setUp() {
		String pathToFile =
				launchActivityRule.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
						+ File.separator
						+ IMAGE_NAME
						+ FILE_ENDING;

		imageFile = new File(pathToFile);
		launchActivityRule.getActivity().model.setSavedPictureUri(Uri.fromFile(imageFile));
		launchActivityRule.getActivity().model.setOpenedFromCatroid(true);

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

		long lastModifiedBefore = imageFile.lastModified();
		long fileSizeBefore = imageFile.length();

		Espresso.pressBackUnconditionally();

		String path = launchActivityRule.getActivityResult().getResultData().getStringExtra(Constants.PAINTROID_PICTURE_PATH);
		assertEquals(imageFile.getAbsolutePath(), path);

		assertThat("Image modification not saved", imageFile.lastModified(), greaterThan(lastModifiedBefore));
		assertThat("Saved image length not changed", imageFile.length(), greaterThan(fileSizeBefore));
	}

	@Test
	public void testBackToPocketCode() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		Espresso.pressBackUnconditionally();

		long lastModifiedBefore = imageFile.lastModified();
		long fileSizeBefore = imageFile.length();

		assertThat("Image modified", imageFile.lastModified(), equalTo(lastModifiedBefore));
		assertThat("Saved image length changed", imageFile.length(), equalTo(fileSizeBefore));
	}
}
