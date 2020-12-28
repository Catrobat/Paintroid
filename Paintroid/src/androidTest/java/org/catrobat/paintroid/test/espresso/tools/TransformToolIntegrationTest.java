/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.tools;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.widget.SeekBar;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper;
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.tools.implementation.TransformTool;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.PerformException;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.waitForToast;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.swipe;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class TransformToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public ScreenshotOnFailRule screenshotOnFailRule = new ScreenshotOnFailRule();

	private int displayWidth;
	private int displayHeight;

	private int initialWidth;
	private int initialHeight;
	private int maxBitmapSize;

	private Perspective perspective;
	private LayerContracts.Model layerModel;
	private ToolReference toolReference;
	private MainActivity mainActivity;
	private MainActivityHelper activityHelper;

	private static void drawPlus(Bitmap bitmap, int lineLength) {
		int horizontalStartX = bitmap.getWidth() / 4;
		int horizontalStartY = bitmap.getHeight() / 2;
		int verticalStartX = bitmap.getWidth() / 2;
		int verticalStartY = bitmap.getHeight() / 2 - lineLength / 2;

		int[] pixelsColorArray = new int[10 * lineLength];
		for (int indexColorArray = 0; indexColorArray < pixelsColorArray.length; indexColorArray++) {
			pixelsColorArray[indexColorArray] = Color.BLACK;
		}

		bitmap.setPixels(pixelsColorArray, 0, lineLength, horizontalStartX,
				horizontalStartY, lineLength, 10);

		bitmap.setPixels(pixelsColorArray, 0, 10, verticalStartX,
				verticalStartY, 10, lineLength);
	}

	private PointF getSurfacePointFromCanvasPoint(PointF point) {
		return perspective.getSurfacePointFromCanvasPoint(point);
	}

	private float getToolSelectionBoxWidth() {
		return ((BaseToolWithRectangleShape) toolReference.get()).boxWidth;
	}

	private void setToolSelectionBoxWidth(float width) {
		((BaseToolWithRectangleShape) toolReference.get()).boxWidth = width;
	}

	private float getToolSelectionBoxHeight() {
		return ((BaseToolWithRectangleShape) toolReference.get()).boxHeight;
	}

	private void setToolSelectionBoxHeight(float height) {
		((BaseToolWithRectangleShape) toolReference.get()).boxHeight = height;
	}

	private void setToolSelectionBoxDimensions(float width, float height) {
		BaseToolWithRectangleShape currentTool = (BaseToolWithRectangleShape)
				toolReference.get();
		currentTool.boxWidth = width;
		currentTool.boxHeight = height;
	}

	private PointF getToolPosition() {
		return ((BaseToolWithShape) toolReference.get()).toolPosition;
	}

	private void setToolPosition(float x, float y) {
		((BaseToolWithShape) toolReference.get()).toolPosition.set(x, y);
	}

	private PointF newPointF(PointF point) {
		return new PointF(point.x, point.y);
	}

	@Before
	public void setUp() {
		mainActivity = launchActivityRule.getActivity();
		activityHelper = new MainActivityHelper(mainActivity);
		perspective = mainActivity.perspective;
		layerModel = mainActivity.layerModel;
		toolReference = mainActivity.toolReference;

		displayWidth = activityHelper.getDisplayWidth();
		displayHeight = activityHelper.getDisplayHeight();

		maxBitmapSize = displayHeight * displayWidth
				* (int) TransformTool.MAXIMUM_BITMAP_SIZE_FACTOR;

		final Bitmap workingBitmap = layerModel.getCurrentLayer().getBitmap();
		initialWidth = workingBitmap.getWidth();
		initialHeight = workingBitmap.getHeight();

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
	}

	@Test
	public void testAutoCrop() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		onTransformToolOptionsView()
				.performAutoCrop();

		assertThat(getToolSelectionBoxWidth(), lessThan((float) initialWidth));
		assertThat(getToolSelectionBoxHeight(), lessThan((float) initialHeight));
	}

	@Test(expected = PerformException.class)
	public void testToolsClosedAfterAutoCrop() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		onTransformToolOptionsView()
				.performAutoCrop();

		onTransformToolOptionsView().performAutoCrop();
	}

	@Test
	public void testAutoCropOnEmptyBitmap() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		PointF position = newPointF(getToolPosition());

		onTransformToolOptionsView()
				.performAutoCrop();

		assertEquals(initialWidth, getToolSelectionBoxWidth(), Float.MIN_VALUE);
		assertEquals(initialHeight, getToolSelectionBoxHeight(), Float.MIN_VALUE);
		assertEquals(position, getToolPosition());

		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, initialHeight)
				.checkLayerDimensions(initialWidth, initialHeight);
	}

	@Test
	public void testAutoCropOnFilledBitmap() {
		onToolBarView()
				.performSelectTool(ToolType.FILL);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		int width = layerModel.getCurrentLayer().getBitmap().getWidth();
		int height = layerModel.getCurrentLayer().getBitmap().getHeight();
		PointF position = newPointF(getToolPosition());

		onTransformToolOptionsView()
				.performAutoCrop();

		assertEquals(width, getToolSelectionBoxWidth(), Float.MIN_VALUE);
		assertEquals(height, getToolSelectionBoxHeight(), Float.MIN_VALUE);
		assertEquals(position, getToolPosition());

		onDrawingSurfaceView()
				.checkBitmapDimension(width, height)
				.checkLayerDimensions(width, height);
	}

	@Test
	public void testToolsMenuClosedOnApply() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.moveSliderTo(50);
		onTransformToolOptionsView()
				.performApplyResize();

		onTransformToolOptionsView()
				.checkIsNotDisplayed();
	}

	@Test
	public void testAutoTextIsShown() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.checkAutoDisplayed();
	}

	@Test
	public void testWhenNoPixelIsOnBitmap() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptionsView();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, initialHeight)
				.checkLayerDimensions(initialWidth, initialHeight);
	}

	@LargeTest
	@Test
	public void testWhenNoPixelIsOnBitmapToasts() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptionsView();

		waitForToast(withText(R.string.transform_info_text), 1000);

		onTopBarView()
				.performClickCheckmark();

		waitForToast(withText(R.string.resize_nothing_to_resize), 1000);
	}

	@Test
	public void testChangeCroppingHeightAndCheckWidth() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptionsView();

		float boundingBoxWidth = getToolSelectionBoxWidth();
		float boundingBoxHeight = getToolSelectionBoxHeight();

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.BOTTOM_MIDDLE,
						DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE));

		assertEquals(boundingBoxWidth, getToolSelectionBoxWidth(), Float.MIN_VALUE);
		assertThat(boundingBoxHeight, greaterThan(getToolSelectionBoxHeight()));
	}

	@Test
	public void testMoveCroppingBordersOnEmptyBitmapAndDoCrop() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptionsView();

		int width = initialWidth / 2;
		int height = initialHeight / 2;
		setToolSelectionBoxDimensions(width, height);

		onTopBarView()
				.performClickCheckmark();

		onDrawingSurfaceView()
				.checkBitmapDimension(width, height)
				.checkLayerDimensions(width, height);
	}

	@Test
	public void testIfOnePixelIsFound() {
		final Bitmap workingBitmap = layerModel.getCurrentLayer().getBitmap();
		workingBitmap.setPixel(initialWidth / 2, initialHeight / 2, Color.BLACK);
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();

		assertEquals(1, getToolSelectionBoxWidth(), Float.MIN_VALUE);
		assertEquals(1, getToolSelectionBoxHeight(), Float.MIN_VALUE);

		onTopBarView()
				.performClickCheckmark();
		onDrawingSurfaceView()
				.checkBitmapDimension(1, 1);
	}

	@Test
	public void testIfMultiplePixelAreFound() {
		final Bitmap workingBitmap = layerModel.getCurrentLayer().getBitmap();
		workingBitmap.setPixel(1, 1, Color.BLACK);
		workingBitmap.setPixel(initialWidth - 1, initialHeight - 1, Color.BLACK);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();

		onTopBarView()
				.performClickCheckmark();

		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth - 1, initialHeight - 1);
	}

	@Test
	public void testIfDrawingSurfaceBoundsAreFoundAndNotCropped() {
		final Bitmap workingBitmap = layerModel.getCurrentLayer().getBitmap();

		workingBitmap.setPixel(initialWidth / 2, 0, Color.BLACK);
		workingBitmap.setPixel(0, initialHeight / 2, Color.BLACK);
		workingBitmap.setPixel(initialWidth - 1, initialHeight / 2, Color.BLACK);
		workingBitmap.setPixel(initialWidth / 2, initialHeight - 1, Color.BLACK);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, initialHeight);
	}

	@Test
	public void testIfClickOnCanvasCrops() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		Bitmap workingBitmap = layerModel.getCurrentLayer().getBitmap();
		workingBitmap.eraseColor(Color.BLACK);

		for (int indexWidth = 0; indexWidth < initialWidth; indexWidth++) {
			workingBitmap.setPixel(indexWidth, 0, Color.TRANSPARENT);
		}

		onTransformToolOptionsView()
				.performAutoCrop();
		onTopBarView()
				.performClickCheckmark();
		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, --initialHeight);

		workingBitmap = layerModel.getCurrentLayer().getBitmap();

		for (int indexWidth = 0; indexWidth < initialWidth; indexWidth++) {
			workingBitmap.setPixel(indexWidth, initialHeight - 1, Color.TRANSPARENT);
		}

		onToolBarView()
				.performOpenToolOptionsView();
		onTransformToolOptionsView()
				.performAutoCrop();
		onTopBarView()
				.performClickCheckmark();
		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, --initialHeight);

		workingBitmap = layerModel.getCurrentLayer().getBitmap();

		for (int indexHeight = 0; indexHeight < initialHeight; indexHeight++) {
			workingBitmap.setPixel(0, indexHeight, Color.TRANSPARENT);
		}

		onToolBarView()
				.performOpenToolOptionsView();
		onTransformToolOptionsView()
				.performAutoCrop();
		onTopBarView()
				.performClickCheckmark();
		onDrawingSurfaceView()
				.checkBitmapDimension(--initialWidth, initialHeight);
		workingBitmap = layerModel.getCurrentLayer().getBitmap();

		for (int indexHeight = 0; indexHeight < initialHeight; indexHeight++) {
			workingBitmap.setPixel(initialWidth - 1, indexHeight, Color.TRANSPARENT);
		}

		onToolBarView()
				.performOpenToolOptionsView();
		onTransformToolOptionsView()
				.performAutoCrop();
		onTopBarView()
				.performClickCheckmark();
		onDrawingSurfaceView()
				.checkBitmapDimension(--initialWidth, initialHeight);
	}

	@Test
	public void testSmallBitmapResizing() {
		Bitmap workingBitmap = layerModel.getCurrentLayer().getBitmap();

		workingBitmap.setPixel(initialWidth / 2, initialHeight / 2, Color.BLACK);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();

		onTopBarView()
				.performClickCheckmark();
		onDrawingSurfaceView()
				.checkBitmapDimension(1, 1);

		setToolSelectionBoxDimensions(initialWidth, initialHeight);
		onTopBarView()
				.performClickCheckmark();
		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, initialHeight);
	}

	@Test
	public void testCenterBitmapAfterCropAndUndo() {
		drawPlus(layerModel.getCurrentLayer().getBitmap(), initialWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onTopBarView()
				.performClickCheckmark();

		final Bitmap croppedBitmap = layerModel.getCurrentLayer().getBitmap();

		assertThat(initialHeight, greaterThan(croppedBitmap.getHeight()));
		assertThat(initialWidth, greaterThan(croppedBitmap.getWidth()));

		onTopBarView()
				.performUndo();

		Bitmap undoBitmap = layerModel.getCurrentLayer().getBitmap();
		assertEquals("undoBitmap.getHeight should be initialHeight", undoBitmap.getHeight(), initialHeight);
		assertEquals("undoBitmap.getWidth should be initialWidth", undoBitmap.getWidth(), initialWidth);
	}

	@Test
	public void testCenterBitmapAfterCropDrawingOnTopRight() {
		final PointF originalTopLeft = getSurfacePointFromCanvasPoint(new PointF(0, 0));
		final PointF originalBottomRight = getSurfacePointFromCanvasPoint(
				new PointF(initialWidth - 1, initialHeight - 1));

		final Bitmap workingBitmap = layerModel.getCurrentLayer().getBitmap();
		final int lineWidth = 10;
		final int lineHeight = initialHeight / 2;
		final int verticalStartX = (initialWidth - lineWidth);
		final int verticalStartY = 10;

		int[] pixelsColorArray = new int[lineWidth * lineHeight];
		for (int indexColorArray = 0; indexColorArray < pixelsColorArray.length; indexColorArray++) {
			pixelsColorArray[indexColorArray] = Color.BLACK;
		}

		workingBitmap.setPixels(pixelsColorArray, 0, lineWidth,
				verticalStartX, verticalStartY, lineWidth, lineHeight);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onTopBarView()
				.performClickCheckmark();

		final Bitmap croppedBitmap = layerModel.getCurrentLayer().getBitmap();
		final PointF topLeft = getSurfacePointFromCanvasPoint(new PointF(0, 0));
		final PointF bottomRight = getSurfacePointFromCanvasPoint(
				new PointF(croppedBitmap.getWidth() - 1, croppedBitmap.getHeight() - 1));

		onDrawingSurfaceView()
				.checkBitmapDimension(lineWidth, lineHeight);

		final PointF centerOfScreen = new PointF(displayWidth / 2, displayHeight / 2);
		assertThat(topLeft.x, greaterThan(originalTopLeft.x));
		assertThat(topLeft.y, greaterThan(originalTopLeft.y));
		assertThat(bottomRight.x, lessThan(originalBottomRight.x));
		assertThat(bottomRight.y, lessThan(originalBottomRight.y));

		assertThat(topLeft.x, lessThan(centerOfScreen.x));
		assertThat(topLeft.y, lessThan(centerOfScreen.y));
		assertThat(bottomRight.x, greaterThan(centerOfScreen.x));
		assertThat(bottomRight.y, greaterThan(centerOfScreen.y));
	}

	@Test
	public void testIfBordersAreAlignedCorrectAfterCrop() {
		drawPlus(layerModel.getCurrentLayer().getBitmap(), initialWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		final Bitmap croppedBitmap = layerModel.getCurrentLayer().getBitmap();
		final int width = croppedBitmap.getWidth();
		final int height = croppedBitmap.getHeight();
		final TransformTool tool = (TransformTool) toolReference.get();
		assertEquals(0.0f, tool.resizeBoundWidthXLeft, Float.MIN_VALUE);
		assertEquals(width - 1, tool.resizeBoundWidthXRight, Float.MIN_VALUE);
		assertEquals(0.0f, tool.resizeBoundHeightYTop, Float.MIN_VALUE);
		assertEquals(height - 1, tool.resizeBoundHeightYBottom, Float.MIN_VALUE);
	}

	@Test
	public void testMoveLeftCroppingBorderAndDoCrop() {
		drawPlus(layerModel.getCurrentLayer().getBitmap(), initialWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onTopBarView()
				.performClickCheckmark();

		final int height = layerModel.getCurrentLayer().getBitmap().getHeight();
		final PointF toolPosition = getToolPosition();

		for (int i = 0; i < 4; i++) {
			final float newSelectionBoxWidth = getToolSelectionBoxWidth() / 2;
			setToolSelectionBoxWidth(newSelectionBoxWidth);
			toolPosition.x += newSelectionBoxWidth / 2;

			onTopBarView()
					.performClickCheckmark();

			onDrawingSurfaceView()
					.checkBitmapDimension((int) (newSelectionBoxWidth + .5f), height);
		}
	}

	@Test
	public void testMoveRightCroppingBorderAndDoCrop() {
		drawPlus(layerModel.getCurrentLayer().getBitmap(), initialWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onTopBarView()
				.performClickCheckmark();

		final int height = layerModel.getCurrentLayer().getBitmap().getHeight();
		final PointF toolPosition = getToolPosition();

		for (int i = 0; i < 4; i++) {
			final float newSelectionBoxWidth = getToolSelectionBoxWidth() / 2;
			setToolSelectionBoxWidth(newSelectionBoxWidth);
			toolPosition.x -= newSelectionBoxWidth / 2;

			onTopBarView()
					.performClickCheckmark();

			onDrawingSurfaceView()
					.checkBitmapDimension((int) newSelectionBoxWidth, height);
		}
	}

	@Test
	public void testMoveTopCroppingBorderAndDoCrop() {
		drawPlus(layerModel.getCurrentLayer().getBitmap(), initialWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onTopBarView()
				.performClickCheckmark();

		final int width = layerModel.getCurrentLayer().getBitmap().getWidth();
		final PointF toolPosition = getToolPosition();

		for (int i = 0; i < 4; i++) {
			final float newSelectionBoxHeight = getToolSelectionBoxHeight() / 2;
			setToolSelectionBoxHeight(newSelectionBoxHeight);
			toolPosition.y += newSelectionBoxHeight / 2;

			onTopBarView()
					.performClickCheckmark();

			onDrawingSurfaceView()
					.checkBitmapDimension(width, (int) (newSelectionBoxHeight + .5f));
		}
	}

	@Test
	public void testMoveBottomCroppingBorderAndDoCrop() {
		drawPlus(layerModel.getCurrentLayer().getBitmap(), initialWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onTopBarView()
				.performClickCheckmark();

		final int width = layerModel.getCurrentLayer().getBitmap().getWidth();
		final PointF toolPosition = getToolPosition();

		for (int i = 0; i < 4; i++) {
			final float newSelectionBoxHeight = getToolSelectionBoxHeight() / 2;
			setToolSelectionBoxHeight(newSelectionBoxHeight);
			toolPosition.y += newSelectionBoxHeight / 2;

			onTopBarView()
					.performClickCheckmark();

			onDrawingSurfaceView()
					.checkBitmapDimension(width, (int) (newSelectionBoxHeight + .5f));
		}
	}

	@Test
	public void testCropFromEverySideOnFilledBitmap() {
		onToolBarView()
				.performSelectTool(ToolType.FILL);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, initialHeight);

		int width = initialWidth;
		int height = initialHeight;

		int cropSize = initialWidth / 8;
		width -= cropSize;
		layerModel.getCurrentLayer().getBitmap().setPixels(new int[cropSize * height],
				0, cropSize, 0, 0, cropSize, height);

		onToolBarView()
				.performOpenToolOptionsView();
		onTransformToolOptionsView()
				.performAutoCrop();
		onTopBarView()
				.performClickCheckmark();
		onDrawingSurfaceView()
				.checkBitmapDimension(width, height);

		cropSize = initialHeight / 8;
		height -= cropSize;
		layerModel.getCurrentLayer().getBitmap().setPixels(new int[cropSize * width],
				0, width, 0, 0, width, cropSize);

		onToolBarView()
				.performOpenToolOptionsView();
		onTransformToolOptionsView()
				.performAutoCrop();
		onTopBarView()
				.performClickCheckmark();
		onDrawingSurfaceView()
				.checkBitmapDimension(width, height);

		cropSize = initialWidth / 8;
		width -= cropSize;
		layerModel.getCurrentLayer().getBitmap().setPixels(new int[cropSize * height],
				0, cropSize, width, 0, cropSize, height);

		onToolBarView()
				.performOpenToolOptionsView();
		onTransformToolOptionsView()
				.performAutoCrop();
		onTopBarView()
				.performClickCheckmark();
		onDrawingSurfaceView()
				.checkBitmapDimension(width, height);

		cropSize = initialHeight / 8;
		height -= cropSize;
		layerModel.getCurrentLayer().getBitmap().setPixels(new int[cropSize * width],
				0, width, 0, height, width, cropSize);

		onToolBarView()
				.performOpenToolOptionsView();
		onTransformToolOptionsView()
				.performAutoCrop();
		onTopBarView()
				.performClickCheckmark();
		onDrawingSurfaceView()
				.checkBitmapDimension(width, height);
	}

	@Test
	public void testResizeBordersMatchBitmapBordersAfterCrop() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptionsView();

		drawPlus(layerModel.getCurrentLayer().getBitmap(), initialWidth / 2);

		setToolSelectionBoxDimensions(initialWidth / 8, initialHeight / 8);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		final Bitmap croppedBitmap = layerModel.getCurrentLayer().getBitmap();
		final int height = croppedBitmap.getHeight();
		final int width = croppedBitmap.getWidth();

		final TransformTool tool = (TransformTool) toolReference.get();
		assertEquals(0.0f, tool.resizeBoundWidthXLeft, Float.MIN_VALUE);
		assertEquals(width - 1, tool.resizeBoundWidthXRight, Float.MIN_VALUE);
		assertEquals(0.0f, tool.resizeBoundHeightYTop, Float.MIN_VALUE);
		assertEquals(height - 1, tool.resizeBoundHeightYBottom, Float.MIN_VALUE);
	}

	@Test
	public void testMaxImageResolution() {
		final int maxWidth = maxBitmapSize / initialHeight;

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptionsView();

		perspective.multiplyScale(.25f);

		PointF dragFrom = getSurfacePointFromCanvasPoint(new PointF(initialWidth, initialHeight));
		PointF dragTo = getSurfacePointFromCanvasPoint(new PointF(maxWidth + 10, initialHeight));

		onDrawingSurfaceView()
				.perform(swipe(dragFrom, dragTo))
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		final Bitmap enlargedBitmap = layerModel.getCurrentLayer().getBitmap();
		final int bitmapSize = enlargedBitmap.getHeight() + enlargedBitmap.getWidth();
		assertTrue(bitmapSize < maxBitmapSize);
	}

	@LargeTest
	@Test
	public void testMaxImageResolutionToast() {
		final int maxWidth = maxBitmapSize / initialHeight;

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptionsView();

		final float zoomFactor = perspective.getScaleForCenterBitmap() * .25f;
		perspective.setScale(zoomFactor);

		PointF dragFrom = getSurfacePointFromCanvasPoint(new PointF(initialWidth, initialHeight));
		PointF dragTo = getSurfacePointFromCanvasPoint(new PointF(maxWidth + 10, initialHeight));

		onDrawingSurfaceView()
				.perform(swipe(dragFrom, dragTo));

		waitForToast(withText(R.string.resize_max_image_resolution_reached), 1000);
	}

	@Test
	public void testEnlargeEverySideAndCheckEnlargedColor() {
		onToolBarView()
				.performSelectTool(ToolType.FILL);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptionsView();

		PointF toolPosition = newPointF(getToolPosition());
		int[] pixels;
		int height;
		int width;

		setToolPosition(toolPosition.x - 1, toolPosition.y);
		onTopBarView()
				.performClickCheckmark();

		height = layerModel.getCurrentLayer().getBitmap().getHeight();
		pixels = new int[height];
		layerModel.getCurrentLayer().getBitmap().getPixels(pixels, 0, 1, 0, 0, 1, height);
		for (int pixel : pixels) {
			assertEquals(Color.TRANSPARENT, pixel);
		}

		setToolPosition(toolPosition.x + 1, toolPosition.y);
		onTopBarView()
				.performClickCheckmark();

		width = layerModel.getCurrentLayer().getBitmap().getWidth();
		height = layerModel.getCurrentLayer().getBitmap().getHeight();
		pixels = new int[height];
		layerModel.getCurrentLayer().getBitmap().getPixels(pixels, 0, 1, width - 1, 0, 1, height);
		for (int pixel : pixels) {
			assertEquals(Color.TRANSPARENT, pixel);
		}

		setToolPosition(toolPosition.x, toolPosition.y - 1);
		onTopBarView()
				.performClickCheckmark();

		width = layerModel.getCurrentLayer().getBitmap().getWidth();
		pixels = new int[width];
		layerModel.getCurrentLayer().getBitmap().getPixels(pixels, 0, width, 0, 0, width, 1);
		for (int pixel : pixels) {
			assertEquals(Color.TRANSPARENT, pixel);
		}

		setToolPosition(toolPosition.x, toolPosition.y + 1);
		onTopBarView()
				.performClickCheckmark();

		width = layerModel.getCurrentLayer().getBitmap().getWidth();
		height = layerModel.getCurrentLayer().getBitmap().getHeight();
		pixels = new int[width];
		layerModel.getCurrentLayer().getBitmap().getPixels(pixels, 0, width, 0, height - 1, width, 1);
		for (int pixel : pixels) {
			assertEquals(Color.TRANSPARENT, pixel);
		}
	}

	@Test
	public void testResizeWithPartialOverlapping() {
		onToolBarView()
				.performSelectTool(ToolType.FILL);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptionsView();

		setToolPosition(initialWidth, initialHeight);

		onTopBarView()
				.performClickCheckmark();

		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, initialHeight);

		onToolBarView()
				.performOpenToolOptionsView();
		onTransformToolOptionsView()
				.performAutoCrop();

		assertEquals(initialWidth / 2, getToolSelectionBoxWidth(), Float.MIN_VALUE);
		assertEquals(initialHeight / 2, getToolSelectionBoxHeight(), Float.MIN_VALUE);
	}

	@Test
	public void testResizeBoxCompletelyOutsideBitmap() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptionsView();

		perspective.multiplyScale(.25f);

		setToolPosition(initialWidth + initialHeight / 2,
				initialHeight + initialHeight / 2);

		setToolSelectionBoxDimensions(initialWidth / 2, initialHeight / 2);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, initialHeight);
	}

	@Test
	public void testResizeBoxCompletelyOutsideBitmapToast() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptionsView();

		perspective.multiplyScale(.25f);

		setToolPosition(initialWidth + initialHeight / 2,
				initialHeight + initialHeight / 2);

		onTopBarView()
				.performClickCheckmark();

		waitForToast(withText(R.string.resize_nothing_to_resize), 1000);
	}

	@Test
	public void testRotateMultipleLayers() {
		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.performAddLayer()
				.performClose();

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onDrawingSurfaceView()
				.checkBitmapDimension(initialHeight, initialWidth);

		onTransformToolOptionsView()
				.performRotateCounterClockwise();
		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, initialHeight);
	}

	@Test
	public void testRotateMultipleLayersUndoRedo() {
		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.performAddLayer()
				.performClose();

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onDrawingSurfaceView()
				.checkBitmapDimension(initialHeight, initialWidth);

		onToolBarView()
				.performCloseToolOptionsView();
		onTopBarView()
				.performUndo();
		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, initialHeight);

		onTopBarView()
				.performRedo();
		onDrawingSurfaceView()
				.checkBitmapDimension(initialHeight, initialWidth);
	}

	@Test
	public void testRotateLeft() {
		onToolProperties()
				.setColorResource(R.color.pocketpaint_color_picker_green1);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateCounterClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT));
		onToolProperties()
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateCounterClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));
		onToolProperties()
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateCounterClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT));
		onToolProperties()
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateCounterClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));
		onToolProperties()
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_green1);
	}

	@Test
	public void testRotateRight() {
		onToolProperties()
				.setColorResource(R.color.pocketpaint_color_picker_green1);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT));
		onToolProperties()
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));
		onToolProperties()
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT));
		onToolProperties()
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));
		onToolProperties()
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_green1);
	}

	@Test
	public void testRotateMultipleColors() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));
		onToolProperties()
				.setColorResource(R.color.pocketpaint_color_picker_green1);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT));
		onToolProperties()
				.checkMatchesColor(Color.BLACK);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT));
		onToolProperties()
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));
		onToolProperties()
				.checkMatchesColor(Color.BLACK);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));
		onToolProperties()
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT));
		onToolProperties()
				.checkMatchesColor(Color.BLACK);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT));
		onToolProperties()
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));
		onToolProperties()
				.checkMatchesColor(Color.BLACK);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));
		onToolProperties()
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_green1);
	}

	@Test
	public void testRotateMultipleLayersUndoRedoWhenRotatingWasNotLastCommand() {
		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.performAddLayer()
				.performClose();

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onDrawingSurfaceView()
				.checkBitmapDimension(initialHeight, initialWidth);

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onLayerMenuView()
				.performOpen()
				.performSelectLayer(1)
				.performClose();

		onDrawingSurfaceView()
				.checkLayerDimensions(initialHeight, initialWidth);

		onTopBarView().onUndoButton().check(matches(isEnabled()));

		onTopBarView().onRedoButton().check(matches(not(isEnabled())));

		onLayerMenuView()
				.performOpen()
				.performSelectLayer(0);

		onTopBarView().onUndoButton().check(matches(isEnabled()));

		onTopBarView().onRedoButton().check(matches(not(isEnabled())));

		onLayerMenuView()
				.performSelectLayer(1)
				.performClose();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onLayerMenuView()
				.performOpen()
				.performSelectLayer(2);

		onDrawingSurfaceView()
				.checkLayerDimensions(initialHeight, initialWidth);
	}

	@Test
	public void testCropWithClickOutsideToolbox() {

		Bitmap workingBitmap;
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));
		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT));
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		onTransformToolOptionsView()
				.performAutoCrop();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));
		onTopBarView()
				.performClickCheckmark();
		workingBitmap = layerModel.getCurrentLayer().getBitmap();
		initialWidth = workingBitmap.getWidth();
		initialHeight = workingBitmap.getHeight();

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.LEFT_MIDDLE,
						DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.OUTSIDE_MIDDLE_LEFT));
		onTopBarView()
				.performClickCheckmark();

		workingBitmap = layerModel.getCurrentLayer().getBitmap();
		assertThat(workingBitmap.getWidth(), lessThan(initialWidth));
		assertThat(workingBitmap.getHeight(), equalTo(initialHeight));
		initialWidth = workingBitmap.getWidth();

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.RIGHT_MIDDLE,
						DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.OUTSIDE_MIDDLE_RIGHT));
		onTopBarView()
				.performClickCheckmark();

		workingBitmap = layerModel.getCurrentLayer().getBitmap();
		assertThat(workingBitmap.getWidth(), lessThan(initialWidth));
		assertThat(workingBitmap.getHeight(), equalTo(initialHeight));
		initialWidth = workingBitmap.getWidth();

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.BOTTOM_MIDDLE,
						DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE));
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.OUTSIDE_MIDDLE_BOTTOM));
		onTopBarView()
				.performClickCheckmark();

		workingBitmap = layerModel.getCurrentLayer().getBitmap();
		assertThat(workingBitmap.getHeight(), lessThan(initialHeight));
		assertThat(workingBitmap.getWidth(), equalTo(initialWidth));
		initialHeight = workingBitmap.getHeight();

		onDrawingSurfaceView()
				.perform(swipe(DrawingSurfaceLocationProvider.TOP_MIDDLE,
						DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE));
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.OUTSIDE_MIDDLE_TOP));
		onTopBarView()
				.performClickCheckmark();

		workingBitmap = layerModel.getCurrentLayer().getBitmap();
		assertThat(workingBitmap.getHeight(), lessThan(initialHeight));
		assertThat(workingBitmap.getWidth(), equalTo(initialWidth));
	}

	@Test
	public void testResizeImage() {
		int width = layerModel.getWidth();
		int height = layerModel.getHeight();

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.moveSliderTo(50);
		onTransformToolOptionsView()
				.performApplyResize();

		int newWidth = (int) ((float) width / 100 * 50);
		int newHeight = (int) ((float) height / 100 * 50);

		assertEquals(newWidth, layerModel.getWidth());
		assertEquals(newHeight, layerModel.getHeight());
	}

	@Test
	public void testTryResizeImageToSizeZero() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.moveSliderTo(1);
		onTransformToolOptionsView()
				.performApplyResize();
		int heightAfterCrop = layerModel.getHeight();
		int widthAfterCrop = layerModel.getWidth();
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.moveSliderTo(1);
		onTransformToolOptionsView()
				.performApplyResize();

		int heightAfterSecondCrop = layerModel.getHeight();
		int widthAfterSecondCrop = layerModel.getWidth();

		waitForToast(withText(R.string.resize_cannot_resize_to_this_size), 1000);
		assertThat(heightAfterCrop, equalTo(heightAfterSecondCrop));
		assertThat(widthAfterCrop, equalTo(widthAfterSecondCrop));
	}

	@Test
	public void testSeekBarAndTextViewTheSame() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		SeekBar seekBar = mainActivity.findViewById(R.id.pocketpaint_transform_resize_seekbar);
		int progress = seekBar.getProgress();

		onTransformToolOptionsView()
				.checkPercentageTextMatches(progress);

		onTransformToolOptionsView()
				.moveSliderTo(1);

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		seekBar = launchActivityRule.getActivity().findViewById(R.id.pocketpaint_transform_resize_seekbar);
		progress = seekBar.getProgress();
		onTransformToolOptionsView()
				.checkPercentageTextMatches(progress);

		onTransformToolOptionsView()
				.performApplyResize();
		onTransformToolOptionsView()
				.moveSliderTo(50);

		progress = seekBar.getProgress();
		onTransformToolOptionsView()
				.checkPercentageTextMatches(progress);

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		onTransformToolOptionsView()
				.performApplyResize();

		seekBar = launchActivityRule.getActivity().findViewById(R.id.pocketpaint_transform_resize_seekbar);
		progress = seekBar.getProgress();
		onTransformToolOptionsView()
				.checkPercentageTextMatches(progress);
	}

	@Test
	public void testTransformToolDoesNotResetPerspectiveScale() {
		float scale = 2.0f;

		perspective.setScale(scale);
		perspective.setSurfaceTranslationX(50);
		perspective.setSurfaceTranslationY(200);
		mainActivity.refreshDrawingSurface();

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		assertEquals(scale, perspective.getScale(), 0.0001f);
	}
}
