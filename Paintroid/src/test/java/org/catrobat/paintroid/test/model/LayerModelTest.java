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

package org.catrobat.paintroid.test.model;

import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.model.LayerModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Iterator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class LayerModelTest {

	@InjectMocks
	private LayerModel layerModel;

	@Test
	public void testSetUp() {
		assertThat(layerModel.getLayerCount(), is(0));
		assertThat(layerModel.getWidth(), is(0));
		assertThat(layerModel.getHeight(), is(0));
		assertNotNull(layerModel.getLayers());
		assertNull(layerModel.getCurrentLayer());

		Iterator<LayerContracts.Layer> iterator = layerModel.listIterator(0);
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testGetCurrentLayer() {
		LayerContracts.Layer layer = mock(LayerContracts.Layer.class);
		layerModel.setCurrentLayer(layer);

		assertThat(layerModel.getCurrentLayer(), is(layer));
		verifyZeroInteractions(layer);
	}

	@Test
	public void testGetWidth() {
		layerModel.setWidth(3);

		assertThat(layerModel.getWidth(), is(3));
	}

	@Test
	public void testGetHeight() {
		layerModel.setHeight(5);

		assertThat(layerModel.getHeight(), is(5));
	}

	@Test
	public void testReset() {
		LayerContracts.Layer layer = mock(LayerContracts.Layer.class);

		layerModel.addLayerAt(0, layer);
		layerModel.reset();

		assertThat(layerModel.getLayerCount(), is(0));
		verifyZeroInteractions(layer);
	}

	@Test
	public void testGetLayerCount() {
		LayerContracts.Layer layer = mock(LayerContracts.Layer.class);

		layerModel.addLayerAt(0, layer);

		assertThat(layerModel.getLayerCount(), is(1));
		verifyZeroInteractions(layer);
	}

	@Test
	public void testGetLayerAt() {
		LayerContracts.Layer layer = mock(LayerContracts.Layer.class);

		layerModel.addLayerAt(0, layer);

		assertThat(layerModel.getLayerAt(0), is(layer));
		verifyZeroInteractions(layer);
	}

	@Test
	public void testGetLayerIndexOf() {
		LayerContracts.Layer layer = mock(LayerContracts.Layer.class);

		layerModel.addLayerAt(0, layer);

		assertThat(layerModel.getLayerIndexOf(layer), is(0));
		verifyZeroInteractions(layer);
	}

	@Test
	public void testListIterator() {
		LayerContracts.Layer layer = mock(LayerContracts.Layer.class);

		layerModel.addLayerAt(0, layer);

		Iterator<LayerContracts.Layer> iterator = layerModel.listIterator(0);

		assertThat(iterator.next(), is(layer));
		verifyZeroInteractions(layer);
	}

	@Test
	public void testSetLayerAt() {
		LayerContracts.Layer firstLayer = mock(LayerContracts.Layer.class);
		LayerContracts.Layer secondLayer = mock(LayerContracts.Layer.class);

		layerModel.addLayerAt(0, firstLayer);
		layerModel.setLayerAt(0, secondLayer);

		assertThat(layerModel.getLayerCount(), is(1));
		assertThat(layerModel.getLayerAt(0), is(secondLayer));
		verifyZeroInteractions(firstLayer, secondLayer);
	}

	@Test
	public void testRemoveLayerAt() {
		LayerContracts.Layer firstLayer = mock(LayerContracts.Layer.class);
		LayerContracts.Layer secondLayer = mock(LayerContracts.Layer.class);

		layerModel.addLayerAt(0, firstLayer);
		layerModel.addLayerAt(0, secondLayer);
		layerModel.removeLayerAt(0);

		assertThat(layerModel.getLayerCount(), is(1));
		assertThat(layerModel.getLayerAt(0), is(firstLayer));
		verifyZeroInteractions(firstLayer, secondLayer);
	}
}
