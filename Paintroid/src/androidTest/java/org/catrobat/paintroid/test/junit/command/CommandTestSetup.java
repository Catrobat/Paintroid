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

package org.catrobat.paintroid.test.junit.command;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.tools.Layer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class CommandTestSetup {

	static final int BITMAP_BASE_COLOR = Color.GREEN;
	static final int BITMAP_REPLACE_COLOR = Color.CYAN;
	static final int PAINT_BASE_COLOR = Color.BLUE;

	Command commandUnderTest;
	Command commandUnderTestNull; // can be used to pass null to constructor
	Paint paintUnderTest;
	PointF pointUnderTest;
	Canvas canvasUnderTest;
	Bitmap bitmapUnderTest;
	Layer layerUnderTest;
	Bitmap canvasBitmapUnderTest;

	@Before
	public void setUp() {
		canvasUnderTest = new Canvas();
		// !WARNING don't make your test-bitmaps to large width*height*(Config.) byte...
		// and assume that the garbage collector is rather slow!
		// Some tests may also need to copy the original bitmap...
		canvasBitmapUnderTest = Bitmap.createBitmap(80, 80, Config.ARGB_8888);
		canvasBitmapUnderTest.eraseColor(BITMAP_BASE_COLOR);
		bitmapUnderTest = canvasBitmapUnderTest.copy(Config.ARGB_8888, true);
		layerUnderTest = new Layer(0, bitmapUnderTest);
		canvasUnderTest.setBitmap(canvasBitmapUnderTest);
		paintUnderTest = new Paint();
		paintUnderTest.setColor(PAINT_BASE_COLOR);
		paintUnderTest.setStrokeWidth(0);
		paintUnderTest.setStyle(Paint.Style.STROKE);
		paintUnderTest.setStrokeCap(Cap.BUTT);
		pointUnderTest = new PointF(canvasBitmapUnderTest.getWidth() / 2, canvasBitmapUnderTest.getHeight() / 2);
	}

	@After
	public void tearDown() {
		canvasUnderTest = null;
		canvasBitmapUnderTest.recycle();
		canvasBitmapUnderTest = null;
		bitmapUnderTest.recycle();
		bitmapUnderTest = null;
		layerUnderTest = null;
		paintUnderTest = null;
		pointUnderTest = null;
	}

	@Test
	public void testRunWithNullParameters() {
		if (commandUnderTestNull != null) {
			commandUnderTestNull.run(null, null);
			commandUnderTestNull.run(null, null);
			commandUnderTestNull.run(canvasUnderTest, null);
			commandUnderTestNull.run(null, layerUnderTest);
		}
	}
}
