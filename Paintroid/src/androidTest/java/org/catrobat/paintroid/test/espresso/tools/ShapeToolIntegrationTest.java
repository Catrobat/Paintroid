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

import android.graphics.Color;
import android.graphics.Paint;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.drawable.DrawableShape;
import org.catrobat.paintroid.tools.drawable.DrawableStyle;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.ShapeTool;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.OffsetLocationProvider.withOffset;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterLeft;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ShapeToolOptionsViewInteraction.onShapeToolOptionsView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShapeToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);
	private ToolReference toolReference;
	private MainActivity mainActivity;

	@Before
	public void setUp() {
		mainActivity = launchActivityRule.getActivity();
		toolReference = mainActivity.toolReference;

		onToolBarView()
				.performSelectTool(ToolType.SHAPE);
	}

	private Paint getCurrentToolBitmapPaint() {
		return ((ShapeTool) toolReference.get()).getShapeBitmapPaint();
	}

	private Paint getToolPaint() {
		return mainActivity.toolPaint.getPaint();
	}

	@Test
	public void testEllipseIsDrawnOnBitmap() {
		onShapeToolOptionsView()
				.performSelectShape(DrawableShape.OVAL);

		BaseToolWithRectangleShape ellipseTool = (BaseToolWithRectangleShape) toolReference.get();
		float rectHeight = ellipseTool.boxHeight;

		onToolBarView()
				.performCloseToolOptionsView();

		onTopBarView()
				.performClickCheckmark();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
				.checkPixelColor(Color.BLACK, withOffset(BitmapLocationProvider.MIDDLE, (int) (rectHeight / 2.5f), 0))
				.checkPixelColor(Color.TRANSPARENT, withOffset(BitmapLocationProvider.MIDDLE, (int) (rectHeight / 2.5f), (int) (rectHeight / 2.5f)));
	}

	@Test
	public void testUndoRedo() {
		onToolBarView()
				.performCloseToolOptionsView();

		onTopBarView()
				.performClickCheckmark();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onTopBarView()
				.performUndo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

		onTopBarView()
				.performRedo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testFilledRectChangesColor() {
		onToolBarView()
				.performCloseToolOptionsView();

		onToolProperties()
				.setColorResource(R.color.pocketpaint_color_picker_brown1);

		onTopBarView()
				.performClickCheckmark();

		onDrawingSurfaceView()
				.checkPixelColorResource(R.color.pocketpaint_color_picker_brown1, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testDrawWithHeartShape() {
		onShapeToolOptionsView()
				.performSelectShape(DrawableShape.HEART);

		onToolBarView()
				.performCloseToolOptionsView();

		onTopBarView()
				.performClickCheckmark();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testAntiAliasingIsOffIfShapeOutlineWidthIsOne() {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);
		onShapeToolOptionsView()
				.performSelectShapeDrawType(DrawableStyle.STROKE);
		onShapeToolOptionsView()
				.performSetOutlineWidth(touchCenterLeft());

		drawShape();

		Paint bitmapPaint = getCurrentToolBitmapPaint();
		Paint toolPaint = getToolPaint();

		assertFalse("BITMAP_PAINT antialiasing should be off", bitmapPaint.isAntiAlias());
		assertTrue("TOOL_PAINT antialiasing should be on", toolPaint.isAntiAlias());
	}

	@Test
	public void testDoNotUseRegularToolPaintInShapeTool() {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);
		onShapeToolOptionsView()
				.performSelectShapeDrawType(DrawableStyle.FILL);

		drawShape();

		Paint bitmapPaint = getCurrentToolBitmapPaint();
		Paint toolPaint = getToolPaint();

		assertNotEquals("bitmapPaint and toolPaint should differ", bitmapPaint, toolPaint);
	}

	@Test
	public void testShapeWithOutlineAlsoWorksWithTransparentColor() {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);
		onShapeToolOptionsView()
				.performSelectShape(DrawableShape.RECTANGLE);
		onShapeToolOptionsView()
				.performSelectShapeDrawType(DrawableStyle.FILL);
		onToolProperties()
				.setColor(Color.BLACK);
		drawShape();
		onToolBarView()
				.performClickSelectedToolButton();

		onShapeToolOptionsView()
				.performSelectShape(DrawableShape.OVAL);
		onShapeToolOptionsView()
				.performSelectShapeDrawType(DrawableStyle.STROKE);
		onToolProperties()
				.setColor(Color.TRANSPARENT);
		drawShape();
		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, DrawingSurfaceLocationProvider.TOOL_POSITION);
		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, DrawingSurfaceLocationProvider.TOP_MIDDLE);
	}

	@Test
	public void testShapeToolBoxGetsPlacedCorrectWhenZoomedIn() {
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		mainActivity.perspective.setSurfaceTranslationY(200);
		mainActivity.perspective.setSurfaceTranslationX(50);
		mainActivity.perspective.setScale(2.0f);
		mainActivity.refreshDrawingSurface();

		onToolBarView()
				.performSelectTool(ToolType.SHAPE);
		onShapeToolOptionsView()
				.performSelectShape(DrawableShape.RECTANGLE);
		onShapeToolOptionsView()
				.performSelectShapeDrawType(DrawableStyle.FILL);
		onTopBarView()
				.performClickCheckmark();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, mainActivity.perspective.surfaceCenterX - mainActivity.perspective.surfaceTranslationX,
						mainActivity.perspective.surfaceCenterY - mainActivity.perspective.surfaceTranslationY);
	}

	public void drawShape() {
		onToolBarView()
				.performCloseToolOptionsView();
		onTopBarView()
				.performClickCheckmark();
	}
}
