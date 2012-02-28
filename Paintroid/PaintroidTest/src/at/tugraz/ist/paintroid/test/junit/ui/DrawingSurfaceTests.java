/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test.junit.ui;

import junit.framework.TestCase;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.test.junit.stubs.CommandManagerStub;

public class DrawingSurfaceTests extends TestCase {
	private MainActivity mainActivity;

	@Override
	public void setUp() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		mainActivity = new MainActivity();
		mainActivity.onCreate(null);
		PaintroidApplication.COMMAND_HANDLER = new CommandManagerStub();
	}

	// public void testShouldDoStuff() throws SecurityException, IllegalArgumentException,
	// NoSuchFieldException,
	// IllegalAccessException {
	// DrawingSurface drawingSurface = (DrawingSurface)
	// PrivateAccess.getMemberValue(MainActivity.class, mainActivity,
	// "drawingSurface");
	// assertNotNull(drawingSurface);
	// }
}
