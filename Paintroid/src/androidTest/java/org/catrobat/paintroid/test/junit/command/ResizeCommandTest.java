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
import android.graphics.Canvas;
import android.graphics.Color;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.AddLayerCommand;
import org.catrobat.paintroid.command.implementation.ResizeCommand;
import org.catrobat.paintroid.common.CommonFactory;
import org.catrobat.paintroid.model.Layer;
import org.catrobat.paintroid.model.LayerModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ResizeCommandTest {

	private static final int INITIAL_HEIGHT = 80;
	private static final int INITIAL_WIDTH = 80;
	private static final int NEW_WIDTH = 40;
	private static final int NEW_HEIGHT = 40;

	private Canvas canvasUnderTest;
	private LayerModel layerModel;

	@Before
	public void setUp() {
		layerModel = new LayerModel();
		layerModel.setWidth(INITIAL_WIDTH);
		layerModel.setHeight(INITIAL_HEIGHT);

		Bitmap bitmapUnderTest = Bitmap.createBitmap(INITIAL_WIDTH, INITIAL_HEIGHT, Bitmap.Config.ARGB_8888);
		Layer layerUnderTest = new Layer(bitmapUnderTest);
		canvasUnderTest = new Canvas();
		canvasUnderTest.setBitmap(bitmapUnderTest);
		layerModel.addLayerAt(0, layerUnderTest);
		layerModel.setCurrentLayer(layerUnderTest);
	}

	@Test
	public void testResizeCommand() {
		Command commandUnderTest = new ResizeCommand(NEW_WIDTH, NEW_HEIGHT);
		commandUnderTest.run(canvasUnderTest, layerModel);
		assertEquals(40, layerModel.getHeight());
		assertEquals(40, layerModel.getWidth());
	}

	@Test
	public void testBitmapKeepsDrawings() {
		layerModel.getCurrentLayer().getBitmap().setPixel(0, 0, Color.BLACK);
		int currentPixel = layerModel.getCurrentLayer().getBitmap().getPixel(0, 0);
		assertNotEquals(currentPixel, 0);

		Command commandUnderTest = new ResizeCommand(NEW_WIDTH, NEW_HEIGHT);
		commandUnderTest.run(canvasUnderTest, layerModel);
		currentPixel = layerModel.getCurrentLayer().getBitmap().getPixel(0, 0);
		assertNotEquals(currentPixel, 0);
	}

	@Test
	public void testLayerStayInSameOrderOnResize() {
		layerModel.getLayerAt(0).getBitmap().eraseColor(Color.GREEN);

		Command addLayerCommand = new AddLayerCommand(new CommonFactory());
		addLayerCommand.run(canvasUnderTest, layerModel);

		layerModel.getLayerAt(0).getBitmap().eraseColor(Color.YELLOW);

		addLayerCommand = new AddLayerCommand(new CommonFactory());
		addLayerCommand.run(canvasUnderTest, layerModel);

		layerModel.getLayerAt(0).getBitmap().eraseColor(Color.BLUE);

		Command commandUnderTest = new ResizeCommand(NEW_WIDTH, NEW_HEIGHT);
		commandUnderTest.run(canvasUnderTest, layerModel);

		assertEquals(layerModel.getLayerAt(2).getBitmap().getPixel(0, 0), Color.GREEN);
		assertEquals(layerModel.getLayerAt(1).getBitmap().getPixel(0, 0), Color.YELLOW);
		assertEquals(layerModel.getLayerAt(0).getBitmap().getPixel(0, 0), Color.BLUE);
	}

	@Test
	public void testAllLayersAreResized() {
		Command addLayerCommand = new AddLayerCommand(new CommonFactory());
		addLayerCommand.run(canvasUnderTest, layerModel);

		addLayerCommand = new AddLayerCommand(new CommonFactory());
		addLayerCommand.run(canvasUnderTest, layerModel);

		Command commandUnderTest = new ResizeCommand(NEW_WIDTH, NEW_HEIGHT);
		commandUnderTest.run(canvasUnderTest, layerModel);

		assertEquals(layerModel.getLayerAt(2).getBitmap().getHeight(), NEW_HEIGHT);
		assertEquals(layerModel.getLayerAt(2).getBitmap().getWidth(), NEW_WIDTH);
		assertEquals(layerModel.getLayerAt(1).getBitmap().getHeight(), NEW_HEIGHT);
		assertEquals(layerModel.getLayerAt(1).getBitmap().getWidth(), NEW_WIDTH);
		assertEquals(layerModel.getLayerAt(0).getBitmap().getHeight(), NEW_HEIGHT);
		assertEquals(layerModel.getLayerAt(0).getBitmap().getWidth(), NEW_WIDTH);
	}
}
