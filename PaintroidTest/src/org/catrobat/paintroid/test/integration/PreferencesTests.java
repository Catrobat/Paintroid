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
package org.catrobat.paintroid.test.integration;

import java.util.ArrayList;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.ui.button.ToolButton;
import org.catrobat.paintroid.ui.button.ToolButtonAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferencesTests extends BaseIntegrationTestClass {
	private int mNotActivatedTools = 2;

	public PreferencesTests() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
		if (PaintroidApplication.IS_OPENED_FROM_CATROID == true) {
			mNotActivatedTools = 0;
		}

	}

	@Override
	@After
	protected void tearDown() throws Exception {
		activateAllToolsInPreferences();
		super.tearDown();
	}

	@Test
	public void testIfToolsPreferenceScreenIsAvailable() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertEquals("No Tools at startup", getToolsNames().length - mNotActivatedTools, currentNumberOfActiveTools());
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_preferences), true);
		mSolo.waitForText(mSolo.getString(R.string.preferences_tools), 1, TIMEOUT, true, true);
		mSolo.waitForText(mSolo.getString(R.string.preferences_tools_summary), 1, TIMEOUT, true, true);
		mSolo.clickOnText(mSolo.getString(R.string.preferences_tools), 1, true);
		final String[] allToolNames = getToolsNames();
		String toolName = "";
		for (int toolNameIndex = 0; toolNameIndex < allToolNames.length - mNotActivatedTools; toolNameIndex++) {
			toolName = allToolNames[toolNameIndex];
			assertTrue("Missing Tool :" + toolName, mSolo.searchText(toolName, 1, true, true));
		}
	}

	@Test
	public void testIfToolsFromCatroidAreSelectable() {
		PaintroidApplication.IS_OPENED_FROM_CATROID = true;
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_preferences), true);
		mSolo.waitForText(mSolo.getString(R.string.preferences_tools), 1, TIMEOUT, true, true);
		mSolo.waitForText(mSolo.getString(R.string.preferences_tools_summary), 1, TIMEOUT, true, true);
		mSolo.clickOnText(mSolo.getString(R.string.preferences_tools), 1, true);
		final String[] allToolNames = getToolsNames();
		String toolName = "";
		for (int toolNameIndex = allToolNames.length - mNotActivatedTools; toolNameIndex < allToolNames.length; toolNameIndex++) {
			toolName = allToolNames[toolNameIndex];
			assertTrue("Missing Tool :" + toolName, mSolo.searchText(toolName, 1, true, true));
		}
	}

	@Test
	public void testIfToolsDoNotAppearInToolsMenuIfPreferenceIsNotCheckedAndReactivateAgain() throws SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(getActivity());
		int originalNumberOfActiveTools = currentNumberOfActiveTools();
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_preferences), true);
		mSolo.clickOnText(mSolo.getString(R.string.preferences_tools), 1, true);
		mSolo.sleep(1000);
		final String[] allToolsNames = getToolsNames();
		for (int toolPreferenceIndex = 0; toolPreferenceIndex < allToolsNames.length - mNotActivatedTools; toolPreferenceIndex++) {
			if (preferenceManager.contains(allToolsNames[toolPreferenceIndex])) {
				if (preferenceManager.getBoolean(allToolsNames[toolPreferenceIndex], false)) {
					mSolo.clickOnText(allToolsNames[toolPreferenceIndex], 1, true);
				}
			}
			mSolo.sleep(200);
			assertFalse("Tool is still available in ToolAdapter: " + allToolsNames[toolPreferenceIndex],
					isToolInToolAdapter(allToolsNames[toolPreferenceIndex]));
			assertEquals("1. Number of active tools wrong " + allToolsNames[toolPreferenceIndex],
					(originalNumberOfActiveTools - 1), currentNumberOfActiveTools());

			mSolo.clickOnText(allToolsNames[toolPreferenceIndex], 1, true);
			mSolo.sleep(200);
			assertTrue("Tool is not available in ToolAdapter: " + allToolsNames[toolPreferenceIndex],
					isToolInToolAdapter(allToolsNames[toolPreferenceIndex]));
			assertEquals("2. Number of active tools wrong " + allToolsNames[toolPreferenceIndex],
					originalNumberOfActiveTools, currentNumberOfActiveTools());

		}
	}

	private boolean isToolInToolAdapter(String toolButtonString) throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		ToolButtonAdapter toolButtonAdapter = new ToolButtonAdapter(getActivity(), false);
		ArrayList<ToolButton> buttonsList = (ArrayList<ToolButton>) PrivateAccess.getMemberValue(
				ToolButtonAdapter.class, toolButtonAdapter, "mButtonsList");
		for (int toolIndex = 0; toolIndex < buttonsList.size(); toolIndex++) {
			if (mSolo.getString(buttonsList.get(toolIndex).stringId).equalsIgnoreCase(toolButtonString))
				return true;
		}
		return false;
	}

	private int currentNumberOfActiveTools() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		ToolButtonAdapter currentToolButtonAdapter = new ToolButtonAdapter(getActivity(), false);
		return currentToolButtonAdapter.getCount();
	}

	private String[] getToolsNames() {
		final String[] allTools = new String[] { mSolo.getString(R.string.button_brush),
				mSolo.getString(R.string.button_cursor), mSolo.getString(R.string.button_pipette),
				mSolo.getString(R.string.button_magic), mSolo.getString(R.string.button_stamp),
				mSolo.getString(R.string.button_import_image), mSolo.getString(R.string.button_crop),
				mSolo.getString(R.string.button_eraser), mSolo.getString(R.string.button_flip),
				mSolo.getString(R.string.button_undo), mSolo.getString(R.string.button_redo) };
		return allTools;
	}

	private void activateAllToolsInPreferences() {
		SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Editor editor = preferenceManager.edit();
		final String[] allToolsNames = getToolsNames();
		for (String toolName : allToolsNames) {
			editor.putBoolean(toolName, true);
		}
		editor.commit();
	}
}
