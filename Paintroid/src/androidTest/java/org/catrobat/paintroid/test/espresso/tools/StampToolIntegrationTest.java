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
import org.catrobat.paintroid.test.espresso.util.wrappers.StampToolViewInteraction;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.drawable.DrawableShape;
import org.catrobat.paintroid.tools.drawable.DrawableStyle;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.StampTool;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ShapeToolOptionsViewInteraction.onShapeToolOptionsView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class StampToolIntegrationTest {

	private static final float SCALE_25 = 0.25f;
	private static final float STAMP_RESIZE_FACTOR = 1.5f;
	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	private Workspace workspace;
	private Perspective perspective;
	private ToolReference toolReference;
	private MainActivity mainActivity;

	@Before
	public void setUp() {
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
		mainActivity = launchActivityRule.getActivity();
		workspace = mainActivity.workspace;
		perspective = mainActivity.perspective;
		toolReference = mainActivity.toolReference;
	}

	@Test
	public void testBorders() {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);

		onShapeToolOptionsView()
				.performSelectShape(DrawableShape.RECTANGLE)
				.performSelectShapeDrawType(DrawableStyle.STROKE);

		onTopBarView()
				.performClickCheckmark();

		onToolBarView()
				.performSelectTool(ToolType.STAMP);

		StampTool stampTool = (StampTool) toolReference.get();
		stampTool.boxHeight -= 25;
		stampTool.boxWidth -= 25;

		StampToolViewInteraction.Companion.onStampToolViewInteraction()
				.performCopy();

		int topLeft = stampTool.drawingBitmap.getPixel(0, 0);
		int topRight = stampTool.drawingBitmap.getPixel(stampTool.drawingBitmap.getWidth() - 1, 0);
		int bottomLeft = stampTool.drawingBitmap.getPixel(0, stampTool.drawingBitmap.getHeight() - 1);
		int bottomRight = stampTool.drawingBitmap.getPixel(stampTool.drawingBitmap.getWidth() - 1, stampTool.drawingBitmap.getHeight() - 1);

		assertEquals(topLeft, Color.BLACK);
		assertEquals(topRight, Color.BLACK);
		assertEquals(bottomLeft, Color.BLACK);
		assertEquals(bottomRight, Color.BLACK);
	}

	@Test
	public void testCopyPixel() {

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.STAMP);

		StampToolViewInteraction.Companion.onStampToolViewInteraction()
				.performCopy();

		StampTool stampTool = (StampTool) toolReference.get();
		stampTool.toolPosition.set(stampTool.toolPosition.x, stampTool.toolPosition.y * .5f);

		StampToolViewInteraction.Companion.onStampToolViewInteraction()
				.performPaste();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, stampTool.toolPosition.x, stampTool.toolPosition.y);
	}

	@Test
	public void testCutAndPastePixel() {

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.STAMP);

		StampToolViewInteraction.Companion.onStampToolViewInteraction()
				.performCut();

		StampTool stampTool = (StampTool) toolReference.get();

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, stampTool.toolPosition.x, stampTool.toolPosition.y);

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

		StampToolViewInteraction.Companion.onStampToolViewInteraction()
				.performCopy();

		StampTool stampTool = (StampTool) toolReference.get();
		stampTool.toolPosition.set(stampTool.toolPosition.x, stampTool.toolPosition.y * .5f);

		StampToolViewInteraction.Companion.onStampToolViewInteraction()
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

		StampToolViewInteraction.Companion.onStampToolViewInteraction()
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

		StampToolViewInteraction.Companion.onStampToolViewInteraction()
				.performCopy();

		Bitmap expectedBitmap = Bitmap.createBitmap(((BaseToolWithRectangleShape)
				toolReference.get()).drawingBitmap);

		assertFalse(expectedBitmap.sameAs(emptyBitmap));

		mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Bitmap actualBitmap = Bitmap.createBitmap(((BaseToolWithRectangleShape)
				toolReference.get()).drawingBitmap);

		assertTrue(expectedBitmap.sameAs(actualBitmap));
	}

	@Test
	public void testStampToolDoesNotResetPerspectiveScale() {
		float scale = 2.0f;

		perspective.setScale(scale);
		perspective.setSurfaceTranslationX(50);
		perspective.setSurfaceTranslationY(200);
		mainActivity.refreshDrawingSurface();

		onToolBarView()
				.performSelectTool(ToolType.STAMP);

		assertEquals(scale, perspective.getScale(), 0.0001f);
	}
}
