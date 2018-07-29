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

import org.catrobat.paintroid.command.implementation.SelectLayerCommand;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.model.LayerModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class SelectLayerCommandTest {

	@Test
	public void testRunWhenNoLayerSelected() {
		LayerModel model = new LayerModel();
		LayerContracts.Layer layer = mock(LayerContracts.Layer.class);
		model.addLayerAt(0, layer);

		SelectLayerCommand command = new SelectLayerCommand(0);
		command.run(mock(Canvas.class), model);

		assertThat(model.getCurrentLayer(), is(layer));
	}

	@Test
	public void testRunWhenOtherLayerSelected() {
		LayerModel model = new LayerModel();
		LayerContracts.Layer firstLayer = mock(LayerContracts.Layer.class);
		LayerContracts.Layer secondLayer = mock(LayerContracts.Layer.class);
		model.addLayerAt(0, firstLayer);
		model.addLayerAt(1, secondLayer);
		model.setCurrentLayer(firstLayer);

		SelectLayerCommand command = new SelectLayerCommand(1);
		command.run(mock(Canvas.class), model);

		assertThat(model.getCurrentLayer(), is(secondLayer));
	}
}
