package org.catrobat.paintroid;

import android.content.Context;
import android.content.Intent;
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
                    setNewLocale(defaultSystemLanguage, null);
                } else if (LANGUAGE_CODE[position - 1].length() == 2) {
                    setNewLocale(LANGUAGE_CODE[position - 1], null);
                    setLanguageSharedPreference(LANGUAGE_CODE[position - 1]);
                } else if (LANGUAGE_CODE[position - 1].length() == 6) {
                    String language = LANGUAGE_CODE[position - 1].substring(0, 2);
                    String country = LANGUAGE_CODE[position - 1].substring(4);
                    setNewLocale(language, country);
                    setLanguageSharedPreference(LANGUAGE_CODE[position - 1]);
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
        conf.locale = mLocale;
        Locale.setDefault(mLocale);
        conf.setLayoutDirection(mLocale);
        resources.updateConfiguration(conf, displayMetrics);
    }

    private void setNewLocale(String languageTag, String countryTag) {
        updateLocale(this, languageTag, countryTag);
        startActivity(new Intent(MultilingualActivity.this, MainActivity.class));
        finishAffinity();
    }

    private void setLanguageSharedPreference(String value) {
        SharedPreferences.Editor editor = languageSharedPreferences.edit();
        editor.putString(LANGUAGE_TAG_KEY, value);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MultilingualActivity.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
