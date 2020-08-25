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

package org.catrobat.paintroid.test.junit.algorithm;

import android.graphics.Bitmap;
import android.graphics.Point;

import org.catrobat.paintroid.tools.helper.JavaFillAlgorithm;
import org.catrobat.paintroid.tools.implementation.FillTool;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Queue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class FillAlgorithmTest {
	private static final float HALF_TOLERANCE = FillTool.MAX_ABSOLUTE_TOLERANCE / 2.0f;

	@Test
	public void testFillAlgorithmMembers() {
		int width = 10;
		int height = 20;
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Point clickedPixel = new Point(width / 2, height / 2);
		int targetColor = 16777215;
		int replacementColor = 0;

		JavaFillAlgorithm fillAlgorithm = new JavaFillAlgorithm();
		fillAlgorithm.setParameters(bitmap, clickedPixel, targetColor, replacementColor, HALF_TOLERANCE);

		int[][] algorithmPixels = fillAlgorithm.pixels;
		assertEquals("Wrong array size", height, algorithmPixels.length);
		assertEquals("Wrong array size", width, algorithmPixels[0].length);

		int algorithmTargetColor = fillAlgorithm.targetColor;
		int algorithmReplacementColor = fillAlgorithm.replacementColor;
		int algorithmColorTolerance = fillAlgorithm.colorToleranceThresholdSquared;
		assertEquals("Wrong target color", targetColor, algorithmTargetColor);
		assertEquals("Wrong replacement color", replacementColor, algorithmReplacementColor);
		assertEquals("Wrong color tolerance", (int) (HALF_TOLERANCE * HALF_TOLERANCE), algorithmColorTolerance);

		Point algorithmClickedPixel = fillAlgorithm.clickedPixel;
		assertEquals("Wrong point for clicked pixel", clickedPixel, algorithmClickedPixel);

		Queue algorithmRanges = fillAlgorithm.ranges;
		assertTrue("Queue for ranges should be empty", algorithmRanges.isEmpty());
	}
}
