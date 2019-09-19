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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.ShapeTool;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.pressBack;

import static org.catrobat.paintroid.test.espresso.util.OffsetLocationProvider.withOffset;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ShapeToolOptionsViewInteraction.onShapeToolOptionsView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RectangleFillToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);
	private Workspace workspace;
	private Perspective perspective;
	private ToolReference toolReference;

	@Before
	public void setUp() {
		MainActivity activity = launchActivityRule.getActivity();
		workspace = activity.workspace;
		perspective = activity.perspective;
		toolReference = activity.toolReference;

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
	}

	@Test
	public void testFilledRectIsCreated() {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);

		BaseToolWithRectangleShape rectangleFillTool = (BaseToolWithRectangleShape) toolReference.get();
		float rectWidth = rectangleFillTool.boxWidth;
		float rectHeight = rectangleFillTool.boxHeight;
		PointF rectPosition = rectangleFillTool.toolPosition;

		assertNotEquals(0.0f, rectWidth);
		assertNotEquals(0.0f, rectHeight);
		assertNotNull(rectPosition);
	}

	@Test
	public void testEllipseIsDrawnOnBitmap() {
		workspace.setScale(1.0f);

		onToolBarView()
				.performSelectTool(ToolType.SHAPE);
		onShapeToolOptionsView()
				.performSelectShape(ShapeTool.BaseShape.OVAL);

		BaseToolWithRectangleShape ellipseTool = (BaseToolWithRectangleShape) toolReference.get();
		float rectHeight = ellipseTool.boxHeight;

		onToolBarView()
				.performCloseToolOptionsView();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		pressBack();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
		onToolProperties()
				.checkMatchesColor(Color.BLACK);

		onTopBarView()
				.performUndo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

		onTopBarView()
				.performRedo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
				.checkPixelColor(Color.BLACK, withOffset(BitmapLocationProvider.MIDDLE, (int) (rectHeight / 2.5f), 0))
				.checkPixelColor(Color.TRANSPARENT, withOffset(BitmapLocationProvider.MIDDLE, (int) (rectHeight / 2.5f), (int) (rectHeight / 2.5f)));
	}

	@Test
	public void testRectOnBitmapHasSameColorAsInColorPickerAfterColorChange() {
		onToolProperties()
				.setColorResource(R.color.pocketpaint_color_picker_brown1)
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_brown1);

		onToolBarView()
				.performSelectTool(ToolType.SHAPE);

		BaseToolWithRectangleShape rectangleFillTool = (BaseToolWithRectangleShape) toolReference.get();

		Bitmap drawingBitmap = rectangleFillTool.drawingBitmap;
		int colorInRectangle = drawingBitmap.getPixel(drawingBitmap.getWidth() / 2, drawingBitmap.getHeight() / 2);

		onToolProperties()
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_brown1)
				.checkMatchesColor(colorInRectangle);
	}

	@Test
	public void testFilledRectChangesColor() {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);

		BaseToolWithRectangleShape rectangleFillTool = (BaseToolWithRectangleShape) toolReference.get();

		Bitmap drawingBitmap = rectangleFillTool.drawingBitmap;
		int colorInRectangle = drawingBitmap.getPixel(drawingBitmap.getWidth() / 2, drawingBitmap.getHeight() / 2);

		onToolProperties()
				.checkMatchesColor(colorInRectangle)
				.checkMatchesColor(Color.BLACK);

		onToolProperties()
				.setColorResource(R.color.pocketpaint_color_picker_brown1);

		Bitmap drawingBitmapAfter = rectangleFillTool.drawingBitmap;
		int colorInRectangleAfter = drawingBitmapAfter.getPixel(drawingBitmap.getWidth() / 2, drawingBitmap.getHeight() / 2);

		onToolProperties()
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_brown1)
				.checkMatchesColor(colorInRectangleAfter);
	}

	@Test
	public void testEraseWithEllipse() {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);
		onShapeToolOptionsView()
				.performSelectShape(ShapeTool.BaseShape.RECTANGLE);
		selectShapeTypeAndDraw(false, Color.TRANSPARENT);

		onToolBarView()
				.performClickSelectedToolButton();

		onShapeToolOptionsView()
				.performSelectShape(ShapeTool.BaseShape.OVAL);
		selectShapeTypeAndDraw(true, Color.TRANSPARENT);
	}

	@Test
	public void testDrawWithDrawableShape() {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);
		onShapeToolOptionsView()
				.performSelectShape(ShapeTool.BaseShape.HEART);
		selectShapeTypeAndDraw(false, Color.BLACK);
	}

	@Test
	public void testCheckeredBackgroundWhenTransparentColorSelected() {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);

		onShapeToolOptionsView()
				.performSelectShape(ShapeTool.BaseShape.HEART);

		onToolBarView()
				.performCloseToolOptionsView();

		onToolProperties()
				.setColor(Color.TRANSPARENT);

		BaseToolWithRectangleShape tool = (BaseToolWithRectangleShape) toolReference.get();
		Bitmap drawingBitmap = tool.drawingBitmap;
		int width = drawingBitmap.getWidth();
		int height = drawingBitmap.getHeight();
		Point upperLeftQuarter = new Point((int) (width * 0.25), (int) (height * 0.25));
		Point upperRightQuarter = new Point((int) (width * 0.75), (int) (height * 0.25));

		int checkeredWhite = Color.WHITE;
		int checkeredGray = 0xFFC0C0C0;

		int pixelColor = drawingBitmap.getPixel(upperLeftQuarter.x, upperLeftQuarter.y);
		assertTrue("Color should correspond to checkered pattern", pixelColor == checkeredGray || pixelColor == checkeredWhite);

		pixelColor = drawingBitmap.getPixel(upperRightQuarter.x, upperRightQuarter.y);
		assertTrue("Color should correspond to checkered pattern", pixelColor == checkeredGray || pixelColor == checkeredWhite);
	}

	@Test
	public void testEraseWithHeartShape() {
		perspective.setScale(1.0f);

		onToolBarView()
				.performSelectTool(ToolType.SHAPE);
		onShapeToolOptionsView()
				.performSelectShape(ShapeTool.BaseShape.RECTANGLE);
		selectShapeTypeAndDraw(true, Color.BLACK);

		onToolBarView()
				.performOpenToolOptionsView();
		onShapeToolOptionsView()
				.performSelectShape(ShapeTool.BaseShape.HEART);
		selectShapeTypeAndDraw(true, Color.TRANSPARENT);

		BaseToolWithRectangleShape tool = (BaseToolWithRectangleShape) toolReference.get();
		Bitmap drawingBitmap = tool.drawingBitmap;
		int boxWidth = drawingBitmap.getWidth();
		int boxHeight = drawingBitmap.getHeight();
		PointF toolPosition = tool.toolPosition;

		Point upperLeftPixel = new Point((int) (toolPosition.x - boxWidth / 4), (int) (toolPosition.y - boxHeight / 4));
		Point upperRightPixel = new Point((int) (toolPosition.x + boxWidth / 4), (int) (toolPosition.y - boxHeight / 4));

		Bitmap bitmap = workspace.getBitmapOfCurrentLayer();

		int pixelColor = bitmap.getPixel(upperLeftPixel.x, upperLeftPixel.y);
		assertEquals("Pixel should have been erased", Color.TRANSPARENT, pixelColor);

		pixelColor = bitmap.getPixel(upperRightPixel.x, upperRightPixel.y);
		assertEquals("Pixel should have been erased", Color.TRANSPARENT, pixelColor);
	}

	public void selectShapeTypeAndDraw(boolean changeColor, int color) {
		BaseToolWithRectangleShape tool = (BaseToolWithRectangleShape) toolReference.get();
		PointF centerPointTool = tool.toolPosition;

		PointF pointUnderTest = new PointF(centerPointTool.x, centerPointTool.y);

		onToolBarView()
				.performCloseToolOptionsView();

		if (changeColor) {
			onToolProperties()
					.setColor(color);

			float rectWidth = tool.boxWidth;
			float rectHeight = tool.boxHeight;
			Bitmap drawingBitmap = tool.drawingBitmap;

			int colorInRectangle = drawingBitmap.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
			if (Color.alpha(color) == 0x00) {
				assertThat(colorInRectangle, is(anyOf(is(Color.WHITE), is(0xFFC0C0C0))));
			} else {
				assertEquals(color, colorInRectangle);
			}
		}

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		int colorAfterDrawing = workspace.getPixelOfCurrentLayer(pointUnderTest);
		onToolProperties()
				.checkMatchesColor(colorAfterDrawing);
	}
}
