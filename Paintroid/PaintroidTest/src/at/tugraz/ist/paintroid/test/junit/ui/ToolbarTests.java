///**
// *  Catroid: An on-device graphical programming language for Android devices
// *  Copyright (C) 2010-2011 The Catroid Team
// *  (<http://code.google.com/p/catroid/wiki/Credits>)
// *  
// *  Paintroid: An image manipulation application for Android, part of the
// *  Catroid project and Catroid suite of software.
// *  
// *  This program is free software: you can redistribute it and/or modify
// *  it under the terms of the GNU Affero General Public License as
// *  published by the Free Software Foundation, either version 3 of the
// *  License, or (at your option) any later version.
// *  
// *  An additional term exception under section 7 of the GNU Affero
// *  General Public License, version 3, is available at
// *  http://www.catroid.org/catroid_license_additional_term
// *  
// *  This program is distributed in the hope that it will be useful,
// *  but WITHOUT ANY WARRANTY; without even the implied warranty of
// *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *  GNU Affero General Public License for more details.
// *   
// *  You should have received a copy of the GNU Affero General Public License
// *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package at.tugraz.ist.paintroid.test.junit.ui;
//
//import java.util.Observable;
//
//import android.test.ActivityInstrumentationTestCase2;
//import at.tugraz.ist.paintroid.MainActivity;
//import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
//import at.tugraz.ist.paintroid.test.utils.TestObserver;
//import at.tugraz.ist.paintroid.test.utils.Utils;
//import at.tugraz.ist.paintroid.tools.Tool;
//import at.tugraz.ist.paintroid.tools.implementation.DrawTool;
//import at.tugraz.ist.paintroid.ui.Toolbar;
//
//public class ToolbarTests extends ActivityInstrumentationTestCase2<MainActivity> {
//
//	protected MainActivity activity;
//	protected Toolbar toolbar;
//
//	public ToolbarTests() {
//		super(MainActivity.class);
//	}
//
//	@Override
//	public void setUp() throws Exception {
//		super.setUp();
//
//		Utils.doWorkaroundSleepForDrawingSurfaceThreadProblem();
//
//		activity = this.getActivity();
//		toolbar = (Toolbar) PrivateAccess.getMemberValue(MainActivity.class, activity, "mToolbar");
//		((Observable) toolbar).deleteObservers();
//	}
//
//	public void testShouldChangeTool() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
//			IllegalAccessException {
//		Tool newTool = new DrawTool(this.getActivity(), Tool.ToolType.BRUSH);
//
//		toolbar.setTool(newTool);
//
//		Tool toolbarTool = toolbar.getCurrentTool();
//		assertSame(newTool, toolbarTool);
//	}
//
//	public void testShouldNotifyObserversOnToolChange() {
//		Tool tool = new DrawTool(this.getActivity(), Tool.ToolType.BRUSH);
//		TestObserver observer = new TestObserver();
//		((Observable) toolbar).addObserver(observer);
//
//		toolbar.setTool(tool);
//
//		assertEquals(1, observer.getCallCount("update"));
//		assertSame(toolbar, observer.getCall("update", 0).get(0));
//	}
// }
