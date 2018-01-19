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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ImageButton;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.espresso.util.DialogHiddenIdlingResource;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.tools.implementation.GeometricFillTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.addNewLayer;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.closeLayerMenu;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getWorkingBitmap;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openLayerMenu;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectColorPickerPresetSelectorColor;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectLayer;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TransformToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private PointF pointOnScreenLeft;
	private PointF pointOnScreenRight;
	private PointF pointOnScreenMiddle;
	PointF pointOnTopLeft;
	PointF pointOnBottomLeft;
	PointF pointOnBottomRight;
	PointF pointOnTopRight;
	private ActivityHelper activityHelper;
	private IdlingResource dialogWait;

	@Before
	public void setUp() {
		dialogWait = new DialogHiddenIdlingResource(IndeterminateProgressDialog.getInstance());
		Espresso.registerIdlingResources(dialogWait);

		activityHelper = new ActivityHelper(launchActivityRule.getActivity());

		int displayWidth = activityHelper.getDisplayWidth();
		int displayHeight = activityHelper.getDisplayHeight();
		pointOnScreenLeft = new PointF(displayWidth * 0.25f, displayHeight * 0.5f);
		pointOnScreenRight = new PointF(displayWidth * 0.75f, displayHeight * 0.5f);
		pointOnScreenMiddle = new PointF(displayWidth * 0.5f, displayHeight * 0.5f);
		pointOnTopLeft = new PointF(displayWidth * 0.25f, displayHeight * 0.15f);
		pointOnBottomLeft = new PointF(displayWidth * 0.15f, displayHeight * 0.60f);
		pointOnBottomRight = new PointF(displayWidth * 0.75f, displayHeight * 0.85f);
		pointOnTopRight = new PointF(displayWidth * 0.75f, displayHeight * 0.40f);
	}

	@After
	public void tearDown() {
		Espresso.unregisterIdlingResources(dialogWait);

		activityHelper = null;
	}

	@Test
	public void testAutoCrop() {
		onView(isRoot()).perform(touchAt(pointOnScreenMiddle));
		selectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_auto_crop_btn)).perform(click());

		float originalWidth = getWorkingBitmap().getWidth();
		float originalHeight = getWorkingBitmap().getHeight();
		float boxWidth = getBoxWidth();
		float boxHeight = getBoxHeight();
		assertTrue("Box width should get smaller", boxWidth < originalWidth);
		assertTrue("Box height should get smaller", boxHeight < originalHeight);
	}

	@Test
	public void testAutoCropOnEmptyBitmap() {
		selectTool(ToolType.TRANSFORM);

		float originalWidth = getWorkingBitmap().getWidth();
		float originalHeight = getWorkingBitmap().getHeight();
		PointF originalPosition = getToolPosition();

		onView(withId(R.id.transform_auto_crop_btn)).perform(click());

		float boxWidth = getBoxWidth();
		float boxHeight = getBoxHeight();
		PointF boxPosition = getToolPosition();

		assertEquals("Box width should not have changed", originalWidth, boxWidth, Double.MIN_VALUE);
		assertEquals("Box height should not have changed", originalHeight, boxHeight, Double.MIN_VALUE);
		assertEquals("Box position should not have changed", originalPosition, boxPosition);
	}

	@Test
	public void testAutoCropOnFilledBitmap() {
		selectTool(ToolType.FILL);
		onView(isRoot()).perform(touchAt(pointOnScreenMiddle));
		selectTool(ToolType.TRANSFORM);

		float originalWidth = getWorkingBitmap().getWidth();
		float originalHeight = getWorkingBitmap().getHeight();
		PointF originalPosition = getToolPosition();

		onView(withId(R.id.transform_auto_crop_btn)).perform(click());

		float boxWidth = getBoxWidth();
		float boxHeight = getBoxHeight();
		PointF boxPosition = getToolPosition();

		assertEquals("Box width should not have changed", originalWidth, boxWidth, Double.MIN_VALUE);
		assertEquals("Box height should not have changed", originalHeight, boxHeight, Double.MIN_VALUE);
		assertEquals("Box position should not have changed", originalPosition, boxPosition);
	}

	@Test
	public void testRotateMultipleLayers() {
		ArrayList<Layer> layers = LayerListener.getInstance().getAdapter().getLayers();
		int bitmapHeightOnStartup = layers.get(0).getImage().getHeight();
		int bitmapWidthOnStartup = layers.get(0).getImage().getWidth();

		openLayerMenu();
		onView(withId(R.id.layer_side_nav_button_add)).perform(click());
		onView(withId(R.id.layer_side_nav_button_add)).perform(click());
		closeLayerMenu();

		selectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_right_btn)).perform(click());

		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after rotating", bitmapHeightOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after rotating", bitmapWidthOnStartup, layer.getImage().getHeight());
		}

		onView(withId(R.id.transform_rotate_left_btn)).perform(click());

		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after rotating", bitmapWidthOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after rotating", bitmapHeightOnStartup, layer.getImage().getHeight());
		}
	}

	@Test
	public void testRotateMultipleLayersUndoRedo() {
		ArrayList<Layer> layers = LayerListener.getInstance().getAdapter().getLayers();
		int bitmapHeightOnStartup = layers.get(0).getImage().getHeight();
		int bitmapWidthOnStartup = layers.get(0).getImage().getWidth();

		openLayerMenu();
		addNewLayer();
		addNewLayer();
		closeLayerMenu();

		selectTool(ToolType.TRANSFORM);

		onView(withId(R.id.transform_rotate_right_btn)).perform(click());
		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after rotating", bitmapHeightOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after rotating", bitmapWidthOnStartup, layer.getImage().getHeight());
		}

		EspressoUtils.clickSelectedToolButton();
		onView(withId(R.id.btn_top_undo)).perform(click());
		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after undo rotating", bitmapWidthOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after undo rotating", bitmapHeightOnStartup, layer.getImage().getHeight());
		}

		onView(withId(R.id.btn_top_redo)).perform(click());
		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after redo rotating", bitmapHeightOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after redo rotating", bitmapWidthOnStartup, layer.getImage().getHeight());
		}
	}

	@Test
	public void testRotateLeft() throws NoSuchFieldException, IllegalAccessException {
		selectTool(ToolType.SHAPE);
		onView(withId(R.id.tools_rectangle)).perform(click());
		selectColorPickerPresetSelectorColor(2);
		GeometricFillTool geometricFillTool = (GeometricFillTool) PaintroidApplication.currentTool;
		geometricFillTool.toolPosition.set(pointOnTopLeft);
		onView(isRoot()).perform(touchAt(pointOnTopLeft));

		selectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_left_btn)).perform(click());
		onView(withId(R.id.tools_transform)).perform(click());
		selectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(pointOnBottomLeft));
		int toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be green", -16283897, toolColor);

		selectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_left_btn)).perform(click());
		onView(withId(R.id.tools_transform)).perform(click());
		selectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(pointOnBottomRight));
		toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be green", -16283897, toolColor);

		selectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_left_btn)).perform(click());
		onView(withId(R.id.tools_transform)).perform(click());
		selectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(pointOnTopRight));
		toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be green", -16283897, toolColor);

		selectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_left_btn)).perform(click());
		onView(withId(R.id.tools_transform)).perform(click());
		selectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(pointOnTopLeft));
		toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be green", -16283897, toolColor);
	}

	@Test
	public void testRotateRight() throws NoSuchFieldException, IllegalAccessException {
		selectTool(ToolType.SHAPE);
		onView(withId(R.id.tools_rectangle)).perform(click());
		selectColorPickerPresetSelectorColor(2);
		GeometricFillTool geometricFillTool = (GeometricFillTool) PaintroidApplication.currentTool;
		geometricFillTool.toolPosition.set(pointOnTopLeft);
		onView(isRoot()).perform(touchAt(pointOnTopLeft));

		selectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_right_btn)).perform(click());
		onView(withId(R.id.tools_transform)).perform(click());
		selectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(pointOnTopRight));
		int toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be green", -16283897, toolColor);

		selectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_right_btn)).perform(click());
		onView(withId(R.id.tools_transform)).perform(click());
		selectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(pointOnBottomRight));
		toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be green", -16283897, toolColor);

		selectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_right_btn)).perform(click());
		onView(withId(R.id.tools_transform)).perform(click());
		selectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(pointOnBottomLeft));
		toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be green", -16283897, toolColor);

		selectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_right_btn)).perform(click());
		onView(withId(R.id.tools_transform)).perform(click());
		selectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(pointOnTopLeft));
		toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be green", -16283897, toolColor);
	}

	@Test
	public void testRotateMultipleColors() throws NoSuchFieldException, IllegalAccessException {
		selectTool(ToolType.SHAPE);
		onView(withId(R.id.tools_rectangle)).perform(click());
		GeometricFillTool geometricFillTool = (GeometricFillTool) PaintroidApplication.currentTool;
		geometricFillTool.toolPosition.set(pointOnTopLeft);
		onView(isRoot()).perform(touchAt(pointOnTopLeft));
		selectColorPickerPresetSelectorColor(2);
		geometricFillTool.toolPosition.set(pointOnBottomRight);
		onView(isRoot()).perform(touchAt(pointOnBottomRight));

		selectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_right_btn)).perform(click());
		onView(withId(R.id.tools_transform)).perform(click());
		selectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(pointOnTopRight));
		int toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be black", Color.BLACK, toolColor);
		onView(isRoot()).perform(touchAt(pointOnBottomLeft));
		toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be green", -16283897, toolColor);

		selectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_right_btn)).perform(click());
		onView(withId(R.id.tools_transform)).perform(click());
		selectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(pointOnBottomRight));
		toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be black", Color.BLACK, toolColor);
		onView(isRoot()).perform(touchAt(pointOnTopLeft));
		toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be green", -16283897, toolColor);

		selectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_right_btn)).perform(click());
		onView(withId(R.id.tools_transform)).perform(click());
		selectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(pointOnBottomLeft));
		toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be black", Color.BLACK, toolColor);
		onView(isRoot()).perform(touchAt(pointOnTopRight));
		toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be green", -16283897, toolColor);

		selectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_right_btn)).perform(click());
		onView(withId(R.id.tools_transform)).perform(click());
		selectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(pointOnTopLeft));
		toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be black", Color.BLACK, toolColor);
		onView(isRoot()).perform(touchAt(pointOnBottomRight));
		toolColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Tool color should be green", -16283897, toolColor);
	}

	@Ignore("Enable with PAINT-192")
	@Test
	public void testRotateMultipleLayersUndoRedoWhenRotatingWasNotLastCommand() {
		ImageButton undoButton = launchActivityRule.getActivity().topBar.getUndoButton();
		Bitmap undoButtonDisabled = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();
		ImageButton redoButton = launchActivityRule.getActivity().topBar.getRedoButton();
		Bitmap redoButtonDisabled = ((BitmapDrawable) redoButton.getDrawable()).getBitmap();

		ArrayList<Layer> layers = LayerListener.getInstance().getAdapter().getLayers();
		int bitmapHeightOnStartup = layers.get(0).getImage().getHeight();
		int bitmapWidthOnStartup = layers.get(0).getImage().getWidth();

		openLayerMenu();
		addNewLayer();
		addNewLayer();
		closeLayerMenu();

		selectTool(ToolType.TRANSFORM);

		onView(withId(R.id.transform_rotate_right_btn)).perform(click());
		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after rotating", bitmapHeightOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after rotating", bitmapWidthOnStartup, layer.getImage().getHeight());
		}

		selectTool(ToolType.BRUSH);
		onView(isRoot()).perform(touchAt(pointOnScreenLeft));
		onView(isRoot()).perform(touchAt(pointOnScreenRight));

		openLayerMenu();
		int layerListPositionForUndoRotation = 1;
		selectLayer(layerListPositionForUndoRotation);
		closeLayerMenu();

		onView(withId(R.id.btn_top_undo)).perform(click());
		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after undo rotating", bitmapWidthOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after undo rotating", bitmapHeightOnStartup, layer.getImage().getHeight());
		}

		assertTrue("Undo button should be disabled", undoButtonDisabled.sameAs(((BitmapDrawable) undoButton.getDrawable()).getBitmap()));
		assertFalse("Redo button should be enabled", redoButtonDisabled.sameAs(((BitmapDrawable) redoButton.getDrawable()).getBitmap()));
		openLayerMenu();
		selectLayer(0);
		assertFalse("Undo button should be enabled", undoButtonDisabled.sameAs(((BitmapDrawable) undoButton.getDrawable()).getBitmap()));
		assertTrue("Redo button should be disabled", redoButtonDisabled.sameAs(((BitmapDrawable) redoButton.getDrawable()).getBitmap()));

		int layerListPositionForDeletingRedoCommands = 1;
		selectLayer(layerListPositionForDeletingRedoCommands);
		closeLayerMenu();
		onView(isRoot()).perform(touchAt(pointOnScreenMiddle));

		int layerListPositionForRedoRotation = 2;
		openLayerMenu();
		selectLayer(layerListPositionForRedoRotation);
		closeLayerMenu();

		onView(withId(R.id.btn_top_redo)).perform(click());
		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after redo rotating", bitmapHeightOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after redo rotating", bitmapWidthOnStartup, layer.getImage().getHeight());
		}
	}

	private float getBoxWidth() {
		return ((BaseToolWithRectangleShape) PaintroidApplication.currentTool).boxWidth;
	}

	private float getBoxHeight() {
		return ((BaseToolWithRectangleShape) PaintroidApplication.currentTool).boxHeight;
	}

	private PointF getToolPosition() {
		return ((BaseToolWithShape) PaintroidApplication.currentTool).toolPosition;
	}
}
