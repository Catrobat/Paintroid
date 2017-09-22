///**
// *  Paintroid: An image manipulation application for Android.
// *  Copyright (C) 2010-2015 The Catrobat Team
// *  (<http://developer.catrobat.org/credits>)
// *
// *  This program is free software: you can redistribute it and/or modify
// *  it under the terms of the GNU Affero General Public License as
// *  published by the Free Software Foundation, either version 3 of the
// *  License, or (at your option) any later version.
// *
// *  This program is distributed in the hope that it will be useful,
// *  but WITHOUT ANY WARRANTY; without even the implied warranty of
// *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// *  GNU Affero General Public License for more details.
// *
// *  You should have received a copy of the GNU Affero General Public License
// *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package org.catrobat.paintroid.test.junit.main;
//
//import org.catrobat.paintroid.MainActivity;
//import org.catrobat.paintroid.test.junit.stubs.TopBarStub;
//import org.catrobat.paintroid.test.utils.PrivateAccess;
//
//import android.test.ActivityInstrumentationTestCase2;
//
//public class MainActivityTests extends ActivityInstrumentationTestCase2<MainActivity> {
//
//	private static final String PRIVATE_ACCESS_STATUSBAR_NAME = "mTopBar";
//
//	MainActivity mainActivity;
//	TopBarStub statusbarStub;
//
//	public MainActivityTests() {
//		super(MainActivity.class);
//	}
//
//	@Override
//	public void setUp() throws Exception {
//		mainActivity = this.getActivity();
//		statusbarStub = new TopBarStub(mainActivity, false);
//		PrivateAccess.setMemberValue(MainActivity.class, mainActivity, PRIVATE_ACCESS_STATUSBAR_NAME, statusbarStub);
//	}
//
//	// status bar is already tested
//	// @UiThreadTest
//	// public void testShouldSetNewToolOnToolbar() throws SecurityException, IllegalArgumentException,
//	// NoSuchFieldException, IllegalAccessException {
//	// Intent data = new Intent();
//	// int brushIndex = -1;
//	// for (int index = 0; index < ToolType.values().length; index++) {
//	// if (ToolType.values()[index] == ToolType.BRUSH) {
//	// brushIndex = index;
//	// break;
//	// }
//	// }
//	// data.putExtra("EXTRA_SELECTED_TOOL", brushIndex);
//	//
//	// int reqToolsDialogCode = (Integer) PrivateAccess.getMemberValue(MenuFileActivity.class, mainActivity,
//	// "REQ_TOOLS_DIALOG");
//	// mainActivity.onActivityResult(reqToolsDialogCode, Activity.RESULT_OK, data);
//	//
//	// assertEquals(1, statusbarStub.getCallCount("setTool"));
//	// Tool tool = (Tool) statusbarStub.getCall("setTool", 0).get(0);
//	// assertTrue(tool instanceof DrawTool);
//	// }
//
//	// figure out how to test this thing
//	//
//	// public void testShouldInitializeMemberFields() throws SecurityException,
//	// IllegalArgumentException,
//	// NoSuchFieldException, IllegalAccessException {
//	// Bundle bundle = new Bundle();
//	// mainActivity.onCreate(bundle);
//	// SurfaceView drawingSurfaceView = (SurfaceView)
//	// PrivateAccess.getMemberValue(MainActivity.class, mainActivity,
//	// "drawingSurface");
//	// assertNotNull(drawingSurfaceView);
//	//
//	// DrawingSurfacePerspective drawingSurfacePerspective = (DrawingSurfacePerspective)
//	// PrivateAccess.getMemberValue(
//	// MainActivity.class, mainActivity, "drawingSurfacePerspective");
//	// assertNotNull(drawingSurfacePerspective);
//	//
//	// OnTouchListener drawingSurfaceListener = (OnTouchListener)
//	// PrivateAccess.getMemberValue(MainActivity.class,
//	// mainActivity, "drawingSurfaceListener");
//	// assertNotNull(drawingSurfaceView);
//	// }
// }
