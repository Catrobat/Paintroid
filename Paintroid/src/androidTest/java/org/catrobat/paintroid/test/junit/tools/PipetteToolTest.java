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

package org.catrobat.paintroid.test.junit.tools;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.test.UiThreadTest;

import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.PipetteTool;
import org.catrobat.paintroid.ui.button.LayersAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PipetteToolTest extends BaseToolTest {

	private final int X_COORDINATE_RED = 1;
	private final int X_COORDINATE_GREEN = 3;
	private final int X_COORDINATE_BLUE = 5;
	private final int X_COORDINATE_PART_TRANSPARENT = 7;

	public PipetteToolTest() {
		super();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		mToolToTest = new PipetteTool(getActivity(), ToolType.PIPETTE);
		super.setUp();

		LayerListener layerListener = LayerListener.getInstance();
		LayersAdapter layersAdapter = layerListener.getAdapter();
		Layer layer = layersAdapter.getLayer(0);
		Bitmap bitmap = layer.getImage();
		bitmap.setPixel(X_COORDINATE_RED, 0, Color.RED);
		bitmap.setPixel(X_COORDINATE_GREEN, 0, Color.GREEN);
		bitmap.setPixel(X_COORDINATE_BLUE, 0, Color.BLUE);
		bitmap.setPixel(X_COORDINATE_PART_TRANSPARENT, 0, 0xAAAAAAAA);
		layer.setImage(bitmap);
	}

	@Override
	@After
	public void tearDown() {
		try {
			super.tearDown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@UiThreadTest
	public void testHandleDown() {
		mToolToTest.handleDown(new PointF(X_COORDINATE_RED, 0));
		assertEquals("Paint color has not changed", Color.RED, mToolToTest.getDrawPaint().getColor());
		mToolToTest.handleMove(new PointF(X_COORDINATE_PART_TRANSPARENT, 0));
		assertEquals("Paint color has not changed", 0xAAAAAAAA, mToolToTest.getDrawPaint().getColor());
	}

	@UiThreadTest
	public void testHandleMove() {
		mToolToTest.handleDown(new PointF(X_COORDINATE_RED, 0));
		assertEquals("Paint color has not changed", Color.RED, mToolToTest.getDrawPaint().getColor());
		mToolToTest.handleMove(new PointF(X_COORDINATE_RED + 1, 0));
		assertEquals("Paint color has not changed", Color.TRANSPARENT, mToolToTest.getDrawPaint().getColor());
		mToolToTest.handleMove(new PointF(X_COORDINATE_GREEN, 0));
		assertEquals("Paint color has not changed", Color.GREEN, mToolToTest.getDrawPaint().getColor());
		mToolToTest.handleMove(new PointF(X_COORDINATE_PART_TRANSPARENT, 0));
		assertEquals("Paint color has not changed", 0xAAAAAAAA, mToolToTest.getDrawPaint().getColor());
	}

	@UiThreadTest
	public void testHandleUp() {
		mToolToTest.handleUp(new PointF(X_COORDINATE_BLUE, 0));
		assertEquals("Paint color has not changed", Color.BLUE, mToolToTest.getDrawPaint().getColor());
		mToolToTest.handleUp(new PointF(X_COORDINATE_PART_TRANSPARENT, 0));
		assertEquals("Paint color has not changed", 0xAAAAAAAA, mToolToTest.getDrawPaint().getColor());
	}

	@Test
	public void testShouldReturnCorrectToolType() {
		ToolType toolType = mToolToTest.getToolType();
		assertEquals(ToolType.PIPETTE, toolType);
	}

	@UiThreadTest
	public void testShouldReturnCorrectColorForForTopButtonIfColorIsTransparent() throws NoSuchFieldException, IllegalAccessException {
		mToolToTest.handleUp(new PointF(0, 0));
		int color = getAttributeButtonColor();
		assertEquals("Transparent colour expected", Color.TRANSPARENT, color);
	}

	@UiThreadTest
	public void testShouldReturnCorrectColorForForTopButtonIfColorIsRed() throws NoSuchFieldException, IllegalAccessException {
		mToolToTest.handleUp(new PointF(X_COORDINATE_RED, 0));
		int color = getAttributeButtonColor();
		assertEquals("Red colour expected", Color.RED, color);
	}

}
