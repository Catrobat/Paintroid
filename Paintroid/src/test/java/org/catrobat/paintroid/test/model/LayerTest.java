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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class LayerTest {

	@Mock
	private Bitmap bitmap;

	@Test
	public void testGetBitmap() {
		Bitmap secondBitmap = mock(Bitmap.class);
		Layer layer = new Layer(bitmap);
		Layer secondLayer = new Layer(secondBitmap);

		assertEquals(bitmap, layer.getBitmap());
		assertEquals(secondBitmap, secondLayer.getBitmap());
		verifyZeroInteractions(bitmap, secondBitmap);
	}

	@Test
	public void testSetBitmap() {
		Bitmap secondBitmap = mock(Bitmap.class);
		Layer layer = new Layer(bitmap);

		layer.setBitmap(secondBitmap);

		assertEquals(secondBitmap, layer.getBitmap());
		verifyZeroInteractions(bitmap, secondBitmap);
	}
}
