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

import android.graphics.Canvas;

import org.catrobat.paintroid.command.implementation.RemoveLayerCommand;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.model.LayerModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class RemoveLayerCommandTest {

	@Mock
	private LayerContracts.Layer firstLayer;

	@Mock
	private LayerContracts.Layer secondLayer;

	private LayerModel layerModel;
	private RemoveLayerCommand command;

	@Before
	public void setUp() {
		layerModel = new LayerModel();
		layerModel.addLayerAt(0, firstLayer);
		layerModel.addLayerAt(1, secondLayer);
	}

	@Test
	public void testRunRemovesOneLayer() {
		command = new RemoveLayerCommand(1);

		command.run(mock(Canvas.class), layerModel);

		assertThat(layerModel.getLayerCount(), is(1));
		assertThat(layerModel.getLayerAt(0), is(firstLayer));
	}

	@Test
	public void testRunSetsCurrentLayer() {
		command = new RemoveLayerCommand(0);

		command.run(mock(Canvas.class), layerModel);

		LayerContracts.Layer currentLayer = layerModel.getCurrentLayer();
		assertThat(layerModel.getLayerAt(0), is(currentLayer));
		assertThat(layerModel.getLayerAt(0), is(secondLayer));
	}
}
