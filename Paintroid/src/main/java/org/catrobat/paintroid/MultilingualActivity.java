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

package org.catrobat.paintroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.catrobat.paintroid.PaintroidApplication.defaultSystemLanguage;
import static org.catrobat.paintroid.PaintroidApplication.languageSharedPreferences;


public class MultilingualActivity extends AppCompatActivity {
	public static final String LANGUAGE_TAG_KEY = "applicationLanguage";
	public static final String[] LANGUAGE_CODE = {"az", "bs", "ca", "cs", "sr-rCS", "sr-rSP", "da", "de", "en-rAU", "en-rCA",
			"en-rGB", "en", "es", "fr", "gl", "hr", "in", "it", "sw-rKE", "hu", "mk", "ms", "nl", "no", "pl", "pt-rBR", "pt", "ru",
			"ro", "sq", "sl", "sk", "sv", "vi", "tr", "ml", "ta", "te", "th", "gu", "hi", "ja", "ko", "zh-rCN", "zh-rTW", "ar",
			"ur", "fa", "ps", "sd", "iw"};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multilingual);
		setTitle(R.string.menu_language);
		ListView listview = (ListView) findViewById(R.id.list_languages);
		final List<String> languagesNames = new ArrayList<>();
		languagesNames.add(getResources().getString(R.string.device_language));
		for (String aLanguageCode : LANGUAGE_CODE) {
			if (aLanguageCode.length() == 2 && !aLanguageCode.equals("sd")) {
				languagesNames.add(new Locale(aLanguageCode).getDisplayName(new Locale(aLanguageCode)));
				// the output text of' new Locale("sd").getDisplayName(new Locale("sd")));' is "Sindhi" which is wrong
				// the correct name of the sindhi language in Sindhi is "سنڌي"
			} else if (aLanguageCode.length() == 2 && aLanguageCode.equals("sd")) {
				languagesNames.add("سنڌي");
			} else {
				String language = aLanguageCode.substring(0, 2);
				String country = aLanguageCode.substring(4);
				languagesNames.add(new Locale(language, country).getDisplayName(new Locale(language, country)));
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.multilingual_name_text, R.id.lang_text, languagesNames);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (position == 0) {
					setLanguageSharedPreference(null);
					setNewLocale(defaultSystemLanguage, null);
				} else if (LANGUAGE_CODE[position - 1].length() == 2) {
					setLanguageSharedPreference(LANGUAGE_CODE[position - 1]);
					setNewLocale(LANGUAGE_CODE[position - 1], null);
				} else if (LANGUAGE_CODE[position - 1].length() == 6) {
					setLanguageSharedPreference(LANGUAGE_CODE[position - 1]);
					String language = LANGUAGE_CODE[position - 1].substring(0, 2);
					String country = LANGUAGE_CODE[position - 1].substring(4);
					setNewLocale(language, country);
				}
			}
		});
	}

	public static void updateLocale(Context context, String languageTag, String countryTag) {
		Locale mLocale;
		if (countryTag == null) {
			mLocale = new Locale(languageTag);
		} else {
			mLocale = new Locale(languageTag, countryTag);
		}
		Resources resources = context.getResources();
		DisplayMetrics displayMetrics = resources.getDisplayMetrics();
		Configuration conf = resources.getConfiguration();
		conf.setLocale(mLocale);
		Locale.setDefault(mLocale);
		conf.setLayoutDirection(mLocale);
		resources.updateConfiguration(conf, displayMetrics);
	}

	private void setNewLocale(String languageTag, String countryTag) {
		updateLocale(this, languageTag, countryTag);
		setResult(RESULT_OK);
		finish();
	}

	private void setLanguageSharedPreference(String value) {
		SharedPreferences.Editor editor = languageSharedPreferences.edit();
		if (value == null) {
			editor.remove(LANGUAGE_TAG_KEY);
		} else {
			editor.putString(LANGUAGE_TAG_KEY, value);
		}
		editor.commit();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
		super.onBackPressed();
	}
}
