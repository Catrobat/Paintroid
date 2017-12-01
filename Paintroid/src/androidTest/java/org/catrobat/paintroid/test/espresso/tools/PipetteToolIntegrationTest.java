/**
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
import android.graphics.PointF;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.TRANSPARENT_COLOR_PICKER_BUTTON_POSITION;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getCanvasPointFromScreenPoint;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetColorPicker;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetDrawPaintAndBrushPickerView;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectColorPickerPresetSelectorColor;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PipetteToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private ActivityHelper activityHelper;
	private PointF screenPoint;
	private PointF canvasPoint;

	@Before
	public void setUp() {
		activityHelper = new ActivityHelper(launchActivityRule.getActivity());

		PaintroidApplication.drawingSurface.destroyDrawingCache();

		screenPoint = new PointF(activityHelper.getDisplayWidth() / 2, activityHelper.getDisplayHeight() / 2);
		canvasPoint = getCanvasPointFromScreenPoint(screenPoint);

		selectTool(ToolType.BRUSH);
		resetColorPicker();
		resetDrawPaintAndBrushPickerView();
	}

	@After
	public void tearDown() {
		screenPoint = null;
		canvasPoint = null;
		activityHelper = null;
	}

	@Test
	public void testEmpty() {
		int toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Initial Tool color should be black", Color.BLACK, toolColor);

		int colorAtCanvas = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Get transparent background color", Color.TRANSPARENT, colorAtCanvas);

		selectTool(ToolType.PIPETTE);

		onView(isRoot()).perform(touchAt(screenPoint));

		toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be transparent", colorAtCanvas, toolColor);
	}

	@Test
	public void testPipetteAfterBrushOnSingleLayer() {

		onView(isRoot()).perform(touchAt(screenPoint));

		int colorAtCanvas = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorAtCanvas);

		selectColorPickerPresetSelectorColor(TRANSPARENT_COLOR_PICKER_BUTTON_POSITION);
		selectTool(ToolType.PIPETTE);

		onView(isRoot()).perform(touchAt(screenPoint));

		int pipetteColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be black", colorAtCanvas, pipetteColor);
	}

	@Test
	public void testPipetteAfterBrushOnMultiLayer() {
		onView(isRoot()).perform(touchAt(screenPoint));

		int colorAtFirstLayer = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorAtFirstLayer);

		onView(withId(R.id.btn_top_layers)).perform(click());
		onView(withId(R.id.layer_side_nav_button_add)).perform(click());

		int colorAtSecondLayer = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Background color should be transparent on new layer", Color.TRANSPARENT, colorAtSecondLayer);

		onView(isRoot()).perform(click());
		selectColorPickerPresetSelectorColor(TRANSPARENT_COLOR_PICKER_BUTTON_POSITION);
		selectTool(ToolType.PIPETTE);

		onView(isRoot()).perform(touchAt(screenPoint));

		int pipetteColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be black", colorAtFirstLayer, pipetteColor);
	}

	@Test
	public void testPipetteAfterUndo() {
		onView(isRoot())
				.perform(touchAt(screenPoint));

		selectTool(ToolType.PIPETTE);
		onView(isRoot())
				.perform(touchAt(screenPoint));
		int pipetteColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals(Color.BLACK, pipetteColor);

		onView(withId(R.id.btn_top_undo))
				.perform(click());

		onView(isRoot())
				.perform(touchAt(screenPoint));
		int newPipetteColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals(Color.TRANSPARENT, newPipetteColor);
	}

	@Test
	public void testPipetteAfterRedo() {
		onView(isRoot())
				.perform(touchAt(screenPoint));

		selectTool(ToolType.PIPETTE);
		onView(isRoot())
				.perform(touchAt(screenPoint));
		int pipetteColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals(Color.BLACK, pipetteColor);

		onView(withId(R.id.btn_top_undo))
				.perform(click());
		onView(withId(R.id.btn_top_redo))
				.perform(click());

		onView(isRoot())
				.perform(touchAt(screenPoint));
		int newPipetteColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals(pipetteColor, newPipetteColor);
	}
}
