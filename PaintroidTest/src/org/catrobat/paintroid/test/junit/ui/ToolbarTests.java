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


package org.catrobat.paintroid.test.junit.ui;

import java.util.Observable;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.TestObserver;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.implementation.DrawTool;
import org.catrobat.paintroid.ui.Toolbar;

import android.test.ActivityInstrumentationTestCase2;

public class ToolbarTests extends ActivityInstrumentationTestCase2<MainActivity> {

	protected MainActivity activity;
	protected Toolbar toolbar;

	public ToolbarTests() {
		super(MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		Utils.doWorkaroundSleepForDrawingSurfaceThreadProblem();

		activity = this.getActivity();
		toolbar = (Toolbar) PrivateAccess.getMemberValue(MainActivity.class, activity, "mToolbar");
		((Observable) toolbar).deleteObservers();
	}

	public void testShouldChangeTool() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		Tool newTool = new DrawTool(this.getActivity(), Tool.ToolType.BRUSH);

		toolbar.setTool(newTool);

		Tool toolbarTool = toolbar.getCurrentTool();
		assertSame(newTool, toolbarTool);
	}

	public void testShouldNotifyObserversOnToolChange() {
		Tool tool = new DrawTool(this.getActivity(), Tool.ToolType.BRUSH);
		TestObserver observer = new TestObserver();
		((Observable) toolbar).addObserver(observer);

		toolbar.setTool(tool);

		assertEquals(1, observer.getCallCount("update"));
		assertSame(toolbar, observer.getCall("update", 0).get(0));
	}
}
