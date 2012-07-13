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
package at.tugraz.ist.paintroid.test.integration;

import org.junit.After;
import org.junit.Before;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;

import com.jayway.android.robotium.solo.Solo;

public class BaseIntegrationTestClass extends ActivityInstrumentationTestCase2<MainActivity> {

	protected Solo mSolo;
	protected TextView mToolBarButtonMain;
	protected TextView mToolBarButtonOne;
	protected TextView mToolBarButtonTwo;
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected final int TIMEOUT = 2000;
	protected MainActivity mMainActivity;
	protected final int VERSION_HONEYCOMB = 11;

	public BaseIntegrationTestClass() throws Exception {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();

		mSolo = new Solo(getInstrumentation(), getActivity());
		mMainActivity = (MainActivity) mSolo.getCurrentActivity();
		mToolBarButtonMain = (TextView) getActivity().findViewById(R.id.btn_Tool);
		mToolBarButtonOne = (TextView) getActivity().findViewById(R.id.btn_Parameter1);
		mToolBarButtonTwo = (TextView) getActivity().findViewById(R.id.btn_Parameter2);
		mScreenWidth = mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		mScreenHeight = mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		mSolo.finishOpenedActivities();
		mSolo = null;
		mMainActivity.finish();
		mMainActivity = null;
		mToolBarButtonMain = null;
		mToolBarButtonOne = null;
		mToolBarButtonTwo = null;
		super.tearDown();
		System.gc();
	}

}
