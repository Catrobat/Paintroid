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

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;

import com.jayway.android.robotium.solo.Solo;

public class LanguageTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;
	private TextView toolbarMainButton;

	private static final String ENGLISH = "Cancel";
	private static final String FRENCH = "Annuler";
	private static final String GERMAN = "Abbrechen";

	//	private static final String TURKISH = "Iptal";

	public LanguageTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();
		Utils.setLocale(solo, Locale.ENGLISH);
		toolbarMainButton = (TextView) mainActivity.findViewById(R.id.btn_Tool);
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

	public void openFileManager() {
		solo.clickOnView(toolbarMainButton);
		solo.waitForActivity("MenuTabActivity", 1000);
		solo.clickOnText("File"); // TODO: should be in resources
		solo.waitForActivity("FileActivity", 1000);
	}

	public void testEnglish() {
		assertEquals(Locale.ENGLISH, Locale.getDefault());
		openFileManager();
		solo.clickOnButton(ENGLISH);
	}

	public void testFrench() {
		Utils.setLocale(solo, Locale.FRENCH);
		assertEquals(Locale.FRENCH, Locale.getDefault());

		openFileManager();
		solo.clickOnButton(FRENCH);
	}

	public void testGerman() {
		Utils.setLocale(solo, Locale.GERMAN);
		assertEquals(Locale.GERMAN, Locale.getDefault());

		openFileManager();
		solo.clickOnButton(GERMAN);
	}

	//	public void testTurkish() {
	//		Utils.setLocale(solo, Locale.TURKISH);
	//		assertEquals(Locale.TURKISH, Locale.getDefault());
	//
	//		openFileManager();
	//		solo.clickOnButton(TURKISH);
	//	}
}
