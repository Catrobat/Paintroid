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

package org.catrobat.paintroid.test.junit.command;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.FlipCommand;
import org.catrobat.paintroid.command.implementation.FlipCommand.FlipDirection;
import org.catrobat.paintroid.model.Layer;
import org.catrobat.paintroid.model.LayerModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FlipCommandTest {

	private static final int BITMAP_BASE_COLOR = Color.GREEN;
	private static final int PAINT_BASE_COLOR = Color.BLUE;
	private static final int INITIAL_HEIGHT = 80;
	private static final int INITIAL_WIDTH = 80;

	private Command commandUnderTest;
	private Canvas canvasUnderTest;
	private Bitmap bitmapUnderTest;
	private LayerModel layerModel;

	@Before
	public void setUp() {
		layerModel = new LayerModel();
		layerModel.setWidth(INITIAL_WIDTH);
		layerModel.setHeight(INITIAL_HEIGHT);

		Bitmap canvasBitmapUnderTest = Bitmap.createBitmap(INITIAL_WIDTH, INITIAL_HEIGHT, Config.ARGB_8888);
		canvasBitmapUnderTest.eraseColor(BITMAP_BASE_COLOR);
		bitmapUnderTest = canvasBitmapUnderTest.copy(Config.ARGB_8888, true);
		Layer layerUnderTest = new Layer(bitmapUnderTest);
		canvasUnderTest = new Canvas();
		canvasUnderTest.setBitmap(canvasBitmapUnderTest);
		layerModel.addLayerAt(0, layerUnderTest);
		layerModel.setCurrentLayer(layerUnderTest);
	}

	@Test
	public void testVerticalFlip() {
		commandUnderTest = new FlipCommand(FlipDirection.FLIP_VERTICAL);
		bitmapUnderTest.setPixel(0, INITIAL_HEIGHT / 2, PAINT_BASE_COLOR);
		commandUnderTest.run(canvasUnderTest, layerModel);
		int pixel = bitmapUnderTest.getPixel(INITIAL_WIDTH - 1, INITIAL_WIDTH / 2);
		assertEquals(PAINT_BASE_COLOR, pixel);
	}

	@Test
	public void testHorizontalFlip() {
		commandUnderTest = new FlipCommand(FlipDirection.FLIP_HORIZONTAL);
		bitmapUnderTest.setPixel(INITIAL_WIDTH / 2, 0, PAINT_BASE_COLOR);
		commandUnderTest.run(canvasUnderTest, layerModel);
		int pixel = bitmapUnderTest.getPixel(INITIAL_WIDTH / 2, INITIAL_WIDTH - 1);
		assertEquals(PAINT_BASE_COLOR, pixel);
	}
}
