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

import org.catrobat.paintroid.command.implementation.FlipCommand;
import org.catrobat.paintroid.command.implementation.FlipCommand.FlipDirection;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FlipCommandTest extends CommandTestSetup {

	private int bitmapHeight;
	private int bitmapWidth;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		bitmapHeight = bitmapUnderTest.getHeight();
		bitmapWidth = bitmapUnderTest.getWidth();
	}

	@Test
	public void testVerticalFlip() {
		commandUnderTest = new FlipCommand(FlipDirection.FLIP_VERTICAL);
		bitmapUnderTest.setPixel(0, bitmapHeight / 2, PAINT_BASE_COLOR);
		commandUnderTest.run(canvasUnderTest, layerUnderTest);
		int pixel = bitmapUnderTest.getPixel(bitmapWidth - 1, bitmapWidth / 2);
		assertEquals("pixel should be paint_base_color", PAINT_BASE_COLOR, pixel);
	}

	@Test
	public void testHorizontalFlip() {
		commandUnderTest = new FlipCommand(FlipDirection.FLIP_HORIZONTAL);
		bitmapUnderTest.setPixel(bitmapWidth / 2, 0, PAINT_BASE_COLOR);
		commandUnderTest.run(canvasUnderTest, layerUnderTest);
		int pixel = bitmapUnderTest.getPixel(bitmapWidth / 2, bitmapWidth - 1);
		assertEquals("pixel should be paint_base_color", PAINT_BASE_COLOR, pixel);
	}
}
