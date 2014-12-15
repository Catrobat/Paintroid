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

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.CommandManagerImplementation;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.test.ActivityInstrumentationTestCase2;

public class CommandManagerTest extends ActivityInstrumentationTestCase2<MainActivity> {

	MainActivity activity;

	public CommandManagerTest() {
		super(MainActivity.class);
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testCommandManagerOnFailedCommand() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		CommandManager commandManager = new CommandManagerImplementation();

		Command commandFailing = new MockCommand(true);
		Command commandOk = new MockCommand(false);

		commandManager.commitCommand(commandFailing);
		commandManager.commitCommand(commandOk);

		commandManager.getNextCommand();
		commandManager.getNextCommand();

		commandFailing.run(null, null);
		commandOk.run(null, null);

		int currentNumberOfCommands = (Integer) PrivateAccess.getMemberValue(CommandManagerImplementation.class,
				commandManager, "mCommandCounter");

		assertEquals("The wrong number of commands is in the CommandManager", 2, currentNumberOfCommands);

	}

	private class MockCommand extends BaseCommand {
		public boolean simulatesFail;

		public MockCommand(boolean simulatesFail) {
			this.simulatesFail = simulatesFail;
		}

		@Override
		public void run(Canvas canvas, Bitmap bitmap) {
			setChanged();
			if (simulatesFail) {
				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
			} else {
				notifyStatus(NOTIFY_STATES.COMMAND_DONE);
			}
		}

	}

}
