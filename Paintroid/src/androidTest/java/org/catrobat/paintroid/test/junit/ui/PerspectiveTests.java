/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.junit.ui;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.test.junit.stubs.SurfaceHolderStub;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.ui.Perspective;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.test.ActivityInstrumentationTestCase2;

public class PerspectiveTests extends ActivityInstrumentationTestCase2<MainActivity> {

	SurfaceHolderStub surfaceHolderStub;
	Perspective perspective;
	float actualCenterX;
	float actualCenterY;

	public PerspectiveTests() {
		super(MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		getActivity();
		surfaceHolderStub = new SurfaceHolderStub();
		perspective = new Perspective(surfaceHolderStub);
		Rect surfaceFrame = surfaceHolderStub.getSurfaceFrame();
		actualCenterX = surfaceFrame.exactCenterX();
		actualCenterY = surfaceFrame.exactCenterY();
	}

	public void testShouldInitializeCorrectly() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		float surfaceWidth = (Float) PrivateAccess.getMemberValue(Perspective.class, perspective, "mSurfaceWidth");
		float surfaceHeight = (Float) PrivateAccess.getMemberValue(Perspective.class, perspective, "mSurfaceHeight");
		assertEquals(SurfaceHolderStub.WIDTH, surfaceWidth);
		assertEquals(SurfaceHolderStub.HEIGHT, surfaceHeight);

		float surfaceCenterX = (Float) PrivateAccess.getMemberValue(Perspective.class, perspective, "mSurfaceCenterX");
		float surfaceCenterY = (Float) PrivateAccess.getMemberValue(Perspective.class, perspective, "mSurfaceCenterY");
		assertEquals(actualCenterX, surfaceCenterX);
		assertEquals(actualCenterY, surfaceCenterY);

		float surfaceScale = (Float) PrivateAccess.getMemberValue(Perspective.class, perspective, "mSurfaceScale");
		assertEquals(1f, surfaceScale);

		assertTrue("x translation should not be 0", 0f != getSurfaceTranslationX());
		assertTrue("y translation should not be 0", 0f != getSurfaceTranslationY());
	}

	public void testShouldScaleCorrectly() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
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

	public void testShouldNotScaleBelowMinimum() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		Matrix controlMatrix = new Matrix();
		Canvas canvas = surfaceHolderStub.getCanvas();
		assertEquals(controlMatrix, canvas.getMatrix());

		float minScale = Perspective.MIN_SCALE;
		assertEquals(0.1f, minScale);

		float scale = 0.09f;
		perspective.multiplyScale(scale);
		perspective.applyToCanvas(canvas);

		controlMatrix.postTranslate(getSurfaceTranslationX(), getSurfaceTranslationY());
		controlMatrix.postScale(minScale, minScale, actualCenterX, actualCenterY);
		assertEquals(controlMatrix, canvas.getMatrix());
	}

	public void testShouldNotScaleAboveMaximum() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		Matrix controlMatrix = new Matrix();
		Canvas canvas = surfaceHolderStub.getCanvas();
		assertEquals(controlMatrix, canvas.getMatrix());

		float maxScale = Perspective.MAX_SCALE;
		assertEquals(100f, maxScale);

		float scale = 101f;
		perspective.multiplyScale(scale);
		perspective.applyToCanvas(canvas);
		controlMatrix.postTranslate(getSurfaceTranslationX(), getSurfaceTranslationY());
		controlMatrix.postScale(maxScale, maxScale, actualCenterX, actualCenterY);
		assertEquals(controlMatrix, canvas.getMatrix());
	}

	public void testShouldRespectBoundaries() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		Matrix controlMatrix = new Matrix();
		Canvas canvas = surfaceHolderStub.getCanvas();
		assertEquals(controlMatrix, canvas.getMatrix());

		perspective.multiplyScale(2f);
		perspective.applyToCanvas(canvas);

		controlMatrix.postTranslate(getSurfaceTranslationX(), getSurfaceTranslationY());
		controlMatrix.postScale(2f, 2f, actualCenterX, actualCenterY);
		assertEquals(controlMatrix, canvas.getMatrix());
	}

	private float getSurfaceTranslationX() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		float surfaceTranslationX = (Float) PrivateAccess.getMemberValue(Perspective.class, perspective,
				"mSurfaceTranslationX");
		return (surfaceTranslationX);
	}

	private float getSurfaceTranslationY() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		float surfaceTranslationY = (Float) PrivateAccess.getMemberValue(Perspective.class, perspective,
				"mSurfaceTranslationY");
		return (surfaceTranslationY);
	}
}
