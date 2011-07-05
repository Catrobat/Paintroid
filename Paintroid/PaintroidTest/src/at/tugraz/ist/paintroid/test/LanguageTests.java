/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test;

import java.util.Locale;

import android.content.res.Configuration;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import at.tugraz.ist.paintroid.MainActivity;

import com.jayway.android.robotium.solo.Solo;

public class LanguageTests extends ActivityInstrumentationTestCase2<MainActivity> {

	public LanguageTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());

		String languageToLoad_before = "de";
		Locale locale_before = new Locale(languageToLoad_before);
		Locale.setDefault(locale_before);

		Configuration config_before = new Configuration();
		config_before.locale = locale_before;

		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources()
				.updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());

		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();
	}

	final int FILE = 8;

	private Solo solo;
	private MainActivity mainActivity;

	public void testEnglish() {
		Log.d("PaintroidTest", "Current language: " + Locale.getDefault().getDisplayLanguage());

		assertEquals("Deutsch", Locale.getDefault().getDisplayLanguage());

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Abbrechen");

		String languageToLoad_after = "en";
		Locale locale_after = new Locale(languageToLoad_after);
		Locale.setDefault(locale_after);

		Configuration config_after = new Configuration();
		config_after.locale = locale_after;

		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources()
				.updateConfiguration(config_after, mainActivity.getBaseContext().getResources().getDisplayMetrics());

		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();

		Log.d("PaintroidTest", "Current language: " + Locale.getDefault().getDisplayLanguage());

		assertEquals("English", Locale.getDefault().getDisplayLanguage());

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Cancel");

	}

	public void testFrench() {
		Log.d("PaintroidTest", "Current language: " + Locale.getDefault().getDisplayLanguage());

		assertEquals("Deutsch", Locale.getDefault().getDisplayLanguage());

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Abbrechen");

		String languageToLoad_after = "fr";
		Locale locale_after = new Locale(languageToLoad_after);
		Locale.setDefault(locale_after);

		Configuration config_after = new Configuration();
		config_after.locale = locale_after;

		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources()
				.updateConfiguration(config_after, mainActivity.getBaseContext().getResources().getDisplayMetrics());

		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();

		Log.d("PaintroidTest", "Current language: " + Locale.getDefault().getDisplayLanguage().substring(0, 2));

		assertEquals("fr", Locale.getDefault().getDisplayLanguage().substring(0, 2));

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Annuler");

	}

	public void testGerman() {
		String languageToLoad_before = "fr";
		Locale locale_before = new Locale(languageToLoad_before);
		Locale.setDefault(locale_before);

		Configuration config_before = new Configuration();
		config_before.locale = locale_before;

		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources()
				.updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());

		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();

		Log.d("PaintroidTest", "Current language: " + Locale.getDefault().getDisplayLanguage().substring(0, 2));

		assertEquals("fr", Locale.getDefault().getDisplayLanguage().substring(0, 2));

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Annuler");

		String languageToLoad_after = "de";
		Locale locale_after = new Locale(languageToLoad_after);
		Locale.setDefault(locale_after);

		Configuration config_after = new Configuration();
		config_after.locale = locale_after;

		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources()
				.updateConfiguration(config_after, mainActivity.getBaseContext().getResources().getDisplayMetrics());

		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();

		Log.d("PaintroidTest", "Current language: " + Locale.getDefault().getDisplayLanguage());

		assertEquals("Deutsch", Locale.getDefault().getDisplayLanguage());
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Abbrechen");

	}

	@Override
	public void tearDown() throws Exception {
		String languageToLoad_before = "en";
		Locale locale_before = new Locale(languageToLoad_before);
		Locale.setDefault(locale_before);

		Configuration config_before = new Configuration();
		config_before.locale = locale_before;

		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources()
				.updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());

		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();

		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

}
