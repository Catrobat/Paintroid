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
import android.graphics.Point;
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
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.espresso.util.DialogHiddenIdlingResource;
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.Layer;
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

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.convertFromCanvasToScreen;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getWorkingBitmap;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.waitForToast;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.swipe;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withDrawable;
import static org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView;
import static org.junit.Assert.assertEquals;
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

	private PointF pointOnScreenLeft;
	private PointF pointOnScreenRight;
	private PointF pointOnScreenMiddle;

	private int displayWidth;
	private int displayHeight;

	private int originalWidth;
	private int originalHeight;

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

	private static PointF getToolPositionOnScreen() throws Exception {
		return getBitmapPositionOnScreen(getToolPosition());
	}

	private static PointF getBitmapPositionOnScreen(PointF point) throws Exception {
		final PointF screenPoint = convertFromCanvasToScreen(point, PaintroidApplication.perspective);
		return new PointF(screenPoint.x + .5f,
				screenPoint.y + Utils.getStatusbarHeight() / 2 + .5f);
	}

	private static void assertLayerDimensions(int expectedWidth, int expectedHeight) {
		ArrayList<Layer> layers = LayerListener.getInstance().getAdapter().getLayers();
		for (Layer layer : layers) {
			assertBitmapDimensions(layer.getImage(), expectedWidth, expectedHeight);
		}
	}

	private static void assertWorkingBitmapDimensions(int expectedWidth, int expectedHeight) throws Exception {
		assertBitmapDimensions(getWorkingBitmap(), expectedWidth, expectedHeight);
	}

	private static void assertBitmapDimensions(Bitmap bitmap, int expectedWidth, int expectedHeight) {
		assertEquals(expectedWidth, bitmap.getWidth());
		assertEquals(expectedHeight, bitmap.getHeight());
	}

	private static float getSelectionBoxWidth() {
		return ((BaseToolWithRectangleShape) PaintroidApplication.currentTool).boxWidth;
	}

	private static void setSelectionBoxWidth(float width) {
		((BaseToolWithRectangleShape) PaintroidApplication.currentTool).boxWidth = width;
	}

	private static float getSelectionBoxHeight() {
		return ((BaseToolWithRectangleShape) PaintroidApplication.currentTool).boxHeight;
	}

	private static void setSelectionBoxHeight(float height) {
		((BaseToolWithRectangleShape) PaintroidApplication.currentTool).boxHeight = height;
	}

	private static PointF getToolPosition() {
		return ((BaseToolWithShape) PaintroidApplication.currentTool).toolPosition;
	}

	@Before
	public void setUp() throws Exception {
		dialogWait = new DialogHiddenIdlingResource(IndeterminateProgressDialog.getInstance());
		IdlingRegistry.getInstance().register(dialogWait);

		activityHelper = new ActivityHelper(launchActivityRule.getActivity());

		displayWidth = activityHelper.getDisplayWidth();
		displayHeight = activityHelper.getDisplayHeight();

		pointOnScreenLeft = new PointF(displayWidth * 0.25f, displayHeight * 0.5f);
		pointOnScreenRight = new PointF(displayWidth * 0.75f, displayHeight * 0.5f);
		pointOnScreenMiddle = new PointF(displayWidth * 0.5f, displayHeight * 0.5f);

		final Bitmap workingBitmap = getWorkingBitmap();
		originalWidth = workingBitmap.getWidth();
		originalHeight = workingBitmap.getHeight();

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
	}

	@After
	public void tearDown() {
		IdlingRegistry.getInstance().unregister(dialogWait);

		activityHelper = null;
	}

	@Test
	public void testAutoCrop() throws NoSuchFieldException, IllegalAccessException {
		onView(isRoot())
				.perform(touchAt(pointOnScreenMiddle));

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		onTransformToolOptionsView()
				.performAutoCrop();

		int originalWidth = getWorkingBitmap().getWidth();
		int originalHeight = getWorkingBitmap().getHeight();
		assertTrue("Box width should get smaller", getSelectionBoxWidth() < originalWidth);
		assertTrue("Box height should get smaller", getSelectionBoxHeight() < originalHeight);
	}

	@Test
	public void testAutoCropOnEmptyBitmap() throws NoSuchFieldException, IllegalAccessException {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		PointF originalPosition = getToolPosition();

		onTransformToolOptionsView()
				.performAutoCrop();

		assertEquals("Box width should not have changed", originalWidth, getSelectionBoxWidth(), Float.MIN_VALUE);
		assertEquals("Box height should not have changed", originalHeight, getSelectionBoxHeight(), Float.MIN_VALUE);
		assertEquals("Box position should not have changed", originalPosition, getToolPosition());

		assertLayerDimensions(originalWidth, originalHeight);
	}

	@Test
	public void testAutoCropOnFilledBitmap() throws NoSuchFieldException, IllegalAccessException {
		onToolBarView()
				.performSelectTool(ToolType.FILL);

		onView(isRoot())
				.perform(touchAt(pointOnScreenMiddle));

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		int originalWidth = getWorkingBitmap().getWidth();
		int originalHeight = getWorkingBitmap().getHeight();
		PointF originalPosition = getToolPosition();

		onTransformToolOptionsView()
				.performAutoCrop();

		assertEquals("Box width should not have changed", originalWidth, getSelectionBoxWidth(), Float.MIN_VALUE);
		assertEquals("Box height should not have changed", originalHeight, getSelectionBoxHeight(), Float.MIN_VALUE);
		assertEquals("Box position should not have changed", originalPosition, getToolPosition());

		assertLayerDimensions(originalWidth, originalHeight);
	}

	@Test
	public void testWhenNoPixelIsOnBitmap() throws Exception {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptions();

		onView(isRoot())
				.perform(touchAt(pointOnScreenMiddle));

		assertWorkingBitmapDimensions(originalWidth, originalHeight);
		assertLayerDimensions(originalWidth, originalHeight);
	}

	@LargeTest
	@Test
	public void testWhenNoPixelIsOnBitmapToasts() {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptions();

		waitForToast(withText(R.string.transform_info_text), 5000);

		onView(isRoot())
				.perform(touchAt(pointOnScreenMiddle));

		waitForToast(withText(R.string.resize_nothing_to_resize), 5000);
	}

	@Test
	public void testChangeCroppingHeightAndCheckWidth() throws Exception {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptions();

		Point dragFrom = Utils.convertFromCanvasToScreen(
				new Point(0, getWorkingBitmap().getHeight()), PaintroidApplication.perspective);

		PointF screenPointOld = new PointF(displayWidth / 2, dragFrom.y);
		PointF screenPointNew = new PointF(displayWidth / 2, dragFrom.y - 200);

		float boundingBoxWidth = getSelectionBoxWidth();

		onView(isRoot())
				.perform(swipe(screenPointOld, screenPointNew));

		assertEquals(boundingBoxWidth, getSelectionBoxWidth(), Float.MIN_VALUE);
	}

	@Test
	public void testMoveCroppingBordersOnEmptyBitmapAndDoCrop() throws Exception {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptions();

		final int newWidth = originalWidth / 2;
		final int newHeight = originalHeight / 2;

		setSelectionBoxWidth(newWidth);
		setSelectionBoxHeight(newHeight);

		onView(isRoot())
				.perform(touchAt(pointOnScreenMiddle));

		assertWorkingBitmapDimensions(newWidth, newHeight);
		assertLayerDimensions(newWidth, newHeight);
	}

	@Test
	public void testIfMultiplePixelAreFound() throws Exception {
		final Bitmap workingBitmap = getWorkingBitmap();
		workingBitmap.setPixel(1, 1, Color.BLACK);
		workingBitmap.setPixel(originalWidth - 1, originalHeight - 1, Color.BLACK);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();

		onView(isRoot())
				.perform(touchAt(pointOnScreenMiddle));

		assertWorkingBitmapDimensions(originalWidth - 1, originalHeight - 1);
	}

	@Test
	public void testIfDrawingSurfaceBoundsAreFoundAndNotCropped() throws Exception {
		final Bitmap workingBitmap = getWorkingBitmap();

		workingBitmap.setPixel(originalWidth / 2, 0, Color.BLACK);
		workingBitmap.setPixel(0, originalHeight / 2, Color.BLACK);
		workingBitmap.setPixel(originalWidth - 1, originalHeight / 2, Color.BLACK);
		workingBitmap.setPixel(originalWidth / 2, originalHeight - 1, Color.BLACK);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onView(isRoot())
				.perform(touchAt(pointOnScreenMiddle));

		assertWorkingBitmapDimensions(originalWidth, originalHeight);
	}

	@Test
	public void testIfClickOnCanvasCrops() throws Exception {

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);

		Bitmap workingBitmap = getWorkingBitmap();
		workingBitmap.eraseColor(Color.BLACK);

		for (int indexWidth = 0; indexWidth < originalWidth; indexWidth++) {
			workingBitmap.setPixel(indexWidth, 0, Color.TRANSPARENT);
		}

		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onView(isRoot())
				.perform(touchAt(pointOnScreenMiddle));

		assertWorkingBitmapDimensions(originalWidth, --originalHeight);
		workingBitmap = getWorkingBitmap();

		for (int indexWidth = 0; indexWidth < originalWidth; indexWidth++) {
			workingBitmap.setPixel(indexWidth, originalHeight - 1, Color.TRANSPARENT);
		}

		onToolBarView()
				.performOpenToolOptions();
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onView(isRoot())
				.perform(touchAt(pointOnScreenMiddle));

		assertWorkingBitmapDimensions(originalWidth, --originalHeight);
		workingBitmap = getWorkingBitmap();

		for (int indexHeight = 0; indexHeight < originalHeight; indexHeight++) {
			workingBitmap.setPixel(0, indexHeight, Color.TRANSPARENT);
		}

		onToolBarView()
				.performOpenToolOptions();
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onView(isRoot())
				.perform(touchAt(pointOnScreenMiddle));

		assertWorkingBitmapDimensions(--originalWidth, originalHeight);
		workingBitmap = getWorkingBitmap();

		for (int indexHeight = 0; indexHeight < originalHeight; indexHeight++) {
			workingBitmap.setPixel(originalWidth - 1, indexHeight, Color.TRANSPARENT);
		}

		onToolBarView()
				.performOpenToolOptions();
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onView(isRoot())
				.perform(touchAt(pointOnScreenMiddle));

		assertWorkingBitmapDimensions(--originalWidth, originalHeight);
	}

	@Test
	public void testSmallBitmapResizing() throws Exception {
		Bitmap workingBitmap = getWorkingBitmap();

		workingBitmap.setPixel(originalWidth / 2, originalHeight / 2, Color.BLACK);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();

		onView(isRoot())
				.perform(touchAt(getToolPositionOnScreen()));

		assertWorkingBitmapDimensions(1, 1);

		setSelectionBoxWidth(originalWidth);
		setSelectionBoxHeight(originalHeight);
		onView(isRoot())
				.perform(touchAt(pointOnScreenMiddle));

		assertWorkingBitmapDimensions(originalWidth, originalHeight);
	}

	@Test
	public void testCenterBitmapAfterCropAndUndo() throws Exception {
		final PointF originalTopLeft = getBitmapPositionOnScreen(new PointF(0, 0));
		final PointF originalBottomRight = getBitmapPositionOnScreen(
				new PointF(originalWidth - 1, originalHeight - 1));

		drawPlus(getWorkingBitmap(), originalWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onView(isRoot())
				.perform(touchAt(getToolPositionOnScreen()));

		final Bitmap croppedBitmap = getWorkingBitmap();

		final PointF topLeft = getBitmapPositionOnScreen(new PointF(0, 0));
		final PointF bottomRight = getBitmapPositionOnScreen(
				new PointF(croppedBitmap.getWidth(), croppedBitmap.getHeight()));

		assertTrue(originalHeight > croppedBitmap.getHeight());
		assertTrue(originalWidth > croppedBitmap.getWidth());

		assertTrue(topLeft.x > originalTopLeft.x);
		assertTrue(topLeft.y > originalTopLeft.y);
		assertTrue(bottomRight.x < originalBottomRight.x);
		assertTrue(bottomRight.y < originalBottomRight.y);

		onTopBarView()
				.performUndo();

		final PointF undoTopLeft = getBitmapPositionOnScreen(new PointF(0, 0));
		final PointF undoBottomRight = getBitmapPositionOnScreen(
				new PointF(originalWidth - 1, originalHeight - 1));

		assertEquals(undoTopLeft, originalTopLeft);
		assertEquals(undoBottomRight, originalBottomRight);
	}

	@Test
	public void testCenterBitmapAfterCropDrawingOnTopRight() throws Exception {
		final PointF originalTopLeft = getBitmapPositionOnScreen(new PointF(0, 0));
		final PointF originalBottomRight = getBitmapPositionOnScreen(
				new PointF(originalWidth - 1, originalHeight - 1));

		final Bitmap workingBitmap = getWorkingBitmap();
		final int lineWidth = 10;
		final int lineHeight = originalHeight / 2;
		final int verticalStartX = (originalWidth - lineWidth);
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
		onView(isRoot())
				.perform(touchAt(getToolPositionOnScreen()));

		final Bitmap croppedBitmap = getWorkingBitmap();
		final PointF topLeft = getBitmapPositionOnScreen(new PointF(0, 0));
		final PointF bottomRight = getBitmapPositionOnScreen(
				new PointF(croppedBitmap.getWidth() - 1, croppedBitmap.getHeight() - 1));

		assertWorkingBitmapDimensions(lineWidth, lineHeight);

		final Point centerOfScreen = new Point(displayWidth / 2, displayHeight / 2);
		assertTrue(topLeft.x > originalTopLeft.x);
		assertTrue(topLeft.y > originalTopLeft.y);
		assertTrue(bottomRight.x < originalBottomRight.x);
		assertTrue(bottomRight.y < originalBottomRight.y);

		assertTrue(topLeft.x < centerOfScreen.x);
		assertTrue(topLeft.y < centerOfScreen.y);
		assertTrue(bottomRight.x > centerOfScreen.x);
		assertTrue(bottomRight.y > centerOfScreen.y);
	}

	@Test
	public void testIfBordersAreAlignedCorrectAfterCrop() throws Exception {

		drawPlus(getWorkingBitmap(), originalWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onView(isRoot())
				.perform(touchAt(getToolPositionOnScreen()));

		final Bitmap croppedBitmap = getWorkingBitmap();
		final int width = croppedBitmap.getWidth();
		final int height = croppedBitmap.getHeight();
		final TransformTool tool = (TransformTool) PaintroidApplication.currentTool;
		assertEquals(0.0f, tool.resizeBoundWidthXLeft, Float.MIN_VALUE);
		assertEquals(width - 1, tool.resizeBoundWidthXRight, Float.MIN_VALUE);
		assertEquals(0.0f, tool.resizeBoundHeightYTop, Float.MIN_VALUE);
		assertEquals(height - 1, tool.resizeBoundHeightYBottom, Float.MIN_VALUE);
	}

	@Ignore("Fix getBitmapPositionOnScreen y")
	@Test
	public void testMoveLeftCroppingBorderAndDoCrop() throws Exception {
		drawPlus(getWorkingBitmap(), originalWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onView(isRoot())
				.perform(touchAt(getToolPositionOnScreen()));

		final int height = getWorkingBitmap().getHeight();
		final PointF toolPosition = getToolPosition();

		for (int i = 0; i < 4; i++) {
			final float newSelectionBoxWidth = getSelectionBoxWidth() / 2;
			setSelectionBoxWidth(newSelectionBoxWidth);
			toolPosition.x += newSelectionBoxWidth / 2;

			onView(isRoot())
					.perform(touchAt(getToolPositionOnScreen()));

			assertWorkingBitmapDimensions((int) newSelectionBoxWidth, height);
		}
	}

	@Ignore("Fix getBitmapPositionOnScreen y")
	@Test
	public void testMoveRightCroppingBorderAndDoCrop() throws Exception {
		drawPlus(getWorkingBitmap(), originalWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onView(isRoot())
				.perform(touchAt(getToolPositionOnScreen()));

		final int height = getWorkingBitmap().getHeight();
		final PointF toolPosition = getToolPosition();

		for (int i = 0; i < 4; i++) {
			final float newSelectionBoxWidth = getSelectionBoxWidth() / 2;
			setSelectionBoxWidth(newSelectionBoxWidth);
			toolPosition.x -= newSelectionBoxWidth / 2;

			onView(isRoot())
					.perform(touchAt(getToolPositionOnScreen()));

			assertWorkingBitmapDimensions((int) newSelectionBoxWidth, height);
		}
	}

	@Ignore("Fix getBitmapPositionOnScreen y")
	@Test
	public void testMoveTopCroppingBorderAndDoCrop() throws Exception {
		drawPlus(getWorkingBitmap(), originalWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onView(isRoot())
				.perform(touchAt(getToolPositionOnScreen()));

		final int width = getWorkingBitmap().getWidth();
		final PointF toolPosition = getToolPosition();

		for (int i = 0; i < 4; i++) {
			final float newSelectionBoxHeight = getSelectionBoxHeight() / 2;
			setSelectionBoxHeight(newSelectionBoxHeight);
			toolPosition.y += newSelectionBoxHeight / 2;

			onView(isRoot())
					.perform(touchAt(getToolPositionOnScreen()));

			assertWorkingBitmapDimensions(width, (int) newSelectionBoxHeight);
		}
	}

	@Ignore("Fix getBitmapPositionOnScreen y")
	@Test
	public void testMoveBottomCroppingBorderAndDoCrop() throws Exception {
		drawPlus(getWorkingBitmap(), originalWidth / 2);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onView(isRoot())
				.perform(touchAt(getToolPositionOnScreen()));

		final int width = getWorkingBitmap().getWidth();
		final PointF toolPosition = getToolPosition();

		for (int i = 0; i < 4; i++) {
			final float newSelectionBoxHeight = getSelectionBoxHeight() / 2;
			setSelectionBoxHeight(newSelectionBoxHeight);
			toolPosition.y += newSelectionBoxHeight / 2;

			onView(isRoot())
					.perform(touchAt(getToolPositionOnScreen()));

			assertWorkingBitmapDimensions(width, (int) newSelectionBoxHeight);
		}
	}

	@Ignore("Elaborate test should be split")
	@Test
	public void testCropFromEverySideOnFilledBitmap() throws Exception {
	}

	@Test
	public void testResizeBordersMatchBitmapBordersAfterCrop() throws Exception {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptions();

		drawPlus(getWorkingBitmap(), originalWidth / 2);

		setSelectionBoxWidth(originalWidth / 8);
		setSelectionBoxHeight(originalHeight / 8);

		onView(isRoot())
				.perform(touchAt(getToolPositionOnScreen()));

		final Bitmap croppedBitmap = getWorkingBitmap();
		final int height = croppedBitmap.getHeight();
		final int width = croppedBitmap.getWidth();

		final TransformTool tool = (TransformTool) PaintroidApplication.currentTool;
		assertEquals(0.0f, tool.resizeBoundWidthXLeft, Float.MIN_VALUE);
		assertEquals(width - 1, tool.resizeBoundWidthXRight, Float.MIN_VALUE);
		assertEquals(0.0f, tool.resizeBoundHeightYTop, Float.MIN_VALUE);
		assertEquals(height - 1, tool.resizeBoundHeightYBottom, Float.MIN_VALUE);
	}

	@Ignore("Replace with unit test")
	@Test
	public void testNoMaximumBorderRatio() throws Exception {
	}

	@Ignore("Replace with unit test")
	@Test
	public void testPreventTooLargeBitmaps() throws Exception {
	}

	@Test
	public void testMaxImageResolution() throws Exception {
		final int maxBitmapSize = displayHeight * displayWidth
				* (int) TransformTool.MAXIMUM_BITMAP_SIZE_FACTOR;
		final int maxWidth = maxBitmapSize / originalHeight;

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptions();

		final float zoomFactor = PaintroidApplication.perspective.getScaleForCenterBitmap() * .25f;
		PaintroidApplication.perspective.setScale(zoomFactor);

		PointF dragFrom = getBitmapPositionOnScreen(new PointF(originalWidth, originalHeight));
		PointF dragTo = getBitmapPositionOnScreen(new PointF(maxWidth + 10, originalHeight));

		onView(isRoot())
				.perform(swipe(dragFrom, dragTo));

		onView(isRoot())
				.perform(touchAt(getToolPositionOnScreen()));

		final Bitmap enlargedBitmap = getWorkingBitmap();
		final int bitmapSize = enlargedBitmap.getHeight() + enlargedBitmap.getWidth();
		assertTrue(bitmapSize < maxBitmapSize);
	}

	@LargeTest
	@Test
	public void testMaxImageResolutionToast() throws Exception {
		final int maxBitmapSize = displayHeight * displayWidth
				* (int) TransformTool.MAXIMUM_BITMAP_SIZE_FACTOR;
		final int maxWidth = maxBitmapSize / originalHeight;

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptions();

		final float zoomFactor = PaintroidApplication.perspective.getScaleForCenterBitmap() * .25f;
		PaintroidApplication.perspective.setScale(zoomFactor);

		PointF dragFrom = getBitmapPositionOnScreen(new PointF(originalWidth, originalHeight));
		PointF dragTo = getBitmapPositionOnScreen(new PointF(maxWidth + 10, originalHeight));

		onView(isRoot())
				.perform(swipe(dragFrom, dragTo));

		waitForToast(withText(R.string.resize_max_image_resolution_reached), 5000);
	}

	@Ignore("Elaborate test should be split")
	@Test
	public void testEnlargeEverySideAndCheckEnlargedColor() throws Exception {
	}

	@Test
	public void testResizeWithPartialOverlapping() throws Exception {
		onToolBarView()
				.performSelectTool(ToolType.FILL);
		onView(isRoot())
				.perform(touchAt(originalWidth / 2, originalHeight / 2));

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptions();

		final PointF toolPosition = getToolPosition();
		toolPosition.set(new PointF(originalWidth, originalHeight));

		onView(isRoot())
				.perform(touchAt(getToolPositionOnScreen()));

		assertWorkingBitmapDimensions(originalWidth, originalHeight);

		onToolBarView()
				.performOpenToolOptions();
		onTransformToolOptionsView()
				.performAutoCrop();

		assertEquals(originalWidth / 2, getSelectionBoxWidth(), Float.MIN_VALUE);
		assertEquals(originalHeight / 2, getSelectionBoxHeight(), Float.MIN_VALUE);
	}

	@Test
	public void testResizeBoxCompletelyOutsideBitmap() throws Exception {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptions();

		final float zoomFactor = PaintroidApplication.perspective.getScaleForCenterBitmap() * .25f;
		PaintroidApplication.perspective.setScale(zoomFactor);

		final PointF toolPosition = getToolPosition();
		toolPosition.set(new PointF(
				originalWidth + originalHeight / 2,
				originalHeight + originalHeight / 2));

		setSelectionBoxHeight(originalHeight / 2);
		setSelectionBoxWidth(originalWidth / 2);

		onView(isRoot())
				.perform(touchAt(getToolPositionOnScreen()));

		assertWorkingBitmapDimensions(originalWidth, originalHeight);
	}

	@LargeTest
	@Test
	public void testResizeBoxCompletelyOutsideBitmapToast() throws Exception {
		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM)
				.performCloseToolOptions();

		final float zoomFactor = PaintroidApplication.perspective.getScaleForCenterBitmap() * .25f;
		PaintroidApplication.perspective.setScale(zoomFactor);

		final PointF toolPosition = getToolPosition();
		toolPosition.set(new PointF(
				originalWidth + originalHeight / 2,
				originalHeight + originalHeight / 2));

		onView(isRoot())
				.perform(touchAt(getToolPositionOnScreen()));

		waitForToast(withText(R.string.resize_nothing_to_resize), 5000);
	}

	@Test
	public void testRotateMultipleLayers() {
		ArrayList<Layer> layers = LayerListener.getInstance().getAdapter().getLayers();
		final Bitmap layerImage = layers.get(0).getImage();
		int bitmapHeightOnStartup = layerImage.getHeight();
		int bitmapWidthOnStartup = layerImage.getWidth();

		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.performAddLayer()
				.performClose();

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		assertLayerDimensions(bitmapHeightOnStartup, bitmapWidthOnStartup);

		onTransformToolOptionsView()
				.performRotateCounterClockwise();
		assertLayerDimensions(bitmapWidthOnStartup, bitmapHeightOnStartup);
	}

	@Test
	public void testRotateMultipleLayersUndoRedo() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Layer> layers = LayerListener.getInstance().getAdapter().getLayers();
		final Bitmap layerImage = layers.get(0).getImage();
		int bitmapHeightOnStartup = layerImage.getHeight();
		int bitmapWidthOnStartup = layerImage.getWidth();

		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.performAddLayer()
				.performClose();

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		assertLayerDimensions(bitmapHeightOnStartup, bitmapWidthOnStartup);

		onToolBarView()
				.performCloseToolOptions();
		onTopBarView()
				.performUndo();
		assertLayerDimensions(bitmapWidthOnStartup, bitmapHeightOnStartup);

		onTopBarView()
				.performRedo();
		assertLayerDimensions(bitmapHeightOnStartup, bitmapWidthOnStartup);
	}

	@Test
	public void testRotateMultipleLayersUndoRedoWhenRotatingWasNotLastCommand() throws NoSuchFieldException, IllegalAccessException {

		final ArrayList<Layer> layers = LayerListener.getInstance().getAdapter().getLayers();
		final Bitmap layerImage = layers.get(0).getImage();
		final int bitmapHeightOnStartup = layerImage.getHeight();
		final int bitmapWidthOnStartup = layerImage.getWidth();

		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.performAddLayer()
				.performClose();

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();
		assertLayerDimensions(bitmapHeightOnStartup, bitmapWidthOnStartup);

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
		onView(isRoot())
				.perform(touchAt(pointOnScreenLeft), touchAt(pointOnScreenRight));

		onLayerMenuView()
				.performOpen()
				.performSelectLayer(1)
				.performClose();

		onTopBarView()
				.performUndo();
		assertLayerDimensions(bitmapWidthOnStartup, bitmapHeightOnStartup);

		onTopBarView().onUndoButton()
				.check(matches(withDrawable(R.drawable.icon_menu_undo)));
		onTopBarView().onRedoButton()
				.check(matches(withDrawable(R.drawable.icon_menu_redo)));

		onLayerMenuView()
				.performOpen()
				.performSelectLayer(0);

		onTopBarView().onUndoButton()
				.check(matches(withDrawable(R.drawable.icon_menu_undo)));
		onTopBarView().onRedoButton()
				.check(matches(withDrawable(R.drawable.icon_menu_redo_disabled)));

		onLayerMenuView()
				.performSelectLayer(1)
				.performClose();

		onView(isRoot())
				.perform(touchAt(pointOnScreenMiddle));

		onLayerMenuView()
				.performOpen()
				.performSelectLayer(2);

		assertLayerDimensions(bitmapWidthOnStartup, bitmapHeightOnStartup);
	}
}
