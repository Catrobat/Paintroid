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

import org.catrobat.paintroid.command.implementation.LoadCommand;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.model.LayerModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoadCommandTest {
	@Mock
	Bitmap bitmap;

	@Mock
	Canvas canvas;

	LayerContracts.Model layerModel;

	@InjectMocks
	LoadCommand command;

	@Before
	public void setUp() {
		layerModel = new LayerModel();
	}

	@Test
	public void testSetUp() {
		verifyZeroInteractions(bitmap);
	}

	@Test
	public void testRunCopiesImage() {
		Bitmap copy = mock(Bitmap.class);
		when(bitmap.copy(Bitmap.Config.ARGB_8888, true)).thenReturn(copy);

		command.run(canvas, layerModel);

		LayerContracts.Layer currentLayer = layerModel.getCurrentLayer();
		assertThat(currentLayer.getBitmap(), is(copy));
	}

	@Test
	public void testRunAddsOneLayer() {
		Bitmap clone = mock(Bitmap.class);
		when(bitmap.copy(Bitmap.Config.ARGB_8888, true)).thenReturn(clone);

		command.run(canvas, layerModel);

		assertThat(layerModel.getLayerCount(), is(1));
	}

	@Test
	public void testRunSetsCurrentLayer() {
		Bitmap clone = mock(Bitmap.class);
		when(bitmap.copy(Bitmap.Config.ARGB_8888, true)).thenReturn(clone);

		command.run(canvas, layerModel);

		LayerContracts.Layer currentLayer = layerModel.getCurrentLayer();
		assertThat(layerModel.getLayerAt(0), is(currentLayer));
	}
}
