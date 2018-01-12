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

import android.graphics.PointF;

import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.test.utils.PaintroidAsserts;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PointCommandTest extends CommandTestSetup {

	@Override
	@Before
	public void setUp() {
		super.setUp();
		commandUnderTest = new PointCommand(paintUnderTest, pointUnderTest);
		commandUnderTestNull = new PointCommand(null, null);
	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
	}

	@Test
	public void testRun() {
		bitmapUnderTest.setPixel((int) pointUnderTest.x, (int) pointUnderTest.y, paintUnderTest.getColor());
		commandUnderTest.run(canvasUnderTest, null);
		PaintroidAsserts.assertBitmapEquals(bitmapUnderTest, canvasBitmapUnderTest);
	}

	@Test
	public void testRunOutOfBounds() {
		pointUnderTest = new PointF(canvasBitmapUnderTest.getHeight() + 1, canvasBitmapUnderTest.getWidth() + 1);
		commandUnderTest = new PointCommand(paintUnderTest, pointUnderTest);
		commandUnderTest.run(canvasUnderTest, null);
		PaintroidAsserts.assertBitmapEquals(bitmapUnderTest, canvasBitmapUnderTest);
	}
}
