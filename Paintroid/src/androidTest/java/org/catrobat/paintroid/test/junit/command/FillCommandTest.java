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

package org.catrobat.paintroid.test.junit.command;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.FillCommand;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.model.Layer;
import org.catrobat.paintroid.model.LayerModel;
import org.catrobat.paintroid.tools.helper.JavaFillAlgorithmFactory;
import org.catrobat.paintroid.tools.implementation.FillTool;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class FillCommandTest {
	private static final float NO_TOLERANCE = 0.0f;
	private static final float HALF_TOLERANCE = FillTool.MAX_ABSOLUTE_TOLERANCE / 2.0f;
	private static final float MAX_TOLERANCE = FillTool.MAX_ABSOLUTE_TOLERANCE;

	private LayerContracts.Model layerModel;

	@Before
	public void setUp() {
		layerModel = new LayerModel();
		Layer layer = new Layer(null);
		layerModel.addLayerAt(0, layer);
		layerModel.setCurrentLayer(layer);
	}

	@Test
	public void testFillingOnEmptyBitmap() {
		int width = 10;
		int height = 20;
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(Color.WHITE);
		layerModel.getCurrentLayer().setBitmap(bitmap);
		Point clickedPixel = new Point(width / 2, height / 2);
		int targetColor = Color.BLACK;
		Paint paint = new Paint();
		paint.setColor(targetColor);

		FillCommand fillCommand = new FillCommand(new JavaFillAlgorithmFactory(), clickedPixel, paint, NO_TOLERANCE);
		fillCommand.run(new Canvas(), layerModel);

		int[][] pixels = getPixelsFromBitmap(bitmap);
		assertEquals("Wrong array size", height, pixels.length);
		assertEquals("Wrong array size", width, pixels[0].length);
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				assertEquals("Color should have been replaced", targetColor, pixels[row][col]);
			}
		}
	}

	@Test
	public void testFillingOnNotEmptyBitmap() {
		int width = 6;
		int height = 8;
		Point clickedPixel = new Point(width / 2, height / 2);
		int targetColor = Color.GREEN;
		int boundaryColor = Color.RED;
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		layerModel.getCurrentLayer().setBitmap(bitmap);

		Paint paint = new Paint();
		paint.setColor(targetColor);

		int[][] pixels = getPixelsFromBitmap(bitmap);
		pixels[0][1] = boundaryColor;
		pixels[1][0] = boundaryColor;
		putPixelsToBitmap(bitmap, pixels);

		FillCommand fillCommand = new FillCommand(new JavaFillAlgorithmFactory(), clickedPixel, paint, NO_TOLERANCE);
		fillCommand.run(new Canvas(), layerModel);

		pixels = getPixelsFromBitmap(bitmap);
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
	public void testFillingWithMaxColorTolerance() {
		int width = 6;
		int height = 8;
		Point clickedPixel = new Point(width / 2, height / 2);
		int targetColor = Color.argb(0xFF, 0xFF, 0xFF, 0xFF);
		int replacementColor = 0;
		int maxTolerancePerChannel = 0xFF;
		int boundaryColor = Color.argb(maxTolerancePerChannel, maxTolerancePerChannel, maxTolerancePerChannel, maxTolerancePerChannel);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		layerModel.getCurrentLayer().setBitmap(bitmap);
		bitmap.eraseColor(replacementColor);
		Paint paint = new Paint();
		paint.setColor(targetColor);

		int[][] pixels = getPixelsFromBitmap(bitmap);
		pixels[0][1] = boundaryColor;
		pixels[1][0] = boundaryColor;
		putPixelsToBitmap(bitmap, pixels);

		FillCommand fillCommand = new FillCommand(new JavaFillAlgorithmFactory(), clickedPixel, paint, MAX_TOLERANCE);
		fillCommand.run(new Canvas(), layerModel);

		pixels = getPixelsFromBitmap(bitmap);

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				assertEquals("Pixel color should have been replaced", targetColor, pixels[row][col]);
			}
		}
	}

	@Test
	public void testFillingWhenOutOfTolerance() {
		int width = 6;
		int height = 8;
		Point clickedPixel = new Point(width / 2, height / 2);
		int targetColor = Color.argb(0xFF, 0xFF, 0xFF, 0xFF);
		int replacementColor = 0;
		int maxTolerancePerChannel = 0xFF;
		int boundaryColor = Color.argb(maxTolerancePerChannel, maxTolerancePerChannel, maxTolerancePerChannel, maxTolerancePerChannel);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		layerModel.getCurrentLayer().setBitmap(bitmap);
		bitmap.eraseColor(replacementColor);
		Paint paint = new Paint();
		paint.setColor(targetColor);

		int[][] pixels = getPixelsFromBitmap(bitmap);
		pixels[0][1] = boundaryColor;
		pixels[1][0] = boundaryColor;
		putPixelsToBitmap(bitmap, pixels);

		FillCommand fillCommand = new FillCommand(new JavaFillAlgorithmFactory(), clickedPixel, paint, MAX_TOLERANCE - 1);
		fillCommand.run(new Canvas(), layerModel);

		pixels = getPixelsFromBitmap(bitmap);

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (row == 0 && col == 0) {
					assertNotEquals("Pixel color should not have been replaced", targetColor, pixels[row][col]);
					continue;
				}
				assertEquals("Pixel color should have been replaced", targetColor, pixels[row][col]);
			}
		}
	}

	@Test
	public void testEqualTargetAndReplacementColorWithTolerance() {
		int width = 8;
		int height = 8;
		Point clickedPixel = new Point(width / 2, height / 2);
		Point boundaryPixel = new Point(width / 4, height / 4);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		layerModel.getCurrentLayer().setBitmap(bitmap);
		int targetColor = 0;
		int boundaryColor = Color.argb(0xFF, 0xFF, 0xFF, 0xFF);
		bitmap.eraseColor(targetColor);
		Paint paint = new Paint();
		paint.setColor(targetColor);

		int[][] pixels = getPixelsFromBitmap(bitmap);
		pixels[boundaryPixel.x][boundaryPixel.y] = boundaryColor;
		putPixelsToBitmap(bitmap, pixels);

		FillCommand fillCommand = new FillCommand(new JavaFillAlgorithmFactory(), clickedPixel, paint, HALF_TOLERANCE);
		fillCommand.run(new Canvas(), layerModel);

		pixels = getPixelsFromBitmap(bitmap);

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (row == boundaryPixel.y && col == boundaryPixel.y) {
					assertEquals("Pixel color should not have been replaced", boundaryColor, pixels[row][col]);
					continue;
				}
				assertEquals("Pixel color should have been replaced", targetColor, pixels[row][col]);
			}
		}
	}

	@Test
	public void testFillingWhenTargetColorIsWithinTolerance() {
		int targetColor = 0xFFAAEEAA;
		int boundaryColor = 0xFFFF0000;
		int replacementColor = 0xFFFFFFFF;
		int height = 8;
		int width = 8;

		Point topLeftQuarterPixel = new Point(width / 4, height / 4);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		layerModel.getCurrentLayer().setBitmap(bitmap);
		bitmap.eraseColor(replacementColor);
		Paint paint = new Paint();
		paint.setColor(targetColor);

		int[][] pixels = getPixelsFromBitmap(bitmap);
		for (int col = 0; col < width; col++) {
			pixels[height / 2][col] = targetColor;
		}
		Point boundaryPixel = new Point(width / 2, height / 4);
		pixels[boundaryPixel.y][boundaryPixel.x] = boundaryColor;
		putPixelsToBitmap(bitmap, pixels);
		FillCommand fillCommand = new FillCommand(new JavaFillAlgorithmFactory(), topLeftQuarterPixel, paint, HALF_TOLERANCE);
		fillCommand.run(new Canvas(), layerModel);

		int[][] actualPixels = getPixelsFromBitmap(bitmap);
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
	public void testFillingWithSpiral() {
		int targetColor = 0xFFAAEEAA;
		int boundaryColor = 0xFFFF0000;
		int replacementColor = 0xFFFFFFFF;
		int[][] pixels = createPixelArrayAndDrawSpiral(replacementColor, boundaryColor);
		int height = pixels.length;
		int width = pixels[0].length;
		Point clickedPixel = new Point(1, 1);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		layerModel.getCurrentLayer().setBitmap(bitmap);
		bitmap.eraseColor(replacementColor);
		Paint paint = new Paint();
		paint.setColor(targetColor);

		putPixelsToBitmap(bitmap, pixels);
		FillCommand fillCommand = new FillCommand(new JavaFillAlgorithmFactory(), clickedPixel, paint, HALF_TOLERANCE);
		fillCommand.run(new Canvas(), layerModel);

		int[][] actualPixels = getPixelsFromBitmap(bitmap);
		int[][] expectedPixels = createPixelArrayAndDrawSpiral(targetColor, boundaryColor);

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				assertEquals("Wrong pixel color for pixels[" + row + "][" + col + "]",
						expectedPixels[row][col], actualPixels[row][col]);
			}
		}
	}

	@Test
	public void testComplexDrawing() {
		int targetColor = 0xFFAAEEAA;
		int boundaryColor = 0xFFFF0000;
		int replacementColor = 0xFFFFFFFF;
		Paint paint = new Paint();
		paint.setColor(targetColor);

		int[][] pixels = createPixelArrayForComplexTest(replacementColor, boundaryColor);
		int height = pixels.length;
		int width = pixels[0].length;

		ArrayList<Point> clickedPixels = new ArrayList<>();
		Point topLeft = new Point(0, 0);
		Point topRight = new Point(width - 1, 0);
		Point bottomRight = new Point(width - 1, height - 1);
		Point bottomLeft = new Point(0, height - 1);
		clickedPixels.add(topLeft);
		clickedPixels.add(topRight);
		clickedPixels.add(bottomRight);
		clickedPixels.add(bottomLeft);

		for (Point clickedPixel : clickedPixels) {
			pixels = createPixelArrayForComplexTest(replacementColor, boundaryColor);
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			layerModel.getCurrentLayer().setBitmap(bitmap);
			bitmap.eraseColor(replacementColor);
			putPixelsToBitmap(bitmap, pixels);
			FillCommand fillCommand = new FillCommand(new JavaFillAlgorithmFactory(), clickedPixel, paint, HALF_TOLERANCE);
			fillCommand.run(new Canvas(), layerModel);

			int[][] actualPixels = getPixelsFromBitmap(bitmap);
			int[][] expectedPixels = createPixelArrayForComplexTest(targetColor, boundaryColor);

			for (int row = 0; row < pixels.length; row++) {
				for (int col = 0; col < pixels[0].length; col++) {
					assertEquals("Wrong pixel color, clicked " + clickedPixel.x + "/" + clickedPixel.y,
							expectedPixels[row][col], actualPixels[row][col]);
				}
			}
		}
	}

	@Test
	public void testSkipPixelsInCheckRangesFunction() {
		int targetColor = 0xFFAAEEAA;
		int boundaryColor = 0xFFFF0000;
		int replacementColor = 0xFFFFFFFF;
		Paint paint = new Paint();
		paint.setColor(targetColor);
		Point clickedPixel = new Point(0, 0);

		int[][] pixels = createPixelArrayForSkipPixelTest(replacementColor, boundaryColor);
		int height = pixels.length;
		int width = pixels[0].length;

		pixels = createPixelArrayForSkipPixelTest(replacementColor, boundaryColor);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		layerModel.getCurrentLayer().setBitmap(bitmap);
		bitmap.eraseColor(replacementColor);
		putPixelsToBitmap(bitmap, pixels);
		FillCommand fillCommand = new FillCommand(new JavaFillAlgorithmFactory(), clickedPixel, paint, HALF_TOLERANCE);
		fillCommand.run(new Canvas(), layerModel);

		int[][] actualPixels = getPixelsFromBitmap(bitmap);
		int[][] expectedPixels = createPixelArrayForSkipPixelTest(targetColor, boundaryColor);

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				assertEquals("Wrong pixel color", expectedPixels[row][col], actualPixels[row][col]);
			}
		}
	}

	@Ignore("Flaky test, sometimes fails on Jenkins. runtime.gc() is not an assurance that memory will be freed might.")
	@Test
	public void testCommandsDoNotLeakMemory() {
		List<Command> commands = new LinkedList<>();
		Bitmap testBitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
		Point clickedPixel = new Point(10, 10);
		Paint paint = new Paint();
		Canvas canvas = new Canvas();

		LayerModel layerModel = new LayerModel();
		Layer testLayer = new Layer(testBitmap);
		layerModel.addLayerAt(0, testLayer);
		layerModel.setCurrentLayer(testLayer);

		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

		for (int i = 0; i < 5; i++) {
			FillCommand fillCommand = new FillCommand(new JavaFillAlgorithmFactory(), clickedPixel, paint, 0.5f);
			fillCommand.run(canvas, layerModel);
			commands.add(fillCommand);
		}

		runtime.gc();
		long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
		assertThat(memoryAfter / 1024, is(lessThan(memoryBefore / 1024 + 10)));
	}

	@SuppressWarnings("UnnecessaryLocalVariable")
	private int[][] createPixelArrayForComplexTest(int backgroundColor, int boundaryColor) {
		int w = boundaryColor;
		int i = backgroundColor;

		int[][] testArray = {
				{i, i, i, i, i, i, i, i, i, i, i, i, i, i, i, i},
				{i, i, i, i, i, w, w, w, i, i, i, w, w, w, i, i},
				{i, i, i, i, i, i, w, i, i, i, w, i, i, i, w, i},
				{i, i, i, w, i, i, w, i, i, i, w, i, i, i, w, i},
				{i, i, w, i, i, w, i, w, i, i, i, i, i, i, w, i},
				{i, i, w, i, i, i, i, w, i, i, i, i, i, w, i, i},
				{i, i, w, w, w, i, w, i, i, i, w, i, i, i, w, i},
				{i, i, w, i, i, i, w, i, i, i, w, w, w, w, w, i},
				{w, i, i, w, w, w, i, i, i, i, i, i, i, i, i, i},
				{i, w, i, i, i, i, i, i, i, i, i, w, w, w, i, i},
				{i, i, i, i, i, i, i, i, i, i, i, i, w, i, i, i}};
		return testArray;
	}

	@SuppressWarnings("UnnecessaryLocalVariable")
	private int[][] createPixelArrayForSkipPixelTest(int backgroundColor, int boundaryColor) {
		int w = boundaryColor;
		int i = backgroundColor;

		int[][] testArray = {
				{i, i, i, i, w},
				{i, i, w, i, w},
				{i, w, i, i, w},
				{i, i, w, w, i},
				{i, i, i, i, i}};
		return testArray;
	}

	private int[][] createPixelArrayAndDrawSpiral(int backgroundColor, int boundaryColor) {
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

	private int[][] getPixelsFromBitmap(Bitmap bitmap) {
		int[][] pixels = new int[bitmap.getHeight()][bitmap.getWidth()];
		for (int i = 0; i < bitmap.getHeight(); i++) {
			bitmap.getPixels(pixels[i], 0, bitmap.getWidth(), 0, i, bitmap.getWidth(), 1);
		}
		return pixels;
	}

	private void putPixelsToBitmap(Bitmap bitmap, int[][] pixels) {
		assertEquals("Height is inconsistent", bitmap.getHeight(), pixels.length);
		assertEquals("Width is inconsistent", bitmap.getWidth(), pixels[0].length);
		for (int i = 0; i < bitmap.getHeight(); i++) {
			bitmap.setPixels(pixels[i], 0, bitmap.getWidth(), 0, i, bitmap.getWidth(), 1);
		}
	}
}
