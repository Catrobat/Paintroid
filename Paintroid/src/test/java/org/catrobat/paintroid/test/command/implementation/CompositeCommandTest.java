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

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.CompositeCommand;
import org.catrobat.paintroid.model.Layer;
import org.catrobat.paintroid.model.LayerModel;
import org.junit.Test;
import org.mockito.InOrder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class CompositeCommandTest {
	@Test
	public void testRunEmpty() {
		Canvas canvas = mock(Canvas.class);
		LayerModel layerModel = new LayerModel();

		CompositeCommand command = new CompositeCommand();
		command.run(canvas, layerModel);

		verifyZeroInteractions(canvas);
		assertThat(layerModel.getLayerCount(), is(0));
		assertNull(layerModel.getCurrentLayer());
	}

	@Test
	public void testRunAfterAddWithoutCurrentLayer() {
		Canvas canvas = mock(Canvas.class);
		LayerModel layerModel = new LayerModel();
		Command firstCommand = mock(Command.class);
		Command secondCommand = mock(Command.class);

		CompositeCommand command = new CompositeCommand();
		command.addCommand(firstCommand);
		command.addCommand(secondCommand);
		command.run(canvas, layerModel);

		verifyZeroInteractions(canvas);
		InOrder inOrder = inOrder(firstCommand, secondCommand);
		inOrder.verify(firstCommand).run(canvas, layerModel);
		inOrder.verify(secondCommand).run(canvas, layerModel);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void testRunAfterAddWithCurrentLayerSet() {
		Canvas canvas = mock(Canvas.class);
		LayerModel layerModel = new LayerModel();
		Command firstCommand = mock(Command.class);
		Command secondCommand = mock(Command.class);

		Layer currentLayer = mock(Layer.class);
		Bitmap currentBitmap = mock(Bitmap.class);
		when(currentLayer.getBitmap()).thenReturn(currentBitmap);
		layerModel.setCurrentLayer(currentLayer);

		CompositeCommand command = new CompositeCommand();
		command.addCommand(firstCommand);
		command.addCommand(secondCommand);
		command.run(canvas, layerModel);

		InOrder inOrder = inOrder(firstCommand, secondCommand, canvas);
		inOrder.verify(canvas).setBitmap(currentBitmap);
		inOrder.verify(firstCommand).run(canvas, layerModel);
		inOrder.verify(canvas).setBitmap(currentBitmap);
		inOrder.verify(secondCommand).run(canvas, layerModel);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void testFreeResourcesEmpty() {
		CompositeCommand command = new CompositeCommand();
		command.freeResources();
	}

	@Test
	public void testFreeResourcesAfterAdd() {
		Command firstCommand = mock(Command.class);
		Command secondCommand = mock(Command.class);

		CompositeCommand command = new CompositeCommand();
		command.addCommand(firstCommand);
		command.addCommand(secondCommand);
		command.freeResources();

		InOrder inOrder = inOrder(firstCommand, secondCommand);
		inOrder.verify(firstCommand).freeResources();
		inOrder.verify(secondCommand).freeResources();
		inOrder.verifyNoMoreInteractions();
	}
}
