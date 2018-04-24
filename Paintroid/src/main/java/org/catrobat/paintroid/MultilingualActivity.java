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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MultilingualActivity extends AppCompatActivity {
	@VisibleForTesting
	public static final String LANGUAGE_TAG_KEY = "applicationLanguage";
	@VisibleForTesting
	public static final String SHARED_PREFERENCES_TAG = "For_language";
	private static final String[] LANGUAGE_CODE = {"az", "bg", "bs", "ca", "cs", "sr-CS", "sr-SP",
			"da", "de", "el", "en-AU", "en-CA", "en-GB", "en", "es", "fr", "gl", "hr", "in", "it",
			"sw", "hu", "lt", "mk", "ms", "nl", "no", "pl", "pt-BR", "pt", "ru", "ro", "sq", "sl",
			"sk", "sv", "vi", "tr", "ml", "ta", "te", "th", "gu", "hi", "ja", "ko", "zh-CN",
			"zh-TW", "ar", "ur", "fa", "ps", "sd", "iw"};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setToChosenLanguage(this);

		setContentView(R.layout.activity_multilingual);
		setTitle(R.string.menu_language);
		ListView listview = (ListView) findViewById(R.id.list_languages);
		final List<String> languagesNames = new ArrayList<>();
		languagesNames.add(getResources().getString(R.string.device_language));
		for (String aLanguageCode : LANGUAGE_CODE) {
			Locale locale = getLocaleFromLanguageTag(aLanguageCode);
			languagesNames.add(getDisplayName(locale));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.multilingual_name_text, R.id.lang_text, languagesNames);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String localeString = getLocaleStringFromPosition(position);
				setLanguageSharedPreference(localeString);
				setResult(RESULT_OK);
				finish();
			}
		});
	}

	private String getDisplayName(Locale locale) {
		String language = locale.getLanguage();
		// the output text of 'locale.getDisplayName(locale)' for "sd" is "Sindhi" which is wrong
		// the correct name of the sindhi language in Sindhi is "سنڌي"
		if (language.equals("sd")) {
			return "سنڌي";
		} else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
				&& language.equals("ps")) {
			return "پښتو";
		} else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
				&& language.equals("ur")) {
			return "اردو";
		}
		return locale.getDisplayName(locale);
	}

	public static void setToChosenLanguage(Activity activity) {
		SharedPreferences sharedPreferences = activity
				.getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
		String languageTag = sharedPreferences.getString(LANGUAGE_TAG_KEY, "");
		Locale locale = Arrays.asList(LANGUAGE_CODE).contains(languageTag)
				? getLocaleFromLanguageTag(languageTag)
				: new Locale(PaintroidApplication.defaultSystemLanguage);

		Locale.setDefault(locale);
		setLocale(activity, locale);
		setLocale(activity.getApplicationContext(), locale);
	}

	private static void setLocale(Context context, Locale locale) {
		Resources resources = context.getResources();
		DisplayMetrics displayMetrics = resources.getDisplayMetrics();
		Configuration conf = resources.getConfiguration();
		conf.setLocale(locale);
		resources.updateConfiguration(conf, displayMetrics);
	}

	private static Locale getLocaleFromLanguageTag(String languageTag) {
		if (languageTag.contains("-")) {
			String[] tags = languageTag.split("-");
			return new Locale(tags[0], tags[1]);
		}
		return new Locale(languageTag);
	}

	private String getLocaleStringFromPosition(int position) {
		return position > 0 ? LANGUAGE_CODE[position - 1] : null;
	}

	private void setLanguageSharedPreference(String value) {
		SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if (value == null) {
			editor.remove(LANGUAGE_TAG_KEY);
		} else {
			editor.putString(LANGUAGE_TAG_KEY, value);
		}
		editor.apply();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
		super.onBackPressed();
	}
}
