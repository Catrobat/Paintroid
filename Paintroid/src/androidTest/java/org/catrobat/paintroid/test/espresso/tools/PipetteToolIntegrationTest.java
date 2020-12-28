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

package org.catrobat.paintroid.test.espresso.tools;

import android.graphics.Color;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;

@RunWith(AndroidJUnit4.class)
public class PipetteToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Before
	public void setUp() {
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
	}

	@Test
	public void testOnEmptyBitmap() {
		onToolProperties()
				.checkMatchesColor(Color.BLACK);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolProperties()
				.checkMatchesColor(Color.TRANSPARENT);
	}

	@Test
	public void testPipetteAfterBrushOnSingleLayer() {
		onToolProperties()
				.setColor(Color.RED);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE);

		onToolProperties()
				.setColorResource(R.color.pocketpaint_color_picker_transparent)
				.checkMatchesColor(Color.TRANSPARENT);
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolProperties()
				.checkMatchesColor(Color.RED);
	}

	@Test
	public void testPipetteAfterBrushOnMultiLayer() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.performClose();

		onToolProperties()
				.setColor(Color.TRANSPARENT);
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);

		onToolProperties()
				.checkMatchesColor(Color.TRANSPARENT);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolProperties()
				.checkMatchesColor(Color.BLACK);
	}

	@Ignore("Flaky on Jenkins, for further information https://github.com/Catrobat/Paintroid/pull/794")
	@Test
	public void testPipetteAfterUndo() {

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolProperties()
				.checkMatchesColor(Color.BLACK);

		onTopBarView()
				.performUndo();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onToolProperties()
				.checkMatchesColor(Color.TRANSPARENT);
	}

	@Ignore("Flaky on Jenkins, for further information https://github.com/Catrobat/Paintroid/pull/794")
	@Test
	public void testPipetteAfterRedo() {

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onToolProperties()
				.checkMatchesColor(Color.BLACK);

		onTopBarView()
				.performUndo();

		onTopBarView()
				.performRedo();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onToolProperties()
				.checkMatchesColor(Color.BLACK);
	}
}
