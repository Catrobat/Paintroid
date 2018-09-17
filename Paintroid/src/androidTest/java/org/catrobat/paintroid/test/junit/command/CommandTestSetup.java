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
import android.graphics.PointF;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.model.Layer;
import org.catrobat.paintroid.model.LayerModel;
import org.junit.Before;
import org.junit.Test;

public abstract class CommandTestSetup {

	static final int BITMAP_BASE_COLOR = Color.GREEN;
	static final int BITMAP_REPLACE_COLOR = Color.CYAN;
	static final int PAINT_BASE_COLOR = Color.BLUE;
	static final int INITIAL_HEIGHT = 80;
	static final int INITIAL_WIDTH = 80;

	Command commandUnderTest;
	Command commandUnderTestNull; // can be used to pass null to constructor
	Paint paintUnderTest;
	PointF pointUnderTest;
	Canvas canvasUnderTest;
	Bitmap bitmapUnderTest;
	Layer layerUnderTest;
	Bitmap canvasBitmapUnderTest;
	LayerModel layerModel;

	@Before
	public void setUp() {
		layerModel = new LayerModel();
		layerModel.setWidth(INITIAL_WIDTH);
		layerModel.setHeight(INITIAL_HEIGHT);

		canvasBitmapUnderTest = Bitmap.createBitmap(INITIAL_WIDTH, INITIAL_HEIGHT, Config.ARGB_8888);
		canvasBitmapUnderTest.eraseColor(BITMAP_BASE_COLOR);
		bitmapUnderTest = canvasBitmapUnderTest.copy(Config.ARGB_8888, true);
		layerUnderTest = new Layer(bitmapUnderTest);
		canvasUnderTest = new Canvas();
		canvasUnderTest.setBitmap(canvasBitmapUnderTest);
		paintUnderTest = new Paint();
		paintUnderTest.setColor(PAINT_BASE_COLOR);
		paintUnderTest.setStrokeWidth(0);
		paintUnderTest.setStyle(Paint.Style.STROKE);
		paintUnderTest.setStrokeCap(Cap.BUTT);
		pointUnderTest = new PointF(INITIAL_WIDTH / 2, INITIAL_HEIGHT / 2);
		layerModel.addLayerAt(0, layerUnderTest);
		layerModel.setCurrentLayer(layerUnderTest);
	}

	@Test
	public void testRunWithNullParameters() {
		if (commandUnderTestNull != null) {
			commandUnderTestNull.run(null, null);
			commandUnderTestNull.run(null, null);
			commandUnderTestNull.run(canvasUnderTest, null);
			commandUnderTestNull.run(null, new LayerModel());
			commandUnderTestNull.run(null, layerModel);
		}
	}
}
