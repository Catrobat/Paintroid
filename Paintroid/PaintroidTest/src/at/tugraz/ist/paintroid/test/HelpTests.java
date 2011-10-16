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

package at.tugraz.ist.paintroid.test;

import java.util.Locale;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.GridView;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;

import com.jayway.android.robotium.solo.Solo;

public class HelpTests extends ActivityInstrumentationTestCase2<MainActivity> {
	private Solo solo;
	private MainActivity mainActivity;

	private TextView toolbarMainButton;
	private TextView toolbarButton1;
	private TextView toolbarButton2;

	public HelpTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();
		Utils.setLocale(solo, Locale.ENGLISH);

		toolbarMainButton = (TextView) mainActivity.findViewById(R.id.btn_Tool);
		toolbarButton1 = (TextView) mainActivity.findViewById(R.id.btn_Parameter1);
		toolbarButton2 = (TextView) mainActivity.findViewById(R.id.btn_Parameter2);
	}

	public void testHelpBrush() {
		solo.clickOnView(toolbarMainButton);
		solo.waitForView(GridView.class, 1, 2000);
		solo.clickLongOnText(mainActivity.getString(R.string.button_brush));
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickOnText(mainActivity.getString(R.string.button_brush));
		solo.waitForActivity("MainActivity", 2000);

		solo.clickLongOnView(toolbarMainButton, 1000);
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickLongOnView(toolbarButton1, 1000);
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickLongOnView(toolbarButton2, 1000);
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
	}

	public void testHelpHand() {
		solo.clickOnView(toolbarMainButton);
		solo.waitForView(GridView.class, 1, 2000);
		solo.clickLongOnText(mainActivity.getString(R.string.button_choose));
		// TODO: add help text
		//		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickOnText(mainActivity.getString(R.string.button_choose));
		solo.waitForActivity("MainActivity", 2000);

		solo.clickLongOnView(toolbarMainButton, 1000);
		// TODO: add help text
		//		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
	}

	public void testHelpCursor() {
		solo.clickOnView(toolbarMainButton);
		solo.waitForView(GridView.class, 1, 2000);
		solo.clickLongOnText(mainActivity.getString(R.string.button_cursor));
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickOnText(mainActivity.getString(R.string.button_cursor));
		solo.waitForActivity("MainActivity", 2000);

		solo.clickLongOnView(toolbarMainButton, 1000);
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickLongOnView(toolbarButton1, 1000);
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickLongOnView(toolbarButton2, 1000);
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
	}

	public void testHelpStamp() {
		solo.clickOnView(toolbarMainButton);
		solo.waitForView(GridView.class, 1, 2000);
		solo.clickLongOnText(mainActivity.getString(R.string.button_floating_box));
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickOnText(mainActivity.getString(R.string.button_floating_box));
		solo.waitForActivity("MainActivity", 2000);

		solo.clickLongOnView(toolbarMainButton, 1000);
		// TODO: add help text
		//		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickLongOnView(toolbarButton1, 1000);
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickLongOnView(toolbarButton2, 1000);
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
	}

	public void testHelpImportPng() {
		solo.clickOnView(toolbarMainButton);
		solo.waitForView(GridView.class, 1, 2000);
		solo.clickLongOnText(mainActivity.getString(R.string.button_import_png));
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
	}

	public void testHelpMagic() {
		solo.clickOnView(toolbarMainButton);
		solo.waitForView(GridView.class, 1, 2000);
		solo.clickLongOnText(mainActivity.getString(R.string.button_magic));
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickOnText(mainActivity.getString(R.string.button_magic));
		solo.waitForActivity("MainActivity", 2000);

		solo.clickLongOnView(toolbarMainButton, 1000);
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickLongOnView(toolbarButton1, 1000);
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
	}

	public void testHelpEyedropper() {
		solo.clickOnView(toolbarMainButton);
		solo.waitForView(GridView.class, 1, 2000);
		solo.clickLongOnText(mainActivity.getString(R.string.button_pipette));
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickOnText(mainActivity.getString(R.string.button_pipette));
		solo.waitForActivity("MainActivity", 2000);

		solo.clickLongOnView(toolbarMainButton, 1000);
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickLongOnView(toolbarButton1, 1000);
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
	}

	public void testHelpRedo() {
		solo.clickOnView(toolbarMainButton);
		solo.waitForView(GridView.class, 1, 2000);
		solo.clickLongOnText(mainActivity.getString(R.string.button_redo));
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
	}

	public void testHelpUndo() {
		solo.clickOnView(toolbarMainButton);
		solo.waitForView(GridView.class, 1, 2000);
		solo.clickLongOnText(mainActivity.getString(R.string.button_undo));
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
	}

	public void testHelpZoom() {
		solo.clickOnView(toolbarMainButton);
		solo.waitForView(GridView.class, 1, 2000);
		solo.clickLongOnText(mainActivity.getString(R.string.button_zoom));
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickOnText(mainActivity.getString(R.string.button_zoom));
		solo.waitForActivity("MainActivity", 2000);

		solo.clickLongOnView(toolbarMainButton, 1000);
		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
		solo.clickLongOnView(toolbarButton1, 1000);
		// TODO: add help text
		//		Assert.assertNotSame("", solo.getText(1).getText().toString());
		solo.clickOnButton(0);
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {

			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}
}
