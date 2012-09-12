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


package org.catrobat.paintroid.test.junit.ui.button;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.test.junit.stubs.AttributeButtonStubbingAndroidFunctions;
import org.catrobat.paintroid.test.junit.stubs.ToolStub;
import org.catrobat.paintroid.test.junit.stubs.ToolbarStub;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.ui.button.ToolbarButton;

import android.test.ActivityInstrumentationTestCase2;

public class AttributeButtonTests extends ActivityInstrumentationTestCase2<MainActivity> {

	protected MainActivity activity;
	protected AttributeButtonStubbingAndroidFunctions attributeButton;
	protected ToolbarStub toolbarStub;
	protected ToolStub toolStub;

	public AttributeButtonTests() {
		super(MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		Utils.doWorkaroundSleepForDrawingSurfaceThreadProblem();

		activity = this.getActivity();
		attributeButton = new AttributeButtonStubbingAndroidFunctions(activity);
		toolbarStub = new ToolbarStub();
		toolStub = new ToolStub();
		toolbarStub.setReturnValue("getCurrentTool", toolStub);
	}

	public void testSetToolbarShouldAddObservableToTool() {
		attributeButton.setToolbar(toolbarStub);

		assertEquals(1, toolStub.getCallCount("addObserver"));
		assertSame(attributeButton, toolStub.getCall("addObserver", 0).get(0));
	}

	public void testSetToolbarShouldAddObservableToToolbar() {
		attributeButton.setToolbar(toolbarStub);

		assertEquals(1, toolbarStub.getCallCount("addObserver"));
		assertSame(attributeButton, toolbarStub.getCall("addObserver", 0).get(0));
	}

	public void testSetToolbarShouldSetTheBackgroundResourceProvidedByTool() {
		toolStub.setReturnValue("getAttributeButtonResource", 15);

		attributeButton.setToolbar(toolbarStub);

		assertEquals(1, attributeButton.getCallCount("setBackgroundResource"));
		assertEquals(0, attributeButton.getCallCount("setBackgroundColor"));
		assertSame(15, attributeButton.getCall("setBackgroundResource", 0).get(0));
	}

	// FIXME
	// public void testSetToolbarShouldSetBackgroundColorIfNoBackgroundResourceProvidedByTool() {
	// toolStub.setReturnValue("getAttributeButtonColor", 13);
	//
	// attributeButton.setToolbar(toolbarStub);
	//
	// assertEquals(1, attributeButton.getCallCount("setBackgroundColor"));
	// assertEquals(0, attributeButton.getCallCount("setBackgroundResource"));
	// assertSame(13, attributeButton.getCall("setBackgroundColor", 0).get(0));
	// }

	public void testShouldAddObservableIfToolbarHasNewTool() {
		attributeButton.setToolbar(toolbarStub);
		ToolStub newTool = new ToolStub();
		toolbarStub.setReturnValue("getCurrentTool", newTool);

		attributeButton.update(toolbarStub, null);

		assertEquals(1, newTool.getCallCount("addObserver"));
		assertSame(attributeButton, newTool.getCall("addObserver", 0).get(0));
	}

	public void testShouldSetNewResourceOnUpdate() {
		toolStub.setReturnValue("getAttributeButtonResource", 15);
		attributeButton.setToolbar(toolbarStub);
		toolStub.setReturnValue("getAttributeButtonResource", 14);

		attributeButton.update(toolbarStub, null);

		assertEquals(2, attributeButton.getCallCount("setBackgroundResource"));
		assertEquals(0, attributeButton.getCallCount("setBackgroundColor"));
		assertSame(14, attributeButton.getCall("setBackgroundResource", 1).get(0));
	}

	// FIXME
	// public void testSetToolbarShouldSetBackgroundColorIfNoBackgroundResourceProvidedByToolOnUpdate() {
	// attributeButton.setToolbar(toolbarStub);
	// toolStub.setReturnValue("getAttributeButtonColor", 13);
	//
	// attributeButton.update(toolbarStub, null);
	//
	// assertEquals(2, attributeButton.getCallCount("setBackgroundColor"));
	// assertEquals(0, attributeButton.getCallCount("setBackgroundResource"));
	// assertSame(13, attributeButton.getCall("setBackgroundColor", 1).get(0));
	// }

	public void testShouldDelegateClickEventsToToolWithCorrectButtonNumber() throws SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		PrivateAccess.setMemberValue(ToolbarButton.class, attributeButton, "mButtonNumber",
				ToolbarButton.ToolButtonIDs.BUTTON_ID_OTHER);
		attributeButton.setToolbar(toolbarStub);

		attributeButton.onClick(attributeButton);

		assertEquals(1, toolStub.getCallCount("attributeButtonClick"));
		assertSame(ToolbarButton.ToolButtonIDs.BUTTON_ID_OTHER, toolStub.getCall("attributeButtonClick", 0).get(0));
	}

	public void testShouldPassCorrectButtonNumberToGetAttributeButtonResourcer() throws SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		PrivateAccess.setMemberValue(ToolbarButton.class, attributeButton, "mButtonNumber",
				ToolbarButton.ToolButtonIDs.BUTTON_ID_OTHER);
		attributeButton.setToolbar(toolbarStub);

		assertEquals(1, toolStub.getCallCount("getAttributeButtonResource"));
		assertSame(ToolbarButton.ToolButtonIDs.BUTTON_ID_OTHER, toolStub.getCall("getAttributeButtonResource", 0)
				.get(0));
	}
	// FIXME
	// public void testShouldPassCorrectButtonNumberTogetAttributeButtonColor() throws SecurityException,
	// IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
	// PrivateAccess.setMemberValue(ToolbarButton.class, attributeButton, "mButtonNumber",
	// ToolbarButton.ToolButtonIDs.BUTTON_ID_OTHER);
	// attributeButton.setToolbar(toolbarStub);
	//
	// assertEquals(1, toolStub.getCallCount("getAttributeButtonColor"));
	// assertSame(ToolbarButton.ToolButtonIDs.BUTTON_ID_OTHER, toolStub.getCall("getAttributeButtonColor", 0).get(0));
	// }
}
