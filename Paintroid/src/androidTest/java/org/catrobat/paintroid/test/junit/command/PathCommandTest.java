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
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.RectF;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.PathCommand;
import org.catrobat.paintroid.model.Layer;
import org.catrobat.paintroid.model.LayerModel;
import org.catrobat.paintroid.test.utils.PaintroidAsserts;
import org.junit.Before;
import org.junit.Test;

public class PathCommandTest {

	private static final int BITMAP_BASE_COLOR = Color.GREEN;
	private static final int PAINT_BASE_COLOR = Color.BLUE;
	private static final int INITIAL_HEIGHT = 80;
	private static final int INITIAL_WIDTH = 80;

	private Command commandUnderTest;
	private Paint paintUnderTest;
	private Canvas canvasUnderTest;
	private Bitmap bitmapUnderTest;
	private Bitmap canvasBitmapUnderTest;

	@Before
	public void setUp() {
		LayerModel layerModel = new LayerModel();
		layerModel.setWidth(INITIAL_WIDTH);
		layerModel.setHeight(INITIAL_HEIGHT);

		canvasBitmapUnderTest = Bitmap.createBitmap(INITIAL_WIDTH, INITIAL_HEIGHT, Config.ARGB_8888);
		canvasBitmapUnderTest.eraseColor(BITMAP_BASE_COLOR);
		bitmapUnderTest = canvasBitmapUnderTest.copy(Config.ARGB_8888, true);
		Layer layerUnderTest = new Layer(bitmapUnderTest);
		canvasUnderTest = new Canvas();
		canvasUnderTest.setBitmap(canvasBitmapUnderTest);
		paintUnderTest = new Paint();
		paintUnderTest.setColor(PAINT_BASE_COLOR);
		paintUnderTest.setStrokeWidth(0);
		paintUnderTest.setStyle(Paint.Style.STROKE);
		paintUnderTest.setStrokeCap(Cap.BUTT);
		layerModel.addLayerAt(0, layerUnderTest);
		layerModel.setCurrentLayer(layerUnderTest);

		Path pathUnderTest = new Path();
		pathUnderTest.moveTo(1, 0);
		pathUnderTest.lineTo(1, canvasBitmapUnderTest.getHeight());
		commandUnderTest = new PathCommand(paintUnderTest, pathUnderTest);
	}

	@Test
	public void testPathOutOfBounds() {
		Path path = new Path();

		float left = canvasBitmapUnderTest.getWidth() + 50;
		float top = canvasBitmapUnderTest.getHeight() + 50;
		float right = canvasBitmapUnderTest.getWidth() + 100;
		float bottom = canvasBitmapUnderTest.getHeight() + 100;
		path.addRect(new RectF(left, top, right, bottom), Path.Direction.CW);

		commandUnderTest = new PathCommand(paintUnderTest, path);

		commandUnderTest.run(canvasUnderTest, null);
	}

	@Test
	public void testRun() {
		int color = paintUnderTest.getColor();
		int height = bitmapUnderTest.getHeight();

		for (int heightIndex = 0; heightIndex < height; heightIndex++) {
			bitmapUnderTest.setPixel(1, heightIndex, color);
		}
		commandUnderTest.run(canvasUnderTest, null);
		PaintroidAsserts.assertBitmapEquals(bitmapUnderTest, canvasBitmapUnderTest);
	}
}
