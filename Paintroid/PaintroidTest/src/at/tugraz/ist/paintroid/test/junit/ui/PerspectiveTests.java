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
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.SurfaceHolder;
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
		actualCenterX = SurfaceHolderStub.WIDTH / 2f;
		actualCenterY = SurfaceHolderStub.HEIGHT / 2f;
	}

	public void testShouldInitializeCorrectly() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		SurfaceHolder holder = (SurfaceHolder) PrivateAccess.getMemberValue(DrawingSurfacePerspective.class,
				perspective, "surfaceHolder");
		assertSame(surfaceHolderStub, holder);

		PointF surfaceCenter = (PointF) PrivateAccess.getMemberValue(DrawingSurfacePerspective.class, perspective,
				"surfaceCenter");
		assertEquals(actualCenterX, surfaceCenter.x);
		assertEquals(actualCenterY, surfaceCenter.y);

		PointF surfaceTranslation = (PointF) PrivateAccess.getMemberValue(DrawingSurfacePerspective.class, perspective,
				"surfaceTranslation");
		assertEquals(0f, surfaceTranslation.x);
		assertEquals(0f, surfaceTranslation.y);
	}

	public void testShouldScaleCorrectly() {
		Matrix controlMatrix = new Matrix();
		Canvas canvas = surfaceHolderStub.getCanvas();
		assertEquals(controlMatrix, canvas.getMatrix());

		float scale = 1.5f;
		perspective.scale(scale);
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
		perspective.scale(scale);
		controlMatrix.postScale(minScale, minScale, actualCenterX, actualCenterY);
		assertEquals(controlMatrix, canvas.getMatrix());
	}

	public void testShouldTranslateCorrectly() {
		Matrix controlMatrix = new Matrix();
		Canvas canvas = surfaceHolderStub.getCanvas();
		assertEquals(controlMatrix, canvas.getMatrix());

		float dx = 10f, dy = 20f;
		perspective.translate(dx, dy);
		controlMatrix.postTranslate(dx, dy);
		assertEquals(controlMatrix, canvas.getMatrix());
	}

	// public void testShouldScaleAndTranslateCorrectly() {
	//
	// }
}
