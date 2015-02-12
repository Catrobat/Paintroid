/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
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

import java.util.Observable;
import java.util.Observer;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.PathCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Path;
import android.graphics.RectF;

public class PathCommandTest extends CommandTestSetup {

	protected Path mPathUnderTest;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mPathUnderTest = new Path();
		mPathUnderTest.moveTo(0, 0);
		mPathUnderTest.quadTo(0, 5, 0, 9);
		mPathUnderTest.lineTo(0, mCanvasBitmapUnderTest.getHeight());
		mCommandUnderTest = new PathCommand(mPaintUnderTest, mPathUnderTest);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
		mPathUnderTest.reset();
		mPathUnderTest = null;
	}

	@Test
	public void testPathOutOfBounds() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		Path path = new Path();

		float left = mCanvasBitmapUnderTest.getWidth() + 50;
		float top = mCanvasBitmapUnderTest.getHeight() + 50;
		float right = mCanvasBitmapUnderTest.getWidth() + 100;
		float bottom = mCanvasBitmapUnderTest.getHeight() + 100;
		path.addRect(new RectF(left, top, right, bottom), Path.Direction.CW);

		mCommandUnderTest = new PathCommand(mPaintUnderTest, path);

		CommandManagerMockup commandManagerMockup = new CommandManagerMockup();
		commandManagerMockup.testCommand(mCommandUnderTest);
		mCommandUnderTest.run(mCanvasUnderTest, null);

		assertEquals("Pathcommand should have failed but didnt get deleted", commandManagerMockup.gotDeleted, true);
	}

	// @Test
	// @Ignore("library test")
	// public void testRun() {
	// int color = mPaintUnderTest.getColor();
	// int height = mBitmapUnderTest.getHeight();
	//
	// for (int heightIndex = 0; heightIndex < height; heightIndex++) {
	// mBitmapUnderTest.setPixel(0, heightIndex, color);
	// }
	// mCommandUnderTest.run(mCanvasUnderTest, null);
	// PaintroidAsserts.assertBitmapEquals(mBitmapUnderTest, mCanvasBitmapUnderTest);
	// }

	private class CommandManagerMockup implements Observer {
		public boolean gotDeleted = false;

		public void testCommand(Command command) {
			((BaseCommand) command).addObserver(this);
		}

		@Override
		public void update(Observable observable, Object data) {
			if (data instanceof BaseCommand.NOTIFY_STATES) {
				if (BaseCommand.NOTIFY_STATES.COMMAND_FAILED == data) {
					if (observable instanceof Command) {
						gotDeleted = true;
					}
				}
			}
		}

	}
}
