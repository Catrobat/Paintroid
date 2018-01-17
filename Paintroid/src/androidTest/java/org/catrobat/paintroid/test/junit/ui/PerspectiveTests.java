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

package org.catrobat.paintroid.test.junit.ui;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.test.rule.ActivityTestRule;
import android.util.DisplayMetrics;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.test.junit.stubs.SurfaceHolderStub;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PerspectiveTests {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
	private SurfaceHolderStub surfaceHolderStub;
	private Perspective perspective;
	private float actualCenterX;
	private float actualCenterY;

	@Before
	public void setUp() throws Exception {
		surfaceHolderStub = new SurfaceHolderStub();
		final Resources resources = activityTestRule.getActivity().getResources();
		final DisplayMetrics metrics = resources.getDisplayMetrics();
		perspective = new Perspective(surfaceHolderStub, metrics.density);
		Rect surfaceFrame = surfaceHolderStub.getSurfaceFrame();
		actualCenterX = surfaceFrame.exactCenterX();
		actualCenterY = surfaceFrame.exactCenterY();
	}

	@Test
	public void testShouldInitializeCorrectly() {

		float surfaceWidth = perspective.surfaceWidth;
		float surfaceHeight = perspective.surfaceHeight;
		assertEquals(SurfaceHolderStub.WIDTH, surfaceWidth, Double.MIN_VALUE);
		assertEquals(SurfaceHolderStub.HEIGHT, surfaceHeight, Double.MIN_VALUE);

		float surfaceCenterX = perspective.surfaceCenterX;
		float surfaceCenterY = perspective.surfaceCenterY;
		assertEquals(actualCenterX, surfaceCenterX, Double.MIN_VALUE);
		assertEquals(actualCenterY, surfaceCenterY, Double.MIN_VALUE);

		float surfaceScale = perspective.surfaceScale;
		assertEquals(1f, surfaceScale, Double.MIN_VALUE);

		assertNotEquals("x translation should not be 0", getSurfaceTranslationX(), 0);
		assertNotEquals("y translation should not be 0", getSurfaceTranslationY(), 0);
	}

	@Test
	public void testShouldScaleCorrectly() {
		Matrix controlMatrix = new Matrix();
		Canvas canvas = surfaceHolderStub.getCanvas();
		assertEquals(controlMatrix, canvas.getMatrix());

		float scale = 1.5f;
		perspective.multiplyScale(scale);
		perspective.applyToCanvas(canvas);

		controlMatrix.postTranslate(getSurfaceTranslationX(), getSurfaceTranslationY());
		controlMatrix.postScale(scale, scale, actualCenterX, actualCenterY);
		assertEquals(controlMatrix, canvas.getMatrix());
	}

	@Test
	public void testShouldNotScaleBelowMinimum() {
		Matrix controlMatrix = new Matrix();
		Canvas canvas = surfaceHolderStub.getCanvas();
		assertEquals(controlMatrix, canvas.getMatrix());

		float minScale = Perspective.MIN_SCALE;
		assertEquals(0.1f, minScale, Double.MIN_VALUE);

		float scale = 0.09f;
		perspective.multiplyScale(scale);
		perspective.applyToCanvas(canvas);

		controlMatrix.postTranslate(getSurfaceTranslationX(), getSurfaceTranslationY());
		controlMatrix.postScale(minScale, minScale, actualCenterX, actualCenterY);
		assertEquals(controlMatrix, canvas.getMatrix());
	}

	@Test
	public void testShouldNotScaleAboveMaximum() {
		Matrix controlMatrix = new Matrix();
		Canvas canvas = surfaceHolderStub.getCanvas();
		assertEquals(controlMatrix, canvas.getMatrix());

		float maxScale = Perspective.MAX_SCALE;
		assertEquals(100f, maxScale, Double.MIN_VALUE);

		float scale = 101f;
		perspective.multiplyScale(scale);
		perspective.applyToCanvas(canvas);
		controlMatrix.postTranslate(getSurfaceTranslationX(), getSurfaceTranslationY());
		controlMatrix.postScale(maxScale, maxScale, actualCenterX, actualCenterY);
		assertEquals(controlMatrix, canvas.getMatrix());
	}

	@Test
	public void testShouldRespectBoundaries() {
		Matrix controlMatrix = new Matrix();
		Canvas canvas = surfaceHolderStub.getCanvas();
		assertEquals(controlMatrix, canvas.getMatrix());

		perspective.multiplyScale(2f);
		perspective.applyToCanvas(canvas);

		controlMatrix.postTranslate(getSurfaceTranslationX(), getSurfaceTranslationY());
		controlMatrix.postScale(2f, 2f, actualCenterX, actualCenterY);
		assertEquals(controlMatrix, canvas.getMatrix());
	}

	private float getSurfaceTranslationX() {
		return perspective.surfaceTranslationX;
	}

	private float getSurfaceTranslationY() {
		return perspective.surfaceTranslationY;
	}
}
