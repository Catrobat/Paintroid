package org.catrobat.paintroid.test.integration;

import java.util.ArrayList;

import org.catrobat.paintroid.MainActivity;
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
		try {
			if ((Boolean) PrivateAccess.getMemberValue(MainActivity.class, getActivity(), "mOpenedFromCatroid")) {
				mNotActivatedTools = 0;
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		activateAllToolsInPreferences();
		super.tearDown();
	}

	@Test
	public void testIfToolsPreferenceScreenISAvailable() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertEquals("No Tools at startup", getToolsNames().length - mNotActivatedTools, currentNumberOfActiveTools());
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_preferences), true);
		mSolo.waitForText(mSolo.getString(R.string.preferences_tools), 1, TIMEOUT, true, true);
		mSolo.waitForText(mSolo.getString(R.string.preferences_tools_summary), 1, TIMEOUT, true, true);
		mSolo.clickOnText(mSolo.getString(R.string.preferences_tools), 1, true);
		final String[] allToolNames = getToolsNames();
		for (String toolName : allToolNames) {
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
			assertEquals("Number of active tools wrong " + allToolsNames[toolPreferenceIndex],
					(originalNumberOfActiveTools - 1), currentNumberOfActiveTools());

			mSolo.clickOnText(allToolsNames[toolPreferenceIndex], 1, true);
			assertTrue("Tool is still available in ToolAdapter: " + allToolsNames[toolPreferenceIndex],
					isToolInToolAdapter(allToolsNames[toolPreferenceIndex]));
			assertEquals("Number of active tools wrong " + allToolsNames[toolPreferenceIndex],
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
