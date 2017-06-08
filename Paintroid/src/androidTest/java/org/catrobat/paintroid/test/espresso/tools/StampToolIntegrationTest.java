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
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.action.Tapper;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.test.espresso.util.UiInteractions;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.tools.implementation.StampTool;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.FIELD_NAME_WORKING_BITMAP;
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
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.waitMillis;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchLongAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.isToast;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class StampToolIntegrationTest {

	private static Tapper TAP_STAMP_LONG = UiInteractions.DefinedLongTap.withPressTimeout(1500);

	private static final int Y_CLICK_OFFSET = 25;
	private static final float SCALE_25 = 0.25f;
	private static final float STAMP_RESIZE_FACTOR = 1.5f;

	// Rotation test
	private static final float SQUARE_LENGTH = 300;
	private static final float MIN_ROTATION = -450f;
	private static final float MAX_ROTATION = 450f;
	private static final float ROTATION_STEPSIZE = 30.0f;
	private static final float ROTATION_TOLERANCE = 10;

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

		PointF toolPosition = new PointF(surfaceCenterPoint.x, surfaceCenterPoint.y);
		PrivateAccess.setMemberValue(BaseToolWithShape.class, stampTool, EspressoUtils.FIELD_NAME_TOOL_POSITION, toolPosition);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxWidth", SQUARE_LENGTH);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxHeight", SQUARE_LENGTH);

		Bitmap copyOfToolBitmap = null;

		for (float rotationOfStampBox = MIN_ROTATION; rotationOfStampBox < MAX_ROTATION; rotationOfStampBox = rotationOfStampBox
				+ ROTATION_STEPSIZE) {
			PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxRotation", (int) (rotationOfStampBox));

			invokeCreateAndSetBitmap(stampTool, PaintroidApplication.drawingSurface);

			copyOfToolBitmap = ((Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, stampTool, "mDrawingBitmap")).copy(Bitmap.Config.ARGB_8888, false);

			float width = copyOfToolBitmap.getWidth();
			float height = copyOfToolBitmap.getHeight();

			// Find one of the black pixels

			PointF pixelFound = null;
			int[] pixelLine = new int[(int) width + 1];
			for (int drawingBitmapYCoordinate = 0; drawingBitmapYCoordinate < height; drawingBitmapYCoordinate++) {
				copyOfToolBitmap.getPixels(pixelLine, 0, (int) width, 0, drawingBitmapYCoordinate, (int) width, 1);
				for (int drawningBitmapXCoordinate = 0; drawningBitmapXCoordinate < width; drawningBitmapXCoordinate++) {
					int pixelColor = pixelLine[drawningBitmapXCoordinate];
					if (pixelColor != 0) {
						pixelFound = new PointF(drawningBitmapXCoordinate, drawingBitmapYCoordinate);
						break;
					}
				}
				if (pixelFound != null) {
					break;
				}
			}

			copyOfToolBitmap.recycle();
			copyOfToolBitmap = null;
			System.gc();

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
			assertEquals("Wrong rotationvalue was calculated", true, rotationOk);
		}
	}

	@Test
	public void testCopyPixel() throws NoSuchFieldException, IllegalAccessException {
		PointF surfaceCenterPoint = getScreenPointFromSurfaceCoordinates(getSurfaceCenterX(), getSurfaceCenterY());
		onView(isRoot()).perform(touchAt(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET));

		selectTool(ToolType.STAMP);

		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		PointF toolPosition = new PointF(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET);
		PrivateAccess.setMemberValue(BaseToolWithShape.class, stampTool, EspressoUtils.FIELD_NAME_TOOL_POSITION, toolPosition);

		clickInStampBox(TAP_STAMP_LONG);

		PointF pixelCoordinateToControlColor = new PointF(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET);
		PointF surfacePoint = getSurfacePointFromScreenPoint(pixelCoordinateToControlColor);
		int pixelToControl = PaintroidApplication.drawingSurface.getPixel(PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(surfacePoint));

		assertEquals("First Pixel not Black after using Stamp for copying", Color.BLACK, pixelToControl);

		int moveOffset = 100;

		toolPosition.y = toolPosition.y - moveOffset;
		PrivateAccess.setMemberValue(BaseToolWithShape.class, stampTool, EspressoUtils.FIELD_NAME_TOOL_POSITION, toolPosition);

		clickInStampBox(Tap.SINGLE);

		toolPosition.y = toolPosition.y - moveOffset;
		PrivateAccess.setMemberValue(BaseToolWithShape.class, stampTool, EspressoUtils.FIELD_NAME_TOOL_POSITION, toolPosition);

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
		PrivateAccess.setMemberValue(Perspective.class, PaintroidApplication.perspective, "mSurfaceScale", SCALE_25);

		selectTool(ToolType.STAMP);

		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		PointF toolPosition = new PointF(getSurfaceCenterX(), getSurfaceCenterY());
		PrivateAccess.setMemberValue(BaseToolWithShape.class, stampTool, EspressoUtils.FIELD_NAME_TOOL_POSITION, toolPosition);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxWidth", (int) (screenWidth * STAMP_RESIZE_FACTOR));
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxHeight", (int) (screenHeight * STAMP_RESIZE_FACTOR));

		onView(isRoot()).perform(touchLongAt(getSurfaceCenterX(), getSurfaceCenterY() + getActionbarHeight() + getStatusbarHeight() - Y_CLICK_OFFSET));

		Bitmap drawingBitmap = ((Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, stampTool, "mDrawingBitmap")).copy(Bitmap.Config.ARGB_8888, false);

		assertNotNull("After activating stamp, mDrawingBitmap should not be null anymore", drawingBitmap);

		drawingBitmap.recycle();
		drawingBitmap = null;
	}

	@Test
	public void testCopyToastIsShown() throws NoSuchFieldException, IllegalAccessException {
		selectTool(ToolType.STAMP);

		// TODO: idling resource for toast
		// Wait until tool name toast disappears (caused by selectTool)
		waitMillis(3000);

		clickInStampBox(Tap.SINGLE);

		onView(withText(R.string.stamp_tool_copy_hint))
				.inRoot(isToast())
				.check(matches(isDisplayed()));
	}

	private void invokeCreateAndSetBitmap(Object object, Object parameter) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
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
