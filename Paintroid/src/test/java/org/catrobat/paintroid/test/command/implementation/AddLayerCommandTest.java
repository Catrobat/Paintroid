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

package org.catrobat.paintroid.test.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import org.catrobat.paintroid.command.implementation.AddLayerCommand;
import org.catrobat.paintroid.common.CommonFactory;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.model.LayerModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class AddLayerCommandTest {

	@Mock
	private CommonFactory commonFactory;

	@Mock
	private Canvas canvas;

	@InjectMocks
	private AddLayerCommand command;

	@Test
	public void testSetUp() {
		verifyZeroInteractions(commonFactory);
	}

	@Test
	public void testAddOneLayer() {
		LayerModel layerModel = new LayerModel();
		layerModel.setWidth(3);
		layerModel.setHeight(5);

		command.run(canvas, layerModel);

		verify(commonFactory).createBitmap(3, 5, Bitmap.Config.ARGB_8888);
		assertEquals(1, layerModel.getLayerCount());

		LayerContracts.Layer currentLayer = layerModel.getCurrentLayer();
		assertEquals(currentLayer, layerModel.getLayerAt(0));
	}

	@Test
	public void testAddTwoLayersAddsToFront() {
		LayerModel layerModel = new LayerModel();

		command.run(canvas, layerModel);
		LayerContracts.Layer firstLayer = layerModel.getLayerAt(0);
		command.run(canvas, layerModel);

		assertEquals(2, layerModel.getLayerCount());

		LayerContracts.Layer currentLayer = layerModel.getCurrentLayer();
		assertEquals(currentLayer, layerModel.getLayerAt(0));
		assertEquals(firstLayer, layerModel.getLayerAt(1));
	}

	@Test
	public void testAddMultipleLayersWillUseSameArguments() {
		LayerModel layerModel = new LayerModel();
		layerModel.setWidth(7);
		layerModel.setHeight(11);

		command.run(canvas, layerModel);
		command.run(canvas, layerModel);
		command.run(canvas, layerModel);
		command.run(canvas, layerModel);

		verify(commonFactory, times(4)).createBitmap(7, 11, Bitmap.Config.ARGB_8888);
	}
}
