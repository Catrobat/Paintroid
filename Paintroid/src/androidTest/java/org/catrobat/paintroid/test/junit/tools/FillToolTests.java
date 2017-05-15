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

package org.catrobat.paintroid.test.junit.tools;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.helper.FillAlgorithm;
import org.catrobat.paintroid.tools.implementation.FillTool;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;
import org.junit.Before;
import org.junit.Test;

import java.util.Queue;

public class FillToolTests extends BaseToolTest {
	private static final float NO_TOLERANCE = 0.0f;
	private static final float HALF_TOLERANCE = FillTool.MAX_ABSOLUTE_TOLERANCE / 2.0f;
	private static final float MAX_TOLERANCE = FillTool.MAX_ABSOLUTE_TOLERANCE;

	public FillToolTests() {
		super();
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		mToolToTest = new FillTool(getActivity(), ToolType.FILL);
		super.setUp();
	}

	@Test
	public void testShouldReturnCorrectToolType() {
		ToolType toolType = mToolToTest.getToolType();
		assertEquals(ToolType.FILL, toolType);
	}

	@Test
	public void testFillToolAlgorithmMembers() throws NoSuchFieldException, IllegalAccessException {
		int width = 10;
		int height = 20;
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Point clickedPixel = new Point(width / 2, height / 2);
		int targetColor = 16777215;
		int replacementColor = 0;

		FillAlgorithm fillAlgorithm = new FillAlgorithm(bitmap, clickedPixel, targetColor, replacementColor, HALF_TOLERANCE);

		int[][] algorithmPixels = (int[][]) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels");
		assertEquals("Wrong array size", height, algorithmPixels.length);
		assertEquals("Wrong array size", width, algorithmPixels[0].length);

		int algorithmTargetColor = (Integer) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mTargetColor");
		int algorithmReplacementColor = (Integer) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mReplacementColor");
		int algorithmColorTolerance = (Integer) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mColorToleranceThresholdSquared");
		assertEquals("Wrong target color", targetColor, algorithmTargetColor);
		assertEquals("Wrong replacement color", replacementColor, algorithmReplacementColor);
		assertEquals("Wrong color tolerance", (int) (HALF_TOLERANCE * HALF_TOLERANCE), algorithmColorTolerance);

		Point algorithmClickedPixel = (Point) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mClickedPixel");
		assertEquals("Wrong point for clicked pixel", clickedPixel, algorithmClickedPixel);

		Queue algorithmRanges = (Queue) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mRanges");
		assertTrue("Queue for ranges should be empty", algorithmRanges.isEmpty());
	}

	@Test
	public void testFillingOnEmptyBitmap() throws NoSuchFieldException, IllegalAccessException {
		int width = 10;
		int height = 20;
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Point clickedPixel = new Point(width / 2, height / 2);
		int targetColor = 16777215;
		int replacementColor = 0;

		FillAlgorithm fillAlgorithm = new FillAlgorithm(bitmap, clickedPixel, targetColor, replacementColor, NO_TOLERANCE);
		fillAlgorithm.performFilling();

		int[][] pixels = (int[][]) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels");
		assertEquals("Wrong array size", height, pixels.length);
		assertEquals("Wrong array size", width, pixels[0].length);
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				assertEquals("Color should have been replaced", targetColor, pixels[row][col]);
			}
		}
	}

	@Test
	public void testFillingOnNotEmptyBitmap() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
		int width = 6;
		int height = 8;
		Point clickedPixel = new Point(width / 2, height / 2);
		int targetColor = 16777215;
		int boundaryColor = 16000000;
		int replacementColor = 0;
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		FillAlgorithm fillAlgorithm = new FillAlgorithm(bitmap, clickedPixel, targetColor, replacementColor, NO_TOLERANCE);
		int[][] pixels = (int[][]) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels");
		assertEquals("Wrong array size", height, pixels.length);
		assertEquals("Wrong array size", width, pixels[0].length);

		pixels[0][1] = boundaryColor;
		pixels[1][0] = boundaryColor;
		PrivateAccess.setMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels", pixels);

		fillAlgorithm.performFilling();

		pixels = (int[][]) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels");
		assertEquals("Color of upper left pixel should not have been replaced", 0, pixels[0][0]);
		assertEquals("Boundary color should not have been replaced",
				boundaryColor, pixels[0][1]);
		assertEquals("Boundary color should not have been replaced",
				boundaryColor, pixels[1][0]);
		assertEquals("Pixel color should have been replaced",
				targetColor, pixels[1][1]);
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (row > 1 || col > 1) {
					assertEquals("Pixel color should have been replaced", targetColor, pixels[row][col]);
				}
			}
		}
	}

	@Test
	public void testFillingWithMaxColorTolerance() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
		int width = 6;
		int height = 8;
		Point clickedPixel = new Point(width / 2, height / 2);
		int targetColor = Color.argb(0xFF, 0xFF, 0xFF, 0xFF);
		int replacementColor = 0;
		int maxTolerancePerChannel = 0xFF;
		int boundaryColor = Color.argb(maxTolerancePerChannel, maxTolerancePerChannel, maxTolerancePerChannel, maxTolerancePerChannel);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		FillAlgorithm fillAlgorithm = new FillAlgorithm(bitmap, clickedPixel, targetColor, replacementColor, MAX_TOLERANCE);
		int[][] pixels = (int[][]) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels");
		assertEquals("Wrong array size", height, pixels.length);
		assertEquals("Wrong array size", width, pixels[0].length);

		pixels[0][1] = boundaryColor;
		pixels[1][0] = boundaryColor;
		PrivateAccess.setMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels", pixels);

		fillAlgorithm.performFilling();

		pixels = (int[][]) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels");
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				assertEquals("Pixel color should have been replaced", targetColor, pixels[row][col]);
			}
		}
	}

	@Test
	public void testFillingWhenOutOfTolerance() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
		int width = 6;
		int height = 8;
		Point clickedPixel = new Point(width / 2, height / 2);
		int targetColor = Color.argb(0xFF, 0xFF, 0xFF, 0xFF);
		int replacementColor = 0;
		int maxTolerancePerChannel = 0xFF;
		int boundaryColor = Color.argb(maxTolerancePerChannel, maxTolerancePerChannel, maxTolerancePerChannel, maxTolerancePerChannel);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		FillAlgorithm fillAlgorithm = new FillAlgorithm(bitmap, clickedPixel, targetColor, replacementColor, MAX_TOLERANCE - 1);
		int[][] pixels = (int[][]) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels");
		assertEquals("Wrong array size", height, pixels.length);
		assertEquals("Wrong array size", width, pixels[0].length);

		pixels[0][1] = boundaryColor;
		pixels[1][0] = boundaryColor;
		PrivateAccess.setMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels", pixels);

		fillAlgorithm.performFilling();

		pixels = (int[][]) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels");
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (row == 0 && col == 0) {
					assertTrue("Pixel color should not have been replaced", targetColor != pixels[row][col]);
					continue;
				}
				assertEquals("Pixel color should have been replaced", targetColor, pixels[row][col]);
			}
		}
	}

	@Test
	public void testEqualTargetAndReplacementColorWithTolerance() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
		int width = 8;
		int height = 8;
		Point clickedPixel = new Point(width / 2, height / 2);
		Point boundaryPixel = new Point(width / 4, height / 4);
				Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		int targetColor = 0;
		int replacementColor = targetColor;
		int boundaryColor = Color.argb(0xFF, 0xFF, 0xFF, 0xFF);

		FillAlgorithm fillAlgorithm = new FillAlgorithm(bitmap, clickedPixel, targetColor, replacementColor, HALF_TOLERANCE);
		int[][] pixels = (int[][]) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels");
		assertEquals("Wrong array size", height, pixels.length);
		assertEquals("Wrong array size", width, pixels[0].length);

		pixels[boundaryPixel.x][boundaryPixel.y] = boundaryColor;
		PrivateAccess.setMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels", pixels);

		fillAlgorithm.performFilling();

		pixels = (int[][]) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels");
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (row == boundaryPixel.y && col == boundaryPixel.y) {
					assertTrue("Pixel color should not have been replaced", boundaryColor == pixels[row][col]);
					continue;
				}
				assertEquals("Pixel color should have been replaced", targetColor, pixels[row][col]);
			}
		}
	}

	@Test
	public void testFillingWhenTargetColorIsWithinTolerance() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
		int targetColor = -16777216;
		int boundaryColor = -1358312;
		int replacementColor = 0;
		int height = 8;
		int width = 8;

		Point topLeftQuarterPixel = new Point(width/4, height/4);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		FillAlgorithm fillAlgorithm = new FillAlgorithm(bitmap, topLeftQuarterPixel, targetColor, replacementColor, HALF_TOLERANCE);
		int[][] pixels = (int[][]) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels");

		for (int col = 0; col < width; col++) {
			pixels[height/2][col] = targetColor;
		}
		Point boundaryPixel = new Point(height/4, width/2);
		pixels[boundaryPixel.y][boundaryPixel.x] = boundaryColor;

		PrivateAccess.setMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels", pixels);
		fillAlgorithm.performFilling();

		int[][] actualPixels = (int[][]) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels");
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (row == boundaryPixel.y && col == boundaryPixel.x) {
					assertEquals("Wrong pixel color for boundary pixel", boundaryColor, actualPixels[row][col]);
				} else {
					assertEquals("Wrong pixel color for pixel[" + row + "][" + col + "]",
							targetColor, actualPixels[row][col]);
				}
			}
		}
	}

	@Test
	public void testFillingWithSpiral() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
		int targetColor = 16777215;
		int boundaryColor = 16000000;
		int replacementColor = 0;
		int[][] pixels = createBitmapArrayAndDrawSpiral(replacementColor, boundaryColor);
		int height = pixels.length;
		int width = pixels[0].length;
		Point clickedPixel = new Point(1, 1);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		FillAlgorithm fillAlgorithm = new FillAlgorithm(bitmap, clickedPixel, targetColor, replacementColor, NO_TOLERANCE);
		PrivateAccess.setMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels", pixels);
		fillAlgorithm.performFilling();

		int[][] actualPixels = (int[][]) PrivateAccess.getMemberValue(FillAlgorithm.class, fillAlgorithm, "mPixels");
		int[][] expectedPixels = createBitmapArrayAndDrawSpiral(targetColor, boundaryColor);

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				assertEquals("Wrong pixel color for pixels[" + row + "][" + col + "]",
						expectedPixels[row][col], actualPixels[row][col]);
			}
		}
	}

	int[][] createBitmapArrayAndDrawSpiral(int backgroundColor, int boundaryColor) {
		int width = 10;
		int height = 10;
		int[][] pixels = new int[height][width];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pixels[y][x] = backgroundColor;
			}
		}

		pixels[4][4] = boundaryColor;
		pixels[5][4] = boundaryColor;
		pixels[5][5] = boundaryColor;
		pixels[4][6] = boundaryColor;
		pixels[3][6] = boundaryColor;
		pixels[2][5] = boundaryColor;
		pixels[2][4] = boundaryColor;
		pixels[2][3] = boundaryColor;
		pixels[3][2] = boundaryColor;
		pixels[4][2] = boundaryColor;
		pixels[5][2] = boundaryColor;
		pixels[6][2] = boundaryColor;
		pixels[7][3] = boundaryColor;
		pixels[7][4] = boundaryColor;

		return pixels;
	}

}
