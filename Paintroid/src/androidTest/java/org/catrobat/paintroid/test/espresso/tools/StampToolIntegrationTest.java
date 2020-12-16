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

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.espresso.util.UiInteractions;
import org.catrobat.paintroid.test.espresso.util.wrappers.StampToolViewInteraction;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.StampTool;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.action.Tapper;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getScreenPointFromSurfaceCoordinates;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.StampToolViewInteraction.Companion;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

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

	private Workspace workspace;
	private Perspective perspective;
	private ToolReference toolReference;

	@Before
	public void setUp() {
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
		MainActivity activity = launchActivityRule.getActivity();
		workspace = activity.workspace;
		perspective = activity.perspective;
		toolReference = activity.toolReference;
	}

	@Ignore("Causes crashes on jenkins")
	@Test
	public void testBoundingboxAlgorithm() {
		perspective.setScale(1.0f);

		PointF surfaceCenterPoint = getScreenPointFromSurfaceCoordinates(perspective.surfaceCenterX, perspective.surfaceCenterY);
		onView(isRoot()).perform(touchAt(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET - (SQUARE_LENGTH / 3)));

		onToolBarView()
				.performSelectTool(ToolType.STAMP);

		StampTool stampTool = (StampTool) toolReference.get();

		stampTool.toolPosition.set(surfaceCenterPoint);
		stampTool.boxWidth = SQUARE_LENGTH;
		stampTool.boxHeight = SQUARE_LENGTH;

		for (int rotationOfStampBox = MIN_ROTATION; rotationOfStampBox < MAX_ROTATION; rotationOfStampBox += ROTATION_STEPSIZE) {

			stampTool.boxRotation = rotationOfStampBox;
			stampTool.copyBoxContent();

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

			assertEquals(rotationPositive, angle, ROTATION_TOLERANCE);
		}
	}

	@Test
	public void testCopyPixel() {

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.STAMP);

		StampToolViewInteraction.Companion.onStampToolViewInteraction()
				.performCopy();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION, tapStampLong));

		StampTool stampTool = (StampTool) toolReference.get();
		stampTool.toolPosition.set(stampTool.toolPosition.x, stampTool.toolPosition.y * .5f);

		StampToolViewInteraction.Companion.onStampToolViewInteraction()
				.performPaste();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, stampTool.toolPosition.x, stampTool.toolPosition.y);
	}

	@Test
	public void testStampToolNotCapturingOtherLayers() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.STAMP);

		onLayerMenuView()
				.performOpen()
				.performAddLayer();

		onLayerMenuView()
				.performClose();

		Companion.onStampToolViewInteraction()
				.performCopy();

		StampTool stampTool = (StampTool) toolReference.get();
		stampTool.toolPosition.set(stampTool.toolPosition.x, stampTool.toolPosition.y * .5f);

		Companion.onStampToolViewInteraction()
				.performPaste();

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, stampTool.toolPosition.x, stampTool.toolPosition.y * .5f);
	}

	@Test
	public void testStampOutsideDrawingSurface() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		int bitmapWidth = workspace.getWidth();
		int bitmapHeight = workspace.getHeight();
		perspective.setScale(SCALE_25);

		onToolBarView()
				.performSelectTool(ToolType.STAMP);

		StampTool stampTool = (StampTool) toolReference.get();
		PointF toolPosition = new PointF(perspective.surfaceCenterX, perspective.surfaceCenterY);
		stampTool.toolPosition.set(toolPosition);
		stampTool.boxWidth = (int) (bitmapWidth * STAMP_RESIZE_FACTOR);
		stampTool.boxHeight = (int) (bitmapHeight * STAMP_RESIZE_FACTOR);

		Companion.onStampToolViewInteraction()
				.performPaste();

		assertNotNull(stampTool.drawingBitmap);
	}

	@Test
	public void testBitmapSavedOnOrientationChange() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.STAMP);

		Bitmap emptyBitmap = Bitmap.createBitmap(((BaseToolWithRectangleShape)
				toolReference.get()).drawingBitmap);

		Companion.onStampToolViewInteraction()
				.performCopy();

		Bitmap expectedBitmap = Bitmap.createBitmap(((BaseToolWithRectangleShape)
				toolReference.get()).drawingBitmap);

		assertFalse(expectedBitmap.sameAs(emptyBitmap));

		launchActivityRule.getActivity()
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Bitmap actualBitmap = Bitmap.createBitmap(((BaseToolWithRectangleShape)
				toolReference.get()).drawingBitmap);

		assertTrue(expectedBitmap.sameAs(actualBitmap));
	}
}
