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

package org.catrobat.paintroid.test.junit.ui;

import java.util.Observable;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.test.junit.stubs.ObserverStub;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.DrawTool;
import org.catrobat.paintroid.ui.TopBar;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

public class StatusbarTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private static final String PRIVATE_ACCESS_STATUSBAR_NAME = "mTopBar";

	protected MainActivity mActivity;
	protected TopBar mToolbar;

	public StatusbarTests() {
		super(MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
		mToolbar = (TopBar) PrivateAccess.getMemberValue(MainActivity.class, mActivity, PRIVATE_ACCESS_STATUSBAR_NAME);
		((Observable) mToolbar).deleteObservers();
	}

	@UiThreadTest
	public void testShouldChangeTool() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		Tool newTool = new DrawTool(this.getActivity(), ToolType.BRUSH);

		mToolbar.setTool(newTool);

		Tool toolbarTool = mToolbar.getCurrentTool();
		assertSame(newTool.getToolType(), toolbarTool.getToolType());
	}

	@UiThreadTest
	public void testShouldNotifyObserversOnToolChange() {
		Tool tool = new DrawTool(this.getActivity(), ToolType.CURSOR);
		ObserverStub observer = new ObserverStub();
		((Observable) mToolbar).addObserver(observer);

		mToolbar.setTool(tool);

		assertEquals(1, observer.getCallCount("update"));
		assertSame(mToolbar, observer.getCall("update", 0).get(0));
	}

    @UiThreadTest
	public void testShouldNotNotifyIfSameToolIsRelselected() {
		Tool tool = new DrawTool(this.getActivity(), ToolType.BRUSH);
		ObserverStub observer = new ObserverStub();
		((Observable) mToolbar).addObserver(observer);

		mToolbar.setTool(tool);

		assertEquals(0, observer.getCallCount("update"));
	}
}
