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

package org.catrobat.catroid.uiespresso.paintroid.tools;

import android.graphics.Color;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.paintroid.MainActivity;
import org.catrobat.catroid.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.catroid.uiespresso.paintroid.util.BitmapLocationProvider;
import org.catrobat.catroid.uiespresso.paintroid.util.DialogHiddenIdlingResource;
import org.catrobat.catroid.uiespresso.paintroid.util.DrawingSurfaceLocationProvider;
import org.catrobat.catroid.common.paintroid.ScreenshotOnFailRule;
import org.catrobat.catroid.common.paintroid.SystemAnimationsRule;
import org.catrobat.catroid.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.catrobat.catroid.uiespresso.paintroid.util.UiInteractions.touchAt;
import static org.catrobat.catroid.uiespresso.paintroid.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.catroid.uiespresso.paintroid.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.catroid.uiespresso.paintroid.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView;

@RunWith(AndroidJUnit4.class)
public class FlipToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Rule
	public ScreenshotOnFailRule screenshotOnFailRule = new ScreenshotOnFailRule();
	private IdlingResource dialogWait;

	@Before
	public void setUp() {
		dialogWait = new DialogHiddenIdlingResource(IndeterminateProgressDialog.getInstance());
		IdlingRegistry.getInstance().register(dialogWait);

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
	}

	@After
	public void tearDown() {
		IdlingRegistry.getInstance().unregister(dialogWait);
	}

	@Test
	public void testHorizontalFlip() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_MIDDLE)
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		onTransformToolOptionsView()
				.performFlipHorizontal();

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_TOP_MIDDLE)
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE);
	}

	@Test
	public void testVerticalFlip() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		onTransformToolOptionsView()
				.performFlipVertical();

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);
	}
}
