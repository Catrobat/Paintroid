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

package org.catrobat.paintroid.test.tools;

import android.graphics.Bitmap;

import org.catrobat.paintroid.tools.Layer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class LayerTest {

	@Mock
	Bitmap bitmap;

	@Test
	public void testGetSelected() {
		Layer layer = new Layer(13, bitmap);

		assertFalse(layer.getSelected());
		verifyZeroInteractions(bitmap);
	}

	@Test
	public void testSetSelected() {
		Layer layer = new Layer(13, bitmap);

		layer.setSelected(true);

		assertTrue(layer.getSelected());

		layer.setSelected(false);

		assertFalse(layer.getSelected());
		verifyZeroInteractions(bitmap);
	}

	@Test
	public void testGetLayerID() {
		Layer layer = new Layer(3, bitmap);
		Layer secondLayer = new Layer(5, bitmap);

		assertEquals(3, layer.getLayerID());
		assertEquals(5, secondLayer.getLayerID());
		verifyZeroInteractions(bitmap);
	}

	@Test
	public void testGetBitmap() {
		Bitmap secondBitmap = mock(Bitmap.class);
		Layer layer = new Layer(5, bitmap);
		Layer secondLayer = new Layer(7, secondBitmap);

		assertEquals(bitmap, layer.getBitmap());
		assertEquals(secondBitmap, secondLayer.getBitmap());
		verifyZeroInteractions(bitmap, secondBitmap);
	}

	@Test
	public void testSetBitmap() {
		Bitmap secondBitmap = mock(Bitmap.class);
		Layer layer = new Layer(11, bitmap);

		layer.setBitmap(secondBitmap);

		assertEquals(secondBitmap, layer.getBitmap());
		verifyZeroInteractions(bitmap, secondBitmap);
	}
}
