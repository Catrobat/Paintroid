/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
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

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.junit.stubs.CommandManagerStub;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.ui.TopBar;
import org.junit.Test;

import android.test.ActivityInstrumentationTestCase2;

// TODO for redesign: check these test

public class StatusbarTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private static final String PRIVATE_ACCESS_STATUSBAR_NAME = "mTopBar";

	protected MainActivity mActivity;
	protected TopBar mToolbar;
	protected CommandManagerStub mCommandManagerStub;

	public StatusbarTests() {
		super(MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		mCommandManagerStub = new CommandManagerStub();
		mActivity = getActivity();
		mToolbar = (TopBar) PrivateAccess.getMemberValue(MainActivity.class, mActivity, PRIVATE_ACCESS_STATUSBAR_NAME);
		mToolbar.deleteObservers();
		PaintroidApplication.commandManager = mCommandManagerStub;
	}

	@Test
	public void testRedoShouldBeDisabled() {
		assertEquals(0, mCommandManagerStub.getCallCount("enableRedo"));
	}

	@Test
	public void testUndoShouldBeDisabled() {
		assertEquals(0, mCommandManagerStub.getCallCount("enableUndo"));
	}
}
