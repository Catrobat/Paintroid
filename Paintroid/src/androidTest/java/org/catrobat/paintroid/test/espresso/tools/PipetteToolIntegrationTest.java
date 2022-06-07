/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.action.GeneralLocation;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.swipe;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerPreviewInteraction.onColorPickerPreview;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;

@RunWith(AndroidJUnit4.class)
public class PipetteToolIntegrationTest {

	@Rule
	public ActivityScenarioRule<MainActivity> launchActivityRule = new ActivityScenarioRule<>(MainActivity.class);

	@Rule
	public ScreenshotOnFailRule screenshotOnFailRule = new ScreenshotOnFailRule();

	@Before
	public void setUp() {
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
	}

	@Test
	public void testOnEmptyBitmapPipetteTools() {
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
	public void testOnEmptyBitmapPipetteColorPicker() {
		onToolProperties()
				.checkMatchesColor(Color.BLACK);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

		onColorPickerView()
				.performOpenColorPicker()
				.perform(swipe(DrawingSurfaceLocationProvider.TOP_MIDDLE,
						DrawingSurfaceLocationProvider.BOTTOM_MIDDLE));
		onColorPickerView()
				.clickPipetteButton();

		onColorPickerPreview()
				.perform(touchAt(GeneralLocation.CENTER));

		onColorPickerPreview()
				.checkColorPreviewColor(Color.TRANSPARENT);
	}

	@Test
	public void testPipetteToolAfterBrushOnSingleLayer() {
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
	public void testPipetteColorPickerAfterBrushOnSingleLayer() {
		onToolProperties()
				.setColor(Color.RED);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE);

		onToolProperties()
				.setColorResource(R.color.pocketpaint_color_picker_transparent)
				.checkMatchesColor(Color.TRANSPARENT);

		onColorPickerView()
				.performOpenColorPicker()
				.perform(swipe(DrawingSurfaceLocationProvider.TOP_MIDDLE,
						DrawingSurfaceLocationProvider.BOTTOM_MIDDLE));
		onColorPickerView()
				.clickPipetteButton();

		onColorPickerPreview()
				.perform(touchAt(GeneralLocation.CENTER));

		onColorPickerPreview()
				.checkColorPreviewColor(Color.RED);
	}

	@Test
	public void testPipetteToolAfterBrushOnMultiLayer() {
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

	@Test
	public void testPipetteColorPickerAfterBrushOnMultiLayer() {
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

		onColorPickerView()
				.performOpenColorPicker()
				.checkCurrentViewColor(Color.TRANSPARENT);

		onColorPickerView()
				.perform(swipe(DrawingSurfaceLocationProvider.TOP_MIDDLE,
						DrawingSurfaceLocationProvider.BOTTOM_MIDDLE));
		onColorPickerView()
				.clickPipetteButton();

		onColorPickerPreview()
				.checkColorPreviewColor(Color.TRANSPARENT);

		onColorPickerPreview()
				.perform(touchAt(GeneralLocation.CENTER));

		onColorPickerPreview()
				.checkColorPreviewColor(Color.BLACK);
	}

	@Test
	public void testPipetteColorPickerAfterBrushOnSingleLayerAcceptColor() {
		onToolProperties()
				.setColor(Color.RED);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE);

		onToolProperties()
				.setColorResource(R.color.pocketpaint_color_picker_transparent)
				.checkMatchesColor(Color.TRANSPARENT);

		onColorPickerView()
				.performOpenColorPicker()
				.perform(swipe(DrawingSurfaceLocationProvider.TOP_MIDDLE,
						DrawingSurfaceLocationProvider.BOTTOM_MIDDLE));
		onColorPickerView()
				.clickPipetteButton();

		onColorPickerPreview()
				.perform(touchAt(GeneralLocation.CENTER));

		onColorPickerPreview()
				.performCloseColorPickerPreviewWithDoneButton();

		onColorPickerView()
				.checkNewColorViewColor(Color.RED);
	}

	@Test
	public void testPipetteColorPickerShowDoneDialog() {
		onToolProperties()
				.setColor(Color.BLACK);

		onColorPickerView()
				.performOpenColorPicker()
				.perform(swipe(DrawingSurfaceLocationProvider.TOP_MIDDLE,
						DrawingSurfaceLocationProvider.BOTTOM_MIDDLE));
		onColorPickerView()
				.clickPipetteButton();

		onColorPickerPreview()
				.perform(touchAt(GeneralLocation.CENTER));

		onColorPickerPreview()
				.assertShowColorPickerPreviewBackDialog();
	}

	@Test
	public void testPipetteColorPickerAfterBrushOnSingleLayerRejectColorWithDoneDialog() {
		onToolProperties()
				.setColor(Color.RED);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE);

		onToolProperties()
				.setColorResource(R.color.pocketpaint_color_picker_transparent)
				.checkMatchesColor(Color.TRANSPARENT);

		onColorPickerView()
				.performOpenColorPicker()
				.perform(swipe(DrawingSurfaceLocationProvider.TOP_MIDDLE,
						DrawingSurfaceLocationProvider.BOTTOM_MIDDLE));
		onColorPickerView()
				.clickPipetteButton();

		onColorPickerPreview()
				.perform(touchAt(GeneralLocation.CENTER));

		onColorPickerPreview()
				.performCloseColorPickerPreviewWithBackButtonDecline();

		onColorPickerView()
				.checkNewColorViewColor(Color.TRANSPARENT);
	}

	@Test
	public void testPipetteColorPickerAfterBrushOnSingleLayerAcceptColorWithDoneDialog() {
		onToolProperties()
				.setColor(Color.RED);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE);

		onToolProperties()
				.setColorResource(R.color.pocketpaint_color_picker_transparent)
				.checkMatchesColor(Color.TRANSPARENT);

		onColorPickerView()
				.performOpenColorPicker()
				.perform(swipe(DrawingSurfaceLocationProvider.TOP_MIDDLE,
						DrawingSurfaceLocationProvider.BOTTOM_MIDDLE));
		onColorPickerView()
				.clickPipetteButton();

		onColorPickerPreview()
				.perform(touchAt(GeneralLocation.CENTER));

		onColorPickerPreview()
				.performCloseColorPickerPreviewWithBackButtonAccept();

		onColorPickerView()
				.checkNewColorViewColor(Color.RED);
	}

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
