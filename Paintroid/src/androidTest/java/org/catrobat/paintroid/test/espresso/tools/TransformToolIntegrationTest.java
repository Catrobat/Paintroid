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
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.espresso.util.DialogHiddenIdlingResource;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.tools.implementation.TransformTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.GREEN_COLOR_PICKER_BUTTON_POSITION;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getWorkingBitmap;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectColorPickerPresetSelectorColor;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.waitForToast;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.swipe;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withDrawable;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TransformToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Rule
	public ScreenshotOnFailRule screenshotOnFailRule = new ScreenshotOnFailRule();
	private ActivityHelper activityHelper;
	private IdlingResource dialogWait;

	private int displayWidth;
	private int displayHeight;

	private int initialWidth;
	private int initialHeight;
	private int maxBitmapSize;

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

	private static PointF getSurfacePointFromCanvasPoint(PointF point) {
		return PaintroidApplication.perspective.getSurfacePointFromCanvasPoint(point);
	}

	private static float getToolSelectionBoxWidth() {
		return ((BaseToolWithRectangleShape) PaintroidApplication.currentTool).boxWidth;
	}

	private static void setToolSelectionBoxWidth(float width) {
		((BaseToolWithRectangleShape) PaintroidApplication.currentTool).boxWidth = width;
	}

	private static float getToolSelectionBoxHeight() {
		return ((BaseToolWithRectangleShape) PaintroidApplication.currentTool).boxHeight;
	}

	private static void setToolSelectionBoxHeight(float height) {
		((BaseToolWithRectangleShape) PaintroidApplication.currentTool).boxHeight = height;
	}

	private static void setToolSelectionBoxDimensions(float width, float height) {
		BaseToolWithRectangleShape currentTool = (BaseToolWithRectangleShape)
				PaintroidApplication.currentTool;
		currentTool.boxWidth = width;
		currentTool.boxHeight = height;
	}

	private static PointF getToolPosition() {
		return ((BaseToolWithShape) PaintroidApplication.currentTool).toolPosition;
	}

	private static void setToolPosition(float x, float y) {
		((BaseToolWithShape) PaintroidApplication.currentTool).toolPosition.set(x, y);
	}

	private static PointF newPointF(PointF point) {
		return new PointF(point.x, point.y);
	}

	@Before
	public void setUp() {
		dialogWait = new DialogHiddenIdlingResource(IndeterminateProgressDialog.getInstance());
		IdlingRegistry.getInstance().register(dialogWait);

		activityHelper = new ActivityHelper(launchActivityRule.getActivity());

		displayWidth = activityHelper.getDisplayWidth();
		displayHeight = activityHelper.getDisplayHeight();

		maxBitmapSize = displayHeight * displayWidth
				* (int) TransformTool.MAXIMUM_BITMAP_SIZE_FACTOR;

		final Bitmap workingBitmap = getWorkingBitmap();
		initialWidth = workingBitmap.getWidth();
		initialHeight = workingBitmap.getHeight();

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
	}

	@After
	public void tearDown() {
		IdlingRegistry.getInstance().unregister(dialogWait);

		activityHelper = null;
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

		int width = getWorkingBitmap().getWidth();
		int height = getWorkingBitmap().getHeight();
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
	public void testWhenNoPixelIsOnBitmap() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptions();

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
				.performCloseToolOptions();

		waitForToast(withText(R.string.transform_info_text), 1000);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		waitForToast(withText(R.string.resize_nothing_to_resize), 1000);
	}

	@Test
	public void testChangeCroppingHeightAndCheckWidth() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptions();

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
				.performCloseToolOptions();

		int width = initialWidth / 2;
		int height = initialHeight / 2;
		setToolSelectionBoxDimensions(width, height);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		onDrawingSurfaceView()
				.checkBitmapDimension(width, height)
				.checkLayerDimensions(width, height);
	}

	@Test
	public void testIfOnePixelIsFound() {
		final Bitmap workingBitmap = getWorkingBitmap();
		workingBitmap.setPixel(initialWidth / 2, initialHeight / 2, Color.BLACK);
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		assertEquals(1, getToolSelectionBoxWidth(), Float.MIN_VALUE);
		assertEquals(1, getToolSelectionBoxHeight(), Float.MIN_VALUE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));
		onDrawingSurfaceView()
				.checkBitmapDimension(1, 1);
	}

	@Test
	public void testIfMultiplePixelAreFound() {
		final Bitmap workingBitmap = getWorkingBitmap();
		workingBitmap.setPixel(1, 1, Color.BLACK);
		workingBitmap.setPixel(initialWidth - 1, initialHeight - 1, Color.BLACK);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth - 1, initialHeight - 1);
	}

	@Test
	public void testIfDrawingSurfaceBoundsAreFoundAndNotCropped() {
		final Bitmap workingBitmap = getWorkingBitmap();

		workingBitmap.setPixel(initialWidth / 2, 0, Color.BLACK);
		workingBitmap.setPixel(0, initialHeight / 2, Color.BLACK);
		workingBitmap.setPixel(initialWidth - 1, initialHeight / 2, Color.BLACK);
		workingBitmap.setPixel(initialWidth / 2, initialHeight - 1, Color.BLACK);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, initialHeight);
	}

	@Test
	public void testIfClickOnCanvasCrops() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		Bitmap workingBitmap = getWorkingBitmap();
		workingBitmap.eraseColor(Color.BLACK);

		for (int indexWidth = 0; indexWidth < initialWidth; indexWidth++) {
			workingBitmap.setPixel(indexWidth, 0, Color.TRANSPARENT);
		}

		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));
		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, --initialHeight);

		workingBitmap = getWorkingBitmap();

		for (int indexWidth = 0; indexWidth < initialWidth; indexWidth++) {
			workingBitmap.setPixel(indexWidth, initialHeight - 1, Color.TRANSPARENT);
		}

		onToolBarView()
				.performOpenToolOptions();
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));
		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, --initialHeight);

		workingBitmap = getWorkingBitmap();

		for (int indexHeight = 0; indexHeight < initialHeight; indexHeight++) {
			workingBitmap.setPixel(0, indexHeight, Color.TRANSPARENT);
		}

		onToolBarView()
				.performOpenToolOptions();
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));
		onDrawingSurfaceView()
				.checkBitmapDimension(--initialWidth, initialHeight);
		workingBitmap = getWorkingBitmap();

		for (int indexHeight = 0; indexHeight < initialHeight; indexHeight++) {
			workingBitmap.setPixel(initialWidth - 1, indexHeight, Color.TRANSPARENT);
		}

		onToolBarView()
				.performOpenToolOptions();
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));
		onDrawingSurfaceView()
				.checkBitmapDimension(--initialWidth, initialHeight);
	}

	@Test
	public void testSmallBitmapResizing() {
		Bitmap workingBitmap = getWorkingBitmap();

		workingBitmap.setPixel(initialWidth / 2, initialHeight / 2, Color.BLACK);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));
		onDrawingSurfaceView()
				.checkBitmapDimension(1, 1);

		setToolSelectionBoxDimensions(initialWidth, initialHeight);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));
		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, initialHeight);
	}

	@Ignore("This is probably not intended behaviour")
	@Test
	public void testCenterBitmapAfterCropAndUndo() {
		final PointF originalTopLeft = getSurfacePointFromCanvasPoint(new PointF(0, 0));
		final PointF originalBottomRight = getSurfacePointFromCanvasPoint(
				new PointF(initialWidth - 1, initialHeight - 1));

		drawPlus(getWorkingBitmap(), initialWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		final Bitmap croppedBitmap = getWorkingBitmap();

		final PointF topLeft = getSurfacePointFromCanvasPoint(new PointF(0, 0));
		final PointF bottomRight = getSurfacePointFromCanvasPoint(
				new PointF(croppedBitmap.getWidth(), croppedBitmap.getHeight()));

		assertThat(initialHeight, greaterThan(croppedBitmap.getHeight()));
		assertThat(initialWidth, greaterThan(croppedBitmap.getWidth()));

		assertThat(topLeft.x, greaterThan(originalTopLeft.x));
		assertThat(topLeft.y, greaterThan(originalTopLeft.y));
		assertThat(bottomRight.x, lessThan(originalBottomRight.x));
		assertThat(bottomRight.y, lessThan(originalBottomRight.y));

		onTopBarView()
				.performUndo();

		final PointF undoTopLeft = getSurfacePointFromCanvasPoint(new PointF(0, 0));
		final PointF undoBottomRight = getSurfacePointFromCanvasPoint(
				new PointF(initialWidth - 1, initialHeight - 1));

		assertEquals(undoTopLeft, originalTopLeft);
		assertEquals(undoBottomRight, originalBottomRight);
	}

	@Test
	public void testCenterBitmapAfterCropDrawingOnTopRight() {
		final PointF originalTopLeft = getSurfacePointFromCanvasPoint(new PointF(0, 0));
		final PointF originalBottomRight = getSurfacePointFromCanvasPoint(
				new PointF(initialWidth - 1, initialHeight - 1));

		final Bitmap workingBitmap = getWorkingBitmap();
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
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		final Bitmap croppedBitmap = getWorkingBitmap();
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
		drawPlus(getWorkingBitmap(), initialWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		final Bitmap croppedBitmap = getWorkingBitmap();
		final int width = croppedBitmap.getWidth();
		final int height = croppedBitmap.getHeight();
		final TransformTool tool = (TransformTool) PaintroidApplication.currentTool;
		assertEquals(0.0f, tool.resizeBoundWidthXLeft, Float.MIN_VALUE);
		assertEquals(width - 1, tool.resizeBoundWidthXRight, Float.MIN_VALUE);
		assertEquals(0.0f, tool.resizeBoundHeightYTop, Float.MIN_VALUE);
		assertEquals(height - 1, tool.resizeBoundHeightYBottom, Float.MIN_VALUE);
	}

	@Test
	public void testMoveLeftCroppingBorderAndDoCrop() {
		drawPlus(getWorkingBitmap(), initialWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		final int height = getWorkingBitmap().getHeight();
		final PointF toolPosition = getToolPosition();

		for (int i = 0; i < 4; i++) {
			final float newSelectionBoxWidth = getToolSelectionBoxWidth() / 2;
			setToolSelectionBoxWidth(newSelectionBoxWidth);
			toolPosition.x += newSelectionBoxWidth / 2;

			onDrawingSurfaceView()
					.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

			onDrawingSurfaceView()
					.checkBitmapDimension((int) (newSelectionBoxWidth + .5f), height);
		}
	}

	@Test
	public void testMoveRightCroppingBorderAndDoCrop() {
		drawPlus(getWorkingBitmap(), initialWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		final int height = getWorkingBitmap().getHeight();
		final PointF toolPosition = getToolPosition();

		for (int i = 0; i < 4; i++) {
			final float newSelectionBoxWidth = getToolSelectionBoxWidth() / 2;
			setToolSelectionBoxWidth(newSelectionBoxWidth);
			toolPosition.x -= newSelectionBoxWidth / 2;

			onDrawingSurfaceView()
					.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

			onDrawingSurfaceView()
					.checkBitmapDimension((int) newSelectionBoxWidth, height);
		}
	}

	@Test
	public void testMoveTopCroppingBorderAndDoCrop() {
		drawPlus(getWorkingBitmap(), initialWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		final int width = getWorkingBitmap().getWidth();
		final PointF toolPosition = getToolPosition();

		for (int i = 0; i < 4; i++) {
			final float newSelectionBoxHeight = getToolSelectionBoxHeight() / 2;
			setToolSelectionBoxHeight(newSelectionBoxHeight);
			toolPosition.y += newSelectionBoxHeight / 2;

			onDrawingSurfaceView()
					.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

			onDrawingSurfaceView()
					.checkBitmapDimension(width, (int) (newSelectionBoxHeight + .5f));
		}
	}

	@Test
	public void testMoveBottomCroppingBorderAndDoCrop() {
		drawPlus(getWorkingBitmap(), initialWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		final int width = getWorkingBitmap().getWidth();
		final PointF toolPosition = getToolPosition();

		for (int i = 0; i < 4; i++) {
			final float newSelectionBoxHeight = getToolSelectionBoxHeight() / 2;
			setToolSelectionBoxHeight(newSelectionBoxHeight);
			toolPosition.y += newSelectionBoxHeight / 2;

			onDrawingSurfaceView()
					.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

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
		getWorkingBitmap().setPixels(new int[cropSize * height],
				0, cropSize, 0, 0, cropSize, height);

		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onDrawingSurfaceView()
				.checkBitmapDimension(width, height);

		cropSize = initialHeight / 8;
		height -= cropSize;
		getWorkingBitmap().setPixels(new int[cropSize * width],
				0, width, 0, 0, width, cropSize);

		onToolBarView()
				.performOpenToolOptions();
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onDrawingSurfaceView()
				.checkBitmapDimension(width, height);

		cropSize = initialWidth / 8;
		width -= cropSize;
		getWorkingBitmap().setPixels(new int[cropSize * height],
				0, cropSize, width, 0, cropSize, height);

		onToolBarView()
				.performOpenToolOptions();
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onDrawingSurfaceView()
				.checkBitmapDimension(width, height);

		cropSize = initialHeight / 8;
		height -= cropSize;
		getWorkingBitmap().setPixels(new int[cropSize * width],
				0, width, 0, height, width, cropSize);

		onToolBarView()
				.performOpenToolOptions();
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onDrawingSurfaceView()
				.checkBitmapDimension(width, height);
	}

	@Test
	public void testResizeBordersMatchBitmapBordersAfterCrop() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptions();

		drawPlus(getWorkingBitmap(), initialWidth / 2);

		setToolSelectionBoxDimensions(initialWidth / 8, initialHeight / 8);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		final Bitmap croppedBitmap = getWorkingBitmap();
		final int height = croppedBitmap.getHeight();
		final int width = croppedBitmap.getWidth();

		final TransformTool tool = (TransformTool) PaintroidApplication.currentTool;
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
				.performCloseToolOptions();

		PaintroidApplication.perspective.multiplyScale(.25f);

		PointF dragFrom = getSurfacePointFromCanvasPoint(new PointF(initialWidth, initialHeight));
		PointF dragTo = getSurfacePointFromCanvasPoint(new PointF(maxWidth + 10, initialHeight));

		onDrawingSurfaceView()
				.perform(swipe(dragFrom, dragTo))
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		final Bitmap enlargedBitmap = getWorkingBitmap();
		final int bitmapSize = enlargedBitmap.getHeight() + enlargedBitmap.getWidth();
		assertTrue(bitmapSize < maxBitmapSize);
	}

	@LargeTest
	@Test
	public void testMaxImageResolutionToast() {
		final int maxWidth = maxBitmapSize / initialHeight;

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptions();

		final float zoomFactor = PaintroidApplication.perspective.getScaleForCenterBitmap() * .25f;
		PaintroidApplication.perspective.setScale(zoomFactor);

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
				.performCloseToolOptions();

		PointF toolPosition = newPointF(getToolPosition());
		int[] pixels;
		int height;
		int width;

		setToolPosition(toolPosition.x - 1, toolPosition.y);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		height = getWorkingBitmap().getHeight();
		pixels = new int[height];
		getWorkingBitmap().getPixels(pixels, 0, 1, 0, 0, 1, height);
		for (int pixel : pixels) {
			assertEquals(Color.TRANSPARENT, pixel);
		}

		setToolPosition(toolPosition.x + 1, toolPosition.y);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		width = getWorkingBitmap().getWidth();
		height = getWorkingBitmap().getHeight();
		pixels = new int[height];
		getWorkingBitmap().getPixels(pixels, 0, 1, width - 1, 0, 1, height);
		for (int pixel : pixels) {
			assertEquals(Color.TRANSPARENT, pixel);
		}

		setToolPosition(toolPosition.x, toolPosition.y - 1);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		width = getWorkingBitmap().getWidth();
		pixels = new int[width];
		getWorkingBitmap().getPixels(pixels, 0, width, 0, 0, width, 1);
		for (int pixel : pixels) {
			assertEquals(Color.TRANSPARENT, pixel);
		}

		setToolPosition(toolPosition.x, toolPosition.y + 1);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		width = getWorkingBitmap().getWidth();
		height = getWorkingBitmap().getHeight();
		pixels = new int[width];
		getWorkingBitmap().getPixels(pixels, 0, width, 0, height - 1, width, 1);
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
				.performCloseToolOptions();

		setToolPosition(initialWidth, initialHeight);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		onDrawingSurfaceView()
				.checkBitmapDimension(initialWidth, initialHeight);

		onToolBarView()
				.performOpenToolOptions();
		onTransformToolOptionsView()
				.performAutoCrop();

		assertEquals(initialWidth / 2, getToolSelectionBoxWidth(), Float.MIN_VALUE);
		assertEquals(initialHeight / 2, getToolSelectionBoxHeight(), Float.MIN_VALUE);
	}

	@Test
	public void testResizeBoxCompletelyOutsideBitmap() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptions();

		PaintroidApplication.perspective.multiplyScale(.25f);

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
				.performCloseToolOptions();

		PaintroidApplication.perspective.multiplyScale(.25f);

		setToolPosition(initialWidth + initialHeight / 2,
				initialHeight + initialHeight / 2);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

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
				.performCloseToolOptions();
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
		selectColorPickerPresetSelectorColor(GREEN_COLOR_PICKER_BUTTON_POSITION);
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
				.checkColorResource(R.color.color_chooser_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateCounterClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));
		onToolProperties()
				.checkColorResource(R.color.color_chooser_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateCounterClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT));
		onToolProperties()
				.checkColorResource(R.color.color_chooser_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateCounterClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));
		onToolProperties()
				.checkColorResource(R.color.color_chooser_green1);
	}

	@Test
	public void testRotateRight() {
		selectColorPickerPresetSelectorColor(GREEN_COLOR_PICKER_BUTTON_POSITION);
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
				.checkColorResource(R.color.color_chooser_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));
		onToolProperties()
				.checkColorResource(R.color.color_chooser_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT));
		onToolProperties()
				.checkColorResource(R.color.color_chooser_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));
		onToolProperties()
				.checkColorResource(R.color.color_chooser_green1);
	}

	@Test
	public void testRotateMultipleColors() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));
		selectColorPickerPresetSelectorColor(GREEN_COLOR_PICKER_BUTTON_POSITION);
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
				.checkColor(Color.BLACK);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT));
		onToolProperties()
				.checkColorResource(R.color.color_chooser_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));
		onToolProperties()
				.checkColor(Color.BLACK);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));
		onToolProperties()
				.checkColorResource(R.color.color_chooser_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT));
		onToolProperties()
				.checkColor(Color.BLACK);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT));
		onToolProperties()
				.checkColorResource(R.color.color_chooser_green1);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));
		onToolProperties()
				.checkColor(Color.BLACK);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));
		onToolProperties()
				.checkColorResource(R.color.color_chooser_green1);
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

		onTopBarView()
				.performUndo();
		onDrawingSurfaceView()
				.checkLayerDimensions(initialWidth, initialHeight);

		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_undo), isEnabled())));
		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_redo), isEnabled())));

		onLayerMenuView()
				.performOpen()
				.performSelectLayer(0);

		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_undo), isEnabled())));
		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_redo_disabled), not(isEnabled()))));

		onLayerMenuView()
				.performSelectLayer(1)
				.performClose();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onLayerMenuView()
				.performOpen()
				.performSelectLayer(2);

		onDrawingSurfaceView()
				.checkLayerDimensions(initialWidth, initialHeight);
	}
}
