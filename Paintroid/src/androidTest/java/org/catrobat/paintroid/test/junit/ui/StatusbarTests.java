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

import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.junit.stubs.CommandManagerStub;
import org.catrobat.paintroid.ui.TopBar;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

// TODO for redesign: check these test
public class StatusbarTests {

	private CommandManagerStub commandManagerStub;

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	public StatusbarTests() {
	}

	@UiThreadTest
	@Before
	public void setUp() {
		commandManagerStub = new CommandManagerStub();
		MainActivity activity = activityTestRule.getActivity();
		TopBar topBar = activity.topBar;
		topBar.deleteObservers();
		PaintroidApplication.commandManager = commandManagerStub;
	}

	@UiThreadTest
	@Test
	public void testRedoShouldBeDisabled() {
		assertEquals(0, commandManagerStub.getCallCount("enableRedo"));
	}

	@UiThreadTest
	@Test
	public void testUndoShouldBeDisabled() {
		assertEquals(0, commandManagerStub.getCallCount("enableUndo"));
	}
}
