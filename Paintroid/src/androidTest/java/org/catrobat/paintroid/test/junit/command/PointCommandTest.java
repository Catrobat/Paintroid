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

import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.test.utils.PaintroidAsserts;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.PointF;

public class PointCommandTest extends CommandTestSetup {

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mCommandUnderTest = new PointCommand(mPaintUnderTest, mPointUnderTest);
		mCommandUnderTestNull = new PointCommand(null, null);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testRun() {
		mBitmapUnderTest.setPixel((int) mPointUnderTest.x, (int) mPointUnderTest.y, mPaintUnderTest.getColor());
		mCommandUnderTest.run(mCanvasUnderTest, null);
		PaintroidAsserts.assertBitmapEquals(mBitmapUnderTest, mCanvasBitmapUnderTest);
	}

	@Test
	public void testRunOutOfBounds() {
		mPointUnderTest = new PointF(mCanvasBitmapUnderTest.getHeight() + 1, mCanvasBitmapUnderTest.getWidth() + 1);
		mCommandUnderTest = new PointCommand(mPaintUnderTest, mPointUnderTest);
		mCommandUnderTest.run(mCanvasUnderTest, null);
		PaintroidAsserts.assertBitmapEquals(mBitmapUnderTest, mCanvasBitmapUnderTest);
	}
}
