/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
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

/* EXCLUDE PREFERENCES FOR RELEASE 
 package org.catrobat.paintroid.preferences;

 import java.util.Locale;

 import org.catrobat.paintroid.PaintroidApplication;
 import org.catrobat.paintroid.R;

 import android.content.Context;
 import android.os.Bundle;
 import android.preference.ListPreference;
 import android.preference.Preference;
 import android.preference.Preference.OnPreferenceChangeListener;
 import android.preference.PreferenceActivity;
 import android.widget.Toast;

 public class SettingsActivity extends PreferenceActivity {

 private static final Locale[] availableLocales = { Locale.ENGLISH,
 Locale.GERMAN, Locale.FRANCE, new Locale("tr") };

 private Context mContext;

 @Override
 public void onCreate(Bundle savedInstanceState) {
 super.onCreate(savedInstanceState);
 mContext = this;
 String action = getIntent().getAction();
 if (action != null
 && action.equals(PaintroidApplication.applicationContext
 .getString(R.string.preferences_tools))) {
 addPreferencesFromResource(R.xml.preferences_tools);
 } else {
 addPreferencesFromResource(R.xml.preferences_legacy);
 initLanguagePreferences();

 }
 }

 private void initLanguagePreferences() {
 String[] localeStrings = new String[availableLocales.length];
 String[] localeValues = new String[availableLocales.length];
 for (int index = 0; index < availableLocales.length; index++) {
 localeStrings[index] = availableLocales[index]
 .getDisplayName(availableLocales[index]);
 localeValues[index] = availableLocales[index].getLanguage();
 }

 ListPreference preference = (ListPreference) findPreference(getString(R.string.preferences_language_key));
 preference.setEntries(localeStrings);
 preference.setEntryValues(localeValues);
 preference.setDefaultValue(getBaseContext().getResources()
 .getConfiguration().locale.getLanguage());
 preference
 .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

 @Override
 public boolean onPreferenceChange(Preference preference,
 Object newValue) {
 Toast.makeText(
 mContext,
 R.string.preferences_language_dialog_reboot_warning_text,
 Toast.LENGTH_LONG).show();

 return true;
 }
 });

 }

 }
 */
