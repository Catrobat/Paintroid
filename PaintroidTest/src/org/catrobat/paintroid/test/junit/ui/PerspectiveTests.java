/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
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

import junit.framework.TestCase;

import org.catrobat.paintroid.test.junit.stubs.SurfaceHolderStub;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.ui.Perspective;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

public class PerspectiveTests extends TestCase {

	SurfaceHolderStub surfaceHolderStub;
	Perspective perspective;
	float actualCenterX;
	float actualCenterY;

	@Override
	public void setUp() {
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

		float surfaceTranslationX = (Float) PrivateAccess.getMemberValue(Perspective.class, perspective,
				"mSurfaceTranslationX");
		float surfaceTranslationY = (Float) PrivateAccess.getMemberValue(Perspective.class, perspective,
				"mSurfaceTranslationY");
		assertEquals(0f, surfaceTranslationX);
		assertEquals(0f, surfaceTranslationY);
	}

	public void testShouldScaleCorrectly() {
		Matrix controlMatrix = new Matrix();
		Canvas canvas = surfaceHolderStub.getCanvas();
		assertEquals(controlMatrix, canvas.getMatrix());

		float scale = 1.5f;
		perspective.multiplyScale(scale);
		perspective.applyToCanvas(canvas);
		controlMatrix.postScale(scale, scale, actualCenterX, actualCenterY);
		assertEquals(controlMatrix, canvas.getMatrix());
	}

	public void testShouldNotScaleBelowMinimum() {
		Matrix controlMatrix = new Matrix();
		Canvas canvas = surfaceHolderStub.getCanvas();
		assertEquals(controlMatrix, canvas.getMatrix());

		float minScale = Perspective.MIN_SCALE;
		assertEquals(0.1f, minScale);

		float scale = 0.09f;
		perspective.multiplyScale(scale);
		perspective.applyToCanvas(canvas);
		controlMatrix.postScale(minScale, minScale, actualCenterX, actualCenterY);
		assertEquals(controlMatrix, canvas.getMatrix());
	}

	public void testShouldNotScaleAboveMaximum() {
		Matrix controlMatrix = new Matrix();
		Canvas canvas = surfaceHolderStub.getCanvas();
		assertEquals(controlMatrix, canvas.getMatrix());

		float maxScale = Perspective.MAX_SCALE;
		assertEquals(20f, maxScale);

		float scale = 21f;
		perspective.multiplyScale(scale);
		perspective.applyToCanvas(canvas);
		controlMatrix.postScale(maxScale, maxScale, actualCenterX, actualCenterY);
		assertEquals(controlMatrix, canvas.getMatrix());
	}

	public void testShouldTranslateCorrectly() {
		Matrix controlMatrix = new Matrix();
		Canvas canvas = surfaceHolderStub.getCanvas();
		assertEquals(controlMatrix, canvas.getMatrix());

		float dx = 10f, dy = 20f;
		perspective.translate(dx, dy);
		perspective.applyToCanvas(canvas);
		controlMatrix.postTranslate(dx, dy);
		assertEquals(controlMatrix, canvas.getMatrix());
	}

	public void testShouldRespectBoundaries() {
		Matrix controlMatrix = new Matrix();
		Canvas canvas = surfaceHolderStub.getCanvas();
		assertEquals(controlMatrix, canvas.getMatrix());

		perspective.multiplyScale(2f);
		perspective.applyToCanvas(canvas);

		controlMatrix.postScale(2f, 2f, actualCenterX, actualCenterY);
		assertEquals(controlMatrix, canvas.getMatrix());

		// perspective.translate(SurfaceHolderStub.WIDTH - 1f, SurfaceHolderStub.HEIGHT * 2f);
		// perspective.applyToCanvas(canvas);

		// controlMatrix.postTranslate(SurfaceHolderStub.WIDTH - 1f, SurfaceHolderStub.HEIGHT);
		// assertEquals(controlMatrix, canvas.getMatrix());
	}

	public void testShouldApplyToCanvas() {
		Bitmap testCanvasBitmap = Bitmap.createBitmap((int) SurfaceHolderStub.WIDTH, (int) SurfaceHolderStub.HEIGHT,
				Bitmap.Config.ARGB_8888);
		Canvas testCanvas = new Canvas(testCanvasBitmap);
		Bitmap controlCanvasBitmap = Bitmap.createBitmap((int) SurfaceHolderStub.WIDTH, (int) SurfaceHolderStub.HEIGHT,
				Bitmap.Config.ARGB_8888);
		Canvas controlCanvas = new Canvas(controlCanvasBitmap);

		perspective.multiplyScale(2f);
		// perspective.translate(2f, 2f);
		perspective.applyToCanvas(testCanvas);
		Matrix testMatrix = testCanvas.getMatrix();

		controlCanvas.scale(2f, 2f, actualCenterX, actualCenterY);
		// controlCanvas.translate(2f, 2f);
		Matrix controlMatrix = controlCanvas.getMatrix();

		assertEquals(testMatrix, controlMatrix);
		controlCanvasBitmap.recycle();
		testCanvasBitmap.recycle();
		controlCanvasBitmap = null;
		testCanvasBitmap = null;
		testCanvas = null;
		controlCanvas = null;
	}
}
