/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.paintroid.test.junit.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Path;
import at.tugraz.ist.paintroid.command.implementation.PathCommand;
import at.tugraz.ist.paintroid.test.utils.PaintroidAsserts;

public class PathCommandTest extends CommandTestSetup {

	protected Path mPathUnderTest;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mPathUnderTest = new Path();
		mPathUnderTest.moveTo(0, 0);
		mPathUnderTest.lineTo(0, 0);
		mPathUnderTest.lineTo(0, 9);
		mCommandUnderTest = new PathCommand(mPaintUnderTest, mPathUnderTest);
		mCommandUnderTestNull = new PathCommand(null, null);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
		mPathUnderTest = null;
	}

	@Test
	public void testRun() {
		int color = mPaintUnderTest.getColor();
		int height = mBitmapUnderTest.getHeight();
		for (int heightIndex = 0; heightIndex < height; heightIndex++)
			mBitmapUnderTest.setPixel(0, heightIndex, color);
		mCommandUnderTest.run(mCanvasUnderTest, null);
		mCanvasUnderTest.drawPath(mPathUnderTest, mPaintUnderTest);
		PaintroidAsserts.assertBitmapEquals(mBitmapUnderTest, mCanvasBitmapUnderTest);
		mCommandUnderTestNull.run(null, null);
		mCommandUnderTestNull.run(mCanvasUnderTest, null);
	}
}
