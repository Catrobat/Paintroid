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

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.test.junit.stubs.ObserverStub;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.DrawTool;
import org.catrobat.paintroid.ui.TopBar;

import java.util.Observable;

public class BottomBar extends ActivityInstrumentationTestCase2<MainActivity> {

	private static final String PRIVATE_ACCESS_BOTTOMBAR_NAME = "mBottomBar";

	protected MainActivity mActivity;
	protected BottomBar mBottomBar;

	public BottomBar() {
		super(MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
		mBottomBar = (BottomBar) PrivateAccess.getMemberValue(MainActivity.class, mActivity, PRIVATE_ACCESS_BOTTOMBAR_NAME);
	}

}
