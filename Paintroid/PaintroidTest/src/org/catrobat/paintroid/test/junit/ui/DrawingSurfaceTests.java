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

package org.catrobat.paintroid.test.junit.ui;

import junit.framework.TestCase;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.junit.stubs.CommandManagerStub;

public class DrawingSurfaceTests extends TestCase {
	private MainActivity mainActivity;

	@Override
	public void setUp() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		mainActivity = new MainActivity();
		mainActivity.onCreate(null);
		PaintroidApplication.COMMAND_MANAGER = new CommandManagerStub();
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
