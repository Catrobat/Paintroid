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
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.espresso.util.wrappers.ClipboardToolViewInteraction;
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.drawable.DrawableShape;
import org.catrobat.paintroid.tools.drawable.DrawableStyle;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.ClipboardTool;
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
public class ClipboardToolIntegrationTest {

	private static final float SCALE_25 = 0.25f;
	private static final float STAMP_RESIZE_FACTOR = 1.5f;
	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public ScreenshotOnFailRule screenshotOnFailRule = new ScreenshotOnFailRule();

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
				.performSelectTool(ToolType.CLIPBOARD);

		ClipboardTool clipboardTool = (ClipboardTool) toolReference.getTool();
		clipboardTool.boxHeight -= 25;
		clipboardTool.boxWidth -= 25;

		ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction()
				.performCopy();

		int topLeft = clipboardTool.drawingBitmap.getPixel(0, 0);
		int topRight = clipboardTool.drawingBitmap.getPixel(clipboardTool.drawingBitmap.getWidth() - 1, 0);
		int bottomLeft = clipboardTool.drawingBitmap.getPixel(0, clipboardTool.drawingBitmap.getHeight() - 1);
		int bottomRight = clipboardTool.drawingBitmap.getPixel(clipboardTool.drawingBitmap.getWidth() - 1, clipboardTool.drawingBitmap.getHeight() - 1);

		assertEquals(topLeft, Color.BLACK);
		assertEquals(topRight, Color.BLACK);
		assertEquals(bottomLeft, Color.BLACK);
		assertEquals(bottomRight, Color.BLACK);
	}

	@Test
	public void testClipboardToolConsidersLayerOpacity() {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);

		onShapeToolOptionsView()
				.performSelectShape(DrawableShape.RECTANGLE);

		onTopBarView()
				.performClickCheckmark();

		onToolBarView()
				.performSelectTool(ToolType.CLIPBOARD);

		org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
				.performOpen()
				.performSetOpacityTo(50, 0)
				.performClose();

		ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction()
				.performCopy();

		ClipboardTool clipboardTool = (ClipboardTool) toolReference.getTool();
		int fiftyPercentOpacityBlack = Color.argb(255 / 2, 0, 0, 0);
		int centerPixel = clipboardTool.drawingBitmap.getPixel(clipboardTool.drawingBitmap.getWidth() / 2, clipboardTool.drawingBitmap.getHeight() / 2);
		assertEquals(centerPixel, Color.BLACK);

		ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction()
				.performPaste();

		onDrawingSurfaceView()
				.checkPixelColor(fiftyPercentOpacityBlack, BitmapLocationProvider.MIDDLE);

		ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction()
				.performCut();

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

		ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction()
				.performPaste();

		onDrawingSurfaceView()
				.checkPixelColor(fiftyPercentOpacityBlack, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testCopyPixel() {

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.CLIPBOARD);

		ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction()
				.performCopy();

		ClipboardTool clipboardTool = (ClipboardTool) toolReference.getTool();
		clipboardTool.toolPosition.set(clipboardTool.toolPosition.x, clipboardTool.toolPosition.y * .5f);

		ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction()
				.performPaste();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, clipboardTool.toolPosition.x, clipboardTool.toolPosition.y);
	}

	@Test
	public void testCutAndPastePixel() {

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.CLIPBOARD);

		ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction()
				.performCut();

		ClipboardTool clipboardTool = (ClipboardTool) toolReference.getTool();

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, clipboardTool.toolPosition.x, clipboardTool.toolPosition.y);

		ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction()
				.performPaste();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, clipboardTool.toolPosition.x, clipboardTool.toolPosition.y);
	}

	@Test
	public void testClipboardToolNotCapturingOtherLayers() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.CLIPBOARD);

		onLayerMenuView()
				.performOpen()
				.performAddLayer();

		onLayerMenuView()
				.performClose();

		ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction()
				.performCopy();

		ClipboardTool clipboardTool = (ClipboardTool) toolReference.getTool();
		clipboardTool.toolPosition.set(clipboardTool.toolPosition.x, clipboardTool.toolPosition.y * .5f);

		ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction()
				.performPaste();

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, clipboardTool.toolPosition.x, clipboardTool.toolPosition.y * .5f);
	}

	@Test
	public void testClipboardToolOutsideDrawingSurface() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		int bitmapWidth = workspace.getWidth();
		int bitmapHeight = workspace.getHeight();
		perspective.setScale(SCALE_25);

		onToolBarView()
				.performSelectTool(ToolType.CLIPBOARD);

		ClipboardTool clipboardTool = (ClipboardTool) toolReference.getTool();
		PointF toolPosition = new PointF(perspective.surfaceCenterX, perspective.surfaceCenterY);
		clipboardTool.toolPosition.set(toolPosition);
		clipboardTool.boxWidth = (int) (bitmapWidth * STAMP_RESIZE_FACTOR);
		clipboardTool.boxHeight = (int) (bitmapHeight * STAMP_RESIZE_FACTOR);

		ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction()
				.performPaste();

		assertNotNull(clipboardTool.drawingBitmap);
	}

	@Test
	public void testBitmapSavedOnOrientationChange() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.CLIPBOARD);

		Bitmap emptyBitmap = Bitmap.createBitmap(((BaseToolWithRectangleShape)
				toolReference.getTool()).drawingBitmap);

		ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction()
				.performCopy();

		Bitmap expectedBitmap = Bitmap.createBitmap(((BaseToolWithRectangleShape)
				toolReference.getTool()).drawingBitmap);

		assertFalse(expectedBitmap.sameAs(emptyBitmap));

		mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Bitmap actualBitmap = Bitmap.createBitmap(((BaseToolWithRectangleShape)
				toolReference.getTool()).drawingBitmap);

		assertTrue(expectedBitmap.sameAs(actualBitmap));
	}

	@Test
	public void testClipboardToolDoesNotResetPerspectiveScale() {
		float scale = 2.0f;

		perspective.setScale(scale);
		perspective.surfaceTranslationX = 50;
		perspective.surfaceTranslationY = 200;
		mainActivity.refreshDrawingSurface();

		onToolBarView()
				.performSelectTool(ToolType.CLIPBOARD);

		assertEquals(scale, perspective.getScale(), 0.0001f);
	}
}
