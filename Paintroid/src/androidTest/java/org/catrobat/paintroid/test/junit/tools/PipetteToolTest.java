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

package org.catrobat.paintroid.test.junit.tools;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.tools.implementation.PipetteTool;
import org.catrobat.paintroid.ui.button.LayersAdapter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PipetteToolTest {

	private static final int X_COORDINATE_RED = 1;
	private static final int X_COORDINATE_GREEN = 3;
	private static final int X_COORDINATE_BLUE = 5;
	private static final int X_COORDINATE_PART_TRANSPARENT = 7;

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	private PipetteTool toolToTest;

	@UiThreadTest
	@Before
	public void setUp() {
		toolToTest = new PipetteTool(activityTestRule.getActivity(), ToolType.PIPETTE);

		LayerListener layerListener = LayerListener.getInstance();
		LayersAdapter layersAdapter = layerListener.getAdapter();
		Layer layer = layersAdapter.getLayer(0);
		Bitmap bitmap = layer.getImage();
		bitmap.setPixel(X_COORDINATE_RED, 0, Color.RED);
		bitmap.setPixel(X_COORDINATE_GREEN, 0, Color.GREEN);
		bitmap.setPixel(X_COORDINATE_BLUE, 0, Color.BLUE);
		bitmap.setPixel(X_COORDINATE_PART_TRANSPARENT, 0, 0xAAAAAAAA);
		layer.setImage(bitmap);

		toolToTest.updateSurfaceBitmap();
	}

	@UiThreadTest
	@Test
	public void testHandleDown() {
		toolToTest.handleDown(new PointF(X_COORDINATE_RED, 0));
		assertEquals(Color.RED, toolToTest.getDrawPaint().getColor());
		toolToTest.handleMove(new PointF(X_COORDINATE_PART_TRANSPARENT, 0));
		assertEquals(0xAAAAAAAA, toolToTest.getDrawPaint().getColor());
	}

	@UiThreadTest
	@Test
	public void testHandleMove() {
		toolToTest.handleDown(new PointF(X_COORDINATE_RED, 0));
		assertEquals(Color.RED, toolToTest.getDrawPaint().getColor());
		toolToTest.handleMove(new PointF(X_COORDINATE_RED + 1, 0));
		assertEquals(Color.TRANSPARENT, toolToTest.getDrawPaint().getColor());
		toolToTest.handleMove(new PointF(X_COORDINATE_GREEN, 0));
		assertEquals(Color.GREEN, toolToTest.getDrawPaint().getColor());
		toolToTest.handleMove(new PointF(X_COORDINATE_PART_TRANSPARENT, 0));
		assertEquals(0xAAAAAAAA, toolToTest.getDrawPaint().getColor());
	}

	@UiThreadTest
	@Test
	public void testHandleUp() {
		toolToTest.handleUp(new PointF(X_COORDINATE_BLUE, 0));
		assertEquals(Color.BLUE, toolToTest.getDrawPaint().getColor());
		toolToTest.handleUp(new PointF(X_COORDINATE_PART_TRANSPARENT, 0));
		assertEquals(0xAAAAAAAA, toolToTest.getDrawPaint().getColor());
	}

	@UiThreadTest
	@Test
	public void testShouldReturnCorrectToolType() {
		ToolType toolType = toolToTest.getToolType();
		assertEquals(ToolType.PIPETTE, toolType);
	}

	@UiThreadTest
	@Test
	public void testShouldReturnCorrectColorForForTopButtonIfColorIsTransparent() {
		toolToTest.handleUp(new PointF(0, 0));
		int color = getAttributeButtonColor();
		assertEquals(Color.TRANSPARENT, color);
	}

	@UiThreadTest
	@Test
	public void testShouldReturnCorrectColorForForTopButtonIfColorIsRed() {
		toolToTest.handleUp(new PointF(X_COORDINATE_RED, 0));
		int color = getAttributeButtonColor();
		assertEquals(Color.RED, color);
	}

	private int getAttributeButtonColor() {
		return BaseTool.BITMAP_PAINT.getColor();
	}
}
