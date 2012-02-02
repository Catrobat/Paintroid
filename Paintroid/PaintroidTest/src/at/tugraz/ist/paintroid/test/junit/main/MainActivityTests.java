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

package at.tugraz.ist.paintroid.test.junit.main;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.test.junit.stubs.ToolbarStub;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.implementation.DrawTool;

public class MainActivityTests extends ActivityInstrumentationTestCase2<MainActivity> {

	MainActivity mainActivity;
	ToolbarStub toolbarStub;

	public MainActivityTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		mainActivity = this.getActivity();
		toolbarStub = new ToolbarStub();
		PrivateAccess.setMemberValue(MainActivity.class, mainActivity, "toolbar", toolbarStub);
	}

	public void testShouldSetNewToolOnToolbar() {
		Intent data = new Intent();
		int brushIndex = -1;
		for (int index = 0; index < ToolType.values().length; index++) {
			if (ToolType.values()[index] == ToolType.BRUSH) {
				brushIndex = index;
				break;
			}
		}
		data.putExtra("SelectedTool", brushIndex);

		mainActivity.onActivityResult(MainActivity.TOOL_MENU, Activity.RESULT_OK, data);

		assertEquals(1, toolbarStub.getCallCount("setTool"));
		Tool tool = (Tool) toolbarStub.getCall("setTool", 0).get(0);
		assertTrue(tool instanceof DrawTool);
	}

	// figure out how to test this thing
	//
	// public void testShouldInitializeMemberFields() throws SecurityException,
	// IllegalArgumentException,
	// NoSuchFieldException, IllegalAccessException {
	// Bundle bundle = new Bundle();
	// mainActivity.onCreate(bundle);
	// SurfaceView drawingSurfaceView = (SurfaceView)
	// PrivateAccess.getMemberValue(MainActivity.class, mainActivity,
	// "drawingSurface");
	// assertNotNull(drawingSurfaceView);
	//
	// DrawingSurfacePerspective drawingSurfacePerspective = (DrawingSurfacePerspective)
	// PrivateAccess.getMemberValue(
	// MainActivity.class, mainActivity, "drawingSurfacePerspective");
	// assertNotNull(drawingSurfacePerspective);
	//
	// OnTouchListener drawingSurfaceListener = (OnTouchListener)
	// PrivateAccess.getMemberValue(MainActivity.class,
	// mainActivity, "drawingSurfaceListener");
	// assertNotNull(drawingSurfaceView);
	// }
}
