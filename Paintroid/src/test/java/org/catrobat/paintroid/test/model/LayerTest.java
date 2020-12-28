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

import android.graphics.Bitmap;

import org.catrobat.paintroid.model.Layer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LayerTest {

	@Mock
	private Bitmap firstBitmap;

	@Mock
	private Bitmap secondBitmap;

	@Test
	public void testGetBitmap() {
		Layer firstLayer = new Layer(firstBitmap);
		Layer secondLayer = new Layer(secondBitmap);

		assertEquals(firstBitmap, firstLayer.getBitmap());
		assertEquals(secondBitmap, secondLayer.getBitmap());
		assertTrue(firstLayer.getCheckBox());
		assertTrue(secondLayer.getCheckBox());
		verify(secondBitmap).getWidth();
		verify(secondBitmap).getHeight();
		verify(firstBitmap).getWidth();
		verify(firstBitmap).getHeight();
	}

	@Test
	public void testSetBitmap() {
		Layer layer = new Layer(firstBitmap);

		layer.setBitmap(secondBitmap);

		assertEquals(secondBitmap, layer.getBitmap());
		assertTrue(layer.getCheckBox());
		verify(firstBitmap).getWidth();
		verify(firstBitmap).getHeight();
	}
}
