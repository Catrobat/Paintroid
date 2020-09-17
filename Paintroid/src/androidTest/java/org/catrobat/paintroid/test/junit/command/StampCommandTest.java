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
import android.graphics.Point;
import android.graphics.PointF;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.StampCommand;
import org.catrobat.paintroid.model.Layer;
import org.catrobat.paintroid.model.LayerModel;
import org.catrobat.paintroid.test.utils.PaintroidAsserts;
import org.junit.Before;
import org.junit.Test;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class StampCommandTest {

	private Bitmap stampBitmapUnderTest;

	private static final int BITMAP_BASE_COLOR = Color.GREEN;
	private static final int BITMAP_REPLACE_COLOR = Color.CYAN;
	private static final int INITIAL_HEIGHT = 80;
	private static final int INITIAL_WIDTH = 80;

	private Command commandUnderTest;
	private Command commandUnderTestNull; // can be used to pass null to constructor
	private PointF pointUnderTest;
	private Canvas canvasUnderTest;
	private Bitmap canvasBitmapUnderTest;
	private LayerModel layerModel;

	@Before
	public void setUp() {
		layerModel = new LayerModel();
		layerModel.setWidth(INITIAL_WIDTH);
		layerModel.setHeight(INITIAL_HEIGHT);

		canvasBitmapUnderTest = Bitmap.createBitmap(INITIAL_WIDTH, INITIAL_HEIGHT, Config.ARGB_8888);
		canvasBitmapUnderTest.eraseColor(BITMAP_BASE_COLOR);
		Bitmap bitmapUnderTest = canvasBitmapUnderTest.copy(Config.ARGB_8888, true);
		Layer layerUnderTest = new Layer(bitmapUnderTest);
		canvasUnderTest = new Canvas();
		canvasUnderTest.setBitmap(canvasBitmapUnderTest);
		pointUnderTest = new PointF(INITIAL_WIDTH / 2, INITIAL_HEIGHT / 2);
		layerModel.addLayerAt(0, layerUnderTest);
		layerModel.setCurrentLayer(layerUnderTest);

		PaintroidApplication.cacheDir = InstrumentationRegistry.getInstrumentation().getTargetContext().getCacheDir();

		stampBitmapUnderTest = canvasBitmapUnderTest.copy(Config.ARGB_8888, true);
		stampBitmapUnderTest.eraseColor(BITMAP_REPLACE_COLOR);
		commandUnderTest = new StampCommand(stampBitmapUnderTest, new Point(canvasBitmapUnderTest.getWidth() / 2,
				canvasBitmapUnderTest.getHeight() / 2), canvasBitmapUnderTest.getWidth(),
				canvasBitmapUnderTest.getHeight(), 0);
		commandUnderTestNull = new StampCommand(null, null, 0, 0, 0);
	}

	@Test
	public void testRunWithNullParameters() {
		commandUnderTestNull.run(null, null);
		commandUnderTestNull.run(null, null);
		commandUnderTestNull.run(canvasUnderTest, null);
		commandUnderTestNull.run(null, new LayerModel());
		commandUnderTestNull.run(null, layerModel);
	}

	@Test
	public void testRun() {
		Layer layer = new Layer(Bitmap.createBitmap(1, 1, Config.ARGB_8888));
		LayerModel model = new LayerModel();
		model.addLayerAt(0, layer);
		model.setCurrentLayer(layer);
		commandUnderTest.run(canvasUnderTest, model);
		PaintroidAsserts.assertBitmapEquals(stampBitmapUnderTest, canvasBitmapUnderTest);

		assertNull("Stamp bitmap not recycled.", ((BaseCommand) commandUnderTest).bitmap);
		assertNotNull("Bitmap not stored", ((BaseCommand) commandUnderTest).fileToStoredBitmap);
		Layer secondLayer = new Layer(Bitmap.createBitmap(10, 10, Config.ARGB_8888));
		LayerModel secondModel = new LayerModel();
		secondModel.addLayerAt(0, secondLayer);
		secondModel.setCurrentLayer(secondLayer);
		commandUnderTest.run(canvasUnderTest, secondModel);
		PaintroidAsserts.assertBitmapEquals(stampBitmapUnderTest, canvasBitmapUnderTest);
	}

	@Test
	public void testRunRotateStamp() {
		stampBitmapUnderTest.setPixel(0, 0, Color.GREEN);
		commandUnderTest = new StampCommand(stampBitmapUnderTest, new Point((int) pointUnderTest.x,
				(int) pointUnderTest.y), canvasBitmapUnderTest.getWidth(), canvasBitmapUnderTest.getHeight(), 180);
		commandUnderTest.run(canvasUnderTest, null);
		stampBitmapUnderTest.setPixel(0, 0, Color.CYAN);
		stampBitmapUnderTest.setPixel(stampBitmapUnderTest.getWidth() - 1, stampBitmapUnderTest.getHeight() - 1,
				Color.GREEN);
		PaintroidAsserts.assertBitmapEquals(stampBitmapUnderTest, canvasBitmapUnderTest);
		assertNull("Stamp bitmap not recycled.", ((BaseCommand) commandUnderTest).bitmap);
		assertNotNull("Bitmap not stored", ((BaseCommand) commandUnderTest).fileToStoredBitmap);
	}
}
