/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PerspectiveTests {

	private static final float SCREEN_DENSITY = 2f;
	private static final int SURFACE_WIDTH = 10;
	private static final int SURFACE_HEIGHT = 100;
	private static final float EXACT_CENTER_X = 5f;
	private static final float EXACT_CENTER_Y = 50f;
	public static final float INITIAL_SCALE = 1f;

	@Mock
	private SurfaceHolder holder;
	@Mock
	private Canvas canvas;

	private Perspective perspective;

	@Before
	public void setUp() throws Exception {
		Rect rect = mock(Rect.class);
		rect.right = SURFACE_WIDTH;
		rect.bottom = SURFACE_HEIGHT;
		when(rect.exactCenterX()).thenReturn(EXACT_CENTER_X);
		when(rect.exactCenterY()).thenReturn(EXACT_CENTER_Y);
		when(holder.getSurfaceFrame()).thenReturn(rect);

		PaintroidApplication.drawingSurface = mock(DrawingSurface.class);

		perspective = new Perspective(holder, SCREEN_DENSITY);
	}

	@Test
	public void testInitialize() {
		float surfaceWidth = perspective.surfaceWidth;
		float surfaceHeight = perspective.surfaceHeight;
		assertEquals(SURFACE_WIDTH, surfaceWidth, Float.MIN_VALUE);
		assertEquals(SURFACE_HEIGHT, surfaceHeight, Float.MIN_VALUE);

		float surfaceCenterX = perspective.surfaceCenterX;
		float surfaceCenterY = perspective.surfaceCenterY;
		assertEquals(EXACT_CENTER_X, surfaceCenterX, Float.MIN_VALUE);
		assertEquals(EXACT_CENTER_Y, surfaceCenterY, Float.MIN_VALUE);

		assertEquals(INITIAL_SCALE, perspective.getScale(), Float.MIN_VALUE);

		assertNotEquals(0, perspective.getSurfaceTranslationX());
		assertNotEquals(0, perspective.getSurfaceTranslationY());
	}

	@Test
	public void testMultiplyScale() {
		float scale = 1.5f;
		perspective.multiplyScale(scale);
		assertEquals(scale, perspective.getScale(), Float.MIN_VALUE);

		perspective.applyToCanvas(canvas);

		InOrder inOrder = Mockito.inOrder(canvas);
		inOrder.verify(canvas).scale(scale, scale, EXACT_CENTER_X, EXACT_CENTER_Y);
		inOrder.verify(canvas).translate(perspective.surfaceTranslationX, perspective.surfaceTranslationY);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void testMultiplyScaleBelowMinimum() {
		float minScale = Perspective.MIN_SCALE;

		perspective.multiplyScale(minScale * 0.9f);
		assertEquals(minScale, perspective.getScale(), Float.MIN_VALUE);
	}

	@Test
	public void testMultiplyScaleAboveMaximum() {
		float maxScale = Perspective.MAX_SCALE;

		perspective.multiplyScale(maxScale * 1.1f);
		assertEquals(maxScale, perspective.getScale(), Float.MIN_VALUE);
	}

	@Test
	public void testSetScale() {
		float scale = 1.5f;
		perspective.setScale(scale);
		assertEquals(scale, perspective.getScale(), Float.MIN_VALUE);

		perspective.applyToCanvas(canvas);

		InOrder inOrder = Mockito.inOrder(canvas);
		inOrder.verify(canvas).scale(scale, scale, EXACT_CENTER_X, EXACT_CENTER_Y);
		inOrder.verify(canvas).translate(perspective.surfaceTranslationX, perspective.surfaceTranslationY);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void testSetScaleBelowMinimum() {
		float minScale = Perspective.MIN_SCALE;

		perspective.setScale(minScale * 0.9f);
		assertEquals(minScale, perspective.getScale(), Float.MIN_VALUE);
	}

	@Test
	public void testSetScaleAboveMaximum() {
		float maxScale = Perspective.MAX_SCALE;

		perspective.setScale(maxScale * 1.1f);
		assertEquals(maxScale, perspective.getScale(), Float.MIN_VALUE);
	}
}
