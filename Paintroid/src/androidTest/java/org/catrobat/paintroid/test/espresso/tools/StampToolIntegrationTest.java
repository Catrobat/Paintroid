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

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.action.Tapper;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.UiInteractions;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.StampTool;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.convertFromCanvasToScreen;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getActionbarHeight;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getScreenPointFromSurfaceCoordinates;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getStatusbarHeight;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getSurfaceCenterX;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getSurfaceCenterY;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getSurfacePointFromScreenPoint;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getToolMemberBoxPosition;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetColorPicker;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetDrawPaintAndBrushPickerView;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.waitForToast;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchLongAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class StampToolIntegrationTest {

	private static final int Y_CLICK_OFFSET = 25;
	private static final float SCALE_25 = 0.25f;
	private static final float STAMP_RESIZE_FACTOR = 1.5f;
	// Rotation test
	private static final float SQUARE_LENGTH = 300;
	private static final int MIN_ROTATION = -450;
	private static final int MAX_ROTATION = 450;
	private static final int ROTATION_STEPSIZE = 30;
	private static final float ROTATION_TOLERANCE = 10;
	private static Tapper tapStampLong = UiInteractions.DefinedLongTap.withPressTimeout(1500);
	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Before
	public void setUp() {
		PaintroidApplication.drawingSurface.destroyDrawingCache();

		selectTool(ToolType.BRUSH);
		resetColorPicker();
		resetDrawPaintAndBrushPickerView();
	}

	@Test
	public void testBoundingboxAlgorithm() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		PaintroidApplication.perspective.setScale(1.0f);

		PointF surfaceCenterPoint = getScreenPointFromSurfaceCoordinates(getSurfaceCenterX(), getSurfaceCenterY());
		onView(isRoot()).perform(touchAt(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET - (SQUARE_LENGTH / 3)));

		selectTool(ToolType.STAMP);

		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;

		stampTool.toolPosition.set(surfaceCenterPoint);
		stampTool.boxWidth = SQUARE_LENGTH;
		stampTool.boxHeight = SQUARE_LENGTH;

		for (int rotationOfStampBox = MIN_ROTATION; rotationOfStampBox < MAX_ROTATION; rotationOfStampBox += ROTATION_STEPSIZE) {

			stampTool.boxRotation = rotationOfStampBox;

			invokeCreateAndSetBitmap(stampTool);

			Bitmap copyOfToolBitmap = stampTool.drawingBitmap.copy(Bitmap.Config.ARGB_8888, false);

			float width = copyOfToolBitmap.getWidth();
			float height = copyOfToolBitmap.getHeight();

			// Find one of the black pixels

			PointF pixelFound = null;
			int[] pixelLine = new int[(int) width + 1];
			for (int drawingBitmapYCoordinate = 0; drawingBitmapYCoordinate < height; drawingBitmapYCoordinate++) {
				copyOfToolBitmap.getPixels(pixelLine, 0, (int) width, 0, drawingBitmapYCoordinate, (int) width, 1);
				for (int drawingBitmapXCoordinate = 0; drawingBitmapXCoordinate < width; drawingBitmapXCoordinate++) {
					int pixelColor = pixelLine[drawingBitmapXCoordinate];
					if (pixelColor != 0) {
						pixelFound = new PointF(drawingBitmapXCoordinate, drawingBitmapYCoordinate);
						break;
					}
				}
				if (pixelFound != null) {
					break;
				}
			}

			copyOfToolBitmap.recycle();

			assertNotNull("The drawn black spot should be found by the stamp, but was not in the Bitmap after rotation", pixelFound);

			// Check if the line from found pixel to center has a fitting rotation value

			// angle of line = (x, y) to vector = (a,b) = (0,1)
			float x = (SQUARE_LENGTH / 2) - pixelFound.x;
			float y = (SQUARE_LENGTH / 2) - pixelFound.y;
			float a = 0f;
			float b = 1f;

			double angle = Math.acos((x * a + y * b) / (Math.sqrt(x * x + y * y) * Math.sqrt(a * a + b * b)));
			angle = Math.toDegrees(angle);

			float rotationPositive = rotationOfStampBox;
			if (rotationPositive < 0.0) {
				rotationPositive = -rotationPositive;
			}

			while (rotationPositive > 360.0) {
				rotationPositive -= 360.0;
			}

			if (rotationPositive > 180.0) {
				rotationPositive = 360 - rotationPositive;
			}

			boolean rotationOk = (rotationPositive + ROTATION_TOLERANCE > angle) && (rotationPositive - ROTATION_TOLERANCE < angle);
			assertTrue("Wrong rotationvalue was calculated", rotationOk);
		}
	}

	@Test
	public void testCopyPixel() throws NoSuchFieldException, IllegalAccessException {
		PointF surfaceCenterPoint = getScreenPointFromSurfaceCoordinates(getSurfaceCenterX(), getSurfaceCenterY());
		onView(isRoot()).perform(touchAt(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET));

		selectTool(ToolType.STAMP);

		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		PointF toolPosition = new PointF(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET);
		stampTool.toolPosition.set(toolPosition);

		clickInStampBox(tapStampLong);

		PointF pixelCoordinateToControlColor = new PointF(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET);
		PointF surfacePoint = getSurfacePointFromScreenPoint(pixelCoordinateToControlColor);
		int pixelToControl = PaintroidApplication.drawingSurface.getPixel(PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(surfacePoint));

		assertEquals("First Pixel not Black after using Stamp for copying", Color.BLACK, pixelToControl);

		int moveOffset = 100;

		toolPosition.y = toolPosition.y - moveOffset;
		stampTool.toolPosition.set(toolPosition);

		clickInStampBox(Tap.SINGLE);

		toolPosition.y = toolPosition.y - moveOffset;
		stampTool.toolPosition.set(toolPosition);

		pixelCoordinateToControlColor = new PointF(toolPosition.x, toolPosition.y + moveOffset + Y_CLICK_OFFSET);
		surfacePoint = getSurfacePointFromScreenPoint(pixelCoordinateToControlColor);
		pixelToControl = PaintroidApplication.drawingSurface.getPixel(PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(surfacePoint));

		assertEquals("Second Pixel not Black after using Stamp for copying", Color.BLACK, pixelToControl);
	}

	@Test
	public void testStampOutsideDrawingSurface() throws NoSuchFieldException, IllegalAccessException {
		onView(isRoot()).perform(touchAt(getSurfaceCenterX(), getSurfaceCenterY() + getActionbarHeight() + getStatusbarHeight() - Y_CLICK_OFFSET));

		int screenWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
		int screenHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
		PaintroidApplication.perspective.setScale(SCALE_25);

		selectTool(ToolType.STAMP);

		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		PointF toolPosition = new PointF(getSurfaceCenterX(), getSurfaceCenterY());
		stampTool.toolPosition.set(toolPosition);
		stampTool.boxWidth = (int) (screenWidth * STAMP_RESIZE_FACTOR);
		stampTool.boxHeight = (int) (screenHeight * STAMP_RESIZE_FACTOR);

		onView(isRoot()).perform(touchLongAt(getSurfaceCenterX(), getSurfaceCenterY() + getActionbarHeight() + getStatusbarHeight() - Y_CLICK_OFFSET));

		Bitmap drawingBitmap = stampTool.drawingBitmap.copy(Bitmap.Config.ARGB_8888, false);

		assertNotNull("After activating stamp, drawingBitmap should not be null anymore", drawingBitmap);

		drawingBitmap.recycle();
		drawingBitmap = null;
	}

	@Test
	public void testCopyToastIsShown() throws NoSuchFieldException, IllegalAccessException {
		selectTool(ToolType.STAMP);

		clickInStampBox(Tap.SINGLE);

		waitForToast(withText(R.string.stamp_tool_copy_hint), 3000);
	}

	@Test
	public void testBitmapSavedOnOrientationChange() throws NoSuchFieldException, IllegalAccessException {
		onView(withId(R.id.drawingSurfaceView))
				.perform(click());

		onToolBarView()
				.performSelectTool(ToolType.STAMP);

		Bitmap emptyBitmap = Bitmap.createBitmap(((BaseToolWithRectangleShape)
				PaintroidApplication.currentTool).drawingBitmap);

		clickInStampBox(tapStampLong);

		Bitmap expectedBitmap = Bitmap.createBitmap(((BaseToolWithRectangleShape)
				PaintroidApplication.currentTool).drawingBitmap);

		assertFalse(expectedBitmap.sameAs(emptyBitmap));

		launchActivityRule.getActivity()
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Bitmap actualBitmap = Bitmap.createBitmap(((BaseToolWithRectangleShape)
				PaintroidApplication.currentTool).drawingBitmap);

		assertTrue(expectedBitmap.sameAs(actualBitmap));
	}

	private void invokeCreateAndSetBitmap(Object object) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method method = object.getClass().getDeclaredMethod("createAndSetBitmap");
		method.setAccessible(true);

		Object[] parameters = new Object[0];
		method.invoke(object, parameters);
	}

	private void clickInStampBox(Tapper tapStyle) throws NoSuchFieldException, IllegalAccessException {
		PointF boxCenter = getToolMemberBoxPosition();
		PointF screenPoint = convertFromCanvasToScreen(boxCenter, PaintroidApplication.perspective);

		onView(isRoot()).perform(touchAt(screenPoint, tapStyle));
	}
}
