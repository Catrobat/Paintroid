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

import android.graphics.Path;
import android.graphics.RectF;

import org.catrobat.paintroid.command.implementation.PathCommand;
import org.catrobat.paintroid.common.CommonFactory;
import org.catrobat.paintroid.test.utils.PaintroidAsserts;
import org.junit.Before;
import org.junit.Test;

public class PathCommandTest extends CommandTestSetup {

	@Override
	@Before
	public void setUp() {
		super.setUp();
		Path pathUnderTest = new Path();
		pathUnderTest.moveTo(1, 0);
		pathUnderTest.lineTo(1, canvasBitmapUnderTest.getHeight());
		commandUnderTest = new PathCommand(paintUnderTest, pathUnderTest, new CommonFactory());
	}

	@Test
	public void testPathOutOfBounds() {
		Path path = new Path();

		float left = canvasBitmapUnderTest.getWidth() + 50;
		float top = canvasBitmapUnderTest.getHeight() + 50;
		float right = canvasBitmapUnderTest.getWidth() + 100;
		float bottom = canvasBitmapUnderTest.getHeight() + 100;
		path.addRect(new RectF(left, top, right, bottom), Path.Direction.CW);

		commandUnderTest = new PathCommand(paintUnderTest, path, new CommonFactory());

		commandUnderTest.run(canvasUnderTest, null);
	}

	@Test
	public void testRun() {
		int color = paintUnderTest.getColor();
		int height = bitmapUnderTest.getHeight();

		for (int heightIndex = 0; heightIndex < height; heightIndex++) {
			bitmapUnderTest.setPixel(1, heightIndex, color);
		}
		commandUnderTest.run(canvasUnderTest, null);
		PaintroidAsserts.assertBitmapEquals(bitmapUnderTest, canvasBitmapUnderTest);
	}
}
