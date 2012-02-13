/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test.junit.ui;

import junit.framework.TestCase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import at.tugraz.ist.paintroid.test.junit.stubs.SurfaceHolderStub;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.ui.Perspective;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfacePerspective;

public class PerspectiveTests extends TestCase {

	SurfaceHolderStub surfaceHolderStub;
	Perspective perspective;
	float actualCenterX;
	float actualCenterY;

	@Override
	public void setUp() {
		surfaceHolderStub = new SurfaceHolderStub();
		perspective = new DrawingSurfacePerspective(surfaceHolderStub);
		Rect surfaceFrame = surfaceHolderStub.getSurfaceFrame();
		actualCenterX = surfaceFrame.exactCenterX();
		actualCenterY = surfaceFrame.exactCenterY();
	}

	public void testShouldInitializeCorrectly() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		float surfaceWidth = (Float) PrivateAccess.getMemberValue(DrawingSurfacePerspective.class, perspective,
				"surfaceWidth");
		float surfaceHeight = (Float) PrivateAccess.getMemberValue(DrawingSurfacePerspective.class, perspective,
				"surfaceHeight");
		assertEquals(SurfaceHolderStub.WIDTH, surfaceWidth);
		assertEquals(SurfaceHolderStub.HEIGHT, surfaceHeight);

		float surfaceCenterX = (Float) PrivateAccess.getMemberValue(DrawingSurfacePerspective.class, perspective,
				"surfaceCenterX");
		float surfaceCenterY = (Float) PrivateAccess.getMemberValue(DrawingSurfacePerspective.class, perspective,
				"surfaceCenterY");
		assertEquals(actualCenterX, surfaceCenterX);
		assertEquals(actualCenterY, surfaceCenterY);

		float surfaceScale = (Float) PrivateAccess.getMemberValue(DrawingSurfacePerspective.class, perspective,
				"surfaceScale");
		assertEquals(1f, surfaceScale);

		float surfaceTranslationX = (Float) PrivateAccess.getMemberValue(DrawingSurfacePerspective.class, perspective,
				"surfaceTranslationX");
		float surfaceTranslationY = (Float) PrivateAccess.getMemberValue(DrawingSurfacePerspective.class, perspective,
				"surfaceTranslationY");
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

		float minScale = DrawingSurfacePerspective.MIN_SCALE;
		assertEquals(0.5f, minScale);

		float scale = 0.1f;
		perspective.multiplyScale(scale);
		perspective.applyToCanvas(canvas);
		controlMatrix.postScale(minScale, minScale, actualCenterX, actualCenterY);
		assertEquals(controlMatrix, canvas.getMatrix());
	}

	public void testShouldNotScaleAboveMaximum() {
		Matrix controlMatrix = new Matrix();
		Canvas canvas = surfaceHolderStub.getCanvas();
		assertEquals(controlMatrix, canvas.getMatrix());

		float maxScale = DrawingSurfacePerspective.MAX_SCALE;
		assertEquals(15f, maxScale);

		float scale = 16f;
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
		Canvas testCanvas = new Canvas(Bitmap.createBitmap((int) SurfaceHolderStub.WIDTH,
				(int) SurfaceHolderStub.HEIGHT, Bitmap.Config.ARGB_8888));
		Canvas controlCanvas = new Canvas(Bitmap.createBitmap((int) SurfaceHolderStub.WIDTH,
				(int) SurfaceHolderStub.HEIGHT, Bitmap.Config.ARGB_8888));

		perspective.multiplyScale(2f);
		// perspective.translate(2f, 2f);
		perspective.applyToCanvas(testCanvas);
		Matrix testMatrix = testCanvas.getMatrix();

		controlCanvas.scale(2f, 2f, actualCenterX, actualCenterY);
		// controlCanvas.translate(2f, 2f);
		Matrix controlMatrix = controlCanvas.getMatrix();

		assertEquals(testMatrix, controlMatrix);
	}
}
