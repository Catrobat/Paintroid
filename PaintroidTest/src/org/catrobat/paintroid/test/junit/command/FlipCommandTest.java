/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.implementation.FlipCommand;
import org.catrobat.paintroid.command.implementation.FlipCommand.FlipDirection;
import org.catrobat.paintroid.test.junit.stubs.DrawingSurfaceStub;
import org.junit.Before;

public class FlipCommandTest extends CommandTestSetup {

	private int mBitmapHeigt;
	private int mBitmapWidth;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mBitmapHeigt = mBitmapUnderTest.getHeight();
		mBitmapWidth = mBitmapUnderTest.getWidth();
		PaintroidApplication.drawingSurface = new DrawingSurfaceStub(getContext());
	}

	public void testVerticalFlip() {
		mCommandUnderTest = new FlipCommand(FlipDirection.FLIP_VERTICAL);
		mBitmapUnderTest.setPixel(0, mBitmapHeigt / 2, PAINT_BASE_COLOR);
		mCommandUnderTest.run(mCanvasUnderTest, mBitmapUnderTest);
		int pixel = PaintroidApplication.drawingSurface.getBitmap().getPixel(mBitmapWidth - 1, mBitmapWidth / 2);
		assertEquals("pixel should be paint_base_color", PAINT_BASE_COLOR, pixel);
	}

	public void testHorizontalFlip() {
		mCommandUnderTest = new FlipCommand(FlipDirection.FLIP_HORIZONTAL);
		mBitmapUnderTest.setPixel(mBitmapWidth / 2, 0, PAINT_BASE_COLOR);
		mCommandUnderTest.run(mCanvasUnderTest, mBitmapUnderTest);
		int pixel = PaintroidApplication.drawingSurface.getBitmap().getPixel(mBitmapWidth / 2, mBitmapWidth - 1);
		assertEquals("pixel should be paint_base_color", PAINT_BASE_COLOR, pixel);
	}

}
