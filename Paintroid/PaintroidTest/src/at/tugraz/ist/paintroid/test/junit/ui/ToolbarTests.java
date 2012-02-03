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

import java.util.Observable;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.test.utils.TestObserver;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.implementation.DrawTool;
import at.tugraz.ist.paintroid.ui.Toolbar;

public class ToolbarTests extends ActivityInstrumentationTestCase2<MainActivity> {

	protected MainActivity activity;
	protected Toolbar toolbar;

	public ToolbarTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
		toolbar = (Toolbar) PrivateAccess.getMemberValue(MainActivity.class, activity, "toolbar");
	}

	public void testShouldChangeTool() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		Tool newTool = new DrawTool(this.getActivity());

		toolbar.setTool(newTool);

		Tool toolbarTool = toolbar.getCurrentTool();
		assertSame(newTool, toolbarTool);
	}

	public void testShouldNotifyObserversOnToolChange() {
		Tool tool = new DrawTool(this.getActivity());
		TestObserver observer = new TestObserver();
		((Observable) toolbar).addObserver(observer);

		toolbar.setTool(tool);

		assertEquals(1, observer.getCallCount("update"));
		assertSame(toolbar, observer.getCall("update", 0).get(0));
	}
}
