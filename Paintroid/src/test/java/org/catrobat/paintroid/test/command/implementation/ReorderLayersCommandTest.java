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

import org.catrobat.paintroid.command.implementation.ReorderLayersCommand;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.model.LayerModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ReorderLayersCommandTest {
	@Mock
	private LayerContracts.Layer firstLayer;

	@Mock
	private LayerContracts.Layer secondLayer;

	@Mock
	private LayerContracts.Layer thirdLayer;

	private LayerModel layerModel;
	private ReorderLayersCommand command;

	@Before
	public void setUp() {
		layerModel = new LayerModel();
		layerModel.addLayerAt(0, firstLayer);
		layerModel.addLayerAt(1, secondLayer);
		layerModel.addLayerAt(2, thirdLayer);
	}

	@Test
	public void testRunReorderToBack() {
		command = new ReorderLayersCommand(0, 2);

		command.run(mock(Canvas.class), layerModel);

		assertThat(layerModel.getLayerAt(0), is(secondLayer));
		assertThat(layerModel.getLayerAt(1), is(thirdLayer));
		assertThat(layerModel.getLayerAt(2), is(firstLayer));
		assertThat(layerModel.getLayerCount(), is(3));
	}

	@Test
	public void testRunReorderToFront() {
		command = new ReorderLayersCommand(2, 0);

		command.run(mock(Canvas.class), layerModel);

		assertThat(layerModel.getLayerAt(0), is(thirdLayer));
		assertThat(layerModel.getLayerAt(1), is(firstLayer));
		assertThat(layerModel.getLayerAt(2), is(secondLayer));
		assertThat(layerModel.getLayerCount(), is(3));
	}

	@Test
	public void testRunDoesNotAffectCurrentLayer() {
		command = new ReorderLayersCommand(2, 0);

		command.run(mock(Canvas.class), layerModel);

		assertNull(layerModel.getCurrentLayer());
	}
}
