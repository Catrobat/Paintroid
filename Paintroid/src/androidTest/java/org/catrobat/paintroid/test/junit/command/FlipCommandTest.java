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

import static org.junit.Assert.*;

public class FlipCommandTest extends CommandTestSetup {

	private int mBitmapHeigt;
	private int mBitmapWidth;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		mBitmapHeigt = mBitmapUnderTest.getHeight();
		mBitmapWidth = mBitmapUnderTest.getWidth();
	}

	@Test
	public void testVerticalFlip() {
		mCommandUnderTest = new FlipCommand(FlipDirection.FLIP_VERTICAL);
		mBitmapUnderTest.setPixel(0, mBitmapHeigt / 2, PAINT_BASE_COLOR);
		mCommandUnderTest.run(mCanvasUnderTest, mLayerUnderTest);
		int pixel = mBitmapUnderTest.getPixel(mBitmapWidth - 1, mBitmapWidth / 2);
		assertEquals("pixel should be paint_base_color", PAINT_BASE_COLOR, pixel);
	}

	@Test
	public void testHorizontalFlip() {
		mCommandUnderTest = new FlipCommand(FlipDirection.FLIP_HORIZONTAL);
		mBitmapUnderTest.setPixel(mBitmapWidth / 2, 0, PAINT_BASE_COLOR);
		mCommandUnderTest.run(mCanvasUnderTest, mLayerUnderTest);
		int pixel = mBitmapUnderTest.getPixel(mBitmapWidth / 2, mBitmapWidth - 1);
		assertEquals("pixel should be paint_base_color", PAINT_BASE_COLOR, pixel);
	}

}
